package com.example.finalproject

import android.app.PendingIntent.getActivity
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.os.Bundle
import android.text.style.StyleSpan
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.databinding.ActivityMoneyBinding
import com.example.finalproject.databinding.ActivityShowscheduleBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jakewharton.threetenabp.AndroidThreeTen.init
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import kotlinx.android.synthetic.main.activity_money.*
import kotlinx.android.synthetic.main.activity_schedule.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate

private const val TAG_SCHEDULE = "fragment_schedule"
private const val TAG_SHOW_SCHEDULE = "fragment_show_schedule"
private const val TAG_BACK = "fragment_back"


class MoneyActivity :AppCompatActivity(){

    private lateinit var binding: ActivityMoneyBinding
    var firestore : FirebaseFirestore? = null
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("nickname")){
            val nickname = intent.getStringExtra("nickname")
            Log.d("nickname", nickname.toString())
            Toast.makeText(this,"$nickname 님 환영합니다.", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"닉네임 가져오기 에러", Toast.LENGTH_SHORT).show()
        }
        val nickname = intent.getStringExtra("nickname").toString()
        Log.d("닉네임테스트",nickname)

        //setFragment(TAG_MONEY,MoneyFragment(),nickname)

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.calendarFragment -> setFragment(TAG_SCHEDULE, ScheduleFragment(),nickname)
                R.id.showCalendarFragment -> setFragment(TAG_SHOW_SCHEDULE, ShowScheduleFragment(),nickname)
                //R.id.moneyFragment ->setFragment(TAG_MONEY, MoneyFragment(),nickname)
                R.id.backFragment-> setFragment(TAG_BACK, BackFragment(),nickname)
            }
            true
        }


        val calendarView = binding.MoneyCalendarView
        calendarView.state()
            .edit()
            .setFirstDayOfWeek(DayOfWeek.of(Calendar.MONDAY))
            .commit();

        calendarView.setTitleFormatter(MonthArrayTitleFormatter(resources.getTextArray(R.array.custom_months)))
        calendarView.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)))

        calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader)


        val incomeEditText = binding.incomeEditText
        val costEditText = binding.costEditText
        val dateTextView2 = binding.dateTextView2


        calendarView.setOnDateChangedListener(object : OnDateSelectedListener { //날짜 선택 시 이벤트 처리
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {

                val selectDate = date.date.toString()
                Log.d("날짜 선택 : $selectDate","날짜 선택 : $selectDate")

                dateTextView2.setText(selectDate)


            }
        })

        binding.moneySaveButton.setOnClickListener{

            val incomeEdietText2 : String = incomeEditText.text.toString()
            val costEditText2 : String = costEditText.text.toString()
            val dateTextView2 : String = dateTextView2.text.toString()


            Log.d("nickname",nickname)
            Log.d("incomeEdietText2",incomeEdietText2)
            Log.d("costEditText2",costEditText2)
            Log.d("dateTextView2",dateTextView2)



            val saveData = hashMapOf(
                "nickname" to nickname,
                "date" to dateTextView2,
                "income" to incomeEdietText2,
                "cost" to costEditText2,
            )
            db.collection("money")
                .add(saveData)
                .addOnSuccessListener {
                    Toast.makeText(this, "저장 성공", Toast.LENGTH_SHORT).show()
                    Log.d("저장성공", "저장성공")
                }
                .addOnFailureListener{
                        e -> Log.w("저장실패","저장실패")
                    Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show()
                }
        }

        val sundayDecorator = SundayDecorator()
        val saturDayDecorator = SaturDayDecorator()
        val dayDecorator = DayDecorator(this)

        calendarView.addDecorators(dayDecorator,sundayDecorator,saturDayDecorator)

        calendarView.setTitleFormatter { day -> // CalendarDay라는 클래스는 LocalDate 클래스를 기반으로 만들어진 클래스다

            // 때문에 MaterialCalendarView에서 연/월 보여주기를 커스텀하려면 CalendarDay 객체의 getDate()로 연/월을 구한 다음 LocalDate 객체에 넣어서
            // LocalDate로 변환하는 처리가 필요하다
            val inputText: LocalDate = day.date
            val calendarHeaderElements: List<String> = inputText.toString().split("-")
            val calendarHeaderBuilder = StringBuilder()
            calendarHeaderBuilder.append(calendarHeaderElements[0])
                .append(" ")
                .append(calendarHeaderElements[1])
            calendarHeaderBuilder.toString()
        }


    }

    private fun setFragment(tag: String, fragment: Fragment,nickname : String?) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val calender = manager.findFragmentByTag(TAG_SCHEDULE)
        val showSchedule = manager.findFragmentByTag(TAG_SHOW_SCHEDULE)
        val money = manager.findFragmentByTag(TAG_BACK)

        if (calender != null){
            fragTransaction.hide(calender)
        }

        if (showSchedule != null){
            fragTransaction.hide(showSchedule)
        }


        if (money != null) {
            fragTransaction.hide(money)
        }

        if (tag == TAG_SCHEDULE) {
            if (calender!=null){
                val intent = Intent(this, ScheduleActivity::class.java)
                intent.putExtra("nickname",nickname)
                startActivity(intent)
            }
        }
        else if (tag == TAG_SHOW_SCHEDULE) {
            if (showSchedule != null) {
                val intent = Intent(this, ShowScheduleActivity::class.java)
                intent.putExtra("nickname",nickname)
                startActivity(intent)
            }
        }

        else if (tag == TAG_BACK){
            if (money != null){
                val intent = Intent(this, MoneyActivity::class.java)
                intent.putExtra("nickname",nickname)
                startActivity(intent)
            }
        }

        fragTransaction.commitAllowingStateLoss()
    }


    class DayDecorator(context: Context?) : DayViewDecorator {

        private val drawable: Drawable


        init {
            drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.calendar_selector) }!!

        }

        // true를 리턴 시 모든 요일에 내가 설정한 드로어블이 적용된다
        override fun shouldDecorate(day: CalendarDay?): Boolean {

            return true

        }

        // 일자 선택 시 내가 정의한 드로어블이 적용되도록 한다
        override fun decorate(view: DayViewFacade) {
            view.setSelectionDrawable(drawable!!)
            view.addSpan(StyleSpan(Typeface.BOLD)) // 달력 안의 모든 숫자들이 볼드 처리됨

        }
    }


}