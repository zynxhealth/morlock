package com.zynx.morlock.models


class SourceFile {
    String fileName
    File repoDir

    List commits = []

    def initialize() {
        if (! repoDir) {
            repoDir = new File(fileName.replaceFirst(/\/[^\/]+$/, ''))
        }
        refreshCommitList()
    }

    def refreshCommitList() {
        Git.executeCommand(repoDir, "git log --pretty=format:%h#%an#%cn#%cd -- $fileName").eachLine {
            def tokens = it.tokenize('#')
            commits << new Commit(abbreviatedHash: tokens[0], author: tokens[1], committer: tokens[2])
        }
        println commits.size()
    }
}
