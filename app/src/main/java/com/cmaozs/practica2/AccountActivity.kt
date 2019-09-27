package com.cmaozs.practica2

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : AppCompatActivity() {

    private val TAG = "AccountActivity"

    private var mProgressBar: ProgressBar? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        progressBar = findViewById(R.id.progressbar)

        // Initialize Firebase Auth
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()

       btaccount.setOnClickListener {
            var email: String = etEmail_account.text.toString()
            var password: String = etPassword_account.text.toString()
            var reppassword: String = etRepassword.text.toString()
            var intent: Intent = Intent(this, LoginActivity::class.java)


            if(email.equals("") && password.equals("") && reppassword.equals("")){
                etEmail_account.setError("Ingrese el correo")
                etPassword_account.setError("Ingrese la contraseña")
            }

           else if (!validarEmail(email)){
                etEmail_account.setError("Correo no válido")
           }

           else if (password.length < 6){
               etPassword_account.setError("Contraseña mínima 6 caracteres")
           }

           else if(password.equals("")){

               etPassword_account.setError("Ingrese la contraseña")
           }

           else if(reppassword.equals("")){

               etRepassword.setError("Ingrese repetir contraseña")
           }

            else if (!password.equals(reppassword)){
               etPassword_account.setError("Contraseñas no coinciden")
               etRepassword.setError("Contraseñas no coinciden")
            }


            else{
                createNewAccount()
                /*intent.putExtra("Correo", email)
                intent.putExtra("Password", password)
                setResult(Activity.RESULT_OK, intent)
                finish()*/
            }
        }
    }


    private fun validarEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    /*private fun registerUser() {
        auth.createUserWithEmailAndPassword(etEmail_account.text.toString(), etEmail_account.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    //goToMainActivity()
                    //auth.signOut()
                    goToLoginActivity()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, task.exception?.message.toString(),
                        Toast.LENGTH_SHORT).show()

                }

                // ...
            }
    }*/

    private fun createNewAccount() {
        var nombre = etNombre_account.text.toString()
        var correo = etEmail_account.text.toString()
        var contrasena = etPassword_account.text.toString()

        if (!TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(correo) && !TextUtils.isEmpty(contrasena)) {
            mProgressBar?.visibility = View.VISIBLE
            mAuth!!
                .createUserWithEmailAndPassword(correo!!, contrasena!!)
                .addOnCompleteListener(this) { task ->
                    //mProgressBar!!.hide()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("exception", "createUserWithEmail:success")
                        val userId = mAuth!!.currentUser!!.uid
                        //Verify Email
                        verifyEmail();
                        //update user profile information
                        val currentUserDb = mDatabaseReference!!.child(userId)
                        currentUserDb.child("Nombre").setValue(nombre)
                        currentUserDb.child("Correo").setValue(correo)
                        updateUserInfoAndUI()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this@AccountActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserInfoAndUI() {
        //start next activity
        val intent = Intent(this@AccountActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun verifyEmail() {
        val mUser = mAuth!!.currentUser;
        mUser!!.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@AccountActivity,
                        "Verification email sent to " + mUser.getEmail(),
                        Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "sendEmailVerification", task.exception)
                    Toast.makeText(this@AccountActivity,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}




