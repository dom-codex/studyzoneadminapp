package com.sparktech.studyzoneadmin

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import androidx.databinding.DataBindingUtil
import com.sparktech.studyzoneadmin.databinding.ActivityVideoPlayerBinding
import java.net.URL

class VideoPlayer : AppCompatActivity() {
    private lateinit var binding:ActivityVideoPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_video_player)
    }

    override fun onStart() {
        super.onStart()
        initPlayer()
    }
    private fun initPlayer(){
         val mediaController = MediaController(this)
        val url = intent?.getStringExtra("uri")
     //   val uri = Uri.parse(url)
        binding.player.setMediaController(mediaController)
        binding.player.setVideoPath(url)
        binding.player.requestFocus()
        binding.player.start()
    }

    override fun onStop() {
        super.onStop()
        binding.player.stopPlayback()
    }
}