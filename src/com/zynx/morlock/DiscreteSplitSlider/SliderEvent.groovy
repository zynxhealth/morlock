package com.zynx.morlock.DiscreteSplitSlider

import java.awt.Component

class SliderEvent extends EventObject{
    boolean is_split
    List<String> selected_values

    def SliderEvent(Component source, boolean is_split, List<String> selected_values) {
        super(source)

        this.is_split = is_split
        this.selected_values = selected_values
    }
}
