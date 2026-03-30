package com.gas.flexapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gas.model.ViewContentModel
import com.gas.orm.context.DbContext
import com.gas.orm.data.And
import com.gas.orm.data.Criteria
import com.gas.orm.data.DbSet
import com.gas.orm.data.Eq
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ComponentViewModel @Inject constructor(
    private val dbContext: DbContext
) : ViewModel() {

    fun getFirstContentViewModel(): ViewContentModel? {
        val viewContentModelDbSet: DbSet<ViewContentModel, String> = dbContext.registerEntityMap(
            ViewContentModel::class.java)
        return viewContentModelDbSet.all.first()
    }

    fun getContentViewModel(viewKey: String?, versionId: Int): ViewContentModel? {
        val viewContentModelDbSet: DbSet<ViewContentModel, String> = dbContext.registerEntityMap(
            ViewContentModel::class.java)
        return viewContentModelDbSet.where(arrayOf<Criteria>(
            And(
                Eq(ViewContentModel::class.java, "viewKey", viewKey),
                Eq(ViewContentModel::class.java, "versionId", versionId)
            )
        )).firstOrNull()
    }

    private val _onVersionSelected: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }

    val onVersionSelected: LiveData<Int> get() = _onVersionSelected

    fun setVersionSelected(versionId: Int) {
        _onVersionSelected.value = versionId
    }
}