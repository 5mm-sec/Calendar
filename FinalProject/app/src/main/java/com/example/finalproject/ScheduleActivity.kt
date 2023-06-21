package com.example.finalproject

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.os.Bundle
import android.text.style.StyleSpan
import android.util.Log
import android.widget.CalendarView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.finalproject.databinding.ActivityScheduleBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import kotlinx.android.synthetic.main.activity_schedule.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.messaging.Constants.MessagePayloadKeys.SENDER_ID
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage


private const val TAG_SCHEDULE = "fragment_schedule"
private const val TAG_SHOW_SCHEDULE = "fragment_show_schedule"
private const val TAG_BACK = "fragment_back"





class ScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleBinding

    private val TAG = this.javaClass.simpleName

    private val calendarView: MaterialCalendarView? = null
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleBinding.inflate(layoutInflater)
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

        setFragment(TAG_SCHEDULE,ScheduleFragment(),nickname)

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.calendarFragment -> setFragment(TAG_SCHEDULE, ScheduleFragment(),nickname)
                R.id.showCalendarFragment -> setFragment(TAG_SHOW_SCHEDULE, ShowScheduleFragment(),nickname)
                //R.id.moneyFragment ->setFragment(TAG_MONEY, MoneyFragment(),nickname)
                R.id.backFragment-> setFragment(TAG_BACK, BackFragment(),nickname)
            }
            true
        }

        val startDateTextView2 = binding.startDateTextView2
        val endDateTextView2 = binding.endDateTextView2
        val stratTimeButton = binding.startTimeButton
        val endTimerButton = binding.endTimeButton
        val saveScheduleButton = binding.scheduleSaveButton


        startTimeButton.setOnClickListener{
            showStartTimePickerDialog()
        }
        endTimerButton.setOnClickListener{
            showEndTimePickerDialog()
        }


        val calendarView = binding.calendarView
        calendarView.state()
            .edit()
            .setFirstDayOfWeek(DayOfWeek.of(Calendar.MONDAY))
            .commit();


        calendarView.setTitleFormatter(MonthArrayTitleFormatter(resources.getTextArray(R.array.custom_months)))
        calendarView.setWeekDayFormatter(ArrayWeekDayFormatter(resources.getTextArray(R.array.custom_weekdays)))

        calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader)


        calendarView.setOnRangeSelectedListener { widget, dates -> // 아래 로그를 통해 시작일, 종료일이 어떻게 찍히는지 확인하고 본인이 필요한 방식에 따라 바꿔 사용한다
            // UTC 시간을 구하려는 경우 이 라이브러리에서 제공하지 않으니 별도의 로직을 짜서 만들어내 써야 한다
            val startDay = dates[0].date.toString()
            val endDay = dates[dates.size - 1].date.toString()
            Log.e(TAG, "시작일 : $startDay, 종료일 : $endDay")

            startDateTextView2.setText(startDay)
            endDateTextView2.setText(endDay)
        }

        calendarView.setOnDateChangedListener(object : OnDateSelectedListener {
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
                // 선택한 날짜에 대한 이벤트 처리
                // 이곳에서 원하는 이벤트를 호출하거나 처리할 수 있습니다.
                // 선택한 날짜는 'date' 매개변수로 전달됩니다.

                val selectDate = date.date.toString()

                Log.d("날짜 선택 : $selectDate","날짜 선택 : $selectDate")
            }
        })

        val sundayDecorator = SundayDecorator()
        val saturDayDecorator = SaturDayDecorator()
        val dayDecorator = DayDecorator(this)




        // 일자 선택 시 내가 정의한 드로어블이 적용되도록 한다
        calendarView.addDecorators(dayDecorator,sundayDecorator,saturDayDecorator)


        // 좌우 화살표 가운데의 연/월이 보이는 방식 커스텀

        // 좌우 화살표 가운데의 연/월이 보이는 방식 커스텀
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



        saveScheduleButton.setOnClickListener{

            val startDateTextView2 : String = startDateTextView2.text.toString()
            var endDateTextView2 : String = endDateTextView2.text.toString()
            val startTimeTextView2 : String = startTimeTextView2.text.toString()
            val endTimeTextView2 : String = endTimeTextView2.text.toString()
            val todoEditText : String = todoEditText.text.toString()
            Log.d(startDateTextView2,startDateTextView2)
            Log.d(endDateTextView2,endDateTextView2)
            Log.d(startTimeTextView2,startTimeTextView2)
            Log.d(endTimeTextView2,endTimeTextView2)
            Log.d(todoEditText,todoEditText)

            var indexingEnddate = endDateTextView2.substring(8).toInt()
            println(indexingEnddate) // 출력:
            var indexingStartdate = startDateTextView2.substring(8).toInt()
            println(indexingStartdate)


            while(indexingEnddate != indexingStartdate-1){
                val saveData = hashMapOf(
                    "nickname" to nickname,
                    "startdate" to startDateTextView2,
                    "enddate" to endDateTextView2,
                    "starttime" to startTimeTextView2,
                    "endtime" to endTimeTextView2,
                    "todo" to todoEditText
                
                )
                db.collection("users")
                    .add(saveData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "저장 성공", Toast.LENGTH_SHORT).show()
                        Log.d("저장성공", "저장성공")
                    }
                    .addOnFailureListener{
                            e -> Log.w("저장실패","저장실패")
                        Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show()
                    }
                indexingEnddate--
                endDateTextView2 = "${endDateTextView2.dropLast(2)}$indexingEnddate" // 종료일에서 마지막 2자리를 indexingEnddate로 덮어씌우기
                Log.d("endDateTextView2",endDateTextView2)

                sendScheduledNotification(todoEditText, endDateTextView2, startTimeTextView2)
                
            }

        }

    }

    private fun sendScheduledNotification(todo: String, startDate: String, startTime: String) {
        val title = "일정 알림"
        val messageBody = "일정이 예약되었습니다. - $todo ($startDate, $startTime)"

        // 알림에 표시될 제목과 본문을 설정합니다.
        val notificationData = hashMapOf(
            "title" to title,
            "body" to messageBody
        )

        // 고유한 메시지 ID 생성
        val messageId = java.lang.String.valueOf(System.currentTimeMillis())

        Log.d("FCM", "Sending scheduled notification. Message ID: $messageId")

        // RemoteMessage.Builder를 사용하여 메시지를 생성합니다.
        val message = RemoteMessage.Builder("$SENDER_ID@fcm.googleapis.com")
            .setMessageId(messageId)
            .setData(notificationData) // 알림 데이터 설정
            .build()

        Log.d("message", message.toString())

        Log.d("SENDER_ID", SENDER_ID)

        try {
            // FirebaseMessaging 인스턴스를 사용하여 메시지를 전송합니다.
            FirebaseMessaging.getInstance().send(message)
            Log.d("FCM", "Scheduled notification sent successfully. Message ID: $messageId")
        } catch (exception: Exception) {
            // 전송 실패 시 예외 처리
            Log.e("FCM", "Failed to send scheduled notification. Message ID: $messageId", exception)
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

    private fun showStartTimePickerDialog() {
        val calendar = Calendar.getInstance()

        // 현재 시간을 가져옵니다.
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // 시작 시간을 표시할 TextView를 가져옵니다.
        val startTimeTextview2 = binding.startTimeTextView2

        // TimePickerDialog를 생성합니다.
        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                // 선택한 시간을 처리하는 부분입니다.
                // 예시로 선택한 시간을 로그로 출력합니다.
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                println("Selected time: $selectedTime")
                startTimeTextview2.setText(selectedTime)
            },
            currentHour,
            currentMinute,
            false
        )

        // TimePickerDialog를 화면에 표시합니다.
        timePickerDialog.show()
    }

    private fun showEndTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val endTimeTextView2 = binding.endTimeTextView2

        Log.d("7","7")

        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                // 선택한 시간 처리를 여기에 작성합니다.
                // 예시로 선택한 시간을 로그로 출력합니다.
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                println("Selected time: $selectedTime")
                endTimeTextView2.setText(selectedTime)

            },
            currentHour,
            currentMinute,
            false
        )

        timePickerDialog.show()
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
