package ru.netology.nework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class LikersFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: LikerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_likers,
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

        val toolbar =
            view.findViewById<MaterialToolbar>(
                R.id.likers_toolbar
            )

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        recycler =
            view.findViewById(
                R.id.likers_recycler
            )

        adapter = LikerAdapter()

        recycler.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        recycler.adapter = adapter

        adapter.submitList(
            listOf(

                Liker(
                    name = "Adison Levin",
                    username = "jugmmer8",
                    avatar = "A"
                ),

                Liker(
                    name = "Livia Donin",
                    username = "spinlurcy",
                    avatar = "L"
                ),

                Liker(
                    name = "Lydia Westervelt",
                    username = "lbragancapx",
                    avatar = "L"
                ),

                Liker(
                    name = "Kaylynn Mango",
                    username = "smorrilll",
                    avatar = "K"
                ),

                Liker(
                    name = "Leo Lipshutz",
                    username = "tokennny8q",
                    avatar = "L"
                ),

                Liker(
                    name = "Vito Dobbs",
                    username = "afuren83",
                    avatar = "V"
                ),

                Liker(
                    name = "Dulce Kenter",
                    username = "thenaughant",
                    avatar = "D"
                ),

                Liker(
                    name = "Lydia Levin",
                    username = "gprends",
                    avatar = "L"
                ),

                Liker(
                    name = "Ryan Dias",
                    username = "fuskbhansf2",
                    avatar = "R"
                ),

                Liker(
                    name = "Charlie Lipshutz",
                    username = "missacson9p",
                    avatar = "C"
                ),

                Liker(
                    name = "Cheyenne Schleifer",
                    username = "cheyenne12",
                    avatar = "C"
                )
            )
        )
    }
}