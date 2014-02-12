package bsh.commands;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import bsh.Interpreter;
import bsh.CallStack;

/**
 * @noinspection UtilityClassWithoutPrivateConstructor
 */
public class match
{
	/** @noinspection UNUSED_SYMBOL*/
	public static String[] invoke(Interpreter env, CallStack callstack, String value, String pattern)
	{
		Matcher matcher=Pattern.compile(pattern).matcher(value);
		if (matcher.matches())
		{
			String[] groups=new String[matcher.groupCount()+1];
			for (int i=0; i<=matcher.groupCount(); i++) groups[i]=matcher.group(i);
			return groups;
		}
		return null;
	}

}
