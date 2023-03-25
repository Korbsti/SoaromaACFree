package me.korbsti.soaromaac;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection {
    /*
     * public static Class<?> getClass(String classname) { try {
     *
     * String version =
     * Bukkit.getServer().getClass().getPackage().getName().replace(".",
     * ",").split(",")[3]; String path = classname.replace("{nms}",
     * "net.minecraft.server." + version) .replace("{nm}", "net.minecraft." +
     * version).replace("{cb}", "org.bukkit.craftbukkit.." + version);
     *
     * return Class.forName(path); } catch (Throwable t) { t.printStackTrace();
     * return null; } }
     */
    public static Class<?> getClass(String classname) {
        try {

            /*
             * String version =
             * Bukkit.getServer().getClass().getPackage().getName().replace(".",
             * ",").split(",")[3]; String path = classname.replace("{nms}",
             * "net.minecraft.server." + version) .replace("{nm}",
             * "net.minecraft." + version).replace("{cb}",
             * "org.bukkit.craftbukkit.." + version);
             */
            return Class.forName(classname);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public static boolean getFieldBooleanSuperClass(Class<?> clazz, Object packet, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (boolean) field.get(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Field field, Object obj) {
        try {
            return (T) field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getFieldValue(Object instance, String fieldName) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldName;
    }

    public static double getFieldValueClass(Class<?> clazz, Object packet, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (double) field.get(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0d;
    }

    public static float getFieldValueClassFloat(Class<?> clazz, Object packet, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (float) field.get(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0f;
    }

    public static Object getNmsPlayer(Player p) {
        try {
            Method getHandle = p.getClass().getMethod("getHandle");
            return getHandle.invoke(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }
}