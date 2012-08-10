package com.zynx.morlock

import java.awt.Dimension
import javax.swing.JComponent
import java.awt.Graphics
import java.awt.event.MouseListener
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.awt.Point

class DiscreteSplitSlider extends JComponent {
    def num_values

    int leftSliderX, rightSliderX
    def is_locked, isSelectedLeft, isSelectedRight

    DiscreteSplitSliderUI ui

    def getSliderValue() {
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

    def findNearestTick(int x_val) {
        int interval = (ui.SLIDER_BAR_END - ui.SLIDER_BAR_START) / (num_values - 1)

        x_val = x_val - ui.SLIDER_BAR_START

        if (x_val % interval < interval / 2)
            x_val - (x_val % interval) + ui.SLIDER_BAR_START
        else
            x_val + (interval - x_val % interval) + ui.SLIDER_BAR_START
    }

    def DiscreteSplitSlider(values) {
        ui = new DiscreteSplitSliderUI(this)

        is_locked = false
        isSelectedLeft = false
        isSelectedRight = false

        leftSliderX = ui.SLIDER_BAR_START
        rightSliderX = ui.SLIDER_BAR_START

        if (values < 0)
            num_values = 0
        else
            num_values = values

        addMouseListener(clickListener)
        addMouseMotionListener(motionListener);
    }

    MouseListener clickListener = new MouseListener() {
        @Override
        void mousePressed(MouseEvent e) {
            def location = ui.getRelevantLocation(e.getLocationOnScreen())

            if (is_locked) {
                moveJointSlider(location)
            } else {
                if (ui.isClickedLeftSlider(location)) {
                    isSelectedLeft = true
                } else if (ui.isClickedRightSlider(location)) {
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
    }

    MouseMotionListener motionListener = new MouseMotionListener() {

        @Override
        void mouseDragged(MouseEvent e) {
            def location = ui.getRelevantLocation(e.getLocationOnScreen())

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
    }

    private void moveLeftSlider(Point location) {
        if (location.x > ui.SLIDER_BAR_START && location.x < ui.SLIDER_BAR_END) {
            def newLoc = findNearestTick((int) location.x)
            if (newLoc <= rightSliderX)
                leftSliderX = newLoc

            repaint()
        }
    }

    private void moveRightSlider(Point location) {
        if (location.x > ui.SLIDER_BAR_START && location.x < ui.SLIDER_BAR_END) {
            def newLoc = findNearestTick((int) location.x)
            if (newLoc >= leftSliderX)
                rightSliderX = newLoc

            repaint()
        }
    }

    private void moveJointSlider(Point location) {
        if (location.x > ui.SLIDER_BAR_START && location.x < ui.SLIDER_BAR_END) {
            rightSliderX = leftSliderX = findNearestTick((int) location.x)
            repaint()
        }
    }

    @Override
    void paint(Graphics g) {
        ui.drawComponent(g)
    }

    @Override
    Dimension getMaximumSize() {
        ui.getComponentDimension()
    }

    @Override
    Dimension getMinimumSize() {
        ui.getComponentDimension()
    }

    @Override
    Dimension getPreferredSize() {
        ui.getComponentDimension()
    }
}
