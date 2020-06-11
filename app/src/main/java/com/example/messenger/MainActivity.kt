package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // perform registration
        register.setOnClickListener{
           register()
        }
        // moving to old user screen
        old_user.setOnClickListener {
            val intent= Intent(this,LogIn::class.java)
            startActivity(intent)
        }
        //handling the rounded button
        profile_image_button.setOnClickListener {
            Log.d("important","try to show photo selector")
            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }
    }
    var selectedpicuri:Uri?=null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode== Activity.RESULT_OK && data != null){
            Log.d("important","image is set")
        }
        selectedpicuri =data?.data
        if(selectedpicuri == null)
            return
        val Bitmap= MediaStore.Images.Media.getBitmap(contentResolver,selectedpicuri)
        circularview_showimage.setImageBitmap(Bitmap)
        profile_image_button.alpha=0f
    }


    private fun register(){
        val username=Username.text.toString()
        val email= Email.text.toString()
        val password=Password.text.toString()
        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"enter email and password both",Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("important","username= $username")
        Log.d("important","Email= $email")
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener{
            if(!it.isSuccessful) {
                return@addOnCompleteListener
            }
            uploadImageToFireBaseStorage()



        }.addOnFailureListener {
            Log.d("important","ki yar")
            Toast.makeText(this@MainActivity,"email or password is already taken",Toast.LENGTH_SHORT).show()
            return@addOnFailureListener

        }
    }
    private fun uploadImageToFireBaseStorage(){
        if( selectedpicuri == null)return
        val filename= UUID.randomUUID().toString()
        val ref=FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedpicuri!!)
            .addOnSuccessListener {
                Log.d("important","image has been uploaded ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("important","file location is $it")
                    saveUserToFireBaseDatabase(it.toString())
                }
                    .addOnFailureListener{
                        Log.d("important","Storage porblem")
                    }
            }
    }
    private fun saveUserToFireBaseDatabase(uri:String){
        val uid= FirebaseAuth.getInstance().uid ?: ""
        val ref=FirebaseDatabase.getInstance().getReference("/users/$uid")

        var user=User(uid,Username.text.toString(),uri)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("important","finally we saved the user to the database")
            }
            .addOnFailureListener{
                Log.d("important","database porblem")

            }

    }
}
class User(val uid:String,val username : String , val profileimageurl : String )

