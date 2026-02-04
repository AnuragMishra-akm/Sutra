package com.example.sutra.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sutra.model.SutraModel
import com.example.sutra.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class HomeViewModel: ViewModel() {
    private val db = FirebaseDatabase.getInstance()
    private val sutra = db.getReference("sutras")

    private val _isPosted = MutableLiveData<Boolean?>()
    val isPosted: LiveData<Boolean?> = _isPosted

    private val _sutraAndUser = MutableLiveData<List<Pair<SutraModel, UserModel>>>()
    val sutraAndUser: LiveData<List<Pair<SutraModel, UserModel>>> = _sutraAndUser

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private fun fetchSutraAndUser(onResult: (List<Pair<SutraModel, UserModel>>)-> Unit){
        sutra.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = mutableListOf<Pair<SutraModel, UserModel>>()
                for(sutraSnapshot in snapshot.children) {
                    val sutra = sutraSnapshot.getValue(SutraModel:: class.java)
                    sutra.let {
                        fetchUserFromSutra(it!!) { user ->
                            result.add(0, it to user)
                            if(result.size == snapshot.childrenCount.toInt()){
                                onResult(result)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        )
    }

    init {
        fetchSutraAndUser { sutraAndUser ->
            _sutraAndUser.value = sutraAndUser
        }
    }
    fun fetchUserFromSutra(sutra: SutraModel, onResult: (UserModel)-> Unit){
        db.getReference("users").child(sutra.userId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                user?.let(onResult)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        )
    }
}


