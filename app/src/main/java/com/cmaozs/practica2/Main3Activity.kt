package com.cmaozs.practica2

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.cmaozs.practica2.ui.main.SectionsPagerAdapter
import com.cmaozs.practica2.ui.main.SectionsPagerAdapter1

class Main3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        val sectionsPagerAdapter = SectionsPagerAdapter1(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager1)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }
}