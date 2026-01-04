#import "/lib/lib.typ": *;
#set raw(lang: "java");
#let title = `AutoCompletable`;
#show: template.with(title: title);



`AutoCompletable`是一个特殊的可迭代接口，用于实现自动完成模式。它在迭代开始时获取数据，在迭代结束时执行完成操作。

= 工作原理

`AutoCompletable`实现了三个关键方法：

- `start()` - 开始操作，返回一个包含元素和附加数据的对
- `complete(T element, D data)` - 完成操作，处理元素和附加数据
- `iterator()` - 返回迭代器，自动管理start和complete的调用

== 执行流程

1. 调用`iterator()`创建迭代器
2. 第一次调用`hasNext()`时，自动调用`start()`获取数据
3. 调用`next()`获取元素
4. 第二次调用`hasNext()`时，自动调用`complete()`完成操作
5. 后续`hasNext()`返回`false`

= 基本用法

== 创建实例

使用`AutoCompletable.of()`静态方法创建实例：

```java
// 不需要附加数据的版本
AutoCompletable<String, Void> auto1 = AutoCompletable.of(
    () -> "Hello",           // start: 获取数据
    str -> System.out.println("Completed: " + str)  // complete: 处理数据
);

// 需要附加数据的版本
AutoCompletable<String, Integer> auto2 = AutoCompletable.of(
    () -> Pair.of("Hello", 42),  // start: 返回元素和附加数据
    (str, num) -> System.out.println("Completed: " + str + ", " + num)  // complete
);
```

== 使用迭代

```java
AutoCompletable<String, Void> auto = AutoCompletable.of(
    () -> "Hello World",
    str -> System.out.println("Finalized: " + str)
);

// 使用增强for循环
for(String str : auto)
{
    System.out.println("Processing: " + str);
}

// 输出:
// Processing: Hello World
// Finalized: Hello World
```

== 使用accept方法

```java
AutoCompletable<String, Void> auto = AutoCompletable.of(
    () -> "Data",
    str -> System.out.println("Cleanup: " + str)
);

auto.accept(str -> System.out.println("Handle: " + str));

// 输出:
// Handle: Data
// Cleanup: Data
```

= 实际应用

== 资源管理

`AutoCompletable`非常适合用于资源管理，确保资源在使用后正确释放：

```java
public class FileResource implements AutoCloseable
{
    private File file;
    private FileInputStream fis;

    public FileResource(File file)
    {
        this.file = file;
    }

    public FileInputStream open() throws IOException
    {
        this.fis = new FileInputStream(file);
        return fis;
    }

    @Override
    public void close()
    {
        if(fis != null)
        {
            try
            {
                fis.close();
                System.out.println("File closed: " + file.getName());
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}

// 使用AutoCompletable管理文件资源
public void processFile(File file)
{
    AutoCompletable<FileInputStream, FileResource> auto = AutoCompletable.of(
        () ->
        {
            FileResource resource = new FileResource(file);
            FileInputStream fis = resource.open();
            return Pair.of(fis, resource);
        },
        (fis, resource) -> resource.close()
    );

    for(FileInputStream fis : auto)
    {
        // 处理文件
        byte[] data = fis.readAllBytes();
        System.out.println("Read " + data.length + " bytes");
    }
    // 自动调用close()
}
```

== 事务处理

```java
public class Transaction
{
    private Connection connection;
    private boolean committed = false;

    public Transaction(Connection connection)
    {
        this.connection = connection;
    }

    public void commit() throws SQLException
    {
        connection.commit();
        committed = true;
    }

    public void rollback()
    {
        try
        {
            if(!committed)
            {
                connection.rollback();
                System.out.println("Transaction rolled back");
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
}

// 使用AutoCompletable管理事务
public void executeTransaction(Connection connection)
{
    AutoCompletable<Statement, Transaction> auto = AutoCompletable.of(
        () ->
        {
            Transaction tx = new Transaction(connection);
            Statement stmt = connection.createStatement();
            return Pair.of(stmt, tx);
        },
        (stmt, tx) -> tx.rollback()
    );

    for(Statement stmt : auto)
    {
        try
        {
            stmt.executeUpdate("INSERT INTO users VALUES (1, 'Alice')");
            stmt.executeUpdate("INSERT INTO users VALUES (2, 'Bob')");
            // 提交事务
            connection.commit();
            System.out.println("Transaction committed");
        }
        catch(SQLException e)
        {
            System.out.println("Transaction failed, will rollback");
            throw new RuntimeException(e);
        }
    }
    // 自动回滚（如果未提交）
}
```

