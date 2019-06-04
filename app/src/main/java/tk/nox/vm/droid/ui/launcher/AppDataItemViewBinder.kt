package tk.nox.vm.droid.ui.launcher

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.virtualapp.R
import io.virtualapp.home.models.AppData
import kotlinx.android.synthetic.main.item_appdata.view.*
import me.drakeet.multitype.ItemViewBinder


/**
 * @program: VirtualApp
 * @description:
 * @author: gavinliu
 * @create: 2019-05-29 14:52
 **/
class AppDataItemViewBinder(private val itemListener: ItemListener) : ItemViewBinder<AppData, AppDataItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_appdata, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: AppData) {
        holder.itemView.tv_title.text = item.name
        holder.itemView.iv_icon.setImageDrawable(item.icon)
        holder.itemView.setOnClickListener {
            itemListener.onClick(item)
        }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)

    interface ItemListener {

        fun onClick(item: AppData)

    }

}
