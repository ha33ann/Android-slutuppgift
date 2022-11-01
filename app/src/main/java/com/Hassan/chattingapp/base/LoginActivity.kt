package com.Hassan.chattingapp.base

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.Hassan.chattingapp.MainActivity
import com.Hassan.chattingapp.databinding.ActivityLoginBinding
import com.Hassan.chattingapp.utils.ConstantKeys
import com.Hassan.chattingapp.utils.TinyDB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    //här instansierar vi vår databinding
    lateinit var binding: ActivityLoginBinding
    lateinit var tinyDB: TinyDB
    private lateinit var auth: FirebaseAuth

    //här skapar vi en instans av firebase autentisering
    //och databasen för att kunna använda oss av dem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        tinyDB = TinyDB(this)
        auth = Firebase.auth
        clicks()
    }

    //här skapar vi en funktion som kollar om användaren redan är inloggad
    //och om det är så så skickas användaren till MainActivity
    public override fun onStart() {
        super.onStart()

    }

    //här skapar vi en funktion som hanterar alla klicks i vår login activity
    private fun clicks() {

        binding.signinBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val email: String = binding.emailEdt.text.toString()
            val password: String = binding.passwordEdt.text.toString()
            //om användaren inte har skrivit in något i något av fälten
            //så kommer ett felmeddelande att visas
            if (email == "") {
                binding.emailEdt.error = "Please Enter Email"
                binding.emailEdt.requestFocus()
                return@setOnClickListener
            }
            //samma sak gäller för lösenordet
            if (password == "") {
                binding.passwordEdt.error = "Please Enter Password"
                binding.passwordEdt.requestFocus()
                return@setOnClickListener
            }
            //här loggar vi in användaren med hjälp av firebase autentisering
            login(email, password)
        }
    }

    //här skapar vi en funktion som loggar in användaren
    //med hjälp av firebase autentisering
    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                //om inloggningen lyckas så skickas användaren
                //till MainActivity
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    tinyDB.putString(ConstantKeys.USER_ID, user!!.uid)
                    binding.progressBar.visibility = View.GONE
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    Firebase.auth.signOut()
                    finish()
                    //om inloggningen misslyckas så visas ett felmeddelande
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        baseContext, "Login failed.",
                        Toast.LENGTH_SHORT
                    ).show()
//

                }
            }

    }
}