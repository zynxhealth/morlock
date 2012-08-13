package com.zynx.morlock.models


class StreamGobbler extends Thread
{
    InputStream is
    String type
    String output = ''

    void run()
    {
        try {
            InputStreamReader isr = new InputStreamReader(is)
            BufferedReader br = new BufferedReader(isr)
            String line
            while ((line = br.readLine()) != null)
                output += (output.isEmpty() ? '' : '\n') + line
        }
        catch (IOException ioe) {
            ioe.printStackTrace()
        }
    }
}

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
        StreamGobbler windowsIoBufferLimitsMakeMeNecessary = new StreamGobbler(is: process.inputStream, type: "OUTPUT")
        windowsIoBufferLimitsMakeMeNecessary.start()
        process.waitFor()
        if (process.exitValue()) {
            throw new Exception("Git execution failed: ${process.inputStream.text}")
        }
        windowsIoBufferLimitsMakeMeNecessary.output
//        println process.inputStream.text
//        process.inputStream.text
    }

}
