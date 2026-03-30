package com.gas.components.media

import android.net.Uri
import android.widget.LinearLayout
import com.gas.components.Element
import com.gas.model.media.ImageSelectorModel

interface ImageSelectorElement : Element<ImageSelectorModel, LinearLayout> {

    fun setListener(listener: ImageSelectorListener)

    fun onSelected(uri: Uri?)
}