package bsh.commands;

import java.util.Collection;

import bsh.CallStack;
import bsh.Interpreter;
import com.kiwisoft.utils.StringUtils;

/**
 * @noinspection UtilityClassWithoutPrivateConstructor
 */
public class isEmpty
{
	/** @noinspection UNUSED_SYMBOL*/
	public static boolean invoke(Interpreter env, CallStack callstack, Object value)
	{
		if (value==null) return true;
		if (value instanceof String) return StringUtils.isEmpty((String)value);
		if (value instanceof Collection) return ((Collection)value).isEmpty();
		if (value instanceof Object[]) return ((Object[])value).length==0;
		return false;
	}

}
