package org.webcastellum;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpSession;

public final class ServerUtils {

    private static final Logger LOGGER = Logger.getLogger(ServerUtils.class.getName());
    
    private static final boolean DEBUG_PRINT_TUNING_TIMINGS = false;

    private static final Character[] CACHE = new Character[127 + 1];
    
    private static final String AMP = "&amp;";            
            
    static {
        for(int i = 0; i < CACHE.length; i++) 
            CACHE[i] = (char)i;
    }
    
    private static Character makeCharacter(final char c) {
        if(c < CACHE.length) 
            return CACHE[(int)c];
        return c;
    }

    


    // TODO: diese regexps patterns hier durch einfachere zeichenwese operationen ersetzen bzw. aho corasick davor (prefilter) bzw. matcher reusen
    
    private static final Pattern PATTERN_LINK_AMPERSAND_MASKED = Pattern.compile(AMP);
    //OLD private static final Pattern PATTERN_LINK_AMPERSAND_UNMASKED = Pattern.compile("&");
    //OLD private static final String LINK_AMPERSAND_MASKED = "&amp;";
    private static final String LINK_AMPERSAND_UNMASKED = "&";

    
    
    private static final Pattern PATTERN_COMMENT = Pattern.compile("/\\*.*?\\*/");
    private static final String COMMENT_REPLACEMENT_EMPTY = ""; 
    private static final String COMMENT_REPLACEMENT_SPACE = " "; 
    // TODO: Auch bei NullByte Removeal lieber ein Space statt eines leeren Strings ?

    private static final Pattern PATTERN_SAME_PATH_REFERENCE = Pattern.compile("\\./");
    private static final String SAME_PATH_REFERENCE_REPLACEMENT = "";

    private static final Pattern PATTERN_MULTI_PATH_SLASH = Pattern.compile("//+");
    private static final String MULTI_PATH_SLASH_REPLACEMENT = "/";

    private static final Pattern PATTERN_XML_CDATA_OPENING = Pattern.compile("<!\\[CDATA\\[");
    private static final String XML_CDATA_OPENING_REPLACEMENT = "";

    private static final Pattern PATTERN_XML_CDATA_CLOSING = Pattern.compile("\\]\\]>");
    private static final String XML_CDATA_CLOSING_REPLACEMENT = "";
    
