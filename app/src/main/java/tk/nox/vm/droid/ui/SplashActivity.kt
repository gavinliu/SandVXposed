package tk.nox.vm.droid.ui

import android.os.Bundle
import android.view.WindowManager
import io.virtualapp.R
import io.virtualapp.VCommends
import jonathanfinerty.once.Once
import tk.nox.vm.droid.ui.base.BaseActivity


/**
 * @program: SandVXposed
 * @description:
 * @author: gavinliu
 * @create: 2019-05-29 17:48
 **/
class SplashActivity : BaseActivity() {

    override fun bindLayoutId(): Int = R.layout.activity_splash2

    override fun onCreate(savedInstanceState: Bundle?) {
        !Once.beenDone(Once.THIS_APP_INSTALL, VCommends.TAG_NEW_VERSION)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
    }

}