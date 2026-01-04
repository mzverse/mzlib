#import "/lib/lib.typ": *;
#let title = [IOUtil];
#show: template.with(title: title);

`IOUtil` 提供了各种输入输出相关的工具方法。

= 文件操作

```java
// 打开文件输入流
InputStream is = IOUtil.openFile(file);

// 打开文件输出流
OutputStream os = IOUtil.openFile(file);

// 从 JAR 中打开文件
InputStream is = IOUtil.openFileInZip(jarFile, "path/to/file");

// 读取文件全部内容
String content = IOUtil.readAllString(file);

// 写入文件
IOUtil.writeAllString(file, content);

// 复制文件
IOUtil.copyFile(source, target);
```

= 流操作

```java
// 读取流为字节数组
byte[] bytes = IOUtil.readAllBytes(inputStream);

// 读取流为字符串
String str = IOUtil.readAllString(inputStream, "UTF-8");

// 写入字节数组
IOUtil.writeAllBytes(outputStream, bytes);

// 复制流
IOUtil.copy(inputStream, outputStream);
```

= 目录操作

```java
// 创建目录
IOUtil.mkdirs(directory);

// 删除目录
IOUtil.deleteDirectory(directory);

// 列出文件
List<File> files = IOUtil.listFiles(directory);

// 查找文件
File file = IOUtil.findFile(directory, "name");
```

= 使用示例

```java
// 读取配置文件
public String readConfig(File file) throws IOException
{
    return IOUtil.readAllString(file);
}

// 保存数据到文件
public void saveData(File file, byte[] data) throws IOException
{
    IOUtil.mkdirs(file.getParentFile());
    IOUtil.writeAllBytes(file, data);
}

// 从资源读取
public String readResource(String path) throws IOException
{
    try (InputStream is = getClass().getResourceAsStream(path))
    {
        return IOUtil.readAllString(is);
    }
}
```

= 注意事项

- 使用后记得关闭流
- 使用 try-with-resources 自动管理资源
- 目录操作会递归处理子目录