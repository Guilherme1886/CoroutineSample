package com.gui.toledo.coroutinesample

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView
    private lateinit var startButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        resultTextView = findViewById(R.id.resultTextView)
        startButton = findViewById(R.id.startButton)
        progressBar = findViewById(R.id.progressBar)

        startButton.setOnClickListener {
            startButton.text = "Running"
            startButton.isEnabled = false
            progressBar.progress = 1
            resultTextView.text = ""
            startCoroutines()
        }
    }

    private suspend fun setProgress() {
        while (progressBar.progress < 100) {
            delay(100)
            progressBar.progress += 1
        }
    }

    private fun startCoroutines() {
        lifecycleScope.launch {
            setProgress()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val results = executeTasks()
            lifecycleScope.launch {
                updateUI(results)
            }
        }
    }

    private suspend fun executeTasks(): List<String> {
        return withContext(Dispatchers.IO) {
            val thread = Thread.currentThread().name
            lifecycleScope.launch {
                Toast.makeText(this@MainActivity, "executing on: $thread", Toast.LENGTH_SHORT)
                    .show()
            }
            val task1 = async { performTask("Task 1") }
            val task2 = async { performTask("Task 2") }
            val task3 = async { performTask("Task 3") }

            val results = listOf(task1.await(), task2.await(), task3.await())

            results
        }
    }

    private suspend fun performTask(taskName: String): String {
        for (i in 0..3) {
            delay(1000)
        }
        return "$taskName completed"
    }

    private fun updateUI(results: List<String>) {
        Toast.makeText(
            this@MainActivity,
            "executing on: ${Thread.currentThread().name}",
            Toast.LENGTH_SHORT
        ).show()
        resultTextView.text = results.joinToString("\n")
        progressBar.progress = 100
        startButton.text = "START COROUTINES"
        startButton.isEnabled = true
    }
}
