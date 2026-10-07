package ru.netology.nework

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewPostFragment : Fragment() {

    private lateinit var postText: EditText

    private lateinit var imageContainer: View
    private lateinit var imageView: ImageView
    private lateinit var removeImageButton: android.widget.Button

    private var selectedImageUri: Uri? = null
    private var selectedFileUri: Uri? = null

    /*
     * ВЫБРАННЫЕ ПОЛЬЗОВАТЕЛИ ДЛЯ УПОМИНАНИЯ
     */
    private var selectedMentionUserIds = longArrayOf()

    /*
     * ВЫБОР ФОТО
     */
    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                selectedImageUri = uri

                imageView.setImageURI(uri)

                imageContainer.visibility =
                    View.VISIBLE
            }
        }

    /*
     * ВЫБОР ФАЙЛА
     */
    private val filePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                selectedFileUri = uri

                val fileName =
                    getFileName(uri)

                Toast.makeText(
                    requireContext(),
                    "Файл: $fileName",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_new_post,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        /*
         * ПРОВЕРКА АВТОРИЗАЦИИ
         */
        if (!isUserLoggedIn()) {

            Toast.makeText(
                requireContext(),
                "Для создания поста необходимо войти или зарегистрироваться",
                Toast.LENGTH_LONG
            ).show()

            parentFragmentManager.popBackStack()

            showAuthDialog()

            return
        }

        /*
         * ПОЛУЧЕНИЕ РЕЗУЛЬТАТА ВЫБОРА ПОЛЬЗОВАТЕЛЕЙ
         */
        parentFragmentManager.setFragmentResultListener(
            MentionUsersFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->

            selectedMentionUserIds =
                bundle.getLongArray(
                    MentionUsersFragment.RESULT_USER_IDS
                ) ?: longArrayOf()

            val mentionsView =
                view.findViewById<TextView>(
                    R.id.new_post_mentions
                )

            if (selectedMentionUserIds.isEmpty()) {

                mentionsView.visibility =
                    View.GONE

            } else {

                mentionsView.visibility =
                    View.VISIBLE

                mentionsView.text =
                    "Выбрано пользователей: ${selectedMentionUserIds.size}"
            }
        }

        /*
         * ЭЛЕМЕНТЫ ЭКРАНА
         */

        val toolbar =
            view.findViewById<MaterialToolbar>(
                R.id.new_post_toolbar
            )

        postText =
            view.findViewById(
                R.id.new_post_text
            )

        imageContainer =
            view.findViewById(
                R.id.new_post_image_container
            )

        imageView =
            view.findViewById(
                R.id.new_post_image
            )

        removeImageButton =
            view.findViewById(
                R.id.new_post_remove_image
            )

        val photoButton =
            view.findViewById<ImageButton>(
                R.id.new_post_photo
            )

        val fileButton =
            view.findViewById<ImageButton>(
                R.id.new_post_file
            )

        val mentionButton =
            view.findViewById<ImageButton>(
                R.id.new_post_mention
            )

        val locationButton =
            view.findViewById<ImageButton>(
                R.id.new_post_location
            )

        val publishButton =
            view.findViewById<ImageButton>(
                R.id.new_post_publish
            )

        /*
         * НАЗАД
         */
        toolbar.setNavigationOnClickListener {

            parentFragmentManager
                .popBackStack()
        }

        /*
         * ФОТО
         */
        photoButton.setOnClickListener {

            imagePicker.launch("image/*")
        }

        /*
         * УДАЛИТЬ ФОТО
         */
        removeImageButton.setOnClickListener {

            selectedImageUri = null

            imageView.setImageDrawable(null)

            imageContainer.visibility =
                View.GONE
        }

        /*
         * ФАЙЛ
         */
        fileButton.setOnClickListener {

            filePicker.launch("*/*")
        }

        /*
         * УПОМИНАНИЯ
         */
        mentionButton.setOnClickListener {

            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_container,
                    MentionUsersFragment()
                )
                .addToBackStack(null)
                .commit()
        }

        /*
         * МЕСТО
         */
        locationButton.setOnClickListener {

            Toast.makeText(
                requireContext(),
                "Добавление места",
                Toast.LENGTH_SHORT
            ).show()
        }

        /*
         * СОХРАНИТЬ ПОСТ
         */
        publishButton.setOnClickListener {

            createPost()
        }
    }

    /*
     * ПРОВЕРКА АВТОРИЗАЦИИ
     */
    private fun isUserLoggedIn(): Boolean {

        return requireContext()
            .getSharedPreferences(
                "nework_auth",
                Context.MODE_PRIVATE
            )
            .getBoolean(
                "is_logged_in",
                false
            )
    }

    /*
     * ОКНО ВХОДА / РЕГИСТРАЦИИ
     */
    private fun showAuthDialog() {

        androidx.appcompat.app.AlertDialog.Builder(
            requireContext()
        )
            .setItems(
                arrayOf(
                    "Войти",
                    "Зарегистрироваться"
                )
            ) { _, which ->

                when (which) {

                    0 -> {

                        parentFragmentManager
                            .beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                LoginFragment()
                            )
                            .addToBackStack(null)
                            .commit()
                    }

                    1 -> {

                        parentFragmentManager
                            .beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                RegistrationFragment()
                            )
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
            .show()
    }

    /*
     * СОЗДАНИЕ ПОСТА
     */
    private fun createPost() {

        if (!isUserLoggedIn()) {

            Toast.makeText(
                requireContext(),
                "Необходимо войти в приложение",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val content =
            postText.text
                .toString()
                .trim()

        /*
         * Пост не должен быть полностью пустым.
         */
        if (
            content.isEmpty() &&
            selectedImageUri == null &&
            selectedFileUri == null
        ) {

            Toast.makeText(
                requireContext(),
                "Введите текст поста",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        /*
         * Данные авторизованного пользователя
         */
        val authPreferences =
            requireContext()
                .getSharedPreferences(
                    "nework_auth",
                    Context.MODE_PRIVATE
                )

        val authorId = authPreferences.getLong(
            "user_id",
            -1L
        )

        val authorName =
            authPreferences
                .getString(
                    "name",
                    null
                )
                ?: authPreferences
                    .getString(
                        "login",
                        "Вы"
                    )
                    .orEmpty()

        /*
         * Создаём новый пост
         */
        val newPostId =
            System.currentTimeMillis()

        val published =
            getCurrentDate()

        val imageUrl =
            selectedImageUri?.toString()

        val videoUrl =
            null

        val link =
            null

        /*
         * Передаём новый пост обратно
         * в PostsFragment.
         *
         * Parcelable здесь НЕ используется.
         */
        parentFragmentManager.setFragmentResult(

            REQUEST_KEY,

            Bundle().apply {

                putLong(
                    RESULT_AUTHOR_ID,
                    authorId
                )

                putLong(
                    RESULT_ID,
                    newPostId
                )

                putString(
                    RESULT_AUTHOR,
                    authorName
                )

                putString(
                    RESULT_PUBLISHED,
                    published
                )

                putString(
                    RESULT_CONTENT,
                    content
                )

                putInt(
                    RESULT_LIKES,
                    0
                )

                putString(
                    RESULT_IMAGE_URL,
                    imageUrl
                )

                putString(
                    RESULT_VIDEO_URL,
                    videoUrl
                )

                putString(
                    RESULT_LINK,
                    link
                )

                putLongArray(
                    RESULT_MENTIONED_USER_IDS,
                    selectedMentionUserIds
                )
            }
        )

        Toast.makeText(
            requireContext(),
            "Пост опубликован",
            Toast.LENGTH_SHORT
        ).show()

        /*
         * Возвращаемся к списку постов.
         */
        parentFragmentManager
            .popBackStack()
    }

    /*
     * ТЕКУЩАЯ ДАТА
     *
     * dd.MM.yyyy HH:mm
     */
    private fun getCurrentDate(): String {

        val formatter =
            SimpleDateFormat(
                "dd.MM.yyyy HH:mm",
                Locale.getDefault()
            )

        return formatter.format(
            Date()
        )
    }

    /*
     * ИМЯ ФАЙЛА
     */
    private fun getFileName(
        uri: Uri
    ): String {

        var result = "Файл"

        val cursor =
            requireContext()
                .contentResolver
                .query(
                    uri,
                    null,
                    null,
                    null,
                    null
                )

        cursor?.use {

            val nameIndex =
                it.getColumnIndex(
                    OpenableColumns.DISPLAY_NAME
                )

            if (
                nameIndex >= 0 &&
                it.moveToFirst()
            ) {

                result =
                    it.getString(
                        nameIndex
                    )
            }
        }

        return result
    }

    companion object {

        const val RESULT_AUTHOR_ID = "new_post_author_id"

        /*
         * КЛЮЧ РЕЗУЛЬТАТА СОЗДАНИЯ ПОСТА
         */
        const val REQUEST_KEY =
            "new_post_result"

        /*
         * ДАННЫЕ ПОСТА
         */
        const val RESULT_ID =
            "new_post_id"

        const val RESULT_AUTHOR =
            "new_post_author"

        const val RESULT_PUBLISHED =
            "new_post_published"

        const val RESULT_CONTENT =
            "new_post_content"

        const val RESULT_LIKES =
            "new_post_likes"

        const val RESULT_IMAGE_URL =
            "new_post_image_url"

        const val RESULT_VIDEO_URL =
            "new_post_video_url"

        const val RESULT_LINK =
            "new_post_link"

        const val RESULT_MENTIONED_USER_IDS =
            "new_post_mentioned_user_ids"
    }
}