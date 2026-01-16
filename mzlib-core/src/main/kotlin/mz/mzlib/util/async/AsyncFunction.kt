package mz.mzlib.util.async

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture

suspend inline fun BasicAwait.await() {
    val future = CompletableFuture<Unit>()
    (currentCoroutineContext()[CoroutineDispatcher]!!.asExecutor() as AsyncFunctionRunner).schedule({ future.complete(Unit) }, this)
    future.await()
}