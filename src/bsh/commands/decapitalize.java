package bsh.commands;

import bsh.Interpreter;
import bsh.CallStack;

/**
 * @noinspection UtilityClassWithoutPrivateConstructor
 */
public class decapitalize
{
	/** @noinspection UNUSED_SYMBOL*/
	public static String invoke(Interpreter env, CallStack callstack, String text)
	{
		if (text==null || text.length()<1) return text;
		char[] characters=text.toCharArray();
		characters[0]=Character.toLowerCase(characters[0]);
		return new String(characters);
	}
}
