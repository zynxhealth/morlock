package com.zynx.morlock

import groovy.swing.SwingBuilder
import java.awt.BorderLayout
import javax.swing.JFrame


class App {

    static App theApp;

    String fileName

    private def setupFrameWindow() {
        def swing = new SwingBuilder()
        swing.frame(title: "Morlock - $fileName", defaultCloseOperation:JFrame.EXIT_ON_CLOSE, pack:true) {
            borderLayout()
            panel(constraints: BorderLayout.NORTH) {
                vbox {
                    label(text: 'I am the button bar.')
                    vstrut(height: 50)
                    hstrut(width: 1000)
                }
            }
            panel(constraints: BorderLayout.CENTER) {
                label(text: 'I am the file contents panel')
            }
            panel(constraints: BorderLayout.SOUTH) {
                label(text: 'I am the commit details panel')
            }
        }
    }

    def start() {
        setupFrameWindow().show()
    }

    static void main(String[] args) {
        if (args.size() > 0) {
            theApp = new App(fileName: args[0])
            theApp.start()
        }
        else {
            println "Usage:  morlock <filename>"
        }
    }
}
