package ru.netology.nework

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.netology.nework.data.AppDatabase
class LoginFragment : Fragment() {

    private lateinit var toolbar: MaterialToolbar

    private lateinit var loginInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout

    private lateinit var loginInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText

    private lateinit var loginButton: MaterialButton
    private lateinit var registerLink: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_login,
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
            R.id.login_toolbar
        )

        loginInputLayout = view.findViewById(
            R.id.login_input_layout
        )

        passwordInputLayout = view.findViewById(
            R.id.password_input_layout
        )

        loginInput = view.findViewById(
            R.id.login_input
        )

        passwordInput = view.findViewById(
            R.id.password_input
        )

        loginButton = view.findViewById(
            R.id.login_button
        )

        registerLink = view.findViewById(
            R.id.register_link
        )

        // =========================
        // КНОПКА НАЗАД
        // =========================

        toolbar.setNavigationOnClickListener {

            parentFragmentManager.popBackStack()
        }

        // =========================
        // УБИРАЕМ ОШИБКИ ПРИ ВВОДЕ
        // =========================

        loginInput.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {
                loginInputLayout.error = null
            }
        }

        passwordInput.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {
                passwordInputLayout.error = null
            }
        }

        // =========================
        // КНОПКА ВОЙТИ
        // =========================

        loginButton.setOnClickListener {

            val login = loginInput.text
                ?.toString()
                ?.trim()
                .orEmpty()

            val password = passwordInput.text
                ?.toString()
                ?.trim()
                .orEmpty()

            var isValid = true

            // Проверяем логин

            if (login.isEmpty()) {

                loginInputLayout.error =
                    "Логин не может быть пустым"

                isValid = false

            } else {

                loginInputLayout.error = null
            }

            // Проверяем пароль

            if (password.isEmpty()) {

                passwordInputLayout.error =
                    "Пароль не может быть пустым"

                isValid = false

            } else {

                passwordInputLayout.error = null
            }

            // Если поля заполнены неправильно
            if (!isValid) {
                return@setOnClickListener
            }

            // =========================
// ПРОВЕРКА ПОЛЬЗОВАТЕЛЯ В ROOM
// =========================

            val database =
                AppDatabase.getInstance(requireContext())

            viewLifecycleOwner.lifecycleScope.launch {

                val user =
                    database.userDao().getByLogin(login)

                // Пользователь не найден
                // или пароль не совпадает
                if (user == null || user.password != password) {

                    Toast.makeText(
                        requireContext(),
                        "Неправильный логин или пароль",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@launch
                }

                // =========================
                // УСПЕШНЫЙ ВХОД
                // =========================

                val authPreferences =
                    requireContext().getSharedPreferences(
                        "nework_auth",
                        Context.MODE_PRIVATE
                    )

                authPreferences.edit()
                    .putBoolean(
                        "is_logged_in",
                        true
                    )
                    .putLong(
                        "user_id",
                        user.id
                    )
                    .putString(
                        "login",
                        user.login
                    )
                    .putString(
                        "name",
                        user.name
                    )
                    .apply()

                Toast.makeText(
                    requireContext(),
                    "Вход выполнен",
                    Toast.LENGTH_SHORT
                ).show()

                parentFragmentManager.popBackStack()
            }
            // =========================
            // РЕГИСТРАЦИЯ
            // =========================

            registerLink.setOnClickListener {

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
}