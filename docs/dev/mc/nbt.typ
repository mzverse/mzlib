#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = [NBT];
#show: template.with(title: title);


#set enum(spacing: 2em);

类似Json，NBT是一种可嵌套的数据结构，基类是`NbtElement`

/ #[= 基本类型]:

    - `NbtByte`

    - `NbtShort`

    - `NbtInt`

    - `NbtLong`

    - `NbtFloat`

    - `NbtDouble`

    - `NbtString`

    `boolean`一般转换为`byte`储存(`1`或`0`)

    调用对应类的`newInstance`方法来创建nbt

    ```java
    NbtByte nbt1 = NbtByte.newInstance((byte)114);
    NbtByte nbt2 = NbtByte.newInstance(true);
    NbtString nbt3 = NbtString.newInstance("HelloWorld");
    ```

    通过`getValue`得到其储存的值
    ```java
    byte v1 = nbt1.getValue();
    String v3 = nbt3.getValue();
    ```

    基本类型一般认为是只读的，创建新实例而不是使用`setValue`修改已有实例

/ #[= `NbtCompound`]:

    类似`Map<String, NbtElement>`的结构，储存键值对，键是唯一的`String`

    使用`newInstance`创建实例，使用`put`添加元素

    ```java
    NbtCompound nbt = NbtCompound.newInstance();
    nbt.put("v1", NbtInt.newInstance(114514));
    ```

    使用`put`的重载方法快捷添加元素

    ```java
    nbt.put("v1", (short) 114);
    nbt.put("v2", 514);
    nbt.put("v3", "awa");
    ```

    使用`get`获取元素：由于元素不一定存在，返回值是`Option<NbtElement>`

    ```java
    for(NbtElement ele: nbt.get("v1")) // 如果元素存在
    {
        if(ele.is(NbtString.FACTORY)) // 判断元素类型
            System.out.println(ele.as(NbtString.FACTORY).getValue()); // 转换类型
    }
    ```

    对于已知类型的元素，使用特化的get方法

    ```java
    Option<String> s = nbt.getString("v3");
    Option<NbtCompound> child = nbt.getNbtCompound("v4");
    ```

    当元素不存在或不为对应类型时，返回`Option.none()`

    / #[== 修订]:

        当你想修改元素但不想改变原实例时，可以使用浅克隆
        ```java
        NbtCompound child = nbt.getNBTCompound("child")
            .map(NbtCompound::clone0) // 浅克隆
            .unwrapOrGet(NbtCompound::newInstance); // 不存在则newInstance
        child.put("value", 114514); // 修改
        nbt.put("child", child); // 将结果放回
        ```

        也可以直接使用等效的修订（revise）
        ```java
        for(NbtCompound child: nbt.reviseNbtCompoundOrNew("child")) // 修订
        {
            child.put("value", 114514); // 修改
        }
        ```
        修订方法的返回值为#link(meta.root+"dev/core/editor")[`Editor`]，更详细的例子在#link(meta.root+"dev/mc/item/item#自定义数据-修改自定义数据")[`Item`]中

/ #[= `NbtList`]:

    类似`List<NbtElement>`的结构，但*所有元素必须为同一类型*

    使用`newInstance`创建实例

    ```java
    NbtList nbt = NbtList.newInstance();
    NbtList nbtListInt = NbtList.newInstance(NbtInt.newInstance(114), NbtInt.newInstance(514));
    ```

    使用`add`和`set`修改元素

    ```java
    nbt.add(NbtString.newInstance("hello"));
    nbt.add(NbtString.newInstance("world"));
    nbt.set(1, NbtString.newInstance("nbt"));
    ```

    使用`get`获取元素

    ```java
    NbtElement e1 = nbt.get(0);
    String e2 = nbt.getString(1);
    ```

