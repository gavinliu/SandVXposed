package tk.nox.vm.droid

import android.app.Application
import tk.nox.reply.bot.BotApplication


/**
 * @program: VirtualApp
 * @description:
 * @author: gavinliu
 * @create: 2019-05-28 15:32
 **/
object VMDroidApplication {

    fun onCreate(application: Application) {
        BotApplication().initModuleApp(application)
    }

}