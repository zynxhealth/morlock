package com.zynx.morlock.models


class SourceFile extends Observable {
    private String fileName
    private File repoDir

    List commits = []
    List history = []

    def setFileName(String fileName) {
        this.fileName = fileName
        if (! repoDir) {
            repoDir = new File(findRepoDir(fileName))
        }
        refreshCommitList()
        refreshHistory()
        notifyObservers()
    }

    private refreshHistory() {
        //  FAKE!
        String contents = contentsAt('HEAD^')
        Random rand = new Random((new Date()).seconds)
        FileHistoryRegion current
        contents.eachLine {
            if (! current) {
                current = new FileHistoryRegion(contents: it, introHash: 'intro', deleteHash: rand.nextInt() % 4 == 0 ? 'delete' : null, committer: 'committer', author: 'author')
            }
            else {
                current.contents += "\n$it"
                if (rand.nextInt() % 6 == 0) {
                    history << current
                    current = null
                }
            }
        }
        if (current) {
            history << current
        }
    }

    def refreshCommitList() {
        Git.executeCommand(repoDir, "git log --pretty=format:%h#%an#%cn#%cd -- $fileName").eachLine {
            def tokens = it.tokenize('#')
            commits << new Commit(abbreviatedHash: tokens[0], author: tokens[1], committer: tokens[2])
        }
    }

    String contentsAt(String commitHash) {
        Git.executeCommand(repoDir, "git show $commitHash:${this.pathUnderRepo}")
    }

    String getPathUnderRepo() {
        return fileName.replaceFirst(repoDir.absolutePath + '/', '')
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
