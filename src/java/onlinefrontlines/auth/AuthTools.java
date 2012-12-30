package onlinefrontlines.auth;

import java.security.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Helper tools for authentication
 * 
 * @author jorrit
 * 
 * Copyright (C) 2009-2013 Jorrit Rouwe
 * 
 * This file is part of Online Frontlines.
 *
 * Online Frontlines is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Online Frontlines is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Online Frontlines.  If not, see <http://www.gnu.org/licenses/>.
 */
public class AuthTools 
{
	/**
	 * Convert string into byte array (discarding high bits of unicode characters)
	 * 
	 * @param in String to convert
	 * @return Byte array
	 */	
	public static byte[] stringToByteArray(String in)
	{
        byte[] res = new byte[in.length()];
        
        for (int i = 0; i < in.length(); ++i)
        	res[i] = (byte)in.charAt(i);
        
        return res;		
	}
	
	/**
	 * Convert byte array to string
	 * 
	 * @param in Byte array to convert
	 * @return String value
	 */
	public static String byteArrayToString(byte[] in)
	{
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < in.length; ++i)
        	sb.append((char)in[i]);
        
        return sb.toString();
	}
	
	/**
	 * Convert byte array to hex string
	 * 
	 * @param in Byte array to convert
	 * @return Hex string
	 */
	public static String byteArrayToHexString(byte[] in)
	{
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < in.length; ++i)
        {
        	int d = ((int)in[i]) & 0xff;
        	
        	String s = Integer.toHexString(d);
        	if (d < 0x10)
        		sb.append("0" + s);
        	else
        		sb.append(s);
        }
        
        return sb.toString();		
	}
	
	/**
	 * Convert hex string to byte array
	 * 
	 * @param in Hex string to convert
	 * @return Byte array
	 */
	public static byte[] hexStringToByteArray(String in)
	{
		byte[] b = new byte[in.length() / 2];
		
		for (int i = 0; i < in.length(); i += 2)
			b[i / 2] = (byte)Integer.parseInt(in.substring(i, i + 2), 16);
		
		return b;
	}
	
	/**
	 * Creates an 8 byte salt
	 *  
	 * @return 16 character hex string
	 * @throws Exception
	 */
	public static String generateSalt() throws Exception
	{
		SecureRandom r = new SecureRandom();
		byte[] bytes = new byte[8];
		r.nextBytes(bytes);
		return byteArrayToHexString(bytes);		
	}

	/**
	 * Hash a value using the HmacMD5 hash function
	 * 
	 * @param in String to hash
	 * @param salt The salt to use
	 * @return Hashed value as a hex string
	 * @throws Exception
	 */
	public static String hashHmacMD5(String in, String salt) throws Exception
	{
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(new SecretKeySpec(stringToByteArray(salt), "HmacMD5"));
        return byteArrayToHexString(mac.doFinal(in.getBytes()));		
	}
	
	/**
	 * Hash a value using the MD5 hash function
	 * 
	 * @param in Value to hash 
	 * @return Hashed value as a hex string
	 * @throws Exception
	 */
	public static String hashMD5(String in) throws Exception
	{
		 MessageDigest md = MessageDigest.getInstance("MD5");
		 return byteArrayToHexString(md.digest(in.getBytes()));
	}
			
	/**
	 * Sign text string
	 * 
	 * @param in Text to sign
	 * @param privateKey The private key
	 * @return Signature as hex string
	 * @throws Exception
	 */
	public static String sign(String in, PrivateKey privateKey) throws Exception
	{
        Signature s = Signature.getInstance("SHA1withRSA");
        s.initSign(privateKey);
        s.update(in.getBytes());
        return AuthTools.byteArrayToHexString(s.sign());
	}
		
	/**
	 * Verify signed text
	 * 
	 * @param in Text that was signed
	 * @param signature Signature as hex string
	 * @param publicKey The public key
	 * @return True if signature is valid
	 * @throws Exception
	 */
	public static boolean verifySignature(String in, String signature, PublicKey publicKey) throws Exception
	{
		Signature v = Signature.getInstance("SHA1withRSA");
		v.initVerify(publicKey);
		v.update(in.getBytes());
		return v.verify(hexStringToByteArray(signature));
	}
}
