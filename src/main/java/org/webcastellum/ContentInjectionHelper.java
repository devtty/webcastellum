package org.webcastellum;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import javax.crypto.Cipher;
import javax.servlet.ServletOutputStream;



public final class ContentInjectionHelper {
    
    private static final Logger LOGGER = Logger.getLogger(ContentInjectionHelper.class.getName());
    
    private boolean injectSecretTokenIntoLinks;
    private boolean stripHtmlComments;
    private boolean protectParametersAndForms;
    private boolean extraProtectDisabledFormFields;
    private boolean extraProtectReadonlyFormFields;
    private boolean extraProtectRequestParamValueCount;
    private boolean encryptQueryStringInLinks;
    private boolean extraFullPathRemoval;
    private boolean extraMediumPathRemoval;
    private boolean extraStrictParameterCheckingForEncryptedLinks;
    private boolean useTunedBlockParser;
    private boolean useResponseBuffering;
    
    private ContentModificationExcludeDefinitionContainer contentModificationExcludeDefinitions;
    private FormFieldMaskingExcludeDefinitionContainer formFieldMaskingExcludeDefinitions;

    
    
    
    public ContentModificationExcludeDefinitionContainer getContentModificationExcludeDefinitions() {
        return contentModificationExcludeDefinitions;
    }
    public void setContentModificationExcludeDefinitions(ContentModificationExcludeDefinitionContainer contentModificationExcludeDefinitions) {
        this.contentModificationExcludeDefinitions = contentModificationExcludeDefinitions;
    }


    
    public FormFieldMaskingExcludeDefinitionContainer getFormFieldMaskingExcludeDefinitions() {
        return formFieldMaskingExcludeDefinitions;
    }
    public void setFormFieldMaskingExcludeDefinitions(FormFieldMaskingExcludeDefinitionContainer formFieldMaskingExcludeDefinitions) {
        this.formFieldMaskingExcludeDefinitions = formFieldMaskingExcludeDefinitions;
    }

    
   
    

    
    public boolean isStripHtmlComments() {
        return stripHtmlComments;
    }
    public void setStripHtmlComments(boolean stripHtmlComments) {
        this.stripHtmlComments = stripHtmlComments;
    }


    
    
    public boolean isUseTunedBlockParser() {
        return useTunedBlockParser;
    }
    public void setUseTunedBlockParser(boolean useTunedBlockParser) {
        this.useTunedBlockParser = useTunedBlockParser;
    }


    
    public boolean isUseResponseBuffering() {
        return useResponseBuffering;
    }
    public void setUseResponseBuffering(boolean useResponseBuffering) {
        this.useResponseBuffering = useResponseBuffering;
    }
    
    
    
    
    
    public boolean isProtectParametersAndForms() {
        return protectParametersAndForms;
    }
    public void setProtectParametersAndForms(boolean protectParametersAndForms) {
        this.protectParametersAndForms = protectParametersAndForms;
    }
    
    
    
    
    public boolean isExtraProtectDisabledFormFields() {
        return extraProtectDisabledFormFields;
    }
    public void setExtraProtectDisabledFormFields(boolean extraProtectDisabledFormFields) {
        this.extraProtectDisabledFormFields = extraProtectDisabledFormFields;
    }
    
    
    
    public boolean isExtraProtectReadonlyFormFields() {
        return extraProtectReadonlyFormFields;
    }
    public void setExtraProtectReadonlyFormFields(boolean extraProtectReadonlyFormFields) {
        this.extraProtectReadonlyFormFields = extraProtectReadonlyFormFields;
    }
    
    
    
    
    public boolean isExtraProtectRequestParamValueCount() {
        return extraProtectRequestParamValueCount;
    }
    public void setExtraProtectRequestParamValueCount(boolean extraProtectRequestParamValueCount) {
        this.extraProtectRequestParamValueCount = extraProtectRequestParamValueCount;
    }
    
    
    
    
    public boolean isEncryptQueryStringInLinks() {
        return encryptQueryStringInLinks;
    }
    public void setEncryptQueryStringInLinks(boolean encryptQueryStringInLinks) {
        this.encryptQueryStringInLinks = encryptQueryStringInLinks;
    }
   
    
    public boolean isExtraMediumPathRemoval() {
        return extraMediumPathRemoval;
    }
    public void setExtraMediumPathRemoval(boolean extraMediumPathRemoval) {
        this.extraMediumPathRemoval = extraMediumPathRemoval;
    }


    
    
