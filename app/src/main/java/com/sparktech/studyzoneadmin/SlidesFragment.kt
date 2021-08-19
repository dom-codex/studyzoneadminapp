package com.sparktech.studyzoneadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sparktech.studyzoneadmin.databinding.SliderLayoutItemBinding

class SlidesFragment: Fragment() {
    private lateinit var binding:SliderLayoutItemBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater,R.layout.slider_layout_item,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }
    private fun initViews(){
        val bundle = requireArguments()
        val name = bundle.getString("name")
        val value = bundle.getString("value")
        binding.sliderValue.text = value
        binding.sliderName.text = name
    }
}