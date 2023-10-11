package com.example.ghostleg.view.setting

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ghostleg.R
import com.example.ghostleg.databinding.ActivitySettingBinding
import com.example.ghostleg.viewmodel.setting.SettingViewModel
import com.example.ghostleg.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private val viewModel: SettingViewModel by viewModels { ViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDecrementButton()
        intiIncrementButton()
        initSaveButton()
        initCloseButton()
        setObservers()
    }

    private fun initDecrementButton() {
        binding.buttonDecrement.setOnClickListener {
            viewModel.decrementButtonClicked()
        }
    }

    private fun intiIncrementButton() {
        binding.buttonIncrement.setOnClickListener {
            viewModel.incrementButtonClicked()
        }
    }

    private fun initSaveButton() {
        binding.buttonSave.setOnClickListener {
            viewModel.saveButtonClicked()
        }
    }

    private fun initCloseButton() {
        binding.buttonClose.setOnClickListener {
            viewModel.closeButtonClicked()
        }
    }

    private fun setObservers() {
        lifecycleScope.launch(Dispatchers.Main) {
            launch {
                viewModel.playerCountFlow.collect {
                    binding.textViewPlayerCount.text = it.toString()
                }
            }
            launch {
                viewModel.playerLimitMessageFlow.collect {
                    Toast.makeText(
                        this@SettingActivity,
                        getString(R.string.label_player_count_limit_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            launch {
                viewModel.pageCloseFlow.collect {
                    if (it) finish()
                }
            }
        }
    }

    companion object {
        fun buildIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }
}