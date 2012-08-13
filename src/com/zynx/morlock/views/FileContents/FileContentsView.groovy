package com.zynx.morlock.views.FileContents

import javax.swing.JTable
import com.zynx.morlock.models.SourceFile
import javax.swing.table.DefaultTableModel
import java.awt.Component
import javax.swing.table.TableColumnModel
import javax.swing.table.TableColumn
import javax.swing.SwingConstants
import java.awt.Font
import java.awt.font.TextAttribute
import com.zynx.morlock.DiscreteSplitSlider.DiscreteSplitSliderListener
import com.zynx.morlock.DiscreteSplitSlider.SliderEvent


class FileContentsView extends JTable implements Observer, DiscreteSplitSliderListener {
    private SourceFile model
    private DefaultTableModel tableModel

    static private columnConfig = [
            'introduced': ['defaultWidth': 60, 'show': true],
            'deleted': ['defaultWidth': 60, 'show': true],
            'committer': ['defaultWidth': 100, 'show': true],
            'author': ['defaultWidth': 100, 'show': true]
    ]

    FileContentsView() {
        super(new DefaultTableModel())
        tableModel = getModel() as DefaultTableModel
        columnConfig.each { key, value ->
            tableModel.addColumn(key)
        }
        tableModel.addColumn('contents')
        tableModel.addColumn('isNew')
        tableHeader = null
        showGrid = false
        autoResizeMode = JTable.AUTO_RESIZE_LAST_COLUMN
        getColumn('contents').setCellRenderer(new ContentCellRenderer())
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
        model.eachHistoryRegion { contents, commitIntroduced, commitDeleted, wasNew ->
            def html = "<html>${contents.join('<br>')}</html>"
            html = html.replaceFirst(/\<br\>\<\/html\>/, '<br><br></html>')
            tableModel.addRow([commitIntroduced.abbreviatedHash,
                    commitDeleted ? commitDeleted.abbreviatedHash : '',
                    commitIntroduced.committer,
                    commitIntroduced.author,
                    html,
                    wasNew] as Object[]
            )
        }
        updateRowHeights()
        updateColumnWidths()
    }

    private updateRowHeights() {
        for (row in 0..<this.rowCount) {
            int calculatedHeight = this.getRowHeight()

            for (column in 0..<this.columnCount) {
                def cellRenderer = this.getCellRenderer(row, column)
                cellRenderer.setVerticalAlignment(SwingConstants.TOP)
                Component comp = prepareRenderer(cellRenderer, row, column)
                calculatedHeight = Math.max(calculatedHeight, comp.getPreferredSize().height)
            }

            this.setRowHeight(row, calculatedHeight)
        }
    }
    
    private setColumnWidth(TableColumn col, int width) {
        col.setMinWidth(width)
        col.setMaxWidth(width)
        col.setPreferredWidth(width)
        col.setWidth(width)
    }

    private updateColumnWidths() {
        TableColumnModel columnModel = this.getColumnModel()
        columnConfig.each { key, value ->
            int newWidth = value['show'] ? value['defaultWidth'] as int : 0
            TableColumn column = columnModel.getColumn(columnModel.getColumnIndex(key))
            setColumnWidth(column, newWidth)
        }
        setColumnWidth(columnModel.getColumn(columnModel.getColumnIndex('isNew')), 0)
    }

    def showColumn(String name, boolean show) {
        columnConfig[name]['show'] = show
        updateColumnWidths()
    }

    @Override
    void sliderValueChanged(Object e) {
        SliderEvent event = e as SliderEvent
        model.setCommitRange(event.selected_values.first(), event.selected_values.last())
    }

    @Override
    void sliderJoined(Object e) {
        SliderEvent event = e as SliderEvent
        model.setCommitRange(event.selected_values.first(), event.selected_values.last())
    }

    @Override
    void sliderSplit(Object e) {
//        SliderEvent event = e as SliderEvent
//        model.setCommitRange(event.selected_values.first(), event.selected_values.last())
    }
}
