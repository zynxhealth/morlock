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
    def final SLIDER_START = 10
    def final SLIDER_END = 990

    int leftSliderX, rightSliderX
    def is_locked, isSelectedLeft, isSelectedRight

    def getSliderValue(){
        if (is_locked)
            ['value': leftSliderX]
        else
            ['leftBound': leftSliderX, 'rightBound': rightSliderX]
    }

    def toggleSplitSliderMode() {
        if (!is_locked) {
            isSelectedLeft = false
            isSelectedRight = false

            leftSliderX = rightSliderX
        }

        is_locked = !is_locked
    }

    def getRelevantLocation(Point locOnScreen) {
        def componentLocation = this.getLocationOnScreen()

        new Point((int) locOnScreen.x - componentLocation.x, (int) locOnScreen.y - componentLocation.y)
    }

    def findNearestTick(int x_val) {
        int interval = (SLIDER_END - SLIDER_START) / (num_values - 1)

        x_val = x_val - SLIDER_START

        if (x_val % interval < interval / 2)
            x_val - (x_val % interval) + SLIDER_START
        else
            x_val + (interval - x_val % interval) + SLIDER_START
    }

    def DiscreteSplitSlider(values) {
        is_locked = false
        isSelectedLeft = false
        isSelectedRight = false

        leftSliderX = SLIDER_START
        rightSliderX = SLIDER_START

        if (values < 0)
            num_values = 0
        else
            num_values = values

        addMouseListener(new MouseListener() {
            @Override
            void mousePressed(MouseEvent e) {
                def location = getRelevantLocation(e.getLocationOnScreen())

                if (is_locked) {
                    moveJointSlider(location)
                } else {
                    if (isClickedLeftSlider(location)) {
                        isSelectedLeft = true
                    } else if (isClickedRightSlider(location)) {
                        isSelectedRight = true
                    }
                }
            }

            @Override
            void mouseClicked(MouseEvent e) {}

            @Override
            void mouseReleased(MouseEvent e) {
                isSelectedLeft = false
                isSelectedRight = false
            }

            @Override
            void mouseEntered(MouseEvent e) {}

            @Override
            void mouseExited(MouseEvent e) {}


        })
        addMouseMotionListener(new MouseMotionListener() {

            @Override
            void mouseDragged(MouseEvent e) {
                def location = getRelevantLocation(e.getLocationOnScreen())

                if (is_locked) {
                    moveJointSlider(location)
                } else {
                    if (isSelectedLeft) {
                        moveLeftSlider(location)
                    } else if (isSelectedRight) {
                        moveRightSlider(location)
                    }
                }
            }

            @Override
            void mouseMoved(MouseEvent e) {}
        });
    }

    private boolean isClickedLeftSlider(Point location) {
        def xdiff, ydiff
        xdiff = leftSliderX - location.x
        ydiff = 0 - location.y

        if (xdiff < 0 || xdiff > 10)
            return false

        if (ydiff < -20 || ydiff > 0)
            return false

        return true
    }

    private boolean isClickedRightSlider(Point location) {
        def xdiff, ydiff
        xdiff = rightSliderX - location.x
        ydiff = 0 - location.y

        if (xdiff > 0 || xdiff < -10)
            return false

        if (ydiff < -20 || ydiff > 0)
            return false

        return true
    }

    private void moveLeftSlider(Point location) {
        if (location.x > SLIDER_START && location.x < SLIDER_END) {
            def newLoc = findNearestTick((int) location.x)
            if (newLoc <= rightSliderX)
                leftSliderX = newLoc

            repaint()
        }
    }

    private void moveRightSlider(Point location) {
        if (location.x > SLIDER_START && location.x < SLIDER_END) {
            def newLoc = findNearestTick((int) location.x)
            if (newLoc >= leftSliderX)
                rightSliderX = newLoc

            repaint()
        }
    }

    private void moveJointSlider(Point location) {
        if (location.x > SLIDER_START && location.x < SLIDER_END) {
            rightSliderX = leftSliderX = findNearestTick((int) location.x)
            repaint()
        }
    }

    @Override
    void paint(Graphics g) {
        drawSliderBar(g)
        drawTicks(g)
        drawSlider(g)
    }

    private void drawSlider(Graphics g) {
        g.setColor(Color.gray)

        int[] left_x_point_array = [leftSliderX - 1, leftSliderX - 10, leftSliderX - 1]
        int[] left_y_point_array = [0, 10, 20]

        int[] right_x_point_array = [rightSliderX + 1, rightSliderX + 10, rightSliderX + 1]
        int[] right_y_point_array = [0, 10, 20]

        g.fillPolygon(left_x_point_array, left_y_point_array, 3)
        g.fillPolygon(right_x_point_array, right_y_point_array, 3)
    }

    private void drawTicks(Graphics g) {
        def tick_width = 2
        def tick_height = 12
        def arc_amount = 3

        int tick_increments = (SLIDER_END - SLIDER_START) / (num_values - 1)

        if (num_values > 0) {
            for (i in 0..(num_values - 2)) {
                g.fillRoundRect(SLIDER_START + i * tick_increments, 0, tick_width, tick_height, arc_amount, arc_amount)
            }

            g.fillRoundRect(SLIDER_END - tick_width, 0, tick_width, tick_height, arc_amount, arc_amount)
        }
    }

    private void drawSliderBar(Graphics g) {
        g.fillRoundRect(SLIDER_START, 0, SLIDER_END - SLIDER_START, SLIDER_HEIGHT, (int) SLIDER_HEIGHT / 2, (int) SLIDER_HEIGHT / 2);
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
