package com.zynx.morlock.models

class FileHistoryLine {
    int lineNumber
    String contents
    Commit introduced
    Commit deleted
    boolean wasNew

    boolean isSameRegion(FileHistoryLine that) {
        that.wasNew == wasNew &&
        that.introduced.is(introduced) && (
            (! that.deleted && ! deleted) ||
            (that.deleted && that.deleted.is(deleted)) ||
            (deleted && deleted.is(that.deleted))
        )
    }
}
