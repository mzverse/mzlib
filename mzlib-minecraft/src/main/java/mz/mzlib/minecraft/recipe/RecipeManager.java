package mz.mzlib.minecraft.recipe;

import com.google.common.collect.Multimap;
import mz.mzlib.minecraft.Identifier;
import mz.mzlib.minecraft.MinecraftServer;
import mz.mzlib.minecraft.VersionName;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.incomprehensible.resource.FeatureSetV1903;
import mz.mzlib.minecraft.registry.RegistrySimple;
import mz.mzlib.minecraft.wrapper.WrapMinecraftClass;
import mz.mzlib.minecraft.wrapper.WrapMinecraftFieldAccessor;
import mz.mzlib.minecraft.wrapper.WrapMinecraftMethod;
import mz.mzlib.util.FunctionInvertible;
import mz.mzlib.util.proxy.ListProxy;
import mz.mzlib.util.proxy.MapProxy;
import mz.mzlib.util.wrapper.SpecificImpl;
import mz.mzlib.util.wrapper.WrapConstructor;
import mz.mzlib.util.wrapper.WrapperFactory;
import mz.mzlib.util.wrapper.WrapperObject;

import java.util.List;
import java.util.Map;

@WrapMinecraftClass({
    @VersionName(name = "net.minecraft.recipe.RecipeDispatcher", end = 1400),
    @VersionName(name = "net.minecraft.recipe.RecipeManager", begin = 1400, end = 2102),
    @VersionName(name = "net.minecraft.recipe.ServerRecipeManager", begin = 2102)
})
public interface RecipeManager extends WrapperObject
{
    WrapperFactory<RecipeManager> FACTORY = WrapperFactory.of(RecipeManager.class);

    static RecipeManager getInstance()
    {
        return FACTORY.getStatic().static$getInstance();
    }


    RecipeManager static$getInstance();
    @SpecificImpl("static$getInstance")
    @VersionRange(end = 1200)
    default RecipeManager static$getInstance$implV_1200()
    {
        return getInstanceV_1200();
    }
    @SpecificImpl("static$getInstance")
    @VersionRange(begin = 1200, end = 1300)
    default RecipeManager static$getInstance$implV1200_1300()
    {
        return FACTORY.getStatic();
    }
    @SpecificImpl("static$getInstance")
    @VersionRange(begin = 1300)
    default RecipeManager static$getInstance$implV1300()
    {
        return MinecraftServer.instance.getRecipeManagerV1300();
    }

    @VersionRange(end = 1200)
    static RecipeManager ofV_1200()
    {
        return FACTORY.getStatic().static$ofV_1200();
    }

    @VersionRange(end = 1200)
    static RecipeManager getInstanceV_1200()
    {
        return FACTORY.getStatic().static$getInstanceV_1200();
    }
    default List<RecipeMojang> getRecipesV_1200()
    {
        return new ListProxy<>(this.getRecipes0V_1200(), FunctionInvertible.wrapper(RecipeMojang.FACTORY));
    }
    @VersionRange(end = 1200)
    @WrapMinecraftFieldAccessor(@VersionName(name = "recipes"))
    List<Object> getRecipes0V_1200();
    @VersionRange(end = 1200)
    @WrapMinecraftFieldAccessor(@VersionName(name = "recipes"))
    void setRecipes0V_1200(List<Object> value);

    @VersionRange(begin = 1200, end = 1300)
    static RegistrySimple<?> getRegistryV1200_1300()
    {
        return FACTORY.getStatic().static$getRegistryV1200_1300();
    }
    @VersionRange(begin = 1200, end = 1300)
    static void setRegistryV1200_1300(RegistrySimple<?> value)
    {
        FACTORY.getStatic().static$setRegistryV1200_1300(value);
    }

    @VersionRange(begin = 1200, end = 1300)
    static boolean setupV1200_1300()
    {
        return FACTORY.getStatic().static$setupV1200_1300();
    }
    @VersionRange(begin = 1200, end = 1300)
    static void registerV1200_1300(Identifier id, RecipeMojang recipe)
    {
        FACTORY.getStatic().static$registerV1200_1300(id, recipe);
    }

