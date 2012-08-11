package com.zynx.morlock.DiscreteSplitSlider

import java.awt.Graphics
import java.awt.Color
import java.awt.Point
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Polygon
import java.awt.RenderingHints

class DiscreteSplitSliderUI {
    DiscreteSplitSlider slider

    def final COMPONENT_WIDTH = 1030
    def final COMPONENT_HEIGHT = 50

    def final SLIDER_BAR_Y = 10

    def final SLIDER_BAR_HEIGHT = 10
    def final SLIDER_BAR_START = 25
    def final SLIDER_BAR_END = 1005

    def final SLIDER_WIDTH = 20
    def final SLIDER_HEIGHT = 20

    def DiscreteSplitSliderUI(caller) {
        slider = caller
    }

    public void drawComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON)

        drawSliderBar(g2d)
        drawTicks(g2d)

        drawSlider(g2d)
        drawSelectionBar(g2d)
    }

    private void drawSelectionBar(Graphics2D g2d) {
        g2d.setColor(Color.lightGray)

        final selection_bar_x = slider.leftSliderX - 1
        final selection_bar_width = slider.rightSliderX - slider.leftSliderX + 2

        g2d.fillRect(selection_bar_x, SLIDER_BAR_Y, selection_bar_width, SLIDER_BAR_HEIGHT)
    }

    private void drawSlider(Graphics2D g) {
        g.setColor(Color.black)

        final slider_y = SLIDER_BAR_Y - (int) (SLIDER_BAR_HEIGHT / 2) - 5

        int[] left_x_point_array = [slider.leftSliderX - 1, slider.leftSliderX - SLIDER_WIDTH, slider.leftSliderX - 1]

        int[] left_y_point_array = [slider_y, slider_y + (SLIDER_HEIGHT / 2) + 5, slider_y + SLIDER_HEIGHT + 10]

        int[] right_x_point_array = [slider.rightSliderX + 1, slider.rightSliderX + SLIDER_WIDTH, slider.rightSliderX + 1]
        int[] right_y_point_array = [slider_y, slider_y + (SLIDER_HEIGHT / 2) + 5, slider_y + SLIDER_HEIGHT + 10]

        def left_slider = new Polygon(left_x_point_array, left_y_point_array, left_x_point_array.length)
        def right_slider = new Polygon(right_x_point_array, right_y_point_array, right_x_point_array.length)

        g.fill(left_slider)
        g.fill(right_slider)
    }

    private void drawTicks(Graphics2D g) {
        g.setColor(Color.darkGray)

        final label_x_offset = 20

        List<String> label_list = getLabelList(label_x_offset)

        def tick_width = 2
        def tick_height = SLIDER_BAR_HEIGHT
        def arc_amount = 3

        int tick_increments = (SLIDER_BAR_END - SLIDER_BAR_START) / (slider.num_values - 1)
        final tick_y = SLIDER_BAR_Y - (int) (SLIDER_BAR_HEIGHT / 2)
        final label_y = SLIDER_BAR_Y + 30

        if (label_list.size() > 0) {\
            selectContextualTextColoring(0, g)
            g.drawString(label_list[0], SLIDER_BAR_START - label_x_offset, label_y)

            selectContextualTextColoring(label_list.size() - 1, g)
            g.drawString(label_list.last(), SLIDER_BAR_END - label_x_offset, label_y)

            for (i in 1..(slider.num_values - 2)) {
                final tick_x = SLIDER_BAR_START + i * tick_increments - (int) (tick_width / 2)

                g.setColor(Color.darkGray)
                g.fillRoundRect(tick_x, tick_y, tick_width, tick_height, arc_amount, arc_amount)

                selectContextualTextColoring(i, g)
                g.drawString(label_list[i], tick_x - label_x_offset, label_y)
            }
        }
    }

    def void selectContextualTextColoring(int index, Graphics2D g) {
        if (slider.getValueIndexAt(slider.leftSliderX) == index || slider.getValueIndexAt(slider.rightSliderX) == index)
            g.setColor(Color.black)
        else
            g.setColor(Color.lightGray)
    }

    def getLabelList(int label_x_offset) {
        def shorten_labels = false
        def no_labels = false

        if (label_x_offset * 2.8 * slider.num_values > SLIDER_BAR_END - SLIDER_BAR_START) {
            shorten_labels = true
        }

        if (label_x_offset * slider.num_values > SLIDER_BAR_END - SLIDER_BAR_START) {
            no_labels = true
        }

        def label_list = []
        for (label in slider.value_list) {
            if (no_labels) {
                label_list.add("")
            } else if (shorten_labels) {
                label_list.add(label.toString().subSequence(0, 2) + "..")
            } else {
                label_list.add(label.toString())
            }
        }
        label_list
    }

    private void drawSliderBar(Graphics2D g) {
        g.setColor(Color.darkGray)

        final slider_bar_curve = 10
        g.fillRoundRect(SLIDER_BAR_START, SLIDER_BAR_Y, SLIDER_BAR_END - SLIDER_BAR_START, SLIDER_BAR_HEIGHT, slider_bar_curve, slider_bar_curve);
    }

    private boolean isClickedLeftSlider(Point location) {
        def xdiff, ydiff
        xdiff = slider.leftSliderX - location.x
        ydiff = SLIDER_BAR_Y - location.y

        if (xdiff < 0 || xdiff > SLIDER_WIDTH)
            return false

        if (ydiff < -SLIDER_HEIGHT || ydiff > 0)
            return false

        return true
    }

    private boolean isClickedRightSlider(Point location) {
        def xdiff, ydiff
        xdiff = slider.rightSliderX - location.x
        ydiff = SLIDER_BAR_Y - location.y

        if (xdiff > 0 || xdiff < -SLIDER_WIDTH)
            return false

        if (ydiff < -SLIDER_HEIGHT || ydiff > 0)
            return false

        return true
    }

    def getRelevantLocation(Point locOnScreen) {
        def componentLocation = slider.getLocationOnScreen()

        new Point((int) locOnScreen.x - componentLocation.x, (int) locOnScreen.y - componentLocation.y)
    }

    Dimension getComponentDimension() {


        new Dimension(COMPONENT_WIDTH, COMPONENT_HEIGHT)
    }
}
