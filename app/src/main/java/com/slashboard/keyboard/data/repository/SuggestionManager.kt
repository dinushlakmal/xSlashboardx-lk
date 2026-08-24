package com.slashboard.keyboard.data.repository

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.slashboard.keyboard.R
import com.slashboard.keyboard.data.model.DictionaryWord
import com.slashboard.keyboard.ui.components.SuggestionItem

/**
 * Offline-First High-Speed Dual-Language Word Suggestion Pipeline & View Binder.
 *
 * Capabilities:
 * 1. Singlish Mode:
 *    - Real-time Helakuru transliteration candidate generation (e.g. "la" -> "ල", "oya" -> "ඔයා").
 *    - In-memory Trie prefix matching for frequent Sinhala vocabulary (e.g. "ල" -> ["ලංකාව", "ලස්සන", "ලෝකය", "ලැබේ"]).
 *    - Merges direct transliterations + prefix completions, ranked by frequency.
 * 2. English Mode:
 *    - Shortcut expansion (e.g. "brb" -> "Be right back!").
 *    - In-memory Trie prefix search (e.g. "be" -> ["beautiful", "because", "before", "between", "best"]).
 * 3. Dynamic UI Rendering:
 *    - Inflates clickable 15sp white chips inside `#suggestion_container` with ripple backgrounds.
 *    - On-tap commits full word + trailing space, triggers smart self-learning, and transitions back to `#utility_bar`.
 */
object SuggestionManager {

    /**
     * Unified suggestion candidate generation for Singlish and English.
     */
    fun getSuggestions(
        fullText: String,
        currentComposing: String,
        isSinglish: Boolean,
        userDictionary: List<DictionaryWord> = emptyList()
    ): List<SuggestionItem> {
        val trimmedComposing = currentComposing.trim()
        if (trimmedComposing.isEmpty()) return emptyList()

        val results = LinkedHashSet<SuggestionItem>()

        if (isSinglish) {
            // 1. Singlish Mode
            // A. Check user dictionary shortcuts first
            val shortcutMatch = userDictionary.find { it.shortcut.equals(trimmedComposing, ignoreCase = true) }
            if (shortcutMatch != null) {
                results.add(
                    SuggestionItem(
                        display = shortcutMatch.word,
                        replacement = shortcutMatch.word,
                        isPrimary = true,
                        isShortcut = true
                    )
                )
            }

            // B. Direct phonetic transliterations
            val transliterations = HelakuruSinglishParser.getSuggestions(trimmedComposing)
            if (transliterations.isNotEmpty()) {
                val primary = transliterations.first()
                results.add(
                    SuggestionItem(
                        display = primary,
                        replacement = primary,
                        isPrimary = results.isEmpty()
                    )
                )

                // C. Query Sinhala Trie for words starting with the transliterated prefix
                val prefixQuery = primary.trim()
                val trieCompletions = SmartDictionaryEngine.searchSinhala(prefixQuery, limit = 5)
                for (candidate in trieCompletions) {
                    results.add(
                        SuggestionItem(
                            display = candidate.word,
                            replacement = candidate.word,
                            isPrimary = false
                        )
                    )
                    if (results.size >= 5) break
                }

                // D. Append other transliteration alternatives if space remains
                for (alt in transliterations.drop(1)) {
                    if (results.size >= 5) break
                    results.add(
                        SuggestionItem(
                            display = alt,
                            replacement = alt,
                            isPrimary = false
                        )
                    )
                }
            }
        } else {
            // 2. English Mode
            val lower = trimmedComposing.lowercase()

            // A. User dictionary shortcut expansion
            val shortcutMatch = userDictionary.find { it.shortcut.equals(lower, ignoreCase = true) }
            if (shortcutMatch != null) {
                results.add(
                    SuggestionItem(
                        display = shortcutMatch.word,
                        replacement = shortcutMatch.word,
                        isPrimary = true,
                        isShortcut = true
                    )
                )
            }

            // B. In-Memory English Trie prefix completions
            val trieCompletions = SmartDictionaryEngine.searchEnglish(lower, limit = 5)
            for ((index, candidate) in trieCompletions.withIndex()) {
                results.add(
                    SuggestionItem(
                        display = candidate.word,
                        replacement = candidate.word,
                        isPrimary = results.isEmpty() && index == 0
                    )
                )
                if (results.size >= 5) break
            }

            // C. Fallback: If no trie completions, offer raw text
            if (results.isEmpty()) {
                results.add(
                    SuggestionItem(
                        display = trimmedComposing,
                        replacement = trimmedComposing,
                        isPrimary = true
                    )
                )
            }
        }

        return results.take(5).toList()
    }

    /**
     * Programmatically populates the suggestion container LinearLayout with styled, clickable TextView chips.
     *
     * @param container The #suggestion_container LinearLayout inside HorizontalScrollView
     * @param suggestions List of candidate SuggestionItem items
     * @param onSelect Callback invoked when a suggestion chip is tapped
     */
    fun renderSuggestionChips(
        container: LinearLayout,
        suggestions: List<SuggestionItem>,
        onSelect: (SuggestionItem) -> Unit
    ) {
        val context = container.context
        container.removeAllViews()

        if (suggestions.isEmpty()) {
            container.visibility = View.GONE
            return
        }
        container.visibility = View.VISIBLE

        val horizontalPadding = dpToPx(context, 14f)
        val verticalPadding = dpToPx(context, 6f)
        val marginHorizontal = dpToPx(context, 4f)

        suggestions.forEachIndexed { index, item ->
            val chip = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(marginHorizontal, 0, marginHorizontal, 0)
                }

                text = item.display
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                typeface = if (item.isPrimary) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                gravity = Gravity.CENTER
                setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)

                // Background ripple and pill shape
                try {
                    setBackgroundResource(R.drawable.bg_suggestion_chip)
                } catch (e: Exception) {
                    setBackgroundColor(Color.parseColor("#26FFFFFF"))
                }

                if (item.isPrimary) {
                    // Subtle highlight for primary candidate
                    setTextColor(Color.parseColor("#FFE082"))
                }

                isClickable = true
                isFocusable = true
                contentDescription = "Suggestion: ${item.display}"

                setOnClickListener {
                    onSelect(item)
                }
            }

            container.addView(chip)
        }
    }

    private fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}
