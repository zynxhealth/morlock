package com.zynx.morlock.models


class Git {

    static String executeCommand(File repoPath, String cmd) {
// This worked under Linux, but Windows?  Oh, no...
//        Process process = cmd.execute([], repoPath)
//        process.waitFor()
//        process.exitValue() == 0 ? process.text : ''
        if (System.getProperty('os.name').find(/[Ww]indows/)) {
            cmd = 'cmd /c ' + cmd
        }
        ProcessBuilder builder = new ProcessBuilder(/*cmd*/)
        builder.command(cmd.tokenize())
        builder.redirectErrorStream(true)
        builder.directory(repoPath)
        Process process = builder.start()
        process.waitFor()
        if (process.exitValue()) {
            throw new Exception("Git execution failed: ${process.inputStream.text}")
        }
        process.inputStream.text
    }

}
