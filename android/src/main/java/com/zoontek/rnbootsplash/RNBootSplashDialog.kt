package com.zoontek.rnbootsplash

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager

import android.view.Window
import android.graphics.Color
import android.os.Build
import android.view.View

import androidx.annotation.StyleRes

class RNBootSplashDialog(
  activity: Activity,
  @StyleRes themeResId: Int,
  private val fade: Boolean
) : Dialog(activity, themeResId) {

  init {
    setOwnerActivity(activity)
    setCancelable(false)
    setCanceledOnTouchOutside(false)
  }

  @Deprecated("Deprecated in favor of OnBackPressedCallback")
  override fun onBackPressed() {
    val activity = ownerActivity
    activity?.moveTaskToBack(true)
  }

  override fun dismiss() {
    if (isShowing) {
      runCatching { super.dismiss() }
    }
  }

  fun dismiss(callback: () -> Unit) {
    if (isShowing) {
      setOnDismissListener { callback() }
      runCatching { super.dismiss() }.onFailure { callback() }
    } else {
      callback()
    }
  }

  override fun show() {
    if (!isShowing) {
      runCatching { super.show() }
    }
  }

  fun show(callback: () -> Unit) {
    if (!isShowing) {
      setOnShowListener { callback() }
      runCatching { super.show() }.onFailure { callback() }
    } else {
      callback()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    window?.apply {
      setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

      setWindowAnimations(
        if (fade) R.style.BootSplashFadeOutAnimation else R.style.BootSplashNoAnimation
      )

      if (RNBootSplashModuleImpl.isSamsungOneUI4()) {
        setBackgroundDrawableResource(R.drawable.compat_splash_screen_oneui_4)
      }

      if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
      }

      if (Build.VERSION.SDK_INT >= 19) {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
      }

      if (Build.VERSION.SDK_INT >= 21) {
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        statusBarColor = Color.TRANSPARENT
      }
    }
  }

  private fun setWindowFlag(bits: Int, on: Boolean) {
    window?.let { win ->
      val winParams = win.attributes
      winParams.flags = if (on) {
        winParams.flags or bits
      } else {
        winParams.flags and bits.inv()
      }
      win.attributes = winParams
    }
  }
}
