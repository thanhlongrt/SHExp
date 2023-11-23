package com.example.salehub.second_activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.salehub.databinding.ActivitySecondBinding
import com.example.salehub.utils.Utils.TAG

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e(TAG, "onCreate: Second")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.e(TAG, "onRestoreInstanceState: Second")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.e(TAG, "onSaveInstanceState: Second")
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart: Second")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume: Second")
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause: Second")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop: Second")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy: Second")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e(TAG, "onRestart: Second")
    }
}
