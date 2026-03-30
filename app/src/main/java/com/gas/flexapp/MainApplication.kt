package com.gas.flexapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.io.FileOutputStream

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            val dbFile = getDatabasePath(BuildConfig.DATABASE_NAME)
            if (!dbFile.exists()) {
                val input = assets.open(BuildConfig.DATABASE_NAME)
                val outFileName = "${applicationInfo.dataDir}/databases/${BuildConfig.DATABASE_NAME}"
                val output = FileOutputStream(outFileName)

                output.use { fileOut ->
                    input.copyTo(fileOut)
                }

                output.flush()
                output.close()
                input.close()
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }
}