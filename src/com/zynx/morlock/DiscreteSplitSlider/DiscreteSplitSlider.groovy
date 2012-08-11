package com.zynx.morlock.DiscreteSplitSlider

import java.awt.Dimension
import javax.swing.JComponent
import java.awt.Graphics
import java.awt.event.MouseListener
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.awt.Point

class DiscreteSplitSlider extends JComponent {
    def num_values
    List value_list

    int leftSliderX, rightSliderX
    def is_locked = false, isSelectedLeft, isSelectedRight

    DiscreteSplitSliderUI ui

    List<DiscreteSplitSliderListener> listeners = []

    def DiscreteSplitSlider(values) {
        ui = new DiscreteSplitSliderUI(this)

        is_locked = true
        isSelectedLeft = false
        isSelectedRight = false

        leftSliderX = ui.SLIDER_BAR_START
        rightSliderX = ui.SLIDER_BAR_START

        value_list = values

        if (value_list.size() < 0)
            num_values = 0
        else
            num_values = value_list.size()

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

    def toggleSplitSliderMode() {
        if (!is_locked) {
            isSelectedLeft = false
            isSelectedRight = false

            leftSliderX = rightSliderX
            is_locked = true

            sliderJoined()
        } else {
            is_locked = false

            sliderSplit()
        }

        repaint()
    }

    def findNearestTick(int x_val) {
        int interval = (ui.SLIDER_BAR_END - ui.SLIDER_BAR_START) / (num_values - 1)

        x_val = x_val - ui.SLIDER_BAR_START

        if (x_val % interval < interval / 2)
            x_val - (x_val % interval) + ui.SLIDER_BAR_START
        else
            x_val + (interval - x_val % interval) + ui.SLIDER_BAR_START
    }

    private void moveLeftSlider(Point location) {
        if (location.x > ui.SLIDER_BAR_START && location.x < ui.SLIDER_BAR_END) {
            def newLoc = findNearestTick((int) location.x)
            if (newLoc > rightSliderX || newLoc == leftSliderX)
                return

            leftSliderX = newLoc

            repaint()
            sliderValueChanged()
        }
    }

    private void moveRightSlider(Point location) {
        if (location.x > ui.SLIDER_BAR_START && location.x < ui.SLIDER_BAR_END) {
            def newLoc = findNearestTick((int) location.x)
            if (newLoc < leftSliderX || newLoc == rightSliderX)
                return

            rightSliderX = newLoc

            repaint()
            sliderValueChanged()
        }
    }

    private void moveJointSlider(Point location) {
        if (location.x > ui.SLIDER_BAR_START && location.x < ui.SLIDER_BAR_END) {
            def new_val = findNearestTick((int) location.x)

            if (new_val == rightSliderX && new_val == leftSliderX)
                return

            rightSliderX = leftSliderX = new_val

            repaint()
            sliderValueChanged()
        }
    }

    int getValueIndexAt(int slider_x) {
        int interval = (ui.SLIDER_BAR_END - ui.SLIDER_BAR_START) / (num_values - 1)
        (slider_x - ui.SLIDER_BAR_START) / interval
    }

    def List getSelectedValues() {
        def beginIndex = getValueIndexAt(leftSliderX)
        def endIndex = getValueIndexAt(rightSliderX)

        List selected_values = []
        for (i in beginIndex..endIndex) {
            selected_values.add(value_list[i])
        }
        selected_values
    }

    def addListener(DiscreteSplitSliderListener listener) {
        listeners.add(listener)
    }

    void sliderValueChanged() {
        for (listener in listeners) {
            listener.sliderValueChanged(new SliderEvent(this, !is_locked, getSelectedValues()))
        }
    }

    void sliderJoined() {
        for (listener in listeners) {
            listener.sliderJoined(new SliderEvent(this, !is_locked, getSelectedValues()))
        }
    }

    void sliderSplit() {
        for (listener in listeners) {
            listener.sliderSplit(new SliderEvent(this, !is_locked, getSelectedValues()))
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
