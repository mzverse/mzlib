package mz.mzlib.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mozilla.javascript.Context;

import java.util.Collections;
import java.util.function.Consumer;

public class TestJsUtil
{
    Consumer<Runnable> callback;
    public void setCallback(Consumer<Runnable> callback)
    {
        this.callback = callback;
    }

    @Test
    public void test()
    {
        JsUtil.eval(JsUtil.wrap(JsUtil.initScope(), this), "setCallback(r=>r.run())");
        System.out.println(Context.getCurrentContext());
        this.callback.accept(() ->
            System.out.println(Context.getCurrentContext()));
    }

    @Test
    public void testCacheContext()
    {
        JsUtil.Settings settings = new JsUtil.Settings();
        JsUtil.Settings settings1 = new JsUtil.Settings();
        Object scope = JsUtil.initScope();
        String label = "success";
        settings.proxies.put(this.getClass(), JsUtil.mapToObject(scope, Collections.singletonMap("toString", JsUtil.function((s, sc, thisObj, as) ->
            label))));
        Assertions.assertEquals(label, JsUtil.eval(settings, JsUtil.wrap(scope, Collections.singletonMap("test", this)), "test.toString()"));
        Assertions.assertEquals(this.toString(), JsUtil.eval(settings1, JsUtil.wrap(scope, Collections.singletonMap("test", this)), "test.toString()"));
        Assertions.assertEquals(label, JsUtil.eval(settings, JsUtil.wrap(scope, Collections.singletonMap("test", this)), "test.toString()"));
    }
}
