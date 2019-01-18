package me.liangfei.areapicker

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.ListFragment
import com.liangfeizc.areapicker.R

/**
 * Created by LIANG.FEI on 16/1/2019.
 */

internal const val TAG = "AreaPicker"

internal typealias AreaPickListenerInner = ((Int, RFAreaModel) -> Unit)

internal class RFAreaListFragment : ListFragment() {
    companion object {
        fun newInstance(pageIndex: Int) =
                RFAreaListFragment().apply {
                    this.pageIndex = pageIndex
                }
    }

    private var pageIndex: Int = 0

    private lateinit var adapter: RFAreaListAdapter

    var pickListenerInner: AreaPickListenerInner? = null

    var areaModels: List<RFAreaModel>? = null
        set(value) {
            field = value

            if (this@RFAreaListFragment::adapter.isInitialized) {
                adapter.apply {
                    clear()
                    value?.let { addAll(it) }
                }
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated @RFAreaListFragment")

        adapter = RFAreaListAdapter(activity!!).apply {
            clear()
            areaModels?.let { addAll(it) }
        }.also {
            listAdapter = it
        }

        listView.apply {
            divider = null
            isVerticalScrollBarEnabled = false
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        adapter.setSelected(position)
        pickListenerInner?.invoke(pageIndex, l.getItemAtPosition(position) as RFAreaModel)
    }
}

internal class RFAreaListAdapter(context: Context) : ArrayAdapter<RFAreaModel>(context, 0) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    fun setSelected(position: Int) {
        for (i in 0 until count) {
            getItem(i)?.isSelected = false
        }
        getItem(position)?.isSelected = true
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        val vh: ViewHolder

        if (itemView == null) {
            itemView = inflater.inflate(R.layout.list_item_area, null, false)
            vh = ViewHolder(itemView)
            itemView.tag = vh
        } else {
            vh = itemView.tag as ViewHolder
        }

        getItem(position)?.let { areaModel ->
            vh.apply {
                name.apply {
                    text = areaModel.name
                    setTextColor(ContextCompat.getColor(context,
                            if (areaModel.isSelected) R.color.tab_theme_color
                            else R.color.tab_selected_text_color))
                }
                checked.visibility = if (areaModel.isSelected) View.VISIBLE else View.GONE
            }
        }

        return itemView!!
    }

    class ViewHolder(itemView: View) {
        val name: TextView = itemView.findViewById(R.id.area_name)
        val checked: ImageView = itemView.findViewById(R.id.check_image)
    }
}

