package com.example.messenger.LoginScreen

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.messenger.R
import com.example.messenger.messages.MessengerActivity
import com.example.messenger.oldUser.LogIn
import com.example.messenger.oldUser.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object{
        var bitmap : Bitmap?=null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profile_image_button.alpha=0f

        circularview_showimage.setImageResource(R.drawable.avatar1)


        // perform registration
        register.setOnClickListener{
           register()
        }
        // moving to old user screen
        old_user.setOnClickListener {
            val intent= Intent(this, LogIn::class.java)
            startActivity(intent)
        }
        //handling the rounded button
        profile_image_button.setOnClickListener {
            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }
    }
    var selectedpicuri:Uri?=null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data?.data == null){
            return
        }else{
            selectedpicuri =data.data
            bitmap= MediaStore.Images.Media.getBitmap(contentResolver,selectedpicuri)
            circularview_showimage.setImageBitmap(bitmap)
        }

    }


    private fun register(){
        val username=Username.text.toString()
        val email= Email.text.toString()
        val password=Password.text.toString()
        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"enter email and password both",Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener{
            if(!it.isSuccessful) {
                return@addOnCompleteListener
            }
            Toast.makeText(this,"Please wait ...",Toast.LENGTH_SHORT).show()
            uploadImageToFireBaseStorage()



        }.addOnFailureListener {
            Toast.makeText(this@MainActivity,"email or password is already taken",Toast.LENGTH_SHORT).show()
            return@addOnFailureListener

        }
    }
    private fun uploadImageToFireBaseStorage(){
        if( selectedpicuri == null){
            saveUserToFireBaseDatabase(" ")
        }else{
            val filename= UUID.randomUUID().toString()
            val ref=FirebaseStorage.getInstance().getReference("/images/$filename")
            ref.putFile(selectedpicuri!!)
                .addOnSuccessListener {

                    ref.downloadUrl.addOnSuccessListener {
                        saveUserToFireBaseDatabase(it.toString())
                    }
                        .addOnFailureListener{
                            Toast.makeText(this,"something went wrong",Toast.LENGTH_SHORT).show()
                        }
                }

        }

    }
    private fun saveUserToFireBaseDatabase(uri:String){
        val uid= FirebaseAuth.getInstance().uid ?: ""
        val ref=FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user= User(uid, Username.text.toString(), uri)

        ref.setValue(user)
            .addOnSuccessListener {
                val intent=Intent(this,
                    MessengerActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener{
                Toast.makeText(this,"something went wrong",Toast.LENGTH_SHORT).show()

            }

    }
}

