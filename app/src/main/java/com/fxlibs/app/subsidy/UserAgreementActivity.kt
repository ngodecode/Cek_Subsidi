package com.fxlibs.app.subsidy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.fxlibs.app.subsidy.data.AppDatabase
import com.fxlibs.app.subsidy.data.UserAccess
import com.fxlibs.app.subsidy.databinding.ActivityUserAgreementBinding
import com.fxlibs.subsidy.tariff.ui.SubsidyActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserAgreementActivity : AppCompatActivity() , KoinComponent {

    private val db: AppDatabase by inject()
    lateinit var binding:ActivityUserAgreementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserAgreementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.isEnabled = false
        binding.btnNext.setOnClickListener {
            allowedAgreement()
        }
        binding.wbView.loadUrl(BuildConfig.UA_URL)
        binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.btnNext.isEnabled = isChecked
        }

    }

    private fun allowedAgreement() {
        lifecycleScope.launch(Dispatchers.IO) {
            db.userAccessDao().insert(UserAccess(1, 1))
            lifecycleScope.launch {
                finish()
                startActivity(Intent(this@UserAgreementActivity, SubsidyActivity::class.java))
            }
        }
    }
}