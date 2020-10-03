package com.example.firestoredemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), FragmentCallback {

    private val customAdapter by lazy { Adapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialize()
    }

    private fun initialize() {
        initLayout()
        initData()
    }

    private fun initLayout() {
        initViewPager2()
        initTabLayout()
    }

    private fun initViewPager2() {
        viewPager2.apply {
            adapter = customAdapter
            offscreenPageLimit = customAdapter.itemCount
        }
    }

    private fun initTabLayout() {
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.setText(customAdapter.items[position].titleId)
        }.attach()
    }

    private fun initData() {
        onRequestUpdateData()
    }

    override fun onCompete(task: Task) {
        FirebaseFirestore.getInstance()
            .collection("tasks")
            .document(task.id)
            .set(task.apply {
                deletedAt = Timestamp.now()
            })
            .addOnSuccessListener {
                onRequestUpdateData()
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    override fun onRequestUpdateData() {
        FirebaseFirestore.getInstance()
            .collection("tasks")
            .get()
            .addOnSuccessListener {
                val list: List<Task> = it.toObjects(Task::class.java)
                updateFragment(list)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    override fun onRequestMakeTask() {
        MaterialDialog(this).show {
            title(R.string.input_task_dialog_title)
            positiveButton(android.R.string.ok)
            negativeButton(android.R.string.cancel)
            input(allowEmpty = false, callback = { _, text ->
                val task = Task().apply {
                    name = text.toString()
                }
                FirebaseFirestore.getInstance()
                    .collection("tasks")
                    .document(task.id)
                    .set(task)
                    .addOnSuccessListener {
                        onRequestUpdateData()
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
            })
        }
    }

    private fun updateFragment(list: List<Task>) {
        customAdapter.items.forEach {
            (it.fragment as? ToDoFragment)?.updateData(list)
        }
    }

    class Adapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
        val items = listOf(
            Item(ToDoFragment.newInstance(false), R.string.todo_task),
            Item(ToDoFragment.newInstance(true), R.string.compete_task)
            )
        override fun getItemCount(): Int = items.size

        override fun createFragment(position: Int): Fragment = items[position].fragment

        class Item(val fragment: Fragment, val titleId: Int)
    }
}
