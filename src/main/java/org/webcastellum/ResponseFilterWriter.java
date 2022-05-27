package org.webcastellum;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import javax.crypto.Cipher;
import javax.servlet.http.HttpSession;




public final class ResponseFilterWriter extends AbstractRelaxingHtmlParserWriter {

    private static final boolean DEBUG = false;
    
    
    private final CharArrayWriter scriptBody = new CharArrayWriter();
    private final CharArrayWriter collectedDisplayValue = new CharArrayWriter();
    
    
    private final boolean stripHtmlEnabled, injectSecretTokensEnabled, protectParamsAndFormsEnabled, encryptQueryStringsEnabled, useFullPathForResourceToBeAccessedProtection, additionalFullResourceRemoval, additionalMediumResourceRemoval, appendSessionIdToLinks, applySetAfterWrite;
    private final String currentRequestUrlToCompareWith, servletPath, contextPath;
    private final String tokenKey, tokenValue;
    private final String protectionTokenKeyKey;
    private final ContentInjectionHelper contentInjectionHelper;
    private final String cryptoDetectionString; // to detect encrypted URLs which have (by definition) already the tokens injected if token injection is active
    private final RequestWrapper request;
    private final ResponseWrapper response;
    private final Cipher cipher;
    private final CryptoKeyAndSalt cryptoKey;
    private final boolean applyExtraProtectionForDisabledFormFields, applyExtraProtectionForReadonlyFormFields, applyExtraProtectionForRequestParamValueCount, maskAmpersandsInModifiedLinks, hiddenFormFieldProtection, selectboxProtection, checkboxProtection, radiobuttonProtection, selectboxValueMasking, checkboxValueMasking, radiobuttonValueMasking, appendQuestionmarkOrAmpersandToLinks, reuseSessionContent;

    private final Matcher[] matchersToExcludeLinksWithinScripts, matchersToExcludeLinksWithinTags, matchersToExcludeCompleteScript, matchersToExcludeCompleteTag;
    private final Matcher[] matchersToCaptureLinksWithinScripts, matchersToCaptureLinksWithinTags;
    private final WordDictionary[] prefiltersToExcludeLinksWithinScripts, prefiltersToExcludeLinksWithinTags, prefiltersToExcludeCompleteScript, prefiltersToExcludeCompleteTag;
    private final WordDictionary[] prefiltersToCaptureLinksWithinScripts, prefiltersToCaptureLinksWithinTags;
    private final int[][] groupNumbersToCaptureLinksWithinScripts, groupNumbersToCaptureLinksWithinTags;
    //private final List<String>[] tagNamesToCheck;
    
    private boolean isWithinScript = false, isWithinStyle = false, isWithinForm = false, isWithinSelectBox = false, isWithinOption = false, isCollectingDisplayValueAsOptionValue = false;
    private ParameterAndFormProtection parameterAndFormProtectionOfCurrentForm = null;
    private String actionUrlOfCurrentForm, nameOfCurrentSelectBox, selectBoxMaskingPrefix, checkBoxMaskingPrefix, radioButtonMaskingPrefix;
    private boolean isCurrentFormRequestMethodPOST = false, isWithinHtmlBody = false, isWithinHtmlTable = false, isMultipartForm = false;
    private short honeylinkCount, tagPartCounter, tagPartCounterTarget = 17;
    private final Random honeylinkRandom;
    
    private final short honeylinkMaxPerPage;
    private final String honeylinkPrefix, honeylinkSuffix;

    private final FormFieldMaskingExcludeDefinition[] matchingFormFieldMaskingExclusions;
    private final List/*<FormFieldMaskingExcludeDefinition>*/ formFieldExclusionsOfCurrentForm = new ArrayList();
    
