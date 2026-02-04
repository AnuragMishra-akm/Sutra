package com.example.sutra.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sutra.model.SutraModel
import com.example.sutra.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class UserProfileViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance()
    private val usersRef = db.getReference("users")
    private val sutrasRef = db.getReference("sutras")

    private val _user = MutableLiveData<UserModel>()
    val user: LiveData<UserModel> = _user

    private val _sutras = MutableLiveData<List<SutraModel>>()
    val sutras: LiveData<List<SutraModel>> = _sutras

    private val _followerList = MutableLiveData(listOf<String>())
    val followerList: LiveData<List<String>> = _followerList

    private val _followingList = MutableLiveData(listOf<String>())
    val followingList: LiveData<List<String>> = _followingList

    // LiveData to store full UserModel objects for followers/following
    private val _usersListData = MutableLiveData<List<UserModel>>(emptyList())
    val usersListData: LiveData<List<UserModel>> = _usersListData

    fun fetchUser(userId: String) {
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                user?.let { _user.postValue(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun fetchSutras(userId: String) {
        sutrasRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val sutraList = snapshot.children.mapNotNull {
                        it.getValue(SutraModel::class.java)
                    }
                    _sutras.postValue(sutraList.asReversed())
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    val fireStoreDb = Firebase.firestore

    fun followUsers(userId: String, currentUserId: String) {
        val ref = fireStoreDb.collection("following").document(currentUserId)
        val followRef = fireStoreDb.collection("followers").document(userId)
        ref.set(mapOf("followingIds" to FieldValue.arrayUnion(userId)), com.google.firebase.firestore.SetOptions.merge())
        followRef.set(mapOf("followerIds" to FieldValue.arrayUnion(currentUserId)), com.google.firebase.firestore.SetOptions.merge())
    }

    fun getFollowers(userId: String) {
        fireStoreDb.collection("followers").document(userId).addSnapshotListener { value, e ->
            val followerIds = value?.get("followerIds") as? List<String> ?: listOf()
            _followerList.value = followerIds
        }
    }

    fun getFollowing(userId: String) {
        fireStoreDb.collection("following").document(userId).addSnapshotListener { value, e ->
            val followingIds = value?.get("followingIds") as? List<String> ?: listOf()
            _followingList.value = followingIds
        }
    }

    // New function to fetch full user models for a list of IDs
    fun fetchUsersFromIds(ids: List<String>) {
        if (ids.isEmpty()) {
            _usersListData.postValue(emptyList())
            return
        }
        val userList = mutableListOf<UserModel>()
        var count = 0
        for (id in ids) {
            usersRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(UserModel::class.java)?.let { userList.add(it) }
                    count++
                    if (count == ids.size) {
                        _usersListData.postValue(userList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    count++
                    if (count == ids.size) {
                        _usersListData.postValue(userList)
                    }
                }
            })
        }
    }
}
