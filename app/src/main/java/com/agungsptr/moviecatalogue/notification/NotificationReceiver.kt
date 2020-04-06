package com.agungsptr.moviecatalogue.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.agungsptr.moviecatalogue.MainActivity
import com.agungsptr.moviecatalogue.R
import com.agungsptr.moviecatalogue.model.Movie
import com.agungsptr.moviecatalogue.view.releasetoday.ReleaseTodayActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ID = "id"
        const val ID_REMAINDER = 200
        const val ID_RELEASE_TODAY = 210
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notifId = intent.getIntExtra(EXTRA_ID, 0)

        if (notifId == ID_REMAINDER) {
            val title = context.resources.getString(R.string.daily_remainder)
            val message = context.resources.getString(R.string.message_daily_remainder)
            showNotification(context, title, message, notifId)
        } else {
            setMovieRelease(context)
        }
    }

    private fun showNotification(
        context: Context,
        title: String,
        message: String,
        notifId: Int
    ) {
        val chanelId = "Channel_1"
        val channelName = "Movie Catalogue"

        val intent = if (notifId == ID_REMAINDER) {
            Intent(context, MainActivity::class.java)
        } else {
            Intent(context, ReleaseTodayActivity::class.java)
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notificationManagerCompat = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, chanelId)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_movies_black)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                chanelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

            builder.setChannelId(chanelId)
            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManagerCompat.notify(notifId, notification)
    }

    fun setRemainder(context: Context, hour: Int, minute: Int, notifId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra(EXTRA_ID, notifId)

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, notifId, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        var message =
            if (notifId == ID_REMAINDER)
                context.resources.getString(R.string.daily_remainder)
            else
                context.resources.getString(R.string.release_today_remainder)
        message += " ${context.resources.getString(R.string.active)}"

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    fun cancelRemainder(context: Context, notifId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, notifId, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)

        var message =
            if (notifId == ID_REMAINDER)
                context.resources.getString(R.string.daily_remainder)
            else
                context.resources.getString(R.string.release_today_remainder)
        message += " ${context.resources.getString(R.string.canceled)}"

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun setMovieRelease(context: Context) {
        val API_KEY = "eafd9e49c70f2f35274ec7c65d9a8816"
        val url =
            "https://api.themoviedb.org/3/discover/movie?api_key=$API_KEY&primary_release_date.gte=${currentDate()}&primary_release_date.lte=${currentDate()}"

        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                try {
                    //parsing json
                    val result = String(responseBody)
                    val responseObject = JSONObject(result)
                    val list = responseObject.getJSONArray("results")

                    val max = if (list.length() < 5) list.length() else 5

                    for (i in 0 until max) {
                        val movieItem = list.getJSONObject(i)
                        val movie = Movie()

                        movie.id = movieItem.getInt("id")
                        movie.title = movieItem.getString("title")
                        movie.poster =
                            "https://image.tmdb.org/t/p/w154/" + movieItem.getString("poster_path")
                        movie.description = movieItem.getString("overview")


                        val title = movie.title
                        val message =
                            "${movie.title} ${context.resources.getString(R.string.release_today)}"
                        showNotification(context, title!!, message, movie.id)
                    }
                } catch (e: Exception) {
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                Log.d("onFailure", error.message.toString())
            }
        })
    }

    private fun currentDate(): String {
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return df.format(date)
    }
}
