package com.example.firestoredemo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TasksView: RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val customAdapter by lazy { Adapter(context) }

    init {
        adapter = customAdapter
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
    }

    class Adapter(val context: Context) : RecyclerView.Adapter<ViewHolder>() {

        private val items = mutableListOf<Task>()

        var isCompleted = false

        var onComplete: ((Task) -> Unit)? = null

        fun refresh(list: List<Task>) {
            items.apply {
                clear()
                addAll(list)
            }
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.task_cell, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (holder is ItemViewHolder)
                onBindViewHolder(holder, position)
        }

        private fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val data = items[position]
            holder.apply {
                nameTextView.text = data.name
                completeButton.visibility = if (isCompleted) View.GONE else View.VISIBLE
                completeButton.setOnClickListener {
                    onComplete?.invoke(data)
                }
            }
        }

        class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
            val completeButton = view.findViewById<Button>(R.id.completeButton)
        }
    }
}