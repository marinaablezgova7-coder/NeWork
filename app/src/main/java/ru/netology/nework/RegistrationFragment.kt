package ru.netology.nework

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.netology.nework.data.AppDatabase
import ru.netology.nework.data.UserEntity
class RegistrationFragment : Fragment() {

    private lateinit var toolbar: MaterialToolbar

    private lateinit var loginLayout: TextInputLayout
    private lateinit var nameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var passwordRepeatLayout: TextInputLayout

    private lateinit var loginInput: TextInputEditText
    private lateinit var nameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var passwordRepeatInput: TextInputEditText

    private lateinit var registrationButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_registration,
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
            R.id.registration_toolbar
        )

        loginLayout = view.findViewById(
            R.id.registration_login_layout
        )

        nameLayout = view.findViewById(
            R.id.registration_name_layout
        )

        passwordLayout = view.findViewById(
            R.id.registration_password_layout
        )

        passwordRepeatLayout = view.findViewById(
            R.id.registration_password_repeat_layout
        )

        loginInput = view.findViewById(
            R.id.registration_login
        )

        nameInput = view.findViewById(
            R.id.registration_name
        )

        passwordInput = view.findViewById(
            R.id.registration_password
        )

        passwordRepeatInput = view.findViewById(
            R.id.registration_password_repeat
        )

        registrationButton = view.findViewById(
            R.id.registration_button
        )

        // НАЗАД
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Убираем ошибки при исправлении
        loginInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                loginLayout.error = null
            }
        }

        nameInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                nameLayout.error = null
            }
        }

        passwordInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                passwordLayout.error = null
            }
        }

        passwordRepeatInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                passwordRepeatLayout.error = null
            }
        }

        // РЕГИСТРАЦИЯ
        registrationButton.setOnClickListener {

            val login = loginInput.text
                ?.toString()
                ?.trim()
                .orEmpty()

            val name = nameInput.text
                ?.toString()
                ?.trim()
                .orEmpty()

            val password = passwordInput.text
                ?.toString()
                ?.trim()
                .orEmpty()

            val passwordRepeat = passwordRepeatInput.text
                ?.toString()
                ?.trim()
                .orEmpty()

            var isValid = true

            // LOGIN
            if (login.isEmpty()) {

                loginLayout.error =
                    "Логин не может быть пустым"

                isValid = false

            } else {

                loginLayout.error = null
            }

            // NAME
            if (name.isEmpty()) {

                nameLayout.error =
                    "Имя не может быть пустым"

                isValid = false

            } else {

                nameLayout.error = null
            }

            // PASSWORD
            if (password.isEmpty()) {

                passwordLayout.error =
                    "Пароль не может быть пустым"

                isValid = false

            } else {

                passwordLayout.error = null
            }

            // REPEAT PASSWORD
            if (passwordRepeat.isEmpty()) {

                passwordRepeatLayout.error =
                    "Повторите пароль"

                isValid = false

            } else if (password != passwordRepeat) {

                passwordRepeatLayout.error =
                    "Пароли не совпадают"

                isValid = false

            } else {

                passwordRepeatLayout.error = null
            }

            // Если есть ошибки в полях
            if (!isValid) {
                return@setOnClickListener
            }
// =========================
// ПРОВЕРКА И СОХРАНЕНИЕ В ROOM
// =========================

            val database =
                AppDatabase.getInstance(requireContext())

            viewLifecycleOwner.lifecycleScope.launch {

                // Проверяем, есть ли такой логин
                val existingUser =
                    database.userDao().getByLogin(login)

                if (existingUser != null) {

                    Toast.makeText(
                        requireContext(),
                        "Пользователь с таким логином уже зарегистрирован",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@launch
                }

                // =========================
                // СОЗДАЁМ НОВОГО ПОЛЬЗОВАТЕЛЯ
                // =========================

                val userId =
                    database.userDao().insert(
                        UserEntity(
                            login = login,
                            name = name,
                            password = password
                        )
                    )

                // =========================
                // АВТОРИЗАЦИЯ
                // =========================

                requireContext()
                    .getSharedPreferences(
                        "nework_auth",
                        Context.MODE_PRIVATE
                    )
                    .edit()
                    .putBoolean(
                        "is_logged_in",
                        true
                    )
                    .putLong(
                        "user_id",
                        userId
                    )
                    .putString(
                        "login",
                        login
                    )
                    .putString(
                        "name",
                        name
                    )
                    .apply()

                Toast.makeText(
                    requireContext(),
                    "Регистрация выполнена",
                    Toast.LENGTH_SHORT
                ).show()

                parentFragmentManager.popBackStack()
            }

        }
    }
}