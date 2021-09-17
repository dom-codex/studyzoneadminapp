package com.sparktech.studyzoneadmin.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sparktech.studyzoneadmin.Auth
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.SettingsLayoutBinding
import com.sparktech.studyzoneadmin.helpers.saveAdminLoginData
import com.sparktech.studyzoneadmin.models.Settings
import com.sparktech.studyzoneadmin.request_models.UpdateSettingsRequestBody
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength

class SettingsFragment : Fragment() {
    private lateinit var binding: SettingsLayoutBinding
    private lateinit var vm: SettingsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.settings_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setupObservers()
    }

    private fun init() {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val fac = SettingsViewModelFactory(requireActivity().application, admin!!)
        vm = ViewModelProvider(requireActivity(), fac).get(SettingsViewModel::class.java)
    }

    private fun setupObservers() {
        vm.isLoading.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    initViews(vm.settings)
                } else {
                    //show spinner
                }
            }
        })
    }

    private fun initViews(settings: List<Settings>) {
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val minWithdrawalSettings = settings.find {
            it.name == "minWithdrawal"
        }
        val maxWithdrawalSettings = settings.find {
            it.name == "maxWithdrawal"
        }
        val freeTrialSettings = settings.find {
            it.name == "freeTrialAvailable"
        }
        val referralBonusSettings = settings.find {
            it.name == "referralBonus"
        }
        binding.apply {
            maxWithdrawal.text = Editable.Factory().newEditable(maxWithdrawalSettings?.value)
            minWithdrawal.text = Editable.Factory().newEditable(minWithdrawalSettings?.value)
            referralBonus.text = Editable.Factory().newEditable(referralBonusSettings?.value)
            //pare free trial to boolean
            val allowFreeTrial = freeTrialSettings?.value.toBoolean()
            freeTrialSwitch.isChecked = allowFreeTrial
            //SET UP Listeners
            logoutBtn.setOnClickListener {
                //clear admin data
                saveAdminLoginData(
                    requireContext().getSharedPreferences(
                        "AdminDetails",
                        Context.MODE_PRIVATE
                    ), "", false, "", ""
                )
                //navigate to auth screen
                requireContext().startActivity(Intent(requireContext(), Auth::class.java))
                requireActivity().finishAfterTransition()
            }
            submitReferral.setOnClickListener {
                val amount = referralBonus.text.toString()
                if (amount == referralBonusSettings?.value!! || !amount.minLength(1) || !amount.isDigitsOnly()) {
                    Toast.makeText(requireContext(), "enter a valid value", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                val rb = UpdateSettingsRequestBody(
                    referralBonusSettings.name,
                    amount,
                    referralBonusSettings.utilsId,
                    admin!!
                )
                hideReferralSave()
                vm.updateSettings(rb) {
                    requireActivity().runOnUiThread {
                        initViews(vm.settings)
                        showReferralSave()
                        if (!it) {
                            Toast.makeText(requireContext(), "updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }
            submitMin.setOnClickListener {
                val amount = minWithdrawal.text.toString()
                if (amount == minWithdrawalSettings?.value!! || !amount.minLength(1) || !amount.isDigitsOnly()) {
                    Toast.makeText(requireContext(), "enter a valid value", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                val rb = UpdateSettingsRequestBody(
                    minWithdrawalSettings.name,
                    amount,
                    minWithdrawalSettings.utilsId,
                    admin!!
                )
                hideMinSave()
                vm.updateSettings(rb) {
                    requireActivity().runOnUiThread {
                        initViews(vm.settings)
                        showMinSave()
                        if (!it) {
                            Toast.makeText(requireContext(), "updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }
            submitMax.setOnClickListener {
                val amount = maxWithdrawal.text.toString()
                if (amount == maxWithdrawalSettings?.value!! || !amount.minLength(1) || !amount.isDigitsOnly()) {
                    Toast.makeText(requireContext(), "enter a valid value", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                val rb = UpdateSettingsRequestBody(
                    maxWithdrawalSettings.name,
                    amount,
                    maxWithdrawalSettings.utilsId,
                    admin!!
                )
                hideMaxSave()
                vm.updateSettings(rb) {
                    requireActivity().runOnUiThread {
                        initViews(vm.settings)
                        showMaxSave()
                        if (!it) {
                            Toast.makeText(requireContext(), "updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }
            freeTrialSwitch.setOnCheckedChangeListener { view, checked ->
                val rb = UpdateSettingsRequestBody(
                    freeTrialSettings?.name!!,
                    checked.toString(),
                    freeTrialSettings.utilsId,
                    admin!!
                )
                hideFreeSave()
                vm.updateSettings(rb) {
                    requireActivity().runOnUiThread {
                        initViews(vm.settings)
                        showFreeSave()
                        if (!it) {
                            Toast.makeText(requireContext(), "updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }
        }
    }

    private fun showMaxSave() {
        binding.apply {
            submitMax.visibility = View.VISIBLE
            maxLoader.visibility = View.GONE
        }
    }

    private fun hideMaxSave() {
        binding.apply {
            submitMax.visibility = View.GONE
            maxLoader.visibility = View.VISIBLE
        }
    }

    private fun showMinSave() {
        binding.apply {
            submitMin.visibility = View.VISIBLE
            minLoader.visibility = View.GONE
        }
    }

    private fun hideMinSave() {
        binding.apply {
            submitMin.visibility = View.GONE
            minLoader.visibility = View.VISIBLE
        }
    }

    private fun showReferralSave() {
        binding.apply {
            submitReferral.visibility = View.VISIBLE
            referralLoader.visibility = View.GONE
        }
    }

    private fun hideReferralSave() {
        binding.apply {
            submitReferral.visibility = View.GONE
            referralLoader.visibility = View.VISIBLE
        }
    }

    private fun showFreeSave() {
        binding.apply {
            freeTrialSwitch.visibility = View.VISIBLE
            freetrialLoader.visibility = View.GONE
        }
    }

    private fun hideFreeSave() {
        binding.apply {
            freeTrialSwitch.visibility = View.GONE
            freetrialLoader.visibility = View.VISIBLE
        }
    }
}