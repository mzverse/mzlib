package mz.mzlib.demokt.entrypoint

import mz.mzlib.demokt.DemoKotlin
import org.bukkit.plugin.java.JavaPlugin

class DemoKotlinBukkit: JavaPlugin() {
    companion object {
        @JvmStatic
        lateinit var instance: DemoKotlinBukkit
    }

    init {
        instance = this
    }

    override fun onEnable() {
        DemoKotlin.load()
    }
    override fun onDisable() {
        DemoKotlin.unload()
    }
}