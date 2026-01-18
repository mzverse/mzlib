package mz.mzlib.util.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TestGeneratorFunction
{
    @Test
    public void test1()
    {
        List<Integer> expected = new ArrayList<>(), actual = new ArrayList<>();
        new GeneratorFunction<Integer>()
        {
            @Override
            public void run()
            {
            }
            @Override
            public BasicAwait emit(Integer value)
            {
                expected.add(value);
                return super.emit(value);
            }
            @Override
            protected Void template()
            {
                for(int i = 0; i < 10; i++)
                    await(emit(i));
                await(emit(114514));
                for(int i = 10; i < 20; i++)
                    await(emit(i));
                return null;
            }
        }.iterator().forEachRemaining(actual::add);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void test2()
    {
        List<Integer> expected = new ArrayList<>(), actual = new ArrayList<>();
        int i = 0;
        for(Iterator<Integer> it = new GeneratorFunction<Integer>()
        {
            @Override
            public void run()
            {
            }
            @Override
            public BasicAwait emit(Integer value)
            {
                expected.add(value);
                return super.emit(value);
            }
            @Override
            protected Void template()
            {
                for(int i = 0; i < 10; i++)
                    await(emit(i));
                await(emit(114514));
                for(int i = 10; i < 20; i++)
                    await(emit(i));
                return null;
            }
        }.iterator(); i < 15 && it.hasNext(); i++)
        {
            actual.add(it.next());
        }
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void test3()
    {
        Assertions.assertThrows(IllegalStateException.class, () ->
        {
            List<Integer> expected = new ArrayList<>(), actual = new ArrayList<>();
            new GeneratorFunction<Integer>()
            {
                @Override
                public void run()
                {
                }
                @Override
                public BasicAwait emit(Integer value)
                {
                    expected.add(value);
                    return super.emit(value);
                }
                @Override
                protected Void template()
                {
                    for(int i = 0; i < 10; i++)
                        await(emit(i));
                    await(emit(114514));
                    await0(new CompletableFuture<>());
                    for(int i = 10; i < 20; i++)
                        await(emit(i));
                    return null;
                }
            }.iterator().forEachRemaining(actual::add);
            Assertions.assertEquals(expected, actual);
        });
    }
}
