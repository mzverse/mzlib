package mz.mzlib.minecraft.vanilla;

import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.mappings.*;
import mz.mzlib.util.RuntimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MinecraftPlatformVanilla implements MinecraftPlatform
{
    public String versionString;
    public Integer version;
    public File mzLibJar;
    public File mzLibDataFolder;
    public Mappings<?> mappings;

    public MinecraftPlatformVanilla(String versionString)
    {
        this.versionString = versionString;
        this.version = MinecraftPlatform.parseVersion(versionString);
        this.mzLibJar = new File(System.getProperty("java.class.path"));
        this.mzLibDataFolder = new File(this.mzLibJar.getParentFile(), "MzLib");
    }

    @Override
    public Set<String> getTags()
    {
        return Collections.singleton(Tag.NEOFORGE);
    }
    @Override
    public String getVersionString()
    {
        return this.versionString;
    }
    @Override
    public int getVersion()
    {
        return this.version;
    }
    @Override
    public String getLanguage(EntityPlayer player)
    {
        // TODO
        return "zh_cn";
    }
    @Override
    public File getMzLibJar()
    {
        return this.mzLibJar;
    }
    @Override
    public File getMzLibDataFolder()
    {
        return this.mzLibDataFolder;
    }

    @Override
    public Mappings<?> getMappings()
    {
        if(this.mappings != null)
            return this.mappings;
        try
        {
            File folder = new File(getMzLibDataFolder(), "mappings");
            List<Mappings<?>> result = new ArrayList<>();
            result.add(new MinecraftMappingsFetcherYarnIntermediary().fetch(getVersionString(), folder));
            result.add(new MinecraftMappingsFetcherYarn().fetch(getVersionString(), folder));
            return this.mappings = new MappingsPipe(result);
        }
        catch(Throwable e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }
}
