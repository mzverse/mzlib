package mz.mzlib.util.async;

import mz.mzlib.util.Either;
import mz.mzlib.util.RuntimeUtil;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public abstract class GeneratorFunction<T> extends AsyncFunction<Void>
{
    public Iterator<T> iterator()
    {
        Runner runner = new Runner();
        this.start(runner);
        return runner;
    }

    public BasicAwait emit(T value)
    {
        return new Yield(value);
    }

    class Yield implements BasicAwait
    {
        T value;
        public Yield(T value)
        {
            this.value = value;
        }
        public T getValue()
        {
            return this.value;
        }
        public GeneratorFunction<T> getFunction()
        {
            return GeneratorFunction.this;
        }
        @Override
        public String toString()
        {
            return GeneratorFunction.this + ".Yield(" + this.value + ")";
        }
    }
    class Runner implements AsyncFunctionRunner, Iterator<T>
    {
        Either<T, Void> result = Either.second(null);
        Runnable task;
        @Override
        public void schedule(Runnable function)
        {
            this.task = function;
        }
        @Override
        public void schedule(Runnable function, BasicAwait await)
        {
            if(!(await instanceof GeneratorFunction.Yield) || ((GeneratorFunction<?>.Yield) await).getFunction() != GeneratorFunction.this)
                throw new UnsupportedOperationException(Objects.toString(await));
            Yield yield = RuntimeUtil.cast(await);
            this.result = Either.first(yield.getValue());
            this.task = function;
        }
        @Override
        public boolean hasNext()
        {
            while(this.result.isSecond() && this.task != null)
            {
                Runnable last = this.task;
                this.task = null;
                last.run();
            }
            if(this.result.isFirst())
                return true;
            else
            {
                if(!GeneratorFunction.this.context.future.isDone())
                    throw new IllegalStateException("Generator can only await yield");
                return false;
            }
        }
        @Override
        public T next()
        {
            if(!this.hasNext())
                throw new NoSuchElementException();
            T result = this.result.getFirst().unwrap();
            this.result = Either.second(null);
            return result;
        }
    }
}