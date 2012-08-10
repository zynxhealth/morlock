package com.zynx.morlock

import java.awt.Dimension
import javax.swing.JComponent
import java.awt.Graphics
import java.awt.event.MouseListener
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.awt.Color
import java.awt.Point

class DiscreteSplitSlider extends JComponent {
    def num_values
    def final SLIDER_HEIGHT = 6
    int sliderX
    def is_split

    def toggleSplitSliderMode() {
        is_split = !is_split
    }

    def getRelevantLocation(Point locOnScreen) {
        def componentLocation = this.getLocationOnScreen()

        new Point((int) locOnScreen.x - componentLocation.x, (int) locOnScreen.y - componentLocation.y)
    }

    def findNearestTick(int x_val) {
        int interval = this.getWidth() / (num_values - 1)

        if (x_val % interval < interval / 2)
            x_val - (x_val % interval)
        else
            x_val + (interval - x_val % interval)
    }

    def DiscreteSplitSlider(values) {
        is_split = false;

        if (values < 0)
            num_values = 0
        else
            num_values = values

        addMouseListener(new MouseListener() {
            @Override
            void mousePressed(MouseEvent e) {
                def location = getRelevantLocation(e.getLocationOnScreen())

                if (location.x > 0 && location.x < getWidth()) {
                    sliderX = findNearestTick((int) location.x)
                    repaint()
                }
            }

            @Override
            void mouseClicked(MouseEvent e) {}

            @Override
            void mouseReleased(MouseEvent e) {}

            @Override
            void mouseEntered(MouseEvent e) {}

            @Override
            void mouseExited(MouseEvent e) {}
        })
        addMouseMotionListener(new MouseMotionListener() {

            @Override
            void mouseDragged(MouseEvent e) {
                def location = getRelevantLocation(e.getLocationOnScreen())

                if (location.x > 0 && location.x < getWidth()) {
                    sliderX = findNearestTick((int) location.x)
                    repaint()
                }
            }

            @Override
            void mouseMoved(MouseEvent e) {}
        });
    }

    @Override
    void paint(Graphics g) {
        drawSliderBar(g)
        drawTicks(g)
        drawSingleSlider(g)
    }

    private void drawSingleSlider(Graphics g) {
        g.setColor(Color.gray)

        int[] x_point_array = [sliderX, sliderX + 10, sliderX - 10]
        int[] y_point_array = [0, 20, 20]

        g.fillPolygon(x_point_array, y_point_array, 3)
    }

    private void drawSplitSlider(Graphics g) {
        //TODO
    }

    private void drawTicks(Graphics g) {
        def tick_width = 2
        def tick_height = 12
        def arc_amount = 3

        int tick_increments = this.getWidth() / (num_values - 1)

        if (num_values > 0) {
            for (i in 0..(num_values - 2)) {
                g.fillRoundRect(i * tick_increments, 0, tick_width, tick_height, arc_amount, arc_amount)
            }

            g.fillRoundRect(this.getWidth() - tick_width, 0, tick_width, tick_height, arc_amount, arc_amount)
        }
    }

    private void drawSliderBar(Graphics g) {
        g.fillRoundRect(0, 0, this.getWidth(), SLIDER_HEIGHT, (int) SLIDER_HEIGHT / 2, (int) SLIDER_HEIGHT / 2);
    }

    @Override
    Dimension getMaximumSize() {
        new Dimension(1000, 50)
    }

    @Override
    Dimension getMinimumSize() {
        new Dimension(1000, 50)
    }

    @Override
    Dimension getPreferredSize() {
        new Dimension(1000, 50)
    }
}
