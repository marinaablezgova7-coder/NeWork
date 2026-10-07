package ru.netology.nework

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.netology.nework.data.AppDatabase
class PostDetailsFragment : Fragment() {

    private lateinit var toolbar: MaterialToolbar

    private lateinit var author: TextView
    private lateinit var job: TextView
    private lateinit var date: TextView
    private lateinit var text: TextView

    private lateinit var likeButton: ImageButton
    private lateinit var likesCount: TextView
    private var likedByMe = false
    private lateinit var likersAvatars: LinearLayout
    private lateinit var mentionedAvatars: LinearLayout
    private lateinit var mentionedCount: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_post_details,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(
            R.id.post_details_toolbar
        )

        author = view.findViewById(
            R.id.details_author
        )

        job = view.findViewById(
            R.id.details_job
        )

        date = view.findViewById(
            R.id.details_date
        )

        text = view.findViewById(
            R.id.details_text
        )

        likeButton = view.findViewById(
            R.id.details_like
        )

        likesCount = view.findViewById(
            R.id.details_likes_count
        )

        likersAvatars = view.findViewById(
            R.id.details_likers_avatars
        )

        mentionedAvatars = view.findViewById(
            R.id.details_mentioned_avatars
        )

        mentionedCount = view.findViewById(
            R.id.details_mentioned_count
        )

        // Данные выбранного поста
        val postAuthor =
            arguments?.getString(ARG_AUTHOR) ?: ""

        val postDate =
            arguments?.getString(ARG_DATE) ?: ""

        val postText =
            arguments?.getString(ARG_TEXT) ?: ""

        val postLikes =
            arguments?.getInt(ARG_LIKES) ?: 0

        val postId =
            arguments?.getLong(ARG_ID) ?: -1L

        likedByMe =
            arguments?.getBoolean(ARG_LIKED_BY_ME) ?: false

        // Заполняем экран
        author.text = postAuthor

