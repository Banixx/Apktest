package com.banixx.retrodialer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    
    private lateinit var numberDisplay: TextView
    private lateinit var callButton: Button
    private lateinit var dialWheel: android.widget.ImageView
    private lateinit var touchArea: android.view.View
    
    private var currentNumber = StringBuilder()
    private var isDialing = false
    private var lastAngle: Double = 0.0
    private var startAngle: Double = 0.0
    private var accumulatedRotation: Float = 0f
    
    // Positionen der Zahlen auf der Wählscheibe (im Uhrzeigersinn)
    private val numberPositions = listOf(
        Pair("1", 60.0),
        Pair("2", 30.0),
        Pair("3", 0.0),
        Pair("4", 330.0),
        Pair("5", 300.0),
        Pair("6", 270.0),
        Pair("7", 240.0),
        Pair("8", 210.0),
        Pair("9", 180.0),
        Pair("0", 150.0),
        Pair("*", 120.0),
        Pair("#", 90.0)
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Views initialisieren
        numberDisplay = findViewById(R.id.numberDisplay)
        callButton = findViewById(R.id.callButton)
        dialWheel = findViewById(R.id.dialWheel)
        touchArea = findViewById(R.id.touchArea)
        
        // Touch-Listener für die Wählscheibe
        touchArea.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startAngle = getAngle(event.x, event.y)
                    lastAngle = startAngle
                    isDialing = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val currentAngle = getAngle(event.x, event.y)
                    val delta = currentAngle - lastAngle
                    
                    // Normalisieren der Drehung
                    if (delta > 180) {
                        lastAngle += 360
                    } else if (delta < -180) {
                        lastAngle -= 360
                    }
                    
                    accumulatedRotation += delta.toFloat()
                    dialWheel.rotation = accumulatedRotation
                    lastAngle = currentAngle
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isDialing) {
                        val endAngle = getAngle(event.x, event.y)
                        val totalRotation = accumulatedRotation
                        
                        // Bestimmen, welche Zahl gewählt wurde
                        val selectedNumber = detectSelectedNumber(totalRotation)
                        if (selectedNumber.isNotEmpty()) {
                            addDigit(selectedNumber)
                        }
                        
                        // Zurücksetzen
                        accumulatedRotation = 0f
                        dialWheel.rotation = 0f
                        isDialing = false
                    }
                    true
                }
                else -> false
            }
        }
        
        // Anruf-Button
        callButton.setOnClickListener {
            if (currentNumber.length >= 10) {
                makeCall()
            }
        }
    }
    
    private fun getAngle(x: Float, y: Float): Double {
        val centerX = touchArea.width / 2f
        val centerY = touchArea.height / 2f
        
        val dx = x - centerX
        val dy = y - centerY
        
        // Berechne Winkel in Grad (0-360)
        var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))
        if (angle < 0) {
            angle += 360.0
        }
        return angle
    }
    
    private fun detectSelectedNumber(rotation: Float): String {
        // Normalisieren der Rotation
        var normalizedRotation = rotation % 360
        if (normalizedRotation < 0) {
            normalizedRotation += 360
        }
        
        // Bestimmen, welche Zahl am nächsten ist
        var closestNumber = ""
        var minDistance = Float.MAX_VALUE
        
        for ((number, angle) in numberPositions) {
            val distance = Math.abs(normalizedRotation - angle)
            val wrappedDistance = 360 - distance
            val actualDistance = minOf(distance, wrappedDistance)
            
            if (actualDistance < minDistance && actualDistance < 30) {
                minDistance = actualDistance
                closestNumber = number
            }
        }
        
        return closestNumber
    }
    
    private fun addDigit(digit: String) {
        if (currentNumber.length < 10) {
            currentNumber.append(digit)
            updateDisplay()
            
            // Vibration für haptisches Feedback
            // (Vibration würde hier implementiert werden, falls gewünscht)
        }
        
        // Anruf-Button aktivieren, wenn 10 Ziffern erreicht
        callButton.isEnabled = currentNumber.length >= 10
    }
    
    private fun updateDisplay() {
        numberDisplay.text = if (currentNumber.isEmpty()) "" else currentNumber.toString()
    }
    
    private fun makeCall() {
        val phoneNumber = currentNumber.toString()
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }
    
    // Löschen-Button für den Fall, dass man korrigieren möchte
    override fun onBackPressed() {
        if (currentNumber.isNotEmpty()) {
            currentNumber.deleteCharAt(currentNumber.length - 1)
            updateDisplay()
            callButton.isEnabled = currentNumber.length >= 10
        } else {
            super.onBackPressed()
        }
    }
}
