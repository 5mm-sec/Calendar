package com.example.finalproject

import android.app.Activity
import android.graphics.drawable.Drawable
import android.widget.TextView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class EventDecorator(
    dates: Collection<CalendarDay>,
    private val context: Activity,
    private val textView: TextView
) : DayViewDecorator {

    private val drawable: Drawable = context.resources.getDrawable(R.drawable.calendar_background)
    private val datesHashSet: HashSet<CalendarDay> = HashSet(dates)

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return datesHashSet.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(drawable)
    }

    fun setText(text: String) {
        textView.text = text
    }
}