    default Map<Identifier, RecipeMojang> getRecipesV1300_1400()
    {
        return new MapProxy<>(this.getRecipes0V1300_1400(), FunctionInvertible.wrapper(Identifier.FACTORY), FunctionInvertible.wrapper(
            RecipeMojang.FACTORY));
    }
    @VersionRange(begin = 1300, end = 1400)
    @WrapMinecraftFieldAccessor(@VersionName(name = "field_17445"))
    Map<Object, Object> getRecipes0V1300_1400();
    @VersionRange(begin = 1300, end = 1400)
    @WrapMinecraftFieldAccessor(@VersionName(name = "field_17445"))
    void setRecipes0V1300_1400(Map<Object, Object> value);
    @VersionRange(begin = 1300, end = 1400)
    @WrapMinecraftMethod(@VersionName(name = "method_16205"))
    void registerV1300_1400(RecipeMojang recipe);

    @VersionRange(begin = 1400, end = 2005)
    @WrapMinecraftFieldAccessor(@VersionName(name = "recipes"))
    Map<Object, Map<Object, Object>> getRecipes0V1400_2005();
    @VersionRange(begin = 1400, end = 2005)
    @WrapMinecraftFieldAccessor(@VersionName(name = "recipes"))
    void setRecipes0V1400_2005(Map<Object, Map<Object, Object>> value);

    @VersionRange(begin = 2005, end = 2102)
    @WrapMinecraftFieldAccessor(@VersionName(name = "recipesByType"))
    Multimap<Object, Object> getTypeRecipes0V2005_2102();
    @VersionRange(begin = 2005, end = 2102)
    @WrapMinecraftFieldAccessor(@VersionName(name = "recipesByType"))
    void setTypeRecipes0V2005_2102(Multimap<Object, Object> value);

    @VersionRange(begin = 1800, end = 2102)
    @WrapMinecraftFieldAccessor(@VersionName(name = "recipesById"))
    Map<Object, Object> getIdRecipes0V1800_2102();
    @VersionRange(begin = 1800, end = 2102)
    @WrapMinecraftFieldAccessor(@VersionName(name = "recipesById"))
    void setIdRecipes0V1800_2102(Map<Object, Object> value);

    @VersionRange(begin = 2102)
    @WrapMinecraftFieldAccessor(@VersionName(name = "preparedRecipes"))
    PreparedRecipesV2102 getPreparedRecipesV2102();
    @VersionRange(begin = 2102)
    @WrapMinecraftFieldAccessor(@VersionName(name = "preparedRecipes"))
    void setPreparedRecipesV2102(PreparedRecipesV2102 value);

    @VersionRange(begin = 2102)
    @WrapMinecraftMethod(@VersionName(name = "initialize"))
    void initializeV2102(FeatureSetV1903 features);


    @VersionRange(end = 1200)
    @WrapConstructor
    RecipeManager static$ofV_1200();

    @VersionRange(end = 1200)
    @WrapMinecraftMethod(@VersionName(name = "getInstance"))
    RecipeManager static$getInstanceV_1200();

    @VersionRange(begin = 1200, end = 1300)
    @WrapMinecraftFieldAccessor(@VersionName(name = "REGISTRY"))
    RegistrySimple<?> static$getRegistryV1200_1300();
    @VersionRange(begin = 1200, end = 1300)
    @WrapMinecraftFieldAccessor(@VersionName(name = "REGISTRY"))
    void static$setRegistryV1200_1300(RegistrySimple<?> value);

    @VersionRange(begin = 1200, end = 1300)
    @WrapMinecraftMethod(@VersionName(name = "setup"))
    boolean static$setupV1200_1300();

    @VersionRange(begin = 1200, end = 1300)
    @WrapMinecraftMethod(@VersionName(name = "method_14260"))
    void static$registerV1200_1300(Identifier id, RecipeMojang recipe);
}
