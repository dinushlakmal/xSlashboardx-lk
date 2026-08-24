package com.slashboard.keyboard.service

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.provider.Settings
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.slashboard.keyboard.MainActivity
import com.slashboard.keyboard.R
import com.slashboard.keyboard.SlashboardApp
import com.slashboard.keyboard.data.repository.HelakuruSinglishParser
import com.slashboard.keyboard.data.repository.SuggestionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Principal Android IME Service for Sinhala & English phonetic typing.
 *
 * Implements:
 * 1. Top-to-Bottom Row Hierarchy (Rows 0-6):
 *    - Layer 0: Root Container with Transparent/Translucent Wallpaper & Dim View
 *    - Row 0 (42dp): Topmost Dynamic Header:
 *        * #utility_settings_bar ([En], [Emoji], [Settings], [Aa], [Mic], [Clipboard], [Collapse])
 *        * #word_suggestion_bar (#suggestion_chips_container)
 *    - Row 1 (36dp): Dedicated Frequently Used Emoji Bar ([ 🥺 | 😔 | 💔 | 😇 | 😎 | 😁 | 😽 | ❤️ | 🤤 | 😘 ])
 *    - Row 2 (42dp): Dedicated Number Row [1 2 3 4 5 6 7 8 9 0]
 *    - Row 3 (46dp): QWERTY Row [q w e r t y u i o p] with secondary hints
 *    - Row 4 (46dp): Home Row [a s d f g h j k l] with secondary hints
 *    - Row 5 (46dp): Bottom Character Row [Shift, z x c v b n m, Backspace]
 *    - Row 6 (46dp): Action Row [123, Globe, ,, Spacebar - Red Pill, ., Enter - Blue Pill]
 * 2. Dynamic cross-fade transitions between #utility_settings_bar and #word_suggestion_bar.
 * 3. Real-time Singlish & English suggestion pipeline via SuggestionManager & HelakuruSinglishParser.
 */
open class SinhalaIME : InputMethodService() {

    // Views
    protected var rootView: View? = null
    protected var utilitySettingsBar: LinearLayout? = null
    protected var wordSuggestionBar: HorizontalScrollView? = null
    protected var suggestionChipsContainer: LinearLayout? = null
    protected var emojiContainer: LinearLayout? = null
    protected var spacebarLabel: TextView? = null
    protected var shiftKey: ImageView? = null
    protected var langToggleBtn: TextView? = null
    protected var wallpaperView: ImageView? = null
    protected var dimView: View? = null

    // State
    protected val composingBuffer = StringBuilder()
    protected var isSinglishMode = true
    protected var isShiftActive = false
    protected var isCapsLocked = false
    protected var lastShiftPressTime = 0L
    protected var isSuggestionStripVisible = false

