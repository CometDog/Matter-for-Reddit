package me.jdowns.matter.utils

import android.graphics.Bitmap
import android.util.LruCache

/** TODO: Seek a better way to manage caching bitmaps */
class BitmapLruCache(maxMemory: Long = (Runtime.getRuntime().maxMemory() / 1024) / 2) :
    LruCache<String, Bitmap>(maxMemory.toInt()) {
    override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
}