package com.zynx.morlock.models


class SourceFile {
    String fileName
    File repoDir

    List commits = []

    def initialize() {
        if (! repoDir) {
            repoDir = new File(findRepoDir(fileName))
        }
        refreshCommitList()
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
