package ru.netology.nework

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import ru.netology.nework.data.AppDatabase
import ru.netology.nework.data.WorkEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewWorkFragment : Fragment() {

    private lateinit var toolbar: MaterialToolbar

    private lateinit var companyEditText: TextInputEditText
    private lateinit var positionEditText: TextInputEditText

    private lateinit var startButton: MaterialButton
    private lateinit var finishButton: MaterialButton
    private lateinit var currentCheckBox: MaterialCheckBox
    private lateinit var saveButton: MaterialButton

    private var startDate: Date? = null
    private var finishDate: Date? = null

    private val dateFormatter =
        SimpleDateFormat(
            "dd MMM yyyy",
            Locale.ENGLISH
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_new_work,
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

        toolbar = view.findViewById(
            R.id.new_work_toolbar
        )

        companyEditText = view.findViewById(
            R.id.work_company
        )

        positionEditText = view.findViewById(
            R.id.work_position
        )

        startButton = view.findViewById(
            R.id.work_start_button
        )

        finishButton = view.findViewById(
            R.id.work_finish_button
        )

        currentCheckBox = view.findViewById(
            R.id.work_current_checkbox
        )

        saveButton = view.findViewById(
            R.id.work_save_button
        )

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        startButton.setOnClickListener {
            showDatePicker(
                isStartDate = true
            )
        }

        finishButton.setOnClickListener {
            if (!currentCheckBox.isChecked) {
                showDatePicker(
                    isStartDate = false
                )
            }
        }

        currentCheckBox.setOnCheckedChangeListener { _, checked ->

            finishButton.isEnabled = !checked

            if (checked) {
                finishDate = null
                finishButton.text = "Дата окончания"
            }
        }

        saveButton.setOnClickListener {
            saveWork()
        }
    }

    private fun showDatePicker(
        isStartDate: Boolean
    ) {

        val calendar = Calendar.getInstance()

        val selectedDate =
            if (isStartDate) {
                startDate
            } else {
                finishDate
            }

        if (selectedDate != null) {
            calendar.time = selectedDate
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->

                calendar.set(
                    year,
                    month,
                    dayOfMonth
                )

                val date =
                    calendar.time

                if (isStartDate) {

                    startDate = date

                    startButton.text =
                        dateFormatter.format(date)

                } else {

                    finishDate = date

                    finishButton.text =
                        dateFormatter.format(date)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveWork() {

        val company =
            companyEditText.text
                ?.toString()
                ?.trim()
                .orEmpty()

        val position =
            positionEditText.text
                ?.toString()
                ?.trim()
                .orEmpty()

        if (company.isEmpty()) {

            companyEditText.error =
                "Введите название компании"

            return
        }

        if (position.isEmpty()) {

            positionEditText.error =
                "Введите должность"

            return
        }

        if (startDate == null) {

            Toast.makeText(
                requireContext(),
                "Выберите дату начала",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (
            !currentCheckBox.isChecked &&
            finishDate == null
        ) {

            Toast.makeText(
                requireContext(),
                "Выберите дату окончания",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val preferences =
            requireContext()
                .getSharedPreferences(
                    "nework_auth",
                    Context.MODE_PRIVATE
                )

        val userId =
            preferences.getLong(
                "user_id",
                -1L
            )

        if (userId == -1L) {

            Toast.makeText(
                requireContext(),
                "Пользователь не авторизован",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val start =
            dateFormatter.format(
                startDate!!
            )

        val finish =
            if (currentCheckBox.isChecked) {
                null
            } else {
                dateFormatter.format(
                    finishDate!!
                )
            }

        viewLifecycleOwner.lifecycleScope.launch {

            val database =
                AppDatabase.getInstance(
                    requireContext()
                )

            database.workDao().insert(
                WorkEntity(
                    userId = userId,
                    company = company,
                    start = start,
                    finish = finish,
                    position = position
                )
            )

            Toast.makeText(
                requireContext(),
                "Работа добавлена",
                Toast.LENGTH_SHORT
            ).show()

            parentFragmentManager.popBackStack()
        }
    }
}