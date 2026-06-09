package com.example.kariainventoryapp

import com.example.kariainventoryapp.models.Branch

object AppState {

    // Holds the currently selected branch
    var selectedBranch: Branch? = null

    // Holds the user role (admin / user)
    var userRole: String = ""

    var currentRole: String? = null
}