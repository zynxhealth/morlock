package com.zynx.morlock.models


class SourceFile extends Observable {
    private String filePath
    private File repoDir
    private String startCommitHash
    private String endCommitHash

    List<Commit> commits = []
    List<FileHistoryLine> history = []

    def setFileName(String fileName) {
        filePath = fileName.replaceAll('\\\\', '/')
        repoDir = new File(findRepoDir(fileName))
        refreshCommitList()
        refreshHistory()
    }

    def setCommitRange(String start, String end) {
        if (commits.any {start.startsWith(it.abbreviatedHash)} && commits.any {end.startsWith(it.abbreviatedHash)}) {
            startCommitHash = start
            endCommitHash = end
            refreshHistory()
        }
        else {
            throw new Exception("Invalid start and/or end commits specified!")
        }
    }

    private startHistoryWithBlame() {
        String lastHash
        Commit commit

        Git.executeCommand(repoDir, "git blame -w $startCommitHash -- $pathUnderRepo").eachLine {
            def matcher = (it =~ /^([^ ]+) [^\)]+ (\d+)\) (.*)$/)
            String hash = matcher[0][1]
            int lineNumber = matcher[0][2] as int
            String line = matcher[0][3]
            if (! commit || lastHash != hash) {
                commit = commits.find {hash.startsWith(it.abbreviatedHash)} as Commit
            }
            history << new FileHistoryLine(lineNumber: lineNumber, contents: line, introduced: commit, wasNew: false)
            lastHash = hash
        }
    }

    private List<DiffHunk> diffCommits(String firstHash, String secondHash) {
        String cmdOut = Git.executeCommand(repoDir, "git diff -w $firstHash $secondHash -- $pathUnderRepo")
        cmdOut = cmdOut.replaceAll(/\n\\ No newline at end of file/, '')
        cmdOut = cmdOut.replaceAll(/\n/, '##newline##')
        cmdOut += '@@'

        List result = []
        (cmdOut =~ /@@ -(\d+),(\d+) \+(\d+),(\d+) @@(##newline##)?(.+)(##newline##)?@@/).each {
                result << new DiffHunk(
                    beforeLine: (it[1] as int) - (it[5] ? 0 : 1),
                    beforeCount: it[2] as int,
                    afterLine: it[3] as int,
                    afterCount: it[4] as int,
                    contents: it[6].replaceAll(/##newline##/, '\n')
            )
        }

        result
    }

    int historyIndexForLine(int lineNumber, int startFromIndex) {
        int found = history.findIndexOf(Math.max(lineNumber - 1, startFromIndex)) {
            it.lineNumber == lineNumber && ! it.deleted
        }
        found < 0 ? history.size() : found
    }

    def applyDiffsToHistory() {
        def startIndex = commits.findIndexOf {startCommitHash.startsWith(it.abbreviatedHash)}
        def endIndex = commits.findIndexOf {endCommitHash.startsWith(it.abbreviatedHash)}

        for (nextCommitIndex in (startIndex + 1)..endIndex) {
            String previousCommitHash = commits[nextCommitIndex - 1].abbreviatedHash
            String nextCommitHash = commits[nextCommitIndex].abbreviatedHash
            Commit nextCommit = commits[nextCommitIndex]

            diffCommits(previousCommitHash, nextCommitHash).each { DiffHunk diffHunk ->
                diffHunk.eachHunklet { String mode, int startLine, List<String> hunklet ->
                    int hunkletSize = hunklet.size()
                    int lastHistoryIndex = 0
                    switch (mode) {
                        case '-':
                            for (i in startLine..<(startLine + hunkletSize)) {
                                def lineIndex = historyIndexForLine(i, lastHistoryIndex)
                                history[lineIndex].deleted = nextCommit
                                lastHistoryIndex = lineIndex + 1
                            }

                            if (lastHistoryIndex < history.size()) {
                                history[ lastHistoryIndex..<history.size() ].each {
                                    it.lineNumber -= hunkletSize
                                }
                            }
                            break

                        case '+':
                            int nextLineIndex = historyIndexForLine(startLine, 0)
                            if (nextLineIndex < history.size() - 1) {
                                history[ (nextLineIndex + 1)..<history.size() ].each {
                                    it.lineNumber += hunkletSize
                                }
                                lastHistoryIndex = nextLineIndex
                            }

                            int linesCollected = 0
                            def newHistoryLines = hunklet.collect { new FileHistoryLine(
                                    lineNumber: startLine + linesCollected++,
                                    contents: it,
                                    introduced: nextCommit,
                                    wasNew: true
                            ) }
                            history.addAll(historyIndexForLine(startLine, lastHistoryIndex), newHistoryLines)
                            break
                    }
                }
            }
        }
    }

    private refreshHistory() {
        if (! (startCommitHash && endCommitHash)) {
            return
        }

        history.clear()
        startHistoryWithBlame()
        int significant = Math.max(startCommitHash.length(), endCommitHash.length()) - 1
        if (startCommitHash.substring(0, significant) != endCommitHash.substring(0, significant)) {
            applyDiffsToHistory()
        }

        setChanged()
        notifyObservers()
        clearChanged()
    }

    def refreshCommitList() {
        Git.executeCommand(repoDir, "git log --reverse --pretty=format:%h#%an#%cn#%cd -- $pathUnderRepo").eachLine {
            def tokens = it.tokenize('#')
            commits << new Commit(abbreviatedHash: tokens[0], author: tokens[1], committer: tokens[2])
        }
    }

    def eachHistoryRegion(Closure c) {
        int historyIndex = 0

        while (historyIndex < history.size()) {
            FileHistoryLine firstRegionLine = history[historyIndex]
            int nextIndex = history.findIndexOf(historyIndex + 1) {! it.isSameRegion(firstRegionLine)}
            if (nextIndex < 0) nextIndex = history.size()
            c(
                    history[historyIndex..<nextIndex].collect {it.contents},
                    firstRegionLine.introduced,
                    firstRegionLine.deleted,
                    firstRegionLine.wasNew
            )
            historyIndex = nextIndex
        }
    }

    String getPathUnderRepo() {
        return filePath.replaceFirst(repoDir.absolutePath.replaceAll('\\\\', '/') + '/', '')
    }

    def getCommitHashList(){
        List<String> hashList = []
        for (Commit commit in commits) {
            hashList.add(commit.abbreviatedHash)
        }
        hashList
    }

    private static String findRepoDir(String fileName) {
        while (! (new File(fileName + '/.git')).exists()) {
            fileName = fileName.replaceFirst(/\/[^\/]+$/, '')
            if (fileName.isEmpty()) {
                return ''
            }
        }
        fileName
    }
}
