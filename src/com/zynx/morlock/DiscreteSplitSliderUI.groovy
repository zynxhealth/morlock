package com.zynx.morlock

import java.awt.Graphics
import java.awt.Color
import java.awt.Point
import java.awt.Dimension

/**
 * Created with IntelliJ IDEA.
 * User: pair
 * Date: 8/10/12
 * Time: 11:06 AM
 * To change this template use File | Settings | File Templates.
 */
class DiscreteSplitSliderUI {
    DiscreteSplitSlider slider

    def DiscreteSplitSliderUI(caller) {
        slider = caller
    }

    public void drawComponent(Graphics g) {
        drawSliderBar(g)
        drawTicks(g)
        drawSlider(g)
    }

    private void drawSlider(Graphics g) {
        g.setColor(Color.gray)

        int[] left_x_point_array = [slider.leftSliderX - 1, slider.leftSliderX - 10, slider.leftSliderX - 1]
        int[] left_y_point_array = [0, 10, 20]

        int[] right_x_point_array = [slider.rightSliderX + 1, slider.rightSliderX + 10, slider.rightSliderX + 1]
        int[] right_y_point_array = [0, 10, 20]

        g.fillPolygon(left_x_point_array, left_y_point_array, 3)
        g.fillPolygon(right_x_point_array, right_y_point_array, 3)
    }

    private void drawTicks(Graphics g) {
        def tick_width = 2
        def tick_height = 12
        def arc_amount = 3

        int tick_increments = (slider.SLIDER_END - slider.SLIDER_START) / (slider.num_values - 1)

        if (slider.num_values > 0) {
            for (i in 0..(slider.num_values - 2)) {
                g.fillRoundRect(slider.SLIDER_START + i * tick_increments, 0, tick_width, tick_height, arc_amount, arc_amount)
            }

            g.fillRoundRect(slider.SLIDER_END - tick_width, 0, tick_width, tick_height, arc_amount, arc_amount)
        }
    }

    private void drawSliderBar(Graphics g) {
        g.fillRoundRect(slider.SLIDER_START, 0, slider.SLIDER_END - slider.SLIDER_START, slider.SLIDER_HEIGHT, (int) slider.SLIDER_HEIGHT / 2, (int) slider.SLIDER_HEIGHT / 2);
    }

    private boolean isClickedLeftSlider(Point location) {
        def xdiff, ydiff
        xdiff = slider.leftSliderX - location.x
        ydiff = 0 - location.y

        if (xdiff < 0 || xdiff > 10)
            return false

        if (ydiff < -20 || ydiff > 0)
            return false

        return true
    }

    private boolean isClickedRightSlider(Point location) {
        def xdiff, ydiff
        xdiff = slider.rightSliderX - location.x
        ydiff = 0 - location.y

        if (xdiff > 0 || xdiff < -10)
            return false

        if (ydiff < -20 || ydiff > 0)
            return false

        return true
    }

    def getRelevantLocation(Point locOnScreen) {
        def componentLocation = slider.getLocationOnScreen()

        new Point((int) locOnScreen.x - componentLocation.x, (int) locOnScreen.y - componentLocation.y)
    }

    Dimension getComponentDimension() {
        new Dimension(1000, 50)
    }
}
