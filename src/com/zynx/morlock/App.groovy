package com.zynx.morlock

import com.zynx.morlock.models.SourceFile
import groovy.swing.SwingBuilder
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JScrollPane
import com.zynx.morlock.DiscreteSplitSlider.DiscreteSplitSlider
import com.zynx.morlock.views.FileContents.FileContentsView
import com.zynx.morlock.views.FileContents.CommitPanel
import javax.swing.JFileChooser


class App {

    static App theApp;

    SourceFile fileModel

    private def showFrameWindow() {
        FileContentsView fileContentsView = new FileContentsView()
        JScrollPane fileContentsPane = new JScrollPane(fileContentsView)
        JScrollPane commitsPane = (new JScrollPane(new CommitPanel(fileModel)))
        def slider = new DiscreteSplitSlider(fileModel.commitHashList)

        def swing = new SwingBuilder()
        swing.edt {
            frame(title: "Morlock - ${fileModel.filePath}", defaultCloseOperation: JFrame.EXIT_ON_CLOSE, pack: true, show: true) {
                borderLayout()
                panel(constraints: BorderLayout.NORTH) {
                    vbox {
                        toolBar(floatable: false) {
                            button(text: 'Choose File', actionPerformed: {
                                def chooser = new JFileChooser()
                                if (chooser.showOpenDialog(fileContentsPane) == JFileChooser.APPROVE_OPTION) {
                                    File file = chooser.getSelectedFile()
                                    fileModel.fileName = file.absolutePath
                                }
                            })
                            separator()
                            checkBox(text: 'Enable Multi-Revision Viewing', actionPerformed: {slider.toggleSplitSliderMode()} )
                            separator()
                            FileContentsView.columnNames().each {
                                String name = it
                                swing.checkBox(it, id: "${name}Button", selected: true, actionPerformed: {fileContentsView.showColumn(name, it.source.selected)})
                            }
                        }
                        vstrut(height: 50)
                        hstrut(width: 1000)
                        widget(slider)
                    }
                }
                widget(fileContentsPane, constraints: BorderLayout.CENTER)
                widget(commitsPane, constraints: BorderLayout.SOUTH)
            }
        }
        def sliderValues = slider.getSelectedValues()
        fileModel.setCommitRange(sliderValues.first(), sliderValues.last())
        fileContentsView.setModel(fileModel)
        slider.addListener(fileContentsView)
    }

    def start() {
        showFrameWindow()
    }

    static void main(String[] args) {
        if (args.size() > 0) {
            SourceFile model = new SourceFile()
            model.setFileName(args[0])
//            model.setCommitRange('d357586c1df060c80753fb0c07667549db6a6b85', '3e96cbc428a718cb010244692210cdd3c665fc77')
            theApp = new App(fileModel: model)
            theApp.start()
        }
        else {
            println "Usage:  morlock <filename>"
        }
    }
}
