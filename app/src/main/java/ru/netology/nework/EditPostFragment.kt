package ru.netology.nework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

class EditPostFragment : Fragment() {

    private lateinit var editPostText: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_edit_post,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        editPostText = view.findViewById(
            R.id.edit_post_text
        )

        val postContent =
            arguments?.getString(ARG_POST_CONTENT)

        editPostText.setText(postContent)

        editPostText.requestFocus()

        editPostText.setSelection(
            editPostText.text?.length ?: 0
        )

        val toolbar = view.findViewById<MaterialToolbar>(
            R.id.edit_post_toolbar
        )

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        toolbar.setOnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.action_save -> {

                    val text = editPostText.text
                        ?.toString()
                        ?.trim()

                    if (text.isNullOrEmpty()) {

                        editPostText.error =
                            "Введите текст поста"

                        true

                    } else {

                        val postId =
                            arguments?.getLong(ARG_POST_ID)
                                ?: return@setOnMenuItemClickListener false

                        parentFragmentManager.setFragmentResult(
                            REQUEST_KEY,
                            Bundle().apply {

                                putLong(
                                    RESULT_POST_ID,
                                    postId
                                )

                                putString(
                                    RESULT_TEXT,
                                    text
                                )
                            }
                        )

                        parentFragmentManager.popBackStack()

                        true
                    }
                }

                else -> false
            }
        }
    }

    companion object {

        const val REQUEST_KEY =
            "edit_post_request"

        const val RESULT_POST_ID =
            "result_post_id"

        const val RESULT_TEXT =
            "result_text"

        private const val ARG_POST_ID =
            "post_id"

        private const val ARG_POST_CONTENT =
            "post_content"

        fun newInstance(
            post: Post
        ): EditPostFragment {

            return EditPostFragment().apply {

                arguments = Bundle().apply {

                    putLong(
                        ARG_POST_ID,
                        post.id
                    )

                    putString(
                        ARG_POST_CONTENT,
                        post.content
                    )
                }
            }
        }
    }
}