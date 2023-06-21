package com.example.finalproject

import android.graphics.Color
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.spans.DotSpan


class CurrentDayDecorator2(context: OnDateSelectedListener, currentDay: CalendarDay) : DayViewDecorator {

    var myDay = currentDay
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == myDay
    }

    override fun decorate(view: DayViewFacade) {

        view?.addSpan((DotSpan(10F, Color.parseColor("#E91E63"))))

    }

}