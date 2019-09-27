package com.cmaozs.practica2

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import android.R.attr.data
import android.app.ProgressDialog
import android.text.TextUtils
import androidx.core.app.NotificationCompat.getExtras
import android.util.Patterns
import android.view.ActionProvider
import android.view.View
import android.widget.ProgressBar
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var email = "null"
    private var password = "null"
    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var progressBar: ProgressBar

    lateinit var googleSignInButton: SignInButton
    lateinit var facebookSignInButton: LoginButton

    //Facebook Callback manager
    var callbackManager: CallbackManager? = null


    val GOOGLE_LOG_IN_RC = 1
    val FACEBOOK_LOG_IN_RC = 2
    // Google API Client object.
    var googleApiClient: GoogleApiClient? = null

    private var TAG = "Login_Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)*/


        googleSignInButton = findViewById<View>(R.id.google_sign_in_button) as SignInButton
        facebookSignInButton = findViewById<View>(R.id.facebook_sign_in_button) as LoginButton

        progressBar = ProgressBar(this)

        googleSignInButton.setOnClickListener(this)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dbReference = database.reference.child("User")

        // Configure Google Sign In
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.request_client_id))
            .requestEmail()
            .build()

        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this /* OnConnectionFailedListener */) { }
            .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
            .build()

        btCreateaccount.setOnClickListener{
            var intent = Intent(this,AccountActivity::class.java)
            startActivityForResult(intent, 4598)
        }

        callbackManager = CallbackManager.Factory.create();

        facebookSignInButton.setReadPermissions("email", "public_profile")

        facebookSignInButton.setOnClickListener {
            signIn()
        }

        btSignin.setOnClickListener{
            var email_login: String = etEmail.text.toString()
            var password_login: String = etPassword.text.toString()

            if(email_login.equals("null") && password_login.equals("null")){
                etEmail.setError("Registrese por favor")
                etPassword.setError("Registrese por favor")
            }

            else {
                if (email_login.equals("") && password_login.equals("")){
                    etEmail.setError("Ingrese correo")
                    etPassword.setError("Ingrese contraseña")
                    Log.d("exception", "entro")
                }

                else if (email_login.equals("")){
                    etEmail.setError("Ingrese correo")
                }

                else if (password_login.equals("")){
                    etPassword.setError("Ingrese contraseña")
                }

                else {

                        loginUser()
                        /*var intent: Intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("Email", email)
                        intent.putExtra("Password", password)
                        startActivityForResult(intent, 4599)*/
                    }
                }

            }

        }

    private fun signIn() {
        facebookSignInButton.registerCallback(callbackManager,object:FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result!!.accessToken)
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {

            }

        } )
    }

    private  fun loginUser(){
        val correo = etEmail.text.toString()
        val password = etPassword.text.toString()

        if(!TextUtils.isEmpty(correo) && !TextUtils.isEmpty(password)){
            progressBar.visibility = View.VISIBLE

            auth.signInWithEmailAndPassword(correo,password)
                .addOnCompleteListener(this){
                        task ->
                    if (task.isSuccessful){

                        Toast.makeText(this, "Login completado", Toast.LENGTH_LONG).show()
                        updateUI()
                        //updateUI()
                    }
                    else{
                        Toast.makeText(this, "Error en el login", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun updateUI() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.i(TAG, "Authenticating user with firebase.")
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener(this) { task ->
            Log.i(TAG, "Firebase Authentication, is result a success? ${task.isSuccessful}.")
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                updateUI()
            } else {
                // If sign in fails, display a message to the user.
                Log.e(TAG, "Authenticating with Google credentials in firebase FAILED !!")
            }
        }
    }

    //callbackmanager?.onActivityResult(requestCode, resultCode, data)

    override fun onClick(view: View?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        when (view?.id) {
            R.id.google_sign_in_button -> {
                Log.i(TAG, "Trying Google LogIn.")
                googleLogin()
            }
           /*R.id.facebook_sign_in_button -> {
                Log.i(TAG, "Trying Twitter LogIn.")
                //twitterLogin()
            }*/
        }
    }

    private fun googleLogin() {
        Log.i(TAG, "Starting Google LogIn Flow.")
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, GOOGLE_LOG_IN_RC)
    }


    private fun handleFacebookAccessToken(accessToken: AccessToken?) {
        //get credential
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
        auth!!.signInWithCredential(credential)
            .addOnFailureListener{e->
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
            .addOnSuccessListener {result ->
                //get email

                val  email = result.user?.email
                Toast.makeText(this, "You logged with email: "+email, Toast.LENGTH_LONG).show()
                updateUI()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Log.i(TAG, "Got Result code ${requestCode}.")
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_LOG_IN_RC) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Log.i(TAG, "With Google LogIn, is result a success? ${result.isSuccess}.")
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                firebaseAuthWithGoogle(result.signInAccount!!)
            } else {
                Toast.makeText(this, "Some error occurred.", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            callbackManager!!.onActivityResult(requestCode, resultCode,data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}

