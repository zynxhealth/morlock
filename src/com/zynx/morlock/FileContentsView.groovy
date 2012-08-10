package com.zynx.morlock

import javax.swing.JTable
import com.zynx.morlock.models.SourceFile
import javax.swing.table.DefaultTableModel


class FileContentsView extends JTable implements Observer {
    private SourceFile model
    private DefaultTableModel tableModel
    private String fileContents

    FileContentsView() {
        super(new DefaultTableModel())
        tableModel = getModel() as DefaultTableModel
        tableModel.addColumn('contents')
        tableHeader = null
        showGrid = false
    }

    def setModel(SourceFile model) {
        this.model?.removeObserver()
        this.model = model
        model.addObserver(this)
        update(model, null)
    }

    @Override
    void update(Observable o, Object arg) {
        println "Update on object $o with arg $arg"
        fileContents = model.contentsAt('HEAD^')
        tableModel.setRowCount(0)
        fileContents.eachLine {
            tableModel.addRow([it] as Object[])
        }
    }
}
