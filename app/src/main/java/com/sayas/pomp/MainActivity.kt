package com.sayas.pomp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.huxq17.download.core.DownloadInfo

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Downloader.builder(this, object : Downloader.DownloadObserver {
            override fun onProgress(progress: Int, info: DownloadInfo) {

            }

            override fun onSuccess(info: DownloadInfo) {
                Toast.makeText(
                    this@MainActivity,
                    "download complete: " + info.name,
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onFailure(info: DownloadInfo) {

            }

        }).download("https://bit.ly/2R5y1BI", "Test")
    }
}
