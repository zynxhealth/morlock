package com.zynx.morlock.models


class SourceFile extends Observable {
    private String filePath
    private File repoDir
    private String startCommit
    private String endCommit

    List<Commit> commits = []
    List<FileHistoryRegion> history = []

    def setFileName(String fileName) {
        println fileName
        filePath = fileName.replaceAll('\\\\', '/')
        if (! repoDir) {
            repoDir = new File(findRepoDir(fileName))
        }
        refreshCommitList()
        refreshHistory()
        notifyObservers()
    }

    def setCommitRange(String start, String end) {
        if (commits.any {start.startsWith(it.abbreviatedHash)} && commits.any {end.startsWith(it.abbreviatedHash)}) {
            startCommit = start
            endCommit = end
            refreshHistory()
        }
        else {
            throw new Exception("Invalid start and/or end commits specified!")
        }
    }

    private applyDiffHunkToHistory(int oldStart, int oldCount, int newStart, int newCount, String hunk) {
        List<FileHistoryRegion> historyAffected = []
        for (FileHistoryRegion region in history) {
            if (region.intersectsLineRange(oldStart, oldStart + oldCount)) {
                historyAffected << region
            }
        }
        historyAffected.each {
            it.applyDiffHunk(oldStart, oldCount, newStart, newCount, hunk, history)
        }
    }

    private refreshHistory() {
        if (! (startCommit && endCommit)) {
            return
        }

        //  Use annotate to set up the initial region list.
        FileHistoryRegion current
        String previousCommitHash

        Git.executeCommand(repoDir, "git blame -w $startCommit -- $pathUnderRepo").eachLine {
            def matcher = (it =~ /^([^ ]+) [^\)]+ (\d+)\) (.+)$/)
            String hash = matcher[0][1]
            int lineNumber = matcher[0][2] as int
            String line = matcher[0][3]
            Commit thisCommit = commits.find {hash.startsWith(it.abbreviatedHash)} as Commit
            if (! current) {
                current = new FileHistoryRegion(lineNumber: lineNumber, contents: line, introHash: hash, committer: thisCommit.committer, author: thisCommit.author)
            }
            else if (previousCommitHash?.startsWith(thisCommit.abbreviatedHash)) {
                current.contents += "\n$line"
            }
            else {
                history << current
                current = new FileHistoryRegion(lineNumber: lineNumber, contents: line, introHash: hash, committer: thisCommit.committer, author: thisCommit.author)
            }
            previousCommitHash = hash
        }
        if (current) {
            history << current
        }

        //  Iterate through diffs, accumulating changes into the history list.
        def startIndex = commits.findIndexOf {startCommit.startsWith(it.abbreviatedHash)}
        def endIndex = commits.findIndexOf {endCommit.startsWith(it.abbreviatedHash)}

        for (i in startIndex..(endIndex - 1)) {
            String startCommitHash = commits[i].abbreviatedHash
            String endCommitHash = commits[i + 1].abbreviatedHash
            String response = Git.executeCommand(repoDir, "git diff -w $startCommitHash $endCommitHash -- $pathUnderRepo")
            response = response.replaceAll(/\n\\ No newline at end of file/, '')
            response = response.replaceAll(/\n/, '##newline##')
            response += '@@'
            def matcher = (response =~ /@@ -(\d+),(\d+) \+(\d+),(\d+) @@##newline##(.+)@@/)
            matcher.each {
                applyDiffHunkToHistory(
                        it[1] as int,
                        it[2] as int,
                        it[3] as int,
                        it[4] as int,
                        it[5].replaceAll(/##newline##/, '\n')
                )
            }
        }

        //  FAKE!
//        String contents = contentsAt('HEAD^')
//        Random rand = new Random((new Date()).seconds)
//        FileHistoryRegion current
//        contents.eachLine {
//            if (! current) {
//                current = new FileHistoryRegion(contents: it, introHash: 'intro', deleteHash: rand.nextInt() % 4 == 0 ? 'delete' : null, committer: 'committer', author: 'author')
//            }
//            else {
//                current.contents += "\n$it"
//                if (rand.nextInt() % 6 == 0) {
//                    history << current
//                    current = null
//                }
//            }
//        }
//        if (current) {
//            history << current
//        }
    }

    def refreshCommitList() {
        println Git.executeCommand(repoDir, "git log --reverse --pretty=format:%h#%an#%cn#%cd -- $pathUnderRepo")
        Git.executeCommand(repoDir, "git log --reverse --pretty=format:%h#%an#%cn#%cd -- $pathUnderRepo").eachLine {
            def tokens = it.tokenize('#')
            println tokens[0]
            commits << new Commit(abbreviatedHash: tokens[0], author: tokens[1], committer: tokens[2])
        }
        commits.each {
            println it.dump()
        }
    }

    String contentsAt(String commitHash) {
        Git.executeCommand(repoDir, "git show $commitHash:$pathUnderRepo")
    }

    String getPathUnderRepo() {
        return filePath.replaceFirst(repoDir.absolutePath.replaceAll('\\\\', '/') + '/', '')
    }

    def getCommitHashList(){
        List<String> hashList = []
        for (Commit commit in commits)
        {
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
