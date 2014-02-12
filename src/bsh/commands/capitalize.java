package bsh.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bsh.Interpreter;
import bsh.CallStack;

/**
 * @noinspection UtilityClassWithoutPrivateConstructor
 */
public class capitalize
{
	/** @noinspection UNUSED_SYMBOL*/
	public static String invoke(Interpreter env, CallStack callstack, String text)
	{
		if (text==null || text.length()<1) return text;
		char[] characters=text.toCharArray();
		characters[0]=Character.toUpperCase(characters[0]);
		return new String(characters);
	}
}
