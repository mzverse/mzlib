package mz.mzlib.util.async;

import mz.mzlib.util.TaskQueue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestAsyncFunction
{
    TaskQueue taskQueue = new TaskQueue();
    List<Integer> order = new ArrayList<>();

    // 自定义 BasicAwait：模拟延迟等待
    static class SleepMillis implements BasicAwait
    {
        final long millis;

        SleepMillis(long millis)
        {
            this.millis = millis;
        }

        long getMillis()
        {
            return millis;
        }
    }

    // 自定义 Runner：支持 SleepMillis
    static class SleepAwareRunner implements AsyncFunctionRunner
    {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

        @Override
        public void schedule(Runnable function)
        {
            this.service.execute(function);
        }

        @Override
        public void schedule(Runnable function, BasicAwait await)
        {
            if(await instanceof SleepMillis)
                this.service.schedule(function, ((SleepMillis) await).getMillis(), TimeUnit.MILLISECONDS);
            else
                throw new UnsupportedOperationException(Objects.toString(await));
        }
    }

    SleepAwareRunner sleepAwareRunner = new SleepAwareRunner();

    CompletableFuture<Void> fun1()
    {
        return new AsyncFunction<Void>()
        {
            @Override
            public void run()
            {
            }
            @Override
            protected Void template()
            {
                order.add(2);
                return null;
            }
        }.start(taskQueue);
    }
    CompletableFuture<Void> fun2()
    {
        return new AsyncFunction<Void>()
        {
            @Override
            public void run()
            {
            }
            @Override
            protected Void template()
            {
                order.add(3);
                return null;
            }
        }.start(taskQueue);
    }

    @Test
    void test()
    {
        new AsyncFunction<Void>()
        {
            @Override
            public void run()
            {
            }
            @Override
            protected Void template()
            {
                order.add(0);
                CompletableFuture<Void> f1 = fun1(), f2 = fun2();
                order.add(1);
                await0(f1);
                await0(f2);
                order.add(4);
                return null;
            }
        }.start(taskQueue);
        taskQueue.run();
        Assertions.assertEquals(Arrays.asList(0, 1, 2, 3, 4), order);
    }

    // 单个协程：等待100ms
    CompletableFuture<Integer> sleepCoroutine(int index)
    {
        return new AsyncFunction<Integer>()
        {
            @Override
            public void run()
            {
            }

            @Override
            protected Integer template()
            {
                await(new SleepMillis(100));
                return index;
            }
        }.start(sleepAwareRunner);
    }
    CompletableFuture<Void> sleepCoroutine()
    {
        return new AsyncFunction<Void>()
        {
            @Override
            public void run()
            {
            }

            @Override
            protected Void template()
            {
                // start all
                List<CompletableFuture<Integer>> futures = new ArrayList<>();
                for(int i = 0; i < 100; i++)
                {
                    futures.add(sleepCoroutine(i));
                }

                // await all
                for(CompletableFuture<?> future : futures)
                {
                    await0(future);
                }

                // assert
                for(int i = 0; i < futures.size(); i++)
                {
                    Assertions.assertEquals(i, futures.get(i).join());
                }

                return null;
            }
        }.start(sleepAwareRunner);
    }

    @Test
    void testConcurrentPerformance()
    {
        // preheat
        sleepCoroutine();

        Assertions.assertTimeout(Duration.ofMillis(1000), () -> sleepCoroutine().join());
    }
}
