package mz.mzlib.util;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class Pair<F, S> implements Comparable<Pair<F, S>>
{
    F first;
    S second;

    public Pair(F first, S second)
    {
        this.first = first;
        this.second = second;
    }

    public F getFirst()
    {
        return this.first;
    }
    public S getSecond()
    {
        return this.second;
    }

    @Override
    public int compareTo(Pair<F, S> o)
    {
        int resultFirst = RuntimeUtil.<Comparable<F>>cast(first).compareTo(o.first);
        if(resultFirst != 0)
            return resultFirst;
        return RuntimeUtil.<Comparable<S>>cast(second).compareTo(o.second);
    }

    public Map.Entry<F, S> toMapEntry()
    {
        return new AbstractMap.SimpleEntry<>(first, second);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.first, this.second);
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof Pair && Objects.equals(first, ((Pair<?, ?>) o).first) &&
            Objects.equals(second, ((Pair<?, ?>) o).second);
    }

    @Override
    public String toString()
    {
        return "("+this.getFirst()+", "+this.getSecond()+")";
    }

    public static <F, S> Pair<F, S> of(F first, S second)
    {
        return new Pair<>(first, second);
    }

    public static <F, S> Comparator<Pair<F, S>> comparingByFirst(Comparator<F> comparator)
    {
        return (a, b) -> comparator.compare(a.first, b.first);
    }
    public static <F, S> Comparator<Pair<F, S>> comparingBySecond(Comparator<S> comparator)
    {
        return (a, b) -> comparator.compare(a.second, b.second);
    }
    public static <F extends Comparable<? super F>, S> Comparator<Pair<F, S>> comparingByFirst()
    {
        return Comparator.comparing(pair -> pair.first);
    }
    public static <F, S extends Comparable<? super S>> Comparator<Pair<F, S>> comparingBySecond()
    {
        return Comparator.comparing(pair -> pair.second);
    }
}
