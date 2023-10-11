package com.example.ghostleg.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ghostleg.data.datastore.DataStoreManager
import com.example.ghostleg.data.datastore.LadderGameDataStore
import com.example.ghostleg.data.repository.LadderGameRepositoryImpl
import com.example.ghostleg.viewmodel.main.MainViewModel
import com.example.ghostleg.viewmodel.setting.SettingViewModel

class ViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(
                    LadderGameRepositoryImpl(
                        LadderGameDataStore(DataStoreManager(context).getLadderGameDataStore())
                    )
                ) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(
                    LadderGameRepositoryImpl(
                        LadderGameDataStore(DataStoreManager(context).getLadderGameDataStore())
                    )
                ) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel Class")
            }
        }
    }
}
