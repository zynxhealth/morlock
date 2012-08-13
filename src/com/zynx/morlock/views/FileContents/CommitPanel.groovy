package com.zynx.morlock.views.FileContents

import com.zynx.morlock.models.SourceFile
import groovy.swing.SwingBuilder

import javax.swing.JPanel
import javax.swing.JList

class CommitPanel extends JPanel {

    CommitPanel(SourceFile model)
    {
        def title = (new SwingBuilder()).label(text: "Commits history")
        def commits = (new SwingBuilder()).list(listData:model.commitsList )
        commits.setVisibleRowCount(0);

        commits.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        this.add(title)
        this.add(commits)
    }
}