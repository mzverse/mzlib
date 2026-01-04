package mz.mzlib.minecraft.mappings;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import mz.mzlib.minecraft.MinecraftPlatform;
import mz.mzlib.util.LazyConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * get: https://hub.spigotmc.org/versions/%version%.json
 * commit: parseJson -> refs -> BuildData
 * <p>
 * get https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/info.json?at=%commit%
 * parseJson ->
 * accessTransformName: fileName
 * classMappingsName: fileName
 * memberMappingsName: fileName
 * <p>
 * accessTransform: get: https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/mappings/%accessTransformName%?at=%commit%
 * classMappings: get: https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/mappings/%classMappingsName%?at=%commit%
 * memberMappings: get: https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/mappings/%memberMappingsName%?at=%commit%
 */
public class MinecraftMappingsFetcherSpigot
{
    public String baseUrl = "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/";

    public Mappings<?> fetch(String version, String protocolVersion, File cacheFolder)
    {
        LazyConstant<String> refsGetter = LazyConstant.of(() -> Objects.requireNonNull(new Gson().fromJson(
            MappingsUtil.request(MappingsUtil.url("https://hub.spigotmc.org/versions/" + version + ".json")),
            JsonObject.class
        )).getAsJsonObject("refs").get("BuildData").getAsString());
        LazyConstant<JsonObject> infoGetter = LazyConstant.of(() -> new Gson().fromJson(
            MappingsUtil.request(MappingsUtil.url(baseUrl + "info.json" + "?at=" + refsGetter.get())),
            JsonObject.class
        ));

        MappingsByMap raw = MappingsByMap.parseCsrg(
            MappingsUtil.cache(
                Optional.ofNullable(cacheFolder).map(it -> new File(new File(it, "Spigot"), version + ".csrg"))
                    .orElse(null), () -> MappingsUtil.request(MappingsUtil.url(
                    baseUrl + "mappings/" + infoGetter.get().get("classMappings").getAsString() + "?at=" +
                        refsGetter.get()))
            ), MappingsUtil.cache(
                Optional.ofNullable(cacheFolder).map(it -> new File(new File(it, "Spigot"), version + "-members.csrg"))
                    .orElse(null), () -> Optional.ofNullable(infoGetter.get().get("memberMappings")).map(
                    it -> MappingsUtil.request(
                        baseUrl + "mappings/" + infoGetter.get().get("memberMappings").getAsString() + "?at=" +
                            refsGetter.get())).orElse("")
            )
        );
        raw.classes.put("net.minecraft.server.MinecraftServer", "net.minecraft.server.MinecraftServer");
        List<Mappings<?>> result = new ArrayList<>();
        if(MinecraftPlatform.parseVersion(version) < 1700)
        {
            result.add(new MappingsPackage("net.minecraft.server." + protocolVersion, null));
            MappingsByMap mcs = new MappingsByMap();
            mcs.classes.put("MinecraftServer", "net.minecraft.server.MinecraftServer");
            result.add(mcs);
        }
        if(version.equals("1.16.5"))
        {
            MappingsByMap mappingsPackage = new MappingsByMap();
            for(String cn : raw.classes.keySet())
            {
                int i = cn.lastIndexOf('.');
                if(i != -1)
                    mappingsPackage.classes.put(cn.substring(i + 1), cn);
            }
            result.add(mappingsPackage);
        }
        result.add(raw);
        return new MappingsPipe(result);
    }
}