        view.findViewById<TextView>(
            R.id.details_avatar
        ).text = postAuthor
            .trim()
            .split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") {
                it.first().uppercase()
            }

        date.text = postDate
        text.text = postText
        likesCount.text = postLikes.toString()

        likeButton.setImageResource(
            if (likedByMe) {
                R.drawable.ic_like_filled
            } else {
                R.drawable.ic_like_outline
            }
        )

        // Последнее место работы
        job.text = when (postAuthor) {
            "Leo Lipshutz" -> "Android Developer"
            "Иван Иванов" -> "В поиске работы"
            "Анна Петрова" -> "Android Developer"
            else -> "В поиске работы"
        }

        // Возвращаемся назад
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Поделиться
        view.findViewById<ImageButton>(
            R.id.details_share
        ).setOnClickListener {

            val shareIntent = Intent(
                Intent.ACTION_SEND
            ).apply {

                type = "text/plain"

                putExtra(
                    Intent.EXTRA_TEXT,
                    postText
                )
            }

            startActivity(
                Intent.createChooser(
                    shareIntent,
                    "Поделиться постом"
                )
            )
        }

        // Лайк
        likeButton.setOnClickListener {

            if (postId == -1L) return@setOnClickListener

            viewLifecycleOwner.lifecycleScope.launch {

                val database =
                    AppDatabase.getInstance(requireContext())

                val post =
                    database.postDao()
                        .getAll()
                        .find { it.id == postId }
                        ?: return@launch

                val newLikedByMe =
                    !post.likedByMe

                val newLikes =
                    if (newLikedByMe) {
                        post.likes + 1
                    } else {
                        (post.likes - 1).coerceAtLeast(0)
                    }

                database.postDao().updateLike(
                    post.id,
                    newLikes,
                    newLikedByMe
                )

                likedByMe = newLikedByMe

                likesCount.text =
                    newLikes.toString()

                likeButton.setImageResource(
                    if (likedByMe) {
                        R.drawable.ic_like_filled
                    } else {
                        R.drawable.ic_like_outline
                    }
                )
            }
        }
        // Демонстрационные лайкнувшие
        val likers = listOf(
            "A" to "Adison Levin",
            "L" to "Livia Donin",
            "L" to "Lydia Westervelt",
            "K" to "Kaylynn Mango",
            "L" to "Leo Lipshutz",
            "V" to "Vito Dobbs",
            "D" to "Dulce Kenter",
            "L" to "Lydia Levin",
            "R" to "Ryan Dias",
            "C" to "Charlie Lipshutz"
        )

        showLikers(likers)

        // Упомянутые пользователи
        val mentioned = listOf(
            "A" to "Анна Петрова",
            "L" to "Leo Lipshutz"
        )

        showMentioned(mentioned)
    }

    /**
     * Показываем лайкнувших.
     *
     * Первые 5 пользователей отображаются аватарами.
     * Если пользователей больше 5 —
     * шестым элементом показываем "+".
     */
    private fun showLikers(
        users: List<Pair<String, String>>
    ) {

        likersAvatars.removeAllViews()

        val visibleUsers =
            users.take(5)

        visibleUsers.forEach { user ->

            likersAvatars.addView(
                createAvatar(
                    letter = user.first,
                    name = user.second
                )
            )
        }

        if (users.size > 5) {

            likersAvatars.addView(
                createPlusAvatar()
            )
        }
    }

    /**
     * Показываем упомянутых пользователей.
     */
    private fun showMentioned(
        users: List<Pair<String, String>>
    ) {

        mentionedAvatars.removeAllViews()

        mentionedCount.text =
            users.size.toString()

        users.forEach { user ->

            mentionedAvatars.addView(
                createAvatar(
                    letter = user.first,
                    name = user.second
                )
            )
        }
    }

    /**
     * Создаёт круглый аватар с инициалом.
     */
    private fun createAvatar(
        letter: String,
        name: String
    ): TextView {

        val avatar = TextView(requireContext())

        avatar.text = letter
        avatar.textSize = 11f
        avatar.setTextColor(Color.WHITE)
        avatar.gravity = android.view.Gravity.CENTER

        val size = dp(32)

        val params =
            LinearLayout.LayoutParams(
                size,
                size
            )

        params.marginEnd = dp(2)

        avatar.layoutParams = params

        val background =
            GradientDrawable().apply {

                shape =
                    GradientDrawable.OVAL

                setColor(
                    Color.rgb(
                        111,
                        74,
                        168
                    )
                )
            }

        avatar.background = background

        avatar.contentDescription = name

        return avatar
    }

    /**
     * Круглая кнопка "+".
     */
    private fun createPlusAvatar(): TextView {

        val plus = TextView(requireContext())

        plus.text = "+"
        plus.textSize = 16f
        plus.setTextColor(Color.WHITE)
        plus.gravity = android.view.Gravity.CENTER

        val size = dp(32)

        val params =
            LinearLayout.LayoutParams(
                size,
                size
            )

        params.marginStart = dp(2)

        plus.layoutParams = params

        val background =
            GradientDrawable().apply {

                shape =
                    GradientDrawable.OVAL

                setColor(
                    Color.rgb(
                        111,
                        74,
                        168
                    )
                )
            }

        plus.background = background

        plus.contentDescription =
            "Показать всех пользователей"

        /*
         * Пока просто показываем кнопку.
         * Следующим шагом сделаем экран Likers
         * и переход по нажатию.
         */
        plus.setOnClickListener {

            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_container,
                    LikersFragment()
                )
                .addToBackStack(null)
                .commit()
        }

        return plus
    }

    private fun dp(value: Int): Int {

        return (
                value *
                        resources.displayMetrics.density
                ).toInt()
    }

    companion object {

        private const val ARG_AUTHOR =
            "author"

        private const val ARG_ID =
            "id"
        private const val ARG_DATE =
            "date"

        private const val ARG_TEXT =
            "text"

        private const val ARG_LIKES =
            "likes"

        private const val ARG_LIKED_BY_ME =
            "liked_by_me"

        fun newInstance(
            post: Post
        ): PostDetailsFragment {

            return PostDetailsFragment().apply {

                arguments = Bundle().apply {

                    putBoolean(
                        ARG_LIKED_BY_ME,
                        post.likedByMe
                    )

                    putLong(
                        ARG_ID,
                        post.id
                    )

                    putString(
                        ARG_AUTHOR,
                        post.author
                    )

                    putString(
                        ARG_DATE,
                        post.published
                    )

                    putString(
                        ARG_TEXT,
                        post.content
                    )

                    putInt(
                        ARG_LIKES,
                        post.likes
                    )
                }
            }
        }
    }
}