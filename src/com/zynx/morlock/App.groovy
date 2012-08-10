package com.zynx.morlock

import com.zynx.morlock.models.SourceFile
import groovy.swing.SwingBuilder
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JScrollPane


class App {

    static App theApp;

    SourceFile fileModel

    private def showFrameWindow() {
        FileContentsView fileContentsView = new FileContentsView()
        JScrollPane fileContentsPane = new JScrollPane(fileContentsView)

        def swing = new SwingBuilder()
        swing.edt {
            frame(title: "Morlock - ${fileModel.fileName}", defaultCloseOperation: JFrame.EXIT_ON_CLOSE, pack: true, show: true) {
                borderLayout()
                panel(constraints: BorderLayout.NORTH) {
                    vbox {
                        toolBar(floatable: false) {
                            checkBox('hash', id: 'hashButton', selected: true, actionPerformed: {fileContentsView.showHashColumn(it.source.selected)})
                            checkBox('committer', id: 'committerButton', selected: true, actionPerformed: {fileContentsView.showCommitterColumn(it.source.selected)})
                            checkBox('author', id: 'authorButton', selected: true, actionPerformed: {fileContentsView.showAuthorColumn(it.source.selected)})
                        }
                        vstrut(height: 50)
                        hstrut(width: 1000)
                        widget(new DiscreteSplitSlider(5))
                    }
                }
                widget(fileContentsPane, constraints: BorderLayout.CENTER)
                panel(constraints: BorderLayout.SOUTH) {
                    label(text: 'I am the commit details panel')
                }
            }
        }
        fileContentsView.setModel(fileModel)
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
