package com.sparktech.studyzoneadmin.annnouncement

import android.content.Context
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
import com.sparktech.studyzoneadmin.models.Announcement
import com.sparktech.studyzoneadmin.models.Notification
import com.sparktech.studyzoneadmin.notification.NotificationAdapter
import com.sparktech.studyzoneadmin.notification.NotificationViewModel
import com.sparktech.studyzoneadmin.notification.NotificationViewModelFactory

class AnnoucementFragment:Fragment() {
    private lateinit var binding:NotificationMainLayoutBinding
    private lateinit var vm: NotificationViewModel
    private lateinit var adapter: AnnouncementAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.notification_main_layout,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sp = requireContext().getSharedPreferences("AdminDetails", Context.MODE_PRIVATE)
        val admin = sp.getString("adminId", "")
        val fac = NotificationViewModelFactory(requireActivity().application, admin!!)
        vm = ViewModelProvider(requireActivity(), fac).get(NotificationViewModel::class.java)
        vm.fetchAnnouncement()
        adapter = AnnouncementAdapter()
        binding.notificationsRcv.adapter = adapter
        binding.notificationsRcv.addOnScrollListener(
            RcvScrollHandler(
                binding.notificationsRcv,
                getLoadingState,
                getListSize,
                setLoaderItem,
                fetchMoreAnnoucements
            )
        )
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(binding.notificationsRcv)
        setUpAnnouncement()
        setUpObservers()
    }

    //scroll lambdas
    private val getLoadingState: () -> Boolean = {
        vm.loadingAnnouncement.value!!
    }
    private val getListSize: () -> Int = {
        vm.announcements.size
    }
    private val setLoaderItem: () -> Unit = {
        val loaderItem =
            Announcement("", "", "", "", true)
        vm.announcements.add(loaderItem)
        adapter.notifyItemInserted(vm.announcements.size - 1)

    }
    private val fetchMoreAnnoucements= {
        vm.fetchNotifications()
    }
    private fun setUpObservers() {
        vm.loadingAnnouncement.observe(viewLifecycleOwner, { loading ->
            loading?.let {
                if (!it) {
                    binding.notificationLoader.visibility = View.GONE
                    if (vm.announcements.size >0) {
                        binding.noNotification.visibility = View.GONE
                        binding.notificationsRcv.visibility = View.VISIBLE
                        adapter.submitList(vm.announcements)
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
        vm.deletingAnnouncement.observe(viewLifecycleOwner, { loading ->
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
    private fun setUpAnnouncement(){
        binding.announceBtn.setOnClickListener {
            if(vm.sending.value!!){
                Toast.makeText(requireContext(),"still sending announcement, please wait", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val alertDialog = AlertDialog.Builder(requireContext())
            val dialog = alertDialog.create()
            val inflater = LayoutInflater.from(alertDialog.context)
            val dialogBinding = SendNotificationDialogFormBinding.inflate(inflater,dialog.listView,false)
            dialog.setView(dialogBinding.root)
            dialogBinding.sendNoteLabel.text = "New Announcement"
            //initialise routes later
            dialogBinding.postBtn.setOnClickListener {
                //get message from input
                val message = dialogBinding.messageBox.text.toString()
                //get subject from input
                val subject = dialogBinding.subjectInput.text.toString()
                //send announce ment
                vm.sendAnnouncement(subject,message)
                //dismiss dialog
                dialog.dismiss()
            }
            dialog.show()
        }
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
                        vm.deleteAnnouncement(index) { err, msg ->
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.show()
            }
        }
    }
}