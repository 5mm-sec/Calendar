package com.example.finalproject

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.os.Bundle
import android.text.style.StyleSpan
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.finalproject.databinding.ActivityShowscheduleBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate

private const val TAG_SCHEDULE = "fragment_schedule"
private const val TAG_SHOW_SCHEDULE = "fragment_show_schedule"
private const val TAG_BACK = "fragment_back"


class ShowScheduleActivity :AppCompatActivity() {


    private lateinit var binding: ActivityShowscheduleBinding
    var firestore : FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowscheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance() //firestore 연결
        val calendarView = binding.showcalendarView
        val showIncome2 = binding.showIncome2
        val showCost2 = binding.showCost2
        val showMonthIncome2 = binding.showMonthIncome2
        val showMonthCost2 = binding.showMonthCost2

        val scheduleModifyButton = binding.scheduleModifyButton
        val moneyModifyButton = binding.moneyModifyButton
        val schedulDeleteButton = binding.scheduleDeleteButton
        val moneyDeleteButton = binding.moneyDeleteButton
        val scheduleCheckButton = binding.scheduleCheckButton

        if(intent.hasExtra("nickname")){
            val nickname = intent.getStringExtra("nickname")
            Log.d("nickname", nickname.toString())
            //Toast.makeText(this,"$nickname 님 환영합니다.", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"닉네임 가져오기 에러", Toast.LENGTH_SHORT).show()
        }
        val nickname = intent.getStringExtra("nickname").toString()
        Log.d("닉네임테스트",nickname)

        setFragment(TAG_SHOW_SCHEDULE,ShowScheduleFragment(),nickname)

        // binding.navigationView.setOnItemSelectedListener는 네비게이션 뷰에서 아이템이 선택될 때 호출되는 콜백을 설정합니다.
        binding.navigationView.setOnItemSelectedListener { item ->
            // 선택된 아이템의 아이디에 따라 분기합니다.
            when(item.itemId) {
                // 아이템의 아이디가 R.id.calendarFragment인 경우
                R.id.calendarFragment -> setFragment(TAG_SCHEDULE, ScheduleFragment(), nickname)
                // 아이템의 아이디가 R.id.showCalendarFragment인 경우
                R.id.showCalendarFragment -> setFragment(TAG_SHOW_SCHEDULE, ShowScheduleFragment(), nickname)
                // 아이템의 아이디가 R.id.backFragment인 경우
                R.id.backFragment -> setFragment(TAG_BACK, BackFragment(), nickname)
            }
            // true를 반환하여 아이템 선택을 처리했음을 나타냅니다.
            true
        }



        val sundayDecorator2 = SundayDecorator()
        val saturDayDecorator2 = SaturDayDecorator()
        val dayDecorator2 = ScheduleActivity.DayDecorator(this)

        // 일자 선택 시 내가 정의한 드로어블이 적용되도록 한다
        calendarView.addDecorators(dayDecorator2,sundayDecorator2,saturDayDecorator2)

        // edit() 메서드를 호출하여 상태를 수정할 수 있는 Editor 객체를 반환합니다.
        calendarView.state()
            .edit()
            // setFirstDayOfWeek() 메서드를 호출하여 주간 단위의 첫 번째 요일을 설정합니다.
            // DayOfWeek.of(Calendar.MONDAY)를 사용하여 월요일을 첫 번째 요일로 설정합니다.
            .setFirstDayOfWeek(DayOfWeek.of(Calendar.MONDAY))
            // commit() 메서드를 호출하여 수정한 상태를 적용합니다.
            .commit()


        // setTitleFormatter() 메서드를 호출하여 CalendarView의 제목 형식을 설정합니다.
// MonthArrayTitleFormatter 객체를 생성하여 사용자 지정 월 배열 리소스를 전달합니다.
// resources.getTextArray(R.array.custom_months)는 res/values/arrays.xml에 정의된
// custom_months 배열 리소스를 가져옵니다.
        calendarView.setTitleFormatter(MonthArrayTitleFormatter(resources.getTextArray(R.array.custom_months)))

// setWeekDayFormatter() 메서드를 호출하여 CalendarView의 요일 형식을 설정합니다.
// ArrayWeekDayFormatter 객체를 생성하여 사용자 지정 요일 배열 리소스를 전달합니다.
// resources.getTextArray(R.array.custom_weekdays)는 res/values/arrays.xml에 정의된
// custom_weekdays 배열 리소스를 가져옵니다.
        calendarView.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)))

