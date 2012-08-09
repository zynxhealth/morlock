package com.zynx.morlock

import javax.swing.JPanel
import java.awt.Graphics
import com.zynx.morlock.models.SourceFile
import java.awt.Font


class FileContentsPanel extends JPanel {
    SourceFile model

    protected void paintComponent(Graphics g) {
        super.paintComponent(g)
        g.setFont(new Font('Courier New', 0, 12.0))
        int y = 10
        String contents = model.contentsAt('HEAD^')
        contents.eachLine {
            g.drawString(it, 10, y)
            y += 16
        }
    }
}
