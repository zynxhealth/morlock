package com.zynx.morlock.views.FileContents

import javax.swing.JLabel
import javax.swing.table.TableCellRenderer
import java.awt.Component
import javax.swing.JTable
import java.awt.Font
import java.awt.font.TextAttribute
import java.awt.Color

class ContentCellRenderer extends JLabel implements TableCellRenderer {
    private static Font cachedStrikeThrough
    private static Font cachedNormal
    private static Color selectedColor = new Color(184, 207, 229)

    ContentCellRenderer() {
        super()
        setOpaque(true)
    }

    private Font getStrikeThroughFont(Component component) {
        if (! cachedStrikeThrough) {
            cachedNormal = component.getFont()
            def attributes = font.getAttributes()
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON)
            cachedStrikeThrough = new Font(attributes)
        }
        cachedStrikeThrough
    }

    private Font getNormalFont(Component component) {
        if (! cachedNormal) {
            cachedNormal = component.getFont()
        }
        cachedNormal
    }

    @Override
    Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        int deletedIndex = table.columnModel.getColumnIndex('deleted')
        int isNewIndex = table.columnModel.getColumnIndex('isNew')
        String text = value ?: ''

        setBackground(isSelected ? selectedColor : Color.WHITE)

        if (table.getValueAt(row, deletedIndex)) {
            setFont(getStrikeThroughFont(this))
            text = "<html><strike>$text</strike></html>"
            setForeground(Color.RED)
        }
        else if (table.getValueAt(row, isNewIndex)) {
            setFont(getNormalFont(this))
            setForeground(Color.BLUE)
        }
        else {
            setFont(getNormalFont(this))
            setForeground(Color.BLACK)
        }

        setText(text)

        this
    }
}
