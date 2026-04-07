package com.gas.flexapp.ui

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.gas.flexapp.BuildConfig
import com.gas.flexapp.MainActivity
import com.gas.flexapp.R
import com.gas.flexapp.adapters.VersionAdapter
import com.gas.flexapp.databinding.ActivityApplicationListBinding
import com.gas.flexapp.viewmodels.FlexAppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class ApplicationListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplicationListBinding
    private val flexAppViewModel: FlexAppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityApplicationListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val accountManager = AccountManager.get(this)
        val accountType = getString(R.string.account_type)

        val versionAdapter = VersionAdapter {
            val accounts = accountManager.getAccountsByType(accountType)
            val versionKey = getString(R.string.version_id)
            val filteredAccount = accounts.firstOrNull { account ->
                val feature = accountManager.getUserData(account, versionKey)
                feature == it.id.toString()
            }

            if (filteredAccount != null) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.putExtra(versionKey, it.id)
                startActivity(intent)
                finish()
            } else {
                val account = accounts[0]
                accountManager.setUserData(account, versionKey, it.id.toString())
                val intent = Intent(applicationContext, LogInActivity::class.java)
                intent.putExtra(versionKey, it.id)
                startActivity(intent)
                finish()
            }
        }

        binding.rvApplicationList.adapter = versionAdapter
        binding.rvApplicationList.addItemDecoration(
            DividerItemDecoration(
                binding.rvApplicationList.context,
                DividerItemDecoration.VERTICAL
            )
        )

        flexAppViewModel.onApplicationList.observe(this) {
            binding.loading.visibility = View.GONE
            if (it.isEmpty()) {
                binding.tvEmptyList.visibility = View.VISIBLE
            } else {
                binding.tvEmptyList.visibility = View.GONE
                /*if (it.size == 1) {

                } else {

                }*/
                versionAdapter.submitList(it)
                binding.toolbar.subtitle = getString(R.string.available_applications)
            }
        }

        lifecycleScope.launch {
            val dbFile = getDatabasePath(BuildConfig.DATABASE_NAME)
            flexAppViewModel.downloadAppStore(dbFile.path, {
                flexAppViewModel.getApplicationList()
            }, {
                binding.loading.visibility = View.GONE
                flexAppViewModel.getApplicationList()
                Toast.makeText(this@ApplicationListActivity, it, Toast.LENGTH_LONG).show()
            })
        }
    }
}