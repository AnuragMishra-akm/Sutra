package com.example.sutra.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sutra.model.SutraModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddSutraViewModel: ViewModel() {
    private val db = FirebaseDatabase.getInstance()
    private val sutraRef = db.getReference("sutras")
    private val storageRef = FirebaseStorage.getInstance().reference

    private val _isPosted = MutableLiveData<Boolean?>()
    val isPosted: LiveData<Boolean?> = _isPosted

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun saveSutra(
        sutra: String,
        userId: String,
        imageUris: List<Uri>,
    ) {
        if (imageUris.isEmpty()) {
            saveData(sutra, userId, emptyList())
        } else {
            val uploadedImageUrls = mutableListOf<String>()
            var uploadCount = 0
            
            for (uri in imageUris) {
                val uniqueName = UUID.randomUUID().toString()
                val imageRef = storageRef.child("sutras/$uniqueName.jpg")
                
                imageRef.putFile(uri).addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        uploadedImageUrls.add(downloadUri.toString())
                        uploadCount++
                        if (uploadCount == imageUris.size) {
                            saveData(sutra, userId, uploadedImageUrls)
                        }
                    }.addOnFailureListener {
                        _isPosted.value = false
                        _error.value = it.message
                    }
                }.addOnFailureListener {
                    _isPosted.value = false
                    _error.value = it.message
                }
            }
        }
    }

    private fun saveData(
        sutra: String,
        userId: String,
        images: List<String>,
    ) {
        val sutraData = SutraModel(sutra, images, userId, System.currentTimeMillis().toString())
        sutraRef.child(sutraRef.push().key!!).setValue(sutraData).addOnSuccessListener {
            _isPosted.value = true
        }.addOnFailureListener {
            _isPosted.value = false
            _error.value = it.message
        }
    }

    fun resetState() {
        _isPosted.value = null
        _error.value = null
    }
}
