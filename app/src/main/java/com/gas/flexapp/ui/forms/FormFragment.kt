package com.gas.flexapp.ui.forms

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gas.components.ComponentBuilder
import com.gas.components.DataChangedListener
import com.gas.components.forms.FormComponent
import com.gas.components.forms.FormListener
import com.gas.components.media.ImageSelectorElement
import com.gas.components.media.ImageSelectorListener
import com.gas.components.media.ImageViewerDialogFragment
import com.gas.components.options.SelectElement
import com.gas.flexapp.viewmodels.FormViewModel
import com.gas.model.ComponentTypes
import com.gas.model.DataTypes
import com.gas.model.selects.SelectModel
import com.gas.model.sources.DataSource
import com.gas.model.sources.DataSourceTypes
import com.gas.model.sources.ParamFormats
import com.gas.model.sources.RemoteDataSource
import com.gas.model.sources.ResponseTypes
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FormFragment : Fragment() {

    private val formViewModel: FormViewModel by viewModels()
    private var _view: View? = null
    private val mView get() = _view!!

    companion object {
        private const val ADAPTER_POSITION = "_adapter_-_position_"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()

        val viewKey = arguments?.getString("viewKey")

        val contentViewModel = formViewModel.getContentViewModel(viewKey)
        val formComponent = ComponentBuilder.buildView(contentViewModel.content, context) as FormComponent
        _view = formComponent.view

        val formModel = formComponent.model
        if (formModel.dataSource != null) {
            val dataSource = formModel.dataSource!!
            when (dataSource.type) {
                DataSourceTypes.NONE -> {}
                DataSourceTypes.REMOTE -> {
                    formComponent.showLoading()
                    lifecycleScope.launch {
                        formViewModel.requestFromRemoteSource(dataSource)
                    }
                }
                DataSourceTypes.NAVIGATION -> {

                }
                DataSourceTypes.DATABASE -> {

                }
            }
        } else {
            if (formModel.editSource != null) {
                when (formModel.editSource!!.type) {
                    DataSourceTypes.NONE -> { formComponent.hideLoading() }
                    DataSourceTypes.REMOTE -> {
                        val remoteDataSource = formModel.editSource as RemoteDataSource
                        lateinit var dataSource: DataSource
                        when (remoteDataSource.paramsFormat) {
                            ParamFormats.PATH -> {
                                var params = ""
                                remoteDataSource.parameters.forEach {
                                    val paramValue = when (it.dataType) {
                                        DataTypes.INT -> {
                                            arguments?.getInt(it.sourceName, 0)
                                        } else -> {
                                            arguments?.getString(it.sourceName, null)
                                        }
                                    }
                                    params += "/$paramValue"
                                }
                                dataSource = RemoteDataSource(
                                    remoteDataSource.type,
                                    remoteDataSource.responseType,
                                    url = "${remoteDataSource.url}$params",
                                    remoteDataSource.requestType,
                                    remoteDataSource.paramsFormat,
                                    remoteDataSource.parameters
                                )
                            }
                            ParamFormats.QUERY -> {
                                var params = "?"
                                remoteDataSource.parameters.forEach {
                                    val paramValue = arguments?.getString(it.sourceName, "")
                                    params += "${it.targetName}=$paramValue&"
                                }
                                params += "**"
                                params = params.replace("&**", "")
                                dataSource = RemoteDataSource(
                                    remoteDataSource.type,
                                    remoteDataSource.responseType,
                                    url = "${remoteDataSource.url}$params",
                                    remoteDataSource.requestType,
                                    remoteDataSource.paramsFormat,
                                    remoteDataSource.parameters
                                )
                            }
                        }
                        formComponent.showLoading()
                        lifecycleScope.launch {
                            formViewModel.requestEditFromRemoteSource(dataSource)
                        }
                    }
                    DataSourceTypes.NAVIGATION -> {}
                    DataSourceTypes.DATABASE -> {}
                }
            }
        }

        formModel.components.forEach { element ->
            when (element.type) {
                ComponentTypes.INPUT,
                ComponentTypes.DATE_INPUT,
                ComponentTypes.TIME_INPUT,
                ComponentTypes.LABELED_INPUT,
                ComponentTypes.CHECKBOX,
                ComponentTypes.SELECT -> {
                    element.setOnDataChanged(object : DataChangedListener {
                        override fun setDataValue(fieldName: String, value: Any?, dataType: DataTypes) {
                            try {
                                formViewModel.setDataValue(fieldName, dataType, value)
                            } catch (_ : Exception) {
                                element.reset()
                            }
                        }
                    })

                    formViewModel.data.observe(viewLifecycleOwner) { json ->
                        val fieldName = element.getFieldName()
                        if (json.has(fieldName)) {
                            val value = json.get(fieldName)
                            element.setData(value)
                        }
                    }
                }
                ComponentTypes.IMAGE_SELECTOR -> {
                    val imageSelectorComponent = element as ImageSelectorElement
                    val galleryLauncher = registerForActivityResult(
                        ActivityResultContracts.GetContent()) { uri ->
                        if (uri != null) {
                            imageSelectorComponent.onSelected(uri)
                        }
                    }
                    val listener = object: ImageSelectorListener {
                        override fun launchGallery() {
                            galleryLauncher.launch("image/*")
                        }

                        override fun onTapImage(uri: Uri) {
                            val imageViewerDialogFragment = ImageViewerDialogFragment(uri)
                            imageViewerDialogFragment.show(parentFragmentManager, ImageViewerDialogFragment.Companion.TAG)
                        }
                    }
                    imageSelectorComponent.setListener(listener)
                } else -> {}
            }
        }

        formViewModel.fromRemoteSource.observe(viewLifecycleOwner) { source ->
            formModel.components.forEach { element ->
                when (element.type) {
                    ComponentTypes.SELECT -> {
                        val selectModel = element.model as SelectModel
                        selectModel.optionSource?.also { os ->
                            when (os.type) {
                                DataSourceTypes.REMOTE -> {
                                    formModel.dataSource?.also { fds ->
                                        when (fds.type) {
                                            DataSourceTypes.REMOTE -> {
                                                val remoteSource = fds as RemoteDataSource
                                                val selectElement = element as SelectElement
                                                when (remoteSource.responseType) {
                                                    ResponseTypes.OBJECT -> {
                                                        val jsonObject = source as JsonObject
                                                        if (jsonObject.has(os.sourceKey)) {
                                                            val jsonArray = source.get(os.sourceKey).asJsonArray
                                                            val options = mutableListOf<JsonObject>()
                                                            jsonArray.forEach {
                                                                options.add(it as JsonObject)
                                                            }
                                                            selectElement.loadOptions(options)
                                                        }
                                                    }
                                                    ResponseTypes.LIST -> {
                                                        if (source.isJsonArray) {
                                                            val jsonArray = source as JsonArray
                                                            val options = mutableListOf<JsonObject>()
                                                            jsonArray.forEach {
                                                                options.add(it as JsonObject)
                                                            }
                                                            selectElement.loadOptions(options)
                                                        }
                                                    }
                                                }

                                                if (formViewModel.data.value?.has(selectModel.fieldName) == true) {
                                                    val value = formViewModel.data.value?.get(selectModel.fieldName)
                                                    if (value != null) {
                                                        selectElement.setData(value)
                                                    }
                                                }
                                            } else -> {}
                                        }
                                    }
                                } else -> {}
                            }
                        }
                    } else -> {}
                }
            }

            if (formModel.editSource == null) {
                formComponent.hideLoading()
            } else {
                when (formModel.editSource!!.type) {
                    DataSourceTypes.NONE -> { formComponent.hideLoading() }
                    DataSourceTypes.REMOTE -> {
                        val remoteDataSource = formModel.editSource as RemoteDataSource
                        lateinit var dataSource: DataSource
                        when (remoteDataSource.paramsFormat) {
                            ParamFormats.PATH -> {
                                var params = ""
                                remoteDataSource.parameters.forEach {
                                    val paramValue = arguments?.getString(it.sourceName, "")
                                    params += "/$paramValue"
                                }
                                dataSource = RemoteDataSource(
                                    remoteDataSource.type,
                                    remoteDataSource.responseType,
                                    url = "${remoteDataSource.url}$params",
                                    remoteDataSource.requestType,
                                    remoteDataSource.paramsFormat,
                                    remoteDataSource.parameters
                                )
                            }
                            ParamFormats.QUERY -> {
                                var params = "?"
                                remoteDataSource.parameters.forEach {
                                    val paramValue = arguments?.getString(it.sourceName, "")
                                    params += "${it.targetName}=$paramValue&"
                                }
                                params += "**"
                                params = params.replace("&**", "")
                                dataSource = RemoteDataSource(
                                    remoteDataSource.type,
                                    remoteDataSource.responseType,
                                    url = "${remoteDataSource.url}$params",
                                    remoteDataSource.requestType,
                                    remoteDataSource.paramsFormat,
                                    remoteDataSource.parameters
                                )
                            }
                        }
                        lifecycleScope.launch {
                            formViewModel.requestEditFromRemoteSource(dataSource)
                        }
                    }
                    DataSourceTypes.NAVIGATION -> {}
                    DataSourceTypes.DATABASE -> {}
                }
            }
        }

        formViewModel.editRemoteSource.observe(viewLifecycleOwner) {
            formComponent.hideLoading()
            formModel.components.forEach { element ->
                val fieldName = element.getFieldName()
                if (it.has(fieldName)) {
                    if (element is SelectElement) {
                        element.setEditValueCondition(it.get(fieldName))
                    } else {
                        element.setData(it.get(fieldName))
                    }
                }
            }
        }

        formViewModel.onSuccess.observe(viewLifecycleOwner) {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    .setTitle("Atención")
                    .setMessage("Datos enviados exitosamente")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar") { _, _ ->
                        if (formModel.updateProperties.isNotEmpty()) {
                            val selectedPosition = arguments?.getInt(ADAPTER_POSITION, -1)!!
                            val bundle = bundleOf(
                                ADAPTER_POSITION to selectedPosition
                            )

                            formModel.updateProperties.forEach {
                                val element = formModel.components.find {  element ->
                                    element.getFieldName() == it
                                }
                                if (element != null) {
                                    when (element.type) {
                                        ComponentTypes.INPUT,
                                        ComponentTypes.DATE_INPUT,
                                        ComponentTypes.TIME_INPUT,
                                        ComponentTypes.LABELED_INPUT,
                                        ComponentTypes.CHECKBOX,
                                        ComponentTypes.SELECT -> {
                                            val fieldName = element.getFieldName()
                                            val value = element.getData()
                                            if (value == null) {
                                                bundle.putString(fieldName, null)
                                            } else {
                                                bundle.putString(fieldName, value.toString())
                                            }
                                        } else -> {}
                                    }
                                }
                            }

                            setFragmentResult("onUpdatedItem", bundle)
                        }

                        if (formModel.appendToPrevList) {
                            setFragmentResult("onItemAdded", bundleOf())
                        }

                        if (formModel.navBack) {
                            val navController = findNavController()
                            if (navController.currentBackStackEntry?.destination?.id != null) {
                                navController.popBackStack(
                                    navController.currentBackStackEntry?.destination?.id!!,
                                    true
                                )
                            } else {
                                navController.popBackStack()
                            }
                        }

                        formComponent.reset()
                    }
                builder.create().show()
            }
        }

        formViewModel.onFail.observe(viewLifecycleOwner) {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            formComponent.hideLoading()
        }

        val formListener = object: FormListener {
            override fun onConfirm(url: String, data: JsonObject) {
                activity?.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                formModel.navigation.expectedArguments.forEach {
                    if (it.includeInBody) {
                        if (!data.has(it.name)) {
                            when (it.dataType) {
                                DataTypes.INT -> {
                                    val paramValue = arguments?.getInt(it.name)
                                    if (it.sendAs.isNullOrEmpty()) {
                                        data.addProperty(it.name, paramValue)
                                    } else {
                                        data.addProperty(it.sendAs, paramValue)
                                    }
                                } else -> {
                                val paramValue = arguments?.getString(it.name)
                                if (it.sendAs.isNullOrEmpty()) {
                                    data.addProperty(it.name, paramValue)
                                } else {
                                    data.addProperty(it.sendAs, paramValue)
                                }
                            }
                            }
                        }
                    }
                }
                lifecycleScope.launch {
                    formViewModel.send(url, data)
                }
            }

            override fun onCancel(data: JsonObject) {

            }
        }
        formComponent.setListener(listener = formListener)

        return mView
    }
}