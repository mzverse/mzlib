package mz.mzlib.minecraft.vanilla;

import mz.mzlib.MzLib;
import mz.mzlib.minecraft.event.server.EventServer;
import mz.mzlib.module.MzModule;
import mz.mzlib.util.RuntimeUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarFile;

/**
 * Initializer in the mod environment.
 */
public class MzLibMinecraftInitializer extends MzModule
{
    public static MzLibMinecraftInitializer instance = new MzLibMinecraftInitializer();

    @Override
    public void onLoad()
    {
        this.register(EventServer.Module.instance);
        this.register(ServerModule.Module.instance);
        this.register(new ServerModule(MzLibVanilla.instance));
    }

    /**
     * @param args
     *  [0]: version
     *  [1]: server jar path
     */
    public static void main(String[] args) throws Throwable
    {
        if(args.length < 2)
        {
            System.err.println("Usage: java -jar mzlib-minecraft.jar <version> <server jar path>");
            System.exit(1);
        }
        String mainClass;
        List<URL> urls = new ArrayList<>();
        try(JarFile jarFile = new JarFile(args[1]))
        {
            try(InputStream inputStream = jarFile.getInputStream(jarFile.getEntry("META-INF/main-class")))
            {
                mainClass = new Scanner(inputStream).nextLine();
            }
            urls.addAll(exportList(jarFile, "libraries"));
            urls.addAll(exportList(jarFile, "versions"));
        }
        urls.add(new File(System.getProperty("java.class.path")).toURI().toURL());
        URLClassLoader cl = new URLClassLoader(urls.toArray(new URL[0]), MzLibMinecraftInitializer.class.getClassLoader().getParent());
        Thread thread = new Thread(() ->
        {
            try
            {
                cl.loadClass(MzLibMinecraftInitializer.class.getName()).getDeclaredMethod("main1", String[].class)
                    .invoke(null, (Object) args);
                cl.loadClass(mainClass).getDeclaredMethod("main", String[].class).invoke(null, (Object) Arrays.copyOfRange(args, 2, args.length)                );
            }
            catch(InvocationTargetException e)
            {
                throw RuntimeUtil.sneakilyThrow(e.getTargetException());
            }
            catch(Throwable e)
            {
                throw RuntimeUtil.sneakilyThrow(e);
            }
        }, "ServerMain");
        thread.setContextClassLoader(cl);
        thread.start();
    }
    public static void main1(String[] args)
    {
        MzModule module = new MzModule();
        module.load();
        module.register(MzLib.instance);
        module.register(new MinecraftPlatformVanilla(args[0]));
        module.register(MzLibMinecraftInitializer.instance);
    }
    static List<URL> exportList(JarFile file, String listName) throws IOException
    {
        try(InputStream inputStream = file.getInputStream(file.getEntry("META-INF/" + listName + ".list")))
        {
            Scanner scanner = new Scanner(inputStream);
            List<URL> result = new ArrayList<>();
            while(true)
            {
                try
                {
                    String hash = scanner.next();
                    String name = scanner.next();
                    String path = scanner.next();
                    Path outputPath = Paths.get(listName+"/"+path);
                    result.add(outputPath.toUri().toURL());
                    if(Files.notExists(outputPath))
                    {
                        Files.createDirectories(outputPath.getParent());
                        try(InputStream libraryStream = file.getInputStream(file.getEntry("META-INF/" + listName + "/" + path)))
                        {
                            Files.copy(libraryStream, outputPath);
                        }
                    }
                }
                catch(NoSuchElementException e)
                {
                    break;
                }
            }
            return result;
        }
    }
}
