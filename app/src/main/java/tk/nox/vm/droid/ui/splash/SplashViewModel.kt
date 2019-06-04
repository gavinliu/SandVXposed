package tk.nox.vm.droid.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.Utils
import com.lody.virtual.client.core.VirtualCore
import com.lody.virtual.os.VUserInfo
import com.lody.virtual.os.VUserManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.virtualapp.abs.ui.VUiKit
import io.virtualapp.home.models.AppData
import io.virtualapp.home.models.AppInfoLite
import io.virtualapp.home.models.MultiplePackageAppData
import io.virtualapp.home.models.PackageAppData
import io.virtualapp.home.repo.AppRepository
import io.virtualapp.home.repo.PackageAppDataStorage
import tk.nox.vm.droid.util.DirUtils
import java.io.IOException


/**
 * @program: SandVXposed
 * @description:
 * @author: gavinliu
 * @create: 2019-05-30 14:19
 **/
class SplashViewModel : ViewModel() {

    val installLiveData = MutableLiveData<ArrayList<AddResult>>()

    val successLiveData = MutableLiveData<Boolean>()

    private val repo: AppRepository = AppRepository(Utils.getApp())

    private val disposables: CompositeDisposable = CompositeDisposable()

    fun checkInstall() {
        val disposable = repo.getNeedInstallApps(Utils.getApp(), DirUtils.fileDir)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    println(it)
                    if (it.isEmpty()) {
                        successLiveData.value = true
                    } else {
                        it.forEach { info -> addApp(info.name, AppInfoLite(info.packageName, info.path, info.fastOpen)) }
                    }
                }, {
                    it.printStackTrace()
                })

        disposables.add(disposable)
    }

    private fun addApp(name: CharSequence, info: AppInfoLite) {
        val addResult = AddResult(name, info)
        notifyInstallStatus(addResult)

        VUiKit.defer()
                .`when` {
                    println("addApp when{} $info")
                    val installedAppInfo = VirtualCore.get().getInstalledAppInfo(info.packageName, 0)
                    addResult.justEnableHidden = installedAppInfo != null
                    if (addResult.justEnableHidden) {
                        val userIds = installedAppInfo!!.installedUsers
                        var nextUserId = userIds.size

                        for (i in userIds.indices) {
                            if (userIds[i] != i) {
                                nextUserId = i
                                break
                            }
                        }

                        addResult.userId = nextUserId

                        if (VUserManager.get().getUserInfo(nextUserId) == null) {
                            val nextUserName = "Space " + (nextUserId + 1)
                            VUserManager.get().createUser(nextUserName, VUserInfo.FLAG_ADMIN)
                                    ?: throw IllegalStateException()
                        }
                        val success = VirtualCore.get().installPackageAsUser(nextUserId, info.packageName)
                        if (!success) {
                            throw IllegalStateException()
                        }
                    } else {
                        val res = repo.addVirtualApp(info)
                        if (!res.isSuccess) {
                            throw IllegalStateException()
                        } else {
                            val ins = VirtualCore.get().getInstalledAppInfo(info.packageName, 0)
                            if (ins?.xposedModule != null) {
                                val name = ins.getApplicationInfo(0).name
                            }
                        }
                    }
                }
                .then {
                    println("addApp then{} $info")
                    addResult.appData = PackageAppDataStorage.get().acquire(info.packageName)
                }
                .done {
                    println("addApp done{} $info")

                    val multipleVersion = addResult.justEnableHidden && addResult.userId != 0
                    if (addResult.appData?.getXposedModule() != null) {
                        val data = addResult.appData
                        data?.isLoading = false
                        notifyInstallStatus(addResult)
                        return@done
                    }

                    if (!multipleVersion) {
                        val data = addResult.appData
                        data?.isLoading = true
                        notifyInstallStatus(addResult)
                        handleOptApp(addResult, data, info.packageName, true)
                    } else {
                        val data = MultiplePackageAppData(addResult.appData, addResult.userId)
                        data.isLoading = true
                        notifyInstallStatus(addResult)
                        handleOptApp(addResult, data, info.packageName, false)
                    }
                }
    }

    @Synchronized
    private fun notifyInstallStatus(result: AddResult?) {
        result ?: return

        val value = installLiveData.value ?: ArrayList()

        if (!value.contains(result)) {
            value.add(result)
        }

        installLiveData.value = value
    }

    private fun handleOptApp(result: AddResult, data: AppData?, packageName: String, needOpt: Boolean) {
        VUiKit.defer().`when` {
            var time = System.currentTimeMillis()
            if (needOpt) {
                try {
                    VirtualCore.get().preOpt(packageName)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            time = System.currentTimeMillis() - time
            if (time < 1500L) {
                try {
                    Thread.sleep(1500L - time)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }.done {
            if (data is PackageAppData) {
                data.isLoading = false
                data.isFirstOpen = true
            } else if (data is MultiplePackageAppData) {
                data.isLoading = false
                data.isFirstOpen = true
            }

            notifyInstallStatus(result)
        }
    }

    class AddResult(val name: CharSequence, val info: AppInfoLite) {
        var appData: PackageAppData? = null
        var userId: Int = 0
        var justEnableHidden: Boolean = false
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}