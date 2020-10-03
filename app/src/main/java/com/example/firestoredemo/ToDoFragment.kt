package com.example.firestoredemo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.to_do_fragment.*

class ToDoFragment: Fragment() {

    private var hasCompleted = false
    private var fragmentCallback: FragmentCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.to_do_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentCallback)
            fragmentCallback = context
    }

    fun updateData(list: List<Task>) {
        tasksView.customAdapter.refresh(list.filter { if (hasCompleted) it.deletedAt != null else it.deletedAt == null })
        swipeRefreshLayout.isRefreshing = false
    }

    private fun initialize() {
        initVariable()
        initClick()
        initRecyclerView()
        initSwipeRefreshLayout()
    }

    private fun initVariable() {
        hasCompleted = arguments?.getBoolean(KEY_COMPLETED) ?: false
    }

    private fun initClick() {
        floatingActionButton.setOnClickListener {
            fragmentCallback?.onRequestMakeTask()
        }
    }

    private fun initRecyclerView() {
        tasksView.customAdapter.apply {
            isCompleted = hasCompleted
            onComplete = {
                fragmentCallback?.onCompete(it)
            }
        }
    }

    private fun initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            fragmentCallback?.onRequestUpdateData()
        }
    }

    companion object {
        private const val KEY_COMPLETED = "key_completed"

        fun newInstance(hasCompleted: Boolean): ToDoFragment =
            ToDoFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(KEY_COMPLETED, hasCompleted)
                }
            }
    }
}