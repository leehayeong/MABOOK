package com.example.mybook.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mybook.R
import com.example.mybook.extensions.replaceFragment
import com.example.mybook.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {

    private var backKeyPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.replaceFragment(
            R.id.fl_fragment_view,
            SearchFragment(), false)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount != 0) {
            supportFragmentManager.popBackStack()
            return
        }

        if (System.currentTimeMillis() - backKeyPressedTime >= 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, R.string.finish_info, Toast.LENGTH_SHORT).show()
            return
        }

        finish()
    }
}