    private static final Pattern PATTERN_SPECIAL_CHAR = Pattern.compile("&[a-zA-Z0-9]{2,8};|\\\\[tnrbf\\\\'\"]");
    private static final Pattern PATTERN_SPECIAL_CHAR_HTML_ONLY = Pattern.compile("&[a-zA-Z0-9]{2,8};");
    private static final Map<String,String> SPECIAL_CHAR_MAPPINGS = new HashMap<>();
    private static final Map<String,String> SPECIAL_CHAR_HTML_ONLY_MAPPINGS = new HashMap<>();
    private static final Map<Character,String> HTML_ENCODING_MAPPING = new HashMap<>();
    static {
        // See also:  http://de.wikipedia.org/wiki/Hilfe:Sonderzeichenreferenz
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&quot;",""+(char)34);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&amp;",""+(char)38);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&lt;",""+(char)60);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&gt;",""+(char)62);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&lsqb;",""+(char)91);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&rsqb;",""+(char)93);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&lcub;",""+(char)123);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&rcub;",""+(char)125);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&nbsp;",""+(char)160);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&iexcl;",""+(char)161); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&cent;",""+(char)162); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&pound;",""+(char)163); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&curren;",""+(char)164); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&yen;",""+(char)165); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&brvbar;",""+(char)166);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&sect;",""+(char)167);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&uml;",""+(char)168);         
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&copy;",""+(char)169); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ordf;",""+(char)170); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&laquo;",""+(char)171); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&not;",""+(char)172);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&shy;",""+(char)173);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&reg;",""+(char)174); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&macr;",""+(char)175); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&deg;",""+(char)176); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&plusmn;",""+(char)177); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&sup2);",""+(char)178); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&sup3);",""+(char)179); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&acute;",""+(char)180); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&micro;",""+(char)181); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&para;",""+(char)182);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&middot;",""+(char)183); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&cedil;",""+(char)184); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&sup1);",""+(char)185); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ordm;",""+(char)186); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&raquo;",""+(char)187); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&frac14);",""+(char)188);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&frac12);",""+(char)189);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&frac34);",""+(char)190);
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&iquest;",""+(char)191); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Agrave;",""+(char)192); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Aacute;",""+(char)193); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Acirc;",""+(char)194); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Atilde;",""+(char)195); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Auml;",""+(char)196); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Aring;",""+(char)197); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&AElig;",""+(char)198); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Ccedil;",""+(char)199); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Egrave;",""+(char)200); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Eacute;",""+(char)201); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Ecirc;",""+(char)202); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Euml;",""+(char)203); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Igrave;",""+(char)204); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Iacute;",""+(char)205); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Icirc;",""+(char)206); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Iuml;",""+(char)207); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ETH;",""+(char)208); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Ntilde;",""+(char)209); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Ograve;",""+(char)210); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Oacute;",""+(char)211); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Ocirc;",""+(char)212); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Otilde;",""+(char)213); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Ouml;",""+(char)214); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&times;",""+(char)215); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Oslash;",""+(char)216); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Ugrave;",""+(char)217); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Uacute;",""+(char)218); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Ucirc;",""+(char)219); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Uuml;",""+(char)220); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Yacute;",""+(char)221); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&THORN;",""+(char)222); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&szlig;",""+(char)223); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&szlig;",""+(char)223); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&aacute;",""+(char)225); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&acirc;",""+(char)226); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&atilde;",""+(char)227); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&auml;",""+(char)228); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&aring;",""+(char)229); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&aelig;",""+(char)230); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ccedil;",""+(char)231); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&egrave;",""+(char)232); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&eacute;",""+(char)233); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ecirc;",""+(char)234); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&euml;",""+(char)235); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&igrave;",""+(char)236); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&iacute;",""+(char)237); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&icirc;",""+(char)238); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&iuml;",""+(char)239); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&eth;",""+(char)240); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ntilde;",""+(char)241); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ograve;",""+(char)242); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&oacute;",""+(char)243); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ocirc;",""+(char)244); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&otilde;",""+(char)245); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ouml;",""+(char)246); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&divide;",""+(char)247); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&oslash;",""+(char)248); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ugrave;",""+(char)249); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&uacute;",""+(char)250); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ucirc;",""+(char)251); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&uuml;",""+(char)252); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&yacute;",""+(char)253); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&thorn;",""+(char)254); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&yuml;",""+(char)255); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Alpha;",""+(char)913); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Beta;",""+(char)914); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Gamma;",""+(char)915); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Delta;",""+(char)916); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Epsilon;",""+(char)917); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Zeta;",""+(char)918); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Eta;",""+(char)919); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Theta;",""+(char)920); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Iota;",""+(char)921); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Kappa;",""+(char)922); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Lambda;",""+(char)923); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Mu;",""+(char)924); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Nu;",""+(char)925); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Xi;",""+(char)926); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Omicron;",""+(char)927); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Pi;",""+(char)928); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Rho;",""+(char)929); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Rho;",""+(char)929); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Tau;",""+(char)932); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Upsilon;",""+(char)933); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Phi;",""+(char)934); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Chi;",""+(char)935); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Psi;",""+(char)936); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Omega;",""+(char)937); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&alpha;",""+(char)945); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&beta;",""+(char)946); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&gamma;",""+(char)947); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&delta;",""+(char)948); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&epsilon;",""+(char)949); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&zeta;",""+(char)950); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&eta;",""+(char)951); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&theta;",""+(char)952); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&iota;",""+(char)953); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&kappa;",""+(char)954); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&lambda;",""+(char)955); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&mu;",""+(char)956); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&nu;",""+(char)957); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&xi;",""+(char)958); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&omicron;",""+(char)959); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&pi;",""+(char)960); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&rho;",""+(char)961); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&sigmaf;",""+(char)962); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&sigma;",""+(char)963); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&tau;",""+(char)964); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&upsilon;",""+(char)965); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&phi;",""+(char)966); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&chi;",""+(char)967); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&psi;",""+(char)968); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&omega;",""+(char)969); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&thetasym;",""+(char)977); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&upsih;",""+(char)978); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&forall;",""+(char)8704); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&part;",""+(char)8706); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&exist;",""+(char)8707); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&empty;",""+(char)8709); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&nabla;",""+(char)8711); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&isin;",""+(char)8712); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&notin;",""+(char)8713); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ni;",""+(char)8715); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&prod;",""+(char)8719); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&sum;",""+(char)8721); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&minus;",""+(char)8722); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&lowast;",""+(char)8727); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&radic;",""+(char)8730); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&prop;",""+(char)8733); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&infin;",""+(char)8734); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ang;",""+(char)8736); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&and;",""+(char)8743); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&or;",""+(char)8744); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&cap;",""+(char)8745); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&cup;",""+(char)8746); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&int;",""+(char)8747); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&there4);",""+(char)8756); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&sim;",""+(char)8764); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&cong;",""+(char)8773); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&asymp;",""+(char)8776); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ne;",""+(char)8800); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&equiv;",""+(char)8801); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&le;",""+(char)8804); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ge;",""+(char)8805); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&sub;",""+(char)8834); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&sup;",""+(char)8835); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&nsub;",""+(char)8836); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&sube;",""+(char)8838); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&supe;",""+(char)8839); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&oplus;",""+(char)8853); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&oplus;",""+(char)8853); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&perp;",""+(char)8869); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&sdot;",""+(char)8901); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&loz;",""+(char)9674); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&lceil;",""+(char)8968); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&rceil;",""+(char)8969); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&lfloor;",""+(char)8970); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&rfloor;",""+(char)8971); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&lang;",""+(char)9001); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&rang;",""+(char)9002); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&larr;",""+(char)8592); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&uarr;",""+(char)8593); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&rarr;",""+(char)8594); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&darr;",""+(char)8595); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&harr;",""+(char)8596); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&crarr;",""+(char)8629); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&lArr;",""+(char)8656); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&uArr;",""+(char)8657); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&rArr;",""+(char)8658); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&dArr;",""+(char)8659); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&hArr;",""+(char)8660); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ensp;",""+(char)8194); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&emsp;",""+(char)8195); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&thinsp;",""+(char)8201); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&zwnj;",""+(char)8204); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&zwj;",""+(char)8205); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&lrm;",""+(char)8206); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&rlm;",""+(char)8207); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ndash;",""+(char)8211); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&mdash;",""+(char)8212); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&lsquo;",""+(char)8216); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&rsquo;",""+(char)8217); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&sbquo;",""+(char)8218); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&ldquo;",""+(char)8220); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&rdquo;",""+(char)8221); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&bdquo;",""+(char)8222); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&dagger;",""+(char)8224); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Dagger;",""+(char)8225); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&permil;",""+(char)8240); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&lsaquo;",""+(char)8249); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&rsaquo;",""+(char)8250); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&bull;",""+(char)8226); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&hellip;",""+(char)8230); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&prime;",""+(char)8242); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&Prime;",""+(char)8243); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&oline;",""+(char)8254); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&frasl;",""+(char)8260); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&frasl;",""+(char)8260); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&image;",""+(char)8465); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&real;",""+(char)8476); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&trade;",""+(char)8482); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&euro;",""+(char)8364); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&alefsym;",""+(char)8501); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&spades;",""+(char)9824); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&clubs;",""+(char)9827); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&hearts;",""+(char)9829); 
        SPECIAL_CHAR_HTML_ONLY_MAPPINGS.put("&diams;",""+(char)9830); 
        SPECIAL_CHAR_MAPPINGS.putAll(SPECIAL_CHAR_HTML_ONLY_MAPPINGS);
        SPECIAL_CHAR_MAPPINGS.put("\\t","\t");
        SPECIAL_CHAR_MAPPINGS.put("\\n","\n");
        SPECIAL_CHAR_MAPPINGS.put("\\r","\r");
        SPECIAL_CHAR_MAPPINGS.put("\\b","\b");
        SPECIAL_CHAR_MAPPINGS.put("\\f","\f");
        SPECIAL_CHAR_MAPPINGS.put("\\\\","\\");
        SPECIAL_CHAR_MAPPINGS.put("\\'","\'");
        SPECIAL_CHAR_MAPPINGS.put("\\\"","\"");
        // now the reverse-mapping (but only of HTML entity encoding relevant chars)
        for (final Iterator entries = SPECIAL_CHAR_MAPPINGS.entrySet().iterator(); entries.hasNext();) {
            final Map.Entry entry = (Map.Entry) entries.next();
            final String key = (String) entry.getKey();
            if (key.charAt(0) == '&' && !AMP.equals(key)) { // = it is an HTML entity mapping, so use it in the reverse mapping... don't encode the ampersand (poor man's workaround for avoiding to encode even the URL param delimiter & signs into &amp; literals)
                final String value = (String) entry.getValue();
                final Character character = makeCharacter( value.charAt(0) ); //1.5 better use auto-boxing
                HTML_ENCODING_MAPPING.put(character, key);
            }
        }
    };
    
    
    private static final Pattern PATTERN_ENCODED_CHAR_EXCEPT_URL_ENCODING = Pattern.compile("(?:\\\\[xX]|\\\\[uU]00)[a-fA-F0-9]{2}|&#[0-9]{0,5}[0-9]{2};?|&#[xX][0-9]{0,5}[a-fA-F0-9]{2};?");
    private static final Pattern PATTERN_ENCODED_CHAR = Pattern.compile("\\+|(?:%|\\\\[xX]|\\\\[uU]00)[a-fA-F0-9]{2}|&#[0-9]{0,5}[0-9]{2};?|&#[xX][0-9]{0,5}[a-fA-F0-9]{2};?");
    private static final Pattern PATTERN_ENCODED_CHAR_URL_ENCODING_ONLY = Pattern.compile("\\+|%[a-fA-F0-9]{2}");

    
    private ServerUtils() {}
    
    public static String unmaskAmpersandsInLink(final String link) {
        if (link == null) {
            return null;
        }
        return PATTERN_LINK_AMPERSAND_MASKED.matcher(link).replaceAll(LINK_AMPERSAND_UNMASKED);
    }
    
    

    
    
    // TODO: noch weiter und tiefer checken ob diese Parsing-Routine angreifbar ist z.B. auf endlos loops oder aehnliches 
    public static Map parseContentDisposition(String disposition) {
        Map m = new HashMap();
        StringTokenizer outer = null;
        StringTokenizer inner = null;
        String element = null;
        String key = null;
        String val = null;
        int startIndex = 0;
        int stopIndex = 0;
        
        if (disposition == null || disposition.trim().length() == 0)  return m;
        
        outer = new StringTokenizer(disposition, "; ", false);
        while (outer.hasMoreTokens()) {
            element = outer.nextToken();
            inner = new StringTokenizer(element, "=", false);
            if (inner.countTokens() == 2) {
                key = inner.nextToken();
                val = inner.nextToken();
                if (val.startsWith("\"")) {
                    startIndex = 1;
                    stopIndex  = (val.length() > 0) ? val.length() - 1 : 0;
                } else {
                    startIndex = 0;
                    stopIndex  = val.length();
                }
                
                if (startIndex != 0 || stopIndex != val.length()) {
                    val = val.substring(startIndex, stopIndex);
                }
                
                //m.put(key, val);
                m.put(key.toLowerCase(), val);
            }
        }
        
        return m;
    }
    
    
    

    
    
    public static final boolean startsWithJavaScriptOrMailto(final String url) {
        if (url == null) return false;
        if (RelaxingHtmlParser.USE_DIRECT_ARRAY_LOOKUPS_INSTEAD_OF_STARTS_WITH) { // is performing better
            // ignore leading whitespace
            int i=0;
            for (i=0; i<url.length(); i++) if (url.charAt(i) > ' ') break;
            // start at offset where first non-whitespace char sits
            if (url.length() >= i+11 
                    && (   (url.charAt(i+0)=='j'||url.charAt(i+0)=='J') 
                        && (url.charAt(i+1)=='a'||url.charAt(i+1)=='A') 
                        && (url.charAt(i+2)=='v'||url.charAt(i+2)=='V') 
                        && (url.charAt(i+3)=='a'||url.charAt(i+3)=='A') 
                        && (url.charAt(i+4)=='s'||url.charAt(i+4)=='S') 
                        && (url.charAt(i+5)=='c'||url.charAt(i+5)=='C') 
                        && (url.charAt(i+6)=='r'||url.charAt(i+6)=='R') 
                        && (url.charAt(i+7)=='i'||url.charAt(i+7)=='I') 
                        && (url.charAt(i+8)=='p'||url.charAt(i+8)=='P') 
                        && (url.charAt(i+9)=='t'||url.charAt(i+9)=='T') 
                        && (url.charAt(i+10)==':')
                    )) return true;
            if (url.length() >= i+7 
                    && (   (url.charAt(i+0)=='m'||url.charAt(i+0)=='M') 
                        && (url.charAt(i+1)=='a'||url.charAt(i+1)=='A') 
                        && (url.charAt(i+2)=='i'||url.charAt(i+2)=='I') 
                        && (url.charAt(i+3)=='l'||url.charAt(i+3)=='L') 
                        && (url.charAt(i+4)=='t'||url.charAt(i+4)=='T') 
                        && (url.charAt(i+5)=='o'||url.charAt(i+5)=='O') 
                        && (url.charAt(i+6)==':')
                    )) return true;
            return false;
        } else {
            final String urlLowerCase = url.toLowerCase().trim();
            return urlLowerCase.startsWith("mailto:") || urlLowerCase.startsWith("javascript:");
        }
    }
    
    

    public static Integer[] convertSimpleToObjectArray(final int[] values) {
        if (values == null) return null;
        final Integer[] result = new Integer[values.length];
        for (int i=0; i<values.length; i++) {
            result[i] = new Integer(values[i]);
        }
        return result;
    }
    
    public static int[] convertObjectToSimpleArray(final Integer[] values) {
        if (values == null) return null;
        final int[] result = new int[values.length];
        for (int i=0; i<values.length; i++) {
            result[i] = values[i].intValue();
        }
        return result;
    }
    
    public static int[][] convertArrayIntegerListTo2DimIntArray(final List/*<Integer[]>*/ listOfValues) {
        if (listOfValues == null) return null;
        final int[][] result = new int[listOfValues.size()][];
        int i=0;
        for (final Iterator iter = listOfValues.iterator(); iter.hasNext();) {
            result[i++] = convertObjectToSimpleArray((Integer[])iter.next());
        }
        return result;
    }
    
    
    
    public static boolean isSameServer(final String referrer, final String url) {
        if (referrer == null) throw new NullPointerException("referrer must not be null");
        if (url == null) throw new NullPointerException("url must not be null");
        try {
            final URL referrerURL = new URL(referrer);
            final URL urlURL = new URL(url);
            final String referrerHost = referrerURL.getHost();
            final String urlHost = urlURL.getHost();
            return referrerHost == null ? urlHost == null : referrerHost.equalsIgnoreCase(urlHost);
        } catch (MalformedURLException e) {
            return false; // = treated as external server here
        }
    }


    public static boolean containsColonBeforeFirstSlashOrQuestionmark(final String value) {
        if (value == null) return false;
        final int colon = value.indexOf(':');
        if (colon == -1) return false;
        final int slash = value.indexOf('/');
        final int qm = value.indexOf('?');
        final int end;
        if (slash == -1) {
            end = qm;
        } else {
            if (qm == -1) {
                end = slash;
            } else {
                end = Math.min(slash, qm);
            }
        }
        if (end == -1) return true;
        return colon < end;
    }

    // TODO: leider koennen wir hier nicht auf den Context-Root der App pruefen, da ja leider die App ggf. hinter nem Apache Reverse-Proxy in einem anderen Web-Root-Context lauft...
    // ... insofern werden hier leider alle URLs angepackt, welche auf dem gleichen Server liegen... damit auch URLs welche zu anderen Apps auf dem gleichen Server zeigen...
    public static boolean isInternalHostURL(final String currentRequestUrlToCompareWith, final String linkedUrl) { // TODO: Diese Methode hier ueber ein Interface mit einer Default-Implementation durch den Anwender bestimmen lassem
        if (currentRequestUrlToCompareWith == null) throw new NullPointerException("currentRequestUrlToCompareWith must not be null");
        if (linkedUrl == null) throw new NullPointerException("linkedUrl must not be null");
        final String decoded = simpleDecodeURL(linkedUrl);
        // TODO: better reuse containsColonBeforeFirstSlashOrQuestionmark()
        int limit = linkedUrl.indexOf('?');
        if (limit == -1) limit = Integer.MAX_VALUE;
        limit = Math.min(limit, decoded.indexOf('?')); // since only text left from a potential ? should be inspected
        if (limit == -1) limit = 8;
        else limit = Math.min(8, limit); // since only the first eight letters are relevant for the relevant web protocol names
        final int pos = linkedUrl.indexOf(':');
        final int posDecoded = decoded.indexOf(':');
        // check if linkedUrl is only a relative URL
        if ((pos > -1 && pos <= limit) || (posDecoded > -1 && posDecoded <= limit)) {
            // check if matching host
            try {
                final URL requestedURL = new URL(currentRequestUrlToCompareWith);
                final URL checkURL = new URL(linkedUrl);
                final String requestedHost = requestedURL.getHost();
                final String checkHost = checkURL.getHost();
                final String checkProtocol = checkURL.getProtocol();
                return ("http".equals(checkProtocol) || "https".equals(checkProtocol)) && checkHost.equals(requestedHost); // = is same (internal) Host
            } catch (MalformedURLException e) {
                return false; // = treated as non-internal host here
            } catch (Exception e) {
                return false; // = treated as non-internal host here
            }
        }
        return true; // = is relative URL
    }
    
    
    private static String simpleDecodeURL(final String url) {
        if (url == null) return url;
        // Further details on URI encoding stuff: http://en.wikipedia.org/wiki/Percent-encoding
        try {
            return URLDecoder.decode(url, WebCastellumFilter.DEFAULT_CHARACTER_ENCODING);
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "Unsupported character encoding configured for WebCastellum: {0}", WebCastellumFilter.DEFAULT_CHARACTER_ENCODING);
            return url;
        } catch (IllegalArgumentException e) {
            return url;
        } 
    }
    
    
    
    public static String encodeHtmlSafe(final String value) {
        if (value == null) return null;
        final StringBuilder result = new StringBuilder(value.length());
        Character character;
        String mapping;
        for (int i=0; i<value.length(); i++) {
            character = makeCharacter(value.charAt(i));
            mapping = (String) HTML_ENCODING_MAPPING.get(character);
            if (mapping != null) {
                result.append(mapping);
                continue;
            }
            result.append(character);
        }
        return result.toString();
    }

    
    
    
    
    
    
    public static String decodeBrokenValueUrlEncodingOnly(final String value) {
        if (value == null) return null;
        // Charset encodings - only URL encoding
        final Matcher matcher = PATTERN_ENCODED_CHAR_URL_ENCODING_ONLY.matcher(value);// TODO: reuse matcher per request
        final StringBuilder result = new StringBuilder( value.length() );
        int pos = 0;
        String encoded;
        while (matcher.find()) {
            result.append( value.substring(pos,matcher.start()) );
            pos = matcher.end();
            encoded = matcher.group();
            // special URL encoded variant with a "+" which stands for a space " "
            if (encoded.charAt(0) == '+') {
                result.append(' ');
                continue;
            }
            // take last two chars as hex
            result.append( (char)CryptoUtils.toByteValue(encoded.substring(encoded.length()-2)) );
        }
        if (pos < value.length()) result.append( value.substring(pos) );
        return result.toString();
    }
    public static String decodeBrokenValueHtmlOnly(final String value, final boolean alsoDecodeAmpersand) { // alsoDecodeAmpersand since &amp; is special in links
        if (value == null) return null;
        final Matcher matcher = PATTERN_SPECIAL_CHAR_HTML_ONLY.matcher(value);// TODO: reuse matcher per request
        final StringBuilder result = new StringBuilder( value.length() );
        int pos = 0;
        String mapping, specialCharacter;
        while (matcher.find()) {
            result.append( value.substring(pos,matcher.start()) );
            pos = matcher.end();
            mapping = matcher.group();
            if (!alsoDecodeAmpersand && AMP.equals(mapping)) specialCharacter = AMP; // = decode &amp; simply to &amp; (= effectively ignoring it) when alsoDecodeAmpersand is false
            else specialCharacter = (String) SPECIAL_CHAR_HTML_ONLY_MAPPINGS.get(mapping);
            if (specialCharacter != null) result.append(specialCharacter);
            else result.append(mapping);
        }
        if (pos < value.length()) result.append( value.substring(pos) );
        return result.toString();
    }
    public static String decodeBrokenValueExceptUrlEncoding(String value) {
        if (value == null) return null;
        { // Special character mappings
            final Matcher matcher = PATTERN_SPECIAL_CHAR.matcher(value); // TODO: reuse matcher per request
            final StringBuilder result = new StringBuilder( value.length() );
            int pos = 0;
            String mapping, specialCharacter;
            while (matcher.find()) {
                result.append( value.substring(pos,matcher.start()) );
                pos = matcher.end();
                mapping = matcher.group();
                specialCharacter = (String) SPECIAL_CHAR_MAPPINGS.get(mapping);
                if (specialCharacter != null) result.append(specialCharacter);
                else result.append(mapping);
            }
            if (pos < value.length()) result.append( value.substring(pos) );
            value = result.toString();
        }
        { // Charset encodings - except URL encodings
            final Matcher matcher = PATTERN_ENCODED_CHAR_EXCEPT_URL_ENCODING.matcher(value); // TODO: reuse matcher per request
            final StringBuilder result = new StringBuilder( value.length() );
            int pos = 0, length;
            String encoded, decimal;
            char zero, two;
            while (matcher.find()) {
                result.append( value.substring(pos,matcher.start()) );
                pos = matcher.end();
                encoded = matcher.group();
                length = encoded.length();
                if (encoded.charAt(length-1) == ';') {
                    encoded = encoded.substring(0,--length);
                }
                zero = encoded.charAt(0);
                two = encoded.charAt(2);
                if (zero == '\\' || two == 'x' || two == 'X') {
                    // take last two chars as hex
                    result.append( (char)CryptoUtils.toByteValue(encoded.substring(length-2)) );
                } else {
                    // take last three (or two when less) chars as decimal
                    decimal = encoded.substring(length-(length>4?3:2));
                    result.append( (char)Integer.parseInt(decimal) );
                }
            }
            if (pos < value.length()) result.append( value.substring(pos) );
            value = result.toString();
        }
        return value;
    }
    public static String decodeBrokenValue(String value) {
        if (value == null) return null;
        { // Special character mappings
            final Matcher matcher = PATTERN_SPECIAL_CHAR.matcher(value);// TODO: reuse matcher per request
            final StringBuilder result = new StringBuilder( value.length() );
            int pos = 0;
            String mapping, specialCharacter;
            while (matcher.find()) {
                result.append( value.substring(pos,matcher.start()) );
                pos = matcher.end();
                mapping = matcher.group();
                specialCharacter = (String) SPECIAL_CHAR_MAPPINGS.get(mapping);
                if (specialCharacter != null) result.append(specialCharacter);
                else result.append(mapping);
            }
            if (pos < value.length()) result.append( value.substring(pos) );
            value = result.toString();
        }
        { // Charset encodings
            final Matcher matcher = PATTERN_ENCODED_CHAR.matcher(value);
            final StringBuilder result = new StringBuilder( value.length() ); // TODO: Java5 use StringBuilder
            int pos = 0, length;
            String encoded, decimal;
            while (matcher.find()) {
                result.append( value.substring(pos,matcher.start()) );
                pos = matcher.end();
                encoded = matcher.group();
                // special URL encoded variant with a "+" which stands for a space " "
                if (encoded.charAt(0) == '+') {
                    result.append(' ');
                    continue;
                }
                // crop potential semi-colon at the end
                length = encoded.length();
                if (encoded.charAt(length-1) == ';') {
                    encoded = encoded.substring(0,--length);
                }
                if (encoded.charAt(0) == '%' || encoded.charAt(0) == '\\' || encoded.charAt(2) == 'x' || encoded.charAt(2) == 'X') {  // yes, use 0 and 2 here as index not 0 and 1 !!   (&#x76)
                    // take last two chars as hex
                    result.append( (char)CryptoUtils.toIntValue(encoded.substring(length-2)) );
                } else {
                    // take last three (or two when less) chars as decimal
                    decimal = encoded.substring(length-(length>4?3:2));
                    result.append( (char)Integer.parseInt(decimal) );
                }
            }
            if (pos < value.length()) result.append( value.substring(pos) );
            value = result.toString();
        }
        return value;
    }
    
        
    
    

    // useful, since UTF-8 is also a variable-length encoding scheme
    public static final String decodeBrokenUTF8(String input) {
        try {
            // at first find all invalid UTF-8 encodings (i.e. replace %K7 or %7K or %KK with %25 since K is not a valid hex digit
            final int length = input.length();
            final StringBuilder tmp = new StringBuilder(length);
            for (int i=0; i<length; i++) {
                final char c = input.charAt(i);
                if ('%' == c &&
                        ( i+2 >= length 
                            || (!isHexDigit(input.charAt(i+1)) || !isHexDigit(input.charAt(i+2))) )
                          ) tmp.append("%25"); else tmp.append(c);
            }
            input = tmp.toString();
            // now try to decode the URL-encoded string using UTF-8 (see http://en.wikipedia.org/wiki/Percent-encoding )
            input = URLDecoder.decode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("UnsupportedEncodingException: "+e);
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException: "+e);
        } catch (RuntimeException e) {
            System.err.println("RuntimeException: "+e);
        }
        return input;
    }
    
    private static final boolean isHexDigit(final char c) {
        return c=='0' || c=='1' || c=='2' || c=='3' || c=='4' || c=='5' || c=='6' || c=='7' || c=='8' || c=='9' || c=='A' || c=='B' || c=='C' || c=='D' || c=='E' || c=='F' || c=='a' || c=='b' || c=='c' || c=='d' || c=='e' || c=='f';
    }

    
    
    
    public static String removeNullBytes(final String value) {
        if (value == null) return value;
        final StringBuilder result = new StringBuilder(value.length());
        char c;
        for (int i=0; i<value.length(); i++) {
            c = value.charAt(i);
            if ((int)c != 0) result.append(c);
        }
        return result.toString();
    }
    public static String removeWhitespaces(final String value, final byte decodingPermutationLevel) {
        if (value == null) return value;
        final StringBuilder result = new StringBuilder(value.length());
        char c;
        for (int i=0; i<value.length(); i++) {
            c = value.charAt(i);
            if ( c > ' ' ) result.append(c);
            /* OLD
            if (decodingPermutationLevel >= 3) {
                if ( !Character.isWhitespace(c) ) result.append(c);
            } else {
                if (c != ' ' && c != '\r' && c != '\t' && c != '\f' && c != '\n' && c != '\b') result.append(c);
            }
            */
        }
        return result.toString();
    }
    public static String compressWhitespaces(String value, final byte decodingPermutationLevel) {
        if (value == null) return value;
        value = value.trim();
        final StringBuilder result = new StringBuilder(value.length());
        char c;
        boolean isPreviousCharacterWhitespace = false;
        for (int i=0; i<value.length(); i++) {
            c = value.charAt(i);
            if ( c <= ' ' ) {
                if (!isPreviousCharacterWhitespace) result.append(' ');
                isPreviousCharacterWhitespace = true;
            } else {
                result.append(c);
                isPreviousCharacterWhitespace = false;
            }
        }
        return result.toString();
    }
    public static String removeComments(final String value) {
        if (value == null) return value;
        return PATTERN_COMMENT.matcher(value).replaceAll(COMMENT_REPLACEMENT_EMPTY);
    }
    public static String replaceCommentsWithSpace(final String value) {
        if (value == null) return value;
        return PATTERN_COMMENT.matcher(value).replaceAll(COMMENT_REPLACEMENT_SPACE);
    }
    public static String removeSamePathReferences(final String value) {
        if (value == null) return value;
        return PATTERN_SAME_PATH_REFERENCE.matcher(value).replaceAll(SAME_PATH_REFERENCE_REPLACEMENT);
    }
    public static String removeMultiPathSlashes(final String value) {
        if (value == null) return value;
        return PATTERN_MULTI_PATH_SLASH.matcher(value).replaceAll(MULTI_PATH_SLASH_REPLACEMENT);
    }
    public static String removeXmlCdataTags(String value) {
        if (value == null) return value;
        value = PATTERN_XML_CDATA_OPENING.matcher(value).replaceAll(XML_CDATA_OPENING_REPLACEMENT);
        value = PATTERN_XML_CDATA_CLOSING.matcher(value).replaceAll(XML_CDATA_CLOSING_REPLACEMENT);
        return value;
    }
    public static String removeBackslashes(final String value) {
        // simply remove backslashes the same way as it is done in JavaScript (see JavaScript alert("aaa\aaa\\aaa\\\aaa\\\\aaa\\\\\aaa\\\\\\aaa") as an example)
        if (value == null) return value;
        final StringBuilder result = new StringBuilder(value.length()); // TODO: Java5 use StringBuilder
        final int length = value.length();
        char c;
        int next, nextNext;
        for (int i=0; i<length; i++) {
            c = value.charAt(i);
            if (c == '\\') {
                // check if next (and next-next) character is also a backslash, which means that \\ will become \
                next = i+1;
                if (next<length && value.charAt(next) == '\\') {
                    nextNext = i+2;
                    if (nextNext<length && value.charAt(next) == '\\') i++;
                    result.append(c);
                }
            } else result.append(c);
        }
        return result.toString();
    }
    
    
    
    // for maps like request-parameter map, or header map, or cookie map
    public static Map/*<String,Permutation[]>*/ permutateVariants(final Map/*<String,String[]>*/ map, final boolean nonStandardPermutationsAllowed, final byte decodingPermutationLevel) {
        final Map/*<String,Permutation[]>*/ permutatedResultMap = new HashMap();
        for (final Iterator entries = map.entrySet().iterator(); entries.hasNext();) {
            final Map.Entry/*<String,String[]>*/ entry = (Map.Entry) entries.next();
            final String[] originalValues = (String[]) entry.getValue();
            final Permutation[] permutatedValues = new Permutation[originalValues.length]; // yes, array of Permutation objects
            for (int i=0; i<originalValues.length; i++) {
                permutatedValues[i] = permutateVariants(originalValues[i], nonStandardPermutationsAllowed, decodingPermutationLevel);
            }
            permutatedResultMap.put( entry.getKey(), permutatedValues );
        }
        return permutatedResultMap;
    }
    /**
     * For single strings
     * @param nonStandardPermutationsAllowed true when enhanced permutations are allowed (this is not always the case)
     * @param decodingPermutationLevel level of decoding: when is is configured high, it uses INSANE decodings (only relevant when nonStandardPermutationsAllowed is true)
     */
    public static Permutation permutateVariants(final String value, final boolean nonStandardPermutationsAllowed, final byte decodingPermutationLevel) { 
        if (decodingPermutationLevel < 0) throw new IllegalArgumentException("decodingPermutationLevel must not be negative");
        final Permutation permutation = new Permutation();
        if (value != null) {
            permutation.addStandardPermutation(value);
            if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println("START with length "+value.length()+"\n"+System.currentTimeMillis());

            if (decodingPermutationLevel >= 1) {
                permutation.addStandardPermutation(ServerUtils.decodeBrokenValue(value)); // this is a standard permutation
                if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                final String value_utf8Decoded = ServerUtils.decodeBrokenUTF8(value);
                permutation.addStandardPermutation(value_utf8Decoded); // this is a standard permutation
                if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                final String value_nullBytesRemoved = ServerUtils.removeNullBytes(value);
                final boolean nullBytesRemovalCreatedNewValue;
                if (!value_nullBytesRemoved.equals(value)) {
                    permutation.addNonStandardPermutation(value_nullBytesRemoved);
                    nullBytesRemovalCreatedNewValue = true;
                } else nullBytesRemovalCreatedNewValue = false;
                if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                permutation.addNonStandardPermutation(ServerUtils.compressWhitespaces(value,decodingPermutationLevel));
                if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                permutation.addNonStandardPermutation(ServerUtils.removeWhitespaces(value,decodingPermutationLevel));
                if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                permutation.addNonStandardPermutation(ServerUtils.replaceCommentsWithSpace(value));
                if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                permutation.addNonStandardPermutation(ServerUtils.removeComments(value));
                if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                permutation.addNonStandardPermutation(ServerUtils.removeXmlCdataTags(value));
                if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                permutation.addNonStandardPermutation(ServerUtils.removeSamePathReferences(value));
                if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                permutation.addNonStandardPermutation(ServerUtils.removeMultiPathSlashes(value));
                if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());

                if (nonStandardPermutationsAllowed && decodingPermutationLevel >= 2) {
                    if (nullBytesRemovalCreatedNewValue) {
                        permutation.addNonStandardPermutation(ServerUtils.decodeBrokenValue(value_nullBytesRemoved));
                    }
                    if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                    permutation.addNonStandardPermutation(ServerUtils.compressWhitespaces(value_utf8Decoded,decodingPermutationLevel));
                    if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                    permutation.addNonStandardPermutation(ServerUtils.removeWhitespaces(value_utf8Decoded,decodingPermutationLevel));
                    if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                    final String value_decodedNullBytesRemoved = ServerUtils.removeNullBytes(value_utf8Decoded);
                    permutation.addNonStandardPermutation(value_decodedNullBytesRemoved);
                    if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                    permutation.addNonStandardPermutation(ServerUtils.replaceCommentsWithSpace(value_utf8Decoded));
                    if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                    permutation.addNonStandardPermutation(ServerUtils.removeComments(value_utf8Decoded));
                    if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                    permutation.addNonStandardPermutation(ServerUtils.removeSamePathReferences(value_utf8Decoded));
                    if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                    permutation.addNonStandardPermutation(ServerUtils.removeXmlCdataTags(value_utf8Decoded));
                    if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                    permutation.addNonStandardPermutation(ServerUtils.removeMultiPathSlashes(value_utf8Decoded));
                    if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                    permutation.addNonStandardPermutation(ServerUtils.removeBackslashes(value));
                    if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                    if (nullBytesRemovalCreatedNewValue) {
                        permutation.addNonStandardPermutation(ServerUtils.removeBackslashes(value_nullBytesRemoved));
                    }
                    if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                    permutation.addNonStandardPermutation(ServerUtils.removeBackslashes(value_utf8Decoded));
                    if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                    permutation.addNonStandardPermutation(ServerUtils.removeBackslashes(value_decodedNullBytesRemoved));
                    if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());

                    if (decodingPermutationLevel >= 3) {
                        //final String value_decodedNullBytesRemovedDecoded = ServerUtils.decodeBrokenValue(value_decodedNullBytesRemoved);
                        //permutation.addNonStandardPermutation(value_decodedNullBytesRemovedDecoded);
                        //if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                        permutation.addNonStandardPermutation(ServerUtils.removeWhitespaces(value_decodedNullBytesRemoved,decodingPermutationLevel));
                        if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                        final String value_utf8DecodedTwice = ServerUtils.decodeBrokenUTF8(value_utf8Decoded);
                        permutation.addNonStandardPermutation(value_utf8DecodedTwice);
                        if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                        permutation.addNonStandardPermutation(ServerUtils.removeWhitespaces(value_utf8DecodedTwice,decodingPermutationLevel));
                        if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                        final String value_decodedTwiceNullBytesRemoved = ServerUtils.removeNullBytes(value_utf8DecodedTwice);
                        permutation.addNonStandardPermutation(value_decodedTwiceNullBytesRemoved);
                        if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                        //final String value_decodedTwiceNullBytesRemovedDecoded = ServerUtils.decodeBrokenValue(value_decodedTwiceNullBytesRemoved);
                        //permutation.addNonStandardPermutation(value_decodedTwiceNullBytesRemovedDecoded);
                        //if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                        permutation.addNonStandardPermutation(ServerUtils.removeWhitespaces(value_decodedTwiceNullBytesRemoved,decodingPermutationLevel));
                        if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                        permutation.addNonStandardPermutation(ServerUtils.removeBackslashes(value_utf8DecodedTwice));
                        if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                        permutation.addNonStandardPermutation(ServerUtils.removeBackslashes(value_decodedTwiceNullBytesRemoved));
                        if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());

                        if (decodingPermutationLevel >= 4) {
                            // using separate loops to feed back the results of each step
                            for (final Iterator iter = permutation.getNonStandardPermutations().iterator(); iter.hasNext();) {
                                permutation.addNonStandardPermutation( ServerUtils.replaceCommentsWithSpace((String)iter.next()) );
                            }
                            if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                            for (final Iterator iter = permutation.getNonStandardPermutations().iterator(); iter.hasNext();) {
                                permutation.addNonStandardPermutation( ServerUtils.removeWhitespaces((String)iter.next(),decodingPermutationLevel) );
                            }
                            if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                            for (final Iterator iter = permutation.getNonStandardPermutations().iterator(); iter.hasNext();) {
                                permutation.addNonStandardPermutation( ServerUtils.removeBackslashes((String)iter.next()) );
                            }
                            if (DEBUG_PRINT_TUNING_TIMINGS) System.out.println(System.currentTimeMillis());
                        }
                    }
                }
            }
        }
        //System.out.println(decodingPermutationLevel+": "+permutation);
        permutation.seal();
        return permutation;
    }
            
            
    public static boolean isVariantMatching(final Permutation permutation, final WordDictionary prefilter, final Matcher emptyMatcherToReuse, final boolean checkExtremePermutationsIfExisting) {
        String value;
        for (final Iterator/*<String>*/ iter = permutation.getStandardPermutations().iterator(); iter.hasNext();) {
            value = (String) iter.next();
            if (WordMatchingUtils.matchesWord(prefilter,value,WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && emptyMatcherToReuse.reset(value).find()) {
                return true;
            }
        }
        if (checkExtremePermutationsIfExisting) {
            for (final Iterator/*<String>*/ iter = permutation.getNonStandardPermutations().iterator(); iter.hasNext();) {
                value = (String) iter.next();
                if (WordMatchingUtils.matchesWord(prefilter,value,WebCastellumFilter.TRIE_MATCHING_THRSHOLD) && emptyMatcherToReuse.reset(value).find()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    
    
    
    public static String escapeSpecialCharactersHTML (final String input) {
        if (input == null) return ""; // returning empty string instead of null here !
        final StringBuilder result = new StringBuilder();
        
        final StringCharacterIterator iterator = new StringCharacterIterator(input);
        char character =  iterator.current();
        while (character != CharacterIterator.DONE ){
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '\"') {
                result.append("&quot;");
            } else if (character == '\'') {
                result.append("&#039;");
            } else if (character == '\\') {
                result.append("&#092;");
            } else if (character == '&') {
                result.append(AMP);
            } else {
                //the char is not a special one
                //add it to the result as is
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }
    
    
    
    
    public static String extractFileFromURL(String url) {
        if (url == null) return null;
        // in case the URL contains a query-string remove that first
        final int firstQuestionmark = url.indexOf('?');
        if (firstQuestionmark != -1) url = url.substring(0, firstQuestionmark);
        // go on
        final int firstSemicolon = url.indexOf(';'); // as we're using "url" which holds all up to the first question-mark, this is the left-most semicolon *before* the first question-mark, so this is correct!
        final int lastSlash = firstSemicolon==-1 ? url.lastIndexOf('/') : url.lastIndexOf('/',firstSemicolon);
        final int beginningOfSessionIdOrQueryString = firstSemicolon==-1 ? url.length() : firstSemicolon;
        if (lastSlash == -1 || lastSlash == beginningOfSessionIdOrQueryString) {
            return url.substring(0,beginningOfSessionIdOrQueryString);
        } else if (    lastSlash == 6 && url.toLowerCase().startsWith("http://")
                       || lastSlash == 7 && url.toLowerCase().startsWith("https://")) {
            return null;
        } else {
            return url.substring(lastSlash+1,beginningOfSessionIdOrQueryString);
        }
    }
    
    
    
    public static String[] convertCollectionToStringArray(Collection col) {
        if (col == null) return null;
        final String[] strings = new String[col.size()];
        int i=0;
        for (final Iterator iter = col.iterator(); iter.hasNext();) {
            strings[i++] = ""+iter.next();// yes, here we flatten all objects (even file descriptors) into strings
        }
        return strings;
    }
    public static Map convertMapOfCollectionsToMapOfStringArrays(Map mapOfCollections) {
        if (mapOfCollections == null) return null;
        final Map/*<String,String[]>*/ result = new HashMap();
        if (mapOfCollections.isEmpty()) return result;
        for (final Iterator entries = mapOfCollections.entrySet().iterator(); entries.hasNext();) {
            final Map.Entry/*<String,Collection>*/ entry = (Map.Entry) entries.next();
            final String key = (String) entry.getKey();
            final Collection col = (Collection) entry.getValue();
            if (col == null) {
                result.put(key, null);
            } else {
                result.put(key, convertCollectionToStringArray(col));
            }
        }
        return result;
    }    
    
    
    

    public static String extractResourceToBeAccessed(String url, final String currentContextPath, String currentRequestUriToUseAsBaseForRelativeLinks, final boolean useFullPathForResourceToBeAccessedProtection) {
        // TODO: hier evtl. noch HTML <base> tags im response beachten und parsen ?!?
        Logger.getLogger(ClusterSubscribeIncrementingCounterClient.class.getName()).log(Level.FINE, "extractResourceToBeAccessed ARGUMENTS url={0}",url);
        Logger.getLogger(ClusterSubscribeIncrementingCounterClient.class.getName()).log(Level.FINE, "currentContextPath={0}", currentContextPath);
        Logger.getLogger(ClusterSubscribeIncrementingCounterClient.class.getName()).log(Level.FINE, "currentRequestUriToUseAsBaseForRelativeLinks={0}", currentRequestUriToUseAsBaseForRelativeLinks);
        Logger.getLogger(ClusterSubscribeIncrementingCounterClient.class.getName()).log(Level.FINE, "useFullPathForResourceToBeAccessedProtection={0}", useFullPathForResourceToBeAccessedProtection);
        // if url seems to be self-submitted form, simply use the currentRequestUri instead
        if (url.length()==0 || "?".equals(url)) url = currentRequestUriToUseAsBaseForRelativeLinks;
        // go on
        url = ResponseUtils.extractURI(url).trim();
        url = url.replaceAll("/\\./", "/"); // = replace all /./ with /
        if (url.length() > 2 && url.startsWith("./")) url = url.substring(2); // = remove leading ./
        final String urlLower = url.toLowerCase();
        final String resourceToBeAccessed;
        if (useFullPathForResourceToBeAccessedProtection) {
            if (isRelativeLink(url)) {
                currentRequestUriToUseAsBaseForRelativeLinks = ResponseUtils.extractURI(currentRequestUriToUseAsBaseForRelativeLinks).trim();
                final String contextPathWithTrailingSlash =    (currentContextPath.length() > 0 && currentContextPath.charAt(currentContextPath.length()-1) == '/')    ?    currentContextPath : currentContextPath+"/";
                final int posContext = currentRequestUriToUseAsBaseForRelativeLinks.indexOf(contextPathWithTrailingSlash);
                if (posContext != -1) {
                    currentRequestUriToUseAsBaseForRelativeLinks = currentRequestUriToUseAsBaseForRelativeLinks.substring(posContext+contextPathWithTrailingSlash.length());
                }
                final int posLastSlash = currentRequestUriToUseAsBaseForRelativeLinks.lastIndexOf("/"); // = crop file
                if (posLastSlash != -1) {
                    currentRequestUriToUseAsBaseForRelativeLinks = currentRequestUriToUseAsBaseForRelativeLinks.substring(0,posLastSlash);
                }
                while (url.startsWith("../")) {
                    url = url.substring(3);
                    final int lastSlash = currentRequestUriToUseAsBaseForRelativeLinks.lastIndexOf("/"); // = crop last path folder
                    if (lastSlash != -1) currentRequestUriToUseAsBaseForRelativeLinks = currentRequestUriToUseAsBaseForRelativeLinks.substring(0,lastSlash);
                }
                if (!url.startsWith(currentContextPath)) {
                    final String tmp = url;
                    url = currentContextPath;
                    url += (url.endsWith("/")||currentRequestUriToUseAsBaseForRelativeLinks.startsWith("/")?"":"/") + currentRequestUriToUseAsBaseForRelativeLinks;
                    url += (url.endsWith("/")||tmp.startsWith("/")?"":"/") + tmp;
                }
                resourceToBeAccessed = url;
            } else {
                if (urlLower.startsWith("http://") || urlLower.startsWith("https://")) { 
                    final int thirdSlash = urlLower.indexOf("/", 8);
                    if (thirdSlash == -1) resourceToBeAccessed = "/";
                    else resourceToBeAccessed = url.substring(thirdSlash);
                } else {
                    if (!url.startsWith(currentContextPath)) {
                        url = currentContextPath + (url.startsWith("/")||currentContextPath.endsWith("/")?"":"/") + url;
                    }
                    resourceToBeAccessed = url;
                }
            }
        } else { // =============== simple style that only takes the resource's file and never looks at its path:
            int lastSlashOrStart = url.lastIndexOf('/');
            if (lastSlashOrStart == -1) lastSlashOrStart = 0; 
            else lastSlashOrStart++;
            resourceToBeAccessed = url.substring(lastSlashOrStart);
        }
        Logger.getLogger(ClusterSubscribeIncrementingCounterClient.class.getName()).log(Level.FINE, "extractResourceToBeAccessed RETURN {0}", resourceToBeAccessed);
        return resourceToBeAccessed;
    }
    
    
    public static boolean isRelativeLink(String link) {
        link = link.trim().toLowerCase();
        if (link.startsWith("http://") || link.startsWith("https://") || link.startsWith("/")) return false;
        return true;
    }

    
    
    
    
    
    
    public static String removeParameterFromQueryString(final String queryString, String parameterNameToRemove) {
        if (queryString == null) return null;
        if (parameterNameToRemove == null) return queryString;
        if (queryString.equals(parameterNameToRemove)) return "";
        if (!queryString.contains(parameterNameToRemove)) {
            try {
                parameterNameToRemove = URLEncoder.encode(parameterNameToRemove, WebCastellumFilter.DEFAULT_CHARACTER_ENCODING);
            } catch (Exception ignored) {}
            if (!queryString.contains(parameterNameToRemove)) return queryString;
        }
        final int pos = queryString.indexOf(parameterNameToRemove);
        assert pos > -1;
        // watch out for param that has a value (up to first & or # after pos)
        int i;
        for (i=pos+parameterNameToRemove.length(); i<queryString.length(); i++) {
            if (queryString.charAt(i) == '#') break;
            if (queryString.charAt(i) == '&') {
                i++;
                break;
            }
        }
        final int lengthToRemove = i-pos;
        assert pos > -1;
//OLD        // watch out for trailing "&" and trailing "=" and trailing "=&"
//OLD        if (queryString.contains(parameterNameToRemove+"=&")) lengthToRemove += 2;
//OLD        else if (queryString.contains(parameterNameToRemove+"&") || queryString.contains(parameterNameToRemove+"=")) lengthToRemove += 1;
        // extract rsult
        final StringBuilder result = new StringBuilder(queryString.length());
        result.append( queryString.substring(0,pos) );
        result.append( queryString.substring(pos+lengthToRemove) );
        String finalResult = result.toString();
        // remove trailing "&" of final result
        if (finalResult.length() > 0 && finalResult.charAt(finalResult.length()-1) == '&') finalResult = finalResult.substring(0,finalResult.length()-1);
        return finalResult;
    }
    
    
    
    
    
    


    /**
      * @return the key of the session key/value pair which has a value equal to the given content
      */
    public static String findReusableSessionContentKeyOrCreateNewOne(final HttpSession session, final ParameterAndFormProtection content, final boolean reuseSessionContent, final boolean applySetAfterWrite) {
        if (content == null) throw new NullPointerException("content must not be null");
        content.markAsFilled();
        if (reuseSessionContent) {
            // find and return reusable key
            /*synchronized (session) {*/ // = here we avoid the synchronization on the session for performance reasons and tolerate the eventual duplicate / overwritten reusage information, since it is only an optional cache
                List/*<String>*/ reusableKeys = (List) getAttributeIncludingInternal(session,WebCastellumFilter.SESSION_REUSABLE_KEY_LIST_KEY);
                if (reusableKeys != null) {
                    synchronized (reusableKeys) {
                        for (final Iterator keys = reusableKeys.iterator(); keys.hasNext();) {
                            // TODO: statt hier ueber die Liste der Keys zu loopen und jedes einzelne Element zu holen und dann per equals() einzeln zu vergleichen... lieber mit einer hashCode()-basierten schnelleren Lookup-Variante (Map? mit back-references zu den Keys?) arbeiten...
                            final String key = (String) keys.next();
                            final Object value = getAttributeIncludingInternal(session,WebCastellumFilter.INTERNAL_CONTENT_PREFIX+key);
                            if (value != null && value.equals(content)) {
                                Logger.getLogger(ClusterSubscribeIncrementingCounterClient.class.getName()).log(Level.FINE, "Session content can be reused ({0} equals {1}): {2}" , new Object[]{value,content,key});
                                return key;
                            }
                        }
                    }
                }
                // create new one and place into session
                final String key = CryptoUtils.generateRandomToken(true);
                session.setAttribute(WebCastellumFilter.INTERNAL_CONTENT_PREFIX+key, content);
                // also track list of those keys allowed for reuse (to avoid mixing with application's session contents)
                if (reusableKeys == null) {
                    reusableKeys = Collections.synchronizedList( new ArrayList() );
                    session.setAttribute(WebCastellumFilter.SESSION_REUSABLE_KEY_LIST_KEY, reusableKeys);
                }
                assert reusableKeys != null;
                reusableKeys.add(key);
                if (applySetAfterWrite) session.setAttribute(WebCastellumFilter.SESSION_REUSABLE_KEY_LIST_KEY, reusableKeys);
                return key;
            /*}*/
        } else {
            final String key = CryptoUtils.generateRandomToken(true);
            session.setAttribute(WebCastellumFilter.INTERNAL_CONTENT_PREFIX+key, content);
            return key;
        }
    }
    
    

    public static void renameSecretTokenParameterInAllCachedParameterAndFormProtectionObjects(final HttpSession session, final String oldSecretTokenParameterName, final String newSecretTokenParameterName, final boolean applySetAfterWrite) {
        if (session == null) return;
        try {
            for (final Enumeration names = getAttributeNamesIncludingInternal(session); names.hasMoreElements();) {
                final String name = (String) names.nextElement();
                final Object value = getAttributeIncludingInternal(session, name);
                if (value instanceof ParameterAndFormProtection) {
                    ((ParameterAndFormProtection)value).renameSecretTokenParameterName(oldSecretTokenParameterName, newSecretTokenParameterName);
                    if (applySetAfterWrite) session.setAttribute(name, value);
                }
            }
        } catch (IllegalStateException e) {
            System.err.println("Not required to rename token parameter names since session already invalidated"); // TODO: better logging
        }
    }
    
    
    
    

    public static String urlEncode(final String value) throws UnsupportedEncodingException {
        if (value == null || value.length() == 0) return value;
        return URLEncoder.encode(value, WebCastellumFilter.DEFAULT_CHARACTER_ENCODING);
    }
    public static String urlDecode(final String value) throws UnsupportedEncodingException {
        if (value == null || value.length() == 0) return value;
        return URLDecoder.decode(value, WebCastellumFilter.DEFAULT_CHARACTER_ENCODING);
    }
    
    
    
    
    public static Matcher[] replaceEmptyMatchersWithNull(final Matcher[] matchers) {
        for (int i=0; i<matchers.length; i++) {
            if (matchers[i] != null && matchers[i].pattern().pattern().trim().length() == 0) matchers[i] = null;
        }
        return matchers;
    }
    
    
    
    
    public static String removeLeadingWhitespace(final String value) {
        if (value == null) return null;
        final StringBuilder result = new StringBuilder();
        boolean reachedNonWhitespace = false;
        for (int i=0; i<value.length(); i++) {
            final char c = value.charAt(i);
            if (!reachedNonWhitespace) {
                if ( c <= ' ' ) {
                    continue;
                } else {
                    reachedNonWhitespace = true;
                }
            }
            result.append(c);
        }
        return result.toString();
    }
    
    
    
    public static String quoteReplacement(String value) {
        if (value == null) return null;
        return Matcher.quoteReplacement(value);
//        value = value.replaceAll("\\\\", "\\\\\\\\");
//        value = value.replaceAll("\\$", "\\\\\\$");
//        return value;
    }
    
    
    
    public static Enumeration getAttributeNamesIncludingInternal(final HttpSession session) {
        if (session == null) throw new NullPointerException("session must not be null");
        if (session instanceof SessionWrapper) {
            return ((SessionWrapper)session).getAttributeNamesIncludingInternal();
        } else return session.getAttributeNames();
    }

    public static Object getAttributeIncludingInternal(final HttpSession session, final String name) {
        if (session == null) throw new NullPointerException("session must not be null");
        if (session instanceof SessionWrapper) {
            return ((SessionWrapper)session).getAttributeIncludingInternal(name);
        } else return session.getAttribute(name);
    }
        
    
    
    
    public static Matcher[] convertListOfPatternToArrayOfMatcher(final List/*<Pattern>*/ patterns) {
        if (patterns == null) return null;
        final Matcher[] matchers = new Matcher[patterns.size()];
        int i=0;
        for (final Iterator/*<Pattern>*/ iter = patterns.iterator(); iter.hasNext(); i++) {
            final Pattern pattern = (Pattern) iter.next();
            matchers[i] = pattern == null ? null : pattern.matcher("");
        }
        return matchers;
    }
    
    
        
    public static String[] concatenateArrays(final String[] original, final String[] additional) {
        if (original == null || original.length == 0) return additional;
        if (additional == null || additional.length == 0) return original;
        final String[] combined = new String[original.length + additional.length];
        System.arraycopy(original, 0, combined, 0, original.length);
        System.arraycopy(additional, 0, combined, original.length, additional.length);
        return combined;
    }
    
    
    public static int[] convertIntegerListToIntArray(final List/*<Integer>*/ listOfIntegers) {
        if (listOfIntegers == null) return null;
        final int[] result = new int[ listOfIntegers.size() ];
        int i=0;
        for (final Iterator iter = listOfIntegers.iterator(); iter.hasNext();) {
            final Integer element = (Integer) iter.next();
            result[i++] = element.intValue();
        }
        return result;
    }
    
    
}
