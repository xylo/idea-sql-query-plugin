package bsh.commands;

import bsh.Interpreter;
import bsh.CallStack;

/**
 * @noinspection UtilityClassWithoutPrivateConstructor
 */
public class fillLeft
{
	/** @noinspection UNUSED_SYMBOL*/
	public static String invoke(Interpreter env, CallStack callstack, String text, String filler, int length)
	{
		StringBuffer buffer=new StringBuffer(length);
		if (text!=null) buffer.append(text);
		while (buffer.length()<length) buffer.insert(0, filler);
		return buffer.toString();
	}
}