    private Matcher matcherFormMethodPost, matcherRequiredInputFormFieldExcludingHiddenFields, matcherRequiredInputFormField, matcherHiddenFormField, matcherCheckbox, matcherRadiobutton;
    
    
        
    
     
    
    
    
    public ResponseFilterWriter(final Writer delegate, final boolean useTunedBlockParser, 
            final String currentRequestUrlToCompareWith, final String contextPath, final String servletPath, final String tokenKey, final String tokenValue, final String protectionTokenKeyKey, final Cipher cipher, final CryptoKeyAndSalt cryptoKey, final ContentInjectionHelper contentInjectionHelper, final String cryptoDetectionString, final RequestWrapper request, final ResponseWrapper response,
            final boolean stripHtmlEnabled, final boolean injectSecretTokensEnabled, final boolean protectParamsAndFormsEnabled, final boolean encryptQueryStringsEnabled, 
            final boolean applyExtraProtectionForDisabledFormFields, final boolean applyExtraProtectionForReadonlyFormFields, final boolean applyExtraProtectionForRequestParamValueCount,
            final WordDictionary[] prefiltersToExcludeCompleteScript, final Matcher[] matchersToExcludeCompleteScript, 
            final WordDictionary[] prefiltersToExcludeCompleteTag, final Matcher[] matchersToExcludeCompleteTag,
            final WordDictionary[] prefiltersToExcludeLinksWithinScripts, final Matcher[] matchersToExcludeLinksWithinScripts, 
            final WordDictionary[] prefiltersToExcludeLinksWithinTags, final Matcher[] matchersToExcludeLinksWithinTags,
            final WordDictionary[] prefiltersToCaptureLinksWithinScripts, final Matcher[] matchersToCaptureLinksWithinScripts, 
            final WordDictionary[] prefiltersToCaptureLinksWithinTags, final Matcher[] matchersToCaptureLinksWithinTags,
            final int[][] groupNumbersToCaptureLinksWithinScripts, final int[][] groupNumbersToCaptureLinksWithinTags, 
            //final List<String>[] tagNamesToCheck,
            final boolean useFullPathForResourceToBeAccessedProtection, final boolean additionalFullResourceRemoval, final boolean additionalMediumResourceRemoval, final boolean maskAmpersandsInModifiedLinks,
            final boolean hiddenFormFieldProtection, final boolean selectboxProtection, final boolean checkboxProtection, final boolean radiobuttonProtection, final boolean selectboxValueMasking, final boolean checkboxValueMasking, final boolean radiobuttonValueMasking,
            final boolean appendQuestionmarkOrAmpersandToLinks, final boolean appendSessionIdToLinks,
            final boolean reuseSessionContent,
            final String honeylinkPrefix, final String honeylinkSuffix, final short honeylinkMaxPerPage, final boolean randomizeHoneylinksOnEveryRequest,
            final boolean applySetAfterWrite) {
        super(delegate,useTunedBlockParser);

        if (currentRequestUrlToCompareWith == null) throw new NullPointerException("currentRequestUrlToCompareWith must not be null");
        this.currentRequestUrlToCompareWith = currentRequestUrlToCompareWith;
        
        if (contextPath == null) throw new NullPointerException("contextPath must not be null");
        this.contextPath = contextPath;
        
        if (servletPath == null) throw new NullPointerException("servletPath must not be null");
        this.servletPath = servletPath;
        
        if (response == null) throw new NullPointerException("response must not be null");
        this.response = response;

        this.applySetAfterWrite = applySetAfterWrite;
        
        if (matchersToExcludeCompleteScript == null) throw new NullPointerException("matchersToExcludeCompleteScript must not be null");
        if (matchersToExcludeCompleteTag == null) throw new NullPointerException("matchersToExcludeCompleteTag must not be null");
        this.prefiltersToExcludeCompleteScript = prefiltersToExcludeCompleteScript;
        this.matchersToExcludeCompleteScript = ServerUtils.replaceEmptyMatchersWithNull(matchersToExcludeCompleteScript);
        this.prefiltersToExcludeCompleteTag = prefiltersToExcludeCompleteTag;
        this.matchersToExcludeCompleteTag = ServerUtils.replaceEmptyMatchersWithNull(matchersToExcludeCompleteTag);
        if (matchersToExcludeLinksWithinScripts == null) throw new NullPointerException("matchersToExcludeLinksWithinScripts must not be null");
        if (matchersToExcludeLinksWithinTags == null) throw new NullPointerException("matchersToExcludeLinksWithinTags must not be null");
        this.prefiltersToExcludeLinksWithinScripts = prefiltersToExcludeLinksWithinScripts;
        this.matchersToExcludeLinksWithinScripts = ServerUtils.replaceEmptyMatchersWithNull(matchersToExcludeLinksWithinScripts);
        this.prefiltersToExcludeLinksWithinTags = prefiltersToExcludeLinksWithinTags;
        this.matchersToExcludeLinksWithinTags = ServerUtils.replaceEmptyMatchersWithNull(matchersToExcludeLinksWithinTags);
        if (matchersToCaptureLinksWithinScripts == null) throw new NullPointerException("matchersToCaptureLinksWithinScripts must not be null");
        if (matchersToCaptureLinksWithinTags == null) throw new NullPointerException("matchersToCaptureLinksWithinTags must not be null");
        this.prefiltersToCaptureLinksWithinScripts = prefiltersToCaptureLinksWithinScripts;
        this.matchersToCaptureLinksWithinScripts = matchersToCaptureLinksWithinScripts;
        this.prefiltersToCaptureLinksWithinTags = prefiltersToCaptureLinksWithinTags;
        this.matchersToCaptureLinksWithinTags = matchersToCaptureLinksWithinTags;
        this.groupNumbersToCaptureLinksWithinScripts = groupNumbersToCaptureLinksWithinScripts;
        this.groupNumbersToCaptureLinksWithinTags = groupNumbersToCaptureLinksWithinTags;
        if (matchersToCaptureLinksWithinScripts.length != groupNumbersToCaptureLinksWithinScripts.length) throw new IllegalArgumentException("Lengths of capturing pattern and group-number array must be equal");
        if (matchersToCaptureLinksWithinTags.length != groupNumbersToCaptureLinksWithinTags.length) throw new IllegalArgumentException("Lengths of capturing pattern and group-number array must be equal");
        if (matchersToCaptureLinksWithinScripts.length != matchersToExcludeLinksWithinScripts.length) throw new IllegalArgumentException("Lengths of capturing pattern and exclusion pattern array must be equal");
        if (matchersToCaptureLinksWithinTags.length != matchersToExcludeLinksWithinTags.length) throw new IllegalArgumentException("Lengths of capturing pattern and exclusion pattern array must be equal");
        //this.tagNamesToCheck = tagNamesToCheck;
        
        this.tokenKey = tokenKey; // may be null which means no secret-token injection activated
        this.tokenValue = tokenValue; // may be null which means no secret-token injection activated
        this.protectionTokenKeyKey = protectionTokenKeyKey; // may be null which means no parameter-and-form protection activated
        this.cipher = cipher; // may be null which means no encryption activated
        this.cryptoKey = cryptoKey; // may be null which means no encryption activated
        if (contentInjectionHelper == null) throw new NullPointerException("contentInjectionHelper must not be null");
        this.contentInjectionHelper = contentInjectionHelper;
        this.cryptoDetectionString = cryptoDetectionString; // may be null which means no encryption activated
        this.request = request; // (used to fetch the most current [potentially already renewed] session) may be null which means no parameter-and-form protection activated
        this.useFullPathForResourceToBeAccessedProtection = useFullPathForResourceToBeAccessedProtection;
        this.additionalFullResourceRemoval = additionalFullResourceRemoval;
        this.additionalMediumResourceRemoval = additionalMediumResourceRemoval;
        
        this.stripHtmlEnabled = stripHtmlEnabled;
        this.injectSecretTokensEnabled = injectSecretTokensEnabled;
        this.protectParamsAndFormsEnabled = protectParamsAndFormsEnabled;
        this.encryptQueryStringsEnabled = encryptQueryStringsEnabled;
        this.applyExtraProtectionForDisabledFormFields = applyExtraProtectionForDisabledFormFields;
        this.applyExtraProtectionForReadonlyFormFields = applyExtraProtectionForReadonlyFormFields;
        this.applyExtraProtectionForRequestParamValueCount= applyExtraProtectionForRequestParamValueCount;
        this.maskAmpersandsInModifiedLinks = maskAmpersandsInModifiedLinks;
        
        this.matchingFormFieldMaskingExclusions = contentInjectionHelper.getFormFieldMaskingExcludeDefinitions() == null ? new FormFieldMaskingExcludeDefinition[0] : contentInjectionHelper.getFormFieldMaskingExcludeDefinitions().getAllMatchingFormFieldMaskingExcludeDefinitions(servletPath, request.getRequestURI());
        
        if (this.injectSecretTokensEnabled && (this.tokenKey == null || this.tokenValue == null)) throw new NullPointerException("tokenKey and/or tokenValue must not be null when injectSecretTokensEnabled is set");
        if (this.protectParamsAndFormsEnabled && (this.request == null || this.protectionTokenKeyKey == null)) throw new NullPointerException("request and/or protectionTokenKeyKey must not be null when protectParamsAndFormsEnabled is set");
        if (this.encryptQueryStringsEnabled && (this.cryptoDetectionString == null || this.cryptoKey == null)) throw new NullPointerException("cryptoDetectionString and/or cryptoKey must not be null when encryptQueryStringsEnabled is set");

        if (this.encryptQueryStringsEnabled && !this.injectSecretTokensEnabled) throw new IllegalArgumentException("encryptQueryStringsEnabled also requires to set injectSecretTokensEnabled");
        if (this.protectParamsAndFormsEnabled && !this.encryptQueryStringsEnabled) throw new IllegalArgumentException("protectParamsAndFormsEnabled also requires to set encryptQueryStringsEnabled");
        if (this.applyExtraProtectionForDisabledFormFields && !this.protectParamsAndFormsEnabled) throw new IllegalArgumentException("applyExtraProtectionForDisabledFormFields also requires to set protectParamsAndFormsEnabled");
        if (this.applyExtraProtectionForReadonlyFormFields && !this.protectParamsAndFormsEnabled) throw new IllegalArgumentException("applyExtraProtectionForReadonlyFormFields also requires to set protectParamsAndFormsEnabled");
        if (this.applyExtraProtectionForRequestParamValueCount && !this.protectParamsAndFormsEnabled) throw new IllegalArgumentException("applyExtraProtectionForRequestParamValueCount also requires to set protectParamsAndFormsEnabled");
        
        this.hiddenFormFieldProtection = hiddenFormFieldProtection;
        this.selectboxProtection = selectboxProtection;
        this.checkboxProtection = checkboxProtection;
        this.radiobuttonProtection = radiobuttonProtection;
        this.selectboxValueMasking = selectboxValueMasking;
        this.checkboxValueMasking = checkboxValueMasking;
        this.radiobuttonValueMasking = radiobuttonValueMasking;
        
        this.appendQuestionmarkOrAmpersandToLinks = appendQuestionmarkOrAmpersandToLinks;
        this.appendSessionIdToLinks = appendSessionIdToLinks;
        this.reuseSessionContent = reuseSessionContent;
        
        this.honeylinkPrefix = honeylinkPrefix;
        this.honeylinkSuffix = honeylinkSuffix;
        this.honeylinkMaxPerPage = honeylinkMaxPerPage;
        if (honeylinkMaxPerPage > 0) {
            this.honeylinkRandom = randomizeHoneylinksOnEveryRequest ? null : new Random( ((long) this.servletPath.hashCode())+WebCastellumFilter.customerIdentifier );
            this.tagPartCounterTarget = HoneylinkUtils.nextTagPartCounterTarget(this.honeylinkRandom);
        } else this.honeylinkRandom = null;
    }
    
    
    
    
    
    
    
    
    


    
    //1.5@Override
    public void handleTag(String tag) throws IOException {
        if (DEBUG) System.out.println("handleTag: "+tag);
        
        
        
        boolean startsWithScriptOpening = false;
        boolean startsWithStyleOpening = false;
        boolean startsWithCommentOpening = false;
        boolean startsWithFormOpening = false;
        boolean startsWithInputOpening = false;
        boolean startsWithButtonOpening = false;
        boolean startsWithTextareaOpening = false;
        boolean startsWithSelectOpening = false;
        boolean startsWithOptionOpening = false;
        if (USE_DIRECT_ARRAY_LOOKUPS_INSTEAD_OF_STARTS_WITH) {
            // direct char position checks are faster than String.startsWith()
            // using direct checks against lower/upper case letter is faster than toLowerCase() for input string
            if (tag.length() >= 4) { // ====== length greater than or equal to four
                startsWithCommentOpening = tag.charAt(1)=='!' && tag.charAt(2)=='-' && tag.charAt(3)=='-';
                if (tag.length() >= 5) { // ====== length greater than or equal to five
                    startsWithFormOpening = (tag.charAt(1)=='f'||tag.charAt(1)=='F') && (tag.charAt(2)=='o'||tag.charAt(2)=='O') && (tag.charAt(3)=='r'||tag.charAt(3)=='R') && (tag.charAt(4)=='m'||tag.charAt(4)=='M');
                    if (tag.length() >= 6) { // ====== length greater than or equal to six
                        startsWithStyleOpening = (tag.charAt(1)=='s'||tag.charAt(1)=='S') && (tag.charAt(2)=='t'||tag.charAt(2)=='T') && (tag.charAt(3)=='y'||tag.charAt(3)=='Y') && (tag.charAt(4)=='l'||tag.charAt(4)=='L') && (tag.charAt(5)=='e'||tag.charAt(5)=='E');
                        startsWithInputOpening = (tag.charAt(1)=='i'||tag.charAt(1)=='I') && (tag.charAt(2)=='n'||tag.charAt(2)=='N') && (tag.charAt(3)=='p'||tag.charAt(3)=='P') && (tag.charAt(4)=='u'||tag.charAt(4)=='U') && (tag.charAt(5)=='t'||tag.charAt(5)=='T');
                        if (tag.length() >= 7) { // ====== length greater than or equal to seven
                            if ((tag.charAt(1)=='s'||tag.charAt(1)=='S') && (tag.charAt(6)=='t'||tag.charAt(6)=='T')) {
                                startsWithScriptOpening = (tag.charAt(2)=='c'||tag.charAt(2)=='C') && (tag.charAt(3)=='r'||tag.charAt(3)=='R') && (tag.charAt(4)=='i'||tag.charAt(4)=='I') && (tag.charAt(5)=='p'||tag.charAt(5)=='P');
                                startsWithSelectOpening = (tag.charAt(2)=='e'||tag.charAt(2)=='E') && (tag.charAt(3)=='l'||tag.charAt(3)=='L') && (tag.charAt(4)=='e'||tag.charAt(4)=='E') && (tag.charAt(5)=='c'||tag.charAt(5)=='C');
                            } else if ((tag.charAt(3)=='t'||tag.charAt(3)=='T') && (tag.charAt(5)=='o'||tag.charAt(5)=='O') && (tag.charAt(6)=='n'||tag.charAt(6)=='N')) {
                                startsWithButtonOpening = (tag.charAt(1)=='b'||tag.charAt(1)=='B') && (tag.charAt(2)=='u'||tag.charAt(2)=='U') && (tag.charAt(4)=='t'||tag.charAt(4)=='T');
                                startsWithOptionOpening = (tag.charAt(1)=='o'||tag.charAt(1)=='O') && (tag.charAt(2)=='p'||tag.charAt(2)=='P') && (tag.charAt(4)=='i'||tag.charAt(4)=='I');
                            }
                            if (tag.length() >= 9) { // ====== length greater than or equal to nine
                                startsWithTextareaOpening = (tag.charAt(1)=='t'||tag.charAt(1)=='T') && (tag.charAt(2)=='e'||tag.charAt(2)=='E') && (tag.charAt(3)=='x'||tag.charAt(3)=='X') && (tag.charAt(4)=='t'||tag.charAt(4)=='T') && (tag.charAt(5)=='a'||tag.charAt(5)=='A') && (tag.charAt(6)=='r'||tag.charAt(6)=='R') && (tag.charAt(7)=='e'||tag.charAt(7)=='E') && (tag.charAt(8)=='a'||tag.charAt(8)=='A');
                            }
                        }
                    }
                }
            }
            /* SLOWER
            if (tag.length() >= 4) { // ====== length greater than or equal to four
                startsWithCommentOpening = tag.charAt(1)=='!' && tag.charAt(2)=='-' && tag.charAt(3)=='-';
                if (!startsWithCommentOpening && tag.length() >= 5) { // ====== length greater than or equal to five
                    startsWithFormOpening = (tag.charAt(1)=='f'||tag.charAt(1)=='F') && (tag.charAt(2)=='o'||tag.charAt(2)=='O') && (tag.charAt(3)=='r'||tag.charAt(3)=='R') && (tag.charAt(4)=='m'||tag.charAt(4)=='M');
                    if (!startsWithFormOpening && tag.length() >= 6) { // ====== length greater than or equal to six
                        startsWithStyleOpening = (tag.charAt(1)=='s'||tag.charAt(1)=='S') && (tag.charAt(2)=='t'||tag.charAt(2)=='T') && (tag.charAt(3)=='y'||tag.charAt(3)=='Y') && (tag.charAt(4)=='l'||tag.charAt(4)=='L') && (tag.charAt(5)=='e'||tag.charAt(5)=='E');
                        if (!startsWithStyleOpening) {
                            startsWithInputOpening = (tag.charAt(1)=='i'||tag.charAt(1)=='I') && (tag.charAt(2)=='n'||tag.charAt(2)=='N') && (tag.charAt(3)=='p'||tag.charAt(3)=='P') && (tag.charAt(4)=='u'||tag.charAt(4)=='U') && (tag.charAt(5)=='t'||tag.charAt(5)=='T');
                            if (!startsWithStyleOpening && !startsWithInputOpening && tag.length() >= 7) { // ====== length greater than or equal to seven
                                if ((tag.charAt(1)=='s'||tag.charAt(1)=='S') && (tag.charAt(6)=='t'||tag.charAt(6)=='T')) {
                                    startsWithScriptOpening = (tag.charAt(2)=='c'||tag.charAt(2)=='C') && (tag.charAt(3)=='r'||tag.charAt(3)=='R') && (tag.charAt(4)=='i'||tag.charAt(4)=='I') && (tag.charAt(5)=='p'||tag.charAt(5)=='P');
                                    if (!startsWithScriptOpening) startsWithSelectOpening = (tag.charAt(2)=='e'||tag.charAt(2)=='E') && (tag.charAt(3)=='l'||tag.charAt(3)=='L') && (tag.charAt(4)=='e'||tag.charAt(4)=='E') && (tag.charAt(5)=='c'||tag.charAt(5)=='C');
                                } else if ((tag.charAt(3)=='t'||tag.charAt(3)=='T') && (tag.charAt(5)=='o'||tag.charAt(5)=='O') && (tag.charAt(6)=='n'||tag.charAt(6)=='N')) {
                                    startsWithButtonOpening = (tag.charAt(1)=='b'||tag.charAt(1)=='B') && (tag.charAt(2)=='u'||tag.charAt(2)=='U') && (tag.charAt(4)=='t'||tag.charAt(4)=='T');
                                    if (!startsWithButtonOpening) startsWithOptionOpening = (tag.charAt(1)=='o'||tag.charAt(1)=='O') && (tag.charAt(2)=='p'||tag.charAt(2)=='P') && (tag.charAt(4)=='i'||tag.charAt(4)=='I');
                                }
                                if (!startsWithScriptOpening && !startsWithSelectOpening && !startsWithButtonOpening && !startsWithOptionOpening && tag.length() >= 9) { // ====== length greater than or equal to nine
                                    startsWithTextareaOpening = (tag.charAt(1)=='t'||tag.charAt(1)=='T') && (tag.charAt(2)=='e'||tag.charAt(2)=='E') && (tag.charAt(3)=='x'||tag.charAt(3)=='X') && (tag.charAt(4)=='t'||tag.charAt(4)=='T') && (tag.charAt(5)=='a'||tag.charAt(5)=='A') && (tag.charAt(6)=='r'||tag.charAt(6)=='R') && (tag.charAt(7)=='e'||tag.charAt(7)=='E') && (tag.charAt(8)=='a'||tag.charAt(8)=='A');
                                }
                            }
                        }
                    }
                }
            }
             */
        } else {
            final String firstFewLower = ResponseUtils.firstTenCharactersLower(tag);
            startsWithScriptOpening = firstFewLower.startsWith("<script");
            startsWithStyleOpening = firstFewLower.startsWith("<style");
            startsWithCommentOpening = firstFewLower.startsWith("<!--");
            startsWithFormOpening = firstFewLower.startsWith("<form");
            startsWithInputOpening = firstFewLower.startsWith("<input");
            startsWithButtonOpening = firstFewLower.startsWith("<button");
            startsWithTextareaOpening = firstFewLower.startsWith("<textarea");
            startsWithSelectOpening = firstFewLower.startsWith("<select");
            startsWithOptionOpening = firstFewLower.startsWith("<option");
        }
        
        
        
        
        
        
        
        
        
        if (this.stripHtmlEnabled && !this.isWithinScript && !this.isWithinStyle && startsWithCommentOpening) {
            return; // = inside a comment (outside a script/style block) and comment-stripping is enabled, so simply return
        }
        
        if (this.honeylinkMaxPerPage> 0) {
            if (USE_DIRECT_ARRAY_LOOKUPS_INSTEAD_OF_STARTS_WITH) {
                // direct char position checks are faster than String.startsWith()
                // using direct checks against lower/upper case letter is faster than toLowerCase() for input string
                if (tag.length() >= 5 
                        && ((tag.charAt(1)=='b'||tag.charAt(1)=='B') && (tag.charAt(2)=='o'||tag.charAt(2)=='O') && (tag.charAt(3)=='d'||tag.charAt(3)=='D') && (tag.charAt(4)=='y'||tag.charAt(4)=='Y')) ) this.isWithinHtmlBody = true;
                if (tag.length() >= 6
                        && ((tag.charAt(1)=='t'||tag.charAt(1)=='T') && (tag.charAt(2)=='a'||tag.charAt(2)=='A') && (tag.charAt(3)=='b'||tag.charAt(3)=='B') && (tag.charAt(4)=='l'||tag.charAt(4)=='L') && (tag.charAt(5)=='e'||tag.charAt(5)=='E')) ) this.isWithinHtmlTable = true;
            } else {
                final String firstFewLower = ResponseUtils.firstTenCharactersLower(tag);
                if (firstFewLower.startsWith("<body")) this.isWithinHtmlBody = true;
                if (firstFewLower.startsWith("<table")) this.isWithinHtmlTable = true;
            }
        }
        
        if (startsWithStyleOpening) this.isWithinStyle = true;
        
        if (startsWithScriptOpening) {
            if (this.isWithinScript) { // = nested <script> tags are not allowed in HTML and therefore ignored here
                return;
            } else { // = a new script starts
                this.isWithinScript = true;
                this.scriptBody.reset();
            }
        }
        
        // determine if this link is the target of a POST form or a GET form or regular link (= also GET)
        boolean isRequestMethodPOST= false;
        if (startsWithFormOpening) { 
            if (this.matcherFormMethodPost == null) this.matcherFormMethodPost = PATTERN_FORM_METHOD_POST.matcher(tag); else this.matcherFormMethodPost.reset(tag);
            this.isMultipartForm = ResponseUtils.isMultipartForm(tag);
            // only forms that explicitly have method=POST are of request method type POST
            if (this.matcherFormMethodPost.find()) isRequestMethodPOST= true;
            if (DEBUG) System.out.println("POST ?: "+isRequestMethodPOST);
        }
        
        // TODO: hier auch File-Upload Felder beruecksichtigen ?!?
        if (this.protectParamsAndFormsEnabled && this.isWithinForm && !this.isWithinScript && !this.isWithinStyle && this.parameterAndFormProtectionOfCurrentForm != null
                && ( startsWithInputOpening ||
                       startsWithButtonOpening ||
                       startsWithSelectOpening ||
                       startsWithTextareaOpening )) {
            final String extractedFieldName = ResponseUtils.extractFieldName(tag);
            if (extractedFieldName != null) {
                // watch out for disabled form fields - when configured to do so (when client-side JavaScript changed the disabled-state of some form-fields better disable the applyExtraProtectionForDisabledFormFields configuration flag
                if (!this.applyExtraProtectionForDisabledFormFields || !ResponseUtils.isFormFieldDisabled(tag)) { // = don't add disabled form fields since they won't get submitted by the browser (and hence the application may rely on NOT receiving them, so we stop an attacker from manually submitting disabled form fields here)

                    // All (enabled) form fields are submitted to the server (meaning that they are required to be received [empty value means also received]), 
                    // but checkboxes, radiobuttons, submit buttons and selectboxes are only submitted when activated explicitly (set clicked, etc) so
                    // we can only treat textarea, input (type text), input (type password) as required to be submitted. Hidden fields are filtered out anyway by out form protection...
                    // And URL parameters are encrypted, so there is no way for someone to omit a URL parameter...
                    final boolean potentiallyRequiredMatch;
                    if (this.hiddenFormFieldProtection) {
                        if (this.matcherRequiredInputFormFieldExcludingHiddenFields == null) this.matcherRequiredInputFormFieldExcludingHiddenFields = PATTERN_REQUIRED_INPUT_FORM_FIELD_EXCLUDING_HIDDEN_FIELDS.matcher(tag); else this.matcherRequiredInputFormFieldExcludingHiddenFields.reset(tag);
                        potentiallyRequiredMatch = matcherRequiredInputFormFieldExcludingHiddenFields.find();
                    } else {
                        if (this.matcherRequiredInputFormField == null) this.matcherRequiredInputFormField = PATTERN_REQUIRED_INPUT_FORM_FIELD.matcher(tag); else this.matcherRequiredInputFormField.reset(tag);
                        potentiallyRequiredMatch = this.matcherRequiredInputFormField.find();
                    }
                    final boolean required;
                    if (applyExtraProtectionForDisabledFormFields) required = startsWithTextareaOpening || (startsWithInputOpening && potentiallyRequiredMatch);
                    else required = false; // = when we don't have any special disabled field protection we cannot safely assure required fields since client-sided JavaScript-code can then for example disabled fields dynamically when then won't get sbmitted..
                    final String extractedFieldNameDecoded = ServerUtils.decodeBrokenValueExceptUrlEncoding(extractedFieldName);
                    
                    // watch out for hidden form fields - when configured to do so
                    if (this.hiddenFormFieldProtection) {
                        if (this.matcherHiddenFormField == null) this.matcherHiddenFormField = PATTERN_HIDDEN_FORM_FIELD.matcher(tag); else this.matcherHiddenFormField.reset(tag);
                        if (this.matcherHiddenFormField.find() && !isFormFieldMaskingExclusion(extractedFieldNameDecoded)) {
                            final String expectedValue = ResponseUtils.extractFieldValue(tag); // TODO diese zeile nicht etwas tiefer erneut ausfuehren..lieber im bedarfsfall nur 1 mal ausfuehren und als lokale variable halten.. gleiches gilt fuer mehrfache verwendung von !ResponseUtils.isFormFieldDisabled(tag)
                            if (!WebCastellumFilter.CAPTCHA_FORM.equals(extractedFieldNameDecoded)) { // = don't remove CAPTCHA hidden form fields
                                if (!ResponseUtils.isFormFieldDisabled(tag)) this.parameterAndFormProtectionOfCurrentForm.addHiddenFieldRemovedValue(extractedFieldNameDecoded, ServerUtils.decodeBrokenValueExceptUrlEncoding(expectedValue));
                                return; // in order to NOT write this hidden form field input tag to the response
                            }
                        }
                    }
                    
                    // watch out for SelectBox form fields - when configured to do so
                    if (this.selectboxProtection) {
                        if (!this.isWithinSelectBox && startsWithSelectOpening && !isFormFieldMaskingExclusion(extractedFieldNameDecoded)) {
                            this.isWithinSelectBox = true;
                            this.nameOfCurrentSelectBox = extractedFieldNameDecoded;
                            if (DEBUG) System.out.println("<select ... with name: "+extractedFieldNameDecoded);
                        }
                    }
                    
                    if (startsWithInputOpening) {
                        // watch out for CheckBox form fields - when configured to do so
                        if (this.checkboxProtection) {
                            if (this.matcherCheckbox == null) this.matcherCheckbox = PATTERN_CHECKBOX.matcher(tag); else this.matcherCheckbox.reset(tag);
                            if (this.matcherCheckbox.find() && !isFormFieldMaskingExclusion(extractedFieldNameDecoded)) {
                                String value = ResponseUtils.extractFieldValue(tag);
                                if (this.checkboxValueMasking) {
                                    // mask the real value with the next index (retrieved from the current ParameterAndFormProtection object)
                                    if (this.checkBoxMaskingPrefix == null) this.checkBoxMaskingPrefix = RequestUtils.createOrRetrieveRandomTokenFromSession(getSession(), WebCastellumFilter.SESSION_CHECKBOX_MASKING_PREFIX, 5,7);
                                    tag = ResponseUtils.setFieldValue(tag, this.checkBoxMaskingPrefix+this.parameterAndFormProtectionOfCurrentForm.getIndexOfNextCheckboxFieldAllowedValue(extractedFieldNameDecoded));
                                }
                                value = ServerUtils.decodeBrokenValueExceptUrlEncoding(value);
                                if (value == null) value = "";
                                this.parameterAndFormProtectionOfCurrentForm.addCheckboxFieldAllowedValue(extractedFieldNameDecoded, value);
                                if (DEBUG) System.out.println("input: checkbox ... with name: "+extractedFieldNameDecoded);
                            }
                        }

                        // watch out for RadioButton form fields - when configured to do so
                        if (this.radiobuttonProtection) {
                            if (this.matcherRadiobutton == null) this.matcherRadiobutton = PATTERN_RADIOBUTTON.matcher(tag); else this.matcherRadiobutton.reset(tag);
                            if (this.matcherRadiobutton.find() && !isFormFieldMaskingExclusion(extractedFieldNameDecoded)) {
                                String value = ResponseUtils.extractFieldValue(tag);
                                if (this.radiobuttonValueMasking) {
                                    // mask the real value with the next index (retrieved from the current ParameterAndFormProtection object)
                                    if (this.radioButtonMaskingPrefix == null) this.radioButtonMaskingPrefix = RequestUtils.createOrRetrieveRandomTokenFromSession(getSession(), WebCastellumFilter.SESSION_RADIOBUTTON_MASKING_PREFIX, 5,7);
                                    tag = ResponseUtils.setFieldValue(tag, this.radioButtonMaskingPrefix+this.parameterAndFormProtectionOfCurrentForm.getIndexOfNextRadiobuttonFieldAllowedValue(extractedFieldNameDecoded));
                                }
                                value = ServerUtils.decodeBrokenValueExceptUrlEncoding(value);
                                if (value == null) value = "";
                                this.parameterAndFormProtectionOfCurrentForm.addRadiobuttonFieldAllowedValue(extractedFieldNameDecoded, value);
                                if (DEBUG) System.out.println("input: radiobutton ... with name: "+extractedFieldNameDecoded);
                            }
                        }
                    }
                    
                    // track form field
                    this.parameterAndFormProtectionOfCurrentForm.addParameterName(extractedFieldNameDecoded, required);

                    // watch out for readonly form fields - when configured to do so (NOTE: when client-side JavaScript changed the readonly-state of some form-fields better disable the applyExtraProtectionForReadonlyFormFields configuration flag)
                    if (this.applyExtraProtectionForReadonlyFormFields) {
                        if (ResponseUtils.isFormFieldReadonly(tag)) {
                            final String expectedValue = ResponseUtils.extractFormFieldValue(tag);
                            this.parameterAndFormProtectionOfCurrentForm.addReadonlyFieldExpectedValue(extractedFieldNameDecoded, ServerUtils.decodeBrokenValueExceptUrlEncoding(expectedValue) );
                        } else this.parameterAndFormProtectionOfCurrentForm.addReadwriteFieldName(extractedFieldNameDecoded);
                    }

                    // count the number of values for each field (when request-param value count protection is active)
                    if (this.applyExtraProtectionForRequestParamValueCount) {
                        if (startsWithTextareaOpening) { // textarea is always submitted, so it counts for minimum and maximum limits with 1
                            this.parameterAndFormProtectionOfCurrentForm.incrementMinimumValueCountForParameterName(extractedFieldNameDecoded, 1);
                            this.parameterAndFormProtectionOfCurrentForm.incrementMaximumValueCountForParameterName(extractedFieldNameDecoded, 1);
                        } else if (startsWithInputOpening) {
                            if (potentiallyRequiredMatch) { // = it is something like type=text or type=password
                                this.parameterAndFormProtectionOfCurrentForm.incrementMinimumValueCountForParameterName(extractedFieldNameDecoded, 1);
                                this.parameterAndFormProtectionOfCurrentForm.incrementMaximumValueCountForParameterName(extractedFieldNameDecoded, 1);
                            } else {
                                // TODO: hier bei type="checkbox" oder type="radio" oder ggf. auch button/submit/reset/hidden, etc. die Werte min und max value count entsprechend diversifiziert inkrementieren... 
                                // fuer checkbox: also minimum nur auf 1 setzen und wenn der name bereits vorhanden ist, nicht weiter inkrementieren 
                                System.err.println("not implemented"); // TODO: not fully implemented in this version (reserved for future featuresets)
                                throw new UnsupportedOperationException("not implemented"); // TODO: not fully implemented in this version (reserved for future featuresets)
                            }
                        } else if (startsWithSelectOpening) {
                            this.parameterAndFormProtectionOfCurrentForm.incrementMinimumValueCountForParameterName(extractedFieldNameDecoded, 1);
                            final boolean isMultipleSelectbox = ResponseUtils.isFormFieldMultiple(tag);
                            if (isMultipleSelectbox) {
                                // TODO: hier bei <select multiple> die anzahl der werte um die anzahl der options statt nur um 1 inkrementieren... (also die options dann per flag merken, dass isWithinSelectMultiple und je <option> opening dann den max-wert inkrementieren... mix-wert bleibt unvraendet
                                System.err.println("not implemented"); // TODO: not fully implemented in this version (reserved for future featuresets)
                                throw new UnsupportedOperationException("not implemented"); // TODO: not fully implemented in this version (reserved for future featuresets)
                            } else {
                                this.parameterAndFormProtectionOfCurrentForm.incrementMaximumValueCountForParameterName(extractedFieldNameDecoded, 1);
                            }
                        }
                    }

                }
            }
        }
        
        
        // selectbox option values
        if (this.selectboxProtection) {
            // on any tag closing or opening in case an option value was being collected (from its display content) can be finished now:
            if (this.isWithinOption) finishOptionDisplayValueCollecting();
            // option values
            if (this.protectParamsAndFormsEnabled && this.isWithinForm && this.parameterAndFormProtectionOfCurrentForm != null
                    && this.isWithinSelectBox && !this.isWithinOption && startsWithOptionOpening) {
                final boolean isDirectlyClosedWithoutMatchingEndTag = tag.endsWith("/>"); // TODO: hiermit werden (das ist das Ziel) <option/> tags getroffen... aber sind denn auch <option/ > tags legal mit nem space zwischen / und > ?!? falls ja muesste dies hier (z.B. mit RegExp) mit geprueft werden...
                this.isWithinOption = !isDirectlyClosedWithoutMatchingEndTag;
                String value = ResponseUtils.extractFieldValue(tag);
                if (this.selectboxValueMasking) {
                    // mask the real option value with the next index (retrieved from the current ParameterAndFormProtection object)
                    if (this.selectBoxMaskingPrefix == null) this.selectBoxMaskingPrefix = RequestUtils.createOrRetrieveRandomTokenFromSession(getSession(), WebCastellumFilter.SESSION_SELECTBOX_MASKING_PREFIX, 5,7);
                    tag = ResponseUtils.setFieldValue(tag, this.selectBoxMaskingPrefix+this.parameterAndFormProtectionOfCurrentForm.getIndexOfNextSelectboxFieldAllowedValue(this.nameOfCurrentSelectBox));
                }
                // special case of directly closed <option/> without value
                if (value == null && isDirectlyClosedWithoutMatchingEndTag) {
                    value = ""; // set value to empty string
                }
                // continue        
                if (value == null) {
                    this.isCollectingDisplayValueAsOptionValue = true;
                    if (DEBUG) System.out.println("\t<option ... without value (collecting the display content as value)");
                } else {
                    value = ServerUtils.decodeBrokenValueExceptUrlEncoding(value);
                    this.parameterAndFormProtectionOfCurrentForm.addSelectboxFieldAllowedValue(this.nameOfCurrentSelectBox, value);
                    if (DEBUG) System.out.println("\t<option ... with value: "+value);
                }
            }
        }

        
        /*
        // determine tag-name
        final String tagNameLowercased;
        {
            final int length = Math.min(10, tag.length());
            final StringBuilder buffer = new StringBuilder(length);
            for (int i=1; i<length; i++) { // yes, start at 1 to omit the always leading <
                final char c = tag.charAt(i);
                if (!Character.isLetter(c)) break;
                buffer.append(c);
            }
            tagNameLowercased = buffer.toString().toLowerCase();
        }*/
        
        
        if (!isWithinScript && !this.isWithinStyle) {
            if (startsWithFormOpening) {
                if (this.isWithinForm) { // = nested <form> tags are not allowed in HTML and therefore filtered-out here
                    return;
                } 
                // make sure NOT to work on external forms to other (partner-)sites
                final String actionUrlFetchedDirectlyFromForm = ResponseUtils.extractActionUrlOfCurrentForm(tag, !(IGNORE_URL_PARAMETERS_ON_FORM_ACTION_WITH_METHOD_GET && !isRequestMethodPOST));
                if (ServerUtils.isInternalHostURL(currentRequestUrlToCompareWith,ServerUtils.decodeBrokenValueHtmlOnly(actionUrlFetchedDirectlyFromForm,false))) {
                    // = a new form starts
                    this.isWithinForm = true;
                    final String extractedFormNameDecoded = ServerUtils.decodeBrokenValueExceptUrlEncoding( ResponseUtils.extractFieldName(tag) );
                    prefilterMatchingFormMaskingExclusions(extractedFormNameDecoded);
                    if ( (this.injectSecretTokensEnabled&&!isRequestMethodPOST) // since only GET forms get their Action-URL-Params ignored by browsers
                        || this.encryptQueryStringsEnabled || this.protectParamsAndFormsEnabled) { // = special form handling where the query-string of the form's action gets removed (and added below as a hidden field on closing of form)
                        this.actionUrlOfCurrentForm = actionUrlFetchedDirectlyFromForm;
                        if (DEBUG) System.out.println("actionUrlOfCurrentForm fetched directly from form: "+actionUrlOfCurrentForm);
                        // in case the action URL is mising or empty, we have to take the original request's URI as form action when full-path-removal is enabled, since otherwise there might be path discrepancies since all paths are flattened during full-path-removal:
                        if ((this.additionalFullResourceRemoval || this.additionalMediumResourceRemoval) && (this.actionUrlOfCurrentForm == null || this.actionUrlOfCurrentForm.length()==0 )) {
                            // for medium path removal we're allowed to use a relative path, otherwise for full path removal we must use an absolute path
                            if (this.additionalMediumResourceRemoval) {
                                final String relativeLink = ServerUtils.extractFileFromURL(this.currentRequestUrlToCompareWith);
                                this.actionUrlOfCurrentForm = relativeLink != null ? relativeLink : this.currentRequestUrlToCompareWith;
                            } else if (this.additionalFullResourceRemoval) {
                                this.actionUrlOfCurrentForm = this.currentRequestUrlToCompareWith;
                            }
                            // make sure that the session-id gets incorporated (this is a must here)
                            if (this.actionUrlOfCurrentForm != null) this.actionUrlOfCurrentForm = this.response.encodeURL(this.actionUrlOfCurrentForm);
                            tag = ResponseUtils.setFieldAction(tag, this.actionUrlOfCurrentForm); // = set form action to the current accessed page
                        }
                        // go on
                        tag = ResponseUtils.removeQueryStringFromActionUrlOfCurrentForm(tag, this.additionalFullResourceRemoval, this.additionalMediumResourceRemoval, this.contextPath, this.response, this.appendQuestionmarkOrAmpersandToLinks, this.appendSessionIdToLinks); // in order to strip away the query-string from the action url
                        this.parameterAndFormProtectionOfCurrentForm = new ParameterAndFormProtection(this.hiddenFormFieldProtection); // = reset + create new
                        this.isCurrentFormRequestMethodPOST = isRequestMethodPOST;
                        /*if (tagNameLowercased.length() >0)*/ tag = applyLinkModifications(tag, /*null,*/ this.prefiltersToCaptureLinksWithinScripts,this.matchersToCaptureLinksWithinScripts,  this.prefiltersToExcludeCompleteScript,this.matchersToExcludeCompleteScript, this.prefiltersToExcludeLinksWithinScripts,this.matchersToExcludeLinksWithinScripts, this.groupNumbersToCaptureLinksWithinScripts /*,null*/); // yes, only scripts
                    } else { // = normal form handling where simply the form's action URL gets eventually slightly adjusted
                        /*if (tagNameLowercased.length() >0) {*/
                            tag = applyLinkModifications(tag, /*null,*/ this.prefiltersToCaptureLinksWithinScripts,this.matchersToCaptureLinksWithinScripts, this.prefiltersToExcludeCompleteScript,this.matchersToExcludeCompleteScript, this.prefiltersToExcludeLinksWithinScripts,this.matchersToExcludeLinksWithinScripts, this.groupNumbersToCaptureLinksWithinScripts /*,null*/);
                            tag = applyLinkModifications(tag, /*tagNameLowercased,*/ this.prefiltersToCaptureLinksWithinTags,this.matchersToCaptureLinksWithinTags, this.prefiltersToExcludeCompleteTag,this.matchersToExcludeCompleteTag, this.prefiltersToExcludeLinksWithinTags,this.matchersToExcludeLinksWithinTags, this.groupNumbersToCaptureLinksWithinTags /*,this.tagNamesToCheck*/);
                        /*}*/
                    }
                }
            } else {
                /*if (tagNameLowercased.length() >0) {*/
                    tag = applyLinkModifications(tag, /*null,*/ this.prefiltersToCaptureLinksWithinScripts,this.matchersToCaptureLinksWithinScripts, this.prefiltersToExcludeCompleteScript,this.matchersToExcludeCompleteScript, this.prefiltersToExcludeLinksWithinScripts,this.matchersToExcludeLinksWithinScripts, this.groupNumbersToCaptureLinksWithinScripts /*,null*/);
                    tag = applyLinkModifications(tag, /*tagNameLowercased,*/ this.prefiltersToCaptureLinksWithinTags,this.matchersToCaptureLinksWithinTags, this.prefiltersToExcludeCompleteTag,this.matchersToExcludeCompleteTag, this.prefiltersToExcludeLinksWithinTags,this.matchersToExcludeLinksWithinTags, this.groupNumbersToCaptureLinksWithinTags /*,this.tagNamesToCheck*/);
                /*}*/
            }
            
            // honeylink stuff
            if (this.honeylinkMaxPerPage > 0 && this.isWithinHtmlBody 
                    && this.honeylinkCount < this.honeylinkMaxPerPage) {
                if (++this.tagPartCounter % this.tagPartCounterTarget == 0) { // using tagPartCounter to speed things up and don't use Random-Check on *every* tag
                    tag = tag + HoneylinkUtils.generateHoneylink(this.honeylinkRandom, this.honeylinkPrefix, this.honeylinkSuffix, this.isWithinHtmlTable);
                    this.honeylinkCount++;
                    this.tagPartCounter = 0;
                    this.tagPartCounterTarget = HoneylinkUtils.nextTagPartCounterTarget(this.honeylinkRandom);
                }
            }
        }

        // watch out for comments when we're inside a script since scripts often have HTML-commented content to be compatible with old browsers
        if (this.isWithinScript && !startsWithScriptOpening) { // = if isWithinScript, but NOT if it is just freshly within script
            this.scriptBody.write(tag);
        } else {
            writeToUnderlyingSink(tag);
        }
    }
    
    
    
    
    //1.5@Override
    public void handleTagClose(final String tag) throws IOException {
        if (DEBUG) System.out.println("handleTagClose: "+tag);
        boolean startsWithScript = false;
        boolean startsWithForm = false;
        boolean startsWithSelectAndProtectionIsActive = false;

        if (USE_DIRECT_ARRAY_LOOKUPS_INSTEAD_OF_STARTS_WITH) {
            if (this.honeylinkMaxPerPage > 0) {
                // direct char position checks are faster than String.startsWith()
                // using direct checks against lower/upper case letter is faster than toLowerCase() for input string
                if (this.isWithinHtmlBody && tag.length() >= 6 
                        && ((tag.charAt(2)=='b'||tag.charAt(2)=='B') && (tag.charAt(3)=='o'||tag.charAt(3)=='O') && (tag.charAt(4)=='d'||tag.charAt(4)=='D') && (tag.charAt(5)=='y'||tag.charAt(5)=='Y')) ) this.isWithinHtmlBody = false;
                if (this.isWithinHtmlTable && tag.length() >= 7
                        && ((tag.charAt(2)=='t'||tag.charAt(2)=='T') && (tag.charAt(3)=='a'||tag.charAt(3)=='A') && (tag.charAt(4)=='b'||tag.charAt(4)=='B') && (tag.charAt(5)=='l'||tag.charAt(5)=='L') && (tag.charAt(6)=='e'||tag.charAt(6)=='E')) ) this.isWithinHtmlTable = false;
            }
            if (this.isWithinStyle && tag.length() >= 7 
                    && ((tag.charAt(2)=='s'||tag.charAt(2)=='S') && (tag.charAt(3)=='t'||tag.charAt(3)=='T') && (tag.charAt(4)=='y'||tag.charAt(4)=='Y') && (tag.charAt(5)=='l'||tag.charAt(5)=='L') && (tag.charAt(6)=='e'||tag.charAt(6)=='E')) ) this.isWithinStyle = false;
            startsWithScript = this.isWithinScript && tag.length() >= 8 
                    && ((tag.charAt(2)=='s'||tag.charAt(2)=='S') && (tag.charAt(3)=='c'||tag.charAt(3)=='C') && (tag.charAt(4)=='r'||tag.charAt(4)=='R') && (tag.charAt(5)=='i'||tag.charAt(5)=='I') && (tag.charAt(6)=='p'||tag.charAt(6)=='P') && (tag.charAt(7)=='t'||tag.charAt(7)=='T'));
            startsWithForm = this.isWithinForm && tag.length() >= 6 
                    && ((tag.charAt(2)=='f'||tag.charAt(2)=='F') && (tag.charAt(3)=='o'||tag.charAt(3)=='O') && (tag.charAt(4)=='r'||tag.charAt(4)=='R') && (tag.charAt(5)=='m'||tag.charAt(5)=='M'));
            startsWithSelectAndProtectionIsActive = this.selectboxProtection && this.isWithinSelectBox && tag.length() >= 8 
                    && ((tag.charAt(2)=='s'||tag.charAt(2)=='S') && (tag.charAt(3)=='e'||tag.charAt(3)=='E') && (tag.charAt(4)=='l'||tag.charAt(4)=='L') && (tag.charAt(5)=='e'||tag.charAt(5)=='E') && (tag.charAt(6)=='c'||tag.charAt(6)=='C') && (tag.charAt(7)=='t'||tag.charAt(7)=='T'));
        } else {
            final String firstFewLower = ResponseUtils.firstTenCharactersLower(tag);
            if (this.honeylinkMaxPerPage > 0) {
                if (this.isWithinHtmlBody && firstFewLower.startsWith("</body")) this.isWithinHtmlBody = false;
                if (this.isWithinHtmlTable && firstFewLower.startsWith("</table")) this.isWithinHtmlTable = false;
            }
            if (this.isWithinStyle && firstFewLower.startsWith("</style")) this.isWithinStyle = false;
            startsWithScript = this.isWithinScript && firstFewLower.startsWith("</script");
            startsWithForm = this.isWithinForm && firstFewLower.startsWith("</form");
            startsWithSelectAndProtectionIsActive = this.selectboxProtection && this.isWithinSelectBox && firstFewLower.startsWith("</select");
        }

        if (startsWithScript) {
            this.isWithinScript = false;
            writeScriptBodyWithLinksAdjusted();
            this.scriptBody.reset();
        } else if (startsWithForm) {
            if (DEBUG) System.out.println("startsWithForm");
            if (this.actionUrlOfCurrentForm != null) {
                if (DEBUG) System.out.println("actionUrlOfCurrentForm != null : "+actionUrlOfCurrentForm);
                if (!this.encryptQueryStringsEnabled || !ResponseUtils.isAlreadyEncrypted(this.cryptoDetectionString,this.actionUrlOfCurrentForm)) { // = only inject tokens when either encryption is disabled or has not taken place, when for example response.encodeURL already caught that URL
                    if (this.injectSecretTokensEnabled) { // this.protectParamsAndFormsEnabled bedingt this.injectSecretTokensEnabled automatisch
                        final String urlDecoded = ServerUtils.decodeBrokenValueHtmlOnly(this.actionUrlOfCurrentForm, false);
                        if (!ServerUtils.startsWithJavaScriptOrMailto(urlDecoded)) {
                            this.actionUrlOfCurrentForm = urlDecoded;
                            // take the action query string removed from the top and inject/encrypt all relevant stuff there
                            this.actionUrlOfCurrentForm = ResponseUtils.injectParameterIntoURL(this.actionUrlOfCurrentForm, this.tokenKey, this.tokenValue, this.maskAmpersandsInModifiedLinks, this.appendQuestionmarkOrAmpersandToLinks, true);
                            if (this.protectParamsAndFormsEnabled && this.parameterAndFormProtectionOfCurrentForm != null) { // =======================
                                final String parameterAndFormProtectionValue = ResponseUtils.getKeyForParameterAndFormProtection(this.actionUrlOfCurrentForm, this.parameterAndFormProtectionOfCurrentForm, getSession(), this.reuseSessionContent, this.applySetAfterWrite);
                                this.actionUrlOfCurrentForm = ResponseUtils.injectParameterIntoURL(this.actionUrlOfCurrentForm, this.protectionTokenKeyKey, parameterAndFormProtectionValue, this.maskAmpersandsInModifiedLinks, this.appendQuestionmarkOrAmpersandToLinks, true);
                            }
                            this.actionUrlOfCurrentForm = ServerUtils.encodeHtmlSafe(this.actionUrlOfCurrentForm); // TODO: hier noetig ?
                            //System.out.println("xxx1 this.actionUrlOfCurrentForm="+this.actionUrlOfCurrentForm);
                            //System.out.println("xxx2 this.encryptQueryStringsEnabled="+this.encryptQueryStringsEnabled);
                            if (this.encryptQueryStringsEnabled) { // ======================= NOTE: When this.protectParamsAndFormsEnabled is activated, this.encryptQueryStringsEnabled is also true automatically since when ParamsAndForms are protected, encryption is a must
                                if (DEBUG) System.out.println("POST of form ? :"+isCurrentFormRequestMethodPOST);
                                this.actionUrlOfCurrentForm = ResponseUtils.encryptQueryStringInURL(this.currentRequestUrlToCompareWith, this.contextPath, this.servletPath, this.actionUrlOfCurrentForm, true, isMultipartForm, Boolean.valueOf(this.isCurrentFormRequestMethodPOST), this.contentInjectionHelper.isSupposedToBeStaticResource(ResponseUtils.extractURI(this.actionUrlOfCurrentForm)), this.cryptoDetectionString, this.cipher, this.cryptoKey, useFullPathForResourceToBeAccessedProtection, this.additionalFullResourceRemoval, this.additionalMediumResourceRemoval, this.response, this.appendQuestionmarkOrAmpersandToLinks);
                            }
                            //System.out.println("xxx3 this.actionUrlOfCurrentForm="+this.actionUrlOfCurrentForm);
                            if (this.appendSessionIdToLinks && this.actionUrlOfCurrentForm != null) this.actionUrlOfCurrentForm = this.response.encodeURL(this.actionUrlOfCurrentForm);
                        }
                    }
                }
                // forms get their complete protection as a hidden field instead of the action URL, so that it can be added at the end of the form
                final String queryStringOfActionUrl = ResponseUtils.extractQueryStringOfActionUrl(this.actionUrlOfCurrentForm);
                if (queryStringOfActionUrl != null) { // TODO: kann es hier vorkommen, dass es mehrere = in dem querystring gibt? also mehrere params ?
                    final int equalsSignPos = queryStringOfActionUrl.indexOf('=');
                    String queryStringOfActionUrl_beforeEqualsSign = queryStringOfActionUrl.substring( 0, equalsSignPos>-1?equalsSignPos:queryStringOfActionUrl.length() );
                    if (this.appendQuestionmarkOrAmpersandToLinks && queryStringOfActionUrl_beforeEqualsSign.endsWith("&")) queryStringOfActionUrl_beforeEqualsSign = queryStringOfActionUrl_beforeEqualsSign.substring(0,queryStringOfActionUrl_beforeEqualsSign.length()-1);
                    String queryStringOfActionUrl_afterEqualsSign = queryStringOfActionUrl.substring( equalsSignPos>-1&&equalsSignPos<queryStringOfActionUrl.length()-1?equalsSignPos+1:queryStringOfActionUrl.length() );
                    if (this.encryptQueryStringsEnabled && WebCastellumFilter.INTERNAL_TYPE_URL.equals(queryStringOfActionUrl_afterEqualsSign)) queryStringOfActionUrl_afterEqualsSign = WebCastellumFilter.INTERNAL_TYPE_FORM;
                    writeToUnderlyingSink(" <input type=\"hidden\" name=\""+queryStringOfActionUrl_beforeEqualsSign+"\" value=\""+queryStringOfActionUrl_afterEqualsSign+"\" /> ");
                }
            }
            this.isWithinForm = false;
            this.formFieldExclusionsOfCurrentForm.clear();
            this.parameterAndFormProtectionOfCurrentForm = null; // reset
            this.actionUrlOfCurrentForm = null; // reset
        }
        
                    
        // watch out for SelectBox form fields - when configured to do so
        if (this.selectboxProtection) {
            // on any tag closing or opening in case an option value was being collected (from its display content) can be finished now:
            if (this.isWithinOption) finishOptionDisplayValueCollecting();
            if (startsWithSelectAndProtectionIsActive) {
                this.isWithinSelectBox = false;
                this.nameOfCurrentSelectBox = null;
                if (DEBUG) System.out.println("</select");
            }
        }
        
        
        if (this.isWithinScript) {
            this.scriptBody.write(tag);
        } else writeToUnderlyingSink(tag);
    }

    



