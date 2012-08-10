package com.zynx.morlock.models


class SourceFile extends Observable {
    private String fileName
    private File repoDir

    private List commits = []

    def setFileName(String fileName) {
        this.fileName = fileName
        if (! repoDir) {
            repoDir = new File(findRepoDir(fileName))
        }
        refreshCommitList()
        notifyObservers()
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
