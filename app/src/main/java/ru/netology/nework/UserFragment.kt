package ru.netology.nework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import ru.netology.nework.data.AppDatabase

class UserFragment : Fragment() {

    private lateinit var backButton: ImageButton
    private lateinit var name: TextView
    private lateinit var login: TextView
    private lateinit var tabs: TabLayout

    private lateinit var wallRecycler: RecyclerView
    private lateinit var jobsRecycler: RecyclerView

    private lateinit var postAdapter: PostAdapter
    private lateinit var workAdapter: WorkAdapter
    private var userId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_user,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        backButton = view.findViewById(R.id.user_back)
        name = view.findViewById(R.id.user_name)
        login = view.findViewById(R.id.user_login)
        tabs = view.findViewById(R.id.user_tabs)

        wallRecycler =
            view.findViewById(R.id.user_wall_recycler)

        jobsRecycler =
            view.findViewById(R.id.user_jobs_recycler)

        userId =
            arguments?.getLong(ARG_USER_ID, -1L)
                ?: -1L

        val userName =
            arguments?.getString(ARG_NAME)
                ?: ""

        val userLogin =
            arguments?.getString(ARG_LOGIN)
                ?: ""

        name.text = userName
        login.text = userLogin

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupWall()
        setupWorks()

        tabs.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(
                    tab: TabLayout.Tab
                ) {
                    when (tab.position) {

                        0 -> {
                            wallRecycler.visibility =
                                View.VISIBLE

                            jobsRecycler.visibility =
                                View.GONE
                        }

                        1 -> {
                            wallRecycler.visibility =
                                View.GONE

                            jobsRecycler.visibility =
                                View.VISIBLE
                        }
                    }
                }

                override fun onTabUnselected(
                    tab: TabLayout.Tab
                ) {
                }

                override fun onTabReselected(
                    tab: TabLayout.Tab
                ) {
                }
            }
        )
    }

    private fun setupWall() {

        postAdapter = PostAdapter(
            onLikeClicked = {
                // Лайк подключим позже
            },
            onMenuClicked = {
                // Для просмотра чужого профиля
                // меню пока не используем
            },
            onPostClicked = {
                // Открытие поста подключим позже
            },
            onShareClicked = {
                // Поделиться подключим позже
            }
        )

        wallRecycler.layoutManager =
            LinearLayoutManager(requireContext())

        wallRecycler.adapter = postAdapter

        loadUserPosts()
    }

    private fun loadUserPosts() {

        if (userId == -1L) {
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {

            val entities =
                AppDatabase
                    .getInstance(requireContext())
                    .postDao()
                    .getAll()

            val posts = entities
                .filter { it.authorId == userId }
                .map { entity ->

                    Post(
                        id = entity.id,
                        author = entity.author,
                        authorId = entity.authorId,
                        published = entity.published,
                        content = entity.content,
                        likes = entity.likes,
                        likedByMe = entity.likedByMe,
                        imageUrl = entity.imageUrl,
                        videoUrl = entity.videoUrl,
                        link = entity.link,
                        mentionedUserIds =
                            if (entity.mentionedUserIds.isBlank()) {
                                emptyList()
                            } else {
                                entity.mentionedUserIds
                                    .split(",")
                                    .mapNotNull {
                                        it.toLongOrNull()
                                    }
                            }
                    )
                }

            postAdapter.submitList(posts)
        }
    }
    private fun setupWorks() {

        workAdapter = WorkAdapter()

        jobsRecycler.layoutManager =
            LinearLayoutManager(requireContext())

        jobsRecycler.adapter = workAdapter

        loadUserWorks()
    }

    private fun loadUserWorks() {

        if (userId == -1L) {
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {

            val entities = AppDatabase
                .getInstance(requireContext())
                .workDao()
                .getByUserId(userId)

            val works = entities.map { entity ->
                Work(
                    id = entity.id,
                    userId = entity.userId,
                    company = entity.company,
                    start = entity.start,
                    finish = entity.finish,
                    position = entity.position
                )
            }

            workAdapter.submitList(works)
        }
    }
    companion object {

        private const val ARG_NAME = "user_name"
        private const val ARG_LOGIN = "user_login"
        private const val ARG_USER_ID = "user_id"

        fun newInstance(user: User): UserFragment {
            return UserFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_USER_ID, user.id)
                    putString(ARG_NAME, user.name)
                    putString(ARG_LOGIN, user.login)
                }
            }
        }
    }
}