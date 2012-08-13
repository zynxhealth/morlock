package com.zynx.morlock.models

class DiffHunk {
    int beforeLine
    int beforeCount
    int afterLine
    int afterCount
    String contents
    Integer lineCount

    Integer getLineCount() {
        lineCount ?: (lineCount = (contents.count('\n') + 1))
    }

    String lineAt(int lineNumber) {
        contents.split(/\n/)[lineNumber - beforeLine]
    }

    private int minOfPositives(int first, int second) {
        if (first < 0)
            return second
        else if (second > 0)
            return Math.min(first, second)
        else
            return first
    }

    private int nextHunkletIndex(String remainder, String mode) {
        int nextDeletion = remainder.indexOf('\n-')
        int nextInsertion = remainder.indexOf('\n+')
        int nextBlank = remainder.indexOf('\n ')
        int result = -1

        switch (mode) {
            case ' ':   result = minOfPositives(nextDeletion, nextInsertion); break
            case '-':   result = minOfPositives(nextBlank, nextInsertion); break
            case '+':   result = minOfPositives(nextBlank, nextDeletion); break
        }

        result
    }

    def eachHunklet(Closure c) {
        int hunkletLineNumber = beforeLine
        String remainder = contents
        List<String> hunklet = []
        String currentMode = ' '
        int nextHunklet = nextHunkletIndex(remainder, ' ') + 1

        while (nextHunklet >= 0) {
            hunkletLineNumber += (currentMode == '+' ? hunklet.size() : 0)
            if (nextHunklet > 0) {
                hunkletLineNumber += remainder.substring(0, nextHunklet - 1).count('\n') + 1
            }
            remainder = remainder.substring(nextHunklet)
            currentMode = remainder[0]
            int hunkletEnds = nextHunkletIndex(remainder, currentMode)
            if (hunkletEnds < 0) hunkletEnds = remainder.size()
            hunklet = remainder.substring(0, hunkletEnds).tokenize("\n$currentMode")

            c(currentMode, hunkletLineNumber, hunklet)

            if (hunkletEnds == remainder.size()) break
            remainder = remainder.substring(hunkletEnds + 1)
            if (remainder[0] == ' ') {
                nextHunklet = nextHunkletIndex(remainder, ' ')
            }
            else {
                nextHunklet = 0
            }
        }
    }
}
