package com.example.studytime_4.ui.adapters

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.studytime_4.R
import com.example.studytime_4.R.color
import kotlinx.android.synthetic.main.year_list_item.view.*
import timber.log.Timber

class YearListAdapter(private val listener: (Int)-> Unit) : RecyclerView.Adapter<YearListAdapter.YearsWithSessionsViewHolder>() {

    var selected = false


    /*
        Manually call setData from the Activity because we need to update the recycler view when we have data.
        When there is a change to the years list we set this adapter's data with set data
     */

    fun setData(years: List<Int>){
        yearList = years
        notifyDataSetChanged()
    }

    var yearList = emptyList<Int>()

    class YearsWithSessionsViewHolder(val cardView: CardView): RecyclerView.ViewHolder(cardView){

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YearsWithSessionsViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.year_list_item, parent, false) as CardView
        return YearsWithSessionsViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: YearsWithSessionsViewHolder, position: Int) {

        val item = yearList[position]
        holder.cardView.yearTV.text = yearList[position].toString()


        holder.cardView.setOnClickListener {

            listener(item)
        } //When card view is clicked, pass the item value from the array (year)  into SessionWithMonthSelectorActivity
    }

    override fun getItemCount() = yearList.size


}
