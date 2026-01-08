package mz.mzlib.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;

import static mz.mzlib.util.ClassUtil.*;

public class TestClassUtil
{
    @Test
    void getName$test()
    {
        String nameObject = Object.class.getName();
        String nameArrayClass = Class[].class.getName();

        Assertions.assertEquals("java.lang.Object", nameObject);
        Assertions.assertEquals("[L" + "java.lang.Class;", nameArrayClass);
    }

    @Test
    void classForName$test() throws ClassNotFoundException
    {
        Class<?> classObject = classForName("java.lang.Object", null);
        Class<?> classInt = classForName("int", null);

        Assertions.assertEquals(Object.class, classObject);
        Assertions.assertEquals(int.class, classInt);
    }

    @Test
    void makeReference$test()
    {
        Object obj = new Object();
        makeReference(this.getClass().getClassLoader(), obj);

        WeakReference<Object> ref = new WeakReference<>(obj);
        //noinspection UnusedAssignment
        obj = null;
        System.gc();
        Assertions.assertNotNull(ref.get());
    }

    @Test
    void getByteCode$test()
    {
        byte[] byteCodeObject = getByteCode(Object.class);
        byte[] byteCodeClass = getByteCode(Class.class);

        Assertions.assertNotEquals(0, byteCodeObject.length);
        Assertions.assertNotEquals(0, byteCodeClass.length);
    }
}
