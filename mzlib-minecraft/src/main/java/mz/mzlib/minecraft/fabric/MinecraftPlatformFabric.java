package mz.mzlib.minecraft.fabric;

import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.minecraft.MzLibMinecraft;
import mz.mzlib.minecraft.entity.player.EntityPlayer;
import mz.mzlib.minecraft.mappings.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftPlatformFabric implements MinecraftPlatform
{
    public String versionString;
    public Integer version;
    public File mzLibJar;
    public File mzLibDataFolder;
    public Mappings<?> mappings;

    @Override
    public Set<String> getTags()
    {
        return Collections.singleton(Tag.FABRIC);
    }
    @Override
    public String getVersionString()
    {
        if(this.versionString != null)
            return this.versionString;
        this.versionString = FabricLoader.getInstance().getModContainer("minecraft")
            .orElseThrow(AssertionError::new).getMetadata().getVersion().getFriendlyString();
        Matcher matcher = Pattern.compile("(?<version>\\d+\\.\\d+(\\.\\d+)?)-alpha\\.(?<snapshot>\\d+)").matcher(this.versionString);
        if(matcher.matches())
            this.versionString = matcher.group("version") + "-snapshot-" + matcher.group("snapshot");
        return this.versionString;
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
        List<Path> paths = FabricLoader.getInstance().getModContainer(MzLibMinecraft.instance.MOD_ID)
            .orElseThrow(AssertionError::new).getOrigin().getPaths();
        assert paths.size() == 1;
        return this.mzLibJar = paths.get(0).toFile();
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
        if(this.getVersion() < 2601)
            this.mappings = new MinecraftMappingsFetcherYarn().fetch(getVersionString(), folder);
        else
            this.mappings = new MappingsPipe(
                new MinecraftMappingsFetcherMojang().fetch("1.21.11", folder),
                new MinecraftMappingsFetcherYarnIntermediary().fetch("1.21.11", folder),
                new MinecraftMappingsFetcherYarn().fetch("1.21.11", folder)
            );
        return this.mappings;
    }
}
