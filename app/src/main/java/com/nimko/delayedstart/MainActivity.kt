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
    private lateinit var binding: ActivityMainBinding
    private lateinit var settings:SharedPreferences


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
    private fun saveSettings() {
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
    private fun setResult(hourJob:Int, minuteJob:Int, hourEnd:Int, minuteEnd:Int){
        val nowTime = LocalTime.now()
        var timeForSetHour = 24 - nowTime.hour + hourEnd - hourJob
        val timeMinute =  minuteEnd - nowTime.minute + minuteJob
        val timeForSetMinute = when{
            timeMinute < 0 -> {
                timeForSetHour--
                60 + timeMinute
            }
            timeMinute > 59 -> timeMinute - 60
            else -> timeMinute
        }
        binding.apply {
            timeNow.text =
                if(nowTime.minute in 0..9)
                    "${nowTime.hour}:0${nowTime.minute}"
                else
                    "${nowTime.hour}:${nowTime.minute}"
            if (timeForSetHour > 23)  timeForSetHour -= 24
            textResult.text =
                if (timeForSetMinute in 0..9) {
                    "$timeForSetHour:0$timeForSetMinute"
                }
                else {
                    "$timeForSetHour:$timeForSetMinute"
                }
        }
    }


    companion object{
        const val HOUR_JOB = "defaultHourJob"
        const val MINUTE_JOB = "defaultMinuteJob"
        const val HOUR_END = "defaultHourEnd"
        const val MINUTE_END = "defaultMinuteEnd"
    }
}