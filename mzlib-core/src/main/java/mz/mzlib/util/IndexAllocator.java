package mz.mzlib.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class IndexAllocator<T>
{
    List<T> list;
    Queue<Integer> bin;

    public IndexAllocator()
    {
        this.list = new ArrayList<>();
        this.bin = new ArrayDeque<>();
    }

    public synchronized int alloc()
    {
        if(!this.bin.isEmpty())
            return this.bin.poll();
        this.list.add(null);
        return this.list.size() - 1;
    }

    public synchronized void free(int index)
    {
        this.bin.add(index);
    }

    public T get(int index)
    {
        return this.list.get(index);
    }

    public void set(int index, T value)
    {
        this.list.set(index, value);
    }

    public int size()
    {
        return this.list.size() - this.bin.size();
    }
}
