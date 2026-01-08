package mz.mzlib.minecraft.neoforge;

import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.MzLibMinecraft;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.mappings.*;
import mz.mzlib.util.RuntimeUtil;
import net.neoforged.fml.ModList;

import java.io.File;
import java.util.Collections;
import java.util.Set;

public class MinecraftPlatformNeoForge implements MinecraftPlatform
{
    public String versionString;
    public Integer version;
    public File mzLibJar;
    public File mzLibDataFolder;
    public Mappings<?> mappings;

    @Override
    public Set<String> getTags()
    {
        return Collections.singleton(Tag.NEOFORGE);
    }
    @Override
    public String getVersionString()
    {
        if(this.versionString != null)
            return this.versionString;
        return this.versionString = ModList.get().getModContainerById("minecraft").orElseThrow(AssertionError::new)
            .getModInfo().getVersion().toString();
    }
    @Override
    public int getVersion()
    {
        if(this.version != null)
            return this.version;
        return this.version = MinecraftPlatform.parseVersion(this.getVersionString());
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
        if(this.mzLibJar != null)
            return this.mzLibJar;
        return this.mzLibJar = ModList.get().getModContainerById(MzLibMinecraft.instance.MOD_ID).orElseThrow(AssertionError::new)
            .getModInfo().getOwningFile().getFile().getFilePath().toFile();
    }
    @Override
    public File getMzLibDataFolder()
    {
        if(this.mzLibDataFolder != null)
            return this.mzLibDataFolder;
        return this.mzLibDataFolder = new File(this.getMzLibJar().getParentFile(), "MzLib");
    }

    @Override
    public Mappings<?> getMappings()
    {
        if(this.mappings != null)
            return this.mappings;
        File folder = new File(getMzLibDataFolder(), "mappings");
        if(this.getVersion() >= 2601)
            return this.mappings = MinecraftPlatform.getMappingsV2601(folder);
        try
        {
            String versionString = this.getVersionString();
            return this.mappings = new MappingsPipe(
                new MinecraftMappingsFetcherMojang().fetch(versionString, folder),
                new MinecraftMappingsFetcherYarnIntermediary().fetch(versionString, folder),
                new MinecraftMappingsFetcherYarn().fetch(versionString, folder)
            );
        }
        catch(Throwable e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }
}
