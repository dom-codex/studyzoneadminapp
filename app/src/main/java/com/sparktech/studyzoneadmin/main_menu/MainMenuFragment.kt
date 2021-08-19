package com.sparktech.studyzoneadmin.main_menu

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.DialogCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.MainMenuBinding
import com.sparktech.studyzoneadmin.helpers.Categories
import com.sparktech.studyzoneadmin.models.Slide

class MainMenuFragment:Fragment() {
    private lateinit var binding:MainMenuBinding
    private lateinit var vm:MainMenuViewModel
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object:Runnable{
        override fun run() {
            val currentItemPos = binding.sliderPager.currentItem
            if(currentItemPos < 2){
                binding.sliderPager.currentItem = currentItemPos +1
            }else{
                binding.sliderPager.currentItem = 0
            }
            handler.postDelayed(this,3000)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.main_menu,container,false)
        InitView()
        return binding.root
    }
    private fun InitView(){
        //init rcv and adapter
        val adapter = MainMenuAdapter()
        binding.mainMenuRcv.adapter = adapter
        adapter.submitList(Categories.categories)

    }
    private fun setUpObservers(){
        vm.isLoading.observe(viewLifecycleOwner, Observer { loading->
            loading?.let{
                if(!it){
                    //show data
                    Toast.makeText(requireContext(),"${vm.sliderData.value}",Toast.LENGTH_SHORT).show()
                    val slides = listOf<Slide>(
                            Slide("users",vm.sliderData.value?.users!!),
                            Slide("transactions",vm.sliderData.value?.transactions!!.toString()),
                            Slide("requests",vm.sliderData.value?.requests!!.toString()),
                    )
                    initSlides(slides)
                }else{
                    //show spinner
                }
            }
        })
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init viewmodels
        vm = ViewModelProvider(requireActivity()).get(MainMenuViewModel::class.java)
     //   vm.fetchSliderDetails()
        setUpObservers()
    }
    private fun initSlides(slides:List<Slide>){
        val adapter = SliderAdapter(slides,this)
        binding.sliderPager.adapter = adapter
        TabLayoutMediator(binding.sliderTab,binding.sliderPager){tab,pos->

        }.attach()
    }

    override fun onStart() {
        super.onStart()
        handler.postDelayed(runnable,3000)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }
}