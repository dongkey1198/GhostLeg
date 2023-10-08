package com.example.ghostleg.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ghostleg.databinding.ActivityMainBinding
import com.example.ghostleg.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLadderView()
        initStartButton()
        observeLadderView()
    }

    private fun initLadderView() {
        lifecycleScope.launch(Dispatchers.Main) {
            launch {
                viewModel.verticalLinesFlow.collect {
                    binding.ladderView.updateVerticalLines(it)
                }
            }
            launch {
                viewModel.horizontalLinesFlow.collect {
                    binding.ladderView.updateHorizontalLines(it)
                }
            }
        }
    }

    private fun initStartButton() {
        with(binding.buttonStart) {
            setOnClickListener {
                viewModel.generateHorizontalLines()
            }
        }
    }

    private fun observeLadderView() {
        with(binding.ladderView) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewModel.generateVerticalLines(
                        width = width.toFloat(),
                        height = height.toFloat()
                    )
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }
}
