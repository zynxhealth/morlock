package com.zynx.morlock

import groovy.swing.SwingBuilder
import java.awt.BorderLayout
import javax.swing.JFrame
import com.zynx.morlock.models.SourceFile
import javax.swing.JScrollPane


class App {

    static App theApp;

    SourceFile fileModel

    private def showFrameWindow() {
        def swing = new SwingBuilder()
        swing.edt {
            frame(title: "Morlock - ${fileModel.fileName}", defaultCloseOperation: JFrame.EXIT_ON_CLOSE, pack: true, show: true) {
                borderLayout()
                panel(constraints: BorderLayout.NORTH) {
                    vbox {
                        label(text: 'I am the button bar.')
                        vstrut(height: 50)
                        hstrut(width: 1000)
                        widget(new DiscreteSplitSlider(5))
                    }
                }
                scrollPane(verticalScrollBarPolicy: JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, constraints: BorderLayout.CENTER) {
                    panel(new FileContentsPanel(model: fileModel))
                }
                panel(constraints: BorderLayout.SOUTH) {
                    label(text: 'I am the commit details panel')
                }
            }
        }
    }

    def start() {
        showFrameWindow()
    }

    static void main(String[] args) {
        if (args.size() > 0) {
            SourceFile model = new SourceFile()
            model.setFileName(args[0])
            theApp = new App(fileModel: model)
            theApp.start()
        }
        else {
            println "Usage:  morlock <filename>"
        }
    }
}
