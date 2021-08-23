package com.sparktech.studyzoneadmin.school

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import androidx.navigation.findNavController
import com.sparktech.studyzoneadmin.R
import com.sparktech.studyzoneadmin.databinding.SchoolLevelsExpandableItemBinding
import com.sparktech.studyzoneadmin.databinding.StudentLevelGroupItemBinding

class SchoolLevelAdapter(
    val levels: List<String>,
    val dataList: HashMap<String, List<String>>,
    val levelData: List<com.sparktech.studyzoneadmin.models.Level>,
    val bundle: Bundle
) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return levels.size
    }

    override fun getChildrenCount(position: Int): Int {
        return dataList[levels[position]]!!.size
    }

    override fun getGroup(position: Int): Any {
        return levels[position]
    }

    override fun getChild(listPosition: Int, expandedLisPosition: Int): Any {
        return dataList[levels[listPosition]]!!.get(expandedLisPosition)
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        return p1.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        listPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var binding: StudentLevelGroupItemBinding
        val inflater = LayoutInflater.from(parent?.context)
        binding = StudentLevelGroupItemBinding.inflate(inflater, parent, false)
        val level = levels.get(listPosition)
        binding.levelName.text = level
        return binding.root
    }

    override fun getChildView(
        lisPosition: Int,
        expandedListPosition: Int,
        isExpanded: Boolean,
        p3: View?,
        parent: ViewGroup?
    ): View {
        var binding: SchoolLevelsExpandableItemBinding
        val inflater = LayoutInflater.from(parent?.context)
        binding = SchoolLevelsExpandableItemBinding.inflate(inflater, parent, false)
        val semester = dataList[levels[lisPosition]]?.get(expandedListPosition)
        binding.levelSemesterLabel.text = semester
        binding.root.setOnClickListener {
            bundle.putString("level", levels[lisPosition])
            bundle.putString("semester", dataList[levels[lisPosition]]?.get(expandedListPosition))
            bundle.putString("lid",levelData[lisPosition].lid)
            it.findNavController()
                .navigate(R.id.action_schoolLevelFragment_to_pastQuestionFragment, bundle)
        }
        return binding.root
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return true
    }
}