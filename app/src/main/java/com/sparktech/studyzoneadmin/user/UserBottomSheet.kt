package com.sparktech.studyzoneadmin.user

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.SendNotificationDialogFormBinding
import com.sparktech.studyzoneadmin.databinding.UserBottomSheetLayoutBinding
import com.sparktech.studyzoneadmin.request_models.PostNotification
import com.sparktech.studyzoneadmin.request_models.ToggleUserStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class UserBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: UserBottomSheetLayoutBinding
    val job = Job()
    val networkScope = CoroutineScope(Dispatchers.IO + job)
    private lateinit var vm: UsersViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.user_bottom_sheet_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = requireArguments()
        val userHash = bundle.getString("user", "")
        val isBlocked = bundle.getBoolean("isBlocked", false)
        initView(userHash, isBlocked)
    }

    private fun initView(userHash: String, isBlocked: Boolean) {
        if (isBlocked) {
            binding.statusImg.setImageResource(R.drawable.ic_baseline_assignment_turned_in_24)
            binding.statusImg.imageTintList =
                ColorStateList.valueOf(resources.getColor(R.color.green, null))
            binding.statusText.text = "Unsuspend"
        } else {
            binding.statusImg.setImageResource(R.drawable.ic_baseline_assignment_turned_in_24)
            binding.statusImg.imageTintList =
                ColorStateList.valueOf(resources.getColor(R.color.green, null))
            binding.statusText.text = resources.getString(R.string.suspend_text)
        }
        binding.sendNotification.setOnClickListener {
            showNewNotificationDialog(userHash, isBlocked)
        }
        binding.changeUserStatus.setOnClickListener {
            showToggleConfirmationDialog(userHash, isBlocked)
        }
    }

    private fun showToggleConfirmationDialog(userHash: String, isBlocked: Boolean) {
        var message = ""
        if (isBlocked) {
            message = "Are you sure you want to UnSuspend this account."
        } else {
            message = "Are you sure you want to Suspend this account."
        }
        AlertDialog.Builder(requireContext()).setTitle("Confirmation!").setMessage(
            message
        ).setNegativeButton("NO", null).setPositiveButton("YES") { dialog, _ ->
            val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
            val admin = sp.getString("adminId", "")
            val body = ToggleUserStatus(admin!!, userHash, isBlocked)
            vm.toggleUserStatus(body)
            dialog.dismiss()
        }
    }

    private fun showNewNotificationDialog(userHash: String, isBlocked: Boolean) {
        val alertDialog = AlertDialog.Builder(requireContext())
        val dialog = alertDialog.create()
        val inflater = LayoutInflater.from(dialog.context)
        val dialogBinding =
            SendNotificationDialogFormBinding.inflate(inflater, dialog.listView, false)
        dialog.setView(dialogBinding.root)
        dialog.show()
        dialogBinding.postBtn.setOnClickListener {
            val message = dialogBinding.messageBox.text.toString()
            val subject = dialogBinding.subjectInput.text.toString()
            if (subject.isEmpty() || message.isEmpty()) {
                Toast.makeText(requireContext(), "input cannot be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (isBlocked) {
                Toast.makeText(
                    requireContext(),
                    "user account is currently suspended",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
            val user = sp.getString("adminId", "")
            val postNotification = PostNotification(user!!, message, subject, userHash)
            vm.postNotification(postNotification)
        }
    }
    companion object{
        fun getBottomSheet(userHash:String,isBlocked: Boolean):UserBottomSheet{
            val bundle = Bundle()
            bundle.putString("user",userHash)
            bundle.putBoolean("isBlocked",isBlocked)
            return UserBottomSheet().apply {
                arguments = bundle
            }
        }
    }
}