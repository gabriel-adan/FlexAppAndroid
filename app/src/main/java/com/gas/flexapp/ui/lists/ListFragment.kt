package com.gas.flexapp.ui.lists

import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.gas.components.ComponentBuilder
import com.gas.components.lists.ItemListClickListener
import com.gas.components.lists.ListComponent
import com.gas.flexapp.ui.forms.FormFragment
import com.gas.flexapp.ui.pages.PageFragment
import com.gas.flexapp.viewmodels.ListViewModel
import com.gas.model.ComponentTypes
import com.gas.model.DataTypes
import com.gas.model.lists.ListModel
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFragment : Fragment() {

    private val listViewModel: ListViewModel by activityViewModels()
    private var _view: View? = null
    private val mView get() = _view!!
    private lateinit var listComponent: ListComponent

    companion object {
        private const val ADAPTER_POSITION = "_adapter_-_position_"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        val navController = findNavController()

        if (_view == null) {
            val viewKey = arguments?.getString("viewKey")
            val contentViewModel = listViewModel.getContentViewModel(viewKey)
            listComponent = ComponentBuilder.buildView(contentViewModel.content, context) as ListComponent
            _view = listComponent.view

            listComponent.setOnItemListClickListener(object : ItemListClickListener<ListModel> {
                override fun onClick(position: Int, item: JsonObject, itemList: ListModel) {
                    when (itemList.action) {
                        "navigation" -> {
                            itemList.navigation?.also {
                                val viewDetail = listViewModel.getContentViewModel(it.viewKey)
                                val type = when (ComponentTypes.valueOf(viewDetail.type.uppercase())) {
                                    ComponentTypes.VIEW_PAGE -> {
                                        PageFragment::class.java
                                    }
                                    ComponentTypes.FORM -> {
                                        FormFragment::class.java
                                    }
                                    ComponentTypes.LIST -> {
                                        ListFragment::class.java
                                    } else -> TODO("The component type ${viewDetail.type} not yet implemented.")
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
                                val args = bundleOf(
                                    ADAPTER_POSITION to position
                                )
                                args.putString("viewKey", it.viewKey)
                                it.paramValues.forEach { paramValueModel ->
                                    when (paramValueModel.type) {
                                        DataTypes.INT -> {
                                            val value = item.get(paramValueModel.name).asInt
                                            args.putInt(paramValueModel.name, value)
                                        }
                                        DataTypes.STRING -> {
                                            val value = item.get(paramValueModel.name).asString
                                            args.putString(paramValueModel.name, value)
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
                                listViewModel.setSelectedItem(item)
                                navController.navigate(destinationId, args, navOptions)
                            }
                        }
                    }
                }
            })

            lifecycleScope.launch {
                listViewModel.getItems(listComponent.sourceUri)
            }

            setFragmentResultListener("onUpdatedItem") { _, bundle ->
                val position = bundle.getInt(ADAPTER_POSITION)
                if (position > -1) {
                    val item = JsonObject()
                    bundle.keySet().forEach {
                        if (it != ADAPTER_POSITION) {
                            item.addProperty(it, bundle.getString(it))
                        }
                    }
                    listComponent.updateItem(position, item)
                }
            }

            setFragmentResultListener("onItemAdded") { _, _ ->
                lifecycleScope.launch {
                    listViewModel.getItems(listComponent.sourceUri)
                }
            }
        }

        lifecycleScope.launch {
            listViewModel.items.observe(viewLifecycleOwner) {
                val state = lifecycle.currentState
                if (state == Lifecycle.State.RESUMED) {
                    listComponent.loadItems(it)
                }
            }
        }

        return mView
    }
}