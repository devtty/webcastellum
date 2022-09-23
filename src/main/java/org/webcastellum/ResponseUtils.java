package org.webcastellum;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class ResponseUtils {
    
    private ResponseUtils() {}
    
    private static final Logger LOGGER = Logger.getLogger(ResponseUtils.class.getName());

    // TODO: konfigurierbar machen (aber immer capturing group 1 oder diese auch konfigurierbar machen)
    private static final Pattern PATTERN_FORM_ACTION = Pattern.compile("(?i)(?s)action\\s*=\\s*[\"']?([^\"'\\s>]*)[\"']?");
            
    
    // TODO: diese regexps patterns hier durch einfachere zeichenwese operationen ersetzen bzw. aho-corasick davor


    static final Pattern HTML_PATTERN_FIELD_NAME_DOUBLE_QUOTED = Pattern.compile("(?i)(?s)name\\s*=\\s*[\"]([^\"]*)[\"]");    
    static final Pattern HTML_PATTERN_FIELD_NAME_SINGLE_QUOTED = Pattern.compile("(?i)(?s)name\\s*=\\s*[']([^']*)[']");    
    static final Pattern HTML_PATTERN_FIELD_NAME_NOT_QUOTED = Pattern.compile("(?i)(?s)name\\s*=\\s*([^\\s>]*)");    
    
    static final Pattern HTML_PATTERN_FIELD_VALUE_DOUBLE_QUOTED = Pattern.compile("(?i)(?s)value\\s*=\\s*[\"]([^\"]*)[\"]");    
    static final Pattern HTML_PATTERN_FIELD_VALUE_SINGLE_QUOTED = Pattern.compile("(?i)(?s)value\\s*=\\s*[']([^']*)[']");    
    static final Pattern HTML_PATTERN_FIELD_VALUE_NOT_QUOTED = Pattern.compile("(?i)(?s)value\\s*=\\s*([^\\s>]*)");    
    
    static final Pattern HTML_PATTERN_FIELD_ENCTYPE_DOUBLE_QUOTED = Pattern.compile("(?i)(?s)enctype\\s*=\\s*[\"]([^\"]*)[\"]");    
    static final Pattern HTML_PATTERN_FIELD_ENCTYPE_SINGLE_QUOTED = Pattern.compile("(?i)(?s)enctype\\s*=\\s*[']([^']*)[']");    
    static final Pattern HTML_PATTERN_FIELD_ENCTYPE_NOT_QUOTED = Pattern.compile("(?i)(?s)enctype\\s*=\\s*([^\\s>]*)");    
    
    static final Pattern HTML_PATTERN_FIELD_ACTION_DOUBLE_QUOTED = Pattern.compile("(?i)(?s)action\\s*=\\s*[\"]([^\"]*)[\"]");    
    static final Pattern HTML_PATTERN_FIELD_ACTION_SINGLE_QUOTED = Pattern.compile("(?i)(?s)action\\s*=\\s*[']([^']*)[']");    
    static final Pattern HTML_PATTERN_FIELD_ACTION_NOT_QUOTED = Pattern.compile("(?i)(?s)action\\s*=\\s*([^\\s>]*)");    
    

//    static final Pattern HTML_PATTERN_LINK_ANY_DIRECT = Pattern.compile("(?i)(?s)\\s+(href|action|src|source)\\s*=\\s*['\"][^'\"]+['\"]"); // here with "\\s+" and within-tag-check



    private static final String DISABLED_LOWERCASED = "disabled";
    private static final String READONLY_LOWERCASED = "readonly";
    private static final String MULTIPLE_LOWERCASED = "multiple";
    
    

    
    public static String extractURI(final String value) { // TODO: move to ServerUtils
        if (value == null) return null;
        // simplified version that simply returns the value up to the first (question-mark "?" or semi-colon ";" or hash "#")
        int posQM = value.indexOf('?');
        int posSC = value.indexOf(';');
        int posHash = value.indexOf('#');
        if (posQM != -1 || posSC != -1 || posHash != -1) {
            if (posQM == -1) posQM = Integer.MAX_VALUE;
            if (posSC == -1) posSC = Integer.MAX_VALUE;
            if (posHash == -1) posHash = Integer.MAX_VALUE;
            final int pos = Math.min( Math.min(posQM,posSC), posHash );
            if (pos != Integer.MAX_VALUE) return value.substring(0, pos);
        }
        return value;
    }
    

    
    private static int determineAnchorPos(final String url, final boolean isAlreadyFullyDecoded) {
        return determineAnchorPos(url, isAlreadyFullyDecoded, 0);
    }
    private static int determineAnchorPos(final String url, final boolean isAlreadyFullyDecoded, final int startAtIndex) {
        // TODO: hier aufpassen, wenn die URL ein encoding hat mit &#070; als "p" z.B. dass nicht dann dort das # als Anker gewertet wird... 
        // erstmal hier ne simple loesung eingebaut mit temporaerem Ersetzen von &# durch &_ zum Suchen
        return url.replaceAll("&#","&_").indexOf('#', startAtIndex);
    }
    
    
    



    // modifies the concrete link URL
    public static String injectParameterIntoURL(String url, final String tokenKey, final String tokenValue/*, final String cryptoDetectionString*/, final boolean maskAmpersandsInModifiedLinks, final boolean appendQuestionmarkOrAmpersandToLinks, 
            final boolean urlAlreadyDecodedAndDoesNotNeedToBeEncodedAndStartsWithCheckAlreadyDone // = performance tuning (see caller) to optimize this method and let the caller take care of some stuff
            ) {
        if (url == null) return null;
        if (tokenKey == null || tokenValue == null) return url;

        if (!urlAlreadyDecodedAndDoesNotNeedToBeEncodedAndStartsWithCheckAlreadyDone) {
            final String urlDecoded = ServerUtils.decodeBrokenValueHtmlOnly(url, false);
            if (ServerUtils.startsWithJavaScriptOrMailto(urlDecoded)) return url;
            url = urlDecoded;
        }

        
        if (url.contains(tokenKey)) return url; // TODO: ist der Check hier noetig?
        final StringBuilder result = new StringBuilder(url.length());
        // temporarily remove any anchor
        String anchor = null; final int anchorPos = determineAnchorPos(url,false);
        if (anchorPos > -1) {
            anchor = url.substring(anchorPos);
            if (anchorPos > 0) result.append( url.substring(0,anchorPos) );
        } else result.append(url);
        // append token
        if (url.indexOf('?') >= 0) result.append( maskAmpersandsInModifiedLinks ? "&amp;" : "&" ); else result.append('?');
        result.append(tokenKey).append('=').append(tokenValue);
        if (appendQuestionmarkOrAmpersandToLinks) result.append('&'); // TODO: klappt das denn auch mit Ankern #123 noch sauber ? oder bei mehrfachen aufrufen (weil es ja einen CSRF-Token gibt und einen Form-Proetction-Token)?
        // re-append anchor (if it had one before)
        if (anchor != null) result.append(anchor);
        
        
        return urlAlreadyDecodedAndDoesNotNeedToBeEncodedAndStartsWithCheckAlreadyDone ? result.toString() : ServerUtils.encodeHtmlSafe(result.toString());
    }

    
    
    
    
    
        
    // setzt den Wert des "value" attributs des tags
    public static String setFieldValue(String tag, String valueOfValueAttribute) {
        if (tag == null) return null;
        if (valueOfValueAttribute == null) valueOfValueAttribute = "";
        tag = tag.trim();
        // at first, remove any existing value attributes
        char quote = '\"'; // default double qouted
        Matcher matcher;
        // double quoted "
        matcher = HTML_PATTERN_FIELD_VALUE_DOUBLE_QUOTED.matcher(tag);
        if (matcher.find()) {
            quote = '\"';
            tag = matcher.replaceAll(" "); // replace with a single space
        }
        // single quoted '
        matcher = HTML_PATTERN_FIELD_VALUE_SINGLE_QUOTED.matcher(tag);
        if (matcher.find()) {
            quote = '\'';
            tag = matcher.replaceAll(" "); // replace with a single space
        }
        // work
        int positionOfFirstSpace = tag.indexOf(' ');
        int positionOfFirstSlash = tag.indexOf('/');
        int positionOfFirstGreaterThan = tag.indexOf('>');
        if (positionOfFirstSpace == -1) positionOfFirstSpace = Integer.MAX_VALUE;
        if (positionOfFirstSlash == -1) positionOfFirstSlash = Integer.MAX_VALUE;
        if (positionOfFirstGreaterThan == -1) positionOfFirstGreaterThan = Integer.MAX_VALUE;
        final int pos = Math.min( Math.min(positionOfFirstSpace,positionOfFirstSlash), positionOfFirstGreaterThan );
        return tag.substring(0,pos) + " value="+quote+valueOfValueAttribute+quote+" " + tag.substring(pos);
    }
    // setzt den Wert des "action" attributs des tags
    public static String setFieldAction(String tag, String valueOfActionAttribute) {
        if (tag == null) return null;
        if (valueOfActionAttribute == null) valueOfActionAttribute = "";
        tag = tag.trim();
        // at first, remove any existing action attributes
        char quote = '\"'; // default double qouted
        Matcher matcher;
        // double quoted "
        matcher = HTML_PATTERN_FIELD_ACTION_DOUBLE_QUOTED.matcher(tag);
        if (matcher.find()) {
            quote = '\"'; // we will also quote our new action using the " style
            tag = matcher.replaceAll(" "); // replace with a single space
        } else {
            // single quoted '
            matcher = HTML_PATTERN_FIELD_ACTION_SINGLE_QUOTED.matcher(tag);
            if (matcher.find()) {
                quote = '\''; // we will also quote our new action using the ' style
                tag = matcher.replaceAll(" "); // replace with a single space
            } else {
                // not quoted
                matcher = HTML_PATTERN_FIELD_ACTION_NOT_QUOTED.matcher(tag);
                if (matcher.find()) {
                    quote = '\"'; // we will insert our new action quoted properly (" style), even though the removed one was not quoted at all
                    tag = matcher.replaceAll(" "); // replace with a single space
                }
            }
        }
        // work
        int positionOfFirstSpace = tag.indexOf(' ');
        int positionOfFirstSlash = tag.indexOf('/');
        int positionOfFirstGreaterThan = tag.indexOf('>');
        if (positionOfFirstSpace == -1) positionOfFirstSpace = Integer.MAX_VALUE;
        if (positionOfFirstSlash == -1) positionOfFirstSlash = Integer.MAX_VALUE;
        if (positionOfFirstGreaterThan == -1) positionOfFirstGreaterThan = Integer.MAX_VALUE;
        final int pos = Math.min( Math.min(positionOfFirstSpace,positionOfFirstSlash), positionOfFirstGreaterThan );
        return tag.substring(0,pos) + " action="+quote+valueOfActionAttribute+quote+" " + tag.substring(pos);
    }
    
    // extrahiert den Wert des "value" attributs des tags
    public static String extractFieldValue(final String tag) {  // TODO: reuse matcher
        if (tag == null) return null;
        Matcher matcher;
        // double quoted "
        matcher = HTML_PATTERN_FIELD_VALUE_DOUBLE_QUOTED.matcher(tag);
        if ( matcher.find() ) return matcher.group(1); //return extractAttributeValueFromAttributeAndValueString( matcher.group() );
        // single quoted '
        matcher = HTML_PATTERN_FIELD_VALUE_SINGLE_QUOTED.matcher(tag);
        if ( matcher.find() ) return matcher.group(1); //return extractAttributeValueFromAttributeAndValueString( matcher.group() );
        // not quoted
        matcher = HTML_PATTERN_FIELD_VALUE_NOT_QUOTED.matcher(tag);
        if ( matcher.find() ) return matcher.group(1).trim(); //return extractAttributeValueFromAttributeAndValueString( matcher.group() );
        return null;
    }
    
    // extrahiert den Wert des "enctype" attributs des tags
    public static String extractFieldEnctype(final String tag) {  // TODO: reuse matcher
        if (tag == null) return null;
        Matcher matcher;
        // double quoted "
        matcher = HTML_PATTERN_FIELD_ENCTYPE_DOUBLE_QUOTED.matcher(tag);
        if ( matcher.find() ) return matcher.group(1); //return extractAttributeValueFromAttributeAndValueString( matcher.group() );
        // single quoted '
        matcher = HTML_PATTERN_FIELD_ENCTYPE_SINGLE_QUOTED.matcher(tag);
        if ( matcher.find() ) return matcher.group(1); //return extractAttributeValueFromAttributeAndValueString( matcher.group() );
        // not quoted
        matcher = HTML_PATTERN_FIELD_ENCTYPE_NOT_QUOTED.matcher(tag);
        if ( matcher.find() ) return matcher.group(1).trim(); //return extractAttributeValueFromAttributeAndValueString( matcher.group() );
        return null;
    }
    
    // extrahiert den Wert des "name" attributs des tags
    public static String extractFieldName(final String tag) {  // TODO: reuse matcher
        if (tag == null) return null;
        Matcher matcher;
        // double quoted "
        matcher = HTML_PATTERN_FIELD_NAME_DOUBLE_QUOTED.matcher(tag);
        if ( matcher.find() ) return matcher.group(1); //return extractAttributeValueFromAttributeAndValueString( matcher.group() );
        // single quoted '
        matcher = HTML_PATTERN_FIELD_NAME_SINGLE_QUOTED.matcher(tag);
        if ( matcher.find() ) return matcher.group(1); //return extractAttributeValueFromAttributeAndValueString( matcher.group() );
        // not quoted
        matcher = HTML_PATTERN_FIELD_NAME_NOT_QUOTED.matcher(tag);
        if ( matcher.find() ) return matcher.group(1).trim(); //return extractAttributeValueFromAttributeAndValueString( matcher.group() );
        return null;
    }
    
    
    
    
    public static final boolean isMultipartForm(final String tag) {
        final String enctype = extractFieldEnctype(tag);
        return enctype != null && enctype.trim().toLowerCase().startsWith("multipart/form-data");
    }
    
    
    

    // TODO: make configurable via web.xml.... false ist hier besser, da unabhaengig vom contextPath
    private static final boolean USE_CONTEXT_PATH_FOR_REMOVED_URL_TARGETS = false; // false ist sicherer, da dann ein zufalls-wert genommen wird, und dies Spider und Bots verwirrt ;-)
    private static final boolean USE_CONTEXT_PATH_FOR_REMOVED_FORM_TARGETS = false; // false ist sicherer, da dann ein zufalls-wert genommen wird, und dies Spider und Bots verwirrt ;-)
    private static final boolean ALWAYS_INSERT_CRYPTO_DETECTION_STRING_AFTER_FIRST_QUESTIONMARK = false; // true ist *nicht* zulaessig, da bei true (also bei immer statischer position des crypto-detection strings) bei non-static resources keine Differenzierung mehr zwischen Crypto-Params per action-less GET-FORM vs. per URL moeglich ist oder nur schwer.... also besser auf "false" lassen
    

    
    public static final boolean isAlreadyEncrypted(final String cryptoDetectionString, final String url) {
        
        LOGGER.log(Level.FINE, "cryptoDetectionString={0} and url={1}", new Object[]{cryptoDetectionString, url});
        
        if (cryptoDetectionString == null) return false;
        if (!ALWAYS_INSERT_CRYPTO_DETECTION_STRING_AFTER_FIRST_QUESTIONMARK) {
            return url.contains(cryptoDetectionString);
        }
        int zeigerDetection = -2; 
        for (int posInUrl=0; posInUrl<url.length(); posInUrl++) {
            if (zeigerDetection == -2) {
                if (url.charAt(posInUrl) == '?') {
                    zeigerDetection = -1;
                }
            } else {
                zeigerDetection++;
                if (zeigerDetection >= cryptoDetectionString.length()) return true; // = alle relevanten Zeichen erfolgreich verglichen
                if (url.charAt(posInUrl) != cryptoDetectionString.charAt(zeigerDetection)) return false;
            }
        }
    
        LOGGER.log(Level.FINE, "zeigerDetection={0} cryptoDetectionString.length()={1}", new Object[]{zeigerDetection, cryptoDetectionString.length()});
    
        return zeigerDetection >= cryptoDetectionString.length(); // weil wenn url zu kurz, dann nada...
        
        
        /*OLD: works, but unfortunately not faster
        final int firstQuestionMark = url.indexOf('?');
        if (firstQuestionMark == -1) return false;
        final int maxToCheck = firstQuestionMark+cryptoDetectionString.length();
        if (url.length() < maxToCheck) return false;
        int posInCryptoDetectionString = 0;
        for (int i=firstQuestionMark+1; i<maxToCheck; i++) {
            if (url.charAt(i) != cryptoDetectionString.charAt(posInCryptoDetectionString++)) return false;
        }
        return true;
        */
    }
    
    
    // isRequestMethodPOST = true when the target is a form of type POST submit; false when it is a GET link; null when it is unknown
    // TODO: Java5: Use an enum instead of Boolean.TRUE,null,Boolean.FALSE ?
    public static String encryptQueryStringInURL(final String currentRequestUrlToCompareWith, final String currentContextPathAccessed, final String currentServletPathAccessed, String url, final boolean isFormAction, final boolean isMultipartForm, final Boolean isRequestMethodPOST, final boolean isStaticResource, final String cryptoDetectionString, 
            final Cipher cipher, final CryptoKeyAndSalt key, final boolean useFullPathForResourceToBeAccessedProtection, final boolean additionalFullResourceRemoval, final boolean additionalMediumResourceRemoval, final HttpServletResponse response, final boolean appendQuestionmarkOrAmpersandToLinks) {
        if (url == null) return null;
        // diese NPE Checks hier auskommentiert, da sie bei null ja weiter unten eh auch einen NPE werfen...
        //if (key == null) throw new NullPointerException("key must not be null");
        //if (cipher == null) throw new NullPointerException("cipher must not be null");
        //if (cryptoDetectionString == null) throw new NullPointerException("cryptoDetectionString must not be null"); // cryptoDetectionString is used to detect encrypted links
        if (additionalFullResourceRemoval && additionalMediumResourceRemoval) throw new IllegalArgumentException("additionalFullResourceRemoval AND additionalMediumResourceRemoval is impossible");
        url = ServerUtils.decodeBrokenValueExceptUrlEncoding(url);
        //OLD if (url.indexOf(cryptoDetectionString) > -1) return url; // = query string is already encrypted
        //OLD if (!ServerUtils.isInternalHostURL(currentRequestUrlToCompareWith,url)) return url; // = if URL is on another host (i.e. an external link) don't encrypt the query string
        final int firstQuestionMark = url.indexOf('?'); // Note that when encrypting links they always have querystring params due to the token injections that happened before
        if (firstQuestionMark > -1 && url.length() > firstQuestionMark+1) {
            try {
                boolean resourceEndsWithSlash = false;
                // determine the resource that is to be accessed

                LOGGER.log(Level.FINE, "url={0}", url);
                
                final String leaveUnencrypted = url.substring(0,firstQuestionMark+1);
                final int lastCharIndex = leaveUnencrypted.length()-1;
                /////////// 20090804-CSR final boolean endsWithRelativePath = lastCharIndex > 2 && leaveUnencrypted.charAt(lastCharIndex) == '?' && leaveUnencrypted.charAt(lastCharIndex-1) == '.' && leaveUnencrypted.charAt(lastCharIndex-2) == '.' && leaveUnencrypted.charAt(lastCharIndex-3) == '/'; // endsWith("/..?")
                if ( //endsWithRelativePath ||
                         (lastCharIndex > 0 && leaveUnencrypted.charAt(lastCharIndex) == '?' && leaveUnencrypted.charAt(lastCharIndex-1) == '/')   // endsWith("/?")
                        ) {
                    resourceEndsWithSlash = true; // it is a directory so in can be safely left unencrypted
//                    leaveUnencrypted = leaveUnencrypted.substring(0, leaveUnencrypted.length()-2) + "?";
                }
                LOGGER.log(Level.FINE, "leaveUnencrypted: {0}", leaveUnencrypted);
                
                String resourceToBeAccessed = ServerUtils.extractResourceToBeAccessed(leaveUnencrypted, currentContextPathAccessed, currentServletPathAccessed, useFullPathForResourceToBeAccessedProtection);
                if (resourceToBeAccessed == null || resourceToBeAccessed.trim().length() == 0) {
                    if (isFormAction) {
                        // it seems to be a form with a self-submit (i.e. an empty action)...
                        // so on an self-submit we are safe to take the current page (current resource) as resourceToBeAccessed
                        resourceToBeAccessed = ServerUtils.extractResourceToBeAccessed(currentRequestUrlToCompareWith, currentContextPathAccessed, currentServletPathAccessed, useFullPathForResourceToBeAccessedProtection);
                    } else {
                        resourceToBeAccessed = ""; // default page in that directory
                    }
//                } else if (endsWithRelativePath) {
//                    resourceToBeAccessed += "/..";
                }
                
                LOGGER.log(Level.FINE, "resourceToBeAccessed={0}", resourceToBeAccessed);
                // temporarily remove any anchor
                String anchor = null; final int anchorPos = determineAnchorPos(url,true);
                StringBuilder encrypt = null;
                if (anchorPos > -1) {
                    anchor = url.substring(anchorPos);
                    encrypt = new StringBuilder( url.substring(firstQuestionMark+1, anchorPos) );
                } else {
                    encrypt = new StringBuilder( url.substring(firstQuestionMark+1) );
                }
                // encrypt the query-string (and include the resourceToBeAccessed into the encrypted result for double-check; as well as include flag if link is of method type GET or POST)
                final char requestMethodGetOrPostFlag;
                if (isRequestMethodPOST == null) requestMethodGetOrPostFlag = WebCastellumFilter.INTERNAL_METHOD_TYPE_UNDEFINED; // = unknown at HTML parsing time since it might be dynamic JavaScript for example
                else requestMethodGetOrPostFlag = isRequestMethodPOST.booleanValue() ? WebCastellumFilter.INTERNAL_METHOD_TYPE_POST: WebCastellumFilter.INTERNAL_METHOD_TYPE_GET;
                
                LOGGER.log(Level.FINE, "Using GET/POST flag: "+requestMethodGetOrPostFlag);
                // BUILD ENCRYPTED STRING
                encrypt.append(WebCastellumFilter.INTERNAL_URL_DELIMITER).append(resourceToBeAccessed).append(WebCastellumFilter.INTERNAL_URL_DELIMITER).append(requestMethodGetOrPostFlag);
                encrypt.append(WebCastellumFilter.INTERNAL_URL_DELIMITER).append((isFormAction?WebCastellumFilter.INTERNAL_TYPE_FORM_FLAG:WebCastellumFilter.INTERNAL_TYPE_LINK_FLAG));
                encrypt.append(WebCastellumFilter.INTERNAL_URL_DELIMITER).append((isMultipartForm?WebCastellumFilter.INTERNAL_MULTIPART_YES_FLAG:WebCastellumFilter.INTERNAL_MULTIPART_NO_FLAG));
                encrypt.append(WebCastellumFilter.INTERNAL_URL_DELIMITER).append((resourceEndsWithSlash?WebCastellumFilter.INTERNAL_RESOURCE_ENDS_WITH_SLASH_YES_FLAG:WebCastellumFilter.INTERNAL_RESOURCE_ENDS_WITH_SLASH_NO_FLAG));
                StringBuilder result = new StringBuilder(url.length()+20);
                final StringBuilder encryptedQueryString = new StringBuilder( CryptoUtils.encryptURLSafe(encrypt.toString(),key,cipher) );
                // place the crypto detection string at a random position within the encrypted string
                final int position;
                if (ALWAYS_INSERT_CRYPTO_DETECTION_STRING_AFTER_FIRST_QUESTIONMARK) {
                    position = 0; // 0 = at the start of the query string
                } else {
                    if (isStaticResource) { 
                        // for static files (even though encrypted URL) we always use the same position of the cryptoDetectionString in order to remain static and allow browser-caching here for static files (like images, icons, css, etc.) so that the browser has a chance to retrieve a HTTP 304 NOT-MODIFIED response instead of always the same resource every time transmitted freshly
                        position = Math.min(WebCastellumFilter.STATIC_REQUEST_CRYPTODETECTION_INSERTION_POSITION, encryptedQueryString.length());
                    } else {
                        position = CryptoUtils.generateRandomNumber(false,0,encryptedQueryString.length());
                    }
                }
                encryptedQueryString.insert(position, cryptoDetectionString);
                // re-create complete URL
                boolean useResponseEncodeURL = false;
                if (additionalFullResourceRemoval) { // FULL RESOURCE REMOVAL = remove the full resource (path + file)
                    final int firstSemicolon = leaveUnencrypted.indexOf(';'); // as we're using "leaveUnencrypted" which holds all up to the first question-mark, this is the left-most semicolon *before* the first question-mark, so this is correct!
                    final String prefix = USE_CONTEXT_PATH_FOR_REMOVED_URL_TARGETS ? currentContextPathAccessed+"/" : CryptoUtils.generateRandomNumber(false,1,9999)+"/../"; // using the random value is the safest, since this also confuses spiders and bots
                    if (firstSemicolon == -1) {
                        result.append(prefix).append("?");
                        //OLD if (WebCastellumFilter.EXPERIMENTAL_FEATURE__AUTO_APPEND_SESSIONID_IF_NECESSARY) useResponseEncodeURL = true;
                    } else {
                        final String sessionID = leaveUnencrypted.substring(firstSemicolon);
                        result.append(sessionID);
                        final String tmp = result.toString();
                        if (!USE_CONTEXT_PATH_FOR_REMOVED_URL_TARGETS || !tmp.startsWith(prefix)) result = new StringBuilder(prefix).append(tmp);
                        if (sessionID.charAt(sessionID.length()-1) != '?') result.append("?");
                    }
                } else if (additionalMediumResourceRemoval) { // MEDIUM RESOURCE REMOVAL = remove only file part of the resource
                    String fileToBeAccessed = ServerUtils.extractFileFromURL(leaveUnencrypted);
                    if (!resourceEndsWithSlash && ( fileToBeAccessed == null || fileToBeAccessed.trim().length() == 0 ) ) {
                        // it seems to be a form with a self-submit (i.e. an empty action)...
                        // so on an self-submit we are safe to take the current page (current resource) as resourceToBeAccessed
                        LOGGER.log(Level.FINE, "currentRequestUrlToCompareWith: {0}", currentRequestUrlToCompareWith);
                        fileToBeAccessed = ServerUtils.extractFileFromURL(currentRequestUrlToCompareWith);
                    }
                    if (fileToBeAccessed == null || fileToBeAccessed.trim().length() == 0) result.append(leaveUnencrypted);
                    else {
                        // replace file within URL with a random string
                        LOGGER.log(Level.FINE, "fileToBeAccessed: {0}", fileToBeAccessed);
                        final int pos = leaveUnencrypted.indexOf(fileToBeAccessed);
                        if (pos == -1) 
                            result.append(leaveUnencrypted);
                        else 
                            result.append(leaveUnencrypted.substring(0,pos))
                                    .append(CryptoUtils.generateRandomNumber(false,1,9999))
                                    .append(leaveUnencrypted.substring(pos+fileToBeAccessed.length()));
                    }
                    //OLD if (WebCastellumFilter.EXPERIMENTAL_FEATURE__AUTO_APPEND_SESSIONID_IF_NECESSARY) useResponseEncodeURL = true;
                } else {
                    result.append(leaveUnencrypted);
                }
                result.append(encryptedQueryString);
                if (WebCastellumFilter.APPEND_EQUALS_SIGN_TO_VALUELESS_URL_PARAM_NAMES) result.append("=").append(WebCastellumFilter.INTERNAL_TYPE_URL);
                if (appendQuestionmarkOrAmpersandToLinks) result.append('&'); // TODO: klappt das denn auch mit Ankern #123 noch sauber ? 
                if (anchor != null) result.append(anchor);
                LOGGER.log(Level.FINE, "encrypted: {0}", result);
                return useResponseEncodeURL ? response.encodeURL(result.toString()) : result.toString(); // encrypted URLs don't have any & in the query string since they only have 1 param
            } catch (InvalidKeyException | IllegalBlockSizeException | NoSuchAlgorithmException |BadPaddingException | NoSuchPaddingException | UnsupportedEncodingException | RuntimeException e) {
                Logger.getLogger(ResponseUtils.class.getCanonicalName()).log(Level.FINE, "Exception while encrypting the querystring" , e);
                return url;
            } 
        } else return url;
    }

    
    
    
    

    
    public static String getKeyForParameterProtectionOnly(final String link, final HttpSession session, final boolean hiddenFormFieldProtection, final boolean reuseSessionContent, final boolean applySetAfterWrite) {
        // create a fresh (empty) ParameterAndFormProtection since no form data is given, as the caller only wants to protect the URL Parameters
        final ParameterAndFormProtection parameterAndFormProtection = new ParameterAndFormProtection(hiddenFormFieldProtection);
        return getKeyForParameterAndFormProtection(link, parameterAndFormProtection, session, reuseSessionContent, applySetAfterWrite);
    }
    public static String getKeyForParameterAndFormProtection(String link, final ParameterAndFormProtection parameterAndFormProtection, final HttpSession session, final boolean reuseSessionContent, final boolean applySetAfterWrite) {
        if (link == null) return null;
        if (parameterAndFormProtection == null) throw new NullPointerException("parameterAndFormProtection must not be null");
        if (session == null) throw new NullPointerException("session must not be null");
// OLD        link = ServerUtils.decodeBrokenString(link);
        // add the URL-param names to the parameterAndFormProtection and fetches a ParameterAndFormProtection-Key for it from the session
        try {
            String queryString = ServerUtils.decodeBrokenValueExceptUrlEncoding(extractQueryStringOfActionUrl(link)); // TODO: decoding hier auch nochmal noetig??
            if (queryString == null) return ServerUtils.findReusableSessionContentKeyOrCreateNewOne(session, parameterAndFormProtection, reuseSessionContent, applySetAfterWrite);
            queryString = ServerUtils.unmaskAmpersandsInLink(queryString);
            final String[] parameters = queryString.split("&");
            for (String parameter : parameters) {
                final int equalsSignPos = parameter.indexOf('=');
                final String name = equalsSignPos > -1 ? parameter.substring(0,equalsSignPos) : parameter;
                final String nameDecoded = ServerUtils.decodeBrokenValue(name);
                parameterAndFormProtection.addParameterName(nameDecoded, false); // false since the URL params are encrypted and therefore always submitted unchanged
                // also track the request parameter value count of the link's params
                // TODO: only required to track when "ExtraRequestParamValueCountProtection" is configured to true:
                parameterAndFormProtection.incrementMinimumValueCountForParameterName(nameDecoded, 1);
                parameterAndFormProtection.incrementMaximumValueCountForParameterName(nameDecoded, 1);
                // in case also a readonly form field exists with the same name as the current URL query-string param, take that readonly form field out of the check...
                // TODO: only required to track when "ExtraReadonlyFormFieldProtection" is configured to true:
                parameterAndFormProtection.addReadwriteFieldName(nameDecoded); // TODO: anstelle hier das ganze readonly-form-feld aus dem readonlyness-schutz rauszunehmen, koennte man auch besser noch einfach den Wert des hier gerade aktiven URL-parameters mit in die Liste der expected values zu dem field-name nehmen... aber so wie es jetzt ist, ist es auch erstmal ok...
            }
            return ServerUtils.findReusableSessionContentKeyOrCreateNewOne(session, parameterAndFormProtection, reuseSessionContent, applySetAfterWrite);
        } catch (IllegalStateException e) {
            // TODO: better exception handling
            LOGGER.log(Level.WARNING, "Strange situation: session exists but is invalidated where it should be valid: {0}", link);
        }
        return null;
    }
    
    


    
    
    public static String extractActionUrlOfCurrentForm(final String tag, final boolean includeQueryString) {
        if (tag == null) return null;
        final Matcher matcher = PATTERN_FORM_ACTION.matcher(tag);
        if (matcher.find()) {
            //String actionURL = extractAttributeValueFromAttributeAndValueString( matcher.group() ).trim();
            String actionURL = matcher.group(1);
            if (!includeQueryString) actionURL = stripQueryString(actionURL);
            return actionURL.trim();
        }
        return ""; // = treat a missing action attribute like an empty action attribute
    }
    
    public static String extractQueryStringOfActionUrl(final String url) {
        if (url == null) return null;
        final int firstQuestionMarkPos = url.indexOf('?');
        if (firstQuestionMarkPos == -1 || firstQuestionMarkPos == url.length()-1) return null; //= no params
        final int anchorPos = determineAnchorPos(url, false, firstQuestionMarkPos); //url.indexOf('#', firstQuestionMarkPos); 
        final String queryString = anchorPos > firstQuestionMarkPos+1 ? url.substring(firstQuestionMarkPos+1,anchorPos) : url.substring(firstQuestionMarkPos+1);
        return queryString;
    }

    // works on a <form> tag
    public static String removeQueryStringFromActionUrlOfCurrentForm(String tag, boolean additionalFullResourceRemoval, final boolean additionalMediumResourceRemoval, final String contextPath, final HttpServletResponse response, final boolean appendQuestionmarkOrAmpersandToLinks, final boolean appendSessionIdToLinks) {
        // TODO: hier auch Anker #abc7 in URLs beachten ?!?
        if (tag == null) return null;
        if (additionalFullResourceRemoval && additionalMediumResourceRemoval) throw new IllegalArgumentException("additionalFullResourceRemoval AND additionalMediumResourceRemoval is impossible");
        String actionURL = extractActionUrlOfCurrentForm(tag, true); // hier hart mit true als parameter
        if (additionalFullResourceRemoval) { // FULL RESOURCE REMOVAL = remove the full resource (path + file)
            if (actionURL == null || actionURL.trim().length() == 0) return tag;
            String newActionURL;
            // use contextPath (or X/../) instead of empty form target since the forms with empty target inherit the previous requests URL params in HTML and this is something we don't want here
            final int posQM = actionURL.indexOf('?');
            final String actionUpToFirstQuestionmark = posQM == -1 ? actionURL : actionURL.substring(0,posQM);
            final int firstSemicolon = actionUpToFirstQuestionmark.indexOf(';');
            String prefix = USE_CONTEXT_PATH_FOR_REMOVED_FORM_TARGETS ? contextPath+"/" : CryptoUtils.generateRandomNumber(false,1,9999)+"/../"; // using the random value is the safest, since this also confuses spiders and bots
            if (firstSemicolon != -1) {
                final String sessionID = actionUpToFirstQuestionmark.substring(firstSemicolon);
                newActionURL = sessionID;
                if (!USE_CONTEXT_PATH_FOR_REMOVED_FORM_TARGETS || !newActionURL.startsWith(prefix)) newActionURL = prefix+newActionURL;
            } else {
                newActionURL = prefix;
                if (appendSessionIdToLinks) newActionURL = response.encodeURL(newActionURL);
            }
            if (appendQuestionmarkOrAmpersandToLinks) newActionURL += "?&";
            // replace action URL within tag with the newActionURL which holds a random number instead of the file name
            return setFieldAction(tag, newActionURL);
        } else { 
            // here (as *not* the *full* path removal is happening) it is required to handle a missing/empty action element in a form target
            if (actionURL == null) actionURL = "";
            if (additionalMediumResourceRemoval) { // MEDIUM RESOURCE REMOVAL = remove only file part of the resource
                String newActionURL = actionURL; // default is leave unchanged
                String fileToBeAccessed = ServerUtils.extractFileFromURL(actionURL);
                final String replacement = ""+CryptoUtils.generateRandomNumber(false,1,9999);
                if (fileToBeAccessed != null && fileToBeAccessed.trim().length() > 0) {
                    // replace file within URL with a random string
                    final int posFile = actionURL.indexOf(fileToBeAccessed);
                    if (posFile > -1) {
                        newActionURL = actionURL.substring(0,posFile)+replacement+actionURL.substring(posFile+fileToBeAccessed.length());
                    }
                    // also crop the query string
                    final int posQM = newActionURL.indexOf('?');
                    if (posQM > 0) newActionURL = newActionURL.substring(0,posQM);
                } else newActionURL = replacement;
                if (appendSessionIdToLinks) newActionURL = response.encodeURL(newActionURL);
                if (appendQuestionmarkOrAmpersandToLinks) newActionURL += "?&";
                // replace action URL within tag with the newActionURL which holds a random number instead of the file name
                return setFieldAction(tag, newActionURL);
            } else { // = SIMPLY REMOVE THE QUERY STRING AND LEAVE EVERYTHNG ELSE INTACT
                // remove the query string (since it has already been captured before and will be added as hidden field before the </form> closes; remove it to avoid duplicate query string vlaues
                String queryString = extractQueryStringOfActionUrl(actionURL);
                if (queryString != null && queryString.trim().length() > 0) {
                    queryString = '?'+queryString;
                    final int pos = tag.indexOf(queryString);
                    if (pos > -1) { // remove the querystring from the tag
                        return tag.substring(0,pos) 
                                // TODO doch nicht ??? + (WebCastellumFilter.EXPERIMENTAL_FEATURE__APPEND_QUESTIONMARK_OR_AMPERSAND_TO_MODIFIED_URLS_TO_SUPPORT_FURTHER_JAVASCRIPT_APPENDS?"?":"") 
                                + tag.substring(pos+queryString.length());
                    }
                }
                return tag;
            }
        }        
    }


    
    // works directly on an URL
    public static String stripQueryString(final String url) {
        if (url == null || url.trim().length() == 0) return url;
        String queryString = extractQueryStringOfActionUrl(url);
        if (queryString == null || queryString.trim().length() == 0) return url;
        queryString = '?'+queryString;
        final int pos = url.indexOf(queryString);
        if (pos > -1) {
            return url.substring(0,pos) + url.substring(pos+queryString.length());
        }
        return url;
    }
    
    
    
    
    public static String removeAttributeValues(final String tag) {
        if (tag == null) return null;
        final StringBuilder result = new StringBuilder();
        boolean isWithinAttributeValue = false;
        char attributeValueOpeningChar = 0;
        for (int i=0; i<tag.length(); i++) {
            final char c = tag.charAt(i);
            if (isWithinAttributeValue) {
                if (attributeValueOpeningChar == 0) {
                    if (c == '\"' || c == '\'') {
                        attributeValueOpeningChar = c;
                    } else result.append(c);
                } else {
                    if (c == attributeValueOpeningChar) {
                        isWithinAttributeValue = false;
                        attributeValueOpeningChar = 0;
                        result.append(' '); // = to have clean spaces between attributes
                    } else continue;
                }
            } else {
                if (c == '=') {
                    isWithinAttributeValue = true;
                } else result.append(c);
            }
        }
        return result.toString();
    }
    
    
    
    public static boolean isFormFieldDisabled(final String tag) {
        return isFormFieldHavingAttribute(tag, DISABLED_LOWERCASED);
    }
    public static boolean isFormFieldReadonly(final String tag) {
        return isFormFieldHavingAttribute(tag, READONLY_LOWERCASED);
    }
    public static boolean isFormFieldMultiple(final String tag) {
        return isFormFieldHavingAttribute(tag, MULTIPLE_LOWERCASED);
    }
    public static boolean isFormFieldHavingAttribute(final String tag, final String attribute) {
        if (tag == null) return false;
        final String withoutValuesLowerCased = removeAttributeValues(tag).toLowerCase();
        final int pos = withoutValuesLowerCased.indexOf(attribute);
        if (pos == -1 || pos == 0 || pos == withoutValuesLowerCased.length()-attribute.length()) return false; // attribute must be there AND within the tag so at least a < or > should be around
        final char characterBefore = withoutValuesLowerCased.charAt(pos-1);
        final char characterAfter = withoutValuesLowerCased.charAt(pos+attribute.length());
        final boolean beforeOK = characterBefore == '\"' || characterBefore == '\'' || characterBefore == '<' || characterBefore <= ' ';
        final boolean afterOK = characterAfter == '=' || characterAfter == '>' || characterAfter == '/' || characterAfter <= ' ';
        return beforeOK && afterOK;
    }
    
    
    
    // currently not used since readonly-field-protection is not activated in this version
    public static  String extractFormFieldValue(final String formFieldTag) {
        // TODO: implement here extraction of initial field value for selectbox [where selected], textarea [possible?!?], input type=text [value], type=radio|checkbox [where selected|checked], hiddex=nix, button=nix, submit=nix, reset=nix, etc.
        throw new UnsupportedOperationException("TODO: implement here extraction of initial field value");
    }
    
    
    public static String firstTenCharactersLower(final String value) {
        if (value.length() < 10) return value.toLowerCase();
        return value.substring(0,10).toLowerCase();
    }
    
    
    
    /* *
    // for local testing only: ==============
    public static final void main(String[] args) throws Exception {
        String tag;
        tag = "<input type='text' style=\"color:red;disabled:=true\" value=\"kj'kjk\" disabled>";
        System.out.println(tag);
        System.out.println(isFormFieldDisabled(tag)+"\t"+removeAttributeValues(tag));
        tag = "<input type='text' style=\"color:red;disabled:true\" value='kj\"kjk' disabled>";
        System.out.println(tag);
        System.out.println(isFormFieldDisabled(tag)+"\t"+removeAttributeValues(tag));
        tag = "<input type='text' style=\"color:red;disabled:true\" value=\"kjkjk\" disabled=>";
        System.out.println(tag);
        System.out.println(isFormFieldDisabled(tag)+"\t"+removeAttributeValues(tag));
        tag = "<input type='text' style=     \"color:red;disabled:true\" value=\"kjkjk\" disabled=>";
        System.out.println(tag);
        System.out.println(isFormFieldDisabled(tag)+"\t"+removeAttributeValues(tag));
        tag = "<input type='text' style=\"\" value=\"kjkjk\" disabled=>";
        System.out.println(tag);
        System.out.println(isFormFieldDisabled(tag)+"\t"+removeAttributeValues(tag));
        tag = "<input type='text' style= value=\"kjkjk\" disabled=>";
        System.out.println(tag);
        System.out.println(isFormFieldDisabled(tag)+"\t"+removeAttributeValues(tag));
        tag = "<input type='text' style= value=\"kjkjk\"disabled=>";
        System.out.println(tag);
        System.out.println(isFormFieldDisabled(tag)+"\t"+removeAttributeValues(tag));
        tag = "<disabled input type='text' style= value=\"kjkjk\">";
        System.out.println(tag);
        System.out.println(isFormFieldDisabled(tag)+"\t"+removeAttributeValues(tag));
        tag = "<input type='text' style= value=\"kj disabled kjk\">";
        System.out.println(tag);
        System.out.println(isFormFieldDisabled(tag)+"\t"+removeAttributeValues(tag));
    }/**/    
    
    
    

    /* *
    // for local testing only: ==============
    public static final void main(String[] args) throws Exception {
        final String url = "http://www.example.com/demo/test;jsession=uuuuuu?id=16&huhu=haha#anchor7";
        System.out.println(url);
        System.out.println(stripQueryString(url));
    }/**/    
    /* *
    // for local testing only: ==============
    public static final void main(String[] args) throws Exception {
        final String formTag = "<form id='some-self-submit-without-action-and-method-attribute-at-all-GET' action=''>";
        System.out.println(formTag);
        final String actionURL = extractActionUrlOfCurrentForm(formTag);
        System.out.println(actionURL);
        final String queryString = extractQueryStringOfActionUrl(actionURL);
        System.out.println(queryString);
        final String formTagWithQueryStringOfActionRemoved = removeQueryStringFromActionUrlOfCurrentForm(formTag);
        System.out.println(formTagWithQueryStringOfActionRemoved);
    }/**/    
        
    /* *
    // for local testing only: ==============
    public static final void main(String[] args) throws Exception {
        final String formTag = "<form id='test123'    aCtion =' http://www.example.com/demo/test;jsession=uuuuuu?id=16&huhu=haha#anchor7'    method=\"POST\">";
        System.out.println(formTag);
        final String actionURL = extractActionUrlOfCurrentForm(formTag);
        System.out.println(actionURL);
        final String queryString = extractQueryStringOfActionUrl(actionURL);
        System.out.println(queryString);
        final String formTagWithQueryStringOfActionRemoved = removeQueryStringFromActionUrlOfCurrentForm(formTag);
        System.out.println(formTagWithQueryStringOfActionRemoved);
        final String newActionURL = addQueryStringToActionUrl(actionURL, queryString);
        System.out.println(newActionURL);
    }/**/
        
    /* *
    // for local testing only: ==============
    public static final void main(String[] args) throws Exception {
        final SecretKey key = CryptoUtils.generateCryptoKey();
        final String encrypted = encryptQueryStringInURL("http://www.example.com/demo/hahah?1=2","http://www.example.com/demo/test;jsession=uuuuuu?id=16&huhu=haha#anchor7", "1234567890", key);
        System.out.println(encrypted);
        final String decrypted = RequestUtils.decryptQueryStringInServletPathWithQueryString(encrypted, "1234567890", key);
        System.out.println(decrypted);
    }/**/
        
}