    private val mainHandler = Handler(Looper.getMainLooper())
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // User-frequent emojis for Row 1
    private val frequentEmojis = listOf(
        "🥺", "😔", "💔", "😇", "😎", "😁", "😽", "❤️", "🤤", "😘",
        "😂", "🤣", "🔥", "👍", "🙏", "😍", "✨", "👏", "💯", "💪",
        "🤔", "🙌", "💙", "🌸", "☕", "👌"
    )

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            (application as? SlashboardApp)?.preferencesRepository?.settingsFlow?.collect {
                applyCustomWallpaper()
                applyKeyStyles()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onCreateInputView(): View {
        val root = layoutInflater.inflate(R.layout.keyboard_root, null)
        rootView = root

        val app = application as? SlashboardApp
        com.slashboard.keyboard.data.repository.SmartDictionaryEngine.initialize(this, app?.database)

        bindViews(root)
        setupEmojiBar()
        setupKeyListeners(root)
        setupUtilityBarActions()
        updateLanguageState(isSinglishMode)

        return root
    }

    private fun bindViews(root: View) {
        utilitySettingsBar = root.findViewById(R.id.utility_settings_bar)
        wordSuggestionBar = root.findViewById(R.id.word_suggestion_bar)
        suggestionChipsContainer = root.findViewById(R.id.suggestion_chips_container)
        emojiContainer = root.findViewById(R.id.emoji_container)
        spacebarLabel = root.findViewById(R.id.spacebar_label)
        shiftKey = root.findViewById(R.id.key_shift)
        langToggleBtn = root.findViewById(R.id.btn_lang_toggle)
        wallpaperView = root.findViewById(R.id.wallpaper_view)
        dimView = root.findViewById(R.id.dim_view)

        applyCustomWallpaper()
        applyKeyStyles()

        // Ensure default state: utility_settings_bar VISIBLE, word_suggestion_bar GONE
        utilitySettingsBar?.apply {
            visibility = View.VISIBLE
            alpha = 1.0f
            translationY = 0f
        }
        wordSuggestionBar?.apply {
            visibility = View.GONE
            alpha = 0.0f
            translationY = 15f
        }
        suggestionChipsContainer?.removeAllViews()
        isSuggestionStripVisible = false
    }

    private fun applyCustomWallpaper() {
        val app = application as? SlashboardApp ?: return
        val settings = app.preferencesRepository.settingsFlow.value
        val wallpaperPath = settings.customWallpaperPath

        if (!wallpaperPath.isNullOrBlank()) {
            val bitmap = com.slashboard.keyboard.data.repository.OnlineThemeRepository.loadWallpaperBitmap(wallpaperPath)
            if (bitmap != null) {
                wallpaperView?.setImageBitmap(bitmap)
                wallpaperView?.alpha = 1.0f
                wallpaperView?.visibility = View.VISIBLE
                dimView?.visibility = View.VISIBLE
                val alpha = settings.wallpaperDim.coerceIn(0.0f, 0.95f)
                val dimColorInt = ((alpha * 255).toInt() shl 24) or 0x000000
                dimView?.setBackgroundColor(dimColorInt)
                rootView?.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                return
            }
        }
        wallpaperView?.setImageDrawable(null)
        wallpaperView?.visibility = View.GONE
        dimView?.visibility = View.GONE
        rootView?.setBackgroundColor(0xFF1E1E2C.toInt())
    }

    /**
     * ROW 1: Dedicated Frequently Used Emoji Bar (36dp height, permanently visible)
     */
    private fun setupEmojiBar() {
        val container = emojiContainer ?: return
        container.removeAllViews()

        val paddingH = dpToPx(6f)
        val paddingV = dpToPx(2f)
        val marginH = dpToPx(3f)

        frequentEmojis.forEach { emoji ->
            val tv = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(marginH, 0, marginH, 0)
                }
                text = emoji
                textSize = 18f
                gravity = Gravity.CENTER
                setPadding(paddingH, paddingV, paddingH, paddingV)
                setBackgroundResource(R.drawable.bg_emoji_chip)
                isClickable = true
                isFocusable = true
                contentDescription = "Emoji $emoji"

                setOnClickListener {
                    vibrateFeedback()
                    commitComposingText()
                    currentInputConnection?.commitText(emoji, 1)
                }
            }
            container.addView(tv)
        }
    }

    /**
     * Composing Active Animation:
     * - Animate #utility_settings_bar from 1f to 0f (translationY = -15f), 150ms -> GONE
     * - Show #word_suggestion_bar: VISIBLE, animate from 0f to 1f (translationY = 0f), 150ms
     */
    fun showSuggestionBar() {
        if (isSuggestionStripVisible) return
        isSuggestionStripVisible = true

        val uBar = utilitySettingsBar
        val sStrip = wordSuggestionBar

        uBar?.animate()
            ?.alpha(0f)
            ?.translationY(-15f)
            ?.setDuration(150)
            ?.withEndAction {
                uBar.visibility = View.GONE
            }
            ?.start()

        sStrip?.apply {
            visibility = View.VISIBLE
            alpha = 0f
            translationY = 15f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(150)
                .start()
        }
    }

    /**
     * Composing Finished Animation:
     * - Animate #word_suggestion_bar from 1f to 0f (translationY = 15f), 150ms -> GONE, clear chips
     * - Show #utility_settings_bar: VISIBLE, animate from 0f to 1f (translationY = 0f), 150ms
     */
    fun showUtilityBar() {
        if (!isSuggestionStripVisible) return
        isSuggestionStripVisible = false

        val uBar = utilitySettingsBar
        val sStrip = wordSuggestionBar

        sStrip?.animate()
            ?.alpha(0f)
            ?.translationY(15f)
            ?.setDuration(150)
            ?.withEndAction {
                sStrip.visibility = View.GONE
                suggestionChipsContainer?.removeAllViews()
            }
            ?.start()

        uBar?.apply {
            visibility = View.VISIBLE
            alpha = 0f
            translationY = -15f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(150)
                .start()
        }
    }

    /**
     * Trigger real-time suggestions and dynamic cross-fade
     */
    private fun updateSuggestions() {
        val sContainer = suggestionChipsContainer ?: return
        val currentQuery = composingBuffer.toString()

        if (currentQuery.isEmpty()) {
            showUtilityBar()
            return
        }

        // Generate candidate suggestions
        val ic = currentInputConnection
        val fullText = ic?.getTextBeforeCursor(100, 0)?.toString() ?: ""
        val suggestions = SuggestionManager.getSuggestions(
            fullText = fullText,
            currentComposing = currentQuery,
            isSinglish = isSinglishMode
        )

        if (suggestions.isNotEmpty()) {
            SuggestionManager.renderSuggestionChips(
                container = sContainer,
                suggestions = suggestions
            ) { selectedItem ->
                vibrateFeedback()
                commitSelectedSuggestion(selectedItem.replacement)
            }
            showSuggestionBar()
        } else {
            showUtilityBar()
        }
    }

    private fun commitSelectedSuggestion(replacement: String) {
        val ic = currentInputConnection ?: return
        ic.commitText("$replacement ", 1)

        val app = application as? SlashboardApp
        com.slashboard.keyboard.data.repository.SmartDictionaryEngine.learnWord(
            word = replacement,
            isSinhala = isSinglishMode,
            database = app?.database
        )

        composingBuffer.clear()
        showUtilityBar()
    }

    private fun commitComposingText(overrideWith: String? = null) {
        val ic = currentInputConnection ?: return
        if (composingBuffer.isNotEmpty()) {
            val textToCommit = overrideWith ?: if (isSinglishMode) {
                HelakuruSinglishParser.parse(composingBuffer.toString())
            } else {
                composingBuffer.toString()
            }
            ic.commitText(textToCommit, 1)
            composingBuffer.clear()
        }
        showUtilityBar()
    }

    /**
     * Key Bindings across Rows 2-6
     */
    private fun setupKeyListeners(root: View) {
        // ROW 2: Number Row [1 2 3 4 5 6 7 8 9 0]
        val numIds = intArrayOf(
            R.id.key_num_1, R.id.key_num_2, R.id.key_num_3, R.id.key_num_4, R.id.key_num_5,
            R.id.key_num_6, R.id.key_num_7, R.id.key_num_8, R.id.key_num_9, R.id.key_num_0
        )
        val nums = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        for (i in numIds.indices) {
            root.findViewById<TextView>(numIds[i])?.setOnClickListener {
                vibrateFeedback()
                commitComposingText()
                currentInputConnection?.commitText(nums[i], 1)
            }
        }

        // ROW 3 & 4 & 5: QWERTY Character Keys
        val charKeyMap = mapOf(
            R.id.key_q to "q", R.id.key_w to "w", R.id.key_e to "e", R.id.key_r to "r", R.id.key_t to "t",
            R.id.key_y to "y", R.id.key_u to "u", R.id.key_i to "i", R.id.key_o to "o", R.id.key_p to "p",
            R.id.key_a to "a", R.id.key_s to "s", R.id.key_d to "d", R.id.key_f to "f", R.id.key_g to "g",
            R.id.key_h to "h", R.id.key_j to "j", R.id.key_k to "k", R.id.key_l to "l",
            R.id.key_z to "z", R.id.key_x to "x", R.id.key_c to "c", R.id.key_v to "v", R.id.key_b to "b",
            R.id.key_n to "n", R.id.key_m to "m"
        )

        charKeyMap.forEach { (viewId, charStr) ->
            root.findViewById<TextView>(viewId)?.setOnClickListener {
                vibrateFeedback()
                handleCharacterInput(charStr)
            }
        }

        // ROW 5: Shift Key
        root.findViewById<ImageView>(R.id.key_shift)?.setOnClickListener {
            vibrateFeedback()
            handleShiftToggle()
        }

        // ROW 5: Backspace Key
        root.findViewById<ImageView>(R.id.key_backspace)?.setOnClickListener {
            vibrateFeedback()
            handleBackspace()
        }

        // ROW 6: ?123 Symbols Toggle
        root.findViewById<TextView>(R.id.key_sym_toggle)?.setOnClickListener {
            vibrateFeedback()
            commitComposingText()
        }

        // ROW 6: Globe Switch
        root.findViewById<ImageView>(R.id.key_globe)?.setOnClickListener {
            vibrateFeedback()
            toggleLanguage()
        }

        // ROW 6: Comma [,]
        root.findViewById<TextView>(R.id.key_comma)?.setOnClickListener {
            vibrateFeedback()
            commitComposingText()
            currentInputConnection?.commitText(",", 1)
        }

        // ROW 6: Red Pill Spacebar
        root.findViewById<View>(R.id.key_spacebar)?.setOnClickListener {
            vibrateFeedback()
            handleSpacePress()
        }

        // ROW 6: Period [.]
        root.findViewById<TextView>(R.id.key_period)?.setOnClickListener {
            vibrateFeedback()
            commitComposingText()
            currentInputConnection?.commitText(".", 1)
        }

        // ROW 6: Blue Pill Enter
        root.findViewById<ImageView>(R.id.key_enter)?.setOnClickListener {
            vibrateFeedback()
            handleEnterPress()
        }
    }

    private fun handleCharacterInput(baseChar: String) {
        val ic = currentInputConnection ?: return
        val effectiveChar = if (isShiftActive || isCapsLocked) {
            baseChar.uppercase()
        } else {
            baseChar.lowercase()
        }

        if (isShiftActive && !isCapsLocked) {
            isShiftActive = false
            updateShiftUI()
        }

        if (isSinglishMode) {
            composingBuffer.append(effectiveChar)
            val transliterated = HelakuruSinglishParser.parse(composingBuffer.toString())
            ic.setComposingText(transliterated, 1)
            updateSuggestions()
        } else {
            composingBuffer.append(effectiveChar)
            ic.setComposingText(composingBuffer.toString(), 1)
            updateSuggestions()
        }
    }

    private fun handleBackspace() {
        val ic = currentInputConnection ?: return
        if (composingBuffer.isNotEmpty()) {
            composingBuffer.deleteCharAt(composingBuffer.length - 1)
            if (composingBuffer.isNotEmpty()) {
                val transliterated = if (isSinglishMode) {
                    HelakuruSinglishParser.parse(composingBuffer.toString())
                } else {
                    composingBuffer.toString()
                }
                ic.setComposingText(transliterated, 1)
            } else {
                ic.finishComposingText()
            }
            updateSuggestions()
        } else {
            val selected = ic.getSelectedText(0)
            if (selected.isNullOrEmpty()) {
                ic.deleteSurroundingText(1, 0)
            } else {
                ic.commitText("", 1)
            }
            showUtilityBar()
        }
    }

    private fun handleSpacePress() {
        val ic = currentInputConnection ?: return
        if (composingBuffer.isNotEmpty()) {
            commitComposingText()
            ic.commitText(" ", 1)
        } else {
            ic.commitText(" ", 1)
        }
        showUtilityBar()
    }

    private fun handleEnterPress() {
        val ic = currentInputConnection ?: return
        if (composingBuffer.isNotEmpty()) {
            commitComposingText()
        }
        val editorInfo = currentInputEditorInfo
        val actionId = editorInfo?.actionId ?: 0
        val imeOptions = editorInfo?.imeOptions ?: 0
        val actionMasked = imeOptions and EditorInfo.IME_MASK_ACTION

        if (actionMasked != EditorInfo.IME_ACTION_NONE && actionMasked != EditorInfo.IME_ACTION_UNSPECIFIED) {
            ic.performEditorAction(actionMasked)
        } else if (actionId != 0) {
            ic.performEditorAction(actionId)
        } else {
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        }
        showUtilityBar()
    }

    private fun handleShiftToggle() {
        val now = System.currentTimeMillis()
        if (now - lastShiftPressTime < 400) {
            // Double tap -> Caps lock
            isCapsLocked = !isCapsLocked
            isShiftActive = isCapsLocked
        } else {
            isCapsLocked = false
            isShiftActive = !isShiftActive
        }
        lastShiftPressTime = now
        updateShiftUI()
    }

    private fun updateShiftUI() {
        shiftKey?.apply {
            when {
                isCapsLocked -> {
                    alpha = 1.0f
                    setColorFilter(0xFFFFD54F.toInt())
                }
                isShiftActive -> {
                    alpha = 1.0f
                    clearColorFilter()
                }
                else -> {
                    alpha = 0.6f
                    clearColorFilter()
                }
            }
        }
    }

    private fun setupUtilityBarActions() {
        // Language Toggle [En] / [සිං]
        langToggleBtn?.setOnClickListener {
            vibrateFeedback()
            toggleLanguage()
        }

        // Emoji
        rootView?.findViewById<ImageView>(R.id.btn_utility_emoji)?.setOnClickListener {
            vibrateFeedback()
        }

        // Settings
        rootView?.findViewById<ImageView>(R.id.btn_utility_settings)?.setOnClickListener {
            vibrateFeedback()
            try {
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            } catch (_: Exception) {}
        }

        // Case (Aa)
        rootView?.findViewById<ImageView>(R.id.btn_utility_case)?.setOnClickListener {
            vibrateFeedback()
            handleShiftToggle()
        }

        // Mic
        rootView?.findViewById<ImageView>(R.id.btn_utility_mic)?.setOnClickListener {
            vibrateFeedback()
        }

        // Clipboard
        rootView?.findViewById<ImageView>(R.id.btn_utility_clipboard)?.setOnClickListener {
            vibrateFeedback()
        }

        // Collapse
        rootView?.findViewById<ImageView>(R.id.btn_utility_collapse)?.setOnClickListener {
            vibrateFeedback()
            requestHideSelf(0)
        }
    }

    private fun toggleLanguage() {
        commitComposingText()
        isSinglishMode = !isSinglishMode
        updateLanguageState(isSinglishMode)
    }

    private fun updateLanguageState(singlish: Boolean) {
        spacebarLabel?.text = if (singlish) "Singlish (සිංහල)" else "English (US)"
        langToggleBtn?.text = if (singlish) "සිං" else "En"
    }

    private fun vibrateFeedback() {
        try {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(20)
        } catch (_: Throwable) {}
    }

    private fun dpToPx(dp: Float): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun applyKeyStyles() {
        val root = rootView ?: return
        val app = application as? SlashboardApp ?: return
        val settings = app.preferencesRepository.settingsFlow.value
        val showBorders = settings.showKeyBorders
        val cornerRadiusPx = dpToPx(settings.keyCornerRadius.toFloat()).toFloat()
        val strokeWidthPx = if (showBorders) dpToPx(1f) else 0
        val strokeColor = android.graphics.Color.parseColor("#38FFFFFF")

        fun createKeyDrawable(fillColor: Int, strokeCol: Int = strokeColor, radius: Float = cornerRadiusPx, strokeW: Int = strokeWidthPx): android.graphics.drawable.Drawable {
            val content = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                setColor(fillColor)
                cornerRadius = radius
                if (strokeW > 0) {
                    setStroke(strokeW, strokeCol)
                }
            }
            val mask = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                setColor(android.graphics.Color.WHITE)
                cornerRadius = radius
            }
            val rippleColorState = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4DFFFFFF"))
            return android.graphics.drawable.RippleDrawable(rippleColorState, content, mask)
        }

        val normalKeyBg = { createKeyDrawable(android.graphics.Color.parseColor("#24FFFFFF")) }
        val specialKeyBg = { createKeyDrawable(android.graphics.Color.parseColor("#36FFFFFF")) }
        val spacebarBg = {
            val strokeW = if (showBorders) dpToPx(1.5f) else 0
            createKeyDrawable(
                fillColor = android.graphics.Color.parseColor("#4A1525"),
                strokeCol = android.graphics.Color.parseColor("#E91E63"),
                radius = dpToPx(22f).toFloat(),
                strokeW = strokeW
            )
        }
        val enterBg = {
            val strokeW = if (showBorders) dpToPx(1.5f) else 0
            createKeyDrawable(
                fillColor = android.graphics.Color.parseColor("#1565C0"),
                strokeCol = android.graphics.Color.parseColor("#42A5F5"),
                radius = dpToPx(22f).toFloat(),
                strokeW = strokeW
            )
        }

        // Apply to number keys
        val numIds = intArrayOf(
            R.id.key_num_1, R.id.key_num_2, R.id.key_num_3, R.id.key_num_4, R.id.key_num_5,
            R.id.key_num_6, R.id.key_num_7, R.id.key_num_8, R.id.key_num_9, R.id.key_num_0
        )
        numIds.forEach { id ->
            root.findViewById<View>(id)?.background = normalKeyBg()
        }

        // Apply to char key containers
        val charIds = intArrayOf(
            R.id.key_q, R.id.key_w, R.id.key_e, R.id.key_r, R.id.key_t,
            R.id.key_y, R.id.key_u, R.id.key_i, R.id.key_o, R.id.key_p,
            R.id.key_a, R.id.key_s, R.id.key_d, R.id.key_f, R.id.key_g,
            R.id.key_h, R.id.key_j, R.id.key_k, R.id.key_l,
            R.id.key_z, R.id.key_x, R.id.key_c, R.id.key_v, R.id.key_b,
            R.id.key_n, R.id.key_m
        )
        charIds.forEach { id ->
            (root.findViewById<View>(id)?.parent as? View)?.background = normalKeyBg()
        }

        // Apply to special & action keys
        root.findViewById<View>(R.id.key_shift)?.background = specialKeyBg()
        root.findViewById<View>(R.id.key_backspace)?.background = specialKeyBg()
        root.findViewById<View>(R.id.key_sym_toggle)?.background = specialKeyBg()
        root.findViewById<View>(R.id.key_globe)?.background = specialKeyBg()
        root.findViewById<View>(R.id.key_comma)?.background = normalKeyBg()
        root.findViewById<View>(R.id.key_period)?.background = normalKeyBg()
        root.findViewById<View>(R.id.key_spacebar)?.background = spacebarBg()
        root.findViewById<View>(R.id.key_enter)?.background = enterBg()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        composingBuffer.clear()
        applyCustomWallpaper()
        applyKeyStyles()
        // Reset state directly
        utilitySettingsBar?.apply {
            visibility = View.VISIBLE
            alpha = 1.0f
            translationY = 0f
        }
        wordSuggestionBar?.apply {
            visibility = View.GONE
            alpha = 0.0f
            translationY = 15f
        }
        suggestionChipsContainer?.removeAllViews()
        isSuggestionStripVisible = false
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        commitComposingText()
    }
}
