package com.nimko.delayedstart

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.nimko.delayedstart.databinding.ActivityMainBinding
import java.time.LocalTime

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            timePickerJob.apply {
                setIs24HourView(true)
                hour = 0
                minute = 20
                setOnTimeChangedListener { timePicker, i, i2 -> run {
                    setResult(timePickerJob.hour, timePickerJob.minute, timePickerEnd.hour, timePickerEnd.minute)
                }}
            }
            timePickerEnd.apply {
                setIs24HourView(true)
                hour = 6
                minute = 30
                setOnTimeChangedListener { timePicker, i, i2 ->
                    run {
                        setResult(timePickerJob.hour, timePickerJob.minute, timePickerEnd.hour, timePickerEnd.minute)
                    }
                }
            }
            setResult(timePickerJob.hour, timePickerJob.minute, timePickerEnd.hour, timePickerEnd.minute)
        }

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
            textResult.text =
            if (timeForSetHour > 23)
                resources.getString(R.string.error)
            else
                if (timeForSetMinute in 0..9)
                    "$timeForSetHour:0$timeForSetMinute"
                else
                    "$timeForSetHour:$timeForSetMinute"
        }


    }
}