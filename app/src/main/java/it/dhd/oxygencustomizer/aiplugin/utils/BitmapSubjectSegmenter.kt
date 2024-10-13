package it.dhd.oxygencustomizer.aiplugin.utils

import android.content.Context
import android.graphics.Bitmap
import dev.eren.removebg.RemoveBg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BitmapSubjectSegmenter(private val mContext: Context) {

    private val mBgRemover: RemoveBg = RemoveBg(mContext)

    private suspend fun segmentSubject(
        inputBitmap: Bitmap,
        listener: SegmentResultListener
    ) {
        mBgRemover.clearBackground(inputBitmap).collect { output ->
            listener.onSuccess(output)
        }
    }

    fun segmentSubjectFromJava(inputBitmap: Bitmap, listener: SegmentResultListener) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                segmentSubject(inputBitmap, listener)
            } catch (e: Exception) {
                listener.onFail(e)
            }
        }
    }

    interface SegmentResultListener {
        fun onSuccess(result: Bitmap?)
        fun onFail(e: Exception)
    }
}
