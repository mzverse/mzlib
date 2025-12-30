package mz.mzlib.minecraft.recipe;

import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.MinecraftServer;
import mz.mzlib.module.IRegistrar;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.Instance;
import mz.mzlib.util.Option;
import mz.mzlib.util.RuntimeUtil;

import java.util.*;

/**
 * @see ModuleRecipe
 */
public abstract class RegistrarRecipeVanilla implements IRegistrar<RecipeRegistration<?>>, Instance
{
    public static RegistrarRecipeVanilla instance;

    @Override
    public Class<RecipeRegistration<?>> getType()
    {
        return RuntimeUtil.castClass(RecipeRegistration.class);
    }


    public boolean isRegistrable(RecipeType type)
    {
        return type instanceof RecipeType.V_1400 || type instanceof RecipeTypeV1400;
    }

    @Override
    public boolean isRegistrable(RecipeRegistration<?> object)
    {
        return this.isRegistrable(object.getRecipe().getType());
    }

    public RegistrarDisabling registrarDisabling = this.new RegistrarDisabling();
    public class RegistrarDisabling implements IRegistrar<RecipeDisabling>
    {
        @Override
        public Class<RecipeDisabling> getType()
        {
            return RecipeDisabling.class;
        }
        @Override
        public boolean isRegistrable(RecipeDisabling object)
        {
            return RegistrarRecipeVanilla.this.isRegistrable(object.getType());
        }

        Map<RecipeType, Map<Identifier, Set<RecipeDisabling>>> disablings = new HashMap<>();

        @Override
        public void register(MzModule module, RecipeDisabling object)
        {
            Set<RecipeDisabling> set = this.disablings.computeIfAbsent(object.type, k -> new HashMap<>()).computeIfAbsent(object.id, k -> new HashSet<>());
            for(Recipe recipe : Option.fromOptional(set.stream().findAny()).flatMap(RecipeDisabling::getRecipe))
                object.setRecipe(recipe);
            set.add(object);
            RegistrarRecipeVanilla.this.markDirty();
        }
        @Override
        public void unregister(MzModule module, RecipeDisabling object)
        {
            this.disablings.computeIfPresent(object.type, (type, map) ->
            {
                map.computeIfPresent(object.id, (id, set) ->
                {
                    set.remove(object);
                    return set.isEmpty() ? null : set;
                });
                return map.isEmpty() ? null : map;
            });
            RegistrarRecipeVanilla.this.markDirty();
        }
    }

    Set<RecipeRegistration<?>> recipeRegistrations = new HashSet<>();
    Map<RecipeType, Map<Identifier, Recipe>> originalRecipes;
    Map<RecipeType, Map<Identifier, Recipe>> enabledRecipes = Collections.emptyMap();

    public Map<RecipeType, Map<Identifier, Recipe>> getEnabledRecipes()
    {
        return this.enabledRecipes;
    }

    public Map<RecipeType, Map<Identifier, Set<RecipeDisabling>>> getDisablings()
    {
        return this.registrarDisabling.disablings;
    }

    public Map<RecipeType, Map<Identifier, Recipe>> getOriginalRecipes()
    {
        if(this.originalRecipes == null)
            this.updateOriginal();
        return this.originalRecipes;
    }

    boolean isDirty = false;

    public synchronized void markDirty()
    {
        if(this.isDirty)
            return;
        this.isDirty = true;
        MinecraftServer.instance.schedule(this::flush);
    }

    public synchronized void flush()
    {
        this.isDirty = false;
        Map<RecipeType, Map<Identifier, Recipe>> enabledRecipes = new HashMap<>(this.getOriginalRecipes());
        for(Map.Entry<RecipeType, Map<Identifier, Recipe>> entry : enabledRecipes.entrySet())
        {
            entry.setValue(new HashMap<>(entry.getValue()));
        }
        for(RecipeRegistration<?> r : this.recipeRegistrations)
        {
            enabledRecipes.computeIfAbsent(r.getRecipe().getType(), k -> new HashMap<>())
                .put(r.getId(), r.getRecipe());
        }
        for(Map.Entry<RecipeType, Map<Identifier, Recipe>> entry : enabledRecipes.entrySet())
        {
            for(Iterator<Map.Entry<Identifier, Recipe>> i = entry.getValue().entrySet().iterator(); i.hasNext(); )
            {
                Map.Entry<Identifier, Recipe> e = i.next();
                boolean flag = false;
                for(RecipeDisabling disabling : this.registrarDisabling.disablings.getOrDefault(entry.getKey(), Collections.emptyMap()).getOrDefault(e.getKey(), Collections.emptySet()))
                {
                    disabling.setRecipe(e.getValue());
                    flag = true;
                }
                if(flag)
                    i.remove();
            }
        }
        for(Map.Entry<RecipeType, Map<Identifier, Recipe>> e : enabledRecipes.entrySet())
        {
            e.setValue(Collections.unmodifiableMap(e.getValue()));
        }
        this.enabledRecipes = Collections.unmodifiableMap(enabledRecipes);
    }

    protected abstract void updateOriginal();

    protected void onReloadEnd()
    {
        this.updateOriginal();
        this.flush();
    }

    @Override
    public synchronized void register(MzModule module, RecipeRegistration<?> object)
    {
        this.recipeRegistrations.add(object);
        this.markDirty();
    }
    @Override
    public synchronized void unregister(MzModule module, RecipeRegistration<?> object)
    {
        this.recipeRegistrations.remove(object);
        this.markDirty();
    }
}
