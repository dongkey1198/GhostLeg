package com.example.ghostleg.view

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
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
        initGame()
        initStartButton()
        initResetButton()
        setObservers()
    }

    private fun initGame() {
        viewModel.initGame()
        binding.ladderView.apply {
            viewTreeObserver.addOnGlobalLayoutListener {
                viewModel.initLadder(width.toFloat(), height.toFloat())
            }
        }
    }

    private fun initStartButton() {
        binding.buttonStart.setOnClickListener {
            // TODO: 게임 시작
        }
    }

    private fun initResetButton() {
        binding.buttonReset.setOnClickListener {
            viewModel.resetGame()
        }
    }

    private fun setObservers() {
        lifecycleScope.launch(Dispatchers.Main) {
            // Player Labels
            launch {
                viewModel.playerLabelsFlow.collect { playerLabels ->
                    playerLabels.forEach { playerLabel ->
                        TextView(this@MainActivity).apply {
                            text = playerLabel
                            textSize = 16f
                            gravity = Gravity.CENTER
                            typeface = Typeface.DEFAULT_BOLD
                            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f)
                        }.let {
                            binding.layoutPlayerLabel.addView(it)
                        }
                    }
                }
            }
            // Result Labels
            launch {
                viewModel.gameResultLabelsFlow.collect { gameResults ->
                    binding.layoutGameResult.removeAllViews()
                    gameResults.forEach { gameResult ->
                        TextView(this@MainActivity).apply {
                            text = gameResult
                            textSize = 16f
                            gravity = Gravity.CENTER
                            typeface = Typeface.DEFAULT_BOLD
                            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f)
                        }.let {
                            binding.layoutGameResult.addView(it)
                        }
                    }
                }
            }
            // Vertical Lines
            launch {
                viewModel.verticalLinesFlow.collect { binding.ladderView.updateVerticalLines(it) }
            }
            // Horizontal Lines
            launch {
                viewModel.horizontalLinesFlow.collect { binding.ladderView.updateHorizontalLines(it) }
            }
        }
    }
}
