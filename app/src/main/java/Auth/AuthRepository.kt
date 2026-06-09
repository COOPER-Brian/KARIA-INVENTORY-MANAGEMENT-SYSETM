package com.example.kariainventoryapp.auth

import com.example.kariainventoryapp.AppState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun registerUser(
        name: String,
        email: String,
        password: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val userMap = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "role" to "user" // Default registered role
                        )

                        firestore.collection("users")
                            .document(uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                onComplete(true, null)
                            }
                            .addOnFailureListener {
                                onComplete(false, it.message)
                            }
                    } else {
                        onComplete(false, "User ID is null")
                    }
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun loginUser(
        email: String,
        password: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        // 🔑 FETCH THE ROLE INSTANTLY UPON SUCCESSFUL LOGIN
                        getUserRole(uid) { role ->
                            AppState.userRole = role ?: "user" // Fallback to user if null
                            onComplete(true, null)
                        }
                    } else {
                        onComplete(false, "User ID not found.")
                    }
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun getUserRole(
        uid: String,
        onResult: (String?) -> Unit
    ) {
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    onResult(role)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}