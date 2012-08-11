package com.zynx.morlock.models


class FileHistoryRegion {
    int lineNumber
    String contents
    String introHash
    String deleteHash
    String committer
    String author

    boolean intersectsLineRange(int first, int last) {
        int lastLine = lineNumber + contents.count('\n')
        (first >= lineNumber && first <= lastLine) || (last >= lineNumber && last <= lastLine)
    }

    def applyDiffHunk(int oldStart, int oldCount, int newStart, int newCount, String hunk, List<FileHistoryRegion> history) {
        
    }
}
