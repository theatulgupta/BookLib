package com.agkminds.booklib.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.agkminds.booklib.*
import com.agkminds.booklib.databinding.ActivityMainBinding
import com.agkminds.booklib.fragment.AboutFragment
import com.agkminds.booklib.fragment.DashboardFragment
import com.agkminds.booklib.fragment.FavouritesFragment
import com.agkminds.booklib.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var previousMenuItem: MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setupToolbar()

//        When we open the app, we will be navigated to Dashboard Fragment
        transactFragmentTo(DashboardFragment(), "Dashboard")

        val actionBarDrawerToggle = ActionBarDrawerToggle(this,
            binding.drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer)

//        Below code makes the HamBurger icon functional. We are setting addDrawerListener.
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

//        Handling clicks on the menu items
        binding.navigationView.setNavigationItemSelectedListener {

            highlightMenuItems(it) // This function highlights the current selected menu items

            when (it.itemId) {
                R.id.dashboard -> {
                    transactFragmentTo(DashboardFragment(), "Dashboard")
                }
                R.id.favourites -> {
                    transactFragmentTo(FavouritesFragment(), "Favourites")
                }
                R.id.profile -> {
                    transactFragmentTo(ProfileFragment(), "Profile")
                }
                R.id.about -> {
                    transactFragmentTo(AboutFragment(), "About")
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "BookLib"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

//        Below code matching for hamburger id click.
        if (id == android.R.id.home) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    //    transactFragmentTo is custom function that is designed to minimize the code for fragment transaction
    private fun transactFragmentTo(passedFragment: Fragment, title: String = "Dashboard") {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, passedFragment)
        transaction.commit()
        binding.drawerLayout.closeDrawers()
        supportActionBar?.title = title
        binding.navigationView.setCheckedItem(R.id.dashboard) // This checks the dashboard as we open the app
    }

    //    We are overriding the onBackPressed() function & set the default behaviour to Dashboard Fragment
    override fun onBackPressed() {

        when (supportFragmentManager.findFragmentById(R.id.frame)) {
            !is DashboardFragment -> transactFragmentTo(DashboardFragment(), "Dashboard")

            else -> super.onBackPressed()
        }
    }

    //    This is a custom function that highlights the selected menu item.
    private fun highlightMenuItems(item: MenuItem) {
        if (previousMenuItem != null) {
            previousMenuItem?.isChecked = false
        }

        item.isCheckable = true
        item.isChecked = true
        previousMenuItem = item
    }
}