package com.example.team_kodex

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.socket.client.IO
import io.socket.client.Socket


class MainActivity : AppCompatActivity() {


    // Initialize Socket.IO
    private var socket: Socket = IO.socket("http://your-socket-io-server-address")






    private lateinit var tvHeading: TextView
    private lateinit var btnSubmit: Button
    private lateinit var etNumber: EditText
    private lateinit var etName: EditText

    // Bottom Navigation Buttons
    private lateinit var btnNav1: Button
    private lateinit var btnNav2: Button
    private lateinit var btnNav3: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        socket.connect()


        // Initialize views
        tvHeading = findViewById(R.id.tvHeading)
        btnSubmit = findViewById(R.id.btnSubmit)
        etNumber = findViewById(R.id.etNumber)
        etName = findViewById(R.id.etName)

        // Initialize bottom navigation buttons
        btnNav1 = findViewById(R.id.btnNav1)
        btnNav2 = findViewById(R.id.btnNav2)
        btnNav3 = findViewById(R.id.btnNav3)

        // Set click listeners for the bottom navigation buttons
        btnNav1.setOnClickListener {
            // Handle click for Nav 1 button
            showToast("Navigation 1 Clicked")
        }

        btnNav2.setOnClickListener {
            // Handle click for Nav 2 button
            showToast("Navigation 2 Clicked")
        }

        btnNav3.setOnClickListener {
            // Handle click for Nav 3 button
            showToast("Navigation 3 Clicked")
        }

        // Set a click listener for the main button
        btnSubmit.setOnClickListener {
            // Retrieve input values
            val headingText = tvHeading.text.toString()
            val numberValue = etNumber.text.toString().toDoubleOrNull()
            val nameValue = etName.text.toString()


            if (numberValue == null) {
                Toast.makeText(this, "Please fill contact field", Toast.LENGTH_SHORT).show()


            }
            else if (nameValue == null){
                android.widget.Toast.makeText(this, "Please fill name field", android.widget.Toast.LENGTH_SHORT).show()
            }
            else{
                socket.emit("submitData", numberValue, nameValue)
            }


            // Handle the name value
        }

        // Inside onCreate method

// Set up a listener for a response from the server
        socket.on("serverResponse") { args ->
            val response = args[0] as String
            runOnUiThread {
                // Handle the response from the server
                showToast(response)
            }
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
