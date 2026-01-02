package mz.mzlib.module;

import mz.mzlib.util.ClassUtil;
import mz.mzlib.util.CollectionUtil;
import mz.mzlib.util.ElementSwitcher;
import mz.mzlib.util.RuntimeUtil;

import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MzModule
{
    public Set<MzModule> submodules = new HashSet<>();
    public Map<Object, Stack<IRegistrar<?>>> registeredObjects = new LinkedHashMap<>();

    public boolean isLoaded = false;
    public CompletableFuture<Void> future = new CompletableFuture<>();
    public boolean isLoaded()
    {
        return this.isLoaded;
    }

    public void load()
    {
        if(this.isLoaded())
            throw new IllegalStateException("Try to load the module but it has been loaded: " + this + ".");
        this.isLoaded = true;
        this.onLoad();
        this.future.complete(null);
    }

    public void unload()
    {
        if(!this.isLoaded())
            throw new IllegalStateException("Try to unload the module but it's not loaded: " + this + ".");
        List<Throwable> es = new ArrayList<>();
        for(Object i : CollectionUtil.reverse(this.registeredObjects.keySet().stream()).collect(Collectors.toList()))
        {
            try
            {
                this.unregister(i);
            }
            catch(Throwable e)
            {
                es.add(e);
            }
        }
        this.isLoaded = false;
        this.future = new CompletableFuture<>();
        try
        {
            this.onUnload();
        }
        catch(Throwable e)
        {
            es.add(e);
        }
        if(!es.isEmpty())
        {
            RuntimeException exception = new RuntimeException("Exception on unloading module " + this + ".");
            es.forEach(exception::addSuppressed);
            throw exception;
        }
    }

    public void register(Object object)
    {
        if(!this.isLoaded())
            throw new IllegalStateException("Try to register an object but the module is not loaded: " + this + ".");
        if(registeredObjects.containsKey(object))
            throw new IllegalStateException("Try to register the object but it has been registered: " + object + ".");
        if(object instanceof MzModule)
        {
            try
            {
                submodules.add((MzModule) object);
                ((MzModule) object).load();
            }
            catch(Throwable e)
            {
                this.registeredObjects.put(object, new Stack<>());
                throw e;
            }
        }
        Set<IRegistrar<?>> registrars = new HashSet<>();
        ClassUtil.forEachSuperUnique(
            object.getClass(), c ->
            {
                if(RegistrarRegistrar.instance.registrars.containsKey(c))
                {
                    for(IRegistrar<?> i : RegistrarRegistrar.instance.registrars.get(c).toArray(new IRegistrar[0]))
                    {
                        if(i.isRegistrable(RuntimeUtil.cast(object)))
                            registrars.add(i);
                    }
                }
            }
        );
        if(registrars.isEmpty() && !(object instanceof MzModule))
            throw new UnsupportedOperationException(
                "Try to register the object but found no registrar: " + object + ".");

        Stack<IRegistrar<?>> workedRegistrarsRecord = new Stack<>();

        Set<IRegistrar<?>> untreatedRegistrars = new HashSet<>(registrars);
        Set<IRegistrar<?>> processLater = new HashSet<>();
        Set<IRegistrar<?>> workedRegistrars = new HashSet<>();
        try
        {
            int cnt = 0;
            while(!untreatedRegistrars.isEmpty())
            {
                Iterator<IRegistrar<?>> it = untreatedRegistrars.iterator();
                IRegistrar<?> now = it.next();
                it.remove();

                if(workedRegistrars.containsAll(now.getDependencies()))
                {
                    now.register(this, RuntimeUtil.cast(object));
                    workedRegistrars.add(now);
                    workedRegistrarsRecord.push(now);
                    cnt++;
                }
                else
                {
                    processLater.add(now);
                }

                if(untreatedRegistrars.isEmpty())
                {
                    if(cnt == 0)
                        throw new IllegalStateException(
                            "Circular dependency or nonexistent dependencies: " + processLater + ".");
                    cnt = 0;
                    untreatedRegistrars = processLater;
                    processLater = new HashSet<>();
                }
            }
        }
        finally
        {
            this.registeredObjects.put(object, workedRegistrarsRecord);
        }
    }

    public void registerIfEnabled(AnnotatedElement element)
    {
        if(ElementSwitcher.isEnabled(element))
            this.register(element);
    }

    public void unregister(Object object)
    {
        if(!this.isLoaded())
            throw new IllegalStateException(
                "Try to unregister an object but the module has been unloaded: " + this + ".");
        List<Throwable> es = new ArrayList<>();
        if(object instanceof MzModule)
        {
            if(!submodules.contains(object))
                es.add(new IllegalStateException(
                    "Try to unload the module(" + object + ") but it's not loaded by this module(" + this + ")."));
            submodules.remove(object);
            ((MzModule) object).unload();
        }
        Stack<IRegistrar<?>> removed = registeredObjects.remove(object);
        if(removed == null)
            es.add(new IllegalStateException("Try to unregister the object but it's not registered: " + object + "."));
        while(removed != null && !removed.isEmpty())
        {
            IRegistrar<?> i = removed.pop();
            Set<IRegistrar<?>> key = RegistrarRegistrar.instance.registrars.get(i.getType());
            if(key == null || !key.contains(i))
                es.add(new IllegalStateException(
                    "Try to unregister an object(" + object + ") but the registrar(" + i + ") has been unloaded."));
            i.unregister(this, RuntimeUtil.cast(object));
        }
        if(!es.isEmpty())
        {
            RuntimeException exception = new RuntimeException("Exception on unloading object in module " + this + ".");
            es.forEach(exception::addSuppressed);
            throw exception;
        }
    }

    public void onLoad()
    {
    }

    @SuppressWarnings("RedundantThrows")
    public void onUnload() throws Throwable
    {
    }
}
