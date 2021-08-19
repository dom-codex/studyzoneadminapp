package com.sparktech.studyzoneadmin.main_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sparktech.studyzoneadmin.SlidesFragment
import com.sparktech.studyzoneadmin.databinding.SliderLayoutItemBinding
import com.sparktech.studyzoneadmin.models.Slide
import com.sparktech.studyzoneadmin.models.SliderData

class SliderAdapter(val slides:List<Slide>,val fragment:Fragment):FragmentStateAdapter(fragment){
    override fun getItemCount(): Int {
        return slides.size
    }

    override fun createFragment(position: Int): Fragment {
        val slide = slides.get(position)
        return SlidesFragment().apply {
            val bundle = Bundle()
            bundle.putString("name",slide.name)
            bundle.putString("value",slide.value)
            this.arguments = bundle
        }
    }

}