    public boolean isExtraStrictParameterCheckingForEncryptedLinks() {
        return extraStrictParameterCheckingForEncryptedLinks;
    }
    public void setExtraStrictParameterCheckingForEncryptedLinks(boolean extraStrictParameterCheckingForEncryptedLinks) {
        this.extraStrictParameterCheckingForEncryptedLinks = extraStrictParameterCheckingForEncryptedLinks;
    }
    
    
    
    
    
    
    public boolean isExtraFullPathRemoval() {
        return extraFullPathRemoval;
    }
    public void setExtraFullPathRemoval(boolean extraFullPathRemoval) {
        this.extraFullPathRemoval = extraFullPathRemoval;
    }
    
    

    public boolean isInjectSecretTokenIntoLinks() {
        return injectSecretTokenIntoLinks;
    }
    public void setInjectSecretTokenIntoLinks(boolean injectSecretTokenIntoLinks) {
        this.injectSecretTokenIntoLinks = injectSecretTokenIntoLinks;
    }
        
    
    
    
    
    public ServletOutputStream addActivatedFilters(final String responseCharsetEncodingName, final ServletOutputStream output, final String currentRequestUrlToCompareWith, final String contextPath, final String servletPath, final String secretTokenKey, final String secretTokenValue, final String cryptoDetectionString, final Cipher cipher, final CryptoKeyAndSalt cryptoKey, final String protectParametersAndFormsTokenKeyKey,
            final RequestWrapper request, final ResponseWrapper response,
            final WordDictionary[] prefiltersToExcludeCompleteScript, final Matcher[] matchersToExcludeCompleteScript, 
            final WordDictionary[] prefiltersToExcludeCompleteTag, final Matcher[] matchersToExcludeCompleteTag,
            final WordDictionary[] prefiltersToExcludeLinksWithinScripts, final Matcher[] matchersToExcludeLinksWithinScripts, 
            final WordDictionary[] prefiltersToExcludeLinksWithinTags, final Matcher[] matchersToExcludeLinksWithinTags,
            final WordDictionary[] prefiltersToCaptureLinksWithinScripts, final Matcher[] matchersToCaptureLinksWithinScripts, 
            final WordDictionary[] prefiltersToCaptureLinksWithinTags, final Matcher[] matchersToCaptureLinksWithinTags,
            final int[][] groupNumbersToCaptureLinksWithinScripts, 
            final int[][] groupNumbersToCaptureLinksWithinTags, 
            //final List<String>[] tagNamesToCheck,
            final boolean useFullPathForResourceToBeAccessedProtection, final boolean additionalFullResourceRemoval, final boolean additionalMediumResourceRemoval, final boolean maskAmpersandsInModifiedLinks,
            final boolean hiddenFormFieldProtection, final boolean selectboxProtection, final boolean checkboxProtection, final boolean radiobuttonProtection, final boolean selectboxValueMasking, final boolean checkboxValueMasking, final boolean radiobuttonValueMasking,
            final boolean appendQuestionmarkOrAmpersandToLinks, final boolean appendSessionIdToLinks, final boolean reuseSessionContent,
            final String honeylinkPrefix, final String honeylinkSuffix, final short honeylinkMaxPerPage, final boolean randomizeHoneylinksOnEveryRequest, final boolean applySetAfterWrite) {
        if (output == null) return null;
        OutputStream wrapper = null;
    
        if (
              (this.encryptQueryStringInLinks && cryptoDetectionString != null && cryptoKey != null) ||
              (this.protectParametersAndForms && protectParametersAndFormsTokenKeyKey != null) ||
              (this.injectSecretTokenIntoLinks && secretTokenKey != null && secretTokenValue != null) ||
              (this.stripHtmlComments)
            ) {
            wrapper = output;
            wrapper = new ResponseFilterStream(wrapper,responseCharsetEncodingName, this.useTunedBlockParser,
                    currentRequestUrlToCompareWith, contextPath, servletPath, secretTokenKey, secretTokenValue, protectParametersAndFormsTokenKeyKey, cipher, cryptoKey, this, cryptoDetectionString, request, response,
                    this.stripHtmlComments, this.injectSecretTokenIntoLinks, this.protectParametersAndForms, this.encryptQueryStringInLinks, this.extraProtectDisabledFormFields, this.extraProtectReadonlyFormFields, this.extraProtectRequestParamValueCount,
                    prefiltersToExcludeCompleteScript, matchersToExcludeCompleteScript, 
                    prefiltersToExcludeCompleteTag, matchersToExcludeCompleteTag,
                    prefiltersToExcludeLinksWithinScripts, matchersToExcludeLinksWithinScripts, 
                    prefiltersToExcludeLinksWithinTags, matchersToExcludeLinksWithinTags,
                    prefiltersToCaptureLinksWithinScripts, matchersToCaptureLinksWithinScripts, 
                    prefiltersToCaptureLinksWithinTags, matchersToCaptureLinksWithinTags,
                    groupNumbersToCaptureLinksWithinScripts, groupNumbersToCaptureLinksWithinTags, 
                    //tagNamesToCheck,
                    useFullPathForResourceToBeAccessedProtection, additionalFullResourceRemoval, additionalMediumResourceRemoval, maskAmpersandsInModifiedLinks,
                    hiddenFormFieldProtection, selectboxProtection, checkboxProtection, radiobuttonProtection, selectboxValueMasking, checkboxValueMasking, radiobuttonValueMasking,
                    appendQuestionmarkOrAmpersandToLinks, appendSessionIdToLinks, reuseSessionContent,
                    honeylinkPrefix, honeylinkSuffix, honeylinkMaxPerPage, randomizeHoneylinksOnEveryRequest, applySetAfterWrite);
            if (this.useResponseBuffering) wrapper = new BufferedOutputStream(wrapper);
        }
        
        if (wrapper != null) return new ServletOutputStreamAdapter(wrapper); else return output;
    }
    
    
    
