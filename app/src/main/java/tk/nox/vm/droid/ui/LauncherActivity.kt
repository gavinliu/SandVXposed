package tk.nox.vm.droid.ui

import android.content.Context
import android.content.Intent
import androidx.navigation.findNavController
import cn.quickits.arch.mvvm.OnBackPressedHandler
import io.virtualapp.R
import tk.nox.vm.droid.ui.base.BaseActivity


/**
 * @program: VirtualApp
 * @description:
 * @author: gavinliu
 * @create: 2019-05-28 16:53
 **/
class LauncherActivity : BaseActivity() {

    override fun bindLayoutId(): Int = R.layout.activity_launcher

    override fun onBackPressed() {
        if (findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.launcherFragment) {

        } else {
            super.onBackPressed()
        }
    }

    companion object {

        fun goLauncher(context: Context) {
            val intent = Intent(context, LauncherActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

    }

}