package com.zynx.morlock

import java.awt.Graphics
import java.awt.Color
import java.awt.Point
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Polygon
import java.awt.RenderingHints

class DiscreteSplitSliderUI {
    DiscreteSplitSlider slider

    def final SLIDER_BAR_Y = 10

    def final SLIDER_BAR_HEIGHT = 10
    def final SLIDER_BAR_START = 10
    def final SLIDER_BAR_END = 990

    def final SLIDER_WIDTH = 10
    def final SLIDER_HEIGHT = 20

    def DiscreteSplitSliderUI(caller) {
        slider = caller
    }

    public void drawComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

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

        final slider_y = SLIDER_BAR_Y - (int)(SLIDER_BAR_HEIGHT / 2) - 5

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

        def tick_width = 2
        def tick_height = SLIDER_BAR_HEIGHT
        def arc_amount = 3

        int tick_increments = (SLIDER_BAR_END - SLIDER_BAR_START) / (slider.num_values - 1)
        final tick_y = SLIDER_BAR_Y - (int)(SLIDER_BAR_HEIGHT / 2)

        if (slider.num_values > 0) {
            for (i in 1..(slider.num_values - 2)) {
                final tick_x = SLIDER_BAR_START + i * tick_increments - (int) (tick_width / 2)

                g.fillRoundRect(tick_x, tick_y, tick_width, tick_height, arc_amount, arc_amount)
            }
        }
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
        def component_width = 1000
        def component_height = 50

        new Dimension(component_width, component_height)
    }
}
