package com.gas.components.media

import android.net.Uri

interface ImageSelectorListener {

    fun launchGallery()

    fun onTapImage(uri: Uri)
}