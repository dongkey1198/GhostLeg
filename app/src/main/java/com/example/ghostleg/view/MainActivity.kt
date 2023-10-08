package com.example.ghostleg.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.ghostleg.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeLadderView()
    }

    private fun observeLadderView() {
        with(binding.ladderView) {
            viewTreeObserver.addOnGlobalLayoutListener {
                Log.d("aaa", "$height, $width")
            }
        }
    }
}
