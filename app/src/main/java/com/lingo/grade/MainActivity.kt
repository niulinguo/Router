package com.lingo.grade

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lingo.router.annotations.Destination
import com.lingo.router.runtime.Router

@Destination(url = "router://page/home", description = "首页")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        Router.go(this, "router://page/profile?name=lingo&msg=hehe")
    }
}