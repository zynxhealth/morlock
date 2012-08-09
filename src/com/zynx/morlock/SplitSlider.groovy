package com.zynx.morlock

import javax.swing.JSlider
import java.awt.Dimension

/**
 * Created with IntelliJ IDEA.
 * User: pair
 * Date: 8/9/12
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
class SplitSlider extends JSlider {

    @Override
    Dimension getMaximumSize()
    {
        new Dimension(1000, 50)
    }

    @Override
    Dimension getMinimumSize()
    {
        new Dimension(1000, 50)
    }

    @Override
    Dimension getPreferredSize()
    {
        new Dimension(1000, 50)
    }
}
