package me.jdowns.matter.helpers

import android.graphics.Bitmap
import android.util.LruCache


class BitmapLruCache(maxMemory: Long = (Runtime.getRuntime().maxMemory() / 1024) / 2) :
    LruCache<String, Bitmap>(maxMemory.toInt()) {
    override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
}