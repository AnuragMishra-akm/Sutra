package com.example.sutra.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sutra.model.UserModel
import com.example.sutra.utils.SharedPrep
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AuthViewModel: ViewModel() {
    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    val userRef = db.getReference("users")
    private val storageRef = FirebaseStorage.getInstance().reference
    private val imageRef = storageRef.child("users/${UUID.randomUUID()}.jpg")


    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?>  = _firebaseUser

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        _firebaseUser.value = auth.currentUser

    }

    fun login(email: String, password: String, context: Context) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _firebaseUser.value = auth.currentUser
                    getData(auth.currentUser!!.uid,context)
                } else {
                    _error.value = task.exception?.message
                }
            }
    }
    fun register(email: String, password: String, name: String, bio: String, username: String,imageUri: Uri,context: android.content.Context) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _firebaseUser.value = auth.currentUser
                    saveImage(email,password, name, bio, username, imageUri, auth.currentUser!!.uid,context)
                } else {
                    _error.value = task.exception?.message
                }
            }
    }
    private fun saveImage(
        email: String,
        password: String,
        name: String,
        bio: String,
        username: String,
        imageUri: Uri,
        uid: String,
        context: android.content.Context
    ) {
        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.addOnSuccessListener {  it ->
            imageRef.downloadUrl.addOnSuccessListener {
                saveData(email, password, name, bio, username, it.toString(), uid,context)
            }

        }

    }

    private fun saveData(
        email: String,
        password: String,
        name: String,
        bio: String,
        username: String,
        toString: String,
        uid: String,
        context: android.content.Context
    ) {
        val fireStoreDb = Firebase.firestore
        val followersRef = fireStoreDb.collection("followers").document(uid)
        val followingRef = fireStoreDb.collection("following").document(uid)

        followingRef.set(mapOf("followingIds" to listOf<String>()))
        followersRef.set(mapOf("followerIds" to listOf<String>()))

        val userData = UserModel(email, password, name, bio, username, toString, uid)
        userRef.child(uid).setValue(userData)
            .addOnSuccessListener {
                SharedPrep.storeData(name, email, bio, username, toString, context)
            }
            .addOnFailureListener {
                _error.value = it.message
            }
    }
    private fun getData(uid: String,context: Context) {
        userRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(UserModel::class.java)
                SharedPrep.storeData(userData!!.name, userData.email, userData.bio, userData.username, userData.imageUrl, context)
            }

            override fun onCancelled(error: DatabaseError) {
                _error.value = error.message
            }


        })
    }
    
     fun logOut() {
        auth.signOut()
        _firebaseUser.postValue(null)
    }

}
