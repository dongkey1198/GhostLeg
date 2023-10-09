package com.example.ghostleg.view

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewTreeObserver
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
        initPlayerLabels()
        initGameResults()
        initLadderView()
        initStartButton()
    }

    private fun initPlayerLabels() {
        lifecycleScope.launch(Dispatchers.Main) {
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
    }

    private fun initGameResults() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.gameResultLabelsFlow.collect { gameResults ->
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
    }

    private fun initLadderView() {
        with(binding.ladderView) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewModel.initGame(width.toFloat(), height.toFloat())
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
            lifecycleScope.launch(Dispatchers.Main) {
                launch {
                    viewModel.verticalLinesFlow.collect { updateVerticalLines(it) }
                }
                launch {
                    viewModel.horizontalLinesFlow.collect { updateHorizontalLines(it) }
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
}
