package com.example.kariainventoryapp.repository

import com.example.kariainventoryapp.models.Branch
import com.google.firebase.firestore.FirebaseFirestore

class BranchRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun addBranch(
        branch: Branch,
        onComplete: (Boolean, String?) -> Unit
    ) {

        // Generate unique branch ID
        val branchId = firestore.collection("branches").document().id

        // Copy branch with generated ID
        val newBranch = branch.copy(
            branchId = branchId
        )

        // Save to Firestore
        firestore.collection("branches")
            .document(branchId)
            .set(newBranch)
            .addOnSuccessListener {

                onComplete(true, null)
            }
            .addOnFailureListener {

                onComplete(false, it.message)
            }
    }
}