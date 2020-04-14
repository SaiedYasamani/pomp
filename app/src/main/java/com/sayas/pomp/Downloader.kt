package com.sayas.pomp

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.ContextCompat
import com.huxq17.download.Pump
import com.huxq17.download.core.DownloadInfo
import com.huxq17.download.core.DownloadListener
import java.io.File

class Downloader private constructor(
    private val context: Context,
    private val observer: DownloadObserver
) {

    companion object {
        fun builder(context: Context, observer: DownloadObserver): Downloader =
            Downloader(context, observer)
    }

    fun download(url: String, fileName: String) {
        Pump.newRequest(url, getFilePath(fileName))
            .threadNum(5)
            .submit()
        observe()
        startService(0)
    }

    private fun startService(progress: Int) {
        val intent = Intent(context, DownloaderService::class.java)
        intent.action = DownloaderService.DOWNLOAD_ACTION
        intent.putExtra(DownloaderService.PROGRESS, progress)
        context.startService(intent)
    }

    private fun observe() {
        object : DownloadListener() {
            override fun onProgress(progress: Int) {
                super.onProgress(progress)
                observer.onProgress(progress, downloadInfo)
                startService(progress)
            }

            override fun onSuccess() {
                super.onSuccess()
                observer.onSuccess(downloadInfo)
                val intent = Intent(context, DownloaderService::class.java)
                intent.action = DownloaderService.SUCCESS_ACTION
                context.startService(intent)
            }

            override fun onFailed() {
                super.onFailed()
                observer.onFailure(downloadInfo)
                val intent = Intent(context, DownloaderService::class.java)
                intent.action = DownloaderService.FAILURE_ACTION
                context.startService(intent)
            }
        }
    }

    private fun getFilePath(fileName: String): String = File(getRoot(), fileName).absolutePath

    private fun getRoot(): String {
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            ContextCompat.getExternalFilesDirs(context, null)[0].absolutePath
        } else {
            context.filesDir.absolutePath
        }
    }

    interface DownloadObserver {
        fun onProgress(progress: Int, info: DownloadInfo)
        fun onSuccess(info: DownloadInfo)
        fun onFailure(info: DownloadInfo)
    }
}