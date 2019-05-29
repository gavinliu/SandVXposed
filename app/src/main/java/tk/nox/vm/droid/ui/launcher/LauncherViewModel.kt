package tk.nox.vm.droid.ui.launcher

import cn.quickits.arch.mvvm.QLceViewModel
import com.blankj.utilcode.util.Utils
import io.virtualapp.home.models.AppData
import io.virtualapp.home.repo.AppRepository


/**
 * @program: VirtualApp
 * @description:
 * @author: gavinliu
 * @create: 2019-05-29 11:43
 **/
class LauncherViewModel : QLceViewModel<List<AppData>>() {

    private val repo: AppRepository = AppRepository(Utils.getApp())

    fun load() {
        displayLoader(false)

        repo.virtualApps.done { content.value = it }.fail { displayError(false, it) }
    }

}