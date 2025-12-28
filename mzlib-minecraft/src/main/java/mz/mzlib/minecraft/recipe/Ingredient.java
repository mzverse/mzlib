package mz.mzlib.minecraft.recipe;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.item.ItemStack;
import mz.mzlib.minecraft.mzitem.MzItem;
import mz.mzlib.minecraft.mzitem.RegistrarMzItem;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Ingredient extends Predicate<ItemStack>
{
    default Collection<ItemStack> getExamples()
    {
        return Collections.emptyList();
    }

    /**
     * Checks if this ingredient requires a specific item count.
     *
     * <p>If {@code false}, the ingredient only cares about item presence.
     * {@link #getCount(ItemStack)} will always return 1 in this case.</p>
     *
     * <p>If {@code true}, the exact count matters and will be consumed
     * during crafting. The count may vary based on item type.</p>
     *
     * @return {@code true} if a specific count is required, {@code false} otherwise
     */
    default boolean isCountRequired()
    {
        return false;
    }

    /**
     * Gets the number of items required from the given itemStack.
     *
     * <p>This is the amount that will be consumed when crafting.
     * The itemStack must have at least this many items.</p>
     *
     * <p>If {@link #isCountRequired()} returns {@code false}, this
     * always returns 1.</p>
     *
     * <p>For ingredients with multiple valid item types, the required
     * count may differ. Example: diamond blocks require 1, diamonds require 9.</p>
     *
     * @param itemStack
     *     a matching item itemStack, not null
     * @return the number of items required, at least 1
     */
    default int getCount(ItemStack itemStack)
    {
        return 1;
    }

    /**
     * TODO
     *
     * @return a new {@link ItemStack} or {@link ItemStack#EMPTY}
     */
    default ItemStack getRemainder(ItemStack itemStack)
    {
        return ItemStack.EMPTY;
    }

    static Ingredient of(ItemStack itemStack)
    {
        Ingredient result = null;
        for(MzItem mzItem : RegistrarMzItem.instance.toMzItem(itemStack))
        {
            result = new MatchingMzItem(mzItem.getMzId());
        }
        if(result == null)
            result = IngredientVanilla.of(itemStack);
        return result.withCount(itemStack.getCount());
    }

    default Ingredient withCount(int count)
    {
        if(!this.isCountRequired() && count == 1)
            return this;
        return new WithCount(this, count);
    }
    class WithCount implements Ingredient
    {
        Ingredient original;
        int count;
        public WithCount(Ingredient original, int count)
        {
            if(original.isCountRequired())
                throw new IllegalArgumentException("Cannot apply count to an already counted ingredient");
            this.original = original;
            this.count = count;
        }
        @Override
        public boolean test(ItemStack itemStack)
        {
            return this.original.test(itemStack);
        }
        @Override
        public Collection<ItemStack> getExamples()
        {
            return this.original.getExamples();
        }
        @Override
        public boolean isCountRequired()
        {
            return true;
        }
        @Override
        public int getCount(ItemStack itemStack)
        {
            return this.count;
        }
        @Override
        public ItemStack getRemainder(ItemStack itemStack)
        {
            ItemStack result = this.original.getRemainder(itemStack);
            if(!result.isEmpty())
                result.setCount(result.getCount() * this.count);
            return result;
        }
        @Override
        public int hashCode()
        {
            return Objects.hash(this.original, this.count);
        }
        @Override
        public boolean equals(Object o)
        {
            if(this == o)
                return true;
            if(!(o instanceof WithCount))
                return false;
            WithCount that = (WithCount) o;
            return this.original.equals(that.original) && this.count == that.count;
        }
    }

    static Ingredient merge(Ingredient... ingredients)
    {
        return merge(Arrays.asList(ingredients));
    }
    static Ingredient merge(List<Ingredient> ingredients)
    {
        if(MinecraftPlatform.instance.getVersion() >= 1200 &&
            ingredients.stream().allMatch(IngredientVanilla.class::isInstance))
        {
            Stream<IngredientVanilla> stream = ingredients.stream().map(IngredientVanilla.class::cast);
            if(MinecraftPlatform.instance.getVersion() < 1300)
                return IngredientVanilla.ofV1200(stream.map(IngredientVanilla::getDataV1200_1300)
                    .flatMap(List::stream).collect(Collectors.toList()));
            else
                return IngredientVanilla.ofV1200(stream.map(IngredientVanilla::getMatchingStacksV1300)
                    .flatMap(List::stream).collect(Collectors.toList()));
        }
        return new Merged(ingredients);
    }
    class Merged implements Ingredient
    {
        final List<Ingredient> ingredients;
        boolean countRequired;

        public Merged(List<Ingredient> ingredients)
        {
            this.ingredients = ingredients;
            this.countRequired = ingredients.stream().anyMatch(Ingredient::isCountRequired);
        }
        public Merged(Ingredient... ingredients)
        {
            this(Arrays.asList(ingredients));
        }

        @Override
        public boolean test(ItemStack itemStack)
        {
            for(Ingredient ingredient : this.ingredients)
            {
                if(ingredient.test(itemStack))
                    return true;
            }
            return false;
        }
        @Override
        public boolean isCountRequired()
        {
            return this.countRequired;
        }
        @Override
        public int getCount(ItemStack itemStack)
        {
            if(!this.isCountRequired())
                return 1;
            for(Ingredient ingredient : this.ingredients)
            {
                if(ingredient.test(itemStack))
                    return ingredient.getCount(itemStack);
            }
            throw new IllegalArgumentException("No matching ingredient found for " + itemStack);
        }
        @Override
        public ItemStack getRemainder(ItemStack itemStack)
        {
            for(Ingredient ingredient : this.ingredients)
            {
                if(ingredient.test(itemStack))
                    return ingredient.getRemainder(itemStack);
            }
            throw new IllegalArgumentException("No matching ingredient found for " + itemStack);
        }

        @Override
        public int hashCode()
        {
            return this.ingredients.hashCode();
        }
        @Override
        public boolean equals(Object obj)
        {
            if(this == obj)
                return true;
            if(!(obj instanceof Merged))
                return false;
            Merged that = (Merged) obj;
            return this.ingredients.equals(that.ingredients);
        }
    }

    class MatchingMzItem implements Ingredient
    {
        Identifier mzId;

        public MatchingMzItem(Identifier mzId)
        {
            this.mzId = mzId;
        }
        @Override
        public boolean test(ItemStack itemStack)
        {
            for(MzItem mzItem : RegistrarMzItem.instance.toMzItem(itemStack))
            {
                return this.mzId.equals(mzItem.getMzId());
            }
            return false;
        }
        @Override
        public Collection<ItemStack> getExamples()
        {
            return Collections.singleton(RegistrarMzItem.instance.newMzItem(this.mzId));
        }
        @Override
        public int hashCode()
        {
            return this.mzId.hashCode();
        }
        @Override
        public boolean equals(Object o)
        {
            if(this == o)
                return true;
            if(!(o instanceof MatchingMzItem))
                return false;
            MatchingMzItem that = (MatchingMzItem) o;
            return this.mzId.equals(that.mzId);
        }
    }

    static Builder builder()
    {
        return new Builder();
    }
    class Builder
    {
        Predicate<ItemStack> predicate;
        int count = 1;
        Collection<ItemStack> examples = Collections.emptyList();
        ItemStack remainder = ItemStack.EMPTY;
        public Builder()
        {
        }
        public Builder predicate(Predicate<ItemStack> value)
        {
            this.predicate = value;
            return this;
        }
        public Builder count(int value)
        {
            this.count = value;
            return this;
        }
        public Builder examples(Collection<ItemStack> value)
        {
            this.examples = value;
            return this;
        }
        public Builder examples(ItemStack... value)
        {
            return this.examples(Arrays.asList(value));
        }
        public Builder remainder(ItemStack value)
        {
            this.remainder = value;
            return this;
        }
        public Ingredient build()
        {
            return new Impl(this);
        }
        static class Impl implements Ingredient
        {
            Predicate<ItemStack> predicate;
            int count;
            Collection<ItemStack> examples;
            ItemStack remainder;
            public Impl(Builder builder)
            {
                if(builder.predicate == null)
                    throw new IllegalArgumentException("predicate cannot be null");
                this.predicate = builder.predicate;
                this.count = builder.count;
                this.examples = builder.examples;
                this.remainder = builder.remainder;
            }

            @Override
            public boolean test(ItemStack itemStack)
            {
                return this.predicate.test(itemStack);
            }
            @Override
            public Collection<ItemStack> getExamples()
            {
                return this.examples;
            }
            @Override
            public boolean isCountRequired()
            {
                return this.count != 1;
            }
            @Override
            public int getCount(ItemStack itemStack)
            {
                return this.count;
            }
            @Override
            public ItemStack getRemainder(ItemStack itemStack)
            {
                return this.remainder;
            }
        }
    }
}
