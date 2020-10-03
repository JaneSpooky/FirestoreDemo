package com.example.firestoredemo

interface FragmentCallback {

    fun onCompete(task: Task)
    fun onRequestUpdateData()
    fun onRequestMakeTask()
}