package com.example.firestoredemo

import com.google.firebase.Timestamp
import java.util.*

class Task {
    var id = "${System.currentTimeMillis()}"
    var name = ""
    var createdAt : Timestamp = Timestamp.now()
    var deletedAt: Timestamp? = null
}