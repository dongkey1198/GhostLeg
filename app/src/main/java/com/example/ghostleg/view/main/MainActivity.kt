package com.example.ghostleg.view.main

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
import com.example.ghostleg.R
import com.example.ghostleg.databinding.ActivityMainBinding
import com.example.ghostleg.view.setting.SettingActivity
import com.example.ghostleg.viewmodel.ViewModelFactory
import com.example.ghostleg.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { ViewModelFactory(this) }
    private val animator by lazy {
        ObjectAnimator.ofFloat(binding.ladderRoutesView, ANIMATION_PROPERTY, 0.0f, 1.0f)
            .apply {
                duration = GAME_PLAYING_DURATION
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        viewModel.gameEnded()
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
        initSettingButton()
        setObservers()
    }

    private fun initGame() {
        binding.ladderView.apply {
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewModel.ladderViewSizeDetected(width.toFloat(), height.toFloat())
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    private fun initStartButton() {
        binding.buttonStart.setOnClickListener {
            viewModel.startButtonClicked()
        }
    }

    private fun initResetButton() {
        binding.buttonReset.setOnClickListener {
            viewModel.resetButtonClicked()
        }
    }

    private fun initSettingButton() {
        binding.buttonSetting.setOnClickListener {
            viewModel.settingButtonClicked()
        }
    }

    private fun setObservers() {
        lifecycleScope.launch(Dispatchers.Main) {
            // Player Labels
            launch {
                viewModel.playerLabelsFlow.collect { playerLabels ->
                    binding.layoutPlayerLabel.removeAllViews()
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
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.label_game_playing_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            // Move to Setting Activity
            launch {
                viewModel.moveToSettingPageFlow.collect {
                    startActivity(SettingActivity.buildIntent(this@MainActivity))
                }
            }
        }
    }

    companion object {
        private const val ANIMATION_PROPERTY = "percentage"
        private const val GAME_PLAYING_DURATION = 5000L
    }
}