    //1.5@Override
    public void handlePseudoTagRestart(final char[] stuff) throws IOException {
        if (DEBUG) System.out.println("handlePseudoTagRestart: "+stuff);
        if (this.isWithinScript) {
            this.scriptBody.write(stuff);
        } else {
            writeToUnderlyingSink(stuff, 0, stuff.length);
        }
    }
            


    //1.5@Override
    public void handleText(final int character) throws IOException {
        if (DEBUG) System.out.println("handleText: "+character);
        if (this.isWithinScript) {
            this.scriptBody.write(character);
        } else {
            if (this.selectboxProtection && this.isCollectingDisplayValueAsOptionValue) {
                this.collectedDisplayValue.write(character);
            }
            // continue with writing
            super.handleText(character);
        }
    }
            


    //1.5@Override
    public void handleText(final String text) throws IOException {
        if (DEBUG) System.out.println("handleText: "+text);
        if (this.isWithinScript) {
            this.scriptBody.write(text);
        } else {
            if (this.selectboxProtection && this.isCollectingDisplayValueAsOptionValue) {
                this.collectedDisplayValue.write(text);
            }
            // continue with writing
            super.handleText(text);
        }
    }
        
    
    
    
    
    
    
    private void finishOptionDisplayValueCollecting() {
        this.isWithinOption = false;
        if (this.isCollectingDisplayValueAsOptionValue && this.parameterAndFormProtectionOfCurrentForm != null) {
            String value = this.collectedDisplayValue.toString();
            value = ServerUtils.decodeBrokenValueExceptUrlEncoding(value);
            this.parameterAndFormProtectionOfCurrentForm.addSelectboxFieldAllowedValue(this.nameOfCurrentSelectBox, value);
            if (DEBUG) System.out.println("\t\tcollected display value: "+value);
            this.isCollectingDisplayValueAsOptionValue = false;
            this.collectedDisplayValue.reset();
        }
        if (DEBUG) System.out.println("\t</option");
    }

    

    
    
