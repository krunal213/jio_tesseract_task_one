package com.app.customelauncher

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.app.myapplication.AppInfo
import com.google.android.flexbox.FlexboxLayoutManager

class AppsAdapter(
    val appsList: ArrayList<AppInfo>,
    val onClickListener: View.OnClickListener,
    val width: Int
) : RecyclerView.Adapter<AppsAdapter.ViewHolder>() {

    private val originalList = ArrayList<AppInfo>()

    init {
        originalList.addAll(appsList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_row_view_list, parent, false)
        view.layoutParams.width = width
        view.layoutParams.height = width
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appLabel: String = appsList[position].label.toString()
        val appPackage: String = appsList[position].packageName.toString()
        val appIcon: Drawable = appsList[position].icon
        val textView = holder.textView
        textView.text = appLabel
        val imageView = holder.img
        imageView.setImageDrawable(appIcon)
        holder.itemView.setTag(R.string.tag_item_application,appsList[position])
        holder.itemView.setOnClickListener(onClickListener)
        val lp = holder.itemView.layoutParams
        if (lp is FlexboxLayoutManager.LayoutParams) {
            lp.flexGrow = 1f
        }
    }

    override fun getItemCount(): Int {
        return appsList.size
    }

    fun notifyDataSetChanged(data: ArrayList<AppInfo>) {
        appsList.clear()
        appsList.addAll(data)
        originalList.clear()
        originalList.addAll(data)
        notifyDataSetChanged()
    }

    fun notifySearchDataSetChanged(data: ArrayList<AppInfo>?) {
        if(data==null){
            appsList.clear()
            appsList.addAll(originalList)
            notifyDataSetChanged()
        }else{
            data?.apply {
                appsList.clear()
                appsList.addAll(data)
                notifyDataSetChanged()
            }
        }
    }

    fun resetList(){
        appsList.clear()
        appsList.addAll(originalList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView
        var img: ImageView
        init {
            textView = itemView.findViewById(R.id.tv_app_name)
            img = itemView.findViewById(R.id.app_icon)
        }
    }
}