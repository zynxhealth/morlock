package com.zynx.morlock

import javax.swing.JTable
import com.zynx.morlock.models.SourceFile
import javax.swing.table.DefaultTableModel
import java.awt.Component
import javax.swing.table.TableColumnModel
import javax.swing.table.TableColumn


class FileContentsView extends JTable implements Observer {
    private SourceFile model
    private DefaultTableModel tableModel
    private String fileContents

    static private columnConfig = [
            'hash': ['defaultWidth': 50, 'show': true],
            'committer': ['defaultWidth': 75, 'show': true],
            'author': ['defaultWidth': 75, 'show': true]
    ]

    FileContentsView() {
        super(new DefaultTableModel())
        tableModel = getModel() as DefaultTableModel
        tableModel.addColumn('hash')
        tableModel.addColumn('committer')
        tableModel.addColumn('author')
        tableModel.addColumn('contents')
        tableHeader = null
        showGrid = false
        autoResizeMode = JTable.AUTO_RESIZE_LAST_COLUMN
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
//        fileContents.eachLine {
//            tableModel.addRow(['hash', 'committer', 'author', it] as Object[])
//        }
        tableModel.addRow(['hash', 'committer', 'author', "<html>${fileContents.replaceAll(/\n/, '<br>')}</html>"] as Object[])
        updateRowHeights()
        updateColumnWidths()
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

    private def updateColumnWidths() {
        TableColumnModel columnModel = this.getColumnModel()
        columnConfig.each { key, value ->
            int newWidth = value['show'] ? value['defaultWidth'] as int : 0
            TableColumn column = columnModel.getColumn(columnModel.getColumnIndex(key))
            column.setMinWidth(newWidth)
            column.setMaxWidth(newWidth)
            column.setPreferredWidth(newWidth)
            column.setWidth(newWidth)
        }
    }

    def showHashColumn(boolean show) {
        columnConfig['hash']['show'] = show
        updateColumnWidths()
    }

    def showCommitterColumn(boolean show) {
        columnConfig['committer']['show'] = show
        updateColumnWidths()
    }

    def showAuthorColumn(boolean show) {
        columnConfig['author']['show'] = show
        updateColumnWidths()
    }

}
