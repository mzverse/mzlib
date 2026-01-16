package mz.mzlib.minecraft.item

inline fun ItemStack(block: ItemStack.Builder.() -> Unit) =
     ItemStack.builder().apply(block).build()!!

inline fun ItemStack(from: ItemStack, block: ItemStack.Builder.() -> Unit) =
    ItemStack.builder(from).apply(block).build()!!
