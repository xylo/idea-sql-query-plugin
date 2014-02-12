package bsh.commands;

import java.text.MessageFormat;

import bsh.Interpreter;
import bsh.CallStack;

/**
 * @noinspection UtilityClassWithoutPrivateConstructor
 */
public class combine
{
	/** @noinspection UNUSED_SYMBOL*/
	public static String invoke(Interpreter env, CallStack callstack, String pattern, Object[] values)
	{
		return MessageFormat.format(pattern, values);
	}

}
