package com.zynx.morlock

import groovy.swing.SwingBuilder
import java.awt.BorderLayout
import javax.swing.JFrame


class App {
    static void main(String[] args) {
        if (args.size() > 0) {
            String fileName = args[0]
            def swing = new SwingBuilder()
            def frame = swing.frame(title: "Morlock - $fileName", defaultCloseOperation:JFrame.EXIT_ON_CLOSE, pack:true) {
                borderLayout()
                label(text: 'slider goes here', constraints: BorderLayout.NORTH)
                panel(constraints: BorderLayout.CENTER) {
                    label(text: 'file contents go here')
                }
            }

            frame.show()
        }
        else {
            println "Usage:  morlock <filename>"
        }
    }
}
