package com.zynx.morlock

import groovy.swing.SwingBuilder
import java.awt.BorderLayout
import javax.swing.JFrame
import com.zynx.morlock.models.SourceFile


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
                panel(new FileContentsPanel(model: fileModel), constraints: BorderLayout.CENTER)
                panel(constraints: BorderLayout.SOUTH) {
                    label(text: 'I am the commit details panel')
                }
            }
        }
    }

    def start() {
        fileModel.initialize()
        showFrameWindow()
    }

    static void main(String[] args) {
        if (args.size() > 0) {
            theApp = new App(fileModel: new SourceFile(fileName: args[0]))
            theApp.start()
        }
        else {
            println "Usage:  morlock <filename>"
        }
    }
}
