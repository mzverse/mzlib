package mz.mzlib.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

public class TaskQueue implements Executor
{
    Queue<Runnable> tasks = new ArrayDeque<>();

    public void schedule(Runnable task)
    {
        this.tasks.add(task);
    }

    @Override
    public void execute(@NotNull Runnable command)
    {
        this.schedule(command);
    }

    public void run()
    {
        for(Runnable task; (task = this.tasks.poll()) != null; )
        {
            try
            {
                task.run();
            }
            catch(Throwable e)
            {
                this.onCatch(e);
            }
        }
    }

    public void onCatch(Throwable e)
    {
        e.printStackTrace(System.err);
    }

    public boolean isEmpty()
    {
        return this.tasks.isEmpty();
    }
}
