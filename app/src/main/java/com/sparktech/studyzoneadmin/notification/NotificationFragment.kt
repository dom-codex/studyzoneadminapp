package com.sparktech.studyzoneadmin.notification

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.NotificationMainLayoutBinding
import com.sparktech.studyzoneadmin.databinding.SendNotificationDialogFormBinding
import com.sparktech.studyzoneadmin.helpers.RcvScrollHandler
import com.sparktech.studyzoneadmin.main_menu.SwipeController
import com.sparktech.studyzoneadmin.models.Notification

class NotificationFragment : Fragment() {
    private lateinit var binding: NotificationMainLayoutBinding
    private lateinit var vm: NotificationViewModel
    private lateinit var adapter: NotificationAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.notification_main_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val fac = NotificationViewModelFactory(requireActivity().application, admin!!)
        vm = ViewModelProvider(requireActivity(), fac).get(NotificationViewModel::class.java)
        adapter = NotificationAdapter()
        binding.notificationsRcv.adapter = adapter
        binding.notificationsRcv.addOnScrollListener(
            RcvScrollHandler(
                binding.notificationsRcv,
                getLoadingState,
                getListSize,
                setLoaderItem,
                fetchMoreNotification
            )
        )
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(binding.notificationsRcv)
        binding.announceBtn.visibility = View.GONE
        setUpObservers()
    }

    //scroll lambdas
    private val getLoadingState: () -> Boolean = {
        vm.loading.value!!
    }
    private val getListSize: () -> Int = {
        vm.notifications.size
    }
    private val setLoaderItem: () -> Unit = {
        val loaderItem =
            Notification("", "", "", "", true)
        vm.notifications.add(loaderItem)
        adapter.notifyItemInserted(vm.notifications.size - 1)

    }
    private val fetchMoreNotification = {
        vm.fetchNotifications()
    }

    private fun setUpObservers() {
        vm.loading.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.notificationLoader.visibility = View.GONE
                    if (vm.notifications.size >0) {
                        binding.noNotification.visibility = View.GONE
                        binding.notificationsRcv.visibility = View.VISIBLE
                        adapter.submitList(vm.notifications)
                    }else{
                        binding.noNotification.visibility = View.VISIBLE
                    }
                } else {
                    if (vm.notifications.size == 0) {
                        binding.notificationLoader.visibility = View.VISIBLE
                        binding.noNotification.visibility = View.GONE
                    }
                }
            }
        })
        vm.isDeleting.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (it) {
                    binding.notificationDeleteLayout.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                } else {
                    binding.notificationDeleteLayout.visibility = View.VISIBLE
                }
            }
        })
    }

    private val swipeController = object : SwipeController() {
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (ItemTouchHelper.LEFT == direction) {
                AlertDialog.Builder(requireContext()).setTitle("Confirmation")
                    .setMessage("Are you sure you want to delete this notification")
                    .setNegativeButton("NO", null)
                    .setPositiveButton("YES"
                    ) { p0, p1 ->
                        val index = viewHolder.adapterPosition
                        p0.dismiss()
                        vm.deleteNotification(index) { err, msg ->
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.show()
            }
        }
    }
}