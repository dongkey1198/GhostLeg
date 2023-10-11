package com.example.ghostleg.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ghostleg.databinding.ActivityMainBinding
import com.example.ghostleg.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val animator by lazy {
        ObjectAnimator.ofFloat(binding.ladderRoutesView, ANIMATION_PROPERTY, 0.0f, 1.0f)
            .apply {
                duration = 9000L
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        viewModel.updateStartButtonState()
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        viewModel.updateIsPlaying()
                        viewModel.updateResultBlindState()
                    }
                })
            }
    }

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
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewModel.initLadder(width.toFloat(), height.toFloat())
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    private fun initStartButton() {
        binding.buttonStart.setOnClickListener {
            viewModel.startGame()
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
                            layoutParams = LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT,
                                1.0f
                            )
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
                            layoutParams = LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT,
                                1.0f
                            )
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
            // Ladder Routes
            launch {
                viewModel.ladderRoutesFlow.collect { ladderRoutes ->
                    with(binding.ladderRoutesView) {
                        if (ladderRoutes.isNotEmpty()) {
                            initView(ladderRoutes)
                            animator.start()
                        } else {
                            resetView()
                            // 지워야함..
                            animator.cancel()
                        }
                    }
                }
            }
            // Start Button
            launch {
                viewModel.startButtonStateFlow.collect {
                    binding.buttonStart.isEnabled = it
                }
            }
            // Result Blind
            launch {
                viewModel.resultBlindStateFlow.collect {
                    if (it) {
                        binding.textViewBlind.visibility = View.VISIBLE
                    } else {
                        binding.textViewBlind.visibility = View.GONE
                    }
                }
            }
            // Game Playing Message
            launch {
                viewModel.gameStateMessageFlow.collect {
                   Toast.makeText(this@MainActivity, getString(it), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val ANIMATION_PROPERTY = "percentage"
    }
}
