package se.l4.commons.config.sources;

import java.util.regex.Pattern;

import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.RichIterable;

/**
 * Helpers related to configuration keys.
 */
public class ConfigKeys
{
	private static Pattern INDEX = Pattern.compile("[0-9]+");

	private ConfigKeys()
	{
	}

	/**
	 * The delimiter that is used between paths.
	 */
	public static char PATH_DELIMITER = '.';

	/**
	 * Check if the given part of a path is valid.
	 *
	 * @param part
	 * @return
	 */
	public static boolean isValidPart(String part)
	{
		for(int i=0, n=part.length(); i<n; i++)
		{
			char c = part.charAt(i);
			if(c == PATH_DELIMITER || c == '[' || c == ']')
			{
				return false;
			}
		}

		return true;
	}

	public static boolean prefixMatches(String key, String prefix)
	{
		if(key.length() < prefix.length() + 1 || ! key.startsWith(prefix))
		{
			return false;
		}

		return key.charAt(prefix.length()) == '.';
	}

	public static IntIterable toList(RichIterable<String> subKeys)
	{
		return subKeys
			.select(k -> INDEX.matcher(k).matches())
			.collectInt(Integer::parseInt);
	}
}
