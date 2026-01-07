package mz.mzlib.minecraft;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestMinecraftPlatform
{
    @Test
    void test$parseVersion()
    {
        Assertions.assertEquals(808, MinecraftPlatform.parseVersion("1.8.8"));
        Assertions.assertEquals(1202, MinecraftPlatform.parseVersion("1.12.2"));
        Assertions.assertEquals(1600, MinecraftPlatform.parseVersion("1.16"));
        Assertions.assertEquals(2601, MinecraftPlatform.parseVersion("26.1"));
        Assertions.assertEquals(2601, MinecraftPlatform.parseVersion("26.1-snapshot-1"));
        Assertions.assertEquals(2601, MinecraftPlatform.parseVersion("26.1.1"));
    }
}
