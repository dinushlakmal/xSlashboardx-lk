package com.slashboard.keyboard

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.slashboard.keyboard.data.model.SmartbarAction

class SmartbarSettingsActivity : AppCompatActivity() {

    private lateinit var activeAdapter: SmartbarActionAdapter
    private lateinit var disabledAdapter: SmartbarActionAdapter
    
    private val activeActions = mutableListOf<SmartbarAction>()
    private val disabledActions = mutableListOf<SmartbarAction>()
    private val prefs by lazy { SlashboardApp.instance.preferencesRepository }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smartbar_customizer)

        val settings = prefs.settingsFlow.value
        
        settings.smartbarActiveActions.forEach { id ->
            SmartbarAction.fromId(id)?.let { activeActions.add(it) }
        }
        
        settings.smartbarDisabledActions.forEach { id ->
            SmartbarAction.fromId(id)?.let { disabledActions.add(it) }
        }

        activeAdapter = SmartbarActionAdapter(activeActions, false) { action, pos ->
            // Move to disabled
            activeActions.removeAt(pos)
            activeAdapter.notifyItemRemoved(pos)
            disabledActions.add(action)
            disabledAdapter.notifyItemInserted(disabledActions.size - 1)
            savePreferences()
        }

        disabledAdapter = SmartbarActionAdapter(disabledActions, true) { action, pos ->
            // Move to active
            disabledActions.removeAt(pos)
            disabledAdapter.notifyItemRemoved(pos)
            activeActions.add(action)
            activeAdapter.notifyItemInserted(activeActions.size - 1)
            savePreferences()
        }

        val rvActive = findViewById<RecyclerView>(R.id.rv_active_actions)
        rvActive.layoutManager = GridLayoutManager(this, 5)
        rvActive.adapter = activeAdapter
        
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(activeAdapter))
        itemTouchHelper.attachToRecyclerView(rvActive)

        val rvDisabled = findViewById<RecyclerView>(R.id.rv_disabled_actions)
        rvDisabled.layoutManager = GridLayoutManager(this, 5)
        rvDisabled.adapter = disabledAdapter

        findViewById<Button>(R.id.btn_reset_default).setOnClickListener {
            activeActions.clear()
            activeActions.addAll(SmartbarAction.DEFAULT_ACTIVE)
            disabledActions.clear()
            disabledActions.addAll(SmartbarAction.DEFAULT_DISABLED)
            activeAdapter.notifyDataSetChanged()
            disabledAdapter.notifyDataSetChanged()
            savePreferences()
        }
    }

    override fun onPause() {
        super.onPause()
        savePreferences()
    }

    private fun savePreferences() {
        prefs.updateSmartbarActions(
            activeActions.map { it.id },
            disabledActions.map { it.id }
        )
    }
}
