package mz.mzlib.minecraft;

import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.mappings.Mappings;
import mz.mzlib.util.ElementSwitcher;
import mz.mzlib.util.ElementSwitcherClass;
import mz.mzlib.util.Instance;
import mz.mzlib.util.RuntimeUtil;

import java.io.File;
import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Set;

public interface MinecraftPlatform extends Instance
{
    MinecraftPlatform instance = RuntimeUtil.nul();

    String getVersionString();

    int getVersion();

    Set<String> getTags();

    default boolean inVersion(int begin, int end)
    {
        return this.getVersion() >= begin && this.getVersion() < end;
    }

    default boolean inVersion(VersionRange range)
    {
        return this.inVersion(range.begin(), range.end());
    }

    default boolean inVersion(VersionName name)
    {
        return this.inVersion(name.begin(), name.end());
    }

    String getLanguage(EntityPlayer player);

    File getMzLibJar();

    File getMzLibDataFolder();

    Mappings<?> getMappings();

    static int parseVersion(String version)
    {
        String[] versions = version.split("\\.", -1);
        return Integer.parseInt(versions[1]) * 100 + (versions.length > 2 ? Integer.parseInt(versions[2]) : 0);
    }

    class Tag
    {
        public static final String FABRIC = "fabric";
        public static final String NEOFORGE = "neoforge";
        public static final String BUKKIT = "bukkit";
        public static final String PAPER = "paper";
        public static final String FOLIA = "folia";
    }

    // TODO

    /**
     * Enabled on the matching platform
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD })
    @Repeatable(Enableds.class)
    @ElementSwitcherClass(Enabled.Handler.class)
    @interface Enabled
    {
        /**
         * Match platforms that contain all the tags
         *
         * @return the tags
         */
        String[] value();

        class Handler implements ElementSwitcher<Enabled>
        {
            @Override
            public boolean isEnabled(Enabled annotation, AnnotatedElement element)
            {
                return MinecraftPlatform.instance.getTags().containsAll(Arrays.asList(annotation.value()));
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD })
    @ElementSwitcherClass(Enableds.Handler.class)
    @interface Enableds
    {
        Enabled[] value();

        class Handler implements ElementSwitcher<Enableds>
        {
            @Override
            public boolean isEnabled(Enableds annotation, AnnotatedElement element)
            {
                for(Enabled enabled : annotation.value())
                {
                    if(new Enabled.Handler().isEnabled(enabled, element))
                        return true;
                }
                return false;
            }
        }
    }

    /**
     * Disabled on the matching platform
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD })
    @Repeatable(Disableds.class)
    @ElementSwitcherClass(Disabled.Handler.class)
    @interface Disabled
    {
        /**
         * Match platforms that contain all the tags
         *
         * @return the tags
         */
        String[] value();

        class Handler implements ElementSwitcher<Disabled>
        {
            @Override
            public boolean isEnabled(Disabled annotation, AnnotatedElement element)
            {
                return !MinecraftPlatform.instance.getTags().containsAll(Arrays.asList(annotation.value()));
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD })
    @ElementSwitcherClass(Disableds.Handler.class)
    @interface Disableds
    {
        Disabled[] value();

        class Handler implements ElementSwitcher<Disableds>
        {
            @Override
            public boolean isEnabled(Disableds annotation, AnnotatedElement element)
            {
                for(Disabled enabled : annotation.value())
                {
                    if(!new Disabled.Handler().isEnabled(enabled, element))
                        return false;
                }
                return true;
            }
        }
    }
}
