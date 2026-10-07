package ru.netology.nework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.netology.nework.data.AppDatabase

class UsersFragment : Fragment() {

    private lateinit var usersRecycler: RecyclerView
    private lateinit var adapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_users,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        usersRecycler = view.findViewById(
            R.id.users_recycler
        )

        adapter = UsersAdapter { user ->

            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_container,
                    UserFragment.newInstance(user)
                )
                .addToBackStack(null)
                .commit()
        }

        usersRecycler.layoutManager =
            LinearLayoutManager(requireContext())

        usersRecycler.adapter = adapter

        loadUsers()
    }

    private fun loadUsers() {

        viewLifecycleOwner.lifecycleScope.launch {

            val database =
                AppDatabase.getInstance(requireContext())

            val usersFromDatabase =
                database.userDao().getAll()

            val users =
                usersFromDatabase.map { entity ->

                    User(
                        id = entity.id,
                        login = entity.login,
                        name = entity.name
                    )
                }

            adapter.submitList(users)
        }
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            loadUsers()
        }
    }
}