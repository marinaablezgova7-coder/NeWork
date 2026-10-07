package ru.netology.nework

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewEventFragment : Fragment() {

    private var editingEventId: Long? = null
    private var editingEvent: Event? = null

    private val calendar = Calendar.getInstance()

    private var selectedDate = ""
    private var selectedEventType = "Online"

    private val selectedSpeakerIds = mutableSetOf<Long>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_new_event,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        arguments?.let { args ->

            if (args.containsKey(ARG_EVENT_ID)) {

                editingEventId =
                    args.getLong(ARG_EVENT_ID)

                editingEvent =
                    Event(
                        id = editingEventId ?: 0L,
                        author = args.getString(ARG_EVENT_AUTHOR) ?: "",
                        authorId = args.getLong(ARG_EVENT_AUTHOR_ID),
                        published = args.getString(ARG_EVENT_PUBLISHED) ?: "",
                        content = args.getString(ARG_EVENT_CONTENT) ?: "",
                        eventDate = args.getString(ARG_EVENT_DATE) ?: "",
                        eventType = args.getString(ARG_EVENT_TYPE) ?: "Online",
                        likes = args.getInt(ARG_EVENT_LIKES),
                        participants = args.getInt(ARG_EVENT_PARTICIPANTS)
                    )

                selectedDate =
                    editingEvent?.eventDate ?: ""

                selectedEventType =
                    editingEvent?.eventType ?: "Online"
            }
        }
        super.onViewCreated(view, savedInstanceState)

        val preferences = requireContext()
            .getSharedPreferences(
                "nework_auth",
                Context.MODE_PRIVATE
            )

        val isLoggedIn = preferences.getBoolean(
            "is_logged_in",
            false
        )

        if (!isLoggedIn) {
            Toast.makeText(
                requireContext(),
                "Для создания события необходимо войти или зарегистрироваться",
                Toast.LENGTH_SHORT
            ).show()

            parentFragmentManager.popBackStack()
            return
        }

        // Заполняем форму при редактировании
        if (editingEvent != null) {

            val contentEditText =
                view.findViewById<TextInputEditText>(
                    R.id.event_content
                )

            contentEditText.setText(
                editingEvent?.content ?: ""
            )
        }

        // Назад
        view.findViewById<ImageButton>(
            R.id.event_back
        ).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Плюс
        view.findViewById<ImageButton>(
            R.id.event_more
        ).setOnClickListener {
            showEventOptions()
        }

        // Опубликовать
        view.findViewById<ImageButton>(
            R.id.event_publish
        ).setOnClickListener {
            saveEvent(view)
        }
    }

    /**
     * BottomSheet с настройками события
     */
    private fun showEventOptions() {

        val dialog = BottomSheetDialog(
            requireContext()
        )

        val container = LinearLayout(
            requireContext()
        ).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24)
        }

        // ДАТА
        val dateLayout = TextInputLayout(
            requireContext()
        ).apply {
            hint = "Date"

            boxBackgroundMode =
                TextInputLayout.BOX_BACKGROUND_OUTLINE

            isHintEnabled = true
        }

        val dateEditText = TextInputEditText(
            requireContext()
        ).apply {
            isFocusable = false
            isClickable = true
            textSize = 18f

            setPadding(
                16,
                12,
                16,
                12
            )
        }

        if (selectedDate.isNotEmpty()) {
            dateEditText.setText(selectedDate)
        } else {
            dateEditText.setText(
                SimpleDateFormat(
                    "MM/dd/yyyy HH:mm",
                    Locale.getDefault()
                ).format(calendar.time)
            )
        }

        dateLayout.addView(dateEditText)
        container.addView(dateLayout)

        val dateHint = TextView(
            requireContext()
        ).apply {
            text = "MM/dd/yyyy HH:mm"
            textSize = 12f

            setPadding(
                16,
                4,
                16,
                16
            )
        }

        container.addView(dateHint)

        // Выбор даты и времени
        dateEditText.setOnClickListener {

            DatePickerDialog(
                requireContext(),

                { _, year, month, dayOfMonth ->

                    calendar.set(
                        Calendar.YEAR,
                        year
                    )

                    calendar.set(
                        Calendar.MONTH,
                        month
                    )

                    calendar.set(
                        Calendar.DAY_OF_MONTH,
                        dayOfMonth
                    )

                    TimePickerDialog(
                        requireContext(),

                        { _, hour, minute ->

                            calendar.set(
                                Calendar.HOUR_OF_DAY,
                                hour
                            )

                            calendar.set(
                                Calendar.MINUTE,
                                minute
                            )

                            val format =
                                SimpleDateFormat(
                                    "MM/dd/yyyy HH:mm",
                                    Locale.getDefault()
                                )

                            selectedDate =
                                format.format(calendar.time)

                            dateEditText.setText(
                                selectedDate
                            )
                        },

                        calendar.get(
                            Calendar.HOUR_OF_DAY
                        ),

                        calendar.get(
                            Calendar.MINUTE
                        ),

                        true

                    ).show()
                },

                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)

            ).show()
        }

        // TYPE
        val typeTitle = TextView(
            requireContext()
        ).apply {
            text = "Type"
            textSize = 18f

            setTypeface(
                null,
                android.graphics.Typeface.BOLD
            )

            setPadding(
                0,
                8,
                0,
                8
            )
        }

        container.addView(typeTitle)

        // RADIO BUTTONS
        val radioGroup = RadioGroup(
            requireContext()
        ).apply {
            orientation = RadioGroup.VERTICAL
        }

        val onlineRadio = RadioButton(
            requireContext()
        ).apply {
            text = "Online"
            textSize = 18f
            id = View.generateViewId()
        }

        val offlineRadio = RadioButton(
            requireContext()
        ).apply {
            text = "Offline"
            textSize = 18f
            id = View.generateViewId()
        }

        if (selectedEventType == "Online") {
            onlineRadio.isChecked = true
        } else {
            offlineRadio.isChecked = true
        }

        radioGroup.addView(
            onlineRadio,
            RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                64
            )
        )

        radioGroup.addView(
            offlineRadio,
            RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                64
            )
        )

        container.addView(radioGroup)

        radioGroup.setOnCheckedChangeListener {
                _,
                checkedId ->

            selectedEventType =
                if (checkedId == onlineRadio.id) {
                    "Online"
                } else {
                    "Offline"
                }
        }

        // СПИКЕРЫ
        val speakersButton = MaterialButton(
            requireContext()
        ).apply {

            text =
                if (selectedSpeakerIds.isEmpty()) {
                    "Выбрать спикеров"
                } else {
                    "Спикеров выбрано: ${selectedSpeakerIds.size}"
                }

            textSize = 16f
            isAllCaps = false

            setOnClickListener {
                showSpeakersDialog()
            }
        }

        container.addView(speakersButton)

        dialog.setContentView(container)
        dialog.show()
    }

    /**
     * Выбор спикеров
     */
    private fun showSpeakersDialog() {

        val users = listOf(
            User(1L, "adison", "Adison Levin"),
            User(2L, "maria", "Мария Иванова"),
            User(3L, "alex", "Александр Петров"),
            User(4L, "anna", "Анна Смирнова"),
            User(5L, "ivan", "Иван Кузнецов"),
            User(6L, "olga", "Ольга Соколова"),
            User(7L, "dmitry", "Дмитрий Волков"),
            User(8L, "elena", "Елена Морозова"),
            User(9L, "sergey", "Сергей Орлов"),
            User(10L, "natalia", "Наталья Павлова")
        )

        val names = users
            .map {
                "${it.name} (@${it.login})"
            }
            .toTypedArray()

        val checkedItems =
            BooleanArray(users.size) { index ->
                selectedSpeakerIds.contains(
                    users[index].id
                )
            }

        MaterialAlertDialogBuilder(
            requireContext()
        )
            .setTitle("Выберите спикеров")

            .setMultiChoiceItems(
                names,
                checkedItems
            ) { _, which, isChecked ->

                val userId = users[which].id

                if (isChecked) {
                    selectedSpeakerIds.add(userId)
                } else {
                    selectedSpeakerIds.remove(userId)
                }
            }

            .setNegativeButton(
                "Отмена",
                null
            )

            .setPositiveButton(
                "Готово"
            ) { _, _ ->

                Toast.makeText(
                    requireContext(),
                    if (selectedSpeakerIds.isEmpty()) {
                        "Спикеры не выбраны"
                    } else {
                        "Выбрано спикеров: ${selectedSpeakerIds.size}"
                    },
                    Toast.LENGTH_SHORT
                ).show()
            }

            .show()
    }

    /**
     * Сохранение события
     */
    private fun saveEvent(view: View) {

        val contentEditText =
            view.findViewById<TextInputEditText>(
                R.id.event_content
            )

        val content =
            contentEditText.text
                ?.toString()
                ?.trim()
                ?: ""

        // Проверяем текст
        if (content.isEmpty()) {

            contentEditText.error =
                "Введите текст события"

            contentEditText.requestFocus()

            return
        }

        // Проверяем дату
        if (selectedDate.isEmpty()) {

            Toast.makeText(
                requireContext(),
                "Выберите дату проведения события",
                Toast.LENGTH_SHORT
            ).show()

            showEventOptions()

            return
        }

        val preferences = requireContext()
            .getSharedPreferences(
                "nework_auth",
                Context.MODE_PRIVATE
            )

        val author =
            preferences.getString(
                "name",
                null
            )
                ?: preferences.getString(
                    "login",
                    null
                )
                ?: "Вы"


        val authorId =
            preferences.getLong(
                "user_id",
                1L
            )

        val event = Event(
            id = editingEventId
                ?: System.currentTimeMillis(),

            author = author,

            authorId = authorId,

            published =
                editingEvent?.published
                    ?: SimpleDateFormat(
                        "dd.MM.yy HH:mm",
                        Locale.getDefault()
                    ).format(Calendar.getInstance().time),

            content = content,

            eventDate = selectedDate,

            eventType = selectedEventType,

            likes =
                editingEvent?.likes
                    ?: 0,

            likedByMe =
                editingEvent?.likedByMe
                    ?: false,

            participants =
                selectedSpeakerIds.size
        )

        // Передаём событие обратно в EventsFragment
        parentFragmentManager.setFragmentResult(
            REQUEST_KEY,
            Bundle().apply {
                putLong(
                    RESULT_ID,
                    event.id
                )

                putString(
                    RESULT_AUTHOR,
                    event.author
                )

                putString(
                    RESULT_PUBLISHED,
                    event.published
                )

                putString(
                    RESULT_CONTENT,
                    event.content
                )

                putString(
                    RESULT_EVENT_DATE,
                    event.eventDate
                )

                putString(
                    RESULT_EVENT_TYPE,
                    event.eventType
                )

                putInt(
                    RESULT_LIKES,
                    event.likes
                )

                putInt(
                    RESULT_PARTICIPANTS,
                    event.participants
                )
            }
        )

        Toast.makeText(
            requireContext(),
            "Событие опубликовано",
            Toast.LENGTH_SHORT
        ).show()

        // Возвращаемся к списку событий
        parentFragmentManager.popBackStack()
    }

    companion object {
        const val ARG_EVENT_ID =
            "arg_event_id"

        const val ARG_EVENT_AUTHOR =
            "arg_event_author"

        const val ARG_EVENT_AUTHOR_ID =
            "arg_event_author_id"

        const val ARG_EVENT_PUBLISHED =
            "arg_event_published"

        const val ARG_EVENT_CONTENT =
            "arg_event_content"

        const val ARG_EVENT_DATE =
            "arg_event_date"

        const val ARG_EVENT_TYPE =
            "arg_event_type"

        const val ARG_EVENT_LIKES =
            "arg_event_likes"

        const val ARG_EVENT_PARTICIPANTS =
            "arg_event_participants"

        const val REQUEST_KEY =
            "new_event_result"

        const val RESULT_ID =
            "result_event_id"

        const val RESULT_AUTHOR =
            "result_event_author"

        const val RESULT_PUBLISHED =
            "result_event_published"

        const val RESULT_CONTENT =
            "result_event_content"

        const val RESULT_EVENT_DATE =
            "result_event_date"

        const val RESULT_EVENT_TYPE =
            "result_event_type"

        const val RESULT_LIKES =
            "result_event_likes"

        const val RESULT_PARTICIPANTS =
            "result_event_participants"
    }
}