package com.gas.components.media

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Base64
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gas.components.Element
import com.gas.components.DataChangedListener
import com.gas.components.adapters.ImageSelectorAdapter
import com.gas.model.ComponentTypes
import com.gas.model.media.FileTypes
import com.gas.model.media.ImageSelectorModel
import java.io.IOException

class ImageSelectorComponent(
    private val imageSelectorModel: ImageSelectorModel
): ImageSelectorElement {
    override val type: ComponentTypes get() = ComponentTypes.IMAGE_SELECTOR
    override val model: ImageSelectorModel get() = imageSelectorModel
    override lateinit var view: LinearLayout
    override val events: MutableMap<String, Element<*, *>> get() = mutableMapOf()
    override var onDataChangedListener: DataChangedListener? = null
    private var listener: ImageSelectorListener? = null
    private lateinit var imageSelectorAdapter: ImageSelectorAdapter
    private lateinit var contentResolver: ContentResolver

    override fun build(context: Context): Element<ImageSelectorModel, LinearLayout> {
        contentResolver = context.contentResolver
        view = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER

            val recyclerview = RecyclerView(context).also {
                it.id = View.generateViewId()
                it.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                imageSelectorAdapter = ImageSelectorAdapter(mutableListOf(), model) { uri ->
                    listener?.onTapImage(uri)
                }

                it.adapter = imageSelectorAdapter
            }

            addView(recyclerview)

            val buttonSelector = Button(context).also {
                it.id = View.generateViewId()
                it.isAllCaps = false
                it.text = model.text
                it.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                it.setOnClickListener {
                    listener?.launchGallery()
                }
            }

            addView(buttonSelector)
        }

        return this
    }

    override fun reset() {
        model.value = null
        model.values.clear()
        imageSelectorAdapter.clear()
    }

    override fun getFieldName(): String {
        return model.fieldName
    }

    override fun setData(value: Any) {

    }

    override fun getData(): Any? {
        return when (model.sendAs) {
            FileTypes.SINGLE -> {
                model.value
            }
            FileTypes.MULTIPLE -> {
                model.values
            }
        }
    }

    override fun setOnDataChanged(listener: DataChangedListener) {

    }

    override fun setListener(listener: ImageSelectorListener) {
        this.listener = listener
    }

    override fun onSelected(uri: Uri?) {
        uri?.let {
            when (model.sendAs) {
                FileTypes.SINGLE -> {
                    model.value = uriToBase64(it, contentResolver)
                    imageSelectorAdapter.addImage(it)
                }
                FileTypes.MULTIPLE -> {
                    val content = uriToBase64(it, contentResolver)
                    model.values.add(content)
                    imageSelectorAdapter.addImage(it)
                }
            }
        }
    }

    private fun uriToBase64(uri: Uri, contentResolver: ContentResolver): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                Base64.encodeToString(bytes, Base64.DEFAULT)
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}