== 临时文件处理

```java
public void processWithTempFile()
{
    AutoCompletable<File, Void> auto = AutoCompletable.of(
        () ->
        {
            try
            {
                // 创建临时文件
                File tempFile = File.createTempFile("temp", ".txt");
                System.out.println("Created temp file: " + tempFile.getAbsolutePath());
                return tempFile;
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        },
        tempFile ->
        {
            // 删除临时文件
            if(tempFile.exists())
            {
                tempFile.delete();
                System.out.println("Deleted temp file: " + tempFile.getName());
            }
        }
    );

    for(File tempFile : auto)
    {
        try
        {
            // 使用临时文件
            Files.write(tempFile.toPath(), "Hello World".getBytes());
            String content = Files.readString(tempFile.toPath());
            System.out.println("Content: " + content);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    // 自动删除临时文件
}
```

== 锁管理

```java
public class LockResource
{
    private Lock lock;

    public LockResource(Lock lock)
    {
        this.lock = lock;
        lock.lock();
        System.out.println("Lock acquired");
    }

    public void unlock()
    {
        lock.unlock();
        System.out.println("Lock released");
    }
}

// 使用AutoCompletable管理锁
public void criticalSection(Lock lock)
{
    AutoCompletable<LockResource, LockResource> auto = AutoCompletable.of(
        () ->
        {
            LockResource resource = new LockResource(lock);
            return Pair.of(resource, resource);
        },
        (resource, data) -> data.unlock()
    );

    for(LockResource resource : auto)
    {
        // 执行临界区代码
        System.out.println("Executing critical section");
    }
    // 自动释放锁
}
```

= 与Editor的关系

`Editor`类基于`AutoCompletable`实现，用于实现get-modify-set模式：

```java
// Editor的实现原理
public class Editor<T> implements AutoCompletable<T, Void>
{
    @Override
    public Pair<T, Void> start()
    {
        // get操作
        T value = getter.get();
        return Pair.of(value, null);
    }

    @Override
    public void complete(T element, Void data)
    {
        // set操作
        setter.accept(element);
    }
}
```

= 注意事项

== 单次迭代

`AutoCompletable`设计为单次迭代器，每个实例只能迭代一次：

```java
AutoCompletable<String, Void> auto = AutoCompletable.of(
    () -> "Data",
    str -> System.out.println("Complete: " + str)
);

// 第一次迭代
for(String str : auto)
{
    System.out.println("First: " + str);
}

// 第二次迭代不会执行任何操作
for(String str : auto)
{
    System.out.println("Second: " + str);  // 不会执行
}
```

== 异常处理

在迭代过程中抛出异常时，`complete`方法仍然会被调用：

```java
AutoCompletable<String, Void> auto = AutoCompletable.of(
    () -> "Data",
    str -> System.out.println("Cleanup executed")
);

try
{
    for(String str : auto)
    {
        System.out.println("Processing: " + str);
        throw new RuntimeException("Error");
    }
}
catch(RuntimeException e)
{
    System.out.println("Caught: " + e.getMessage());
}

// 输出:
// Processing: Data
// Cleanup executed
// Caught: Error
```

== 性能考虑

`AutoCompletable`使用轻量级的迭代器实现，性能开销很小。但对于高频调用的场景，建议直接使用传统的方法调用。

#cardTip[
  `AutoCompletable`特别适合需要确保资源释放的场景，如文件操作、数据库连接、锁管理等
]

#cardAttention[
  不要在`complete`方法中抛出未捕获的异常，这可能导致资源无法正确清理
]