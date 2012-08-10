package com.zynx.morlock.models


class Commit {
    String abbreviatedHash
    String author
    String committer
    Date date

    String dump() {
        "$abbreviatedHash $author $committer $date"
    }
}
