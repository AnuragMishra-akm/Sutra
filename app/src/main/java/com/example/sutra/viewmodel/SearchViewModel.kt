package com.example.sutra.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sutra.model.SutraModel
import com.example.sutra.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchViewModel: ViewModel() {
    private val db = FirebaseDatabase.getInstance()
    private val users = db.getReference("users")

    private val _isPosted = MutableLiveData<Boolean?>()
    val isPosted: LiveData<Boolean?> = _isPosted

    private val _user = MutableLiveData<List<UserModel>>()
    val userList: LiveData<List<UserModel>> = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private fun fetchUsers(onResult: (List<UserModel>)-> Unit){
        users.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = mutableListOf<UserModel>()
                for(sutraSnapshot in snapshot.children) {
                    val sutra = sutraSnapshot.getValue(UserModel:: class.java)
                    result.add(sutra!!)
                }
                onResult(result)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        )
    }

    init {
        fetchUsers { it ->
            _user.value = it
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


