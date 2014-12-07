package stevesaddons.helpers;

import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionHelper
{
    public static boolean setPrivateStaticFinalField(Class clazz, String fieldName, Object value)
    {
        return setPrivateFinalField(clazz, fieldName, null, value);
    }

    public static boolean setPrivateFinalField(Class clazz, String fieldName, Object instance, Object value)
    {
        try
        {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(instance, value);
            return true;
        } catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
