package com.example.finalproject

import android.R
import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import org.threeten.bp.DayOfWeek
import java.io.File

class SundayDecorator:DayViewDecorator {
    private val calendar = Calendar.getInstance()
    override fun shouldDecorate(day: CalendarDay?): Boolean {

        val sunday = day?.date?.with(DayOfWeek.MONDAY)?.dayOfMonth

        return sunday == day?.day
    }
    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(object:ForegroundColorSpan(Color.RED){})
    }
}