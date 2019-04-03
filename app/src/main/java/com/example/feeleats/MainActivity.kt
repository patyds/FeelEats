package com.example.feeleats

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.feeleats.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var buttonSpeak: Button? = null
    private var editText: EditText? = null
    private var positive = ArrayList<String>()
    private var negative = ArrayList<String>()
    private var drinks = ArrayList<String>()
    private var meals = ArrayList<String>()
    private var soups = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val file = application.assets.open("positive.csv")
        val isr = InputStreamReader(file)
        val reader = BufferedReader(isr)

        var line = reader.readLine()
        while(line!=null){
            positive.add(line)
            line = reader.readLine()
        }

        val fileN = application.assets.open("negative.csv")
        val isrN = InputStreamReader(fileN)
        val readerN = BufferedReader(isrN)

        var lineN = readerN.readLine()
        while(lineN!=null){
            negative.add(lineN)
            lineN = readerN.readLine()
        }
        val fileM = application.assets.open("menu.csv")
        val isrM = InputStreamReader(fileM)
        val readerM = BufferedReader(isrM)

        var lineM = readerM.readLine()
        while(lineM!=null){
            var menu : List<String> = lineM.split(",")
            drinks.add(menu[0])
            meals.add(menu[1])
            soups.add(menu[2])
            lineM = readerM.readLine()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonSpeak = this.button_speak
        editText = this.edittext_input

        buttonSpeak!!.isEnabled = false;
        tts = TextToSpeech(this, this)

        buttonSpeak!!.setOnClickListener { speakOut() }
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            } else {
                buttonSpeak!!.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }

    }

    private fun speakOut() {
        val text = editText!!.text.toString()
        tts!!.speak(recommend(text), TextToSpeech.QUEUE_FLUSH, null,"")
    }

    private fun recommend(text: String): String{
        var moodPositive = 0
        var moodNegative = 0

        text.toLowerCase().split(" ").forEach{
            if(positive.contains(it)){
                moodPositive++
            }
            if(negative.contains(it)){
                moodNegative++
            }
        }
        if( moodPositive > moodNegative){
            val drink = (1..drinks.size-1).random()
            val meal = (1..drinks.size-1).random()
            val soup = (1..drinks.size-1).random()

            return "I suggest you take a glass of "+ drinks[drink]+ ", eat "+meals[meal]+ " and "+soups[soup]
        }else if(moodPositive< moodNegative){
            val drink = (0..14).random()
            val meal = (0..14).random()
            val soup = (0..14).random()
            return "I suggest you take a glass of "+ drinks[drink]+ ", eat "+meals[meal]+ " and "+soups[soup]
        }else{
            val drink = (15..19).random()
            val meal = (15..19).random()
            val soup = (15..19).random()
            return "I suggest you take a glass of "+ drinks[drink]+ ", eat "+meals[meal]+ " and "+soups[soup]
        }

    }

    public override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

}