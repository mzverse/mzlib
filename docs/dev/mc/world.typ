#import "/lib/lib.typ": *;
#let title = [世界系统];
#show: template.with(title: title);

世界系统提供了对游戏世界的操作功能。

= World

`World` 是世界接口。

```java
// 获取世界类型
Identifier dimension = world.getDimension();

// 获取难度
Difficulty difficulty = world.getDifficulty();

// 设置时间
world.setTime(time);

// 获取时间
long time = world.getTime();

// 设置天气
world.setRaining(true);
world.setThundering(true);

// 获取玩家
Iterable<EntityPlayer> players = world.getPlayers();

// 获取实体
Iterable<Entity> entities = world.getEntities();

// 获取方块
BlockState blockState = world.getBlockState(pos);

// 设置方块
world.setBlockState(pos, blockState);

// 生成粒子
world.addParticle(particleType, pos, velocity, count);

// 播放声音
world.playSound(player, pos, soundEvent, soundCategory, volume, pitch);

// 通知方块更新
world.notifyBlockUpdate(pos, oldState, newState, flags);
```

= WorldServer

`WorldServer` 是服务端世界。

```java
// 获取世界 ID
String worldId = worldServer.getWorldId();

// 保存世界
worldServer.save();

// 获取时间
long dayTime = worldServer.getDayTime();

// 设置白天/黑夜
worldServer.setDayTime(dayTime);

// 生成结构
worldServer.generateFeature(pos, feature);

// 获取区块
Chunk chunk = worldServer.getChunk(chunkPos);

// 加载区块
boolean loaded = worldServer.loadChunk(chunkX, chunkZ);

// 卸载区块
worldServer.unloadChunk(chunkX, chunkZ);
```

= 使用示例

```java
public class WorldExample
{
    // 在玩家位置生成粒子
    public void spawnParticles(EntityPlayer player)
    {
        World world = player.getWorld();
        Vec3d pos = player.getPos();

        world.addParticle(
            ParticleTypes.HEART,
            pos.x, pos.y + 2.0, pos.z,
            0.0, 0.0, 0.0,
            1
        );
    }

    // 在指定位置播放声音
    public void playSound(World world, BlockPos pos)
    {
        world.playSound(
            null,  // player
            pos,
            SoundEvents.BLOCK_STONE_BREAK,
            SoundSource.BLOCKS,
            1.0f,  // volume
            1.0f   // pitch
        );
    }

    // 获取所有玩家
    public void getAllPlayers(WorldServer worldServer)
    {
        for (EntityPlayer player : worldServer.getPlayers())
        {
            String name = player.getName();
            // 处理玩家
        }
    }
}
```

= 注意事项

- 世界操作需要考虑线程安全
- 某些操作需要在主线程执行
- 使用 `schedule` 在主线程执行任务