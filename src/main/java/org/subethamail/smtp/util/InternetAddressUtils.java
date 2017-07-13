package org.subethamail.smtp.util;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * @author Sean Scott
 */
public class InternetAddressUtils
{
	/**
	 * @return true if the string is a valid email address
	 */
	public static boolean isValidSingleAddress(String address)
	{
		if (address == null || address.length() == 0)
			return true;

		boolean result = false;
		try
		{
			InternetAddress[] ia = InternetAddress.parse(address, true);
			if (ia.length != 1)
				result = false;
			else
				result = true;
		}
		catch (AddressException ae)
		{
			result = false;
		}
		return result;
	}
}
