#import "/lib/lib.typ": *;
#let title = [实体系统];
#show: template.with(title: title);

实体系统提供了对 Minecraft 实体的操作和管理功能。

= Entity

`Entity` 是实体的基类。

```java
// 获取实体 ID
int id = entity.getId();

// 获取 UUID
UUID uuid = entity.getUuid();

// 获取位置
double x = entity.getX();
double y = entity.getY();
double z = entity.getZ();

// 获取世界
World world = entity.getWorld();

// 获取实体类型
EntityType type = entity.getType();

// 移除实体
entity.remove();

// 传送实体
entity.teleport(x, y, z);
```

= EntityLiving

`EntityLiving` 是生物实体，继承自 `Entity`。

```java
// 获取生命值
float health = living.getHealth();

// 获取最大生命值
float maxHealth = living.getMaxHealth();

// 设置生命值
living.setHealth(20);

// 伤害实体
living.damage(damageSource, 5);

// 检查是否存活
boolean alive = living.isAlive();

// 获取手持物品
ItemStack mainHand = living.getMainHandStack();
ItemStack offHand = living.getOffHandStack();
```

= EntityPlayer

`EntityPlayer` 是玩家实体，继承自 `EntityLiving`。

```java
// 获取玩家名称
String name = player.getName();

// 获取 UUID
UUID uuid = player.getUuid();

// 获取游戏模式
GameMode gameMode = player.getGameMode();

// 设置游戏模式
player.setGameMode(GameMode.CREATIVE);

// 发送消息
player.sendMessage(Text.literal("Hello!"));

// 获取库存
InventoryPlayer inventory = player.getInventory();

// 获取能力
Abilities abilities = player.getAbilities();

// 检查是否为 OP
boolean op = player.hasPermissionLevel(4);

// 踢出玩家
player.disconnect(Text.literal("Kicked"));
```

= EntityType

`EntityType` 表示实体类型。

```java
// 获取实体类型 ID
Identifier id = entityType.getId();

// 获取实体类型名称
String name = entityType.getName();

// 创建实体
Entity entity = entityType.create(world);

// 检查实体类型
boolean isPlayer = entityType == EntityType.PLAYER;
```

= EntityItem

`EntityItem` 是掉落物实体。

```java
// 获取物品堆叠
ItemStack stack = itemEntity.getStack();

// 设置物品堆叠
itemEntity.setStack(stack);

// 获取拾取延迟
int pickupDelay = itemEntity.getPickupDelay();

// 设置拾取延迟
itemEntity.setPickupDelay(10);

// 设置不可拾取
itemEntity.setPickupDelay(32767);
```

= EntityDataTracker

`EntityDataTracker` 追踪实体的数据。

```java
// 获取数据追踪器
EntityDataTracker tracker = entity.getDataTracker();

// 创建数据键
EntityDataKey<Integer> key = EntityDataKey.of(tracker, 0, Integer.class);

// 获取数据
Option<Integer> value = tracker.get(key);

// 设置数据
tracker.set(key, 42);

// 标记数据为脏
tracker.markDirty();
```

= DamageSource

`DamageSource` 表示伤害来源。

```java
// 获取伤害类型
String type = damageSource.getName();

// 获取造成伤害的实体
Option<Entity> source = damageSource.getSource();

// 获取直接造成伤害的实体
Option<Entity> attacker = damageSource.getAttacker();

// 检查是否由玩家造成
boolean isPlayer = damageSource.isPlayer();

// 检查是否由生物造成
boolean isLiving = damageSource.isOf(DamageType.PLAYER_ATTACK);
```

= DisplayEntity

`DisplayEntity` 是显示实体（1.19+）。

```java
// 创建显示实体
DisplayEntity display = DisplayEntity.create(world);

// 设置变换
display.setTransformation(transformation);

// 设置插值持续时间
display.setInterpolationDuration(10);

// 设置插值延迟
display.setInterpolationDelay(0);

// 设置发光
display.setGlowing(true);

// 设置阴影
display.setShadowRadius(0.5f);

// 设置可见范围
display.setViewRange(0.5f);
```

= 使用示例

```java
public class EntityExample
{
    // 创建掉落物
    public void spawnDropItem(World world, double x, double y, double z, ItemStack stack)
    {
        EntityItem item = new EntityItem(world, x, y, z, stack);
        world.spawnEntity(item);
    }

    // 伤害玩家
    public void damagePlayer(EntityPlayer player, float amount)
    {
        DamageSource source = DamageSource.GENERIC;
        player.damage(source, amount);
    }

    // 传送玩家
    public void teleportPlayer(EntityPlayer player, double x, double y, double z)
    {
        player.teleport(x, y, z);
    }

    // 给玩家物品
    public void giveItem(EntityPlayer player, ItemStack stack)
    {
        player.getInventory().offerOrDrop(stack);
    }
}
```

= 注意事项

- 实体操作需要在主线程执行
- 使用 `EntityPlayer` 而不是 `Player` 处理在线玩家
- 实体 ID 在世界内唯一
- 移除实体后不能再使用该实体