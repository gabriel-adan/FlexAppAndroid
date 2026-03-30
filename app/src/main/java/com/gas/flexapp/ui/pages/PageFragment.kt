package com.gas.flexapp.ui.pages

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.gas.components.ComponentBuilder
import com.gas.components.media.ImageSelectorElement
import com.gas.components.media.ImageSelectorListener
import com.gas.components.media.ImageViewerDialogFragment
import com.gas.components.screens.ScreenElement
import com.gas.flexapp.ui.lists.ExpandableFragment
import com.gas.flexapp.ui.forms.FormFragment
import com.gas.flexapp.ui.lists.ListFragment
import com.gas.flexapp.viewmodels.ComponentViewModel
import com.gas.model.ComponentTypes
import com.gas.model.DataTypes
import com.gas.model.buttons.ButtonModel
import com.gas.model.media.ImageSelectorModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PageFragment : Fragment() {

    private val componentViewModel: ComponentViewModel by activityViewModels()
    private var _view: View? = null
    private val mView get() = _view!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        val navController = findNavController()

        val versionId = componentViewModel.onVersionSelected.value!!

        lateinit var viewComponent: ScreenElement
        if (_view == null) {
            val viewKey = arguments?.getString("viewKey")
            val contentViewModel = componentViewModel.getContentViewModel(viewKey, versionId)!!
            viewComponent = ComponentBuilder.buildView(contentViewModel.content, context) as ScreenElement

            viewComponent.model.components.forEach { element ->
                when (element.model) {
                    is ButtonModel -> {
                        val buttonModel = element.model as ButtonModel
                        buttonModel.navigation?.also {
                            when (buttonModel.action) {
                                "navigation" -> {
                                    val button = element.view as Button
                                    button.setOnClickListener { _ ->
                                        val viewDestination = componentViewModel.getContentViewModel(it.viewKey, versionId)!!
                                        val type = when (ComponentTypes.valueOf(viewDestination.type.uppercase())) {
                                            ComponentTypes.VIEW_PAGE -> {
                                                PageFragment::class.java
                                            }
                                            ComponentTypes.FORM -> {
                                                FormFragment::class.java
                                            }
                                            ComponentTypes.LIST -> {
                                                ListFragment::class.java
                                            }
                                            ComponentTypes.EXPANDABLE_LIST -> {
                                                ExpandableFragment::class.java
                                            } else -> TODO("The component type ${viewDestination.type} not yet implemented.")
                                        }

                                        val destination = navController.navigatorProvider.getNavigator(
                                            FragmentNavigator::class.java)
                                            .createDestination().apply {
                                                id = it.destinationId
                                                label = it.title
                                                setClassName(ComponentName(context, type).className)
                                            }
                                        navController.graph.addDestination(destination)

                                        val destinationId = destination.id
                                        val navOptions = NavOptions.Builder()
                                            .setPopUpTo(destinationId, inclusive = true, saveState = true)
                                            .build()
                                        val args = bundleOf()
                                        args.putString("viewKey", it.viewKey)
                                        it.paramValues.forEach { paramValueModel ->
                                            when (paramValueModel.type) {
                                                DataTypes.INT -> {

                                                }
                                                DataTypes.STRING -> {

                                                }
                                                DataTypes.DECIMAL -> {

                                                }
                                                DataTypes.FLOAT -> {

                                                }
                                                DataTypes.DATE -> {

                                                }
                                                DataTypes.TIME -> {

                                                }
                                                DataTypes.DATETIME -> {

                                                }
                                                DataTypes.BOOLEAN -> {

                                                }
                                            }
                                        }

                                        navController.navigate(destinationId, args, navOptions)
                                    }
                                }
                            }
                        }
                    }
                    is ImageSelectorModel -> {
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
                    }
                }
            }

            _view = viewComponent.view
        }

        return mView
    }
}