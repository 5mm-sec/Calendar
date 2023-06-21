package com.example.finalproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // 수신한 메시지에 데이터 페이로드가 있는지 확인합니다.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            if (true) {
                // 데이터 페이로드를 처리해야하는 경우 작업을 예약합니다.
                scheduleJob()
            } else {
                // 데이터 페이로드를 즉시 처리해야하는 경우 호출합니다.
                handleNow()
            }
        }

        // 수신한 메시지에 알림 페이로드가 있는 경우 알림을 생성하고 표시합니다.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.body)
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // 서버에 토큰을 등록하는 로직을 여기에 구현합니다.
        sendRegistrationToServer(token)
    }

    private fun scheduleJob() {
        // 작업 예약을 수행합니다.
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()
    }

    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    private fun sendRegistrationToServer(token: String?) {
        // 서버에 토큰을 전송하는 로직을 여기에 구현합니다.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    private fun sendNotification(messageBody: String?) {
        Log.d("sendNotification실행", "sendNotification실행")
        if (messageBody != null) {
            Log.d("messageBody", messageBody)
        }

        // 알림을 클릭했을 때 실행할 인텐트를 설정합니다.
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림 채널 ID를 가져옵니다.
        val channelId = getString(R.string.default_notification_channel_id)

        // 기본 알림 소리를 설정합니다.
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 알림을 생성합니다.
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24) // 작은 아이콘 설정
            .setContentTitle(getString(R.string.fcm_message)) // 알림 제목 설정
            .setContentText(messageBody) // 알림 내용 설정
            .setAutoCancel(true) // 알림을 클릭하면 자동으로 닫히도록 설정
            .setSound(defaultSoundUri) // 알림 소리 설정
            .setContentIntent(pendingIntent) // 클릭 시 실행할 인텐트 설정

        // NotificationManager를 가져옵니다.
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android O 이상인 경우 알림 채널을 생성합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 알림을 표시합니다.
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}