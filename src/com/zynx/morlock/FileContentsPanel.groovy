package com.zynx.morlock

import javax.swing.JPanel
import java.awt.Graphics
import com.zynx.morlock.models.SourceFile


class FileContentsPanel extends JPanel {
    SourceFile model

    protected void paintComponent(Graphics g) {
        super.paintComponent(g)
        g.drawString("Viewing ${model.fileName} here.", 10, 10)
    }
}