// setHeaderTextAppearance() 메서드를 호출하여 CalendarView의 헤더 텍스트 외관을 설정합니다.
// R.style.CalendarWidgetHeader는 res/values/styles.xml에 정의된
// CalendarWidgetHeader 스타일 리소스를 참조합니다.
        calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader)


        // setOnRangeSelectedListener() 메서드를 호출하여 CalendarView의 범위 선택 이벤트를 처리하는 리스너를 설정합니다.
        // 이벤트가 발생하면 주어진 클로저가 실행됩니다.
        calendarView.setOnRangeSelectedListener { widget, dates ->

            // 선택된 날짜 범위에서 첫 번째 날짜의 문자열 표현을 가져옵니다.
            val startDay = dates[0].date.toString()

            // 선택된 날짜 범위에서 마지막 날짜의 문자열 표현을 가져옵니다.
            // dates.size - 1은 선택된 범위의 마지막 날짜를 나타냅니다.
            val endDay = dates[dates.size - 1].date.toString()

            // 시작일과 종료일을 로그로 출력합니다.
            Log.e(TAG, "시작일 : $startDay, 종료일 : $endDay")
        }

        val sundayDecorator = SundayDecorator()
        val saturDayDecorator = SaturDayDecorator()
        val dayDecorator = DayDecorator(this)


        // 일자 선택 시 내가 정의한 드로어블이 적용되도록 한다
        calendarView.addDecorators(dayDecorator,sundayDecorator,saturDayDecorator)

        val nowDate = calendarView.currentDate
        val nowMonth = (nowDate.month).toString()
        Log.d("Current Month: ", nowMonth)

        var totalIncome = 0 // 총 수입을 저장할 변수 선언
        var totalCost = 0 // 총 지출을 저장할 변수 선언

        firestore?.collection("money")
            ?.whereEqualTo("nickname", nickname)
            ?.get()
            ?.addOnSuccessListener { documents3 ->
                for(document3 in documents3){
                    val income = document3.getString("income")
                    val cost = document3.getString("cost")
                    val date = document3.getString("date")
                    val parts = date?.split("-")
                    val getMonth = parts?.get(1)?.toInt()

                    Log.d("get Month: ", getMonth.toString())

                    if (nowMonth == getMonth.toString()) { // 현재 월과 문서의 월이 일치하는 경우
                        income?.toIntOrNull()?.let { incomeValue ->
                            totalIncome += incomeValue // 수입을 더해줌
                        }
                        cost?.toIntOrNull()?.let { costValue ->
                            totalCost += costValue // 지출을 더해줌
                        }
                    }
                }
                Log.d("Total Income: ", totalIncome.toString())
                Log.d("Total Cost: ", totalCost.toString())

                showMonthIncome2.text = totalIncome.toString()+" 원"
                showMonthCost2.text = totalCost.toString()+" 원"

            }





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


        var users : ArrayList<DataClass> = arrayListOf()

        firestore?.collection("users") // users 데이터 가져오기
            ?.whereEqualTo("nickname", nickname)
            ?.get()
            ?.addOnSuccessListener { documents ->
                for (document in documents) {
                    val todo = document.getString("todo")
                    val enddate = document.getString("enddate")
                    val nickname = document.getString("nickname")
                    val endtime = document.getString("endtime")
                    val starttime = document.getString("starttime")
                    val startdate = document.getString("startdate")

                    val parts = enddate?.split("-")
                    val year = parts?.get(0)?.toInt()
                    val month = parts?.get(1)?.toInt()
                    val day = parts?.get(2)?.toInt()

                    val insertSchedule = CalendarDay.from(year!!, month!!, day!!)
                    calendarView.addDecorators(CurrentDayDecorator(this,insertSchedule))



                    Log.d(TAG, "${document.id} => ${document.data}")
                }

                calendarView.setOnDateChangedListener(object : OnDateSelectedListener {
                    @SuppressLint("MissingInflatedId")
                    override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {

                        binding.showStartTimeTextView2.text = "-"
                        binding.showEndTimeTextView2.text = "-"
                        binding.todoTextView2.text = "-"
                        binding.showIncome2.text = "-"
                        binding.showCost2.text = "-"

                        val selectDate = date.date.toString()
                        val parts2 = selectDate.split("-")
                        val day2 = parts2?.get(2)?.toInt()

                        // 여기에서 documents를 사용할 수 있습니다.
                        for (document in documents) {
                            val todo = document.getString("todo")
                            val enddate = document.getString("enddate")
                            val nickname = document.getString("nickname")
                            val endtime = document.getString("endtime")
                            val starttime = document.getString("starttime")
                            val startdate = document.getString("startdate")

                            val parts = enddate?.split("-")
                            val day = parts?.get(2)?.toInt()

                            // 사용 예시: 해당 날짜에 대한 작업 수행
                            if (day2 == day) {
                                binding.showStartTimeTextView2.text = starttime
                                binding.showEndTimeTextView2.text = endtime
                                binding.todoTextView2.text = todo
                            }
                        }

                        Log.d("날짜 선택 : $selectDate", "날짜 선택 : $selectDate")

                        firestore?.collection("money")
                            ?.whereEqualTo("nickname",nickname)
                            ?.whereEqualTo("date",selectDate)
                            ?.get()
                            ?.addOnSuccessListener { documents2->
                                for(document2 in documents2){
                                    val income = document2.getString("income")
                                    val cost = document2.getString("cost")
                                    val date = document2.getString("date")

                                    val parts = date?.split("-")
                                    val year = parts?.get(0)?.toInt()
                                    val month = parts?.get(1)?.toInt()
                                    val moneyday = parts?.get(2)?.toInt()

                                    Log.d("day", day2.toString())
                                    Log.d("moneyday", moneyday.toString())

                                    if(day2 == moneyday){
                                        binding.showIncome2.text = income+" 원"
                                        binding.showCost2.text = cost+" 원"
                                    }
                                    Log.d(TAG, "${document2.id} => ${document2.data}")
                                }

                            }

                        binding.scheduleModifyButton.setOnClickListener {
                            // AlertDialog 생성
                            val builder = AlertDialog.Builder(this@ShowScheduleActivity)
                            builder.setTitle("일정 수정")
                            val dialogLayout = layoutInflater.inflate(R.layout.dialog_schedule_modify, null)
                            builder.setView(dialogLayout)

                            // AlertDialog의 확인 버튼 클릭 시
                            builder.setPositiveButton("확인") { dialogInterface, _ ->
                                // 입력한 데이터 가져오기
                                val todoEditText = dialogLayout.findViewById<EditText>(R.id.todoEditText)
                                val endTimeEditText = dialogLayout.findViewById<EditText>(R.id.endTimeEditText)
                                val startTimeEditText = dialogLayout.findViewById<EditText>(R.id.startTimeEditText)

                                val todo = todoEditText.text.toString()
                                val endTime = endTimeEditText.text.toString()
                                val startTime = startTimeEditText.text.toString()

                                // Firestore에서 해당 일정의 문서를 업데이트
                                firestore?.collection("users")
                                    ?.whereEqualTo("nickname", nickname)
                                    ?.whereEqualTo("enddate", selectDate)
                                    ?.get()
                                    ?.addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            // 해당 문서 업데이트
                                            val documentRef = firestore?.collection("users")?.document(document.id)
                                            documentRef?.update(
                                                "todo", todo,
                                                "endtime", endTime,
                                                "starttime", startTime
                                            )
                                                ?.addOnSuccessListener {
                                                    // 업데이트 성공 시 작업 수행
                                                    Log.d(TAG, "Document successfully updated!")
                                                }
                                                ?.addOnFailureListener { e ->
                                                    // 업데이트 실패 시 예외 처리
                                                    Log.w(TAG, "Error updating document", e)
                                                }
                                        }
                                    }
                            }
                            // AlertDialog의 취소 버튼 클릭 시
                            builder.setNegativeButton("취소") { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }

                            // AlertDialog 표시
                            builder.show()
                        }

                        binding.scheduleDeleteButton.setOnClickListener{
                            // 선택한 날짜의 문자열을 가져옵니다.
                            val selectedDate = date.date.toString()

                            // 해당 일자의 데이터를 삭제하기 위해 Firestore에서 해당 문서를 쿼리합니다.
                            firestore?.collection("users")
                                ?.whereEqualTo("nickname", nickname)
                                ?.whereEqualTo("enddate", selectedDate)
                                ?.get()
                                ?.addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        // 문서를 삭제합니다.
                                        firestore?.collection("users")
                                            ?.document(document.id)
                                            ?.delete()
                                            ?.addOnSuccessListener {
                                                // 삭제 성공 시 작업 수행
                                                Log.d(TAG, "Document successfully deleted!")
                                                // 필요한 경우 여기에 추가적인 작업을 수행할 수 있습니다.
                                            }
                                            ?.addOnFailureListener { e ->
                                                // 삭제 실패 시 예외 처리
                                                Log.w(TAG, "Error deleting document", e)
                                            }
                                    }
                                }
                        }
                        binding.moneyModifyButton.setOnClickListener {
                            // AlertDialog 생성
                            val builder = AlertDialog.Builder(this@ShowScheduleActivity)
                            builder.setTitle("수입 및 지출 수정")
                            val dialogLayout = layoutInflater.inflate(R.layout.dialog_money_modify, null)
                            builder.setView(dialogLayout)

                            // AlertDialog의 확인 버튼 클릭 시
                            builder.setPositiveButton("확인") { dialogInterface, _ ->
                                // 입력한 데이터 가져오기
                                val incomeEditText = dialogLayout.findViewById<EditText>(R.id.incomeEditText)
                                val costEditText = dialogLayout.findViewById<EditText>(R.id.costEditText)

                                val income = incomeEditText.text.toString()
                                val cost = costEditText.text.toString()

                                // Firestore에서 해당 일자의 문서를 업데이트
                                firestore?.collection("money")
                                    ?.whereEqualTo("nickname", nickname)
                                    ?.whereEqualTo("date", selectDate)
                                    ?.get()
                                    ?.addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            // 해당 문서 업데이트
                                            val documentRef = firestore?.collection("money")?.document(document.id)
                                            documentRef?.update(
                                                "income", income,
                                                "cost", cost
                                            )
                                                ?.addOnSuccessListener {
                                                    // 업데이트 성공 시 작업 수행
                                                    Log.d(TAG, "Document successfully updated!")
                                                }
                                                ?.addOnFailureListener { e ->
                                                    // 업데이트 실패 시 예외 처리
                                                    Log.w(TAG, "Error updating document", e)
                                                }
                                        }
                                    }
                            }

                            // AlertDialog의 취소 버튼 클릭 시
                            builder.setNegativeButton("취소") { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }

                            // AlertDialog 표시
                            builder.show()
                        }

                        binding.moneyDeleteButton.setOnClickListener{
                            // 선택한 날짜의 문자열을 가져옵니다.
                            val selectedDate = date.date.toString()

                            // 해당 일자의 데이터를 삭제하기 위해 Firestore에서 해당 문서를 쿼리합니다.
                            firestore?.collection("money")
                                ?.whereEqualTo("nickname", nickname)
                                ?.whereEqualTo("date", selectedDate)
                                ?.get()
                                ?.addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        // 문서를 삭제합니다.
                                        firestore?.collection("money")
                                            ?.document(document.id)
                                            ?.delete()
                                            ?.addOnSuccessListener {
                                                // 삭제 성공 시 작업 수행
                                                Log.d(TAG, "Document successfully deleted!")

                                                // 필요한 경우 여기에 추가적인 작업을 수행할 수 있습니다.
                                            }
                                            ?.addOnFailureListener { e ->
                                                // 삭제 실패 시 예외 처리
                                                Log.w(TAG, "Error deleting document", e)
                                            }
                                    }
                                }

                        }
                        binding.scheduleCheckButton.setOnClickListener {
                            // 선택한 날짜의 CalendarDay 객체를 가져옵니다.
                            val selectedDate = calendarView.selectedDate
                            // 선택한 날짜에 해당하는 decorator를 제거합니다.


                            // 새로운 decorator를 추가합니다.

                            Log.d("checkSelectDate", selectedDate.toString())

                            val calendarDayStr = selectedDate.toString()
                            val parts = calendarDayStr.split("{")[1].split("}")[0].split("-")
                            val year = parts[0].toInt()
                            val month = parts[1].toInt()
                            val day = parts[2].toInt()

                            Log.d("Year", year.toString())
                            Log.d("Month", month.toString())
                            Log.d("Day", day.toString())

                            val insertSchedule = CalendarDay.from(year!!, month!!, day!!)
                            calendarView.addDecorators(CurrentDayDecorator2(this,insertSchedule))

                            // 선택한 날짜를 기준으로 다시 decorator를 적용합니다.
                            if (selectedDate != null) {
                                calendarView.addDecorators(CurrentDayDecorator2(this,insertSchedule))
                            }
                    }

                    }



                })




            }
            ?.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }




    }

    // setFragment()는 지정된 태그와 프래그먼트를 사용하여 화면에 새로운 프래그먼트를 추가하거나 기존 프래그먼트를 숨기는 역할을 합니다.
    private fun setFragment(tag: String, fragment: Fragment, nickname: String?) {
        // FragmentManager를 가져옵니다.
        val manager: FragmentManager = supportFragmentManager

        // FragmentTransaction을 생성합니다.
        val fragTransaction = manager.beginTransaction()

        // 지정된 태그로 프래그먼트를 검색하여 null인 경우에만 추가합니다.
        if (manager.findFragmentByTag(tag) == null) {
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        // 태그에 해당하는 프래그먼트를 검색합니다.
        val calender = manager.findFragmentByTag(TAG_SCHEDULE)
        val showSchedule = manager.findFragmentByTag(TAG_SHOW_SCHEDULE)
        val money = manager.findFragmentByTag(TAG_BACK)

        // 검색된 프래그먼트를 숨깁니다.
        if (calender != null) {
            fragTransaction.hide(calender)
        }

        if (showSchedule != null) {
            fragTransaction.hide(showSchedule)
        }

        if (money != null) {
            fragTransaction.hide(money)
        }

        // 태그에 따라 다른 동작을 수행합니다.
        if (tag == TAG_SCHEDULE) {
            // TAG_SCHEDULE에 해당하는 프래그먼트가 검색되면 ScheduleActivity로 이동하는 인텐트를 생성하고 시작합니다.
            if (calender != null) {
                val intent = Intent(this, ScheduleActivity::class.java)
                intent.putExtra("nickname", nickname)
                startActivity(intent)
            }
        } else if (tag == TAG_SHOW_SCHEDULE) {
            // TAG_SHOW_SCHEDULE에 해당하는 프래그먼트가 검색되면 ShowScheduleActivity로 이동하는 인텐트를 생성하고 시작합니다.
            if (showSchedule != null) {
                val intent = Intent(this, ShowScheduleActivity::class.java)
                intent.putExtra("nickname", nickname)
                startActivity(intent)
            }
        } else if (tag == TAG_BACK) {
            // TAG_BACK에 해당하는 프래그먼트가 검색되면 MoneyActivity로 이동하는 인텐트를 생성하고 시작합니다.
            if (money != null) {
                val intent = Intent(this, MoneyActivity::class.java)
                intent.putExtra("nickname", nickname)
                startActivity(intent)
            }
        }


        fragTransaction.commitAllowingStateLoss()
    }

    class DayDecorator(context: Context?) : DayViewDecorator {
        // 드로어블을 저장할 변수 선언
        private val drawable: Drawable

        // 생성자에서 Context를 받아서 드로어블을 초기화합니다.
        init {
            drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.calendar_selector) }!!
        }

        // shouldDecorate() 메서드는 모든 요일에 대해 내가 설정한 드로어블을 적용할지 여부를 결정합니다.
        // 항상 true를 반환하므로 모든 요일에 드로어블이 적용됩니다.
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return true
        }

        // decorate() 메서드는 DayViewFacade에 드로어블과 스타일 스팬을 추가하여 날짜 뷰를 장식합니다.
        override fun decorate(view: DayViewFacade) {
            view.setSelectionDrawable(drawable) // 드로어블을 선택 상태에 적용합니다.
            view.addSpan(StyleSpan(Typeface.BOLD)) // 스타일 스팬을 추가하여 텍스트를 굵게 표시합니다.
        }
    }
}
