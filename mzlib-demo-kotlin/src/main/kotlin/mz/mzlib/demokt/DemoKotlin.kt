package mz.mzlib.demokt

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import mz.mzlib.minecraft.MinecraftServer
import mz.mzlib.minecraft.SleepTicks
import mz.mzlib.minecraft.item.ItemStack
import mz.mzlib.minecraft.text.Text
import mz.mzlib.minecraft.text.TextColor
import mz.mzlib.module.MzModule
import mz.mzlib.util.async.await

object DemoKotlin: MzModule() {
    override fun onLoad() {
        val is1 = ItemStack {
            fromId("minecraft:stone")
            count(1)
            customName(Text.literal("Hello, world!"))
        }
        val is2 = ItemStack(is1) {
            count(2)
        }
        val text = Text.literal("Hello, world!").apply {
            color = TextColor.GOLD
            bold = true
        }

        val runner = MinecraftServer.instance.registrable()
        this.register(runner)
        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch(runner.asCoroutineDispatcher()) {
            for(i in 0 until 10)
            {
                println("Hello Kt Coroutine! $i")
                SleepTicks(20L).await()
            }
        }
    }
}