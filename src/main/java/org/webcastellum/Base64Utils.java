package org.webcastellum;

import java.io.ByteArrayOutputStream;

public final class Base64Utils {
    
    
    private static final boolean URL_SAFE = true;
    private static final boolean LINE_WRAP = false;
    
    private static final String B64CHARS_STANDARD = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final String B64CHARS_URL_SAFE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
    private static final char[] B64CHARS = (URL_SAFE ? B64CHARS_URL_SAFE : B64CHARS_STANDARD).toCharArray();;
    
    private static final String ILLEGAL_STRING = "Invalid Base64 encoding";

    
    
    
    /**
     *  Encodes binary data by BASE64 method.
     *  @param  data binary data to encode
     *  @return BASE64 encoded data 
     */
    public static String encode(final byte[] data) {
	final char output[] = new char[4];
	byte state = 1;
	int restbits = 0;
        int chunks = 0;

	final StringBuilder encoded = new StringBuilder();
    	for(int i=0; i < data.length; i++) {
            int ic = (data[i] >= 0 ? data[i] : (data[i] & 0x7F) + 128);
            switch (state) {
            	case 1: output[0] = B64CHARS[ic >>> 2];
                     restbits = ic & 0x03;
                     break;
             	case 2: output[1] = B64CHARS[(restbits << 4) | (ic >>> 4)];
                     restbits = ic & 0x0F;
                     break;
             	case 3: output[2] = B64CHARS[(restbits << 2) | (ic >>> 6)];
                     output[3] = B64CHARS[ic & 0x3F];
                     encoded.append(output);
                     // keep no more then 76 character per line
                     if (LINE_WRAP) {
                         chunks++;
                         if ((chunks % 19)==0) encoded.append("\r\n");
                     }
                     break;
            }
            state = (byte)(state < 3 ? state+1 : 1);
    	} // for

    	/* finalize */
    	switch (state) {
    	     case 2:
             	 output[1] = B64CHARS[(restbits << 4)];
                 output[2] = output[3] = (URL_SAFE?'.':'=');
                 encoded.append(output);
                 break;
             case 3:
             	 output[2] = B64CHARS[(restbits << 2)];
                 output[3] = (URL_SAFE?'.':'=');
		 encoded.append(output);
                 break;
    	}

	return encoded.toString();
    }
    
    
    
    
    /**
     *  Decodes BASE64 encoded string.
     *  @param encoded BASE64 string to decode
     *  @return decoded data
     */
    public static byte[] decode(final String encoded)  {
        int i;
    	byte output[] = new byte[3];
    	int state;

	ByteArrayOutputStream data = new ByteArrayOutputStream(encoded.length());

    	state = 1;
    	for(i=0; i < encoded.length(); i++) {
            byte c;
            {
            	char alpha = encoded.charAt(i);
            	if (Character.isWhitespace(alpha)) continue;

   		if ((alpha >= 'A') && (alpha <= 'Z')) c = (byte)(alpha - 'A');
   		else if ((alpha >= 'a') && (alpha <= 'z')) c = (byte)(26 + (alpha - 'a'));
		else if ((alpha >= '0') && (alpha <= '9')) c = (byte)(52 + (alpha - '0'));
	   	else if (alpha==(URL_SAFE?'-':'+')) c = 62;
   		else if (alpha==(URL_SAFE?'_':'/')) c = 63;
	   	else if (alpha==(URL_SAFE?'.':'=')) break; // end
   		else throw new IllegalArgumentException(ILLEGAL_STRING); // error
            }

            switch(state) {
                case 1: output[0] = (byte)(c << 2);
                        break;
                case 2: output[0] |= (byte)(c >>> 4);
                        output[1] = (byte)((c & 0x0F) << 4);
                        break;
                case 3: output[1] |= (byte)(c >>> 2);
                        output[2] =  (byte)((c & 0x03) << 6);
                        break;
                case 4: output[2] |= c;
                        data.write(output,0,output.length);
                        break;
            }
            state = (state < 4 ? state+1 : 1);
    	} // for

	if (i < encoded.length()) /* then '=' found, but the end of string */
            switch(state) {
                case 3: data.write(output,0,1);
                    if ((encoded.charAt(i)==(URL_SAFE?'.':'=')) && (encoded.charAt(i+1)==(URL_SAFE?'.':'=')))
                    	 return data.toByteArray();
                    else throw new IllegalArgumentException(ILLEGAL_STRING);

            	case 4:
            	    data.write(output,0,2);
                    if (encoded.charAt(i)==(URL_SAFE?'.':'=')) return data.toByteArray();
                    else throw new IllegalArgumentException(ILLEGAL_STRING);

            	default:
            	    throw new IllegalArgumentException(ILLEGAL_STRING);
            }
    	else { // end of string
    	    if (state==1) return data.toByteArray();
    	    else throw new IllegalArgumentException(ILLEGAL_STRING); 
    	}
    }
    
    

    
    
    private Base64Utils() {}
    
    
    
}

