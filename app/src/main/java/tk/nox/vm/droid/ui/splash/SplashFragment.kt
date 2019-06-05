package tk.nox.vm.droid.ui.splash

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import cn.quickits.arch.mvvm.base.BaseFragment
import com.blankj.utilcode.util.FileIOUtils
import com.lody.virtual.client.core.VirtualCore
import io.virtualapp.R
import io.virtualapp.abs.ui.VUiKit
import kotlinx.android.synthetic.main.fragment_splash2.*
import tk.nox.vm.droid.util.DirUtils


/**
 * @program: SandVXposed
 * @description:
 * @author: gavinliu
 * @create: 2019-05-30 14:17
 **/
class SplashFragment : BaseFragment() {

    private lateinit var viewModel: SplashViewModel

    override fun bindLayoutId(): Int = R.layout.fragment_splash2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)

        viewModel.successLiveData.observe(this, Observer { value ->
            if (value == true) {
                gotoLauncher()
            }
        })

        viewModel.installLiveData.observe(this, Observer {
            it?.let { list ->
                var isAllLoaded = true

                val sb = StringBuilder()
                list.forEach { result ->
                    val appData = result.appData
                    val isLoading = appData?.isLoading ?: true

                    sb.append(result.name)
                    sb.append(if (isLoading) " 正在安装" else " 安装成功")
                    sb.append(", ")

                    isAllLoaded = !isLoading
                }
                sb.append("\n此过程可能会持续几分钟，请耐心等待...")

                msg.text = sb.toString()

                if (isAllLoaded) {
                    gotoLauncher()
                }
            }
        })
    }

    private fun gotoLauncher() {
        activity?.let {
            findNavController().navigate(R.id.action_splashFragment_to_launcherFragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        VUiKit.defer().`when` {
            doActionInThread()
            FileIOUtils.writeFileFromIS(DirUtils.fileDir.absolutePath + "/WeChat.apk", activity?.assets?.open("WeChat_v6.7.3.apk"))
            FileIOUtils.writeFileFromIS(DirUtils.fileDir.absolutePath + "/Hooker.apk", activity?.assets?.open("app_hooker_20190605-105059_debug_v2.0.0-alpha9.apk"))
        }.done {
            viewModel.checkInstall()
        }
    }

    private fun doActionInThread() {
        if (!VirtualCore.get().isEngineLaunched) {
            VirtualCore.get().waitForEngine()
        }
    }

}