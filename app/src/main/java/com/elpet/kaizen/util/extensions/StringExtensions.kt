package com.elpet.kaizen.util.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context


fun String.copyToClipboard(context: Context) {
    val clipboard: ClipboardManager? = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText("Copy", this)
    clipboard?.setPrimaryClip(clip)
}
