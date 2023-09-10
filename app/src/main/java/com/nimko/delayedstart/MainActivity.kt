package com.nimko.delayedstart

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.nimko.delayedstart.databinding.ActivityMainBinding
import java.time.LocalTime

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var settings:SharedPreferences


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = getSharedPreferences("MySettings", MODE_PRIVATE)
        binding.apply {
            timePickerJob.apply {
                setIs24HourView(true)
                hour = settings.getInt(HOUR_JOB, 0)
                minute = settings.getInt(MINUTE_JOB, 20)
                setOnTimeChangedListener { timePicker, i, i2 -> run {
                    setResult(timePickerJob.hour, timePickerJob.minute, timePickerEnd.hour, timePickerEnd.minute)
                }}
            }
            timePickerEnd.apply {
                setIs24HourView(true)
                hour = settings.getInt(HOUR_END, 6)
                minute = settings.getInt(MINUTE_END, 30)
                setOnTimeChangedListener { timePicker, i, i2 ->
                    run {
                        setResult(timePickerJob.hour, timePickerJob.minute, timePickerEnd.hour, timePickerEnd.minute)
                    }
                }
            }
            setResult(timePickerJob.hour, timePickerJob.minute, timePickerEnd.hour, timePickerEnd.minute)
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun saveSettings() {
        binding.apply {
            settings.edit().apply{
                putInt(HOUR_JOB, timePickerJob.hour)
                putInt(MINUTE_JOB, timePickerJob.minute)
                putInt(HOUR_END, timePickerEnd.hour)
                putInt(MINUTE_END, timePickerEnd.minute)
            }.apply()
        }
    }

    override fun onPause() {
        saveSettings()
        super.onPause()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setResult(hourJob:Int, minuteJob:Int, hourEnd:Int, minuteEnd:Int){
        val nowTime = LocalTime.now()
        val timeForSetMinute :Int
        var timeForSetHour = 24 - nowTime.hour + hourEnd - hourJob
        val timeMinute =  minuteEnd - nowTime.minute + minuteJob
        when{
            timeMinute < 0 -> {
                timeForSetHour--
                timeForSetMinute = 60 + timeMinute
            }
            timeMinute > 59 -> timeForSetMinute = timeMinute - 60
            else -> timeForSetMinute = timeMinute
        }
        binding.apply {
            timeNow.text =
                if(nowTime.minute in 0..9)
                    "${nowTime.hour}:0${nowTime.minute}"
                else
                    "${nowTime.hour}:${nowTime.minute}"
            if (timeForSetHour > 23)
                timeForSetHour = timeForSetHour - 24
            textResult.text =
                if (timeForSetMinute in 0..9)
                    "$timeForSetHour:0$timeForSetMinute"
                else
                    "$timeForSetHour:$timeForSetMinute"
        }
    }


    companion object{
        const val HOUR_JOB = "defaultHourJob"
        const val MINUTE_JOB = "defaultMinuteJob"
        const val HOUR_END = "defaultHourEnd"
        const val MINUTE_END = "defaultMinuteEnd"
    }
}