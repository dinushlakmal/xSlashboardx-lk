package com.slashboard.keyboard.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.preference.PreferenceManager

fun Int.dpToPx(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this * density).toInt()
}

fun Float.dpToPx(context: Context): Float {
    val density = context.resources.displayMetrics.density
    return this * density
}

object ThemeEngine {

    fun isKeyBordersEnabled(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        if (prefs.contains("key_borders_enabled")) {
            return prefs.getBoolean("key_borders_enabled", false)
        }
        val customPrefs = context.getSharedPreferences("slashboard_prefs", Context.MODE_PRIVATE)
        if (customPrefs.contains("key_borders_enabled")) {
            return customPrefs.getBoolean("key_borders_enabled", false)
        }
        if (customPrefs.contains("show_key_borders")) {
            return customPrefs.getBoolean("show_key_borders", false)
        }
        return prefs.getBoolean("show_key_borders", false)
    }

    /**
     * Dynamically generates the Key Background GradientDrawable based on current border preference
     */
    fun createKeyDrawable(context: Context, isPressed: Boolean): Drawable {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val bordersEnabled = isKeyBordersEnabled(context)
        val borderWidthPx = if (bordersEnabled) 1.dpToPx(context) else 0
        val borderColor = if (bordersEnabled) Color.parseColor("#4DFFFFFF") else Color.TRANSPARENT
        val normalDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 8.dpToPx(context).toFloat()
            setColor(if (isPressed) Color.parseColor("#33FFFFFF") else Color.parseColor("#14FFFFFF"))
            setStroke(borderWidthPx, borderColor)
        }
        return normalDrawable
    }

    /**
     * Creates a reactive ripple drawable for standard/special IME keys
     */
    fun createKeyRippleDrawable(
        context: Context,
        fillColor: Int = Color.parseColor("#24FFFFFF"),
        strokeColor: Int = Color.parseColor("#66FFFFFF"),
        radiusDp: Float = 8f,
        strokeWidthDp: Float = 1.2f
    ): Drawable {
        val bordersEnabled = isKeyBordersEnabled(context)
        val strokeWidthPx = if (bordersEnabled) (strokeWidthDp * context.resources.displayMetrics.density).toInt() else 0
        val actualStrokeColor = if (bordersEnabled) strokeColor else Color.TRANSPARENT
        val radiusPx = radiusDp * context.resources.displayMetrics.density

        val content = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(fillColor)
            cornerRadius = radiusPx
            if (strokeWidthPx > 0 && bordersEnabled) {
                setStroke(strokeWidthPx, actualStrokeColor)
            } else {
                setStroke(0, Color.TRANSPARENT)
            }
        }

        val mask = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.WHITE)
            cornerRadius = radiusPx
        }

        return RippleDrawable(
            android.content.res.ColorStateList.valueOf(Color.parseColor("#33FFFFFF")),
            content,
            mask
        )
    }

    fun applyCustomBackground(context: Context, rootView: android.view.View, bitmap: android.graphics.Bitmap?) {
        val bgImageView = rootView.findViewById<android.widget.ImageView>(com.slashboard.keyboard.R.id.keyboard_bg_image) ?: return
        val dimView = rootView.findViewById<android.view.View>(com.slashboard.keyboard.R.id.keyboard_bg_dim)

        if (bitmap != null) {
            bgImageView.visibility = android.view.View.VISIBLE
            bgImageView.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            bgImageView.setImageBitmap(bitmap)

            // Read dim opacity from settings (default: 40% dim)
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val dimLevel = try {
                prefs.getInt("theme_bg_dim_level", 40) / 100f
            } catch (e: Exception) {
                0.4f
            }
            dimView?.visibility = android.view.View.VISIBLE
            dimView?.alpha = dimLevel
        } else {
            bgImageView.visibility = android.view.View.GONE
            dimView?.visibility = android.view.View.GONE
        }
    }
}
