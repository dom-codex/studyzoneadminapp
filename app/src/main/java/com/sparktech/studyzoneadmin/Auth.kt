package com.sparktech.studyzoneadmin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sparktech.studyzoneadmin.databinding.KeysMainLayoutBinding
import com.sparktech.studyzoneadmin.models.LisenseKey

class Auth : AppCompatActivity() {
    private lateinit var binding: KeysMainLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.keys_main_layout)
        val keys = listOf(
                LisenseKey("ddsfsfffs", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsfwe", "dom", 800f, false, "test@test.com"),
                LisenseKey("ddsfsf", "dom", 800f, false, "test@test.com"))
        val adapter = KeysAdapter()
        binding.lisenseKeysRcv.adapter = adapter
        adapter.submitList(keys)
    }
}