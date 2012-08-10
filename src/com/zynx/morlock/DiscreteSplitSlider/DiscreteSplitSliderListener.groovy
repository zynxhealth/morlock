package com.zynx.morlock.DiscreteSplitSlider

public interface DiscreteSplitSliderListener extends EventListener{
    void sliderValueChanged(SliderEvent)

    void sliderJoined(SliderEvent)
    void sliderSplit(SliderEvent)
}