package org.runaway.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class NMS {
    private static MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static Field MODIFIERS_FIELD = getField(Field.class, "modifiers", false);
    private static Object UNSAFE;
    private static MethodHandle UNSAFE_FIELD_OFFSET;
    private static MethodHandle UNSAFE_PUT_OBJECT;
    private static MethodHandle UNSAFE_STATIC_FIELD_OFFSET;

    public static Field getField(Class<?> clazz, String field) {
        return getField(clazz, field, true);
    }

    public static Field getField(Class<?> clazz, String field, boolean log) {
        if (clazz == null)
            return null;
        Field f = null;
        try {
            f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            return f;
        } catch (Exception e) {

            return null;
        }
    }

    public static MethodHandle getFinalSetter(Class<?> clazz, String field, boolean log) {
        Field f;
        if (MODIFIERS_FIELD == null) {
            if (UNSAFE == null) {
                try {
                    UNSAFE = getField(Class.forName("sun.misc.Unsafe"), "theUnsafe").get(null);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                UNSAFE_STATIC_FIELD_OFFSET = getMethodHandle(UNSAFE.getClass(), "staticFieldOffset", true, Field.class).bindTo(UNSAFE);
                UNSAFE_FIELD_OFFSET = getMethodHandle(UNSAFE.getClass(), "objectFieldOffset", true, Field.class)
                        .bindTo(UNSAFE);
                UNSAFE_PUT_OBJECT = getMethodHandle(UNSAFE.getClass(), "putObject", true, Object.class, long.class, Object.class)
                        .bindTo(UNSAFE);
            }
            f = getField(clazz, field, log);
            if (f == null) {
                return null;
            }
            try {
                boolean isStatic = Modifier.isStatic(f.getModifiers());
                long offset = (long) (isStatic ? UNSAFE_STATIC_FIELD_OFFSET.invoke(f) : UNSAFE_FIELD_OFFSET.invoke(f));
                return isStatic ? MethodHandles.insertArguments(UNSAFE_PUT_OBJECT, 0, clazz, offset)
                        : MethodHandles.insertArguments(UNSAFE_PUT_OBJECT, 1, offset);
            } catch (Throwable t) {
                t.printStackTrace();
                return null;
            }
        }
        f = getField(clazz, field, log);
        if (f == null) {
            return null;
        }
        try {
            MODIFIERS_FIELD.setInt(f, f.getModifiers() & ~Modifier.FINAL);
        } catch (Exception e) {

            return null;
        }
        try {
            return LOOKUP.unreflectSetter(f);
        } catch (Exception e) {
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String method, boolean log, Class<?>... params) {
        if (clazz == null)
            return null;
        Method f = null;
        try {
            f = clazz.getDeclaredMethod(method, params);
            f.setAccessible(true);
        } catch (Exception e) {

        }
        return f;
    }

    public static MethodHandle getMethodHandle(Class<?> clazz, String method, boolean log, Class<?>... params) {
        if (clazz == null)
            return null;
        try {
            return LOOKUP.unreflect(getMethod(clazz, method, log, params));
        } catch (Exception e) {

        }
        return null;
    }
}
