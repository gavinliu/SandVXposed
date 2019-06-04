package tk.nox.vm.droid.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


/**
 * @program: VirtualApp
 * @description:
 * @author: gavinliu
 * @create: 2019-05-28 18:31
 **/
abstract class BaseActivity : AppCompatActivity() {

    abstract fun bindLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindLayoutId())
    }

}