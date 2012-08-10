package com.zynx.morlock.models


class Git {

    static String executeCommand(File repoPath, String cmd) {
        println "Repo path: $repoPath -- Command: $cmd"
        Process process = cmd.execute([], repoPath)
        process.waitFor()
        process.exitValue() == 0 ? process.text : ''
    }

}
