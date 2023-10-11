package com.example.ghostleg.view.setting

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ghostleg.R

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
    }

    companion object {

        const val INTENT_KEY_PLAYER_COUNT = "PLAYER_COUNT"

        fun buildIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }
}