    public PrintWriter addActivatedFilters(final PrintWriter writer, final String currentRequestUrlToCompareWith, final String contextPath, final String servletPath, final String secretTokenKey, final String secretTokenValue, final String cryptoDetectionString, final Cipher cipher, final CryptoKeyAndSalt cryptoKey, final String protectParametersAndFormsTokenKeyKey, 
            final RequestWrapper request, final ResponseWrapper response,
            final WordDictionary[] prefiltersToExcludeCompleteScript, final Matcher[] matchersToExcludeCompleteScript, 
            final WordDictionary[] prefiltersToExcludeCompleteTag, final Matcher[] matchersToExcludeCompleteTag,
            final WordDictionary[] prefiltersToExcludeLinksWithinScripts, final Matcher[] matchersToExcludeLinksWithinScripts, 
            final WordDictionary[] prefiltersToExcludeLinksWithinTags, final Matcher[] matchersToExcludeLinksWithinTags,
            final WordDictionary[] prefiltersToCaptureLinksWithinScripts, final Matcher[] matchersToCaptureLinksWithinScripts, 
            final WordDictionary[] prefiltersToCaptureLinksWithinTags, final Matcher[] matchersToCaptureLinksWithinTags,
            final int[][] groupNumbersToCaptureLinksWithinScripts, 
            final int[][] groupNumbersToCaptureLinksWithinTags, 
            //final List<String>[] tagNamesToCheck,
            final boolean useFullPathForResourceToBeAccessedProtection, final boolean additionalFullResourceRemoval, final boolean additionalMediumResourceRemoval, final boolean maskAmpersandsInModifiedLinks,
            final boolean hiddenFormFieldProtection, final boolean selectboxProtection, final boolean checkboxProtection, final boolean radiobuttonProtection, final boolean selectboxValueMasking, final boolean checkboxValueMasking, final boolean radiobuttonValueMasking,
            final boolean appendQuestionmarkOrAmpersandToLinks, final boolean appendSessionIdToLinks, final boolean reuseSessionContent,
            final String honeylinkPrefix, final String honeylinkSuffix, final short honeylinkMaxPerPage, final boolean randomizeHoneylinksOnEveryRequest, final boolean applySetAfterWrite) {
        if (writer == null) return null;
        Writer wrapper = null;

        if (
              (this.encryptQueryStringInLinks && cryptoDetectionString != null && cryptoKey != null) ||
              (this.protectParametersAndForms && protectParametersAndFormsTokenKeyKey != null) ||
              (this.injectSecretTokenIntoLinks && secretTokenKey != null && secretTokenValue != null) ||
              (this.stripHtmlComments)
            ) {
            wrapper = writer;
            wrapper = new ResponseFilterWriter(wrapper, this.useTunedBlockParser,
                    currentRequestUrlToCompareWith, contextPath, servletPath, secretTokenKey, secretTokenValue, protectParametersAndFormsTokenKeyKey, cipher, cryptoKey, this, cryptoDetectionString, request, response,
                    this.stripHtmlComments, this.injectSecretTokenIntoLinks, this.protectParametersAndForms, this.encryptQueryStringInLinks, this.extraProtectDisabledFormFields, this.extraProtectReadonlyFormFields, this.extraProtectRequestParamValueCount,
                    prefiltersToExcludeCompleteScript, matchersToExcludeCompleteScript, 
                    prefiltersToExcludeCompleteTag, matchersToExcludeCompleteTag,
                    prefiltersToExcludeLinksWithinScripts, matchersToExcludeLinksWithinScripts, 
                    prefiltersToExcludeLinksWithinTags, matchersToExcludeLinksWithinTags,
                    prefiltersToCaptureLinksWithinScripts, matchersToCaptureLinksWithinScripts, 
                    prefiltersToCaptureLinksWithinTags, matchersToCaptureLinksWithinTags,
                    groupNumbersToCaptureLinksWithinScripts, groupNumbersToCaptureLinksWithinTags, 
                    //tagNamesToCheck,
                    useFullPathForResourceToBeAccessedProtection, additionalFullResourceRemoval, additionalMediumResourceRemoval, maskAmpersandsInModifiedLinks,
                    hiddenFormFieldProtection, selectboxProtection, checkboxProtection, radiobuttonProtection, selectboxValueMasking, checkboxValueMasking, radiobuttonValueMasking,
                    appendQuestionmarkOrAmpersandToLinks, appendSessionIdToLinks, reuseSessionContent,
                    honeylinkPrefix, honeylinkSuffix, honeylinkMaxPerPage, randomizeHoneylinksOnEveryRequest, applySetAfterWrite);
            if (this.useResponseBuffering) wrapper = new BufferedWriter(wrapper);
        }
            
        if (wrapper != null) return new PrintWriter(wrapper); else return writer;
    }
    
    
    public final boolean isSupposedToBeStaticResource(final String linkTargetUri) {
        // here a second differentiation of using "isMatchingIncomingLinkModificationExclusionEvenWhenFullPathRemovalEnabled" or "isMatchingIncomingLinkModificationExclusion" is NOT necessary and MUST NOT be made... so here we're always using "isMatchingIncomingLinkModificationExclusion" for static content get its crypto-detection-string always nserted at the same position (to allow browser caching of static resources)
        return this.contentModificationExcludeDefinitions != null && this.contentModificationExcludeDefinitions.isMatchingIncomingLinkModificationExclusion(linkTargetUri);
        // TODO: DIese Methode kann entfallen und es kann einfach im Caller isMatchingIncomingLinkModificationExclusion() genommen bzw. das Ergebnis der vorhergehenden isMatchingIncomingLinkModificationExclusion()-Invokierung auf dem Caller wiederverwendet werden, wenn Full-Path-Removal und dessen Browser-Probleme wegfallen ==> TUNING
    }
    
    public final boolean isMatchingIncomingLinkModificationExclusion(final String linkTargetUri) {
        final boolean result = this.contentModificationExcludeDefinitions != null 
                && ( 
                    this.extraFullPathRemoval ? this.contentModificationExcludeDefinitions.isMatchingIncomingLinkModificationExclusionEvenWhenFullPathRemovalEnabled(linkTargetUri) : this.contentModificationExcludeDefinitions.isMatchingIncomingLinkModificationExclusion(linkTargetUri)
                );
        
        LOGGER.log(Level.FINE, "{0}: {1}", new Object[]{linkTargetUri, result});
        
        return result;
    }
    
    public final boolean isMatchingOutgoingResponseModificationExclusion(final String servletPath, final String requestURI) {
        // expect servletPath to be empty, when WebLogic for example handles a static file access (like a .css or .js or .gif or .jpeg or .html file, etc.)
        final boolean result = this.contentModificationExcludeDefinitions != null && this.contentModificationExcludeDefinitions.isMatchingOutgoingResponseModificationExclusion(servletPath, requestURI);
        
        LOGGER.log(Level.FINE, "{0}: {1}", new Object[]{requestURI, result});
        
        return result;
    }
    
}