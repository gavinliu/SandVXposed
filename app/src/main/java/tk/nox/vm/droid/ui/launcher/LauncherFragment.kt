package tk.nox.vm.droid.ui.launcher

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.view.View
import cn.quickits.arch.mvvm.QLceViewFragment
import com.blankj.utilcode.util.Utils
import io.virtualapp.R
import io.virtualapp.home.LoadingActivity
import io.virtualapp.home.models.AppData
import io.virtualapp.home.models.MultiplePackageAppData
import io.virtualapp.home.models.PackageAppData
import kotlinx.android.synthetic.main.fragment_launcher.*
import me.drakeet.multitype.MultiTypeAdapter


/**
 * @program: VirtualApp
 * @description:
 * @author: gavinliu
 * @create: 2019-05-28 16:53
 **/
class LauncherFragment : QLceViewFragment<List<AppData>, LauncherViewModel, androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(), AppDataItemViewBinder.ItemListener {

    private lateinit var adapter: MultiTypeAdapter

    override fun bindLayoutId(): Int = R.layout.fragment_launcher

    override fun createViewModel(): LauncherViewModel {
        return ViewModelProviders.of(this).get(LauncherViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentView.isEnabled = false

        adapter = MultiTypeAdapter()
        adapter.register(AppData::class.java, AppDataItemViewBinder(this))

        recycler_view.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.load()
    }

    override fun showContent(content: List<AppData>?) {
        super.showContent(content)
        content ?: return

        adapter.items = content
        adapter.notifyDataSetChanged()
    }

    override fun onClick(item: AppData) {
        try {
            if (item is PackageAppData) {
                item.isFirstOpen = false
                LoadingActivity.launch(Utils.getApp(), item.packageName, 0)
            } else if (item is MultiplePackageAppData) {
                item.isFirstOpen = false
                LoadingActivity.launch(Utils.getApp(), item.appInfo.packageName, item.userId)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed(): Boolean = true

}