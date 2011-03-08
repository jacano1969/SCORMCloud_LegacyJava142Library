package com.rusticisoftware.hostedengine.client;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class RequestSigner {
	
	private static List excludedParams = Arrays.asList(new String[]{ "sig", "filedata" });
	public static boolean isExcludedParam(String paramName){
		return excludedParams.contains(paramName);
	}
	
	// Return a hex string representing the MD5 hash of the secret key and request params
	// *In a client library, the secret key could be made into a class member of this class.
	//public static String getSignatureForRequest(Map requestParams, String secretKey) throws WebServiceException
	public static String getSignatureForRequest(Map requestParams, String secretKey) throws Exception
	{	
		try {
			String serializedRequestString = getSerializedParams(requestParams);
			
			String signatureParts = secretKey + serializedRequestString;
			byte[] encodedSigParts = signatureParts.getBytes("UTF8");
			byte[] generatedSignature = MessageDigest.getInstance("MD5").digest(encodedSigParts);

			return getHexString(generatedSignature);
			
		} catch (NoSuchAlgorithmException nsae) {
			return "";
		} catch (UnsupportedEncodingException uee) {
			return "";
		}
	}
	
	// Return the serialized request string. 
	public static String getSerializedParams(Map requestParams)
	{	
		StringBuffer paramString = new StringBuffer();
		List paramNames = new ArrayList(requestParams.keySet());
		Collections.sort(paramNames, String.CASE_INSENSITIVE_ORDER);
		
		//for (String paramName : paramNames) {
		Iterator it = paramNames.iterator();
		while(it.hasNext()){
			String paramName = (String)it.next();
			if (!isExcludedParam(paramName)){				
				paramString.append(paramName);
				
				Object val = requestParams.get(paramName);
				
				// Allow the value to be a single string or
				// an array of strings as is returned by
				// request.getParameterMap()
				if (val instanceof String[]) {	
					List valList = Arrays.asList((String[])val);
					Collections.sort(valList);
					Iterator it2 = valList.iterator();
					while(it2.hasNext()){
						String v = (String)it2.next();
						paramString.append(v);
					}
				} else {
					paramString.append((String)val);
				}
			}
		}
		return paramString.toString();
	}
	
	// Return a hex string representation of the passed in byte array
	protected static String getHexString(byte[] input){
		char hexDigit[] = {
	       '0', '1', '2', '3', '4', '5', '6', '7',
	       '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	    };
	    
		StringBuffer hexString = new StringBuffer();
		for(int i = 0; i < input.length; i++){
			byte b = input[i];
			char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
	    	hexString.append(array);
		}
		return hexString.toString();
	}
	
}