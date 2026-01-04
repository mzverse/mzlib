package mz.mzlib.util;

import org.mozilla.javascript.*;
import org.mozilla.javascript.json.JsonParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsUtil
{
    public static Object newObject(Settings settings, Object scope)
    {
        try(Context context = settings.enterContext())
        {
            return context.newObject((Scriptable) scope);
        }
    }
    public static Object newObject(Object scope)
    {
        return newObject(Settings.def, scope);
    }

    public static Object initSafeScope(Settings settings)
    {
        try(Context context = settings.enterContext())
        {
            return context.initSafeStandardObjects();
        }
    }
    public static Object initSafeScope()
    {
        return initSafeScope(Settings.def);
    }

    public static Object initScope(Settings settings)
    {
        try(Context context = settings.enterContext())
        {
            return context.initStandardObjects();
        }
    }
    public static Object initScope()
    {
        return initScope(Settings.def);
    }

    public static Object function(Function function)
    {
        return new NativeJavaFunction(function);
    }

    static Cache<String, Script> cache = Cache.<String, Script>builder().weakKey().build();
    public static Object eval(Settings settings, Object scope, String script)
    {
        try(Context context = settings.enterContext())
        {
            return cache.get(script, ()->
            {
                context.setOptimizationLevel(9);
                return context.compileString(script, "js", 1, null);
            }).exec(context, (Scriptable) scope);
        }
        catch(Throwable e)
        {
            throw RuntimeUtil.sneakilyThrow(e);
        }
    }
    public static Object eval(Object scope, String script)
    {
        return eval(Settings.def, scope, script);
    }

    public static Object get(Object obj, String key)
    {
        return ((Scriptable) obj).get(key, (Scriptable) obj);
    }

    public static void put(Object obj, String key, Object value)
    {
        ((Scriptable) obj).put(key, (Scriptable) obj, value);
    }

    public static void put(Settings settings, Object obj, String key, Object value)
    {
        put(obj, key, wrap(settings, obj, value));
    }

    public static Object mapToObject(Settings settings, Object scope, Map<String, ?> map)
    {
        Object result = newObject(settings, scope);
        for(Map.Entry<String, ?> e : map.entrySet())
        {
            put(settings, result, e.getKey(), e.getValue());
        }
        return result;
    }
    public static Object mapToObject(Object scope, Map<String, Object> map)
    {
        return mapToObject(Settings.def, scope, map);
    }

    public static Object wrap(Settings settings, Object scope, Object obj)
    {
        try(Context context = settings.enterContext())
        {
            return context.getWrapFactory().wrap(context, (Scriptable) scope, obj, null);
        }
    }
    public static Object wrap(Object scope, Object obj)
    {
        return wrap(Settings.def, scope, obj);
    }
    public static Object wrapClass(Settings settings, Object scope, Class<?> obj)
    {
        try(Context context = settings.enterContext())
        {
            return context.getWrapFactory().wrapJavaClass(context, (Scriptable) scope, obj);
        }
    }
    public static Object wrapClass(Object scope, Class<?> obj)
    {
        return wrapClass(Settings.def, scope, obj);
    }

    public static Object parseJson(Settings settings, Object scope, String json) throws Exception
    {
        try(Context context = settings.enterContext())
        {
            return new JsonParser(context, (Scriptable) scope).parseValue(json);
        }
    }
    public static Object parseJson(Object scope, String json) throws Exception
    {
        return parseJson(Settings.def, scope, json);
    }

    public static String toJson(Settings settings, Object scope, Object obj)
    {
        try(Context context = settings.enterContext())
        {
            return (String) toJvm(NativeJSON.stringify(context, (Scriptable) scope, obj, null, null));
        }
    }
    public static String toJson(Object scope, Object obj)
    {
        return toJson(Settings.def, scope, obj);
    }

    public static Object toJvm(Object obj)
    {
        if(obj instanceof ConsString)
            return ((ConsString) obj).toString();
        else if(obj instanceof Undefined)
            return null;
        if(!(obj instanceof Scriptable))
            return obj;
        if(obj instanceof Wrapper)
            return ((Wrapper) obj).unwrap();
        else if(obj instanceof NativeArray)
            return RuntimeUtil.<List<Object>>cast(obj).stream().map(JsUtil::toJvm).collect(Collectors.toList());
        else if(obj instanceof NativeObject)
            return RuntimeUtil.<Map<String, Object>>cast(obj).entrySet().stream()
                .map(e -> new Pair<>(e.getKey(), toJvm(e.getValue())))
                .filter(p -> p.getFirst() != null && p.getSecond() != null)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        else if(obj instanceof Callable)
            return (Function) (settings, scope, thisObj, args) ->
            {
                try(Context context = settings.enterContext())
                {
                    return ((Callable) obj).call(context, (Scriptable) scope, (Scriptable) thisObj, args);
                }
            };
        else
            throw new IllegalArgumentException(obj.toString());
    }

    @FunctionalInterface
    public interface Function
    {
        Object call(Settings settings, Object scope, Object thisObj, Object[] args);
    }

    static class NativeJavaFunction extends BaseFunction implements Wrapper
    {
        Function function;
        public NativeJavaFunction(Function function)
        {
            this.function = function;
        }

        @Override
        public Object unwrap()
        {
            return this.function;
        }

        @Override
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
        {
            return this.function.call(Settings.of(cx), scope, thisObj, args);
        }
    }

    public static class Settings
    {
        public static Settings def = new Settings();

        public Map<Class<?>, Object> proxies = new HashMap<>();

        WrapFactory wrapFactory = new WF(this);

        Context enterContext()
        {
            Context context = new ContextFactory()
            {
                @Override
                protected boolean hasFeature(Context cx, int featureIndex)
                {
                    //noinspection SwitchStatementWithTooFewBranches
                    switch(featureIndex)
                    {
                        case Context.FEATURE_ENABLE_JAVA_MAP_ACCESS:
                            return true;
                        default:
                            return super.hasFeature(cx, featureIndex);
                    }
                }
            }.enterContext();
            context.setLanguageVersion(Context.VERSION_ES6);
            context.setWrapFactory(this.wrapFactory);
            return context;
        }

        static Settings of(WrapFactory factory)
        {
            if(factory instanceof WF)
                return ((WF) factory).settings;
            else
                return def;
        }
        static Settings of(Context context)
        {
            return of(context.getWrapFactory());
        }
    }

    static class WF extends WrapFactory
    {
        Settings settings;
        public WF(Settings settings)
        {
            this.settings = settings;
            this.setJavaPrimitiveWrap(false);
        }

        @Override
        public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType)
        {
            for(Map.Entry<Class<?>, Object> e : this.settings.proxies.entrySet())
            {
                if(e.getKey().isInstance(javaObject))
                    return new NativeJavaObjectProxy(
                        javaObject, (Scriptable) JsUtil.wrap(this.settings, scope, e.getValue()));
            }
            if(javaObject instanceof Function)
                return (Scriptable) function((Function) javaObject);
            else
                return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
        }
    }

    static class NativeJavaObjectProxy extends NativeJavaObject
    {
        Scriptable proxy;

        public NativeJavaObjectProxy(Object javaObject, Scriptable proxy)
        {
            super(proxy.getParentScope(), javaObject, null);
            this.proxy = proxy;
        }

        @Override
        public Scriptable getPrototype()
        {
            return this.proxy.getPrototype();
        }
        @Override
        public Object[] getIds()
        {
            return Stream.concat(Arrays.stream(this.proxy.getIds()), Arrays.stream(super.getIds())).distinct()
                .toArray();
        }

        @Override
        public boolean has(int index, Scriptable start)
        {
            return this.proxy.has(index, start) || super.has(index, start);
        }

        @Override
        public boolean has(String name, Scriptable start)
        {
            return this.proxy.has(name, start) || super.has(name, start);
        }

        @Override
        public Object get(int index, Scriptable start)
        {
            Object result = this.proxy.get(index, start);
            if(result == Scriptable.NOT_FOUND)
                result = super.get(index, start);
            return result;
        }

        @Override
        public Object get(String name, Scriptable start)
        {
            Object result = this.proxy.get(name, start);
            if(result == Scriptable.NOT_FOUND)
                result = super.get(name, start);
            return result;
        }

        @Override
        public Object getDefaultValue(Class<?> hint)
        {
            if(this.proxy instanceof ScriptableObject)
                return ScriptableObject.getDefaultValue(this, hint);
            else
                return this.proxy.getDefaultValue(hint);
        }
    }
}
