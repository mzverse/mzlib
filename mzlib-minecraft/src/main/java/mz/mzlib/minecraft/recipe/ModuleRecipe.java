package mz.mzlib.minecraft.recipe;

import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.VersionRange;
import mz.mzlib.minecraft.incomprehensible.profiler.Profiler;
import mz.mzlib.minecraft.incomprehensible.resource.ResourceManagerV1300;
import mz.mzlib.minecraft.incomprehensible.resource.ResourceReloaderV1300;
import mz.mzlib.minecraft.incomprehensible.resource.SinglePreparationResourceReloaderV1400;
import mz.mzlib.minecraft.recipe.smelting.ModuleFurnaceUpdateV_1300;
import mz.mzlib.minecraft.recipe.smelting.SmeltingManagerV_1300;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.nothing.Nothing;
import mz.mzlib.util.nothing.NothingInject;
import mz.mzlib.util.nothing.NothingInjectLocating;
import mz.mzlib.util.nothing.NothingInjectType;
import mz.mzlib.util.wrapper.WrapMethodFromBridge;
import mz.mzlib.util.wrapper.WrapSameClass;

import java.util.List;

public class ModuleRecipe extends MzModule
{
    public static ModuleRecipe instance = new ModuleRecipe();

    @Override
    public void onLoad()
    {
        final int version = MinecraftPlatform.instance.getVersion();
        // registrar
        if(version < 1200)
            this.register(new RegistrarRecipeVanillaV_1200());
        else if(version < 1300)
            this.register(new RegistrarRecipeVanillaV1200_1300());
        else if(version < 1400)
            this.register(new RegistrarRecipeVanillaV1300_1400());
        else if(version < 1800)
            this.register(new RegistrarRecipeVanillaV1400_2005());
        else if(version < 2005)
            this.register(new RegistrarRecipeVanillaV1800_2005());
        else if(version < 2102)
            this.register(new RegistrarRecipeVanillaV2005_2102());
        else
            this.register(new RegistrarRecipeVanillaV2102());
        this.register(RegistrarRecipeVanilla.instance.registrarDisabling);

        // nothing
        if(version < 1300)
        {
            this.register(NothingRecipeManagerV_1300.class);
            this.register(NothingSmeltingManagerV_1300.class);
        }
        else if(version < 1400)
            this.register(NothingRecipeManagerV1300_1400.class);
        else
            this.register(NothingRecipeManagerV1400.class);

        // module
        if(version < 1300)
            this.register(ModuleFurnaceUpdateV_1300.instance);

        this.register(RecipeMojangAbstract.Module.instance);
    }

    @VersionRange(end = 1300)
    @WrapSameClass(SmeltingManagerV_1300.class)
    public interface NothingSmeltingManagerV_1300 extends Nothing, SmeltingManagerV_1300
    {
        @NothingInject(
            wrapperMethodName = "<init>",
            wrapperMethodParams = {},
            locateMethod = "locateAllReturn",
            type = NothingInjectType.INSERT_BEFORE
        )
        default void static$of$end()
        {
            RegistrarRecipeVanilla.instance.markDirty();
        }
    }
    @VersionRange(end = 1300)
    @WrapSameClass(RecipeManager.class)
    public interface NothingRecipeManagerV_1300 extends Nothing, RecipeManager
    {
        static void locate$static$ofV_1200$begin(NothingInjectLocating locating)
        {
            locating.nextAccessWrapped(RecipeManager.class, "setRecipes0V_1200", List.class);
            locating.offset(1);
            if(locating.locations.size() != 1)
                throw new IllegalStateException();
        }
        @VersionRange(end = 1200)
        @NothingInject(
            wrapperMethodName = "<init>",
            wrapperMethodParams = {},
            locateMethod = "locateAllReturn",
            type = NothingInjectType.INSERT_BEFORE
        )
        default void static$ofV_1200$end()
        {
            RegistrarRecipeVanilla.instance.markDirty();
        }

        @VersionRange(begin = 1200)
        @NothingInject(
            wrapperMethodName = "static$setupV1200_1300",
            wrapperMethodParams = {},
            locateMethod = "locateAllReturn",
            type = NothingInjectType.INSERT_BEFORE
        )
        static void setupV1200_1300$end()
        {
            RegistrarRecipeVanilla.instance.markDirty();
        }
    }
    @VersionRange(begin = 1300, end = 1400)
    @WrapSameClass(RecipeManager.class)
    public interface NothingRecipeManagerV1300_1400 extends Nothing, RecipeManager, ResourceReloaderV1300
    {
        @NothingInject(
            wrapperMethodName = "reloadV_1400",
            wrapperMethodParams = { ResourceManagerV1300.class },
            locateMethod = "locateAllReturn",
            type = NothingInjectType.INSERT_BEFORE
        )
        default void reload$end()
        {
            RegistrarRecipeVanilla.instance.onReloadEnd(this);
        }
    }
    @VersionRange(begin = 1400)
    @WrapSameClass(RecipeManager.class)
    public interface NothingRecipeManagerV1400 extends Nothing, RecipeManager, SinglePreparationResourceReloaderV1400<Object>
    {
        @WrapMethodFromBridge(
            name = "applyV1400", params = { Object.class, ResourceManagerV1300.class, Profiler.class }
        )
        void apply$impl(Object prepared, ResourceManagerV1300 manager, Profiler profiler);

        @NothingInject(
            wrapperMethodName = "apply$impl",
            wrapperMethodParams = { Object.class, ResourceManagerV1300.class, Profiler.class },
            locateMethod = "locateAllReturn",
            type = NothingInjectType.INSERT_BEFORE
        )
        default void apply$impl$end()
        {
            RegistrarRecipeVanilla.instance.onReloadEnd(this);
        }
    }
}
