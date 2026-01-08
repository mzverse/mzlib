package mz.mzlib.minecraft.bukkit;

import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.bukkit.entity.BukkitEntityUtil;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.fabric.MinecraftPlatformFabric;
import mz.mzlib.minecraft.mappings.*;
import mz.mzlib.util.Option;
import mz.mzlib.util.RuntimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class MinecraftPlatformBukkit implements MinecraftPlatform
{
    public static MinecraftPlatformBukkit instance = new MinecraftPlatformBukkit();

    public Option<MinecraftPlatformFabric> fabric;

    Set<String> tags = new HashSet<>(Collections.singleton(Tag.BUKKIT));
    @Override
    public Set<String> getTags()
    {
        return this.tags;
    }
    @Override
    public String getLanguage(EntityPlayer player)
    {
        if(this.getVersion() < 1200)
            return this.getLanguageV_1200(player);
        else
            return this.getLanguageV1200(player);
    }
    @SuppressWarnings("deprecation")
    public String getLanguageV_1200(EntityPlayer player)
    {
        return ((Player) BukkitEntityUtil.toBukkit(player)).spigot().getLocale();
    }
    public String getLanguageV1200(EntityPlayer player)
    {
        return ((Player) BukkitEntityUtil.toBukkit(player)).getLocale();
    }

    public String protocolVersion;

    {
        if(RuntimeUtil.runAndCatch(() -> Class.forName("net.fabricmc.api.ModInitializer")) == null)
        {
            this.tags.add(Tag.FABRIC);
            this.fabric = Option.some(new MinecraftPlatformFabric()
            {
                @Override
                public String getVersionString()
                {
                    return MinecraftPlatformBukkit.this.getVersionString();
                }
                @Override
                public String getLanguage(EntityPlayer player)
                {
                    return MinecraftPlatformBukkit.this.getLanguage(player);
                }
                @Override
                public File getMzLibJar()
                {
                    return MinecraftPlatformBukkit.this.getMzLibJar();
                }
                @Override
                public File getMzLibDataFolder()
                {
                    return MinecraftPlatformBukkit.this.getMzLibDataFolder();
                }
            });
        }
        else
            this.fabric = Option.none();
        this.isPaper = RuntimeUtil.runAndCatch(() -> Class.forName("com.destroystokyo.paper.PaperConfig")) == null ||
            RuntimeUtil.runAndCatch(() -> Class.forName("io.papermc.paper.configuration.Configuration")) == null;
        if(this.isPaper)
            this.tags.add(Tag.PAPER);
        String packageName = Bukkit.getServer().getClass().getPackage().getName()
            .substring("org.bukkit.craftbukkit".length());
        protocolVersion = packageName.isEmpty() ? null : packageName.substring(".".length());
    }

    public String versionString;
    public int version;

    {
        this.versionString = Bukkit.getBukkitVersion().split("-")[0];
        this.version = MinecraftPlatform.parseVersion(this.versionString);
    }

    public boolean isPaper;
    public boolean isPaper()
    {
        return this.isPaper;
    }

    @Override
    public String getVersionString()
    {
        return versionString;
    }
    @Override
    public int getVersion()
    {
        return version;
    }

    @Override
    public File getMzLibJar()
    {
        return MzLibBukkitPlugin.instance.getFile();
    }
    @Override
    public File getMzLibDataFolder()
    {
        return MzLibBukkitPlugin.instance.getDataFolder();
    }

    public Mappings<?> mappings;

    @Override
    public Mappings<?> getMappings()
    {
        for(MinecraftPlatformFabric minecraftPlatformFabric : this.fabric)
        {
            return minecraftPlatformFabric.getMappings();
        }
        if(this.mappings != null)
            return this.mappings;
        File folder = new File(getMzLibDataFolder(), "mappings");
        if(this.getVersion() >= 2601)
            return this.mappings = MinecraftPlatform.getMappingsV2601(folder);
        try
        {
            List<Mappings<?>> result = new ArrayList<>();
            if(this.isPaper() && this.getVersion() >= 2005)
                result.add(new MinecraftMappingsFetcherMojang().fetch(getVersionString(), folder));
            else
                result.add(
                    new MinecraftMappingsFetcherSpigot().fetch(getVersionString(), this.protocolVersion, folder));
            if(this.getVersion() < 1400)
            {
                // = new MinecraftMappingsFetcherLegacyYarnIntermediary().fetch(getVersionString(), folder);
                // TODO
                result.add(new MinecraftMappingsFetcherLegacyYarn().fetch(getVersionString(), folder));
            }
            else
            {
                result.add(new MinecraftMappingsFetcherYarnIntermediary().fetch(getVersionString(), folder));
                result.add(new MinecraftMappingsFetcherYarn().fetch(getVersionString(), folder));
            }
            return this.mappings = new MappingsPipe(result);
        }
        catch(Throwable e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }
}
