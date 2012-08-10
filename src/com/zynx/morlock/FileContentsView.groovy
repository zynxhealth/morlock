package com.zynx.morlock

import javax.swing.JTable
import com.zynx.morlock.models.SourceFile
import javax.swing.table.DefaultTableModel
import java.awt.Component


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
        tableModel.addRow("<html>${fileContents.replaceAll(/\n/, '<br>')}</html>")
        updateRowHeights()
    }

    private void updateRowHeights() {
        for (row in 0..(this.rowCount - 1)) {
            int calculatedHeight = this.getRowHeight()

            for (column in 0..(this.columnCount - 1)) {
                Component comp = this.prepareRenderer(this.getCellRenderer(row, column), row, column)
                calculatedHeight = Math.max(calculatedHeight, comp.getPreferredSize().height)
            }

            this.setRowHeight(row, calculatedHeight)
        }
    }

}
