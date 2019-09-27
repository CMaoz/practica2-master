package com.cmaozs.practica2

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.core.os.HandlerCompat.postDelayed
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    private var email = ""
    private var password = ""
    private var canExitApp = false
    private var backpressedtime:Long = 0
    private var mAuth: FirebaseAuth? = null
    //private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private lateinit var mDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().reference


        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
            var name = user?.displayName
            var email = user?.email
            var id = user?.uid
            writeNewUser(id.toString(), name.toString(), email )
        } else {
            // No user is signed in
        }

        val extras = intent.getExtras()
        if (extras != null) {
            email = extras.getString("Email").toString()
            password = extras.getString("Password").toString()
        }

        //tvEmail_main.setText(email)
        btequiposfavoritos.setOnClickListener {

            var intent: Intent = Intent(this, Main2Activity::class.java)
            startActivity(intent)
            finish()

        }
    }

    fun logOutMenuOnClicked(){
        mAuth?.signOut()
        LoginManager.getInstance().logOut()
        goToLoginActivity()

    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        logOutMenuOnClicked()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        var intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }

    private fun writeNewUser(userId: String, name: String, email: String?) {
        val user = User(name, email)
        mDatabaseReference.child("users").child(userId).setValue(user)
    }

}
