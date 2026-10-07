package ru.netology.nework

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class WorkAdapter(
    private val onDeleteClicked: ((Work) -> Unit)? = null
) : RecyclerView.Adapter<WorkAdapter.WorkViewHolder>() {

    private var works: List<Work> = emptyList()

    fun submitList(newWorks: List<Work>) {
        works = newWorks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.work_item, parent, false)

        return WorkViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: WorkViewHolder,
        position: Int
    ) {
        holder.bind(works[position])
    }

    override fun getItemCount(): Int {
        return works.size
    }

    inner class WorkViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val company: TextView =
            itemView.findViewById(R.id.work_company)

        private val period: TextView =
            itemView.findViewById(R.id.work_period)

        private val position: TextView =
            itemView.findViewById(R.id.work_position)

        private val deleteButton: MaterialButton =
            itemView.findViewById(R.id.work_delete_button)

        fun bind(work: Work) {

            company.text = work.company

            period.text =
                "${work.start} — ${work.finish ?: "по настоящее время"}"

            position.text = work.position

            if (onDeleteClicked != null) {
                deleteButton.visibility = View.VISIBLE

                deleteButton.setOnClickListener {
                    onDeleteClicked.invoke(work)
                }
            } else {
                deleteButton.visibility = View.GONE
            }
        }
    }
}