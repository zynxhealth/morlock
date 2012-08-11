package com.zynx.morlock

import javax.swing.JTable
import com.zynx.morlock.models.SourceFile
import javax.swing.table.DefaultTableModel
import java.awt.Component
import javax.swing.table.TableColumnModel
import javax.swing.table.TableColumn
import javax.swing.SwingConstants
import com.zynx.morlock.models.FileHistoryRegion


class FileContentsView extends JTable implements Observer {
    private SourceFile model
    private DefaultTableModel tableModel
    private String fileContents

    static private columnConfig = [
            'introduced': ['defaultWidth': 50, 'show': true],
            'deleted': ['defaultWidth': 50, 'show': true],
            'committer': ['defaultWidth': 75, 'show': true],
            'author': ['defaultWidth': 75, 'show': true]
    ]

    FileContentsView() {
        super(new DefaultTableModel())
        tableModel = getModel() as DefaultTableModel
        columnConfig.each { key, value ->
            tableModel.addColumn(key)
        }
        tableModel.addColumn('contents')
        tableHeader = null
        showGrid = false
        autoResizeMode = JTable.AUTO_RESIZE_LAST_COLUMN
    }

    static columnNames() {
        columnConfig.keySet()
    }

    def setModel(SourceFile model) {
        this.model?.removeObserver()
        this.model = model
        model.addObserver(this)
        update(model, null)
    }

    @Override
    void update(Observable o, Object arg) {
        tableModel.setRowCount(0)
        model.history.each { FileHistoryRegion region ->
            def contents = "<html>${region.contents.replaceAll(/\n/, '<br>')}</html>"
            contents = contents.replaceFirst(/\<br\>\<\/html\>/, '<br><br></html>')
            tableModel.addRow([region.introHash, region.deleteHash, region.committer, region.author, contents] as Object[])
        }
        updateRowHeights()
        updateColumnWidths()
    }

    private void updateRowHeights() {
        for (row in 0..(this.rowCount - 1)) {
            int calculatedHeight = this.getRowHeight()

            for (column in 0..(this.columnCount - 1)) {
                def cellRenderer = this.getCellRenderer(row, column)
                cellRenderer.setVerticalAlignment(SwingConstants.TOP)
                Component comp = this.prepareRenderer(cellRenderer, row, column)
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

    def showColumn(String name, boolean show) {
        columnConfig[name]['show'] = show
        updateColumnWidths()
    }

}
