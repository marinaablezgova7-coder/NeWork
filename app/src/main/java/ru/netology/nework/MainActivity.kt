package ru.netology.nework

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Верхняя панель
        val toolbar = findViewById<MaterialToolbar>(
            R.id.toolbar
        )

        toolbar.setOnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.action_profile -> {

                    openProfileOrAuth()

                    true
                }

                else -> false
            }
        }

        // Нижнее меню
        val bottomNavigation =
            findViewById<BottomNavigationView>(
                R.id.bottom_navigation
            )

        // При первом запуске открываем экран Посты
        if (savedInstanceState == null) {

            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_container,
                    PostsFragment()
                )
                .commit()
        }

        // Переключение нижнего меню
        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.navigation_posts -> {

                    openFragment(PostsFragment())

                    true
                }

                R.id.navigation_events -> {

                    openFragment(EventsFragment())

                    true
                }

                R.id.navigation_users -> {

                    openFragment(UsersFragment())

                    true
                }

                else -> false
            }
        }

        // По умолчанию выбраны Посты
        bottomNavigation.selectedItemId =
            R.id.navigation_posts
    }

    /*
     * ОТКРЫТИЕ ФРАГМЕНТА
     */
    private fun openFragment(
        fragment: Fragment
    ) {

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_container,
                fragment
            )
            .commit()
    }

    /*
     * ПРОФИЛЬ ИЛИ ВХОД
     *
     * Если пользователь уже вошёл —
     * открываем его профиль.
     *
     * Если не вошёл —
     * показываем Войти / Зарегистрироваться.
     */
    private fun openProfileOrAuth() {

        val preferences =
            getSharedPreferences(
                "nework_auth",
                Context.MODE_PRIVATE
            )

        val isLoggedIn =
            preferences.getBoolean(
                "is_logged_in",
                false
            )

        if (isLoggedIn) {

            val login =
                preferences.getString(
                    "login",
                    ""
                ).orEmpty()

            val name =
                preferences.getString(
                    "name",
                    ""
                ).orEmpty()

            openFragment(
                UserDetailsFragment.newInstance(
                    name,
                    login
                )
            )

        } else {

            showAuthDialog()
        }
    }

    /*
     * ОКНО ВХОДА / РЕГИСТРАЦИИ
     */
    private fun showAuthDialog() {

        AlertDialog.Builder(this)
            .setItems(
                arrayOf(
                    "Войти",
                    "Зарегистрироваться"
                )
            ) { _, which ->

                when (which) {

                    // ВОЙТИ
                    0 -> {

                        supportFragmentManager
                            .beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                LoginFragment()
                            )
                            .addToBackStack(null)
                            .commit()
                    }

                    // ЗАРЕГИСТРИРОВАТЬСЯ
                    1 -> {

                        supportFragmentManager
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
}