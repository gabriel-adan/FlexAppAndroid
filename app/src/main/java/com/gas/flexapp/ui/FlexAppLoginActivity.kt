package com.gas.flexapp.ui

import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.toColorInt
import com.gas.flexapp.viewmodels.FlexAppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class FlexAppLoginActivity : AppCompatActivity() {

    private val flexAppViewModel: FlexAppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val progressBar = ProgressBar(this).also {
            it.id = View.generateViewId()
            val layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            it.setPadding(450, 450, 450, 450)
            it.layoutParams = layoutParams
            it.setBackgroundColor("#F2F2F2F2".toColorInt())
            it.visibility = View.GONE
            it.tag = "progressBar"
        }

        val linearLayout = LinearLayout(this).apply {
            id = View.generateViewId()
            orientation = LinearLayout.VERTICAL
            val layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(50, 0, 50, 0)
            this.layoutParams = layoutParams
        }

        val textView = TextView(this).also {
            it.text = "Inicio de sesion a Flex App"
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 100, 0, 100)
            it.layoutParams = layoutParams
            it.textSize = 20f
            it.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            it.setTypeface(null, Typeface.BOLD)
        }
        linearLayout.addView(textView)

        val textViewUserName = TextView(this).also {
            it.text = "Nombre de usuario"
        }
        linearLayout.addView(textViewUserName)

        val editTextUserName = EditText(this)
        linearLayout.addView(editTextUserName)

        val textViewPassword = TextView(this).also {
            it.text = "Contraseña"
        }
        linearLayout.addView(textViewPassword)

        val editTextPassword = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        linearLayout.addView(editTextPassword)

        val button = Button(this).also {
            it.text = "Ingresar"
            it.isAllCaps = false
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 100, 0, 0)
            it.layoutParams = layoutParams
            it.setOnClickListener {
                progressBar.visibility = View.VISIBLE
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                flexAppViewModel.logIn(editTextUserName.text.toString(),
                    editTextPassword.text.toString(), { bundle ->
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    intent.putExtras(bundle)
                    setResult(RESULT_OK, intent)
                    finish()
                }, { msg ->
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                })
            }
        }
        linearLayout.addView(button)

        val constraintLayout = ConstraintLayout(this).apply {
            id = View.generateViewId()
            this.layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(linearLayout)
            addView(progressBar)

            val constraintSet = ConstraintSet()
            constraintSet.clone(this)

            constraintSet.centerHorizontally(linearLayout.id, ConstraintSet.PARENT_ID)
            constraintSet.centerVertically(linearLayout.id, ConstraintSet.PARENT_ID)

            constraintSet.centerHorizontally(progressBar.id, ConstraintSet.PARENT_ID)
            constraintSet.centerVertically(progressBar.id, ConstraintSet.PARENT_ID)

            constraintSet.applyTo(this)
        }

        setContentView(constraintLayout)
    }
}