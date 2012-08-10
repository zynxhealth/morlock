package com.zynx.morlock

import javax.swing.JPanel
import java.awt.Graphics
import com.zynx.morlock.models.SourceFile
import java.awt.Font
import java.awt.Dimension


class FileContentsPanel extends JPanel implements Observer {
    private SourceFile model
    private String fileContents
    private Dimension mySize
    private int lineHeight = 16

    def setModel(SourceFile model) {
        this.model?.removeObserver()
        this.model = model
        model.addObserver(this)
        update(model, null)
    }

    Dimension getMinimumSize() {
        mySize
    }

    Dimension getMaximumSize() {
        mySize
    }

    Dimension getPreferredSize() {
        mySize
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g)
        g.setFont(new Font('Courier New', Font.PLAIN, 12))
        int y = 10
        fileContents.eachLine {
            g.drawString(it, 10, y)
            y += lineHeight
        }
    }

    @Override
    void update(Observable o, Object arg) {
        println "Update on object $o with arg $arg"
        fileContents = model.contentsAt('HEAD^')
        mySize = new Dimension(1000, (fileContents.count('\n') + 1) * lineHeight)
    }
}
