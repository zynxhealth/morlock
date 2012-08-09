package com.zynx.morlock

import javax.swing.JPanel
import java.awt.Graphics


class FileContentsPanel extends JPanel {
    FileModel model

    protected void paintComponent(Graphics g) {
        super.paintComponent(g)
        g.drawString("Viewing ${model.fileName} here.", 10, 10)
    }
}