    // TODO: die Methode hier aus ResponseFilterStream/Writer in Superklasse zentralisieren
    private void prefilterMatchingFormMaskingExclusions(String extractedFormNameDecoded) {
        if (extractedFormNameDecoded == null) extractedFormNameDecoded = ""; // = to enable matching of forms without a name attribute
        // take only those that are matching the current form's name from the array of form-field-masking-exclusions that match the request
        for (FormFieldMaskingExcludeDefinition formFieldExclusion : this.matchingFormFieldMaskingExclusions) {
            if ((formFieldExclusion.getFormNamePrefilter()==null || WordMatchingUtils.matchesWord(formFieldExclusion.getFormNamePrefilter(),extractedFormNameDecoded,WebCastellumFilter.TRIE_MATCHING_THRSHOLD)) 
                    && formFieldExclusion.getFormNamePattern().matcher(extractedFormNameDecoded).find()) this.formFieldExclusionsOfCurrentForm.add(formFieldExclusion);
        }
    }
    // TODO: die Methode hier aus ResponseFilterStream/Writer in Superklasse zentralisieren
    private boolean isFormFieldMaskingExclusion(String extractedFieldNameDecoded) {
        if (extractedFieldNameDecoded == null) extractedFieldNameDecoded = ""; // = to enable matching of fields without a name attribute
        for (final Iterator iter = this.formFieldExclusionsOfCurrentForm.iterator(); iter.hasNext();) {
            FormFieldMaskingExcludeDefinition exclusion = (FormFieldMaskingExcludeDefinition) iter.next();
            if ((exclusion.getFieldNamePrefilter()==null || WordMatchingUtils.matchesWord(exclusion.getFieldNamePrefilter(),extractedFieldNameDecoded,WebCastellumFilter.TRIE_MATCHING_THRSHOLD)) 
                    && exclusion.getFieldNamePattern().matcher(extractedFieldNameDecoded).find()) return true;
        }
        return false;
    }
        
    
    
    
    
    

    
    /**
     * Writes the script-body content from 'this.scriptBody' into the output writer via 'super.out'
     * while replacing all 'location.href's and 'location.replace's to include the proper tokens
     */
    private void writeScriptBodyWithLinksAdjusted() throws IOException {
        if (this.scriptBody != null && this.scriptBody.size() > 0) {
            //out.write(this.scriptBody.toCharArray());
            String script = this.scriptBody.toString();
            this.scriptBody.reset();
            script = applyLinkModifications(script /*,null*/, this.prefiltersToCaptureLinksWithinScripts,this.matchersToCaptureLinksWithinScripts, this.prefiltersToExcludeCompleteScript,this.matchersToExcludeCompleteScript, this.prefiltersToExcludeLinksWithinScripts,this.matchersToExcludeLinksWithinScripts, this.groupNumbersToCaptureLinksWithinScripts /*,null*/); // yes, only scripts - without tagnames check here
            writeToUnderlyingSink(script);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    // TODO: diese methode weg-optimieren durch inlining des loops in der anderen? und damit nicht mehr params uebergeben, welche this. sind (ist doof)
    private String applyLinkModifications(String scriptOrTag, /*final String tagNameLowercased,*/ final WordDictionary[] capturingPrefilters,final Matcher[] capturingMatchers, final WordDictionary[] exclusionPrefiltersComplete,final Matcher[] exclusionMatchersComplete, final WordDictionary[] exclusionPrefiltersWithin,final Matcher[] exclusionMatchersWithin, final int[][] capturingGroupNumbers /*,final List<String>[] tagNamesToCheck*/) {
        if (scriptOrTag == null) return null;
        for (int i=0; i<capturingMatchers.length; i++) {
            /*if (tagNameLowercased == null || tagNamesToCheck[i] == null || tagNamesToCheck[i].isEmpty() || tagNamesToCheck[i].contains(tagNameLowercased)) */
            scriptOrTag = replaceAllLocations(scriptOrTag, capturingPrefilters[i],capturingMatchers[i], exclusionPrefiltersComplete[i],exclusionMatchersComplete[i], exclusionPrefiltersWithin[i],exclusionMatchersWithin[i], capturingGroupNumbers[i]);
        }
        return scriptOrTag;
    }

    
    
//    private String replaceAllLocations(final String scriptOrTag, final Pattern capturingPattern, final Pattern exclusionPatternComplete, final Pattern exclusionPatternWithin, final int capturingGroupNumber) {
    private String replaceAllLocations(final String scriptOrTag, final WordDictionary capturingPrefilter,final Matcher capturingMatcher, final WordDictionary exclusionPrefilterComplete,final Matcher exclusionMatcherComplete, final WordDictionary exclusionPrefilterWithin,final Matcher exclusionMatcherWithin, final int[] capturingGroupNumberAlternatives) {
        if (capturingMatcher == null) return scriptOrTag;
        if (capturingPrefilter != null && !WordMatchingUtils.matchesWord(capturingPrefilter, scriptOrTag, WebCastellumFilter.TRIE_MATCHING_THRSHOLD)) return scriptOrTag;
        if (exclusionMatcherComplete != null && WordMatchingUtils.matchesWord(exclusionPrefilterComplete,scriptOrTag,WebCastellumFilter.TRIE_MATCHING_THRSHOLD) /*&& exclusionMatcherComplete.pattern().pattern().trim().length() > 0*/ && exclusionMatcherComplete.reset(scriptOrTag).find()) return scriptOrTag; // nothing to do, since the complete scriptOrTag matches the exclusion for complete script-or-tag
        final StringBuilder result = new StringBuilder(scriptOrTag.length()+100);
        //final Matcher matcher = 
        capturingMatcher.reset(scriptOrTag);
        //OLD if (capturingMatcher.groupCount() < 1) throw new IllegalArgumentException("Pattern must have an explicitly defined capturing group to identify the URL: "+capturingMatcher.pattern());
        int pos = 0;
        while (capturingMatcher.find()) {
            final String match = capturingMatcher.group();
            if (exclusionMatcherWithin != null && WordMatchingUtils.matchesWord(exclusionPrefilterWithin,match,WebCastellumFilter.TRIE_MATCHING_THRSHOLD) /*&& exclusionMatcherWithin.pattern().pattern().trim().length() > 0*/ && exclusionMatcherWithin.reset(match).find()) continue; // continue with next, since it is an exclusion match
            int capturingGroupNumber; String url; int i=0;
            do {
                capturingGroupNumber = capturingGroupNumberAlternatives[i++];
                url = capturingMatcher.group(capturingGroupNumber);
                if (url != null) {
                    url = url.trim();
                    break;
                }
            } while(capturingGroupNumber>0 && url==null);
            final int start = capturingMatcher.start(capturingGroupNumber);
            if (DEBUG) {
                System.out.println("capturingPattern: "+capturingMatcher.pattern());
                System.out.println("url: "+url);
            }
            if (!ServerUtils.isInternalHostURL(currentRequestUrlToCompareWith,ServerUtils.decodeBrokenValueHtmlOnly(url,false))) continue;
// OLD            if (this.responseProtectionExclusion != null && this.responseProtectionExclusion.matcher(ResponseUtils.extractURI(url)).matches()) continue;
            final String extractedURI = ResponseUtils.extractURI(url);
            if (this.contentInjectionHelper.isMatchingIncomingLinkModificationExclusion(extractedURI)) continue;
            final int end = capturingMatcher.end(capturingGroupNumber);
            result.append( scriptOrTag.substring(pos,start) );
            if (!this.encryptQueryStringsEnabled || !ResponseUtils.isAlreadyEncrypted(this.cryptoDetectionString,url)) { // = only inject tokens when either encryption is disabled or has not taken place, when for example response.encodeURL already caught that URL
                if (this.injectSecretTokensEnabled) { // this.protectParamsAndFormsEnabled bedingt this.injectSecretTokensEnabled automatisch
                    final String urlDecoded = ServerUtils.decodeBrokenValueHtmlOnly(url, false);
                    if (!ServerUtils.startsWithJavaScriptOrMailto(urlDecoded)) {
                        url = urlDecoded;
                        url = ResponseUtils.injectParameterIntoURL(url, this.tokenKey, this.tokenValue, this.maskAmpersandsInModifiedLinks, this.appendQuestionmarkOrAmpersandToLinks, true);
                        if (this.protectParamsAndFormsEnabled  // =======================
                                && !this.contentInjectionHelper.isExtraStrictParameterCheckingForEncryptedLinks()) { // weil bei aktivierter Strictness koennen wir uns die PAF (ParameterAndFormProtection) hier sparen, da es hier immer links und keine forms sind (anders als weiter oben wo es forms sind und daher PAF notwendig ist)
                            final String parameterAndFormProtectionValue = ResponseUtils.getKeyForParameterProtectionOnly(url, getSession(), this.hiddenFormFieldProtection, this.reuseSessionContent, this.applySetAfterWrite);
                            url = ResponseUtils.injectParameterIntoURL(url, this.protectionTokenKeyKey, parameterAndFormProtectionValue, this.maskAmpersandsInModifiedLinks, this.appendQuestionmarkOrAmpersandToLinks, true);
                        }
                        url = ServerUtils.encodeHtmlSafe(url); // TODO: hier noetig ?
                        if (this.encryptQueryStringsEnabled) { // =======================
                            url = ResponseUtils.encryptQueryStringInURL(this.currentRequestUrlToCompareWith, this.contextPath, this.servletPath, url, false, false, null, this.contentInjectionHelper.isSupposedToBeStaticResource(extractedURI), this.cryptoDetectionString, this.cipher, this.cryptoKey, useFullPathForResourceToBeAccessedProtection, this.additionalFullResourceRemoval, this.additionalMediumResourceRemoval, this.response, this.appendQuestionmarkOrAmpersandToLinks); 
                        }
                        if (this.appendSessionIdToLinks && url != null) url = this.response.encodeURL(url);
                    }
                }
            }
            result.append(url);
            pos = end;
        }
        result.append( scriptOrTag.substring(pos) );
        return result.toString();
    }
    
    
    
    
    
    // retrieve the session lazily to grab it as late as possible, so that it is already renewed when a session-renew takes place
    private HttpSession session;
    private HttpSession getSession() {
        if (this.session == null) {
            this.session = this.request.getSession(false);
            if (this.session == null) System.err.println("Strange situation: session is null where it should not be null");
        }
        return this.session;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    // for local testing only ==============================================================
    /* *
    public static final void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: source-file target-file");
            System.exit(-1);
        }
        final File source = new File(args[0]);
        final File target = new File(args[1]);
        System.out.println("Source file: "+source.getAbsoluteFile());
        System.out.println("Target file: "+target.getAbsoluteFile());
        long durationWithout, durationWith;
        durationWithout = test(source, target, false);
        durationWith = test(source, target, true);
        System.out.println("Delta time: "+(durationWith-durationWithout)+" ms");
    }
    private static final long test(final File source, final File target, final boolean applyFilter) throws Exception {
        final boolean stripHtmlEnabled = true;
        final boolean injectSecretTokensEnabled = true;
        final boolean encryptQueryStringsEnabled = true;
        final boolean protectParamsAndFormsEnabled = true;
        final boolean applyExtraProtectionForDisabledFormFields = true;
        final boolean applyExtraProtectionForReadonlyFormFields = false;
        final boolean applyExtraProtectionForRequestParamValueCount = false;
        final HttpSession session = new SessionMock();
        
        final Pattern[] patternsToExcludeLinksWithinScripts = new Pattern[] {
            Pattern.compile(""),
            Pattern.compile(""),
            Pattern.compile("(?i)(?s)location\\s*\\.\\s*href\\s*=\\s*('|\")([^\\+]*?)\\1\\s*\\+"),
            Pattern.compile("")
        };
        final Pattern[] patternsToCaptureLinksWithinScripts = new Pattern[] {
            Pattern.compile("(?i)(?s)\\.\\s*action\\s*=\\s*('|\")([^\\+]*?)\\1"),
            Pattern.compile("(?i)(?s)window\\s*\\.\\s*open\\s*\\(\\s*('|\")([^\\+]*?)\\1"),
            Pattern.compile("(?i)(?s)location\\s*\\.\\s*href\\s*=\\s*('|\")([^\\+]*?)\\1(\\s*\\+)?"),
            Pattern.compile("(?i)(?s)location\\s*\\.\\s*replace\\s*\\(\\s*('|\")([^\\+]*?)\\1")
        }; final int[] groupNumbersToCaptureLinksWithinScripts = new int[]{2,2,2,2};
        
        final Pattern[] patternsToExcludeLinksWithinTags = new Pattern[] {
            Pattern.compile(""),
            Pattern.compile("")
        };
        final Pattern[] patternsToCaptureLinksWithinTags = new Pattern[] {
            Pattern.compile("(?i)(?s)\\s+(?:href|action|src|source)\\s*=\\s*('|\")(.+?)\\1"),
            Pattern.compile("(?i)(?s)style\\s*=\\s*('|\")(?:.*?)behavior\\s*:\\s*url\\s*\\(([^\\)]+)\\)")
        }; final int[] groupNumbersToCaptureLinksWithinTags = new int[]{2,2};
        
        
        // warm up
        final CryptoKeyAndSalt key = CryptoUtils.generateRandomCryptoKeyAndSalt();
        Cipher.getInstance("AES");
        MessageDigest.getInstance("SHA-1");
        
        final long start = System.currentTimeMillis();
        Reader reader = null;
        Writer writer = null;
        try {
            reader = new BufferedReader( new FileReader(source) );
            writer = new FileWriter(target);
            if (applyFilter) writer = new ResponseFilterWriter(writer, "http://127.0.0.1/test/sample", "___SEC-KEY___", "___SEC-VALUE___", "___PROT-KEY___", key, null, "___ENCRYPTED___", session,
            stripHtmlEnabled, injectSecretTokensEnabled, protectParamsAndFormsEnabled, encryptQueryStringsEnabled, 
            applyExtraProtectionForDisabledFormFields, applyExtraProtectionForReadonlyFormFields, applyExtraProtectionForRequestParamValueCount,
            patternsToExcludeLinksWithinScripts, patternsToExcludeLinksWithinTags,
            patternsToCaptureLinksWithinScripts, patternsToCaptureLinksWithinTags,
            groupNumbersToCaptureLinksWithinScripts, groupNumbersToCaptureLinksWithinTags);
            writer = new BufferedWriter(writer);
            char[] chars = new char[8*1024];
            int read;
            while ( (read=reader.read(chars)) != -1 ) {
                if (read > 0) writer.write(chars, 0, read);
            }
            return System.currentTimeMillis()-start;
        } finally {
            if (reader != null) try { reader.close(); } catch(IOException ignored) {}
            if (writer != null) try { writer.close(); } catch(IOException ignored) {}
        }
    }
    public static final class SessionMock implements HttpSession {
        private final Map map = new HashMap();
        public long getCreationTime() {return -1;}
        public String getId() {return "X";}
        public long getLastAccessedTime() {return -1;}
        public ServletContext getServletContext() {return null;}
        public void setMaxInactiveInterval(int i) {}
        public int getMaxInactiveInterval() {return -1;}
        public javax.servlet.http.HttpSessionContext getSessionContext() {return null;}
        public void setAttribute(String string, Object object) {this.map.put(string,object);}
        public void removeAttribute(String string) {this.map.remove(string);}
        public Object getAttribute(String string) {return this.map.get(string);}
        public Enumeration getAttributeNames() {return new Vector(this.map.keySet()).elements();}
        public void invalidate() {this.map.clear();}
        public Object getValue(String string) {return this.map.get(string);}
        public String[] getValueNames() {return (String[])this.map.keySet().toArray(new String[0]);}
        public void putValue(String string, Object object) {this.map.put(string,object);}
        public void removeValue(String string) {this.map.remove(string);}
        public boolean isNew() {return false;}
    }
    /**/    

    
}
