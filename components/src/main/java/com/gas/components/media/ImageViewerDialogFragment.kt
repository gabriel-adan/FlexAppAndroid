package com.gas.components.media

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.DialogFragment

class ImageViewerDialogFragment(
    private val uri: Uri
) : DialogFragment() {

    companion object {
        const val TAG = "ImageViewerDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val imageViewerDialog = inflater.inflate(com.gas.components.R.layout.image_viewer_dialog, container, false)
        val imageView = imageViewerDialog.findViewById<ImageView>(com.gas.components.R.id.image_viewer)
        imageView.setImageURI(uri)

        val imageButton = imageViewerDialog.findViewById<ImageButton>(com.gas.components.R.id.close_button)
        imageButton.setOnClickListener {
            dismiss()
        }
        return imageViewerDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.gas.components.R.style.FullScreenDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.also {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.window?.setLayout(width, height)
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}