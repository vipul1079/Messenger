package com.example.messenger.oldUser

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messenger.R
import com.example.messenger.messages.MessengerActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.new_login.*

class LogIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_login);

        // finish the current activity and move on the the previous activity
        Sign_in.setOnClickListener {
            finish()
        }
        //logging in if the user is an old one


        Log_in.setOnClickListener{
            val Known_Email=Known_Email.text.toString()
            val Known_Password=Known_password.text.toString()
            if (Known_Email.isEmpty() || Known_Password.isEmpty()){
                Toast.makeText(this,"enter email and password both",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(Known_Email,Known_Password).addOnCompleteListener{
                if(!it.isSuccessful)return@addOnCompleteListener

                Log.d("important","user is successfully log in ${it.result}")
                val intent= Intent(this,
                    MessengerActivity::class.java)
                intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
                .addOnCanceledListener {
                    Toast.makeText(this,"incorrect username or password",Toast.LENGTH_SHORT).show()

                }

        }
    }
}