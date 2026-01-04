#import "/lib/lib.typ": *;
#let title = [命令];
#show: template.with(title: title);



= 处理命令参数

参数处理使用`ArgumentParser`的实例，构造器中`name`表示参数名，仅用于提示帮助信息

例如，处理字符串参数可以使用`ArgumentParserString`，第二个参数表示字符串中是否可以包含空格，后面的参数表示预设值

```java
public class Demo extends MzModule
{
    public static Demo instance = new Demo();

    public Command command;

    @Override
    public void onLoad()
    {
        this.register(this.command=new Command("demo", "d").setHandler(context->
        {
            // handle arg0
            String arg0=new ArgumentParserString("arg0", false, "enum1", "enum2").handle(context);
            // 若arg0解析失败直接返回
            if(!context.successful)
                return;
            switch(arg0)
            {
                case "enum1":
                    // 第一种用法
                    if(!context.successful || !context.doExecute)
                        return;
                    context.sender.sendMessage(Text.literal("This is the first usage of this command"));
                    break;
                case "enum2":
                    // 第二种用法
                    // 再读一个参数
                    // 后面没有其它参数，可以允许包含空格
                    String arg1=new ArgumentParserString("arg1", true).handle(context);
                    if(!context.successful || !context.doExecute)
                        return;
                    context.sender.sendMessage(Text.literal("Second: "+arg1));
                    break;
                default: // 无效的arg0，命令解析失败
                    context.successful=false;
                    break;
            }
        }));
    }
}
```