/ #[= 保存到文件]:

    使用`NbtIo`类进行NBT数据的文件读写操作

    == 写入文件

    将一个`NbtElement`存入文件，*原则上它必须是`NbtCompound`*

    ```java
    // 创建NBT数据
    NbtCompound nbt = NbtCompound.newInstance();
    nbt.put("playerName", "Steve");
    nbt.put("score", 100);
    nbt.put("isOnline", true);

    // 写入文件
    try(FileOutputStream fos = new FileOutputStream("data.dat");
        DataOutputStream dos = new DataOutputStream(fos))
    {
        NbtIo.write(nbt, dos);
    }
    catch(IOException e)
    {
        e.printStackTrace();
    }
    ```

    == 读取文件

    读取时使用`NbtSizeTracker`限制其大小，防止恶意的大文件攻击

    ```java
    try(FileInputStream fis = new FileInputStream("data.dat");
        DataInputStream dis = new DataInputStream(fis))
    {
        // 使用默认大小限制
        NbtElement nbt = NbtIo.read(dis, NbtSizeTracker.newInstance());
        
        // 使用自定义大小限制（单位：字节）
        NbtElement nbtLimited = NbtIo.read(dis, NbtSizeTracker.newInstance(1024 * 1024)); // 1MB限制
        
        if(nbt.is(NbtCompound.FACTORY))
        {
            NbtCompound compound = nbt.as(NbtCompound.FACTORY);
            String playerName = compound.getString("name").unwrap();
            int score = compound.getInt("score").unwrap();
        }
    }
    catch(IOException e)
    {
        e.printStackTrace();
    }
    ```

    == 压缩保存

    推荐使用默认的压缩格式保存：*仅支持`NbtCompound`*

    === 写入压缩文件

    ```java
    NbtCompound nbt = NbtCompound.newInstance();
    nbt.put("version", 1);
    nbt.put("data", "Hello World");

    // 写入GZIP压缩文件
    try(FileOutputStream fos = new FileOutputStream("data.nbt");
        GZIPOutputStream gzos = new GZIPOutputStream(fos))
    {
        NbtIo.writeCompoundCompressed(nbt, gzos);
    }
    catch(IOException e)
    {
        e.printStackTrace();
    }
    ```

    === 读取压缩文件

    ```java
    try(FileInputStream fis = new FileInputStream("data.nbt");
        GZIPInputStream gzis = new GZIPInputStream(fis))
    {
        NbtCompound nbt = NbtIo.readCompoundCompressed(gzis);
        
        // 读取数据
        int version = nbt.getInt("version").unwrap();
        String data = nbt.getString("data").unwrap();
    }
    catch(IOException e)
    {
        e.printStackTrace();
    }
    ```

    == 实用示例

    === 保存玩家数据

    ```java
    public void savePlayerData(EntityPlayer player, File file)
    {
        NbtCompound nbt = NbtCompound.newInstance();
        
        // 保存基本信息
        nbt.put("name", player.getName());
        nbt.put("uuid", player.getUuid().toString());
        
        // 保存位置
        NbtCompound pos = NbtCompound.newInstance();
        pos.put("x", player.getX());
        pos.put("y", player.getY());
        pos.put("z", player.getZ());
        nbt.put("position", pos);
        
        // 保存物品栏
        NbtList inventory = NbtList.newInstance();
        for(int i = 0; i < player.getInventory().getSize(); i++)
        {
            ItemStack item = player.getInventory().getItemStack(i);
            if(!item.isEmpty())
            {
                // 将物品编码为NBT
                for(NbtCompound itemNbt : item.encode().getValue())
                {
                    inventory.add(itemNbt);
                }
            }
        }
        nbt.put("inventory", inventory);
        
        // 写入文件
        try(FileOutputStream fos = new FileOutputStream(file);
            GZIPOutputStream gzos = new GZIPOutputStream(fos))
        {
            NbtIo.writeCompoundCompressed(nbt, gzos);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    ```

    === 加载玩家数据

    ```java
    public void loadPlayerData(EntityPlayer player, File file)
    {
        if(!file.exists())
            return;
        
        try(FileInputStream fis = new FileInputStream(file);
            GZIPInputStream gzis = new GZIPInputStream(fis))
        {
            NbtCompound nbt = NbtIo.readCompoundCompressed(gzis);
            
            // 验证玩家UUID
            String savedUuid = nbt.getString("uuid").unwrap();
            if(!savedUuid.equals(player.getUuid().toString()))
            {
                System.err.println("UUID mismatch!");
                return;
            }
            
            // 恢复位置
            for(NbtCompound pos : nbt.getNbtCompound("position"))
            {
                double x = pos.getDouble("x").unwrap();
                double y = pos.getDouble("y").unwrap();
                double z = pos.getDouble("z").unwrap();
                player.teleport(x, y, z);
            }
            
            // 恢复物品栏
            for(NbtList inventory : nbt.getNbtList("inventory"))
            {
                for(int i = 0; i < inventory.size() && i < player.getInventory().getSize(); i++)
                {
                    NbtCompound itemNbt = inventory.get(i).as(NbtCompound.FACTORY);
                    ItemStack item = ItemStack.decode(itemNbt).getValue().unwrap();
                    player.getInventory().setItemStack(i, item);
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    ```

    === 配置文件读写

    ```java
    public class Config
    {
        private NbtCompound data;
        private File file;
        
        public Config(File file)
        {
            this.file = file;
            this.data = NbtCompound.newInstance();
            load();
        }
        
        public void load()
        {
            if(file.exists())
            {
                try(FileInputStream fis = new FileInputStream(file);
                    GZIPInputStream gzis = new GZIPInputStream(fis))
                {
                    this.data = NbtIo.readCompoundCompressed(gzis);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        public void save()
        {
            try(FileOutputStream fos = new FileOutputStream(file);
                GZIPOutputStream gzos = new GZIPOutputStream(fos))
            {
                NbtIo.writeCompoundCompressed(data, gzos);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        
        public String get(String key, String defaultValue)
        {
            return data.getString(key).unwrapOr(defaultValue);
        }
        
        public void set(String key, String value)
        {
            data.put(key, value);
        }
        
        public int getInt(String key, int defaultValue)
        {
            return data.getInt(key).unwrapOr(defaultValue);
        }
        
        public void setInt(String key, int value)
        {
            data.put(key, value);
        }
    }
    ```

    #cardTip[
      使用GZIP压缩可以显著减少文件大小，特别是对于包含大量文本数据的NBT文件
    ]

    #cardAttention[
      始终使用`NbtSizeTracker`来限制读取的NBT数据大小，防止恶意大文件导致内存溢出
    ]