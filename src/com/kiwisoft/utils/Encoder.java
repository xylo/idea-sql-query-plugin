/*
 * Copyright (C) 2002-2006 Stefan Stiller
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.kiwisoft.utils;

import java.security.GeneralSecurityException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.4 $, $Date: 2006/03/24 18:15:19 $
 */
public class Encoder
{
	private static Encoder instance;

	public synchronized static Encoder getInstance()
	{
		if (instance==null) instance=new Encoder();
		return instance;
	}

	private final static String ALGORITHM="PBEWITHMD5ANDDES";
	private final static int ITERATIONS=20;
	private final static byte[] DEFAULT_SALT={
		(byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
		(byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
	};

	private Encoder()
	{
	}

    public static boolean isAvailable()
    {
		return Security.getAlgorithms("Cipher").contains("PBEWITHMD5ANDDES");
    }

	public String encrypt(String original, String encryptionKey) throws GeneralSecurityException
	{
		PBEParameterSpec pbeParamSpec=new PBEParameterSpec(DEFAULT_SALT, ITERATIONS);
		PBEKeySpec pbeKeySpec=new PBEKeySpec(encryptionKey.toCharArray());
		SecretKeyFactory keyFac=SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey pbeKey=keyFac.generateSecret(pbeKeySpec);
		Cipher pbeCipher=Cipher.getInstance(ALGORITHM);
		pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
		byte[] encryptedOriginal=pbeCipher.doFinal(original.getBytes());
		return StringUtils.toByteString(encryptedOriginal, null);
	}

	public String decrypt(String encryptedOriginal, String encryptionKey) throws GeneralSecurityException
	{
		PBEParameterSpec pbeParamSpec=new PBEParameterSpec(DEFAULT_SALT, ITERATIONS);
		PBEKeySpec pbeKeySpec=new PBEKeySpec(encryptionKey.toCharArray());
		SecretKeyFactory keyFac=SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey pbeKey=keyFac.generateSecret(pbeKeySpec);
		Cipher pbeCipher=Cipher.getInstance(ALGORITHM);
		pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
		byte[] originalBytes=pbeCipher.doFinal(StringUtils.toByteArray(encryptedOriginal));
		return new String(originalBytes);
	}
}
