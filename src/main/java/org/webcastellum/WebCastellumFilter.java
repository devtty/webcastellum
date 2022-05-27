package org.webcastellum;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.crypto.Cipher;
import javax.servlet.*;
import javax.servlet.http.*;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;



public final class WebCastellumFilter implements javax.servlet.Filter {


    
    
    public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
    
    
    
    private static final String PARAM_DEBUG = "Debug";
    private static final String PARAM_SHOW_TIMINGS = "ShowTimings";
    private static final String PARAM_BLOCK_ATTACKING_CLIENTS_THRESHOLD = "BlockAttackingClientsThreshold";
    
    private static final String PARAM_DEV_ATTACK_REPLY_STATUS_CODE_OR_MESSAGE_RESOURCE = "DevelopmentAttackReplyStatusCodeOrMessageResource";
    private static final String PARAM_PROD_ATTACK_REPLY_STATUS_CODE_OR_MESSAGE_RESOURCE = "ProductionAttackReplyStatusCodeOrMessageResource";
    private static final String PARAM_DEV_EXCEPTION_REPLY_STATUS_CODE_OR_MESSAGE_RESOURCE = "DevelopmentExceptionReplyStatusCodeOrMessageResource";
    private static final String PARAM_PROD_EXCEPTION_REPLY_STATUS_CODE_OR_MESSAGE_RESOURCE = "ProductionExceptionReplyStatusCodeOrMessageResource";
    private static final String PARAM_DEV_CONFIG_MISSING_REPLY_STATUS_CODE_OR_MESSAGE_RESOURCE = "DevelopmentConfigurationMissingReplyStatusCodeOrMessageResource";
    private static final String PARAM_PROD_CONFIG_MISSING_REPLY_STATUS_CODE_OR_MESSAGE_RESOURCE = "ProductionConfigurationMissingReplyStatusCodeOrMessageResource";
    
    private static final String PARAM_FLUSH_RESPONSE = "FlushResponse";
    private static final String PARAM_INVALIDATE_SESSION_ON_ATTACK = "InvalidateSessionOnAttack";
    private static final String PARAM_TIE_WEB_SESSION_TO_CLIENT_ADDRESS = "TieWebSessionToClientAddress";
    private static final String PARAM_TIE_WEB_SESSION_TO_HEADER_LIST = "TieWebSessionToHeaderList";
    private static final String PARAM_BLOCK_RESPONSE_HEADERS_WITH_CRLF = "BlockResponseHeadersWithCRLF";
    private static final String PARAM_BLOCK_FUTURE_LAST_MODIFIED_HEADERS = "BlockFutureLastModifiedResponseHeaders";
    private static final String PARAM_BLOCK_INVALID_LAST_MODIFIED_HEADERS = "BlockInvalidLastModifiedResponseHeaders";
    private static final String PARAM_BLOCK_REQUESTS_WITH_UNKNOWN_REFERRER = "BlockRequestsWithUnknownReferrer";
    private static final String PARAM_BLOCK_REQUESTS_WITH_MISSING_REFERRER = "BlockRequestsWithMissingReferrer";
    private static final String PARAM_BLOCK_REQUESTS_WITH_DUPLICATE_HEADERS = "BlockRequestsWithDuplicateHeaders";
    private static final String PARAM_BLOCK_NON_LOCAL_REDIRECTS = "BlockNonLocalRedirects";
    private static final String PARAM_400_OR_404_ATTACK_THRESHOLD = "HttpInvalidRequestOrNotFoundStatusCodeAttackThreshold";
    private static final String PARAM_400_OR_404_ATTACK_THRESHOLD__CLUSTER_AWARE = "HttpInvalidRequestOrNotFoundStatusCodeClusterAware";
    private static final String PARAM_SESSION_CREATION_ATTACK_THRESHOLD = "SessionCreationAttackThreshold";
    private static final String PARAM_SESSION_CREATION_ATTACK_THRESHOLD__CLUSTER_AWARE = "SessionCreationClusterAware";
    
    private static final String PARAM_SECRET_TOKEN_LINK_INJECTION = "SecretTokenLinkInjection";
    private static final String PARAM_ENCRYPT_QUERY_STRINGS = "QueryStringEncryption";
    private static final String PARAM_PARAMETER_AND_FORM_PROTECTION = "ParameterAndFormProtection";
    private static final String PARAM_EXTRA_DISABLED_FORM_FIELD_PROTECTION = "ExtraDisabledFormFieldProtection";
    private static final String PARAM_EXTRA_READONLY_FORM_FIELD_PROTECTION = "ExtraReadonlyFormFieldProtection";
    private static final String PARAM_EXTRA_REQUEST_PARAM_VALUE_COUNT_PROTECTION = "ExtraRequestParamValueCountProtection";
    private static final String PARAM_EXTRA_HIDDEN_FORM_FIELD_PROTECTION = "ExtraHiddenFormFieldProtection";
    private static final String PARAM_EXTRA_SELECTBOX_PROTECTION = "ExtraSelectboxProtection";
    private static final String PARAM_EXTRA_RADIOBUTTON_PROTECTION = "ExtraRadiobuttonProtection";
    private static final String PARAM_EXTRA_CHECKBOX_PROTECTION = "ExtraCheckboxProtection";
    private static final String PARAM_EXTRA_SELECTBOX_VALUE_MASKING = "ExtraSelectboxValueMasking";
    private static final String PARAM_EXTRA_RADIOBUTTON_VALUE_MASKING = "ExtraRadiobuttonValueMasking";
    private static final String PARAM_EXTRA_CHECKBOX_VALUE_MASKING = "ExtraCheckboxValueMasking";
    private static final String PARAM_EXTRA_HASH_PROTECTION = "ExtraEncryptedValueHashProtection";
    private static final String PARAM_EXTRA_FULL_PATH_PROTECTION = "ExtraEncryptedFullPathProtection";
    private static final String PARAM_EXTRA_MEDIUM_PATH_REMOVAL = "ExtraEncryptedMediumPathRemoval";
    private static final String PARAM_EXTRA_FULL_PATH_REMOVAL = "ExtraEncryptedFullPathRemoval";
    private static final String PARAM_EXTRA_STRICT_PARAMETER_CHECKING_FOR_ENCRYPTED_LINKS = "ExtraStrictParameterCheckingForLinks";
    private static final String PARAM_EXTRA_IMAGE_MAP_PARAMETER_EXCLUDE = "ExtraImageMapParameterExclude";
    private static final String PARAM_EXTRA_SESSION_TIMEOUT_HANDLING = "ExtraSessionTimeoutHandling";
    private static final String PARAM_SESSION_TIMEOUT_REDIRECT_PAGE = "SessionTimeoutRedirectPage";
    

    // TODO: legacy safe rename to better "RuleCategoryNameToXxxx" instead of "PathToXxxx"
    private static final String PARAM_PATH_TO_BAD_REQUEST_FILES = "PathToBadRequestFiles";
    private static final String PARAM_PATH_TO_WHITELIST_REQUESTS_FILES = "PathToWhitelistRequestFiles";
    private static final String PARAM_PATH_TO_ENTRY_POINT_FILES = "PathToEntryPointFiles";
    private static final String PARAM_PATH_TO_OPTIMIZATION_HINT_FILES = "PathToOptimizationHintFiles";
    private static final String PARAM_PATH_TO_DOS_LIMIT_FILES = "PathToDenialOfServiceLimitFiles";
    private static final String PARAM_PATH_TO_RENEW_SESSION_AND_TOKEN_POINT_FILES = "PathToRenewSessionAndTokenPointFiles";
    private static final String PARAM_PATH_TO_CAPTCHA_POINT_FILES = "PathToCaptchaPointFiles";
    private static final String PARAM_PATH_TO_INCOMING_PROTECTION_EXCLUDE_FILES = "PathToIncomingProtectionExcludeFiles";
    private static final String PARAM_PATH_TO_RESPONSE_MODIFICATION_FILES = "PathToResponseModificationFiles"; // TODO: rename (legacy-safe) to link-patterns
    private static final String PARAM_PATH_TO_CONTENT_MODIFICATION_EXCLUDE_FILES = "PathToContentModificationExcludeFiles";
    private static final String PARAM_PATH_TO_TOTAL_EXCLUDE_FILES = "PathToTotalExcludeFiles";
    private static final String PARAM_PATH_TO_SIZE_LIMIT_FILES = "PathToSizeLimitFiles";
    private static final String PARAM_PATH_TO_MULTIPART_SIZE_LIMIT_FILES = "PathToMultipartSizeLimitFiles";
    private static final String PARAM_PATH_TO_DECODING_PERMUTATION_FILES = "PathToDecodingPermutationFiles";
    private static final String PARAM_PATH_TO_FORM_FIELD_MASKING_EXCLUDE_FILES = "PathToFormFieldMaskingExcludeFiles";

    // TODO: die Namen der anderen Regeltypen auch als Literale aus dem Code rausloesen und hier als Konstanten definieren...
    static final String RESPONSE_MODIFICATIONS_DEFAULT = "response-modifications";
    static final String MODIFICATION_EXCLUDES_DEFAULT = "content-modification-excludes";
    
    //private static final String PARAM_CLIENT_IP_DETERMINATION = "ClientIpDetermination";
    private static final String PARAM_MASK_AMPERSANDS_IN_LINK_ADDITIONS = "MaskAmpersandsInLinkAdditions";
    private static final String PARAM_STRIP_HTML_COMMENTS = "StripHtmlComments";
    private static final String PARAM_FORCED_SESSION_INVALIDATION_PERIOD_MINUTES = "ForcedSessionInvalidationPeriod";
    private static final String PARAM_RULE_LOADER = "RuleLoader"; private static final String LEGACY_PARAM_RULE_FILE_LOADER = "RuleFileLoader";
    private static final String PARAM_GEO_LOCATOR = "GeoLocator";
    private static final String PARAM_ATTACK_LOGGER = "AttackLogger";
    private static final String PARAM_CAPTCHA_GENERATOR = "CaptchaGenerator";
    private static final String PARAM_PRODUCTION_MODE_CHECKER = "ProductionModeChecker";
    private static final String PARAM_CLIENT_IP_DETERMINATOR = "ClientIpDeterminator";
    private static final String PARAM_MULTIPART_REQUEST_PARSER = "MultipartRequestParser";
    private static final String PARAM_BLOCK_ATTACKING_CLIENTS_DURATION = "BlockAttackingClientsDuration";
    private static final String PARAM_RESET_PERIOD_ATTACK = "ResetPeriodAttack";
    private static final String PARAM_RESET_PERIOD_SESSION_CREATION = "ResetPeriodSessionCreation";
    private static final String PARAM_RESET_PERIOD_BAD_RESPONSE_CODE = "ResetPeriodBadResponseCode";
    private static final String PARAM_RESET_PERIOD_REDIRECT_THRESHOLD = "ResetPeriodRedirectThreshold"; // TODO: hier der Einheitlichkeit halber nur "ResetPeriodRedirect" nehmen und das Threshold wegfallen lassen und nur als legacy konfig lesen... oder bei allen anderen ein Threshold auch dran
    private static final String PARAM_HOUSEKEEPING_INTERVAL = "HousekeepingInterval";
    private static final String PARAM_BLOCK_INVALID_ENCODED_QUERY_STRING = "BlockInvalidEncodedQueryString";
    private static final String PARAM_APPLICATION_NAME = "ApplicationName";
    private static final String PARAM_LEARNING_MODE_AGGREGATION_DIRECTORY = "LearningModeAggregationDirectory";
    private static final String PARAM_LOG_SESSION_VALUES_ON_ATTACK = "LogSessionValuesOnAttack";
    private static final String PARAM_RULE_RELOADING_INTERVAL = "RuleReloadingInterval"; private static final String LEGACY_PARAM_RULE_FILE_RELOADING_INTERVAL = "RuleFileReloadingInterval";
    private static final String PARAM_CONFIG_RELOADING_INTERVAL = "ConfigurationReinitializationInterval";
    private static final String PARAM_ANTI_CACHE_RESPONSE_HEADER_INJECTION_CONTENT_TYPES = "AntiCacheResponseHeaderInjectionContentTypes";
    private static final String PARAM_RESPONSE_MODIFICATION_CONTENT_TYPES = "ResponseBodyModificationContentTypes";
    private static final String PARAM_FORCE_ENTRANCE_THROUGH_ENTRY_POINTS = "ForceEntranceThroughEntryPoints";
    private static final String PARAM_REDIRECT_WELCOME_PAGE = "RedirectWelcomePage";
    private static final String PARAM_CHARACTER_ENCODING = "CharacterEncoding"; private static final String LEGACY_PARAM_CHARACTER_ENCODING = "RequestCharacterEncoding";
    private static final String PARAM_HANDLE_UNCAUGHT_EXCEPTIONS = "HandleUncaughtExceptions";
    private static final String PARAM_LOG_VERBOSE_FOR_DEVELOPMENT_MODE = "LogVerboseForDevelopmentMode";
    private static final String PARAM_BLOCK_REPEATED_REDIRECTS_THRESHOLD = "BlockRepeatedRedirectsThreshold"; // TODO hier RedirectThreshold ohne Plural-s besser und das andere als legacy
    private static final String PARAM_REMOVE_SENSITIVE_DATA_REQUEST_PARAM_NAME_PATTERN = "RemoveSensitiveDataRequestParamNamePattern";
    private static final String PARAM_REMOVE_SENSITIVE_DATA_VALUE_PATTERN = "RemoveSensitiveDataValuePattern";
    private static final String PARAM_TREAT_NON_MATCHING_SERVLET_PATH_AS_MATCH_FOR_WHITELIST_RULES = "TreatNonMatchingServletPathAsMatchForWhitelistRules";
    private static final String PARAM_REMEMBER_LAST_CAPTCHA_FOR_MULTI_SUBMITS = "RememberLastCaptchaForMultiSubmits";
    private static final String PARAM_LOG_CLIENT_USER_DATA = "LogClientUserData";
    private static final String PARAM_APPEND_QUESTIONMARK_OR_AMPERSAND_TO_LINKS = "AppendQuestionmarkOrAmpersandToLinks";
    private static final String PARAM_APPEND_SESSIONID_TO_LINKS = "AppendSessionIdToLinks";
    private static final String PARAM_FAILED_CAPTCHA_PER_SESSION_ATTACK_THRESHOLD = "FailedCaptchaPerSessionAttackThreshold";
    private static final String PARAM_CLUSTER_INITIAL_CONTEXT_FACTORY = "ClusterInitialContextFactory";
    private static final String PARAM_CLUSTER_BROADCAST_PERIOD = "ClusterBroadcastPeriod";
    private static final String PARAM_CLUSTER_JMS_PROVIDER_URL = "ClusterJmsProviderUrl";
    private static final String PARAM_CLUSTER_JMS_CONNECTION_FACTORY = "ClusterJmsConnectionFactory";
    private static final String PARAM_CLUSTER_JMS_TOPIC = "ClusterJmsTopic";
    private static final String PARAM_REUSE_SESSION_CONTENT = "ReuseSessionContent";
    private static final String PARAM_PARSE_MULTI_PART_FORMS = "InspectMultipartFormSubmits"; // for forms that have file uploads.... also useful to prvent attackers from changing form enctype to multipart/form-data and then circumventing servlet filters
    private static final String PARAM_PRESENT_MULTIPART_FORM_PARAMS_AS_REGULAR_PARAMS_TO_APPLICATION = "PresentMultipartFormParametersAsRegularParametersToApplication";
    private static final String PARAM_HIDE_INTERNAL_SESSION_ATTRIBUTES = "HideInternalSessionAttributes";
    private static final String PARAM_HONEYLINK_PREFIX = "HoneylinkPrefix";
    private static final String PARAM_HONEYLINK_SUFFIX = "HoneylinkSuffix";
    private static final String PARAM_HONEYLINK_MAX_PER_RESPONSE = "HoneylinkMaxPerResponse";
    private static final String PARAM_RANDOMIZE_HONEYLINKS_ON_EVERY_RESPONSE = "HoneylinkRandomizeOnEveryResponse";
    private static final String PARAM_PDF_XSS_PROTECTION = "PdfXssProtection";
    private static final String PARAM_BLOCK_MULTIPART_REQUESTS_FOR_NON_MULTIPART_FORMS = "BlockMultipartRequestsForNonMultipartForms";
    private static final String PARAM_ALLOWED_REQUEST_MIME_TYPES = "AllowedRequestMimeTypes";

    private static final String PARAM_BUFFER_FILE_UPLOADS_TO_DISK = "BufferFileUploadsToDisk";
    private static final String PARAM_APPLY_SET_AFTER_SESSION_WRITE = "ApplySetAfterSessionWrite";

    private static final String PARAM_VALIDATE_CLIENT_ADDRESS_FORMAT = "ValidateClientAddressFormat";

    private static final String PARAM_TRANSPARENT_QUERYSTRING = "TransparentQueryString"; private static final String LEGACY_PARAM_TRANSPARENT_QUERYSTRING = "TransparentQuerystring";
    private static final String PARAM_TRANSPARENT_FORWARDING = "TransparentForwarding";



    // tuning configs
    private static final String PARAM_USE_TUNED_BLOCK_PARSER = "UseTunedBlockParser";
    private static final String PARAM_USE_RESPONSE_BUFFERING = "UseResponseBuffering";
    
    
    
    
    
    
//    static final String INTERNAL_CONTENT_PREFIX = WebCastellumFilter.class.getName()+"_";
    static final String INTERNAL_CONTENT_PREFIX = "WC_";
    
    static final String SESSION_CLIENT_ADDRESS_KEY = INTERNAL_CONTENT_PREFIX+/*PARAM_TIE_WEB_SESSION_TO_CLIENT_ADDRESS*/1; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String SESSION_CLIENT_HEADERS_KEY = INTERNAL_CONTENT_PREFIX+/*PARAM_TIE_WEB_SESSION_TO_HEADER_LIST*/2; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String SESSION_SECRET_RANDOM_TOKEN_KEY_KEY = INTERNAL_CONTENT_PREFIX+/*PARAM_SECRET_TOKEN_LINK_INJECTION*/3+"-K"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String SESSION_SECRET_RANDOM_TOKEN_VALUE_KEY = INTERNAL_CONTENT_PREFIX+/*PARAM_SECRET_TOKEN_LINK_INJECTION*/4+"-V"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String SESSION_PARAMETER_AND_FORM_PROTECTION_RANDOM_TOKEN_KEY_KEY = INTERNAL_CONTENT_PREFIX+/*PARAM_PARAMETER_AND_FORM_PROTECTION*/5; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String SESSION_ENCRYPT_QUERY_STRINGS_CRYPTODETECTION_KEY = INTERNAL_CONTENT_PREFIX+/*PARAM_ENCRYPT_QUERY_STRINGS*/6+"-CD"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String SESSION_ENCRYPT_QUERY_STRINGS_CRYPTOKEY_KEY = INTERNAL_CONTENT_PREFIX+/*PARAM_ENCRYPT_QUERY_STRINGS*/7+"-CK"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String SESSION_ENTRY_POINT_TOUCHED_KEY = INTERNAL_CONTENT_PREFIX+/*PARAM_FORCE_ENTRANCE_THROUGH_ENTRY_POINTS*/8+"-TD"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String SESSION_REUSABLE_KEY_LIST_KEY = INTERNAL_CONTENT_PREFIX+"SRKLK"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String SESSION_CAPTCHA_IMAGES = INTERNAL_CONTENT_PREFIX+"SCI-"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String SESSION_CAPTCHA_FAILED_COUNTER = INTERNAL_CONTENT_PREFIX+"SCFC-"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String SESSION_SELECTBOX_MASKING_PREFIX = INTERNAL_CONTENT_PREFIX+"SSMP-"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String SESSION_CHECKBOX_MASKING_PREFIX = INTERNAL_CONTENT_PREFIX+"SCMP-"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String SESSION_RADIOBUTTON_MASKING_PREFIX = INTERNAL_CONTENT_PREFIX+"SRMP-"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String SESSION_SESSION_WRAPPER_REFERENCE = INTERNAL_CONTENT_PREFIX+"SSWR-"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String REQUEST_NESTED_FORWARD_CALL = INTERNAL_CONTENT_PREFIX+"NF"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();

    static final String REQUEST_ALREADY_DECRYPTED_FLAG = INTERNAL_CONTENT_PREFIX+"ALD"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String REQUEST_IS_FORM_SUBMIT_FLAG = INTERNAL_CONTENT_PREFIX+"FSF"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String REQUEST_IS_URL_MANIPULATED_FLAG = INTERNAL_CONTENT_PREFIX+"IUM"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    
    
    
    static final char INTERNAL_URL_DELIMITER = '$'; // just something that is not part of regular URLs (the $ is reserved character and must be encoded in URIs itself and therefore safe to use)
    static final char INTERNAL_METHOD_TYPE_POST = '6'; // just something meaningless but unique
    static final char INTERNAL_METHOD_TYPE_GET = '3'; // just something meaningless but unique
    static final char INTERNAL_METHOD_TYPE_UNDEFINED = '4'; // just something meaningless but unique
    
    // must be String for equals check
    static final String INTERNAL_TYPE_URL = "0"; // just something meaningless but unique
    static final String INTERNAL_TYPE_FORM = "1"; // just something meaningless but unique
    
    // here as chars for something other
    static final char INTERNAL_TYPE_LINK_FLAG = '9'; // just something meaningless but unique
    static final char INTERNAL_TYPE_FORM_FLAG = '7'; // just something meaningless but unique

    // here as chars for something other
    static final char INTERNAL_MULTIPART_YES_FLAG = '2'; // just something meaningless but unique
    static final char INTERNAL_MULTIPART_NO_FLAG = '5'; // just something meaningless but unique

    // here as chars for something other
   static final char INTERNAL_RESOURCE_ENDS_WITH_SLASH_YES_FLAG = 'S'; // just something meaningless but unique
    static final char INTERNAL_RESOURCE_ENDS_WITH_SLASH_NO_FLAG = 'J'; // just something meaningless but unique

    static final String CAPTCHA_IMAGE = "CI"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String CAPTCHA_FORM = "CF"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String CAPTCHA_VALUE = "CV"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    static final String LAST_CAPTCHA = "_LC"; //NOT RANDOM HERE TO ALLOW SHARED SESSIONS ACROSS APPS +CryptoUtils.generateRandomToken();
    
    static final int STATIC_REQUEST_CRYPTODETECTION_INSERTION_POSITION = CryptoUtils.generateRandomNumber(false, 0,150);
    
    
    
    private static boolean isOldJavaEE13 = false;
    
    
    
    
    
    
    
    private final ContentInjectionHelper contentInjectionHelper = new ContentInjectionHelper();
    
    /**
     * The filter configuration object we are associated with.
     * If this value is null, this filter instance is not currently
     * configured.
     */
    private FilterConfig filterConfig;
    
    // TODO: auch hier die anderen dependent objects (AttackHandler, etc.) wie den ContentInjectionHelper final machen und bereits hier in der Deklaration instantiieren und im Config-Laden lediglich parametrisieren per Settern + dann ueberall wo die instanzen reingereicht werden diese non-nullable machen per NPE
    private AttackHandler attackHandler;
    private String developmentAttackReplyMessage, productionAttackReplyMessage, developmentConfigurationMissingReplyMessage, productionConfigurationMissingReplyMessage, developmentExceptionReplyMessage, productionExceptionReplyMessage, redirectWelcomePage, sessionTimeoutRedirectPage, requestCharacterEncoding;
    private int developmentAttackReplyStatusCode=200, productionAttackReplyStatusCode=200, developmentConfigurationMissingReplyStatusCode=503, productionConfigurationMissingReplyStatusCode=503, developmentExceptionReplyStatusCode=503, productionExceptionReplyStatusCode=503, forcedSessionInvalidationPeriodMinutes, housekeepingIntervalMinutes, blockPeriodMinutes;
    private long ruleFileReloadingIntervalMillis, nextRuleReloadingTime;
    private long configReloadingIntervalMillis, nextConfigReloadingTime;
    private int resetPeriodMinutesAttack, resetPeriodMinutesSessionCreation, resetPeriodMinutesBadResponseCode, resetPeriodMinutesRedirectThreshold;
    private boolean debug, showTimings, catchAll, /*redefineSecretTokensOnSessionRenew, redefineParamAndFormTokensOnSessionRenew, redefineCryptoKeysOnSessionRenew,*/ forceEntranceThroughEntryPoints, tieSessionToClientAddress, blockResponseHeadersWithCRLF, blockFutureLastModifiedHeaders, blockInvalidLastModifiedHeaders, blockRequestsWithUnknownReferrer,blockRequestsWithMissingReferrer, blockRequestsWithDuplicateHeaders, blockNonLocalRedirects, blockInvalidEncodedQueryString, /*OLD isInsaneDecodingActivated,*/ useFullPathForResourceToBeAccessedProtection, additionalFullResourceRemoval, additionalMediumResourceRemoval, maskAmpersandsInLinkAdditions, 
            hiddenFormFieldProtection, selectboxProtection, checkboxProtection, radiobuttonProtection, selectboxValueMasking, checkboxValueMasking, radiobuttonValueMasking, reuseSessionContent, parseMultipartForms, hideInternalSessionAttributes,imageMapParameterExclude,
            bufferFileUploadsToDisk, extraSessionTimeoutHandling;
    private String[] tieSessionToHeaderList;
    private Set/*<String>*/ antiCacheResponseHeaderInjectionContentTypes, responseBodyModificationContentTypes;
    private HttpStatusCodeTracker httpStatusCodeCounter;
    private SessionCreationTracker sessionCreationCounter;
    private DenialOfServiceLimitTracker denialOfServiceLimitCounter;
    private GeoLocatingCache geoLocatingCache;
    private CaptchaGenerator captchaGenerator;
    private ClientIpDeterminator clientIpDeterminator;
    private MultipartRequestParser multipartRequestParser;
    private String /*clientIpDeterminationHeader,*/ /*encodingOfModifiedResponses, *//*attackLogDirectory,*/ applicationName, learningModeAggregationDirectory, clusterInitialContextFactory, clusterJmsProviderUrl, clusterJmsConnectionFactory, clusterJmsTopic;
    private Class ruleFileLoaderClass, productionModeCheckerClass, clientIpDeterminatorClass, multipartRequestParserClass;
    private boolean isProductionMode, logSessionValuesOnAttack, invalidateSessionOnAttack, logVerboseForDevelopmentMode, extraEncryptedValueHashProtection, rememberLastCaptchaForMultiSubmits, appendQuestionmarkOrAmpersandToLinks, appendSessionIdToLinks,  jmsUsed=false, flushResponse=true;
    private boolean presentMultipartFormParametersAsRegularParametersToApplication, blockMultipartRequestsForNonMultipartForms, /*blockAllMultipartRequests,*/ pdfXssProtection, applySetAfterWrite;
    private int blockRepeatedRedirectsThreshold, failedCaptchaPerSessionAttackThreshold, clusterBroadcastPeriod;
    private Pattern removeSensitiveDataRequestParamNamePattern, removeSensitiveDataValuePattern;
    private String honeylinkPrefix, honeylinkSuffix;
    private short honeylinkMaxPerPage;
    private boolean randomizeHoneylinksOnEveryRequest;
    private Set/*<String>*/ allowedRequestMimeTypesLowerCased = new HashSet();
    

    private boolean isHavingEnabledQueryStringCheckingRules = false, isHavingEnabledRequestParameterCheckingRules = false, isHavingEnabledHeaderCheckingRules = false, isHavingEnabledCookieCheckingRules = false;
    private boolean validateClientAddressFormat = false;
    private boolean transparentQuerystring = true, transparentForwarding = true;


    private WhitelistRequestDefinitionContainer whiteListDefinitions; private boolean treatNonMatchingServletPathAsMatchForWhitelistRules;
    private BadRequestDefinitionContainer badRequestDefinitions;
    private DenialOfServiceLimitDefinitionContainer denialOfServiceLimitDefinitions;
    private EntryPointDefinitionContainer entryPointDefinitions;
    private OptimizationHintDefinitionContainer optimizationHintDefinitions;
    private RenewSessionAndTokenPointDefinitionContainer renewSessionPointDefinitions;
    private CaptchaPointDefinitionContainer captchaPointDefinitions;
    private IncomingProtectionExcludeDefinitionContainer incomingProtectionExcludeDefinitions;
    private ResponseModificationDefinitionContainer responseModificationDefinitions;
    
    private TotalExcludeDefinitionContainer totalExcludeDefinitions;
    private ContentModificationExcludeDefinitionContainer contentModificationExcludeDefinitions;
    private SizeLimitDefinitionContainer sizeLimitDefinitions;
    private MultipartSizeLimitDefinitionContainer multipartSizeLimitDefinitions;
    private DecodingPermutationDefinitionContainer decodingPermutationDefinitions;
    private FormFieldMaskingExcludeDefinitionContainer formFieldMaskingExcludeDefinitions;



    
    /*
    private Timer reloadRulesTimer;
    private TimerTask reloadRulesTask;
    */
    
    private boolean restartCompletelyOnNextRequest = true; // initially true to load config on init()
    private boolean reloadRulesOnNextRequest = false; // initially false (handled during config loading on init automatically)
    
    
    
    public WebCastellumFilter() {
        System.out.println( Version.tagLine() );
    }

    
    
    // TODO: in Utility-Klasse packen (ServerUtils)
    private static void concatenateParameterMaps(final Map/*<String,String[]>*/ parameters, final Map/*<String,String[]>*/ parameterMapToAdd) {
        for (final Iterator entries = parameterMapToAdd.entrySet().iterator(); entries.hasNext();) {
            final Map.Entry/*<String,String[]>*/ entry = (Map.Entry) entries.next();
            final String key = (String) entry.getKey();
            String[] value = (String[]) entry.getValue();
            if (parameters.containsKey(key)) {
                // append
                final String[] originalValues = (String[]) parameters.get(key);
                value = ServerUtils.concatenateArrays(value, originalValues); // use the values first, an let the original (submitted) values be appended !
            } 
            parameters.put(key, value);
        }
    }
    

    
    
    
    /**
     * @return success flag
     */
    private AllowedFlagWithMessage doBeforeProcessing(final RequestWrapper request, final ResponseWrapper response, final RequestDetails requestDetails, final String cryptoDetectionString, final Boolean isDecryptedFormSubmit, final Boolean wasEncryptedUrlManipulated) throws IOException, ServletException, StopFilterProcessingException, NoSuchAlgorithmException {
        if (this.debug) logLocal("WebCastellum:doBeforeProcessing ================================== begin");

        if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) {
            Thread.dumpStack();
        }      
      
      
        // Check for invalid encoding of query string
        // Further details on URI encoding stuff: http://en.wikipedia.org/wiki/Percent-encoding
        // =========================================================
        if (this.blockInvalidEncodedQueryString && requestDetails.queryString != null) { // if defined to block wrong encodings, do so:
            try {
                URLDecoder.decode(requestDetails.queryString, DEFAULT_CHARACTER_ENCODING);
            } catch (UnsupportedEncodingException e) { // = wrong configuration
                final StopFilterProcessingException ex = new StopFilterProcessingException("Unsupported request character encoding in WebCastellum: "+DEFAULT_CHARACTER_ENCODING);
                sendUnavailableMessage((HttpServletResponse)response, ex);
                throw ex;
            } catch (IllegalArgumentException e) {
                // The query string contains invalid encoded data (i.e. %XV which is not hex)
                final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Invalid encoded query string");
                return new AllowedFlagWithMessage(false, attack);
            } catch (RuntimeException e) {
                // The query string contains invalid encoded data (i.e. %XV which is not hex)
                final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Invalid encoded query string");
                return new AllowedFlagWithMessage(false, attack);
            }
        }

        // =========================================================
        // Determine if some special points are accessed (entry-points and incomding-protection-excludes, ...)
        // =========================================================
        boolean isEntryPoint,
                isIncomingReferrerProtectionExclude, isIncomingSecretTokenProtectionExclude, isIncomingParameterAndFormProtectionExclude, 
                isIncomingSelectboxFieldProtectionExclude, isIncomingCheckboxFieldProtectionExclude, isIncomingRadiobuttonFieldProtectionExclude, 
                isIncomingForceEntranceProtectionExclude, isIncomingSessionToHeaderBindingProtectionExclude,isExcludeExtraSessionTimeoutHandling,isIncomingExtraSessionTimeoutHandlingExclude;
        try {
            isEntryPoint = this.entryPointDefinitions.isEntryPoint(request,
                        requestDetails.servletPath, requestDetails.contextPath, requestDetails.pathInfo, requestDetails.pathTranslated, requestDetails.clientAddress, requestDetails.remoteHost, requestDetails.remotePort,
                        requestDetails.remoteUser, requestDetails.authType, requestDetails.scheme, requestDetails.method, requestDetails.protocol, requestDetails.mimeType, requestDetails.encoding, requestDetails.contentLength,
                        requestDetails.headerMapVariants, requestDetails.url, requestDetails.uri, requestDetails.serverName, requestDetails.serverPort, requestDetails.localAddr, requestDetails.localName, requestDetails.localPort, requestDetails.country,
                        requestDetails.cookieMapVariants, requestDetails.requestedSessionId, requestDetails.queryStringVariants, 
                        requestDetails.requestParameterMapVariants, requestDetails.requestParameterMap);

            // TODO: die isIncomingXxxxxProtectionExclude Flags nur dann ermitteln lassen, wenn deren exclude ueberhaupt Beachtung findet... 
            // ...Sprich wenn ich z.B. keine Secret-Tokens aktiviert habe in der Config ist ein Exclude oder Nicht-Exclude ohnehin voll egal... 
            // ...Sprich nur bei aktivierten Secret-Tokens auch checken, ob es ein Secret-Token Exclude ist... gleiches gilt fuer die anderen Typen von Excludes...
            final IncomingProtectionExcludeDefinition incomingProtectionExcludeDefinition = this.incomingProtectionExcludeDefinitions.getMatchingIncomingProtectionExcludeDefinition(request,
                        requestDetails.servletPath, requestDetails.contextPath, requestDetails.pathInfo, requestDetails.pathTranslated, requestDetails.clientAddress, requestDetails.remoteHost, requestDetails.remotePort,
                        requestDetails.remoteUser, requestDetails.authType, requestDetails.scheme, requestDetails.method, requestDetails.protocol, requestDetails.mimeType, requestDetails.encoding, requestDetails.contentLength,
                        requestDetails.headerMapVariants, requestDetails.url, requestDetails.uri, requestDetails.serverName, requestDetails.serverPort, requestDetails.localAddr, requestDetails.localName, requestDetails.localPort, requestDetails.country,
                        requestDetails.cookieMapVariants, requestDetails.requestedSessionId, requestDetails.queryStringVariants, 
                        requestDetails.requestParameterMapVariants, requestDetails.requestParameterMap);
            isIncomingReferrerProtectionExclude = incomingProtectionExcludeDefinition != null && incomingProtectionExcludeDefinition.isExcludeReferrerProtection();
            isIncomingSecretTokenProtectionExclude = incomingProtectionExcludeDefinition != null && incomingProtectionExcludeDefinition.isExcludeSecretTokenProtection();
            isIncomingParameterAndFormProtectionExclude = incomingProtectionExcludeDefinition != null && incomingProtectionExcludeDefinition.isExcludeParameterAndFormProtection();
            isIncomingSelectboxFieldProtectionExclude = incomingProtectionExcludeDefinition != null && incomingProtectionExcludeDefinition.isExcludeSelectboxFieldProtection();
            isIncomingCheckboxFieldProtectionExclude = incomingProtectionExcludeDefinition != null && incomingProtectionExcludeDefinition.isExcludeCheckboxFieldProtection();
            isIncomingRadiobuttonFieldProtectionExclude = incomingProtectionExcludeDefinition != null && incomingProtectionExcludeDefinition.isExcludeRadiobuttonFieldProtection();
            isIncomingForceEntranceProtectionExclude = incomingProtectionExcludeDefinition != null && incomingProtectionExcludeDefinition.isExcludeForceEntranceProtection();
            isIncomingSessionToHeaderBindingProtectionExclude = incomingProtectionExcludeDefinition != null && incomingProtectionExcludeDefinition.isExcludeSessionToHeaderBindingProtection();
            isIncomingExtraSessionTimeoutHandlingExclude = incomingProtectionExcludeDefinition != null && incomingProtectionExcludeDefinition.isExcludeExtraSessionTimeoutHandling();
        } catch (Exception e) {
            e.printStackTrace();
            final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Unable to determine if it is a special point (exception during checking): "+e.getMessage());
            return new AllowedFlagWithMessage(false, attack);
        }
        
        // Check if session is expired and redirect to session timeout redirect page or welcome page
    	if(this.extraSessionTimeoutHandling && !isEntryPoint && !isIncomingExtraSessionTimeoutHandlingExclude) {
	        if(!RequestUtils.checkSessionIsActive(request)) {
	    		if(response instanceof HttpServletResponse) {
	    			HttpServletResponse httpResponse = (HttpServletResponse)response;
		    		if(this.sessionTimeoutRedirectPage != null) {
		    			httpResponse.sendRedirect(httpResponse.encodeRedirectURL(sessionTimeoutRedirectPage));
		    			throw new StopFilterProcessingException("Session expired.");
		    		} else if(this.redirectWelcomePage != null) {
		    			httpResponse.sendRedirect(httpResponse.encodeRedirectURL(redirectWelcomePage));
		    			throw new StopFilterProcessingException("Session expired.");
		    		} 
		    		else {
		    			final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Session expired and no session timeout redirect page and redirect welcome page configured\n\t");
		                return new AllowedFlagWithMessage(false, attack);
		    		}
	    		}
	    	}
    	}                                 
        
        // =======================================================================
        // Check for session thefts and param/form tampering + UNCOVER SUBMITTED VALUES
        // =======================================================================
        HttpSession session = request.getSession(false);
        // TODO: was ist hier mit session-timeout zwischen Abruf der Quell-Seite und Submit des Forms auf die Ziel-Seite ?!? ist das form-field-protection und selectbox-protection dann anzupassen / weiter abzusichern ?
        if (session != null) {
            // be careful, if session was invalidated meanwhile
            boolean sessionInvalidated=false, isNewSession=false;
            try {
                isNewSession = session.isNew();
            } catch (IllegalStateException e) {
                sessionInvalidated = true;
            }
            // only apply session theft checks when session is NOT invalidated and NOT new (but not treating renewed sessions as new)
            //if (isNewSession && isRenewSessionPoint) isNewSession = false;
            if (!sessionInvalidated && !isNewSession) {
                // ---------------------------------------------------------
                // Session to IP binding:
                // ---------------------------------------------------------
                if (this.tieSessionToClientAddress) {
                    final String clientBoundInSession = (String) ServerUtils.getAttributeIncludingInternal(session,SESSION_CLIENT_ADDRESS_KEY);
                    if (clientBoundInSession == null) {
                        // Set client IP into session
                        session.setAttribute(SESSION_CLIENT_ADDRESS_KEY, requestDetails.clientAddress);
                    } else if (clientBoundInSession != null) {
                        // Check if client has still the same IP and flag as bad request when not... (possible a session-theft)
                        if (!clientBoundInSession.equals(requestDetails.clientAddress)) {
                            final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Session theft (expected client '"+clientBoundInSession+"' but was called from client '"+requestDetails.clientAddress+"'): "+session.getId());
                            return new AllowedFlagWithMessage(false, attack);
                        }
                    }
                }
                // ---------------------------------------------------------
                // Session to HTTP header binding:
                // ---------------------------------------------------------
                if (this.tieSessionToHeaderList != null && this.tieSessionToHeaderList.length > 0 && !isIncomingSessionToHeaderBindingProtectionExclude) {
                    final Map/*<String,List<String>>*/ relevantHeaders = new HashMap();
                    for (final Iterator entries = requestDetails.headerMap.entrySet().iterator(); entries.hasNext();) {
                        final Map.Entry entry = (Map.Entry) entries.next();
                        final String name = (String) entry.getKey();
                        for (String tieName : this.tieSessionToHeaderList) {
                            if (tieName.equalsIgnoreCase(name)) {
                                final String[] values = (String[]) entry.getValue();
                                relevantHeaders.put( name, Arrays.asList(values) );
                                break;
                            }
                        }
                    }
                    if (this.debug) logLocal("Client session-check relevant headers: "+relevantHeaders);
                    final Map/*<String,List<String>>*/ headersBoundInSession = (Map) ServerUtils.getAttributeIncludingInternal(session,SESSION_CLIENT_HEADERS_KEY);
                    if (headersBoundInSession == null) {
                        // Set client headers into session
                        session.setAttribute(SESSION_CLIENT_HEADERS_KEY, relevantHeaders);
                    } else if (headersBoundInSession != null) {
                        // Check if client has still the same headers and flag as bad request when not... (possible a session-theft)
                        if (!headersBoundInSession.equals(relevantHeaders)) {
                            final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Session theft (expected relevant headers '"+headersBoundInSession+"' do not match the headers sent by the client '"+relevantHeaders+"'): "+session.getId());
                            return new AllowedFlagWithMessage(false, attack);
                        }
                    }
                }
                
                
                // link/form protection stuff - only apply to relevant resource types AND also watch out for "isIncomingProtectionExclude"-matches (done inside the following code)
                    
                // ---------------------------------------------------------
                // Secret random token matching (of protective-content-injection):
                // ---------------------------------------------------------
                if (this.contentInjectionHelper.isInjectSecretTokenIntoLinks() && !isIncomingSecretTokenProtectionExclude) {
                    final String expectedTokenKey = (String) ServerUtils.getAttributeIncludingInternal(session,SESSION_SECRET_RANDOM_TOKEN_KEY_KEY);
                    final String expectedTokenValue = (String) ServerUtils.getAttributeIncludingInternal(session,SESSION_SECRET_RANDOM_TOKEN_VALUE_KEY);
                    if (expectedTokenKey != null && expectedTokenValue != null) {
                        final String actualTokenValue = request.getParameter(expectedTokenKey); // here the temporarily injected param is still in the request (not in the extracted map though, but that is OK)
                        if (actualTokenValue == null || !expectedTokenValue.equals(actualTokenValue)) {
                            // Session theft (client provided no matching request token)
                            if (isEntryPoint) {
                                try {
                                    if (session != null) {
                                        session.invalidate();
                                        session = null;
                                    }
                                } catch (IllegalStateException ignored) {}
                                this.attackHandler.logRegularRequestMessage("Client provided no matching secret request token - definitely invalidated the session but let the request pass (since it is an entry-point): "+requestDetails.servletPath+" with query string: "+requestDetails.queryString);
                            } else {
                                if (DEBUG_PRINT_UNCOVERING_DETAILS) System.out.println("actualTokenValue is "+actualTokenValue+" for request query string "+request.getQueryString());
                                if (this.redirectWelcomePage.length() == 0) {
                                    // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                                    final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Session theft (client provided no matching secret request token [actualTokenValue:"+actualTokenValue+"] and it is not on an entry-point) - and no redirect welcome page configured\n\tIf the requested URL is an application defined requirement without protection tokens consider the available 'incoming protection excludes' rule definitions (and watch for previous spoofing requests that might have terminated the session and are the root cause)");
                                    return new AllowedFlagWithMessage(false, attack);
                                } else {
                                    final String message = "Client provided no matching secret request token [expectedTokenValue:"+expectedTokenValue+" vs. actualTokenValue:"+actualTokenValue+"] and it is not on an entry-point (could also be caused by a session timeout)\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage+"\n\tIf the requested URL is an application defined requirement without protection tokens consider the available 'incoming protection excludes' rule definitions (and watch for previous spoofing requests that might have terminated the session and are the root cause)";
                                    try {
                                        if (session != null) {
                                            session.invalidate();
                                            session = null;
                                        }
                                    } catch (IllegalStateException ignored) {}
                                    response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                                    throw new StopFilterProcessingException(message); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                                }
                            }
                        }
                    }
                }


                // ---------------------------------------------------------
                // Parameter And Form Protection uncovering (PAF):
                // ---------------------------------------------------------
                // Schnelle Abkuerzung, damit auch bei aktiviertem Strict-Mode der Encrypted Links eine Chance auf Ausnahmen durch Excludes besteht...
                if (wasEncryptedUrlManipulated != null && wasEncryptedUrlManipulated.booleanValue() && !isIncomingParameterAndFormProtectionExclude) {
                    // potential parameter tampering (spoofing) detected
                    if (this.redirectWelcomePage.length() == 0) {
                        // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                        final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Parameter and/or form manipulation (client provided mismatching request parameters) - and no redirect welcome page configured");
                        return new AllowedFlagWithMessage(false, attack);
                    } else {
                        final String message = "Client provided mismatching request parameters. Instead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                        try {
                            if (session != null) {
                                session.invalidate();
                                session = null;
                            }
                        } catch (IllegalStateException ignored) {}
                        response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                        throw new StopFilterProcessingException(message); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                    }
                }
               
                // Hier nun die eigentlchen PAF Checks
                if (this.contentInjectionHelper.isProtectParametersAndForms() && session != null  // "session != null" here, since the previous checks above could set the session to null
                        && !(
                            this.contentInjectionHelper.isExtraStrictParameterCheckingForEncryptedLinks() && isDecryptedFormSubmit != null && !isDecryptedFormSubmit.booleanValue() // sprich es ist ein erfolgreich decrypteter link-request (kein form) mit aktiver Strict-Pruefung
                        ) ) // Check auf die Strict-Encrypted-Link-Protections, da bei Link-Request (nicht bei Form-Request) in Verbindung mit Strict-Check und erfolgreich durchgefuehrter Decryption kein PAF-Token-Check mehr notwendig ist
                {
                    final String expectedParameterAndFormProtectionKeyKey = (String) ServerUtils.getAttributeIncludingInternal(session,SESSION_PARAMETER_AND_FORM_PROTECTION_RANDOM_TOKEN_KEY_KEY);
                    if (expectedParameterAndFormProtectionKeyKey != null) {
                        final String key = request.getParameter(expectedParameterAndFormProtectionKeyKey); // here the temporarily injected param is still in the request (not in the extracted request-param-map though, but that is by design and OK)
                        if (key == null && !isIncomingParameterAndFormProtectionExclude) {
                            // Session theft (client provided no matching param-and-form pointer token)
                            if (isEntryPoint) {
                                try {
                                    if (session != null) {
                                        session.invalidate();
                                        session = null;
                                    }
                                } catch (IllegalStateException ignored) {}
                                this.attackHandler.logRegularRequestMessage("Client provided no matching protection token - definitely invalidated the session but let the request pass (since it is an entry-point): "+requestDetails.servletPath+" with query string: "+requestDetails.queryString);
                            } else {
                                if (this.redirectWelcomePage.length() == 0) {
                                    // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                                    final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Parameter and/or form manipulation (client provided no matching protection token and it is not on an entry-point) - and no redirect welcome page configured");
                                    return new AllowedFlagWithMessage(false, attack);
                                } else {
                                    final String message = "Client provided no matching protection token and it is not on an entry-point (could also be caused by a session timeout)\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                                    try {
                                        if (session != null) {
                                            session.invalidate();
                                            session = null;
                                        }
                                    } catch (IllegalStateException ignored) {}
                                    response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                                    throw new StopFilterProcessingException(message); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                                }
                            }
                        }
                        // OK, so when we've come here, we can check if the session holds a param-and-form protection for the given key 
                        if (key != null) { // key can be null when we're on an entry-point
                            final ParameterAndFormProtection parameterAndFormProtection = (ParameterAndFormProtection) ServerUtils.getAttributeIncludingInternal(session,INTERNAL_CONTENT_PREFIX+key);
                            if (parameterAndFormProtection == null && !isIncomingParameterAndFormProtectionExclude) {
                                final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Parameter and/or form manipulation - no matching ParameterAndFormProtection in session for key: "+key);
                                return new AllowedFlagWithMessage(false, attack);
                            }
                            if (parameterAndFormProtection != null) {
                                // OK, so when we've come here, we can check the concrete parameters and form-fields
                                final Set allParametersExpected = parameterAndFormProtection.getAllParameterNames();
                                final Set requiredParametersExpected = parameterAndFormProtection.getRequiredParameterNames();
                                final Set parametersSentFromClient = (imageMapParameterExclude ? RequestUtils.filterRequestParameterMap(requestDetails.requestParameterMap.keySet()) : requestDetails.requestParameterMap.keySet());
                                // take incoming parameter protection excludes into account when checking for spoofings/attacks
                                if (!isIncomingParameterAndFormProtectionExclude) {
                                    if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("Minimum parameter expectation: "+requiredParametersExpected);
                                    // check if fields have been added or removed by the client
                                    if ( !allParametersExpected.containsAll(parametersSentFromClient) // so we check if unexpected parameters were submitted... 
                                         || !parametersSentFromClient.containsAll(requiredParametersExpected) // and check if required-to-submit parameters have been illegaly removed by the client...
                                         ) { // here disabled form fields are checked automatically (when configured to check) since they were already taken care of when filling the "parameterAndFormProtection" object during the previous response (see ResponseFilter)
                                        // potential parameter tampering (spoofing) detected
                                        if (this.redirectWelcomePage.length() == 0) {
                                            // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                                            final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Parameter and/or form manipulation (client provided mismatching request parameters) - and no redirect welcome page configured");
                                            return new AllowedFlagWithMessage(false, attack);
                                        } else {
                                            final String message = "Client provided mismatching request parameters\n\tExpected maximum: "+allParametersExpected+"\n\tExpected minimum: "+requiredParametersExpected+"\n\tActually received from client: "+parametersSentFromClient+"\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                                            try {
                                                if (session != null) {
                                                    session.invalidate();
                                                    session = null;
                                                }
                                            } catch (IllegalStateException ignored) {}
                                            response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                                            throw new StopFilterProcessingException(message); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                                        }
                                    }
                                    // check if the request parameter value counts (when configured to check) have mismatches according to their min/max value counts
                                    if (this.contentInjectionHelper.isExtraProtectRequestParamValueCount()) {
                                        for (final Iterator entries = request.getOriginalParameterMap().entrySet().iterator(); entries.hasNext();) {
                                            final Map.Entry/*<String,String[]>*/ entry = (Map.Entry) entries.next();
                                            final String parameterName = (String) entry.getKey();
                                            final String[] parameterValues = (String[]) entry.getValue();
                                            final int minimum = parameterAndFormProtection.getMinimumValueCountForParameterName(parameterName);
                                            final int maximum = parameterAndFormProtection.getMaximumValueCountForParameterName(parameterName);
                                            if (parameterValues.length < minimum  ||  parameterValues.length > maximum) {
                                                // potential parameter tampering (spoofing) detected
                                                if (this.redirectWelcomePage.length() == 0) {
                                                    // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                                                    final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Parameter and/or form manipulation (client provided mismatching request parameter value count) - and no redirect welcome page configured");
                                                    return new AllowedFlagWithMessage(false, attack);
                                                } else {
                                                    final String message = "Client provided mismatching request parameter value count for parameter: "+parameterName+"\n\tExpected maximum value count for this parameter: "+maximum+"\n\tExpected minimum value count for this parameter: "+minimum+"\n\tValue count for this parameter actually received from client: "+parameterValues.length+"\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                                                    try {
                                                        if (session != null) {
                                                            session.invalidate();
                                                            session = null;
                                                        }
                                                    } catch (IllegalStateException ignored) {}
                                                    response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                                                    throw new StopFilterProcessingException(message); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                                                }
                                            }
                                        }
                                    }
                                    // check if readonly fields (when configured to check) have mismatching (tampered) values
                                    if (this.contentInjectionHelper.isExtraProtectReadonlyFormFields()) {
                                        for (final Iterator readonlyFields = parameterAndFormProtection.getReadonlyFieldsName2ExpectedValues().entrySet().iterator(); readonlyFields.hasNext();) {
                                            final Map.Entry/*<String,List<String>>*/ readonlyField = (Map.Entry) readonlyFields.next();
                                            final String fieldname = (String) readonlyField.getKey();
                                            // consider the potential case where a readonly field is defined multiple times and one of them is readwrite allowed... 
                                            if ( !parameterAndFormProtection.isAlsoReadwriteField(fieldname) ) { // ...therefore only apply readonly field protection to those readonly fields that are *not* a readwrite field at the same time
                                                final List/*<String>*/ expectedValues = (List) readonlyField.getValue();
                                                assert expectedValues != null;
                                                final String[] actualSubmittedValues = (String[]) request.getOriginalParameterValues(fieldname);
                                                if (RequestUtils.isMismatch(expectedValues, actualSubmittedValues)) {
                                                    // potential readonly field tampering (spoofing) detected
                                                    if (this.redirectWelcomePage.length() == 0) {
                                                        // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                                                        final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Parameter and/or form manipulation (client provided mismatching request parameter value for readonly field '"+fieldname+"') - and no redirect welcome page configured");
                                                        return new AllowedFlagWithMessage(false, attack);
                                                    } else {
                                                        final String message = "Client provided mismatching request parameter value for readonly field '"+fieldname+"'\n\tExpected: "+expectedValues+"\n\tActually received from client: "+Arrays.asList(actualSubmittedValues)+"\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                                                        try {
                                                            if (session != null) {
                                                                session.invalidate();
                                                                session = null;
                                                            }
                                                        } catch (IllegalStateException ignored) {}
                                                        response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                                                        throw new StopFilterProcessingException(message); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }


                                // Here the uncovering stuff to re-add previously removed hidden fields happens:
                                requestDetails.somethingHasBeenUncovered = false;
                                if (this.hiddenFormFieldProtection) {
                                    for (final Iterator previouslyRemovedHiddenFields = parameterAndFormProtection.getHiddenFieldsName2RemovedValues().entrySet().iterator(); previouslyRemovedHiddenFields.hasNext();) {
                                        final Map.Entry/*<String,List<String>>*/ previouslyRemovedHiddenField = (Map.Entry) previouslyRemovedHiddenFields.next();
                                        final String fieldname = (String) previouslyRemovedHiddenField.getKey();
                                        final List/*<String>*/ values = (List) previouslyRemovedHiddenField.getValue();
                                        if (fieldname != null && values != null) {
                                            final String[] valueArray = (String[])values.toArray(new String[0]);
                                            request.setParameter(fieldname, valueArray, false);
                                            requestDetails.somethingHasBeenUncovered = true;
                                        }
                                    }
                                }


                                // TODO: noch per konfig flag einstellen koennen, dass bei radiobuttons sowie bei non-multiple selectboxes es als spoofing gilt, wenn mehr als 1 wert submitted wurden
                                AllowedFlagWithMessage selectboxCheckboxRadiobuttonProtectionResult;
                                // Uncover SelectBox-Protection
                                selectboxCheckboxRadiobuttonProtectionResult = selectboxCheckboxRadiobuttonProtection(session, requestDetails, request, response, parameterAndFormProtection.getSelectboxFieldsName2AllowedValues(), 
                                                                this.selectboxProtection, this.selectboxValueMasking, 
                                                                SESSION_SELECTBOX_MASKING_PREFIX, isIncomingSelectboxFieldProtectionExclude);
                                if (selectboxCheckboxRadiobuttonProtectionResult != null) return selectboxCheckboxRadiobuttonProtectionResult;
                                // Uncover CheckBox-Protection
                                selectboxCheckboxRadiobuttonProtectionResult = selectboxCheckboxRadiobuttonProtection(session, requestDetails, request, response, parameterAndFormProtection.getCheckboxFieldsName2AllowedValues(), 
                                                                this.checkboxProtection, this.checkboxValueMasking, 
                                                                SESSION_CHECKBOX_MASKING_PREFIX, isIncomingCheckboxFieldProtectionExclude);
                                if (selectboxCheckboxRadiobuttonProtectionResult != null) return selectboxCheckboxRadiobuttonProtectionResult;
                                // Uncover RadioButton-Protection
                                selectboxCheckboxRadiobuttonProtectionResult = selectboxCheckboxRadiobuttonProtection(session, requestDetails, request, response, parameterAndFormProtection.getRadiobuttonFieldsName2AllowedValues(), 
                                                                this.radiobuttonProtection, this.radiobuttonValueMasking, 
                                                                SESSION_RADIOBUTTON_MASKING_PREFIX, isIncomingRadiobuttonFieldProtectionExclude);
                                if (selectboxCheckboxRadiobuttonProtectionResult != null) return selectboxCheckboxRadiobuttonProtectionResult;

                                
                                // now re-create the requestDetails paramMap and its variants (since now the uncovering has taken place) - but only when something was uncovered
                                if (requestDetails.somethingHasBeenUncovered) {
                                    if (DEBUG_PRINT_UNCOVERING_DETAILS) System.out.println("********* BEFORE RE-CREATION: "+RequestUtils.printParameterMap(requestDetails.requestParameterMap));
                                    requestDetails.requestParameterMap = new HashMap( request.getParameterMap() ); // defensive copy of the WRAPPED REQUEST map (i.e. the request as seen by the application)
                                    removeTemporarilyInjectedParametersFromMap(requestDetails.requestParameterMap, session, cryptoDetectionString);
                                    if (isHavingEnabledRequestParameterCheckingRules) requestDetails.requestParameterMapVariants = ServerUtils.permutateVariants(requestDetails.requestParameterMap, requestDetails.nonStandardPermutationsRequired,requestDetails.decodingPermutationLevel);
                                    if (DEBUG_PRINT_UNCOVERING_DETAILS) System.out.println("********* AFTER RE-CREATION: "+RequestUtils.printParameterMap(requestDetails.requestParameterMap));
                                }

                            }
                        }
                    }
                }





                // SESSION AUTO-CREATE, AFTER DESIRED INVALIDATION (see above)
                if (session == null && isEntryPoint /*&& !requestDetails.isMatchingOutgoingResponseModificationExclusion*/) { 
                    // create or retrieve session-based crypto stuff - for later use (see below)
                    if (this.contentInjectionHelper.isEncryptQueryStringInLinks()) {
                        try {
                            session = request.getSession(true);
                            assert session != null;
                            RequestUtils.createOrRetrieveRandomTokenFromSession(session, SESSION_ENCRYPT_QUERY_STRINGS_CRYPTODETECTION_KEY);
                            RequestUtils.createOrRetrieveRandomCryptoKeyFromSession(session, SESSION_ENCRYPT_QUERY_STRINGS_CRYPTOKEY_KEY, this.extraEncryptedValueHashProtection);
                        } catch (Exception e) {
                            this.attackHandler.logWarningRequestMessage("Unable to define protection content in session: "+e.getMessage());
                            try {
                                if (session != null) {
                                    session.invalidate();
                                    session = null;
                                }
                            } catch (IllegalStateException ignored) {}
                            // direkt schon hier als Attack werten
                            return new AllowedFlagWithMessage(false, new Attack("Unable to define protection content in session"));
                        }
                    }
                    // create or retrieve session-based request tokens - for later use (see below)
                    if (this.contentInjectionHelper.isInjectSecretTokenIntoLinks()) {
                        try {
                            session = request.getSession(true);
                            assert session != null;
                            RequestUtils.createOrRetrieveRandomTokenFromSession(session, SESSION_SECRET_RANDOM_TOKEN_KEY_KEY);
                            RequestUtils.createOrRetrieveRandomTokenFromSession(session, SESSION_SECRET_RANDOM_TOKEN_VALUE_KEY);
                        } catch (Exception e) {
                            this.attackHandler.logWarningRequestMessage("Unable to define protection content in session: "+e.getMessage());
                            try {
                                if (session != null) {
                                    session.invalidate();
                                    session = null;
                                }
                            } catch (IllegalStateException ignored) {}
                            // direkt schon hier als Attack werten
                            return new AllowedFlagWithMessage(false, new Attack("Unable to define protection content in session"));
                        }
                    }
                    // now the key for param-and-form protection keys - for later use (see below)
                    if (this.contentInjectionHelper.isProtectParametersAndForms()) {
                        try {
                            session = request.getSession(true);
                            assert session != null;
                            RequestUtils.createOrRetrieveRandomTokenFromSession(session, SESSION_PARAMETER_AND_FORM_PROTECTION_RANDOM_TOKEN_KEY_KEY); // yes, it is the key of the key
                            //OLD parameterAndFormProtectionEmptyValue = ServerUtils.findReusableSessionContentKeyOrCreateNewOne(session, ParameterAndFormProtection.EMPTY);
                        } catch (Exception e) {
                            this.attackHandler.logWarningRequestMessage("Unable to define protection content in session: "+e.getMessage());
                            try {
                                if (session != null) {
                                    session.invalidate();
                                    session = null;
                                }
                            } catch (IllegalStateException ignored) {}
                            // direkt schon hier als Attack werten
                            return new AllowedFlagWithMessage(false, new Attack("Unable to define protection content in session"));
                        }
                    }
                }



                    
                    
            }
        }
        
        
        
        
        
        
        
        
        
        
        // =========================================================
        // Check against DoS limits
        // =========================================================
        if (this.denialOfServiceLimitCounter != null && this.denialOfServiceLimitDefinitions.hasEnabledDefinitions()) {
            try {
                final DenialOfServiceLimitDefinition definition = this.denialOfServiceLimitDefinitions.getMatchingDenialOfServiceLimitDefinition(request,
                        requestDetails.servletPath, requestDetails.contextPath, requestDetails.pathInfo, requestDetails.pathTranslated, requestDetails.clientAddress, requestDetails.remoteHost, requestDetails.remotePort,
                        requestDetails.remoteUser, requestDetails.authType, requestDetails.scheme, requestDetails.method, requestDetails.protocol, requestDetails.mimeType, requestDetails.encoding, requestDetails.contentLength,
                        requestDetails.headerMapVariants, requestDetails.url, requestDetails.uri, requestDetails.serverName, requestDetails.serverPort, requestDetails.localAddr, requestDetails.localName, requestDetails.localPort, requestDetails.country,
                        requestDetails.cookieMapVariants, requestDetails.requestedSessionId, requestDetails.queryStringVariants, 
                        requestDetails.requestParameterMapVariants, requestDetails.requestParameterMap);
                if (definition != null) {
                    this.denialOfServiceLimitCounter.trackDenialOfServiceRequest(requestDetails.clientAddress, definition, request);
                }
            } catch (Exception e) {
                e.printStackTrace();
                final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Unable to determine if it is a DoS match (exception during checking): "+e.getMessage());
                return new AllowedFlagWithMessage(false, attack);
            }
        }

        
        // =========================================================
        // Check against white-list
        // =========================================================
        if (this.whiteListDefinitions.hasEnabledDefinitions()) {
            try {
                final boolean isWhitelistMatch = this.whiteListDefinitions.isWhitelistMatch(request,
                        requestDetails.servletPath, requestDetails.contextPath, requestDetails.pathInfo, requestDetails.pathTranslated, requestDetails.clientAddress, requestDetails.remoteHost, requestDetails.remotePort,
                        requestDetails.remoteUser, requestDetails.authType, requestDetails.scheme, requestDetails.method, requestDetails.protocol, requestDetails.mimeType, requestDetails.encoding, requestDetails.contentLength,
                        requestDetails.headerMapVariants, requestDetails.url, requestDetails.uri, requestDetails.serverName, requestDetails.serverPort, requestDetails.localAddr, requestDetails.localName, requestDetails.localPort, requestDetails.country,
                        requestDetails.cookieMapVariants, requestDetails.requestedSessionId, requestDetails.queryStringVariants, 
                        requestDetails.requestParameterMapVariants, requestDetails.requestParameterMap, this.treatNonMatchingServletPathAsMatchForWhitelistRules);
                if (!isWhitelistMatch) {
                    final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Request does not match a white-list definition");
                    return new AllowedFlagWithMessage(false, attack);
                }
            } catch (Exception e) {
                e.printStackTrace();
                final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Unable to determine if it is a whitelist match (exception during checking): "+e.getMessage());
                return new AllowedFlagWithMessage(false, attack);
            }
        }
        
        
        // =========================================================
        // Determine if some special points are accessed (renew-session-and-token-point, captcha-point, etc.)
        // =========================================================
        boolean isRenewSessionPoint, isRenewSecretTokenPoint, isRenewParamAndFormTokenPoint, isRenewCryptoKeyPoint;
        CaptchaPointDefinition captchaPointMatch = null; // if this variable is filled (not null) below, then we've got a captcha required access
        try {
            final RenewSessionAndTokenPointDefinition renewSessionAndTokenPointDefinition = this.renewSessionPointDefinitions.getMatchingRenewSessionAndTokenPointDefinition(request,
                        requestDetails.servletPath, requestDetails.contextPath, requestDetails.pathInfo, requestDetails.pathTranslated, requestDetails.clientAddress, requestDetails.remoteHost, requestDetails.remotePort,
                        requestDetails.remoteUser, requestDetails.authType, requestDetails.scheme, requestDetails.method, requestDetails.protocol, requestDetails.mimeType, requestDetails.encoding, requestDetails.contentLength,
                        requestDetails.headerMapVariants, requestDetails.url, requestDetails.uri, requestDetails.serverName, requestDetails.serverPort, requestDetails.localAddr, requestDetails.localName, requestDetails.localPort, requestDetails.country,
                        requestDetails.cookieMapVariants, requestDetails.requestedSessionId, requestDetails.queryStringVariants, 
                        requestDetails.requestParameterMapVariants, requestDetails.requestParameterMap);
            isRenewSessionPoint = renewSessionAndTokenPointDefinition != null && renewSessionAndTokenPointDefinition.isRenewSession();
            isRenewSecretTokenPoint = renewSessionAndTokenPointDefinition != null && renewSessionAndTokenPointDefinition.isRenewSecretToken();
            isRenewParamAndFormTokenPoint = renewSessionAndTokenPointDefinition != null && renewSessionAndTokenPointDefinition.isRenewParamAndFormToken();
            isRenewCryptoKeyPoint = renewSessionAndTokenPointDefinition != null && renewSessionAndTokenPointDefinition.isRenewCryptoKey();
            
            captchaPointMatch = this.captchaPointDefinitions.getMatchingCaptchaPointDefinition(request,
                        requestDetails.servletPath, requestDetails.contextPath, requestDetails.pathInfo, requestDetails.pathTranslated, requestDetails.clientAddress, requestDetails.remoteHost, requestDetails.remotePort,
                        requestDetails.remoteUser, requestDetails.authType, requestDetails.scheme, requestDetails.method, requestDetails.protocol, requestDetails.mimeType, requestDetails.encoding, requestDetails.contentLength,
                        requestDetails.headerMapVariants, requestDetails.url, requestDetails.uri, requestDetails.serverName, requestDetails.serverPort, requestDetails.localAddr, requestDetails.localName, requestDetails.localPort, requestDetails.country,
                        requestDetails.cookieMapVariants, requestDetails.requestedSessionId, requestDetails.queryStringVariants, 
                        requestDetails.requestParameterMapVariants, requestDetails.requestParameterMap);
        } catch (Exception e) {
            e.printStackTrace();
            final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Unable to determine if it is a special point (exception during checking): "+e.getMessage());
            return new AllowedFlagWithMessage(false, attack);
        }
        
                
        
        // =========================================================
        // Check for renew-session-and-token-points
        // =========================================================
        if (session != null && (isRenewSessionPoint || isRenewSecretTokenPoint || isRenewParamAndFormTokenPoint || isRenewCryptoKeyPoint)) {
            
            // check if we should renew the session
            if (isRenewSessionPoint) {
                final String oldSessionId = session.getId();
                // copy session content from old session into temporary map
                final Map/*<String,Object>*/ sessionContent = new HashMap();
                for (final Enumeration/*<String>*/ names = ServerUtils.getAttributeNamesIncludingInternal(session); names.hasMoreElements();) {
                    final String name = (String) names.nextElement();
                    final Object value = ServerUtils.getAttributeIncludingInternal(session,name);
                    sessionContent.put(name, value);
                }
                // redefine the secret token keys - along with the session renewal
                if (isRenewSecretTokenPoint) {
                    final String oldSecretTokenKey = (String) sessionContent.get(SESSION_SECRET_RANDOM_TOKEN_KEY_KEY);
                    final String oldSecretTokenValue = (String) sessionContent.get(SESSION_SECRET_RANDOM_TOKEN_VALUE_KEY);
                    if (oldSecretTokenKey != null && oldSecretTokenValue != null) {
                        // set the new secret token IDs into the session
                        final String newSecretTokenKey = CryptoUtils.generateRandomToken(true);
                        final String newSecretTokenValue = CryptoUtils.generateRandomToken(true);
                        sessionContent.put(SESSION_SECRET_RANDOM_TOKEN_KEY_KEY, newSecretTokenKey);
                        sessionContent.put(SESSION_SECRET_RANDOM_TOKEN_VALUE_KEY, newSecretTokenValue);
                        // replace the old ones with the new ones in the request (but only if the old ones were correct)
                        final String oldSecretTokenValueInRequest = request.getParameter(oldSecretTokenKey);
                        if (oldSecretTokenValueInRequest != null && oldSecretTokenValueInRequest.equals(oldSecretTokenValue)) {
                            // OK, the token the user supplied matches the correct (old) one so we are allowed to magically add the correct (new) one to the request (and remove the old one)
                            request.setParameter(newSecretTokenKey, new String[]{newSecretTokenValue}, true); // add the new one
                            request.removeParameter(oldSecretTokenKey);
                        }
                        // also set the new redefined tokens into the response wrapper for injecting the correct (new) ones into the response
                        response.redefineSecretTokenKey(newSecretTokenKey);
                        response.redefineSecretTokenValue(newSecretTokenValue);
                        // AND as it is the secret token (which also gets incorporated as parameter into ParameterAndFormProtection-objects) also adjust those PAF objects in session to expect the new instead of the old secret token parameter
                        ServerUtils.renameSecretTokenParameterInAllCachedParameterAndFormProtectionObjects(session, oldSecretTokenKey, newSecretTokenKey, applySetAfterWrite);
                    }
                }
                // redefine the param-and-form token keys - along with the session renewal
                if (isRenewParamAndFormTokenPoint) {
                    final String oldParamAndFormTokenKey = (String) sessionContent.get(SESSION_PARAMETER_AND_FORM_PROTECTION_RANDOM_TOKEN_KEY_KEY);
                    if (oldParamAndFormTokenKey != null) {
                        // set the new param-and-form token ID into the session
                        final String newParamAndFormTokenKey = CryptoUtils.generateRandomToken(true);
                        sessionContent.put(SESSION_PARAMETER_AND_FORM_PROTECTION_RANDOM_TOKEN_KEY_KEY, newParamAndFormTokenKey);
                        // replace the old ones with the new ones in the request
                        final String oldParamAndFormTokenValueInRequest = request.getParameter(oldParamAndFormTokenKey);
                        if (oldParamAndFormTokenValueInRequest != null) {
                            // OK, magically add the new one to the request (and remove the old one)
                            request.setParameter(newParamAndFormTokenKey, new String[]{oldParamAndFormTokenValueInRequest}, true); // add the new one with the old value (to keep the form association)
                            request.removeParameter(oldParamAndFormTokenKey);
                        }
                        // also set the new redefined tokens into the response wrapper for injecting the correct (new) ones into the response
                        response.redefineParameterAndFormProtectionKey(newParamAndFormTokenKey);
                    }
                }
                // redefine the crypto keys - along with the session renewal
                if (isRenewCryptoKeyPoint) {
                    final String oldCryptoDetectionString = (String) sessionContent.get(SESSION_ENCRYPT_QUERY_STRINGS_CRYPTODETECTION_KEY);
                    final CryptoKeyAndSalt oldCryptoKey = (CryptoKeyAndSalt) sessionContent.get(SESSION_ENCRYPT_QUERY_STRINGS_CRYPTOKEY_KEY);
                    if (oldCryptoDetectionString != null && oldCryptoKey != null) {
                        // set the new crypto keys into the session
                        final String newCryptoDetectionString = CryptoUtils.generateRandomToken(true);
                        final CryptoKeyAndSalt newCryptoKey = CryptoUtils.generateRandomCryptoKeyAndSalt(this.extraEncryptedValueHashProtection);
                        sessionContent.put(SESSION_ENCRYPT_QUERY_STRINGS_CRYPTODETECTION_KEY, newCryptoDetectionString);
                        sessionContent.put(SESSION_ENCRYPT_QUERY_STRINGS_CRYPTOKEY_KEY, newCryptoKey);
                        // also set the new redefined tokens into the response wrapper for injecting the correct (new) ones into the response
                        response.redefineCryptoDetectionString(newCryptoDetectionString);
                        response.redefineCryptoKey(newCryptoKey);
                    }
                }
                // RENEW THE SESSION
                // invalidate old session
                session.invalidate();
                // create a new session
                session = request.getSession(true);
                // copy content from temporary map to new session
                for (final Iterator entries = sessionContent.entrySet().iterator(); entries.hasNext();) {
                    final Map.Entry/*<String,Object>*/ entry = (Map.Entry) entries.next();
                    final String name = (String) entry.getKey();
                    final Object value = entry.getValue();
                    session.setAttribute(name, value);
                }
                this.attackHandler.logRegularRequestMessage("User is touching a renew-session-and-token-point (see the following logged request details): session will be renewed from "+oldSessionId+" to "+session.getId()+" after the following logged request");
            } else {
                // OK, so *no* session renew is wanted, but maybe the token/key renewals are still desired:
                // =================================================== TODO: ggf. aus den Teilen im if()-Block (oben) und den hier folgenden Teilen im else-Block Gemeinsamkeiten herausloesen
                // redefine the secret token keys - without a session renewal
                if (isRenewSecretTokenPoint) {
                    final String oldSecretTokenKey = (String) ServerUtils.getAttributeIncludingInternal(session,SESSION_SECRET_RANDOM_TOKEN_KEY_KEY);
                    final String oldSecretTokenValue = (String) ServerUtils.getAttributeIncludingInternal(session,SESSION_SECRET_RANDOM_TOKEN_VALUE_KEY);
                    if (oldSecretTokenKey != null && oldSecretTokenValue != null) {
                        // set the new secret token IDs into the session
                        final String newSecretTokenKey = CryptoUtils.generateRandomToken(true);
                        final String newSecretTokenValue = CryptoUtils.generateRandomToken(true);
                        session.setAttribute(SESSION_SECRET_RANDOM_TOKEN_KEY_KEY, newSecretTokenKey);
                        session.setAttribute(SESSION_SECRET_RANDOM_TOKEN_VALUE_KEY, newSecretTokenValue);
                        // replace the old ones with the new ones in the request (but only if the old ones were correct)
                        final String oldSecretTokenValueInRequest = request.getParameter(oldSecretTokenKey);
                        if (oldSecretTokenValueInRequest != null && oldSecretTokenValueInRequest.equals(oldSecretTokenValue)) {
                            // OK, the token the user supplied matches the correct (old) one so we are allowed to magically add the correct (new) one to the request (and remove the old one)
                            request.setParameter(newSecretTokenKey, new String[]{newSecretTokenValue}, true); // add the new one
                            request.removeParameter(oldSecretTokenKey);
                        }
                        // also set the new redefined tokens into the response wrapper for injecting the correct (new) ones into the response
                        response.redefineSecretTokenKey(newSecretTokenKey);
                        response.redefineSecretTokenValue(newSecretTokenValue);
                        // AND as it is the secret token (which also gets incorporated as parameter into ParameterAndFormProtection-objects) also adjust those PAF objects in session to expect the new instead of the old secret token parameter
                        ServerUtils.renameSecretTokenParameterInAllCachedParameterAndFormProtectionObjects(session, oldSecretTokenKey, newSecretTokenKey, applySetAfterWrite);
                    }
                }
                // redefine the param-and-form token keys - without a session renewal
                if (isRenewParamAndFormTokenPoint) {
                    final String oldParamAndFormTokenKey = (String) ServerUtils.getAttributeIncludingInternal(session,SESSION_PARAMETER_AND_FORM_PROTECTION_RANDOM_TOKEN_KEY_KEY);
                    if (oldParamAndFormTokenKey != null) {
                        // set the new param-and-form token ID into the session
                        final String newParamAndFormTokenKey = CryptoUtils.generateRandomToken(true);
                        session.setAttribute(SESSION_PARAMETER_AND_FORM_PROTECTION_RANDOM_TOKEN_KEY_KEY, newParamAndFormTokenKey);
                        // replace the old ones with the new ones in the request
                        final String oldParamAndFormTokenValueInRequest = request.getParameter(oldParamAndFormTokenKey);
                        if (oldParamAndFormTokenValueInRequest != null) {
                            // OK, magically add the new one to the request (and remove the old one)
                            request.setParameter(newParamAndFormTokenKey, new String[]{oldParamAndFormTokenValueInRequest}, true); // add the new one with the old value (to keep the form association)
                            request.removeParameter(oldParamAndFormTokenKey);
                        }
                        // also set the new redefined tokens into the response wrapper for injecting the correct (new) ones into the response
                        response.redefineParameterAndFormProtectionKey(newParamAndFormTokenKey);
                    }
                }
                // redefine the crypto keys - without a session renewal
                if (isRenewCryptoKeyPoint) {
                    final String oldCryptoDetectionString = (String) ServerUtils.getAttributeIncludingInternal(session,SESSION_ENCRYPT_QUERY_STRINGS_CRYPTODETECTION_KEY);
                    final CryptoKeyAndSalt oldCryptoKey = (CryptoKeyAndSalt) ServerUtils.getAttributeIncludingInternal(session,SESSION_ENCRYPT_QUERY_STRINGS_CRYPTOKEY_KEY);
                    if (oldCryptoDetectionString != null && oldCryptoKey != null) {
                        // set the new crypto keys into the session
                        final String newCryptoDetectionString = CryptoUtils.generateRandomToken(true);
                        final CryptoKeyAndSalt newCryptoKey = CryptoUtils.generateRandomCryptoKeyAndSalt(this.extraEncryptedValueHashProtection);
                        session.setAttribute(SESSION_ENCRYPT_QUERY_STRINGS_CRYPTODETECTION_KEY, newCryptoDetectionString);
                        session.setAttribute(SESSION_ENCRYPT_QUERY_STRINGS_CRYPTOKEY_KEY, newCryptoKey);
                        // also set the new redefined tokens into the response wrapper for injecting the correct (new) ones into the response
                        response.redefineCryptoDetectionString(newCryptoDetectionString);
                        response.redefineCryptoKey(newCryptoKey);
                    }
                }
            }
            
        }
        
        
        
        // log some stuff for debugging purposes
        if (this.debug) {
            // Client IP stuff
            logLocal("WebCastellum:doBeforeProcessing: access from client: "+requestDetails.clientAddress+" with user-agent: "+requestDetails.agent);
            logLocal("WebCastellum:doBeforeProcessing: client host name: "+requestDetails.remoteHost);
            logLocal("WebCastellum:doBeforeProcessing: client port: "+requestDetails.remotePort);
            
            // special points stuff
            logLocal("WebCastellum:doBeforeProcessing: entry-point: "+isEntryPoint);
            logLocal("WebCastellum:doBeforeProcessing: renew-point (session): "+isRenewSessionPoint);
            logLocal("WebCastellum:doBeforeProcessing: renew-point (secret token): "+isRenewSecretTokenPoint);
            logLocal("WebCastellum:doBeforeProcessing: renew-point (param-and-form token): "+isRenewParamAndFormTokenPoint);
            logLocal("WebCastellum:doBeforeProcessing: renew-point (crypto key): "+isRenewCryptoKeyPoint);
            logLocal("WebCastellum:doBeforeProcessing: captcha-point: "+(captchaPointMatch!=null));
            logLocal("WebCastellum:doBeforeProcessing: incoming-protection-exclude (referrer): "+isIncomingReferrerProtectionExclude);
            logLocal("WebCastellum:doBeforeProcessing: incoming-protection-exclude (secret-token): "+isIncomingSecretTokenProtectionExclude);
            logLocal("WebCastellum:doBeforeProcessing: incoming-protection-exclude (parameter-and-form): "+isIncomingParameterAndFormProtectionExclude);
            logLocal("WebCastellum:doBeforeProcessing: incoming-protection-exclude (selectbox-field): "+isIncomingSelectboxFieldProtectionExclude);
            logLocal("WebCastellum:doBeforeProcessing: incoming-protection-exclude (force-entrance): "+isIncomingForceEntranceProtectionExclude);
            logLocal("WebCastellum:doBeforeProcessing: incoming-protection-exclude (session-header-binding): "+isIncomingSessionToHeaderBindingProtectionExclude);
            
            // HTTP header stuff
            logLocal("WebCastellum:doBeforeProcessing: HTTP headers of request (see below)");
            final Enumeration headers = request.getHeaderNames();
            if (headers != null) {
                while (headers.hasMoreElements()) {
                    final String headerName = (String) headers.nextElement();
                    for (final Enumeration headerValues = request.getHeaders(headerName); headerValues.hasMoreElements();) {
                        final String headerValue = (String) headerValues.nextElement();
                        logLocal(headerName+" = "+headerValue);
                    }
                }
            } else logLocal("This servlet-container does not allow the access of HTTP headers");
            
            // Encoding and content stuff
            logLocal("WebCastellum:doBeforeProcessing: character encoding: "+requestDetails.encoding);
            logLocal("WebCastellum:doBeforeProcessing: content length: "+requestDetails.contentLength);
            logLocal("WebCastellum:doBeforeProcessing: content MIME type: "+requestDetails.mimeType);
            logLocal("WebCastellum:doBeforeProcessing: protocol: "+requestDetails.protocol);
            logLocal("WebCastellum:doBeforeProcessing: scheme: "+requestDetails.scheme);
            logLocal("WebCastellum:doBeforeProcessing: secure: "+request.isSecure());
            
            // Local (server) NIC
            logLocal("WebCastellum:doBeforeProcessing: receiving NIC address: "+requestDetails.localAddr);
            logLocal("WebCastellum:doBeforeProcessing: receiving NIC name: "+requestDetails.localName);
            logLocal("WebCastellum:doBeforeProcessing: receiving NIC port: "+requestDetails.localPort);
            logLocal("WebCastellum:doBeforeProcessing: receiving server name: "+requestDetails.serverName);
            logLocal("WebCastellum:doBeforeProcessing: receiving server port: "+requestDetails.serverPort);
            
            // Authentication stuff
            logLocal("WebCastellum:doBeforeProcessing: auth type: "+requestDetails.authType);
            logLocal("WebCastellum:doBeforeProcessing: remote user: "+requestDetails.remoteUser);
            logLocal("WebCastellum:doBeforeProcessing: user principal: "+request.getUserPrincipal());
            
            // URI stuff
            logLocal("WebCastellum:doBeforeProcessing: requested URI: "+requestDetails.uri);
            logLocal("WebCastellum:doBeforeProcessing: requested URL: "+requestDetails.url);
            logLocal("WebCastellum:doBeforeProcessing: servlet path: "+requestDetails.servletPath);
            logLocal("WebCastellum:doBeforeProcessing: context path: "+requestDetails.contextPath);
            logLocal("WebCastellum:doBeforeProcessing: path info: "+requestDetails.pathInfo);
            logLocal("WebCastellum:doBeforeProcessing: path translated: "+requestDetails.pathTranslated);
            logLocal("WebCastellum:doBeforeProcessing: query string: "+requestDetails.queryString);
            logLocal("WebCastellum:doBeforeProcessing: method: "+requestDetails.method);
            
            // Request parameter stuff
            logLocal("WebCastellum:doBeforeProcessing: request parameters (see below)");
            for (final Enumeration parameters = request.getParameterNames(); parameters.hasMoreElements();) {
                final String parameterName = (String) parameters.nextElement();
                final String[] parameterValues = request.getParameterValues(parameterName);
                for (String parameterValue : parameterValues) {
                    logLocal(parameterName+" = " + parameterValue);
                }
            }
            
            // Session stuff
            logLocal("WebCastellum:doBeforeProcessing: requested session-id: "+requestDetails.requestedSessionId);
            if (session != null) {
                try {
                    // TODO use StringBuilder
                    final String fromWhere = (requestDetails.sessionCameFromCookie?"[cookie]":"") + (requestDetails.sessionCameFromCookie?"[url]":"");
                    final String existingOrNew = session.isNew() ? "new" : "existing";
                    logLocal("WebCastellum:doBeforeProcessing: "+existingOrNew+" session ("+fromWhere+"): "+session.getId());
                } catch (IllegalStateException e) {
                    logLocal("Unable to log session: "+e.getMessage());
                }
            }
        }
        
        
        
        
        // =========================================================
        // Check for duplicate headers
        // =========================================================
        if (this.blockRequestsWithDuplicateHeaders) {
            String duplicateHeaderName = null;
            for (final Iterator entries = requestDetails.headerMap.entrySet().iterator(); entries.hasNext();) {
                final Map.Entry/*<String,String[]>*/ entry = (Map.Entry) entries.next();
                final String name = (String) entry.getKey();
                final String[] values = (String[]) entry.getValue();
                if (values.length > 1) {
                    duplicateHeaderName = name;
                    break;
                }
            }
            if (duplicateHeaderName != null) {
                final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Duplicate request headers detected: "+duplicateHeaderName);
                return new AllowedFlagWithMessage(false, attack);
            }
        }
        
        
        
        // =========================================================
        // Check for strange (unknown or missing) referring URLs
        // =========================================================
        if (!isEntryPoint && !isIncomingReferrerProtectionExclude) {
            if (requestDetails.referrer == null) { // treat missing referrer as potential spoofing
                if (this.blockRequestsWithMissingReferrer) {
                    if (this.redirectWelcomePage.length() == 0) {
                        // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                        final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Missing referrer header in request to non-entry point - and no redirect welcome page configured");
                        return new AllowedFlagWithMessage(false, attack);
                    } else {
                        final String message = "Missing referrer header in request to non-entry point\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                        try {
                            if (session != null) {
                                session.invalidate();
                                session = null;
                            }
                        } catch (IllegalStateException ignored) {}
                        response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                        throw new StopFilterProcessingException(message); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                    }
                }
            } else if ( !ServerUtils.isSameServer(requestDetails.referrer,requestDetails.url) ) { // treat wrong referrer as potential spoofing
                if (this.blockRequestsWithUnknownReferrer) {
                    if (this.redirectWelcomePage.length() == 0) {
                        // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                        final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Unknown (strange) referrer header in request to non-entry point (not matching the server that is to be accessed) - and no redirect welcome page configured");
                        return new AllowedFlagWithMessage(false, attack);
                    } else {
                        final String message = "Unknown (strange) referrer header in request to non-entry point (not matching the server that is to be accessed)\n\tURL: "+requestDetails.url+"\n\tReferrer: "+requestDetails.referrer+"\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                        try {
                            if (session != null) {
                                session.invalidate();
                                session = null;
                            }
                        } catch (IllegalStateException ignored) {}
                        response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                        throw new StopFilterProcessingException(message); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                    }
                }
            }
        }
        
        
        
        
        
        
        
        
        
        // =========================================================
        // Check for bad request patterns
        // =========================================================
        try {
            final BadRequestDefinition badRequestDefinition = this.badRequestDefinitions.getMatchingBadRequestDefinition(request,
                        requestDetails.servletPath, requestDetails.contextPath, requestDetails.pathInfo, requestDetails.pathTranslated, requestDetails.clientAddress, requestDetails.remoteHost, requestDetails.remotePort,
                        requestDetails.remoteUser, requestDetails.authType, requestDetails.scheme, requestDetails.method, requestDetails.protocol, requestDetails.mimeType, requestDetails.encoding, requestDetails.contentLength,
                        requestDetails.headerMapVariants, requestDetails.url, requestDetails.uri, requestDetails.serverName, requestDetails.serverPort, requestDetails.localAddr, requestDetails.localName, requestDetails.localPort, requestDetails.country,
                        requestDetails.cookieMapVariants, requestDetails.requestedSessionId, requestDetails.queryStringVariants, 
                        requestDetails.requestParameterMapVariants, requestDetails.requestParameterMap);
            if (badRequestDefinition != null) { // when "badRequestDefinition" is not null, it is a bad-request
                final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Bad request ("+badRequestDefinition.getDescription()+"): "+badRequestDefinition.getIdentification());
                return new AllowedFlagWithMessage(false, attack);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: klar, dass wir hier den request ablehnen, da ein fehler im checking aufgetreten ist... aber sollten wir hier nicht vielleicht es vermeiden, dass der attack-counter hier auch inkrementiert wird und die session terminiert wird?
            // klar, dass es geblockt wird, aber es ist keine vollwertige attacke...
            final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Unable to determine if it is a bad-request (exception during bad-request checking): "+e.getMessage());
            return new AllowedFlagWithMessage(false, attack);
        }
        
        
        // =========================================================
        // Entrance-Enforcement of the web-application through an entry-point in the session
        // =========================================================
        if (this.forceEntranceThroughEntryPoints) {
            if (isEntryPoint) { // = the user comes through an entry-point and we record that
                if (this.contentInjectionHelper.isMatchingOutgoingResponseModificationExclusion(requestDetails.servletPath,requestDetails.uri)) {
                    // TODO: log also to another log ? since otherwise it will only be readable when pre-/post attack logging sends the messages to the log file on an attack
                    this.attackHandler.logRegularRequestMessage("Poor configuration: Entry-point definition is also an outgoing response modification excluded page match. If it is impossible to avoid that overlap remember to add the dynamic pages, where this page links to, also to the entry-point definitions.");
                }
                // auto-create session if no session exists
                if (session == null) {
                    session = request.getSession(true);
                    this.attackHandler.logRegularRequestMessage("Auto-created web session on entry-point: "+session.getId());
                }
                session.setAttribute(SESSION_ENTRY_POINT_TOUCHED_KEY, Boolean.TRUE);
                // (re)define the secret tokens from the session in the response...
                // this is important to ensure the invariant that every possible raising (setting) of the touched-an-entry-point flag also ensures that secret tokens and crypto stuff are present and expected in the session!
                response.redefineSecretTokenKey(RequestUtils.createOrRetrieveRandomTokenFromSession(session, SESSION_SECRET_RANDOM_TOKEN_KEY_KEY));
                response.redefineSecretTokenValue(RequestUtils.createOrRetrieveRandomTokenFromSession(session, SESSION_SECRET_RANDOM_TOKEN_VALUE_KEY));
                response.redefineCryptoDetectionString(RequestUtils.createOrRetrieveRandomTokenFromSession(session, SESSION_ENCRYPT_QUERY_STRINGS_CRYPTODETECTION_KEY));
                response.redefineCryptoKey(RequestUtils.createOrRetrieveRandomCryptoKeyFromSession(session, SESSION_ENCRYPT_QUERY_STRINGS_CRYPTOKEY_KEY, this.extraEncryptedValueHashProtection));
                response.redefineParameterAndFormProtectionKey(RequestUtils.createOrRetrieveRandomTokenFromSession(session, SESSION_PARAMETER_AND_FORM_PROTECTION_RANDOM_TOKEN_KEY_KEY));
                this.attackHandler.logRegularRequestMessage("User is entering the web application through an entry-point (see the following logged request details)");
            } else { // = as this is not an entry-point page request, we have to check if the user did already come through an entry-point
                // when the session is null or the session exists but does not have the SESSION_ENTRY_POINT_TOUCHED_KEY flag
                // then the user is cheating and did not came through an entry-point
                boolean isSessionValidAndFine = false;
                if (session != null) {
                    try {
                        // check if "already touched an entry-point"-flag is set in session
                        if (Boolean.TRUE.equals(ServerUtils.getAttributeIncludingInternal(session,SESSION_ENTRY_POINT_TOUCHED_KEY))) isSessionValidAndFine = true;
                    } catch (IllegalStateException e) {
                        isSessionValidAndFine = false; // as the session is already invalidated
                    } catch (NullPointerException e) {
                        isSessionValidAndFine = false;
                    }
                }
                if (!isSessionValidAndFine && !isIncomingForceEntranceProtectionExclude) {
//                if (session == null || ServerUtils.getAttributeIncludingInternal(session,SESSION_ENTRY_POINT_TOUCHED_KEY) == null) {
                    // NOTE: this could also be a simple session-timeout
                    if (this.redirectWelcomePage.length() == 0) {
                        // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                        final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Direct access without proper web-site entry (and no redirect welcome page configured)");
                        return new AllowedFlagWithMessage(false, attack);
                    } else {
                        final String message = (session==null?"Potential session-timeout (NOT on entry-point)":"User entered application NOT through an entry-point (could also be caused by a session timeout)")+"\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                        try {
                            if (session != null) {
                                session.invalidate();
                                session = null;
                            }
                        } catch (IllegalStateException ignored) {}
                        response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                        throw new StopFilterProcessingException(message); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                    }
                }
                
            }
        }
        

        
        
        
        
        
        

        
        // =========================================================
        // Check for captcha form submits
        // =========================================================
        boolean hasSuccessfullyPassedCaptchaTest = false;
        Map originalParameterMap = null;
        final String captchaIdReceivedForForm = request.getParameter(CAPTCHA_FORM);
        if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) {
            logLocal("=========> submitted captcha id "+captchaIdReceivedForForm+" and session is: "+session);
        }
        if (session != null && captchaIdReceivedForForm != null) {
            try {
                Captcha captcha = (Captcha) ServerUtils.getAttributeIncludingInternal(session, SESSION_CAPTCHA_IMAGES+captchaIdReceivedForForm);
                // when it is null, check the eventual case that a user submitted the captcha form twice and reuse the previously last captcha (that was removed from the session during the first request of the double-submitted form)
                if (this.rememberLastCaptchaForMultiSubmits && captcha == null) {
                    captcha = (Captcha) ServerUtils.getAttributeIncludingInternal(session, SESSION_CAPTCHA_IMAGES+LAST_CAPTCHA);
                    if (captcha != null) {
                        // OK, when we've come here, the user has submitted the form multiple times simultaneously for a single captcha (and already invalidated it and removed it from the session) and this request is the second or maybe even 
                        // third request of the impatient user when the ID matches... so we simply let the request go and as the captcha is already invalidated, the user's answer is wrong, so he automatically gets a new fresh captcha to play with...
                        // check its ID
                        if (!captchaIdReceivedForForm.equals(captcha.getReferenceId())) {
                            captcha = null; // = wrong ID, so set again back to null
                        } else if (captcha.isExpired()) {
                            session.removeAttribute(SESSION_CAPTCHA_IMAGES+LAST_CAPTCHA);
                            captcha = null;
                        }
                    }
                }
                // when it is still null, there's someone attacking us
                if (captcha == null) {
                    final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "No captcha available for ID: "+captchaIdReceivedForForm);
                    return new AllowedFlagWithMessage(false, attack);
                }
                assert captcha != null;
                final String submittedCaptchaValue = request.getParameter(CAPTCHA_VALUE);
                originalParameterMap = captcha.getOriginalParameterMap(); // already safe params in case of multiple tests (when some captchas are too bad ro recognize by humans or someone enters a typo, we must take care for multi-step tests)
                if (captcha.isMatching(submittedCaptchaValue)) {
                    session.removeAttribute(SESSION_CAPTCHA_FAILED_COUNTER);
                    hasSuccessfullyPassedCaptchaTest = true;
                    // reconstruct parameters from original request
                    if (originalParameterMap != null) {
                        // OLD: don't clear since will be saved later as LAST_CAPTCHA: captcha.clearOriginalParameterMap();
                        for (final Iterator params = originalParameterMap.entrySet().iterator(); params.hasNext();) {
                            final Map.Entry param = (Map.Entry) params.next();
                            request.setParameter((String)param.getKey(), (String[])param.getValue(), true);
                        }
                    }
                } else {
                    // track failed captcha count
                    Integer failedCounter = (Integer) ServerUtils.getAttributeIncludingInternal(session, SESSION_CAPTCHA_FAILED_COUNTER);
                    if (failedCounter == null) failedCounter = new Integer(1); else failedCounter = new Integer( failedCounter.intValue()+1 );
                    assert failedCounter != null;
                    if (failedCounter.intValue() > this.failedCaptchaPerSessionAttackThreshold) {
                        session.removeAttribute(SESSION_CAPTCHA_FAILED_COUNTER);
                        final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Failed CAPTCHA limit exceeded: "+failedCounter);
                        return new AllowedFlagWithMessage(false, attack);
                    } else session.setAttribute(SESSION_CAPTCHA_FAILED_COUNTER, failedCounter);
                }
                // for the effect that the captcha form was submitted twice (and this is the first submit of a twice-submitted form), we simply remember the last captcha.... and its ID....
                if (this.rememberLastCaptchaForMultiSubmits) {
                    captcha.setReferenceId(captchaIdReceivedForForm);
                    session.setAttribute(SESSION_CAPTCHA_IMAGES+LAST_CAPTCHA, captcha);
                }
                // remove it from its ID-based storage
                session.removeAttribute(SESSION_CAPTCHA_IMAGES+captchaIdReceivedForForm); // remove used captcha from session; BUT this might cause the effect, that the second submit of a twice-submitted captcha-form-submit does not find the captcha anymore... but that's why we keep the last captcha around for a while
            } catch (Exception e) { // TODO: Hier ggf. die IllegalStateException abfangen, wenn bereits die session invalidiert wurde... aber was dann? dann auch als Attacke werten? oder auf Startseite leiten ?
                e.printStackTrace();
                // TODO: klar, dass wir hier den request ablehnen, da ein fehler im checking aufgetreten ist... aber sollten wir hier nicht vielleicht es vermeiden, dass der attack-counter hier auch inkrementiert wird und die session terminiert wird?
                // klar, dass es geblockt wird, aber es ist keine vollwertige attacke...
                final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Unable to work on request (exception during captcha form handling): "+e.getMessage());
                return new AllowedFlagWithMessage(false, attack);
            }
        }
        // =========================================================
        // Check for captcha image requests
        // =========================================================
        final String captchaIdReceivedForImage = request.getParameter(CAPTCHA_IMAGE);
        if (session != null && captchaIdReceivedForImage != null) {
            try {
                final Captcha captcha = (Captcha) ServerUtils.getAttributeIncludingInternal(session, SESSION_CAPTCHA_IMAGES+captchaIdReceivedForImage);
                if (captcha == null) {
                    final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "No captcha (image) available for ID: "+captchaIdReceivedForImage);
                    return new AllowedFlagWithMessage(false, attack);
                }
                if (response.isCommitted()) {
                    response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                    throw new StopFilterProcessingException("Unable to introduce captcha image into already committed response"); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                }  
                response.reset();
                response.setHeader("Cache-Control", "no-store");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0); // TODO: oder lieber -1 wie be AntiCacher-Header-Injection ?!?
                response.setContentType("image/"+captcha.getImageFormat());
                response.getOutputStream().write(captcha.getImage());
                response.flushBuffer();
                return new AllowedFlagWithMessage(false, captcha); // to stop further processing, since only the captcha should be sent back
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: klar, dass wir hier den request ablehnen, da ein fehler im checking aufgetreten ist... aber sollten wir hier nicht vielleicht es vermeiden, dass der attack-counter hier auch inkrementiert wird und die session terminiert wird?
                // klar, dass es geblockt wird, aber es ist keine vollwertige attacke...
                final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Unable to work on request (exception during captcha image handling): "+e.getMessage());
                return new AllowedFlagWithMessage(false, attack);
            }
        }
        // =========================================================
        // Check for captcha-points
        // =========================================================
        if (captchaPointMatch != null && !hasSuccessfullyPassedCaptchaTest) {
            // auto-create session if no session exists
            if (session == null) {
                session = request.getSession(true);
                this.attackHandler.logRegularRequestMessage("Auto-created web session on captcha-point: "+session.getId());
            }
            try {
                final Captcha captcha = this.captchaGenerator.generateCaptcha();
                // capture original request's parameters (also taking care of eventually removed hidden fields)
                // here we handle multi-step tests... on the first step, use the requests params, on all subsequent steps use the already rescued params
                final Map/*<String,String[]>*/ parameters;
                if (originalParameterMap == null) {
                    parameters = request.getParameterMap(); // fetch directly freshly (since hdden field and selectbx protection might already have changed it) instead of the old one: requestDetails.parameterMapExcludingInternalParams;
                    /* OLD
                    parameters = requestDetails.parameterMapExcludingInternalParams;
                    if (requestDetails.parameterMapOfPreviouslyRemovedHiddenFields != null) {
                        // also add the removed hidden fields as the originating request might come from a form that has hidden fields removed
                        // BUT add them in a non-overwriting way
                        concatenateParameterMaps(parameters, requestDetails.parameterMapOfPreviouslyRemovedHiddenFields);
                    }
                      */
                } else parameters = originalParameterMap;
                captcha.setOriginalParameterMap(parameters); 
                // place captcha into session
                final String captchaIdGenerated = CryptoUtils.generateRandomToken(true);
                session.setAttribute(SESSION_CAPTCHA_IMAGES+captchaIdGenerated, captcha);
                // render captcha page instead of letting the request pass
                if (response.isCommitted()) {
                    // TODO: testen ob im Falle eines bereits committeten Responses ein Rdirect noch moeglich ist? Sonst nur blocken und loggen halt ohne Redirect...
                    response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                    throw new StopFilterProcessingException("Unable to introduce captcha into already committed response"); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                }
                final String logReferenceId = "time "+System.currentTimeMillis();
                String message = captchaPointMatch.getHtmlContentLoaded();

                // create captcha image URL
                String imageURL = requestDetails.url+"?"+CAPTCHA_IMAGE+"="+captchaIdGenerated;
                imageURL = response.encodeURL(imageURL);

                // create captcha form URL
                String formURL = requestDetails.url;
                formURL = response.encodeURL(formURL);
                
                // replace placeholders in HTML template
                //OLD final String imageHTML = "<img width=\""+captcha.getImageWidth()+"\" height=\""+captcha.getImageHeight()+"\" src=\""+ServerUtils.escapeSpecialCharactersHTML(imageURL)+"\" border=\"1\" />";
                //OLD final String formHTML = "<form action=\""+ServerUtils.escapeSpecialCharactersHTML(formURL)+"\" method=\""+ServerUtils.escapeSpecialCharactersHTML(requestDetails.method)+"\"><input type=\"text\" name=\""+CAPTCHA_VALUE+"\" /><input type=\"submit\" value=\"OK\" /><input type=\"hidden\" name=\""+CAPTCHA_FORM+"\" value=\""+captchaIdGenerated+"\" /></form>";
                final String imageHTML = MessageFormat.format(captchaPointMatch.getCaptchaImageHTML(), new Object[]{ServerUtils.escapeSpecialCharactersHTML(imageURL), new Integer(captcha.getImageWidth()), new Integer(captcha.getImageHeight())});
                final String formHTML = MessageFormat.format(captchaPointMatch.getCaptchaFormHTML(), new Object[]{ServerUtils.escapeSpecialCharactersHTML(formURL),ServerUtils.escapeSpecialCharactersHTML(requestDetails.method),CAPTCHA_VALUE,CAPTCHA_FORM,captchaIdGenerated});
                message = message.replaceAll("\\$\\{id\\}", ServerUtils.quoteReplacement(ServerUtils.escapeSpecialCharactersHTML(logReferenceId)));
                message = message.replaceAll("\\$\\{image\\}", ServerUtils.quoteReplacement(imageHTML));
                message = message.replaceAll("\\$\\{form\\}", ServerUtils.quoteReplacement(formHTML));
                response.reset(); // TODO: diese hier oefters auftretenden vier zeilen: reset+Cache-Control+Pragma+Expires in ResponseUtils auslagern ud jeweils aufrufen
                response.setHeader("Cache-Control", "no-store");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0); // TODO: oder lieber -1 wie be AntiCacher-Header-Injection ?!?
                response.setContentType("text/html");
                response.getWriter().write(message);
                response.flushBuffer();
                return new AllowedFlagWithMessage(false, captcha); // to stop further processing, since only the captcha should be sent back
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: klar, dass wir hier den request ablehnen, da ein fehler im checking aufgetreten ist... aber sollten wir hier nicht vielleicht es vermeiden, dass der attack-counter hier auch inkrementiert wird und die session terminiert wird?
                // klar, dass es geblockt wird, aber es ist keine vollwertige attacke...
                final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Unable to work on request (exception during captcha handling): "+e.getMessage());
                return new AllowedFlagWithMessage(false, attack);
            }
        }
                
        
        
        
        
        
        
        
        
        
        
        
        // =========================================================
        // When we've come here, everything is fine and we welcome the user... 
        // "...here is your welcome cocktail...."
        // =========================================================
        if (this.debug) logLocal("WebCastellum:doBeforeProcessing --- end");
        this.attackHandler.handleRegularRequest(request, requestDetails.clientAddress);
        return new AllowedFlagWithMessage(true);
    }
    
    
    

    private AllowedFlagWithMessage selectboxCheckboxRadiobuttonProtection(final HttpSession session, final RequestDetails requestDetails, final RequestWrapper request, final ResponseWrapper response, final Map/*<String,List<String>>*/ fieldsName2AllowedValues, 
                                                            final boolean isProtectionEnabled, final boolean isMaskingEnabled, final String maskingPrefixSessionKey, final boolean isIncomingFieldProtectionExclude) throws StopFilterProcessingException, IOException {
        // Here we uncover and check the select/check/radiobox-protected values (anyway even when isIncomingSelect/Check/RadioboxFieldProtectionExclude is true):
        // BUT: On all "treat as attack/spoofing" things here only treat as attack/spoofing when isIncomingSelect/Check/RadioboxFieldProtectionExclude is false
        if (isProtectionEnabled) {
            if (fieldsName2AllowedValues != null && !fieldsName2AllowedValues.isEmpty()) {
                final String maskingPrefix = (String) ServerUtils.getAttributeIncludingInternal(session, maskingPrefixSessionKey);
                for (final Iterator entries = fieldsName2AllowedValues.entrySet().iterator(); entries.hasNext();) {
                    final Map.Entry/*<String,List<String>>*/ entry = (Map.Entry) entries.next();
                    final String fieldName = (String) entry.getKey();
                    final List/*<String>*/ allowedValues = (List) entry.getValue();
                    final String[] submittedValues = request.getParameterValues(fieldName);
                    if (submittedValues != null && submittedValues.length > 0) {
                        // work on this field:
                        if (isMaskingEnabled) { // uncover (when they were masked)
                            if (maskingPrefix == null) { // spoofing, since the prefix must be in the session
                                if (!isIncomingFieldProtectionExclude) {
                                    // potential select/check/radiobox field tampering (spoofing) detected
                                    if (this.redirectWelcomePage.length() == 0) {
                                        // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                                        final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Parameter and/or form manipulation (client provided mismatching request parameter value for selectbox/checkbox/radiobutton field '"+fieldName+"') - and no redirect welcome page configured");
                                        return new AllowedFlagWithMessage(false, attack);
                                    } else {
                                        final String message = "Client provided mismatching request parameter value for selectbox/checkbox/radiobutton field '"+fieldName+"'\n\tUnable to locate the random masking prefix in the session.\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                                        try {
                                            if (session != null) {
                                                session.invalidate();
                                                //session = null;
                                            }
                                        } catch (IllegalStateException ignored) {}
                                        response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                                        throw new StopFilterProcessingException(message); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                                    }
                                }
                            }
                            final String[] uncoveredValues = new String[submittedValues.length];
                            for (int i=0; i<submittedValues.length; i++) {
                                try {
                                    // check if the prefix is there and strip it off from the id
                                    if (maskingPrefix != null) {
                                        if (submittedValues[i].indexOf(maskingPrefix) == 0) { // found prefix at first position
                                            // strip it off
                                            submittedValues[i] = submittedValues[i].substring(maskingPrefix.length());
                                        } else { // spoofing, since the prefix must be at there and at the first position
                                            if (!isIncomingFieldProtectionExclude) {
                                                // potential select/check/radiobox field tampering (spoofing) detected
                                                if (this.redirectWelcomePage.length() == 0) {
                                                    // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                                                    final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Parameter and/or form manipulation (client provided mismatching request parameter value for selectbox/checkbox/radiobutton field '"+fieldName+"') - and no redirect welcome page configured");
                                                    return new AllowedFlagWithMessage(false, attack);
                                                } else {
                                                    final String message = "Client provided mismatching request parameter value for selectbox/checkbox/radiobutton field '"+fieldName+"'\n\tUnable to locate the random masking prefix in the submitted identifier.\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                                                    try {
                                                        if (session != null) {
                                                            session.invalidate();
                                                            //session = null;
                                                        }
                                                    } catch (IllegalStateException ignored) {}
                                                    response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                                                    throw new StopFilterProcessingException(message); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                                                }
                                            }
                                        }
                                    }
                                    // when we've come here, and select/check/radioBoxMaskingPrefix is still null, then it is on an incoming protection exclude
                                    final int submittedId = Integer.parseInt(submittedValues[i]);
                                    final String uncoveredValue = (String) allowedValues.get(submittedId);
                                    uncoveredValues[i] = uncoveredValue;
                                } catch (NumberFormatException e) { // = someone has spoofed the form field by altering the masked index into something non-numeric
                                    if (isIncomingFieldProtectionExclude) {
                                        uncoveredValues[i] = submittedValues[i]; // simply use the submitted value since it is an incoming protection exclude here
                                    } else {
                                        // potential select/check/radiobox field tampering (spoofing) detected
                                        if (this.redirectWelcomePage.length() == 0) {
                                            // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                                            final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Parameter and/or form manipulation (client provided mismatching request parameter value for selectbox/checkbox/radiobutton field '"+fieldName+"') - and no redirect welcome page configured");
                                            return new AllowedFlagWithMessage(false, attack);
                                        } else {
                                            final String message = "Client provided mismatching request parameter value for selectbox/checkbox/radiobutton field '"+fieldName+"'\n\tUnable to number-parse the submitted identifier.\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                                            try {
                                                if (session != null) {
                                                    session.invalidate();
                                                    //session = null;
                                                }
                                            } catch (IllegalStateException ignored) {}
                                            response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                                            throw new StopFilterProcessingException(message); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                                        }
                                    }
                                } catch (IndexOutOfBoundsException e) { // = someone has spoofed the form field by altering the masked index into something out of the number range of allowed values (allowed indexes)
                                    if (isIncomingFieldProtectionExclude) {
                                        uncoveredValues[i] = submittedValues[i]; // simply use the submitted value since it is an incoming protection exclude here
                                    } else {
                                        // potential select/check/radiobox field tampering (spoofing) detected
                                        if (this.redirectWelcomePage.length() == 0) {
                                            // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                                            final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Parameter and/or form manipulation (client provided mismatching request parameter value for selectbox/checkbox/radiobutton field '"+fieldName+"') - and no redirect welcome page configured");
                                            return new AllowedFlagWithMessage(false, attack);
                                        } else {
                                            final String message = "Client provided mismatching request parameter value for selectbox/checkbox/radiobutton field '"+fieldName+"'\n\tThe submitted identifier is out of range of allowed values.\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                                            try {
                                                if (session != null) {
                                                    session.invalidate();
                                                    //session = null;
                                                }
                                            } catch (IllegalStateException ignored) {}
                                            response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                                            throw new StopFilterProcessingException(message); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                                        }
                                    }
                                }
                            }
                            // write the successfully uncovered values into the request (overwriting the submitted values)
                            request.setParameter(fieldName, uncoveredValues, true);
                            requestDetails.somethingHasBeenUncovered = true;
                        } else { // no uncovering, so at least check against mismatches
                            if (!isIncomingFieldProtectionExclude) {
                                for (String submittedValue : submittedValues) {
                                    if (!allowedValues.contains(submittedValue)) {
                                        // potential select/check/radiobox field tampering (spoofing) detected
                                        if (this.redirectWelcomePage.length() == 0) {
                                            // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                                            final Attack attack = this.attackHandler.handleAttack(request, requestDetails.clientAddress, "Parameter and/or form manipulation (client provided mismatching request parameter value for selectbox/checkbox/radiobutton field '"+fieldName+"') - and no redirect welcome page configured");
                                            return new AllowedFlagWithMessage(false, attack);
                                        } else {
                                            final String message = "Client provided mismatching request parameter value for selectbox/checkbox/radiobutton field '"+fieldName+"'\n\tAllowed: "+allowedValues+"\n\tActually received from client: "+Arrays.asList(submittedValues)+"\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                                            try {
                                                if (session != null) {
                                                    session.invalidate();
                                                    //session = null;
                                                }
                                            } catch (IllegalStateException ignored) {}
                                            response.sendRedirectDueToRecentAttack( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                                            throw new StopFilterProcessingException(message); // = don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null; // = indicating a "please just continue normally" message to the caller
    }
    
    
    
    
    
    private void doAfterProcessing(final RequestWrapper request, final ResponseWrapper response) throws IOException, ServletException {
        if (this.debug) logLocal("WebCastellum:doAfterProcessing --- begin");
        final int statusCode = response.getCapturedStatus();
        final String ip = RequestUtils.determineClientIp(request,this.clientIpDeterminator);
        if (this.debug) {
            logLocal("WebCastellum:doAfterProcessing current response status code (up to here): "+statusCode);
        }
        // check honeylink access
        if (this.honeylinkMaxPerPage > 0 
                && (statusCode == HttpServletResponse.SC_BAD_REQUEST || statusCode == HttpServletResponse.SC_NOT_FOUND)) {
            String servletPathOrRequestURI = request.getServletPath();
            if (servletPathOrRequestURI == null) servletPathOrRequestURI = request.getRequestURI();
            if (HoneylinkUtils.isHoneylinkFilename(servletPathOrRequestURI)) {
                this.attackHandler.handleAttack(request, ip, "Potential honeylink accessed");
            }
        }
        // track status code
        this.httpStatusCodeCounter.trackStatusCode(ip, statusCode, request);
        // determine content type
        String contentTypeUpperCased = response.extractContentTypeUpperCased();
        if (contentTypeUpperCased == null) contentTypeUpperCased = "NULL"; // yes, the word "NULL" in configuration means "match with an unset content-type" here...so we set the variable to check against to the word "NULL" here
        contentTypeUpperCased = contentTypeUpperCased.trim();
        // set anti-cache headers
        if (this.antiCacheResponseHeaderInjectionContentTypes != null && this.antiCacheResponseHeaderInjectionContentTypes.size() > 0) {
            // check if it matches
            if ( this.antiCacheResponseHeaderInjectionContentTypes.contains(contentTypeUpperCased) ) {
                try {
                    if (!response.isCommitted()) {
                        // using the "setHeader"-methods instead of the "addHeader"-methods so that existing headers (eventually set by the application) will get overwritten by the following new values
                        response.setDateHeader("Expires", -1); // Causes the proxy cache to see the page as "stale"
                        response.setHeader("Pragma","no-cache"); // HTTP 1.0 backward compatibility
                        response.setHeader("Cache-Control","no-store"); // prevent storing in cache
                    }
                } catch (RuntimeException e) {
                    this.attackHandler.logWarningRequestMessage("Unable to send anti-cache headers: "+e.getMessage());
                }
            }
        }
        if (this.debug) logLocal("WebCastellum:doAfterProcessing ================================== end");
    }
    
    
    
    
    
    
    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        // check if total-exclude match:
        if (this.totalExcludeDefinitions != null && this.totalExcludeDefinitions.hasEnabledDefinitions() && request instanceof HttpServletRequest) {
            final HttpServletRequest httpRequest = (HttpServletRequest) request;
            final String servletPath = httpRequest.getServletPath();
            final String requestURI = httpRequest.getRequestURI();
            if ( this.totalExcludeDefinitions.isTotalExclude(servletPath,requestURI) ) {
                // TOTAL EXCLUDE: SO DON'T DO ANY SECURITY STUFF HERE, JUST LET THE REQUEST PASS - AT THE CUSTOMER'S OWN WILL AND RISK
                chain.doFilter(request, response); // TODO: log this total-exlucde access in the log-file for revision safeness !!
                return;
            }
        }
        
        
        // apply full security filter stuff (i.e. NO total-exclude):
        if (this.catchAll) {
            try {
                internalDoFilter(request, response, chain);
            } catch (Exception e) {
                logLocal("Uncaught exception: "+e.getClass().getName()+": "+e.getMessage()); // TODO: hier auch den root-cause loggen !
                try {
                    sendUncaughtExceptionResponse((HttpServletResponse)response, e);
                } catch (Exception e2) {
                    logLocal("Uncaught exception during return of exception message: "+e2.getClass().getName()+": "+e2.getMessage());
                }
            }
        } else {
            internalDoFilter(request, response, chain);
        }
    }
    
    
    private void internalDoFilter(ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final long timerBefore = System.currentTimeMillis();

        if (Boolean.TRUE.equals(request.getAttribute(REQUEST_NESTED_FORWARD_CALL))) { // NOTE: the request and response can be of any type here, since some applications even wrap further and add another wrapper over our wrappers
            chain.doFilter(request, response);
            return; // = avoid being called in nested (forwarded) pages too (of course still working on decrypted requests since decryption removes this flag)
        }
        
        
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        // Check for this filter's readyness (possibility to reload config even during application's lifetime)
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        {
            try {
                if (this.configReloadingIntervalMillis > 0 && System.currentTimeMillis() > this.nextConfigReloadingTime && this.nextConfigReloadingTime > 0) {
                    this.nextConfigReloadingTime += this.configReloadingIntervalMillis;
                    registerConfigReloadOnNextRequest();
                }
                restartCompletelyWhenRequired();
            } catch (Exception e) {
                sendUnavailableMessage((HttpServletResponse)response, e);
                return;
            }
        }
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        // Reload rules when required (possibility to reload rules even during application's lifetime)
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        {
            try {
                if (this.ruleFileReloadingIntervalMillis > 0 && System.currentTimeMillis() > this.nextRuleReloadingTime && this.nextRuleReloadingTime > 0) {
                    this.nextRuleReloadingTime += this.ruleFileReloadingIntervalMillis;
                    registerRuleReloadOnNextRequest();
                }
                reloadRulesWhenRequired();
            } catch (Exception e) {
                sendUnavailableMessage((HttpServletResponse)response, e);
                return;
            }
        }
        
        
        
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        // =========================================================
        // Define request character encoding (usually UTF-8) !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ==> THIS MUST BE THE VERY FIRST THING TO TOUCH THE REQUEST !!!!!
        // =========================================================
        if (this.debug) logLocal("Original request character encoding: "+request.getCharacterEncoding());
        if (this.requestCharacterEncoding != null && this.requestCharacterEncoding.length() > 0) {
            try {
                // IMPORTANT: set the encoding here as early as possible BEFORE ANY params are read,
                // otherwise the request.setCharacterEncoding() won't work when already request parameters have been read !!!
                request.setCharacterEncoding(this.requestCharacterEncoding);
                if (this.debug) logLocal("Request character encoding set to: "+this.requestCharacterEncoding);
            } catch (UnsupportedEncodingException e) { // = wrong configuration
                this.attackHandler.handleRegularRequest(httpRequest, RequestUtils.determineClientIp(httpRequest, this.clientIpDeterminator)); // = since we're about to stop this request, we log it here
                this.attackHandler.logWarningRequestMessage("Desired stop in filter processing of previously logged request: "+"Unsupported request character encoding configured for WebCastellum: "+this.requestCharacterEncoding);
                sendUnavailableMessage((HttpServletResponse)response, e);
                return; // = stop processing as desired :-)
            }
        }
        
        
        

        if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) {
            logLocal("---------------------- "+new Date());
            logLocal("1A param map: "+httpRequest.getParameterMap().keySet());
            logLocal("1A query string: "+httpRequest.getQueryString());
            logLocal("1A servlet path: "+httpRequest.getServletPath());
        }
        
        
        
        
        
        


        
        
        

            
        final String clientAddress = RequestUtils.determineClientIp(httpRequest, this.clientIpDeterminator);
        HttpSession session = httpRequest.getSession(false);
        
        
        // =========================================================
        // Check for blocked IPs (due to too many recent attacks)
        // =========================================================
        if (this.attackHandler.shouldBeBlocked(clientAddress)) {
            // direkt schon hier als Attack werten, aber ohne es dem AttackHandler zum mitzaehlen zu geben, da bereits geblockt
            sendDisallowedResponse((HttpServletResponse)response, new Attack("Client is temporarily blocked: "+clientAddress));
            return;
        }

        
        
        
        
        
        
        
        // ===== PER-REQUEST CHECKING STUFF STARTS HERE:
        
        
        if (this.validateClientAddressFormat && clientAddress != null && !PATTERN_VALID_CLIENT_ADDRESS.matcher(clientAddress).find()) {
            final String message = "Strange client address (nothing found matching the expected pattern of client address) - not incrementing attack-counter since tracking attacks using strange IP addresses is useless; simply blocking the request: "+clientAddress;
            this.attackHandler.logWarningRequestMessage(message);
            final Attack attack = new Attack(message);
            final HttpServletResponse httpResponse = (HttpServletResponse) response;
            sendDisallowedResponse(httpResponse, attack);
            return; // = stop processing as desired :-)
        }
        
        
        
        
        
        
        final String servletPath = httpRequest.getServletPath();
        final String requestURI = httpRequest.getRequestURI();
        final String queryString = httpRequest.getQueryString();        
        
        
        // =========================================================
        // Check against allowed request mime types
        // =========================================================
        if (this.allowedRequestMimeTypesLowerCased != null && !this.allowedRequestMimeTypesLowerCased.isEmpty()) {
            final String mimeType = httpRequest.getContentType();
            if (mimeType != null) {
                String mimeTypeUpToFirstSemicolon = mimeType;
                final int pos = mimeType.indexOf(';');
                if (pos != -1) mimeTypeUpToFirstSemicolon = mimeType.substring(0, pos);
                if (!this.allowedRequestMimeTypesLowerCased.contains(mimeTypeUpToFirstSemicolon.toLowerCase().trim())) {
                    final Attack attack = this.attackHandler.handleAttack(httpRequest, clientAddress, "Mime type of request not allowed (configurable): "+mimeType);
                    final HttpServletResponse httpResponse = (HttpServletResponse) response;
                    sendDisallowedResponse(httpResponse, attack);
                    return; // = stop processing as desired :-)
                }
            }
        }
        
        

        // =========================================================
        // Parsing of multipart forms if required to do so
        // =========================================================
        MultipartServletRequest multikulti = null;
        if (request instanceof MultipartServletRequest) { // = FORWARDED DUE TO DECRYPTION
            multikulti = (MultipartServletRequest) request;
            // RE-EXTRACT the URL params !!!! due to requestDispatcher.forward() thingie
            multikulti.reextractSubmittedUrlValues();
            if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) {
                final HttpServletRequest innerDelegate = (HttpServletRequest) multikulti.getRequest();
                logLocal("1B[*inner-delegate-of-multipart*] param map: "+innerDelegate.getParameterMap().keySet());
                logLocal("1B[*inner-delegate-of-multipart*] query string: "+innerDelegate.getQueryString());
                logLocal("1B[*inner-delegate-of-multipart*] servlet path: "+innerDelegate.getServletPath());
            }
        } else { // = FRESH REQUEST, FIRST CONTACT
            if (this.parseMultipartForms) {
                // check if it is a multipart form submission
                if (this.multipartRequestParser.isMultipartRequest(httpRequest)) {
                    MultipartSizeLimitDefinition multipartSizeLimit = null;
                    // Fetch limits for the multipart-size-limits checking
                    if (this.multipartSizeLimitDefinitions != null && this.multipartSizeLimitDefinitions.hasEnabledDefinitions()) {
                        multipartSizeLimit = this.multipartSizeLimitDefinitions.getMatchingMultipartSizeLimitDefinition(servletPath, requestURI);
                    }
                    if (multipartSizeLimit != null && !multipartSizeLimit.isMultipartAllowed()) {
                        final Attack attack = this.attackHandler.handleAttack(httpRequest, clientAddress, "Multipart request not allowed for current resource (see multipart rule file for opening this)");
                        final HttpServletResponse httpResponse = (HttpServletResponse) response;
                        sendDisallowedResponse(httpResponse, attack);
                        return; // = stop processing as desired :-)
                    }
                    // parse it - checking stream length limit
                    multikulti = new MultipartServletRequest(this.multipartRequestParser, httpRequest, multipartSizeLimit, bufferFileUploadsToDisk);
                    httpRequest = multikulti; // in order to have also the variable "httpRequest" point to the multipart wrapper
                    request = multikulti; // in order to have also the variable "request" point to the multipart wrapper
                }
            }
        }
                
        
        
        
        // =========================================================
        // Calculate sizes for the size-limits checking
        // =========================================================
        if (this.sizeLimitDefinitions != null && this.sizeLimitDefinitions.hasEnabledDefinitions()) {
            // Header
            int headerCount = 0;
            int totalHeaderSize = 0;
            int greatestHeaderNameLength = 0;
            int greatestHeaderValueLength = 0;
            final Enumeration names = httpRequest.getHeaderNames();
            if (names != null) {
                while (names.hasMoreElements()) {
                    final String name = (String) names.nextElement();
                    headerCount++;
                    if (name == null) continue;
                    final int nameLength = name.length();
                    greatestHeaderNameLength = Math.max(greatestHeaderNameLength, nameLength);
                    totalHeaderSize += nameLength;
                    final Enumeration values = httpRequest.getHeaders(name);
                    if (values != null) {
                        while (values.hasMoreElements()) {
                            final String value = (String) values.nextElement();
                            if (value == null) continue;
                            final int valueLength = value.length();
                            greatestHeaderValueLength = Math.max(greatestHeaderValueLength, valueLength);
                            totalHeaderSize += valueLength;
                        }
                    } else logLocal("Container does not allow to access header information");
                }
            } else logLocal("Container does not allow to access header information");
            // Cookie
            int cookieCount = 0;
            int totalCookieSize = 0;
            int greatestCookieNameLength = 0;
            int greatestCookieValueLength = 0;
            final Cookie[] cookies = httpRequest.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    cookieCount++;
                    final String comment = cookie.getComment();
                    final String domain = cookie.getDomain();
                    final String path = cookie.getPath();
                    final String name = cookie.getName();
                    final String value = cookie.getValue();
                    if (comment != null) totalCookieSize += comment.length();
                    if (domain != null) totalCookieSize += domain.length();
                    if (path != null) totalCookieSize += path.length();
                    if (name != null) {
                        final int length = name.length();
                        greatestCookieNameLength = Math.max(greatestCookieNameLength, length);
                        totalCookieSize += length;
                    }
                    if (value != null) {
                        final int length = value.length();
                        greatestCookieValueLength = Math.max(greatestCookieValueLength, length);
                        totalCookieSize += length;
                    }
                }
            }
            // Request-Param
            int requestParamCount = 0;
            int totalRequestParamSize = 0;
            int greatestRequestParamNameLength = 0;
            int greatestRequestParamValueLength = 0;
            for (final Enumeration paramNames = httpRequest.getParameterNames(); paramNames.hasMoreElements();) {
                final String name = (String) paramNames.nextElement();
                requestParamCount++;
                if (name == null) continue;
                final int nameLength = name.length();
                greatestRequestParamNameLength = Math.max(greatestRequestParamNameLength, nameLength);
                totalRequestParamSize += nameLength;
                final String[] values = httpRequest.getParameterValues(name);
                if (values == null) continue;
                for (String value : values) {
                    final int valueLength = value.length();
                    greatestRequestParamValueLength = Math.max(greatestRequestParamValueLength, valueLength);
                    totalRequestParamSize += valueLength;
                }
            }
            // Query-String
            final int queryStringLength = queryString == null ? 0 : queryString.length();

            // limit exceeded ?
            if ( this.sizeLimitDefinitions.isSizeLimitExceeded(servletPath,requestURI,
                    headerCount, cookieCount, requestParamCount, 
                    queryStringLength,
                    greatestHeaderNameLength, greatestHeaderValueLength, totalHeaderSize, 
                    greatestCookieNameLength, greatestCookieValueLength, totalCookieSize, 
                    greatestRequestParamNameLength, greatestRequestParamValueLength, totalRequestParamSize) ) {
                final String message = "Size limit exceeded by request (therefore not logging details)";
                this.attackHandler.logWarningRequestMessage(message); // TODO: zusaetzlich noch dafuer sorgen (handleAttack ?) dass der Attack-Counter inkrementiert wird
                final Attack attack = new Attack(message);
                final HttpServletResponse httpResponse = (HttpServletResponse) response;
                sendDisallowedResponse(httpResponse, attack);
                return; // = stop processing as desired :-)
            }
        }        
                
        
        
        
        
        
        
        
        
        if (this.debug) logLocal("doFilter on "+request.getClass().getName());
        if (this.debug) logLocal("... with URL "+httpRequest.getRequestURL()+" and query string "+queryString);
        if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("2: "+httpRequest.getParameterMap().keySet());
        
        
        
        // =========================================================
        // check forced-session-invalidation
        // =========================================================
        if (session != null && this.forcedSessionInvalidationPeriodMinutes > 0) {
            final long forcedCutoff = session.getCreationTime() + (this.forcedSessionInvalidationPeriodMinutes * 60 * 1000L);
            if ( System.currentTimeMillis() > forcedCutoff ) {
                this.attackHandler.logWarningRequestMessage("Forced session invalidation: "+session.getId());
                try {
                    if (session != null) {
                        session.invalidate();
                        session = null;
                    }
                } catch (IllegalStateException ignored) {}
                final HttpServletResponse httpResponse = (HttpServletResponse) response;
                if (this.redirectWelcomePage.length() == 0) {
                    // send disallowed response without AttackHandler counting (i.e. we directly create the Attack object), since the session will be invalidated here (was done above) any way, so AttackHandler does not need to check if session-invalidation-on-attack is defined
                    final Attack attack = new Attack("Maximum session lifetime exceeded ("+(this.forcedSessionInvalidationPeriodMinutes*60)+" seconds) - and no redirect welcome page configured");
                    sendDisallowedResponse(httpResponse, attack);
                } else {
                    final String message = "Maximum session lifetime exceeded ("+(this.forcedSessionInvalidationPeriodMinutes*60)+" seconds)\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                    // Session was already invalidated above
                    // response.sendRedirectDueToRecentAttack is not yet possible, since the response is not yet wrapped (will be wrapped below), so it is safe to use standard response.sendRedirect here
                    httpResponse.sendRedirect( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                    // don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                    this.attackHandler.handleRegularRequest(httpRequest, clientAddress); // = since we're about to stop this request, we log it here
                    this.attackHandler.logWarningRequestMessage("Desired stop in filter processing of previously logged request: "+message);
                }
                return; // = stop processing as desired :-)
            }
        }
        
        
        

        // SESSION AUTO-CREATE
        
        // create or retrieve session-based crypto stuff - for later use (see below)
        String cryptoDetectionString = null; CryptoKeyAndSalt cryptoKey = null; Cipher cipher = null;
        if (/*!isMatchingOutgoingResponseModificationExclusion &&*/ this.contentInjectionHelper.isEncryptQueryStringInLinks()) {
            try {
                if (session == null) {
                    session = httpRequest.getSession(true);
                }
                assert session != null;
                cryptoDetectionString = RequestUtils.createOrRetrieveRandomTokenFromSession(session, SESSION_ENCRYPT_QUERY_STRINGS_CRYPTODETECTION_KEY);
                cipher = CryptoUtils.getCipher();
                cryptoKey = RequestUtils.createOrRetrieveRandomCryptoKeyFromSession(session, SESSION_ENCRYPT_QUERY_STRINGS_CRYPTOKEY_KEY, this.extraEncryptedValueHashProtection);
            } catch (Exception e) {
                this.attackHandler.logWarningRequestMessage("Unable to define protection content in session: "+e.getMessage());
                try {
                    if (session != null) {
                        session.invalidate();
                        session = null;
                    }
                } catch (IllegalStateException ignored) {}
                // direkt schon hier als Attack werten, aber ohne es dem AttackHandler zum mitzaehlen zu geben
                sendDisallowedResponse((HttpServletResponse)response, new Attack("Unable to define protection content in session"));
                return;
            }
        }
        // create or retrieve session-based request tokens - for later use (see below)
        String secretTokenKey = null, secretTokenValue = null;
        if (/*!isMatchingOutgoingResponseModificationExclusion &&*/ this.contentInjectionHelper.isInjectSecretTokenIntoLinks()) {
            try {
                if (session == null) {
                    session = httpRequest.getSession(true);
                }
                assert session != null;
                secretTokenKey = RequestUtils.createOrRetrieveRandomTokenFromSession(session, SESSION_SECRET_RANDOM_TOKEN_KEY_KEY);
                secretTokenValue = RequestUtils.createOrRetrieveRandomTokenFromSession(session, SESSION_SECRET_RANDOM_TOKEN_VALUE_KEY);
            } catch (Exception e) {
                this.attackHandler.logWarningRequestMessage("Unable to define protection content in session: "+e.getMessage());
                try {
                    if (session != null) {
                        session.invalidate();
                        session = null;
                    }
                } catch (IllegalStateException ignored) {}
                // direkt schon hier als Attack werten, aber ohne es dem AttackHandler zum mitzaehlen zu geben
                sendDisallowedResponse((HttpServletResponse)response, new Attack("Unable to define protection content in session"));
                return;
            }
        }
        // now the key for param-and-form protection keys - for later use (see below)
        String parameterAndFormProtectionKeyKey = null;//, parameterAndFormProtectionEmptyValue = null;
        if (/*!isMatchingOutgoingResponseModificationExclusion &&*/ this.contentInjectionHelper.isProtectParametersAndForms()) {
            try {
                if (session == null) {
                    session = httpRequest.getSession(true);
                }
                assert session != null;
                parameterAndFormProtectionKeyKey = RequestUtils.createOrRetrieveRandomTokenFromSession(session, SESSION_PARAMETER_AND_FORM_PROTECTION_RANDOM_TOKEN_KEY_KEY); // yes, it is the key of the key
                //OLD parameterAndFormProtectionEmptyValue = ServerUtils.findReusableSessionContentKeyOrCreateNewOne(session, ParameterAndFormProtection.EMPTY);
            } catch (Exception e) {
                this.attackHandler.logWarningRequestMessage("Unable to define protection content in session: "+e.getMessage());
                try {
                    if (session != null) {
                        session.invalidate();
                        session = null;
                    }
                } catch (IllegalStateException ignored) {}
                // direkt schon hier als Attack werten, aber ohne es dem AttackHandler zum mitzaehlen zu geben
                sendDisallowedResponse((HttpServletResponse)response, new Attack("Unable to define protection content in session"));
                return;
            }
        }


        if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("3: "+httpRequest.getParameterMap().keySet());
        
        
        // ########################################################
        // Try to decrypt request since it might be encrypted
        // ########################################################
        if (DEBUG_PRINT_UNCOVERING_DETAILS) logLocal("cryptoDetectionString="+cryptoDetectionString);
        if (this.contentInjectionHelper.isEncryptQueryStringInLinks() && cryptoDetectionString != null && cryptoKey != null
                && request.getAttribute(REQUEST_ALREADY_DECRYPTED_FLAG) == null) { // = potentially encrypted (and not yet via "requestDispatcher.forward" decrypt handled) request - i.e. encryption is active ==> Note that here request.getAttribute and not getParameter is used !! This is important !
            try {
                // if the request contains a secret-token *already unencrypted* (via URL or FORM), we can assume some spoofings, since the real secret token sits inside the encrypted value (when encryption is active),
                // because every link that gets a secret-token injected also gets encrypted when both is activated
                if (this.contentInjectionHelper.isInjectSecretTokenIntoLinks() && secretTokenKey != null && request.getParameter(secretTokenKey) != null) {
                    throw new ServerAttackException("Additional (unencrypted) secret token parameter provided (potential spoofing)");
                }
                // if the request contains a protection-token *already unencrypted* (via URL or FORM), we can assume some spoofings, since the real protection token sits inside the encrypted value (when encryption is active),
                // because every link that gets a protection-token injected also gets encrypted when both is activated
                if (this.contentInjectionHelper.isProtectParametersAndForms() && parameterAndFormProtectionKeyKey != null && request.getParameter(parameterAndFormProtectionKeyKey) != null) {
                    throw new ServerAttackException("Additional (unencrypted) parameter-and-form protection token parameter provided (potential spoofing)");
                }


                // Prepare for a query-string adjustment: when a form field submits the crypto value and the form has an empty action url 
                // in which case the form submits to itself (current url; a.k.a self-submit) the URL params of the current URL are also reused
                // by the browser on the self-submit... That means that (potentially different) crypto values are present in the URL and the FORM submit.
                // When that is the case, the FORM submitted value has precedence and the URL's query-string must get the crypto-value removed (as
                // it is an old one)...
                String queryStringEventuallyAdjusted = httpRequest.getQueryString();
                String queryStringContentToRemove = null;

                // OK, try to decrypt the potentially encrypted request
                // ==============================
                // At first loop over the request parameter names to check if there is one parameter with the cryptoDetectionString in its name...
                // In case there are two ones (this could be the case when a self-submit form with an empty action value submits against the 
                // current URL where then the browser also uses the current page's URL params as params for the self-submit) we have to use
                // the form-field submitted value instead of the URL value... 
                String encryptedParam = null;
                boolean hasAdditionalParameters = false;
                for (final Enumeration paramNames = httpRequest.getParameterNames(); paramNames.hasMoreElements();) {
                    final String paramName = (String) paramNames.nextElement();
                    if (DEBUG_PRINT_UNCOVERING_DETAILS) logLocal("paramName="+paramName);
                    if (paramName.contains(cryptoDetectionString)) {
                        final String value = httpRequest.getParameter(paramName);
                        // check for multiple encrypted tokens
                        if (encryptedParam != null) { // = we already have one
                            // Now we have to check if the current one is a form-field one (i.e. not part of the query-string)... 
                            // So when the current one is a form-field submitted crypto value, use that instead of the previous one...  for example on action-less GET-based forms that are submitted to their URLs back inclusive their params (at least most of the time)
                            // OLD final boolean cameViaFormField = queryStringEventuallyAdjusted == null || !queryStringEventuallyAdjusted.contains(paramName); // This may require *random* positions of the crypto detection string in the name on each link + also watch out for url-encoding issues?
                            final boolean cameViaFormField = value != null && INTERNAL_TYPE_FORM.equals(value);
                            if (DEBUG_PRINT_UNCOVERING_DETAILS) {
                                logLocal("queryStringEventuallyAdjusted=   "+queryStringEventuallyAdjusted);
                                logLocal("cameViaFormField="+cameViaFormField);
                            }
                            if (cameViaFormField) {
                                if (queryStringContentToRemove != null) {
                                    throw new ServerAttackException("Multiple (more than the two allowed URL vs. FORM) crypto values provided (potential spoofing): "+paramName);
                                }
                                // use it (see below) and set a flag that the queryString has to be adjusted by removing any crypto URL content from the queryString
                                queryStringContentToRemove = encryptedParam;
                            } else {
                                // here the FORM field submitted value came as the first param and so the current param is the URL-borne... 
                                // which must get removed, since FORM-borne crypto params have precedence
                                queryStringContentToRemove = paramName;
                                continue; // = continue directly and don't use it in below's code
                            }
                        }
                        if (APPEND_EQUALS_SIGN_TO_VALUELESS_URL_PARAM_NAMES) {
                            if ( value == null || (!value.equals(INTERNAL_TYPE_URL) && !value.equals(INTERNAL_TYPE_FORM)) ) {
                                throw new ServerAttackException("Missing or wrong value for encrypted name-only parameter provided (potential spoofing): "+value);
                            }
                        } else {
                            if ( value != null && value.trim().length() > 0 ) {
                                throw new ServerAttackException("Value for encrypted name-only parameter provided (potential spoofing): "+value);
                            }
                        }
                        // OK, take it
                        encryptedParam = paramName;
                    } else hasAdditionalParameters = true;
                }
                if (DEBUG_PRINT_UNCOVERING_DETAILS) logLocal("encryptedParam="+encryptedParam);
                if (queryStringContentToRemove != null) {
                    // remove any crypto value from the queryString
//                    logLocal("queryStringEventuallyAdjusted pre adjustment ="+queryStringEventuallyAdjusted);
                    queryStringEventuallyAdjusted = ServerUtils.removeParameterFromQueryString(queryStringEventuallyAdjusted, queryStringContentToRemove);
//                    logLocal("queryStringEventuallyAdjusted post adjustment ="+queryStringEventuallyAdjusted);
                }
//                logLocal("encryptedParam=   "+encryptedParam);
                // if there is something encrypted submitted, decrypt it
                if (encryptedParam != null) {
                    final String encryptedInput;
                    // check if it came in via URL or FORM param
                    boolean cameViaURL = false;
                    final boolean isQueryStringFilled = queryStringEventuallyAdjusted != null && queryStringEventuallyAdjusted.length() > 0;
                    if (isQueryStringFilled) cameViaURL = queryStringEventuallyAdjusted.contains(cryptoDetectionString);
                    // re-create the unencrypted original URL 
                    final StringBuilder servletPathWithQueryString = new StringBuilder(servletPath);
                    if (isQueryStringFilled) servletPathWithQueryString.append('?').append(queryStringEventuallyAdjusted);
                    if (!cameViaURL) { // = if it came in via FORM param, re-inject the param as URL param into the URL and use that re-created URL
                        // re-inject the encrypted thing into the URL
//                        servletPathWithQueryString.append(isQueryStringFilled?(ResponseUtils.MASK_AMPERSANDS_IN_MODIFIED_URLS?"&amp;":"&"):"?").append(encryptedParam); // BOOKMARK 62653266
                        servletPathWithQueryString.append(isQueryStringFilled?'&':'?').append(encryptedParam); // BOOKMARK 62653266
                    }
                    encryptedInput = servletPathWithQueryString.toString();
                    assert encryptedInput != null;
                    final boolean isRequestMethodPOST = "POST".equalsIgnoreCase(httpRequest.getMethod());
                    // logLocal("isRequestMethodPOST="+isRequestMethodPOST);
                    if (!this.contentInjectionHelper.isExtraStrictParameterCheckingForEncryptedLinks()) hasAdditionalParameters = false;
                    final RequestUtils.DecryptedQuerystring decryptedRequest = RequestUtils.decryptQueryStringInServletPathWithQueryString(httpRequest.getContextPath(), servletPath, encryptedInput, cryptoDetectionString, cryptoKey, requestURI, hasAdditionalParameters, isRequestMethodPOST, this.useFullPathForResourceToBeAccessedProtection, this.additionalFullResourceRemoval||this.additionalMediumResourceRemoval, this.appendQuestionmarkOrAmpersandToLinks);
                    if (DEBUG_PRINT_UNCOVERING_DETAILS) logLocal("decryptedRequest="+decryptedRequest);
                    if (decryptedRequest != null) {
                        if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("4 (dive into forward): "+httpRequest.getParameterMap().keySet());
                        // OK, it was encrypted and is now decrypted... so forward to the decrypted URL...
                        this.attackHandler.logRegularRequestMessage("Successfully decrypted request:\n\tEncrypted input: "+encryptedInput+"\n\tDecrypted output without added parameters: "+decryptedRequest.decryptedString);
                        // check multipartness 
                        if (this.blockMultipartRequestsForNonMultipartForms && multikulti != null && Boolean.FALSE.equals(decryptedRequest.isFormMultipart)) {
                            throw new ServerAttackException("Multipart encoding used for non-multipart form request");
                        }
                        // go on
                        final RequestDispatcher requestDispatcher = httpRequest.getRequestDispatcher(decryptedRequest.decryptedString);
                        // TODO: was wenn requestDispatcher == null ? gibt's sowas ? hier eigentlich nicht mehr... aber Theorie und Praxis ist immer bei diversen Web-Containern so ne Sache....
                        if (request != null) {
                            request.setAttribute(REQUEST_ALREADY_DECRYPTED_FLAG, Boolean.TRUE);
                            request.setAttribute(REQUEST_IS_FORM_SUBMIT_FLAG, decryptedRequest.isFormSubmit);
                            request.setAttribute(REQUEST_IS_URL_MANIPULATED_FLAG, decryptedRequest.wasManipulated);
                            request.removeAttribute(REQUEST_NESTED_FORWARD_CALL); // = to let the filter work on that forwarded (i.e. nested) call too we simulate that this is not a nested (forwarded) call
                            // set the response character encoding to the same custom request character encoding (when defined) as this is a very special situation here: we're including/forwarding stuff....
                            if (this.requestCharacterEncoding != null && this.requestCharacterEncoding.length() > 0) {
                                if (isOldJavaEE13) response.setContentType("text/html; charset="+this.requestCharacterEncoding); // TODO: was ist hier, wenn die App nun im Servlet des decrypteten Links ein Image streamed oder eine PDF-Datei erzeugt und den Content-Typ erneut setzt ... sollte ebenfalls hiermit gehen, oder?
                                else response.setCharacterEncoding(this.requestCharacterEncoding);
                            }

                            // forward to the original unencrypted resource (not including since the include mechanism does not forward control to the resource, so that for example redirects and such originating from the included application logic will not work...
                            // therefore we delegate to the application logic by forwarding server-side control to the decrypted resource)!
                            // FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD
                            // FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD
                            // FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD
                            // FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD
                            requestDispatcher.forward(request, response);
                            // FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD
                            // FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD
                            // FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD
                            // FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD FORWARD
                            if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("5 (exit from forward): "+httpRequest.getParameterMap().keySet());
                            return; // = to avoid further processing here, since the forwarded request will handle and filter/check all the stuff
                        }
                    }
                }

                
            } catch (ServerAttackException e) {
                final HttpServletResponse httpResponse = (HttpServletResponse) response;
                if (this.redirectWelcomePage.length() == 0) {
                    // as no redirect welcome page is defined we have to treat it as a potential attack nevertheless
                    final Attack attack = this.attackHandler.handleAttack(httpRequest, clientAddress, "Client provided mismatching crypto values ("+e.getMessage()+") - and no redirect welcome page configured");
                    sendDisallowedResponse(httpResponse, attack);
                } else {
                    final String message = "Client provided mismatching crypto values ("+e.getMessage()+")\n\tInstead of letting the request pass we invalidate the session and redirect to the welcome page: "+this.redirectWelcomePage;
                    this.attackHandler.handleRegularRequest(httpRequest, clientAddress); // = since we're about to stop this request, we log it here
                    this.attackHandler.logWarningRequestMessage("Desired stop in filter processing of previously logged request: "+message);
                    try {
                        if (session != null) {
                            session.invalidate();
                            session = null;
                        }
                    } catch (IllegalStateException ignored) {}
                    // response.sendRedirectDueToRecentAttack is not yet possible, since the response is not yet wrapped (will be wrapped below), so it is safe to use standard response.sendRedirect here
                    httpResponse.sendRedirect( /*response.encodeRedirectURL(*/this.redirectWelcomePage/*)*/ ); // = by design we don't session-encode the URL here
                    // don't treat as attack but also don't let user pass, so simply after sending the redirect, stop the further processing
                }
                return; // = stop processing as desired :-)
            }
        }
        
        
        

        // =========================================================
        // Decoding Permutation according to the defined level
        // =========================================================
        byte decodingPermutationLevel = 1; // default level
        if (this.decodingPermutationDefinitions != null && this.decodingPermutationDefinitions.hasEnabledDefinitions()) {
            final DecodingPermutationDefinition matchingDefinition = this.decodingPermutationDefinitions.getMatchingDecodingPermutationDefinition(servletPath, requestURI);
            if (matchingDefinition != null) decodingPermutationLevel = matchingDefinition.getLevel();
        }
        



        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // Request detail data fetching
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        final boolean nonStandardPermutationsRequired; // here we check all those that "extends RequestDefinitionContainer" for non-standard permutation requirements:
        if (this.whiteListDefinitions.isNonStandardPermutationsAllowed() && this.whiteListDefinitions.hasEnabledDefinitions()) nonStandardPermutationsRequired = true; 
        else if (this.badRequestDefinitions.isNonStandardPermutationsAllowed() && this.badRequestDefinitions.hasEnabledDefinitions()) nonStandardPermutationsRequired = true; 
        else if (this.denialOfServiceLimitDefinitions.isNonStandardPermutationsAllowed() && this.denialOfServiceLimitDefinitions.hasEnabledDefinitions()) nonStandardPermutationsRequired = true; 
        else if (this.entryPointDefinitions.isNonStandardPermutationsAllowed() && this.entryPointDefinitions.hasEnabledDefinitions()) nonStandardPermutationsRequired = true; 
        else if (this.optimizationHintDefinitions.isNonStandardPermutationsAllowed() && this.optimizationHintDefinitions.hasEnabledDefinitions()) nonStandardPermutationsRequired = true; 
        else if (this.renewSessionPointDefinitions.isNonStandardPermutationsAllowed() && this.renewSessionPointDefinitions.hasEnabledDefinitions()) nonStandardPermutationsRequired = true; 
        else if (this.captchaPointDefinitions.isNonStandardPermutationsAllowed() && this.captchaPointDefinitions.hasEnabledDefinitions()) nonStandardPermutationsRequired = true; 
        else if (this.incomingProtectionExcludeDefinitions.isNonStandardPermutationsAllowed() && this.incomingProtectionExcludeDefinitions.hasEnabledDefinitions()) nonStandardPermutationsRequired = true; 
        else if (this.responseModificationDefinitions.isNonStandardPermutationsAllowed() && this.responseModificationDefinitions.hasEnabledDefinitions()) nonStandardPermutationsRequired = true; 
        else nonStandardPermutationsRequired = false;
        final RequestDetails requestDetails = new RequestDetails();
        requestDetails.clientAddress = RequestUtils.determineClientIp(httpRequest, this.clientIpDeterminator);
        requestDetails.agent = httpRequest.getHeader("user-agent");
        requestDetails.servletPath = servletPath; //httpRequest.getServletPath();
        requestDetails.queryString = httpRequest.getQueryString();
        if (isHavingEnabledQueryStringCheckingRules) requestDetails.queryStringVariants = ServerUtils.permutateVariants(requestDetails.queryString, nonStandardPermutationsRequired,decodingPermutationLevel);
        //OLD requestDetails.requestParameterMap = httpRequest.getParameterMap(); // it is automatically a defensive copy (see RequestWrapper.getParameterMap)
        //OLD requestDetails.requestParameterMapVariants = ServerUtils.permutateVariants(requestDetails.requestParameterMap, nonStandardPermutationsRequired,decodingPermutationLevel);
        requestDetails.requestedSessionId = httpRequest.getRequestedSessionId();
        requestDetails.sessionCameFromCookie = httpRequest.isRequestedSessionIdFromCookie();
        requestDetails.sessionCameFromURL = httpRequest.isRequestedSessionIdFromURL();
        requestDetails.referrer = httpRequest.getHeader("Referer"); // yes, according to HTTP RFC the exact name is "Referer" which is a misspelling
        requestDetails.url = ""+httpRequest.getRequestURL();
        requestDetails.uri = requestURI; //httpRequest.getRequestURI();
        requestDetails.method = httpRequest.getMethod();
        requestDetails.protocol = httpRequest.getProtocol();
        requestDetails.mimeType = httpRequest.getContentType();
        requestDetails.remoteHost = httpRequest.getRemoteHost();
        requestDetails.remoteUser = httpRequest.getRemoteUser();
        requestDetails.headerMap = RequestUtils.createHeaderMap(httpRequest);
        if (isHavingEnabledHeaderCheckingRules) requestDetails.headerMapVariants = ServerUtils.permutateVariants(requestDetails.headerMap, nonStandardPermutationsRequired,decodingPermutationLevel);
        requestDetails.cookieMap = RequestUtils.createCookieMap(httpRequest);
        if (isHavingEnabledCookieCheckingRules) requestDetails.cookieMapVariants = ServerUtils.permutateVariants(requestDetails.cookieMap, nonStandardPermutationsRequired,decodingPermutationLevel);
        requestDetails.encoding = httpRequest.getCharacterEncoding();
        requestDetails.contentLength = httpRequest.getContentLength();
        requestDetails.scheme = httpRequest.getScheme();
        requestDetails.serverName = httpRequest.getServerName();
        requestDetails.serverPort = httpRequest.getServerPort();
        requestDetails.authType = httpRequest.getAuthType();
        requestDetails.contextPath = httpRequest.getContextPath();
        requestDetails.pathInfo = httpRequest.getPathInfo();
        requestDetails.pathTranslated = httpRequest.getPathTranslated();
        if (this.geoLocatingCache != null && clientAddress != null) requestDetails.country = this.geoLocatingCache.getCountryCode(clientAddress); // TODO: when later adding different client identification possibilitis make sure that here only the IP address is used !!
        //OLD requestDetails.isMatchingOutgoingResponseModificationExclusion = isMatchingOutgoingResponseModificationExclusion; //! this.contentInjectionHelper.isRelevantResourceType(servletPath);

        // those methods are only availably in Java EE 1.4 or higher, so take care for older Java EE 1.3
        requestDetails.remotePort = 0;
        requestDetails.localPort = 0;
        requestDetails.localAddr = "";
        requestDetails.localName = "";
        if (!WebCastellumFilter.isOldJavaEE13) {
            try {
                requestDetails.remotePort = httpRequest.getRemotePort();
                requestDetails.localPort = httpRequest.getLocalPort();
                requestDetails.localAddr = httpRequest.getLocalAddr();
                requestDetails.localName = httpRequest.getLocalName();
            } catch (NoSuchMethodError e) {
                WebCastellumFilter.isOldJavaEE13 = true;
            }
        }
        
        if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("6: "+httpRequest.getParameterMap().keySet());
        // remove temporarily injected parameters (from copied Map only, NOT from original request)
        requestDetails.requestParameterMap = new HashMap( httpRequest.getParameterMap() ); // defensive copy of the map
        if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("7: "+httpRequest.getParameterMap().keySet());
        removeTemporarilyInjectedParametersFromMap(requestDetails.requestParameterMap, httpRequest.getSession(false), cryptoDetectionString);
        if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("8: "+httpRequest.getParameterMap().keySet());
        if (isHavingEnabledRequestParameterCheckingRules) requestDetails.requestParameterMapVariants = ServerUtils.permutateVariants(requestDetails.requestParameterMap, nonStandardPermutationsRequired,decodingPermutationLevel);
        requestDetails.nonStandardPermutationsRequired = nonStandardPermutationsRequired;
        requestDetails.decodingPermutationLevel = decodingPermutationLevel;
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        
        
        
        
                
        /*
         * Create wrappers for the request and response objects.
         * Using these, you can extend the capabilities of the
         * request and response, for example, allow setting parameters
         * on the request before sending the request to the rest of the filter chain,
         * or keep track of the cookies that are set on the response.
         *
         * Caveat: some servers do not handle wrappers very well for forward or
         * include requests.
         */
        final RequestWrapper wrappedRequest = new RequestWrapper(httpRequest, this.contentInjectionHelper, this.sessionCreationCounter, requestDetails.clientAddress, 
                this.hideInternalSessionAttributes, this.transparentQuerystring, this.transparentForwarding);
        ResponseWrapper wrappedResponse = null;
        AllowedFlagWithMessage allowed = new AllowedFlagWithMessage(false, new Attack("Disallowed by default"));
        if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("9: "+httpRequest.getParameterMap().keySet());
        if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("9c: "+wrappedRequest.getParameterMap().keySet());
        try {
            
    
            // ===================================================
            // If defined, check for request params that have a name with a leading question-mark and remove that (due to the JavaScript-supporting ampersand appendings)
            // ===================================================
            if (appendQuestionmarkOrAmpersandToLinks) {
                //List/*<String>*/ removedLeadingQuestionMarkFromParameterNameList = new ArrayList();
                for (final Enumeration names = wrappedRequest.getParameterNames(); names.hasMoreElements();) {
                    final String name = (String) names.nextElement();
                    if (name != null && name.length() > 0 && name.charAt(0)=='?') {
                        final String[] values = wrappedRequest.getParameterValues(name);
                        wrappedRequest.removeParameter(name);
                        wrappedRequest.setParameter(name, values, true);
                        //removedLeadingQuestionMarkFromParameterNameList.add(name);
                    }
                }
                /*OLD
                // hier auch das "?" entfernen in den bereits weiter oben in requestDetails abgelegten Parametern, damit wir sauber bleiben...
                if (!removedLeadingQuestionMarkFromParameterNameList.isEmpty()) {
                    for (final Iterator namesWithLeadingQuestionMark = removedLeadingQuestionMarkFromParameterNameList.iterator(); namesWithLeadingQuestionMark.hasNext();) {
                        final String nameWithLeadingQuestionMark = (String) namesWithLeadingQuestionMark.next();
                        if (requestDetails.parameterMap.containsKey(nameWithLeadingQuestionMark)) {
                            if (nameWithLeadingQuestionMark.length() > 1) {
                                final String[] values = (String[]) requestDetails.parameterMap.get(nameWithLeadingQuestionMark);
                                requestDetails.parameterMap.remove(nameWithLeadingQuestionMark);
                                requestDetails.parameterMap.put(nameWithLeadingQuestionMark.substring(1), values);
                            }
                        }
                    }
                    // NO re-creation of the permutated variants is required, since we've ONLY changed the name
                    // but we've got to re-create the map of params with WebCastellum-internals removed form the name-adjusted parameter map freshly:
                    requestDetails.parameterMapExcludingInternalParams = new HashMap( requestDetails.parameterMap ); // defensive copy of the map
                    removeTemporarilyInjectedParametersFromMap(requestDetails.parameterMapExcludingInternalParams, httpRequest.getSession(false), cryptoDetectionString);
                }*/
            }

            
            // fetch the response-modification patterns
            final ResponseModificationDefinition[] responseModificationDefinitionsArr = this.responseModificationDefinitions.getAllMatchingResponseModificationDefinitions(httpRequest,
                        requestDetails.servletPath, requestDetails.contextPath, requestDetails.pathInfo, requestDetails.pathTranslated, requestDetails.clientAddress, requestDetails.remoteHost, requestDetails.remotePort,
                        requestDetails.remoteUser, requestDetails.authType, requestDetails.scheme, requestDetails.method, requestDetails.protocol, requestDetails.mimeType, requestDetails.encoding, requestDetails.contentLength,
                        requestDetails.headerMapVariants, requestDetails.url, requestDetails.uri, requestDetails.serverName, requestDetails.serverPort, requestDetails.localAddr, requestDetails.localName, requestDetails.localPort, requestDetails.country,
                        requestDetails.cookieMapVariants, requestDetails.requestedSessionId, requestDetails.queryStringVariants, 
                        requestDetails.requestParameterMapVariants, requestDetails.requestParameterMap);
            final List/*<Pattern>*/ tmpPatternsToExcludeCompleteTag = new ArrayList(responseModificationDefinitionsArr.length);
            final List/*<Pattern>*/ tmpPatternsToExcludeCompleteScript = new ArrayList(responseModificationDefinitionsArr.length);
            final List/*<Pattern>*/ tmpPatternsToExcludeLinksWithinScripts = new ArrayList(responseModificationDefinitionsArr.length);
            final List/*<Pattern>*/ tmpPatternsToExcludeLinksWithinTags = new ArrayList(responseModificationDefinitionsArr.length);
            final List/*<Pattern>*/ tmpPatternsToCaptureLinksWithinScripts = new ArrayList(responseModificationDefinitionsArr.length);
            final List/*<Pattern>*/ tmpPatternsToCaptureLinksWithinTags = new ArrayList(responseModificationDefinitionsArr.length);
            final List/*<WordDictionary>*/ tmpPrefiltersToExcludeCompleteTag = new ArrayList(responseModificationDefinitionsArr.length);
            final List/*<WordDictionary>*/ tmpPrefiltersToExcludeCompleteScript = new ArrayList(responseModificationDefinitionsArr.length);
            final List/*<WordDictionary>*/ tmpPrefiltersToExcludeLinksWithinScripts = new ArrayList(responseModificationDefinitionsArr.length);
            final List/*<WordDictionary>*/ tmpPrefiltersToExcludeLinksWithinTags = new ArrayList(responseModificationDefinitionsArr.length);
            final List/*<WordDictionary>*/ tmpPrefiltersToCaptureLinksWithinScripts = new ArrayList(responseModificationDefinitionsArr.length);
            final List/*<WordDictionary>*/ tmpPrefiltersToCaptureLinksWithinTags = new ArrayList(responseModificationDefinitionsArr.length);
            final List/*<Integer[]>*/ tmpGroupNumbersToCaptureLinksWithinScripts = new ArrayList(responseModificationDefinitionsArr.length);
            final List/*<Integer[]>*/ tmpGroupNumbersToCaptureLinksWithinTags = new ArrayList(responseModificationDefinitionsArr.length);
            //final List<List<String>> tmpTagNames = new ArrayList(responseModificationDefinitions.length);
            for (ResponseModificationDefinition responseModificationDefinition : responseModificationDefinitionsArr) {
                if ( responseModificationDefinition.isMatchesScripts() ) {
                    tmpPatternsToExcludeCompleteScript.add( responseModificationDefinition.getScriptExclusionPattern() );
                    tmpPrefiltersToExcludeCompleteScript.add( responseModificationDefinition.getScriptExclusionPrefilter() );
                    tmpPatternsToExcludeLinksWithinScripts.add( responseModificationDefinition.getUrlExclusionPattern() );
                    tmpPrefiltersToExcludeLinksWithinScripts.add( responseModificationDefinition.getUrlExclusionPrefilter() );
                    tmpPatternsToCaptureLinksWithinScripts.add( responseModificationDefinition.getUrlCapturingPattern() );
                    tmpPrefiltersToCaptureLinksWithinScripts.add( responseModificationDefinition.getUrlCapturingPrefilter() );
                    tmpGroupNumbersToCaptureLinksWithinScripts.add( ServerUtils.convertSimpleToObjectArray(responseModificationDefinition.getCapturingGroupNumbers()) );
                }
                if ( responseModificationDefinition.isMatchesTags() ) {
                    tmpPatternsToExcludeCompleteTag.add( responseModificationDefinition.getTagExclusionPattern() );
                    tmpPrefiltersToExcludeCompleteTag.add( responseModificationDefinition.getTagExclusionPrefilter() );
                    tmpPatternsToExcludeLinksWithinTags.add( responseModificationDefinition.getUrlExclusionPattern() );
                    tmpPrefiltersToExcludeLinksWithinTags.add( responseModificationDefinition.getUrlExclusionPrefilter() );
                    tmpPatternsToCaptureLinksWithinTags.add( responseModificationDefinition.getUrlCapturingPattern() );
                    tmpPrefiltersToCaptureLinksWithinTags.add( responseModificationDefinition.getUrlCapturingPrefilter() );
                    tmpGroupNumbersToCaptureLinksWithinTags.add( ServerUtils.convertSimpleToObjectArray(responseModificationDefinition.getCapturingGroupNumbers()) );
                    //tmpTagNames.add(responseModificationDefinition.getTagNames());
                }
            }
            // convert lists of Pattern to arrays of Matcher
            final Matcher[] matchersToExcludeCompleteTag = ServerUtils.convertListOfPatternToArrayOfMatcher(tmpPatternsToExcludeCompleteTag);
            final Matcher[] matchersToExcludeCompleteScript = ServerUtils.convertListOfPatternToArrayOfMatcher(tmpPatternsToExcludeCompleteScript);
            final Matcher[] matchersToExcludeLinksWithinScripts = ServerUtils.convertListOfPatternToArrayOfMatcher(tmpPatternsToExcludeLinksWithinScripts);
            final Matcher[] matchersToExcludeLinksWithinTags = ServerUtils.convertListOfPatternToArrayOfMatcher(tmpPatternsToExcludeLinksWithinTags);
            final Matcher[] matchersToCaptureLinksWithinScripts = ServerUtils.convertListOfPatternToArrayOfMatcher(tmpPatternsToCaptureLinksWithinScripts);
            final Matcher[] matchersToCaptureLinksWithinTags = ServerUtils.convertListOfPatternToArrayOfMatcher(tmpPatternsToCaptureLinksWithinTags);
            final WordDictionary[] prefiltersToExcludeCompleteTag = (WordDictionary[]) tmpPrefiltersToExcludeCompleteTag.toArray(new WordDictionary[0]);
            final WordDictionary[] prefiltersToExcludeCompleteScript = (WordDictionary[]) tmpPrefiltersToExcludeCompleteScript.toArray(new WordDictionary[0]);
            final WordDictionary[] prefiltersToExcludeLinksWithinScripts = (WordDictionary[]) tmpPrefiltersToExcludeLinksWithinScripts.toArray(new WordDictionary[0]);
            final WordDictionary[] prefiltersToExcludeLinksWithinTags = (WordDictionary[]) tmpPrefiltersToExcludeLinksWithinTags.toArray(new WordDictionary[0]);
            final WordDictionary[] prefiltersToCaptureLinksWithinScripts = (WordDictionary[]) tmpPrefiltersToCaptureLinksWithinScripts.toArray(new WordDictionary[0]);
            final WordDictionary[] prefiltersToCaptureLinksWithinTags = (WordDictionary[]) tmpPrefiltersToCaptureLinksWithinTags.toArray(new WordDictionary[0]);
            final int[][] groupNumbersToCaptureLinksWithinScripts = ServerUtils.convertArrayIntegerListTo2DimIntArray(tmpGroupNumbersToCaptureLinksWithinScripts);
            final int[][] groupNumbersToCaptureLinksWithinTags = ServerUtils.convertArrayIntegerListTo2DimIntArray(tmpGroupNumbersToCaptureLinksWithinTags);
            //final List<String>[] tagNamesToCheck = tmpTagNames.toArray(new List[0]);

            if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("10: "+httpRequest.getParameterMap().keySet());
            if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("10c: "+wrappedRequest.getParameterMap().keySet());
            // create response wrapper (but watch out for optimization-hint requests)
            final boolean isOptimizationHint = this.optimizationHintDefinitions.isOptimizationHint(wrappedRequest,
                        requestDetails.servletPath, requestDetails.contextPath, requestDetails.pathInfo, requestDetails.pathTranslated, requestDetails.clientAddress, requestDetails.remoteHost, requestDetails.remotePort,
                        requestDetails.remoteUser, requestDetails.authType, requestDetails.scheme, requestDetails.method, requestDetails.protocol, requestDetails.mimeType, requestDetails.encoding, requestDetails.contentLength,
                        requestDetails.headerMapVariants, requestDetails.url, requestDetails.uri, requestDetails.serverName, requestDetails.serverPort, requestDetails.localAddr, requestDetails.localName, requestDetails.localPort, requestDetails.country,
                        requestDetails.cookieMapVariants, requestDetails.requestedSessionId, requestDetails.queryStringVariants, 
                        requestDetails.requestParameterMapVariants, requestDetails.requestParameterMap);
            wrappedResponse = new ResponseWrapper((HttpServletResponse)response, wrappedRequest, this.attackHandler, this.contentInjectionHelper, isOptimizationHint, cryptoDetectionString,cipher,cryptoKey, secretTokenKey,secretTokenValue, 
                    parameterAndFormProtectionKeyKey, this.blockResponseHeadersWithCRLF, this.blockFutureLastModifiedHeaders, this.blockInvalidLastModifiedHeaders, this.blockNonLocalRedirects, clientAddress, this.responseBodyModificationContentTypes,
                    prefiltersToExcludeCompleteScript, matchersToExcludeCompleteScript, 
                    prefiltersToExcludeCompleteTag, matchersToExcludeCompleteTag,
                    prefiltersToExcludeLinksWithinScripts, matchersToExcludeLinksWithinScripts, 
                    prefiltersToExcludeLinksWithinTags, matchersToExcludeLinksWithinTags,
                    prefiltersToCaptureLinksWithinScripts, matchersToCaptureLinksWithinScripts, 
                    prefiltersToCaptureLinksWithinTags, matchersToCaptureLinksWithinTags,
                    groupNumbersToCaptureLinksWithinScripts,  
                    groupNumbersToCaptureLinksWithinTags, 
                    //tagNamesToCheck,
                    this.useFullPathForResourceToBeAccessedProtection, this.additionalFullResourceRemoval, this.additionalMediumResourceRemoval, this.maskAmpersandsInLinkAdditions,
                    hiddenFormFieldProtection, selectboxProtection, checkboxProtection, radiobuttonProtection, selectboxValueMasking, checkboxValueMasking, radiobuttonValueMasking,
                    this.appendQuestionmarkOrAmpersandToLinks, this.appendSessionIdToLinks, this.reuseSessionContent,
                    this.honeylinkPrefix, this.honeylinkSuffix, this.honeylinkMaxPerPage, this.randomizeHoneylinksOnEveryRequest, this.pdfXssProtection, this.applySetAfterWrite);
            if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("11: "+httpRequest.getParameterMap().keySet());
            if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("11c: "+wrappedRequest.getParameterMap().keySet());
            // do "before processing" stuff
            //final String requestURL = ""+wrappedRequest.getRequestURL();
            final Boolean isFormSubmit = (Boolean) request.getAttribute(REQUEST_IS_FORM_SUBMIT_FLAG);
            final Boolean isUrlManipulated = (Boolean) request.getAttribute(REQUEST_IS_URL_MANIPULATED_FLAG);
            allowed = doBeforeProcessing(wrappedRequest, wrappedResponse, requestDetails, cryptoDetectionString, isFormSubmit, isUrlManipulated);
            if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("12: "+httpRequest.getParameterMap().keySet());
            if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("12c: "+wrappedRequest.getParameterMap().keySet());
        } catch (StopFilterProcessingException e) {
            this.attackHandler.handleRegularRequest(httpRequest, clientAddress); // = since we're about to stop this request, we log it here
            this.attackHandler.logWarningRequestMessage("Desired stop in filter processing of previously logged request: "+e.getMessage());
            return; // = stop processing as desired :-)
        } catch (Exception e) {
            final String message = "Exception ("+e.getMessage()+") while checking request (therefore disallowing it by default)";
            allowed = new AllowedFlagWithMessage(false, new Attack(message));
            if (!(e instanceof ServerAttackException)) {
                // TODO: hier eigentlich auch auf stderr noetig oder ueberfluessig?
                System.err.println(message);
                e.printStackTrace();
            }
            this.attackHandler.logWarningRequestMessage(message);
        }
        
        if (allowed.isAllowed()) {
            assert wrappedResponse != null;
            Throwable problem = null;
            boolean attackSoFar = false;
            try {
                if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("13: "+httpRequest.getParameterMap().keySet());
                if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("13c: "+wrappedRequest.getParameterMap().keySet());
                // to avoid pollution of the application remove any temporarily injected parameters (used only internally by this filter)
                removeTemporarilyInjectedParametersFromRequest(wrappedRequest, cryptoDetectionString);
                if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("14: "+httpRequest.getParameterMap().keySet());
                if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("14c: "+wrappedRequest.getParameterMap().keySet());
                // during the chain (i.e. during the application logic code) transfer protective session contents in case the application itself renews the session
                // that way all WebCastellum content of the old session which the application might invalidate is transferred into the new one
                wrappedRequest.setTransferProtectiveSessionContentToNewSessionsDefinedByApplication(true);
                // during the chain (i.e. during the application logic code) apply even the unsecure parameter values checks
                wrappedRequest.setApplyUnsecureParameterValueChecks(true); // TODO: hierbei auch gleich noch waehrend der chain die durch WebCastellum temporaer in die Session platzierten Inhalte im Session-Wrapper mit ner Tarnkappe im Getter versehen (also verstecken) ?
                // CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN
                // CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN
                // CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN
                // CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN
                // CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN
                wrappedRequest.setAttribute(REQUEST_NESTED_FORWARD_CALL, Boolean.TRUE);
                wrappedRequest.removeParameter(CAPTCHA_VALUE); // can only be removed here... 
                // multipart stuff
                if (!this.presentMultipartFormParametersAsRegularParametersToApplication && multikulti != null) {
                    multikulti.setHideMultipartFormParametersSinceWeAreWithingApplicationAccess(true);
                }
                if (this.showTimings) logLocal("Duration for pre-processing of request: "+(System.currentTimeMillis()-timerBefore)+" ms");
                chain.doFilter(wrappedRequest, wrappedResponse);
                final int status = wrappedResponse.getCapturedStatus();
                if (status < 400) this.attackHandler.handleLearningModeRequestAggregation(wrappedRequest); // = LEARNING MODE
                wrappedRequest.removeAttribute(REQUEST_NESTED_FORWARD_CALL);
                if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("15: "+httpRequest.getParameterMap().keySet());
                if (INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE && this.debug) logLocal("15c: "+wrappedRequest.getParameterMap().keySet());
                // CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN
                // CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN
                // CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN
                // CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN
                // CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN CHAIN
            } catch(ServerAttackException e) {
                attackSoFar = true;
                if (this.debug) e.printStackTrace();
                wrappedRequest.setTransferProtectiveSessionContentToNewSessionsDefinedByApplication(false); // also clears the eventually used TreadLocal
                // already handled by attack-handler, but send disallowed response nevertheless without counting it in AttackHandler
                sendDisallowedResponse((HttpServletResponse)response, new Attack(e.getMessage()));
            } catch(Throwable t) {
                /*
                 * If an exception is thrown somewhere down the filter chain,
                 * we still want to execute our after processing, and then
                 * rethrow the problem after that.
                 */
                problem = t;
                t.printStackTrace(); // TODO: put an "if (this.debug)" around this line ?
            } finally {
                // Stop transferring WebCastellum session content from the old into new sessions, as the chain is over and if now the session gets invalidated it gets
                // invalidated by WebCastellum, so that is then desired and should no longer get the WebCastellum session contents transferred...
                wrappedRequest.setTransferProtectiveSessionContentToNewSessionsDefinedByApplication(false); // also clears the eventually used TreadLocal
                // Stop the unsecure values checks as the application logic has now finished
                wrappedRequest.setApplyUnsecureParameterValueChecks(false);
                // multipart stuff
                if (!this.presentMultipartFormParametersAsRegularParametersToApplication && multikulti != null) {
                    multikulti.setHideMultipartFormParametersSinceWeAreWithingApplicationAccess(false);
                    multikulti.clear();
                }
            }
            
            final long timerAfter = System.currentTimeMillis();
            if (!attackSoFar) doAfterProcessing(wrappedRequest, wrappedResponse);
            if (this.showTimings) logLocal("Duration for post-processing of response: "+(System.currentTimeMillis()-timerAfter)+" ms");

            // multipart stuff (clear again to be safe)
            if (multikulti != null) multikulti.clear();
            
            /*
             * If there was a problem, we want to rethrow it if it is
             * a known type, otherwise log it.
             */
            if (problem != null) {
                // TODO: Hier noch SocketExceptions im Sinne von "Broken pipe" oder "Conection reset by peer" abfangen?
                if (problem instanceof ServletException) throw (ServletException)problem;
                if (problem instanceof IOException) throw (IOException)problem;
                if (problem instanceof RuntimeException) throw (RuntimeException)problem;
                sendProcessingError(problem, (HttpServletResponse)response);
            } else {
		// Folgende Zeile (da bei statischen resourcen 304 gefaehrlich) kann man auf false in config setzen...
                if (this.flushResponse) wrappedResponse.flushBuffer(); // TODO: Hier noch SocketExceptions im Sinne von "Broken pipe" oder "Conection reset by peer" abfangen?
            }           
            
        } else {
            if (allowed.getAttack() != null) {
                // already counted in AttackHandler (that's where the Attack object came from), so only send the disallowed response here
                sendDisallowedResponse((HttpServletResponse)response, allowed.getAttack());
            } else {
                // here it should be a captcha
                return;
            }
        }
        
        
    }
    
    
    
    
    
    
    
    
    

    
    
    public void registerRuleReloadOnNextRequest() {
        this.reloadRulesOnNextRequest = true;
    }
    private final Object mutex_reloadRulesOnNextRequest = new Object();
    private void reloadRulesWhenRequired() throws RuleLoadingException {
        if (this.reloadRulesOnNextRequest) {
            List/*<String>*/ messages = null; // to have the logging outside the synchronized block
            synchronized (this.mutex_reloadRulesOnNextRequest) {
                if (this.reloadRulesOnNextRequest) {
                    try {
                        isHavingEnabledQueryStringCheckingRules = false;
                        isHavingEnabledRequestParameterCheckingRules = false;
                        isHavingEnabledHeaderCheckingRules = false;
                        isHavingEnabledCookieCheckingRules = false;
                        messages = new ArrayList(); // here we reload all types of rule-files, regardless if "extends RequestDefinition" or "extends SimpleDefinitions":
                        if (this.whiteListDefinitions != null) {
                            messages.add( this.whiteListDefinitions.parseDefinitions() );
                            if (this.whiteListDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                            if (this.whiteListDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                            if (this.whiteListDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                            if (this.whiteListDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                        }
                        if (this.badRequestDefinitions != null) {
                            messages.add( this.badRequestDefinitions.parseDefinitions() );
                            if (this.badRequestDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                            if (this.badRequestDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                            if (this.badRequestDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                            if (this.badRequestDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                        }
                        if (this.denialOfServiceLimitDefinitions != null) {
                            messages.add( this.denialOfServiceLimitDefinitions.parseDefinitions() );
                            if (this.denialOfServiceLimitDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                            if (this.denialOfServiceLimitDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                            if (this.denialOfServiceLimitDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                            if (this.denialOfServiceLimitDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                        }
                        if (this.entryPointDefinitions != null) {
                            messages.add( this.entryPointDefinitions.parseDefinitions() );
                            if (this.entryPointDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                            if (this.entryPointDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                            if (this.entryPointDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                            if (this.entryPointDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                        }
                        if (this.optimizationHintDefinitions != null) {
                            messages.add( this.optimizationHintDefinitions.parseDefinitions() );
                            if (this.optimizationHintDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                            if (this.optimizationHintDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                            if (this.optimizationHintDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                            if (this.optimizationHintDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                        }
                        if (this.renewSessionPointDefinitions != null) {
                            messages.add( this.renewSessionPointDefinitions.parseDefinitions() );
                            if (this.renewSessionPointDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                            if (this.renewSessionPointDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                            if (this.renewSessionPointDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                            if (this.renewSessionPointDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                        }
                        if (this.captchaPointDefinitions != null) {
                            messages.add( this.captchaPointDefinitions.parseDefinitions() );
                            if (this.captchaPointDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                            if (this.captchaPointDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                            if (this.captchaPointDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                            if (this.captchaPointDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                        }
                        if (this.incomingProtectionExcludeDefinitions!= null) {
                            messages.add( this.incomingProtectionExcludeDefinitions.parseDefinitions() );
                            if (this.incomingProtectionExcludeDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                            if (this.incomingProtectionExcludeDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                            if (this.incomingProtectionExcludeDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                            if (this.incomingProtectionExcludeDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                        }
                        if (this.responseModificationDefinitions != null) {
                            messages.add( this.responseModificationDefinitions.parseDefinitions() );
                            if (this.responseModificationDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                            if (this.responseModificationDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                            if (this.responseModificationDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                            if (this.responseModificationDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                        }
                        if (this.totalExcludeDefinitions != null) {
                            messages.add( this.totalExcludeDefinitions.parseDefinitions() );
                        }
                        if (this.contentModificationExcludeDefinitions != null) {
                            messages.add( this.contentModificationExcludeDefinitions.parseDefinitions() );
                        }
                        if (this.sizeLimitDefinitions != null) {
                            messages.add( this.sizeLimitDefinitions.parseDefinitions() );
                        }
                        if (this.multipartSizeLimitDefinitions != null) {
                            messages.add( this.multipartSizeLimitDefinitions.parseDefinitions() );
                        }
                        if (this.decodingPermutationDefinitions != null) {
                            messages.add( this.decodingPermutationDefinitions.parseDefinitions() );
                        }
                        if (this.formFieldMaskingExcludeDefinitions != null) {
                            messages.add( this.formFieldMaskingExcludeDefinitions.parseDefinitions() );
                        }
                        this.reloadRulesOnNextRequest = false;
                    } catch (RuleLoadingException e) {
                        this.reloadRulesOnNextRequest = true;
                        logLocal("Unable to (re)load security rules", e);
                        throw e;
                    } catch (RuntimeException e) {
                        this.reloadRulesOnNextRequest = true;
                        logLocal("Unable to (re)load security rules", e);
                        throw e;
                    }
                }
            }
            // to have the actual logging happen outside of the synchronized block:
            if (messages != null && !messages.isEmpty()) {
                for (final Iterator iter = messages.iterator(); iter.hasNext();) {
                    final String message = (String) iter.next();
                    logLocal(message);
                }
            }
        }
    }
    
    
    
    
    
    
    
    
    public void registerConfigReloadOnNextRequest() {
        this.restartCompletelyOnNextRequest = true;
    }
    private final Object mutex_restartCompletelyWhenRequired = new Object();
    private void restartCompletelyWhenRequired() throws UnavailableException {
        if (this.restartCompletelyOnNextRequest) {
            synchronized (this.mutex_restartCompletelyWhenRequired) {
                if (this.restartCompletelyOnNextRequest) {
                    try {
                        checkRequirementsAndInitialize();
                        this.restartCompletelyOnNextRequest = false;
                        logLocal("Initialized protection layer");
                    } catch (RuntimeException e) {
                        this.restartCompletelyOnNextRequest = true;
                        // when a full-restart failed, clean up any open stuff that might have been already initialized during our first init try 
                        // to have a clean beginning for the subsequent restart try
                        try { destroy(); } catch (Exception e2) { e2.printStackTrace(); }
                        e.printStackTrace();
                        throw e;
                    } catch (UnavailableException e) {
                        this.restartCompletelyOnNextRequest = true;
                        // when a full-restart failed, clean up any open stuff that might have been already initialized during our first init try
                        // to have a clean beginning for the subsequent restart try
                        try { destroy(); } catch (Exception e2) { e2.printStackTrace(); }
                        e.printStackTrace();
                        throw e;
                    }
                }
            }
        }
    }
    
    
    
    




    

    /**
     * Destroy method for this filter
     *
     */
    public void destroy() {
        if (this.httpStatusCodeCounter != null) {
            try {
                this.httpStatusCodeCounter.destroy();
            } catch (Exception e) {
                logLocal("Exception during destroy: "+e);
            }
        }
        if (this.sessionCreationCounter != null) {
            try {
                this.sessionCreationCounter.destroy();
            } catch (Exception e) {
                logLocal("Exception during destroy: "+e);
            }
        }
        if (this.denialOfServiceLimitCounter != null) {
            try {
                this.denialOfServiceLimitCounter.destroy();
            } catch (Exception e) {
                logLocal("Exception during destroy: "+e);
            }
        }
        if (this.attackHandler != null) {
            try {
                this.attackHandler.destroy(); // = this also destroys the ClientBlacklist which lives only inside of the AttackHandler as an implementation detail of the AttackHandler
            } catch (Exception e) {
                logLocal("Exception during destroy: "+e);
            }
        }
        if (this.geoLocatingCache != null) {
            try {
                this.geoLocatingCache.destroy();
            } catch (Exception e) {
                logLocal("Exception during destroy: "+e);
            }
        }
        if (this.jmsUsed) try {
            JmsUtils.closeQuietly(true);
        } catch (Exception e) {
            // log only (so that not found JMS classes when JMS is not used don't make problems
            logLocal("JMS utility not destroyed",e);
        }
        this.restartCompletelyOnNextRequest = true;
    }


    /**
     * Init method for this filter
     *
     */
    public void init(final FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        try {
            restartCompletelyWhenRequired();
        } catch (Exception e) {
            // not throwing the exception here in order to init the filter laziy (when the error condition is over and keep the app meanwhile blocked)
            logLocal("Unable to initialize security filter", e);
        }
    }
    
    
    private void checkRequirementsAndInitialize() throws UnavailableException {
        if (this.filterConfig == null) throw new IllegalStateException("Filter mit be initialized via web container before 'init()' this method may be called");

        try {
            destroy();
        } catch (RuntimeException e) {
            logLocal("Unable to destroy configuration during (re-)initialization", e);
        }

        final ConfigurationManager configManager;
        try {
            configManager = new ConfigurationManager(this.filterConfig);
            logLocal("ConfigurationManager: "+configManager);
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Unable to initialize ConfigurationManager (caught ClassNotFoundException): "+e.getMessage());
        } catch (InstantiationException e) {
            throw new UnavailableException("Unable to initialize ConfigurationManager (caught InstantiationException): "+e.getMessage());
        } catch (IllegalAccessException e) {
            throw new UnavailableException("Unable to initialize ConfigurationManager (caught IllegalAccessException): "+e.getMessage());
        } catch (FilterConfigurationException e) {
            throw new UnavailableException("Unable to initialize ConfigurationManager (caught FilterConfigurationException): "+e.getMessage());
        } catch (RuntimeException e) {
            throw new UnavailableException("Unable to initialize ConfigurationManager (caught RuntimeException): "+e.getMessage());
        }
        assert configManager != null;

        
        boolean initJMS = false; // might be set to true during initialization, which then indicates that we should init JMS (i.e. start listening) at the end of config loading (see below)
        
        // LOAD THE "CONFIG-MISSING-STUFF" AT FIRST HERE IN CONFIG LOADING
        
        // Load config: Configuration missing reply HTTP status code or message resource [PROD] - OPTIONAL
        {
            String productionConfigurationMissingReplyConfigValue = configManager.getConfigurationValue(PARAM_PROD_CONFIG_MISSING_REPLY_STATUS_CODE_OR_MESSAGE_RESOURCE);
            if (productionConfigurationMissingReplyConfigValue == null) productionConfigurationMissingReplyConfigValue = "503"; // we're using HTTP Status-Code 503 instead of a message file like "org/webcastellum/missing.html" to be even safer
            try {
                this.productionConfigurationMissingReplyStatusCode = Integer.parseInt(productionConfigurationMissingReplyConfigValue.trim());
                if (this.productionConfigurationMissingReplyStatusCode < 0) throw new UnavailableException("Configured HTTP status code to send as reply to missing configuration (in production mode) must not be negative: "+productionConfigurationMissingReplyConfigValue);
            } catch(NumberFormatException e) {
                // treat as file pointer into classpath instead of treating it as a status code
                final InputStream input = WebCastellumFilter.class.getClassLoader().getResourceAsStream(productionConfigurationMissingReplyConfigValue);
                if (input == null) throw new UnavailableException("Unable to number-parse configured HTTP status code to send as reply to missing configuration (in production mode) as well as unable to locate a resource in classpath with name: "+productionConfigurationMissingReplyConfigValue);
                BufferedReader buffer = null;
                try {
                    buffer = new BufferedReader( new InputStreamReader(input) );
                    final StringBuilder content = new StringBuilder();
                    String line;
                    while ( (line=buffer.readLine()) != null ) {
                        content.append(line).append("\n");
                    }
                    this.productionConfigurationMissingReplyMessage = content.toString().trim();
                } catch (Exception ex) {
                    throw new UnavailableException("Unable to load content from the specified resource in classpath with name: "+productionConfigurationMissingReplyConfigValue);
                } finally {
                    if (buffer != null) try { buffer.close(); } catch (IOException ignored) {}
                }
            }
        }
        
        // Load config: Configuration missing reply HTTP status code or message resource [DEV] - OPTIONAL
        {
            String developmentConfigurationMissingReplyConfigValue = configManager.getConfigurationValue(PARAM_DEV_CONFIG_MISSING_REPLY_STATUS_CODE_OR_MESSAGE_RESOURCE);
            if (developmentConfigurationMissingReplyConfigValue == null) developmentConfigurationMissingReplyConfigValue = "org/webcastellum/missing.html";
            try {
                this.developmentConfigurationMissingReplyStatusCode = Integer.parseInt(developmentConfigurationMissingReplyConfigValue.trim());
                if (this.developmentConfigurationMissingReplyStatusCode < 0) throw new UnavailableException("Configured HTTP status code to send as reply to missing configuration (in development mode) must not be negative: "+developmentConfigurationMissingReplyConfigValue);
            } catch(NumberFormatException e) {
                // treat as file pointer into classpath instead of treating it as a status code
                final InputStream input = WebCastellumFilter.class.getClassLoader().getResourceAsStream(developmentConfigurationMissingReplyConfigValue);
                if (input == null) throw new UnavailableException("Unable to number-parse configured HTTP status code to send as reply to missing configuration (in development mode) as well as unable to locate a resource in classpath with name: "+developmentConfigurationMissingReplyConfigValue);
                BufferedReader buffer = null;
                try {
                    buffer = new BufferedReader( new InputStreamReader(input) );
                    final StringBuilder content = new StringBuilder();
                    String line;
                    while ( (line=buffer.readLine()) != null ) {
                        content.append(line).append("\n");
                    }
                    this.developmentConfigurationMissingReplyMessage = content.toString().trim();
                } catch (Exception ex) {
                    throw new UnavailableException("Unable to load content from the specified resource in classpath with name: "+developmentConfigurationMissingReplyConfigValue);
                } finally {
                    if (buffer != null) try { buffer.close(); } catch (IOException ignored) {}
                }
            }
        }
        
        
        
        // init to false
        isHavingEnabledQueryStringCheckingRules = false;
        isHavingEnabledRequestParameterCheckingRules = false;
        isHavingEnabledHeaderCheckingRules = false;
        isHavingEnabledCookieCheckingRules = false;
        
        // THE REGULAR STUFF STARTS HERE:
        
        
        
        
        // Load config: debug flag - OPTIONAL
        {
            String debugValue = configManager.getConfigurationValue(PARAM_DEBUG);
            if (debugValue == null) debugValue = "false";
            this.debug = (""+true).equals( debugValue.trim().toLowerCase() );
        }
        
        // Load config: timing flag - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_SHOW_TIMINGS);
            if (value == null) value = "false";
            this.showTimings = (""+true).equals( value.trim().toLowerCase() );
        }
        
        // Load config: redirect welcome page - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_REDIRECT_WELCOME_PAGE);
            if (value == null) value = "";
            this.redirectWelcomePage = value.trim();
            if (this.debug) logLocal("Redirect welcome page: "+this.redirectWelcomePage);
        }
        
        // Load config: session timeout redirect page - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_SESSION_TIMEOUT_REDIRECT_PAGE);
            if (value == null) value = "";
            this.sessionTimeoutRedirectPage = value.trim();
            if (this.debug) logLocal("Session Timeout Redirect page: "+this.sessionTimeoutRedirectPage);
        }
        
        
        // Load config: request character encoding - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_CHARACTER_ENCODING);
            if (value == null) value = configManager.getConfigurationValue(LEGACY_PARAM_CHARACTER_ENCODING); // only for backwards-compatibility to old param name
            if (value == null) value = DEFAULT_CHARACTER_ENCODING;
            this.requestCharacterEncoding = value.trim();
            if (this.debug) logLocal("Request character encoding: "+this.requestCharacterEncoding);
        }

        // Load config: log session values on attack - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_LOG_SESSION_VALUES_ON_ATTACK);
            if (value == null) value = ""+false;
            this.logSessionValuesOnAttack = (""+true).equals( value.trim().toLowerCase() );
        }
        
        // Load config: learning mode aggregation directory - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_LEARNING_MODE_AGGREGATION_DIRECTORY);
            if (value == null) value = ""; // this default (empty) means here: no learning mode, not even through inherited logger
            this.learningModeAggregationDirectory = value.trim();
            if (this.debug) logLocal("Learning mode aggregation directory: "+this.learningModeAggregationDirectory);
        }
        
        // Load config: application name - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_APPLICATION_NAME);
            if (value == null) value = "DEFAULT";
            this.applicationName = value.trim();
            if (this.debug) logLocal("Application name: "+this.applicationName);
        }
        
        // Load config: rule-file-loader - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_RULE_LOADER);
            if (value == null) value = configManager.getConfigurationValue(LEGACY_PARAM_RULE_FILE_LOADER); // only for backwards-compatibility to old param name
            if (value == null) value = "org.webcastellum.ClasspathZipRuleFileLoader"; // = this implementation has defaults too so that it does not require any further mandatory init-params
            value = value.trim();
            if (value.length() == 0) throw new UnavailableException("Filter init-param is empty: "+PARAM_RULE_LOADER);
            try {
                this.ruleFileLoaderClass = Class.forName(value);
            } catch (ClassNotFoundException e) {
                throw new UnavailableException("Unable to find rule-file-loader class ("+value+"): "+e.getMessage());
            }
            assert this.ruleFileLoaderClass != null;
        }
        
        // Load config: captcha generator - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_CAPTCHA_GENERATOR);
            if (value == null) value = "org.webcastellum.DefaultCaptchaGenerator"; // = this implementation has defaults too so that it does not require any further mandatory init-params
            value = value.trim();
            if (value.length() == 0) throw new UnavailableException("Filter init-param is empty: "+PARAM_CAPTCHA_GENERATOR);
            try {
                this.captchaGenerator = (CaptchaGenerator) Class.forName(value).newInstance();
                this.captchaGenerator.setFilterConfig(filterConfig);
            } catch (ClassNotFoundException e) {
                throw new UnavailableException("Unable to find captcha-generator class ("+value+"): "+e.getMessage());
            } catch (InstantiationException e) {
                throw new UnavailableException("Unable to instantiate captcha-generator ("+value+"): "+e.getMessage());
            } catch (IllegalAccessException e) {
                throw new UnavailableException("Unable to access captcha-generator ("+value+"): "+e.getMessage());
            } catch (FilterConfigurationException e) {
                throw new UnavailableException("Unable to configure captcha-generator ("+value+"): "+e.getMessage());
            } catch (RuntimeException e) {
                throw new UnavailableException("Unable to use captcha-generator ("+value+"): "+e.getMessage());
            }
            assert this.captchaGenerator != null;
        }
        
        // Load config: production-mode-checker - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PRODUCTION_MODE_CHECKER);
            if (value == null) value = "org.webcastellum.DefaultProductionModeChecker"; // = this checker has defaults too so that it does not require any further mandatory init-params
            value = value.trim();
            if (value.length() == 0) throw new UnavailableException("Filter init-param is empty: "+PARAM_PRODUCTION_MODE_CHECKER);
            try {
                this.productionModeCheckerClass = Class.forName(value);
            } catch (ClassNotFoundException e) {
                throw new UnavailableException("Unable to find production-mode-checker class ("+value+"): "+e.getMessage());
            }
            try {
                assert this.productionModeCheckerClass != null;
                final ProductionModeChecker productionModeChecker = (ProductionModeChecker) this.productionModeCheckerClass.newInstance();
                productionModeChecker.setFilterConfig(filterConfig);
                this.isProductionMode = productionModeChecker.isProductionMode();
                System.out.println("WebCastellum is "+(this.isProductionMode?"":"NOT ")+"set into production mode for application "+this.applicationName);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to check for production mode: "+ex.getMessage());
            }
        }
        
        // Load config: client-ip-determinator - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_CLIENT_IP_DETERMINATOR);
            if (value == null) value = "org.webcastellum.DefaultClientIpDeterminator"; // = this determinator has defaults too so that it does not require any further mandatory init-params
            value = value.trim();
            if (value.length() == 0) throw new UnavailableException("Filter init-param is empty: "+PARAM_CLIENT_IP_DETERMINATOR);
            try {
                this.clientIpDeterminatorClass = Class.forName(value);
            } catch (ClassNotFoundException e) {
                throw new UnavailableException("Unable to find client-ip-determinator class ("+value+"): "+e.getMessage());
            }
            try {
                assert this.clientIpDeterminatorClass != null;
                this.clientIpDeterminator = (ClientIpDeterminator) this.clientIpDeterminatorClass.newInstance();
                this.clientIpDeterminator.setFilterConfig(filterConfig);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to create and configure client-ip-determinator instance: "+ex.getMessage());
            }
        }
        
        // Load config: multipart-request-parser - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_MULTIPART_REQUEST_PARSER);
            if (value == null) value = "org.webcastellum.DefaultMultipartRequestParser"; // = this parser has defaults too so that it does not require any further mandatory init-params
            value = value.trim();
            if (value.length() == 0) throw new UnavailableException("Filter init-param is empty: "+PARAM_MULTIPART_REQUEST_PARSER);
            try {
                this.multipartRequestParserClass = Class.forName(value);
            } catch (ClassNotFoundException e) {
                throw new UnavailableException("Unable to find multipart-request-parser class ("+value+"): "+e.getMessage());
            }
            try {
                assert this.multipartRequestParserClass != null;
                this.multipartRequestParser = (MultipartRequestParser) this.multipartRequestParserClass.newInstance();
                this.multipartRequestParser.setFilterConfig(filterConfig);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to create and configure multipart-request-parser instance: "+ex.getMessage());
            }
        }
        
        // Load config: failed captcha per session attack threshold - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_FAILED_CAPTCHA_PER_SESSION_ATTACK_THRESHOLD);
            if (value == null) value = "30";
            try {
                this.failedCaptchaPerSessionAttackThreshold = Integer.parseInt(value.trim());
                if (this.failedCaptchaPerSessionAttackThreshold < 0) throw new UnavailableException("Configured failed captcha per session attack threshold must not be negative: "+value);
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured 'failed captcha per session attack threshold': "+value);
            }
            if (this.debug) logLocal("Failed captcha per session attack threshold: "+this.failedCaptchaPerSessionAttackThreshold);
        }
        
        // Load config: cluster JMS broadcast period - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_CLUSTER_BROADCAST_PERIOD);
            if (value == null) value = "60";
            try {
                this.clusterBroadcastPeriod = Integer.parseInt(value.trim());
                if (this.clusterBroadcastPeriod < 0) throw new UnavailableException("Configured cluster JMS broadcast period must not be negative: "+value);
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured 'cluster JMS broadcast period': "+value);
            }
            if (this.debug) logLocal("Cluster JMS broadcast period: "+this.clusterBroadcastPeriod);
        }
        // Load config: cluster initial context factory - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_CLUSTER_INITIAL_CONTEXT_FACTORY); // z.B. org.exolab.jms.jndi.InitialContextFactory bei openJMS kann man nehmen
            if (value == null) value = "";
            this.clusterInitialContextFactory = value.trim();
            if (this.debug) logLocal("Cluster initial context factory: "+value);
        }
        // Load config: cluster JMS provider URL - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_CLUSTER_JMS_PROVIDER_URL); // z.B. tcp://localhost:3035/ bei openJMS kann man nehmen
            if (value == null) value = "";
            this.clusterJmsProviderUrl = value.trim();
            if (this.debug) logLocal("Cluster JMS provider URL: "+value);
        }
        // Load config: cluster JMS connection factory - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_CLUSTER_JMS_CONNECTION_FACTORY); // z.B. JmsTopicConnectionFactory bei openJMS kann man nehmen
            if (value == null) value = "";
            this.clusterJmsConnectionFactory = value.trim();
            if (this.debug) logLocal("Cluster JMS connection factory: "+value);
        }
        // Load config: cluster JMS topic - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_CLUSTER_JMS_TOPIC);
            if (value == null) value = "";
            this.clusterJmsTopic = value.trim();
            if (this.debug) logLocal("Cluster JMS topic: "+value);
        }
        
        // Load config: housekeeping interval minutes - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_HOUSEKEEPING_INTERVAL);
            if (value == null) value = "15";
            try {
                this.housekeepingIntervalMinutes = Integer.parseInt(value.trim());
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured 'housekeeping interval': "+value);
            }
            if (this.debug) logLocal("Housekeeping interval minutes: "+this.housekeepingIntervalMinutes);
        }
        
        // Load config: rule reloading interval minutes - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_RULE_RELOADING_INTERVAL);
            if (value == null) value = configManager.getConfigurationValue(LEGACY_PARAM_RULE_FILE_RELOADING_INTERVAL); // only for backwards-compatibility to old param name
            if (value == null) value = "0"; // defaults to 0 to disable rule reloading (for security reasons)
            try {
                this.ruleFileReloadingIntervalMillis = Integer.parseInt(value.trim()) * 60 * 1000L;
                if (this.ruleFileReloadingIntervalMillis > 0) {
                    this.nextRuleReloadingTime = System.currentTimeMillis() + this.ruleFileReloadingIntervalMillis;
                }
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured 'rule reloading interval': "+value);
            }
            if (this.debug) logLocal("Rule reloading interval minutes: "+value);
        }

        // Load config: config reloading interval minutes - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_CONFIG_RELOADING_INTERVAL);
            if (value == null) value = "0"; // defaults to 0 to disable config reloading (for security reasons)
            try {
                this.configReloadingIntervalMillis = Integer.parseInt(value.trim()) * 60 * 1000L;
                if (this.configReloadingIntervalMillis > 0) {
                    this.nextConfigReloadingTime = System.currentTimeMillis() + this.configReloadingIntervalMillis;
                }
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured 'config reloading interval': "+value);
            }
            if (this.debug) logLocal("Config reloading interval minutes: "+value);
        }
        
        // Load config: block period minutes - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_BLOCK_ATTACKING_CLIENTS_DURATION);
            if (value == null) value = "20";
            try {
                this.blockPeriodMinutes = Integer.parseInt(value.trim());
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured 'block period minutes': "+value);
            }
            if (this.debug) logLocal("Block period minutes: "+this.blockPeriodMinutes);
        }
        
        // Load config: reset period minutes (attack) - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_RESET_PERIOD_ATTACK);
            if (value == null) value = "10";
            try {
                this.resetPeriodMinutesAttack = Integer.parseInt(value.trim());
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured 'reset period minutes (attack)': "+value);
            }
            if (this.debug) logLocal("Reset period minutes (attack): "+value);
        }
        // Load config: reset period minutes (session creation) - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_RESET_PERIOD_SESSION_CREATION);
            if (value == null) value = "5";
            try {
                this.resetPeriodMinutesSessionCreation = Integer.parseInt(value.trim());
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured 'reset period minutes (session creation)': "+value);
            }
            if (this.debug) logLocal("Reset period minutes (session creation): "+value);
        }
        // Load config: reset period minutes (bad response code) - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_RESET_PERIOD_BAD_RESPONSE_CODE);
            if (value == null) value = "2"; // TODO: evtl. nicht sogar lieber 1 minute als default nehmen ?
            try {
                this.resetPeriodMinutesBadResponseCode = Integer.parseInt(value.trim());
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured 'reset period minutes (bad response code)': "+value);
            }
            if (this.debug) logLocal("Reset period minutes (bad response code): "+value);
        }
        // Load config: reset period minutes (redirect threshold) - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_RESET_PERIOD_REDIRECT_THRESHOLD);
            if (value == null) value = "2"; // TODO: evtl. nicht sogar lieber 1 minute als default nehmen ?
            try {
                this.resetPeriodMinutesRedirectThreshold = Integer.parseInt(value.trim());
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured 'reset period minutes (redirect threshold)': "+value);
            }
            if (this.debug) logLocal("Reset period minutes (redirect threshold): "+value);
        }
        
        // Load config: flush response - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_FLUSH_RESPONSE);
            if (value == null) value = "true";
            this.flushResponse = (""+true).equals( value.trim().toLowerCase() );
            if (this.debug) logLocal("Flush response: "+this.flushResponse);
        }
        
        // Load config: invalidate session on attack - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_INVALIDATE_SESSION_ON_ATTACK);
            if (value == null) value = "false";
            this.invalidateSessionOnAttack = (""+true).equals( value.trim().toLowerCase() );
            if (this.debug) logLocal("Invalidate session on attack: "+this.invalidateSessionOnAttack);
        }
        
        // Load config: log verbose for development mode - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_LOG_VERBOSE_FOR_DEVELOPMENT_MODE);
            if (value == null) value = "false";
            this.logVerboseForDevelopmentMode = (""+true).equals( value.trim().toLowerCase() );
            if (this.debug) logLocal("Log verbose for development mode: "+this.logVerboseForDevelopmentMode);
        }

        // Load config: block repeated redirects threshold - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_BLOCK_REPEATED_REDIRECTS_THRESHOLD);
            if (value == null) value = "150";
            try {
                this.blockRepeatedRedirectsThreshold = Integer.parseInt(value.trim());
                if (this.blockRepeatedRedirectsThreshold < 0) throw new UnavailableException("Configured 'block repeated redirects threshold' must not be negative: "+value);
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured 'block repeated redirects threshold': "+value);
            }
            if (this.debug) logLocal("Block repeated redirects threshold: "+this.blockRepeatedRedirectsThreshold);
        }

        // Load config: remove sensitive data request param name pattern - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_REMOVE_SENSITIVE_DATA_REQUEST_PARAM_NAME_PATTERN);
            if (value == null) value = "(?i)p(?:ass)?(?:wor[dt]|phrase|wd)|kennwort";
            try {
                this.removeSensitiveDataRequestParamNamePattern = Pattern.compile(value.trim()); // TODO: hier auch einen leeren String = disabled zulassen !!
                if (this.debug) logLocal("Remove sensitive data request param name pattern: "+value);
            } catch (PatternSyntaxException ex) {
                throw new UnavailableException("Unable to compile regular expression pattern for "+PARAM_REMOVE_SENSITIVE_DATA_REQUEST_PARAM_NAME_PATTERN+": "+ex.getMessage());
            }
        }

        // Load config: remove sensitive data value pattern - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_REMOVE_SENSITIVE_DATA_VALUE_PATTERN);
            if (value == null) value = "(?:\\d{4}[- \\+]){3}\\d{4}|(?:(?!000)([0-6]\\d{2}|7([0-6]\\d|7[012]))([ -]?)(?!00)\\d\\d\\3(?!0000)\\d{4})"; // = Creditcard number as well as SSN number value
            try {
                this.removeSensitiveDataValuePattern = Pattern.compile(value.trim()); // TODO: hier auch einen leeren String = disabled zulassen !!
                if (this.debug) logLocal("Remove sensitive data value pattern: "+value);
            } catch (PatternSyntaxException ex) {
                throw new UnavailableException("Unable to compile regular expression pattern for "+PARAM_REMOVE_SENSITIVE_DATA_VALUE_PATTERN+": "+ex.getMessage());
            }
        }
        
        
        // Load config: log client user data - OPTIONAL
        boolean logClientUserData; // used in the next config item only (see below)
        {
            String value = configManager.getConfigurationValue(PARAM_LOG_CLIENT_USER_DATA);
            if (value == null) value = ""+true;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            logClientUserData = flag;
            if (this.debug) logLocal("Log client user data: "+flag);
        }
        
        // Load config: block attacking clients threshold - OPTIONAL
        {
            final AttackLogger attackLogger; // only used below inside the AttackHandler
            String value = configManager.getConfigurationValue(PARAM_ATTACK_LOGGER);
            if (value == null) value = "org.webcastellum.DefaultAttackLogger"; // = this implementation has defaults too so that it does not require any further mandatory init-params
            value = value.trim();
            if (value.length() == 0) throw new UnavailableException("Filter init-param is empty: "+PARAM_ATTACK_LOGGER);
            try {
                attackLogger = (AttackLogger) Class.forName(value).newInstance();
                attackLogger.setFilterConfig(filterConfig);
            } catch (ClassNotFoundException e) {
                throw new UnavailableException("Unable to find geo-locator class ("+value+"): "+e.getMessage());
            } catch (InstantiationException e) {
                throw new UnavailableException("Unable to instantiate geo-locator ("+value+"): "+e.getMessage());
            } catch (IllegalAccessException e) {
                throw new UnavailableException("Unable to access geo-locator ("+value+"): "+e.getMessage());
            } catch (FilterConfigurationException e) {
                throw new UnavailableException("Unable to configure geo-locator ("+value+"): "+e.getMessage());
            } catch (RuntimeException e) {
                throw new UnavailableException("Unable to use geo-locator ("+value+"): "+e.getMessage());
            }
            assert attackLogger != null;

            String blockAttackingClientsThresholdValue = configManager.getConfigurationValue(PARAM_BLOCK_ATTACKING_CLIENTS_THRESHOLD);
            if (blockAttackingClientsThresholdValue == null) blockAttackingClientsThresholdValue = "0";
            try {
                this.attackHandler = new AttackHandler( attackLogger, Integer.parseInt(blockAttackingClientsThresholdValue.trim()), housekeepingIntervalMinutes*60*1000L, blockPeriodMinutes*60*1000L, resetPeriodMinutesAttack*60*1000L, resetPeriodMinutesRedirectThreshold*60*1000L,
                        this.learningModeAggregationDirectory, this.applicationName, this.logSessionValuesOnAttack, this.invalidateSessionOnAttack, 
                        this.blockRepeatedRedirectsThreshold, this.isProductionMode , this.logVerboseForDevelopmentMode, this.removeSensitiveDataRequestParamNamePattern, this.removeSensitiveDataValuePattern, logClientUserData );
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured 'block attacking clients threshold': "+blockAttackingClientsThresholdValue);
            }
            assert this.attackHandler != null;
            if (this.debug) logLocal("Attack handler with block attacking clients threshold: "+this.attackHandler.getBlockAttackingClientsThreshold());
        }
        
        // Load config: geo locator - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_GEO_LOCATOR);
            if (value == null) value = "org.webcastellum.DefaultGeoLocator"; // = this implementation has defaults too so that it does not require any further mandatory init-params
            value = value.trim();
            if (value.length() == 0) throw new UnavailableException("Filter init-param is empty: "+PARAM_GEO_LOCATOR);
            try {
                final GeoLocator geoLocator = (GeoLocator) Class.forName(value).newInstance();
                geoLocator.setFilterConfig(filterConfig);
                this.geoLocatingCache = new GeoLocatingCache(geoLocator, housekeepingIntervalMinutes*60*1000);
            } catch (ClassNotFoundException e) {
                throw new UnavailableException("Unable to find geo-locator class ("+value+"): "+e.getMessage());
            } catch (InstantiationException e) {
                throw new UnavailableException("Unable to instantiate geo-locator ("+value+"): "+e.getMessage());
            } catch (IllegalAccessException e) {
                throw new UnavailableException("Unable to access geo-locator ("+value+"): "+e.getMessage());
            } catch (FilterConfigurationException e) {
                throw new UnavailableException("Unable to configure geo-locator ("+value+"): "+e.getMessage());
            } catch (RuntimeException e) {
                throw new UnavailableException("Unable to use geo-locator ("+value+"): "+e.getMessage());
            }
            assert this.geoLocatingCache != null;
        }
        
        // Load config: entry-point file path - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_ENTRY_POINT_FILES);
            if (value == null) value = "entry-points";
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.entryPointDefinitions = new EntryPointDefinitionContainer(ruleFileLoader);
                final String message = this.entryPointDefinitions.parseDefinitions();
                if (this.entryPointDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                if (this.entryPointDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                if (this.entryPointDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                if (this.entryPointDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                logLocal(message);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load entry point definitions: "+ex.getMessage());
            }
            assert this.entryPointDefinitions != null;
        }
        
        // Load config: optimization-hint file path - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_OPTIMIZATION_HINT_FILES);
            if (value == null) value = "optimization-hints";
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.optimizationHintDefinitions = new OptimizationHintDefinitionContainer(ruleFileLoader);
                final String message = this.optimizationHintDefinitions.parseDefinitions();
                if (this.optimizationHintDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                if (this.optimizationHintDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                if (this.optimizationHintDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                if (this.optimizationHintDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                logLocal(message);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load optimization hint definitions: "+ex.getMessage());
            }
            assert this.optimizationHintDefinitions != null;
        }
        
        // Load config: renewSession-point file path - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_RENEW_SESSION_AND_TOKEN_POINT_FILES);
            if (value == null) value = "renew-session-and-token-points";
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.renewSessionPointDefinitions = new RenewSessionAndTokenPointDefinitionContainer(ruleFileLoader);
                final String message = this.renewSessionPointDefinitions.parseDefinitions();
                if (this.renewSessionPointDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                if (this.renewSessionPointDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                if (this.renewSessionPointDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                if (this.renewSessionPointDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                logLocal(message);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load renew-session-and-token point definitions: "+ex.getMessage());
            }
            assert this.renewSessionPointDefinitions != null;
        }
        
        // Load config: captcha-point file path - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_CAPTCHA_POINT_FILES);
            if (value == null) value = "captcha-points";
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.captchaPointDefinitions = new CaptchaPointDefinitionContainer(ruleFileLoader);
                final String message = this.captchaPointDefinitions.parseDefinitions();
                if (this.captchaPointDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                if (this.captchaPointDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                if (this.captchaPointDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                if (this.captchaPointDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                logLocal(message);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load captcha point definitions: "+ex.getMessage());
            }
            assert this.captchaPointDefinitions != null;
        }
        
        // Load config: incomingProtectionExclude-point file path - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_INCOMING_PROTECTION_EXCLUDE_FILES);
            if (value == null) value = "incoming-protection-excludes";
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.incomingProtectionExcludeDefinitions = new IncomingProtectionExcludeDefinitionContainer(ruleFileLoader);
                final String message = this.incomingProtectionExcludeDefinitions.parseDefinitions();
                if (this.incomingProtectionExcludeDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                if (this.incomingProtectionExcludeDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                if (this.incomingProtectionExcludeDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                if (this.incomingProtectionExcludeDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                logLocal(message);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load incoming-protection exclude definitions: "+ex.getMessage());
            }
            assert this.incomingProtectionExcludeDefinitions != null;
        }
        
        // Load config: response-modifications file path - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_RESPONSE_MODIFICATION_FILES);
            if (value == null) value = RESPONSE_MODIFICATIONS_DEFAULT;
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.responseModificationDefinitions = new ResponseModificationDefinitionContainer(ruleFileLoader);
                final String message = this.responseModificationDefinitions.parseDefinitions();
                if (this.responseModificationDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                if (this.responseModificationDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                if (this.responseModificationDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                if (this.responseModificationDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                logLocal(message);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load response modification definitions: "+ex.getMessage());
            }
            assert this.responseModificationDefinitions != null;
        }
        
        // Load config: white-list request file path - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_WHITELIST_REQUESTS_FILES);
            if (value == null) value = "whitelist-requests";
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.whiteListDefinitions = new WhitelistRequestDefinitionContainer(ruleFileLoader);
                final String message = this.whiteListDefinitions.parseDefinitions();
                if (this.whiteListDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                if (this.whiteListDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                if (this.whiteListDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                if (this.whiteListDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                logLocal(message);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load white-list definitions: "+ex.getMessage());
            }
            assert this.whiteListDefinitions != null;
        }
        
        // Load config: bad-request file path - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_BAD_REQUEST_FILES);
            if (value == null) value = "bad-requests";
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.badRequestDefinitions = new BadRequestDefinitionContainer(ruleFileLoader);
                final String message = this.badRequestDefinitions.parseDefinitions();
                if (this.badRequestDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                if (this.badRequestDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                if (this.badRequestDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                if (this.badRequestDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                logLocal(message);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load bad request definitions: "+ex.getMessage());
            }
            assert this.badRequestDefinitions != null;
        }
        
        // Load config: DoS-Limit file path - OPTIONAL
        {
            assert this.attackHandler != null;
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_DOS_LIMIT_FILES);
            if (value == null) value = "denial-of-service-limits";
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.denialOfServiceLimitDefinitions = new DenialOfServiceLimitDefinitionContainer(ruleFileLoader);
                final String message = this.denialOfServiceLimitDefinitions.parseDefinitions();
                if (this.denialOfServiceLimitDefinitions.isHavingEnabledQueryStringCheckingRules()) isHavingEnabledQueryStringCheckingRules = true;
                if (this.denialOfServiceLimitDefinitions.isHavingEnabledRequestParamCheckingRules()) isHavingEnabledRequestParameterCheckingRules = true;
                if (this.denialOfServiceLimitDefinitions.isHavingEnabledHeaderCheckingRules()) isHavingEnabledHeaderCheckingRules = true;
                if (this.denialOfServiceLimitDefinitions.isHavingEnabledCookieCheckingRules()) isHavingEnabledCookieCheckingRules = true;
                logLocal(message);
                this.denialOfServiceLimitCounter = new DenialOfServiceLimitTracker(this.attackHandler/*, this.denialOfServiceLimitDefinitions*/, housekeepingIntervalMinutes*60*1000L);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load DoS limit definitions: "+ex.getMessage());
            }
            assert this.denialOfServiceLimitDefinitions != null;
            assert this.denialOfServiceLimitCounter != null;
        }
        
        // Load config: total exclude request file path - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_TOTAL_EXCLUDE_FILES);
            if (value == null) value = "total-excludes";
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.totalExcludeDefinitions = new TotalExcludeDefinitionContainer(ruleFileLoader);
                final String message = this.totalExcludeDefinitions.parseDefinitions();
                logLocal(message);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load total-excludes definitions: "+ex.getMessage());
            }
            assert this.totalExcludeDefinitions != null;
        }
        
        // Load config: content modification exclude request file path - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_CONTENT_MODIFICATION_EXCLUDE_FILES);
            if (value == null) value = MODIFICATION_EXCLUDES_DEFAULT;
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.contentModificationExcludeDefinitions = new ContentModificationExcludeDefinitionContainer(ruleFileLoader);
                final String message = this.contentModificationExcludeDefinitions.parseDefinitions();
                this.contentInjectionHelper.setContentModificationExcludeDefinitions(this.contentModificationExcludeDefinitions);
                logLocal(message);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load content-modification-excludes definitions: "+ex.getMessage());
            }
            assert this.contentModificationExcludeDefinitions != null;
        }
        
        // Load config: size limit request file path - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_SIZE_LIMIT_FILES);
            if (value == null) value = "size-limits";
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.sizeLimitDefinitions = new SizeLimitDefinitionContainer(ruleFileLoader);
                final String message = this.sizeLimitDefinitions.parseDefinitions();
                logLocal(message);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load size-limits definitions: "+ex.getMessage());
            }
            assert this.sizeLimitDefinitions != null;
        }
        
        // Load config: multipart size limit request file path - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_MULTIPART_SIZE_LIMIT_FILES);
            if (value == null) value = "multipart-size-limits";
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.multipartSizeLimitDefinitions = new MultipartSizeLimitDefinitionContainer(ruleFileLoader);
                final String message = this.multipartSizeLimitDefinitions.parseDefinitions();
                logLocal(message);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load multipart-size-limits definitions: "+ex.getMessage());
            }
            assert this.multipartSizeLimitDefinitions != null;
        }
        
        // Load config: decoding permutation file path - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_DECODING_PERMUTATION_FILES);
            if (value == null) value = "decoding-permutations";
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.decodingPermutationDefinitions = new DecodingPermutationDefinitionContainer(ruleFileLoader);
                final String message = this.decodingPermutationDefinitions.parseDefinitions();
                logLocal(message);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load decoding-permutations definitions: "+ex.getMessage());
            }
            assert this.decodingPermutationDefinitions != null;
        }
        
        // Load config: form field masking exclude request file path - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PATH_TO_FORM_FIELD_MASKING_EXCLUDE_FILES);
            if (value == null) value = "form-field-masking-excludes";
            try {
                assert this.ruleFileLoaderClass != null;
                final RuleFileLoader ruleFileLoader = (RuleFileLoader) this.ruleFileLoaderClass.newInstance();
                ruleFileLoader.setFilterConfig(filterConfig);
                ruleFileLoader.setPath(value);
                this.formFieldMaskingExcludeDefinitions = new FormFieldMaskingExcludeDefinitionContainer(ruleFileLoader);
                final String message = this.formFieldMaskingExcludeDefinitions.parseDefinitions();
                this.contentInjectionHelper.setFormFieldMaskingExcludeDefinitions(this.formFieldMaskingExcludeDefinitions);
                logLocal(message);
            } catch (Exception ex) {
                throw new UnavailableException("Unable to load form-field-masking-excludes definitions: "+ex.getMessage());
            }
            assert this.formFieldMaskingExcludeDefinitions != null;
        }


        
        // Load config: secret token link injection - OPTIONAL
        {
            String applyProtectiveLinkInjectionValue = configManager.getConfigurationValue(PARAM_SECRET_TOKEN_LINK_INJECTION);
            if (applyProtectiveLinkInjectionValue == null) applyProtectiveLinkInjectionValue = "false";
            final boolean flag = (""+true).equals( applyProtectiveLinkInjectionValue.trim().toLowerCase() );
            this.contentInjectionHelper.setInjectSecretTokenIntoLinks(flag);
            if (this.debug) logLocal("Apply secret token link injection: "+flag);
        } // ordering is important here, since the features depend on each other
        // Load config: encrypt query strings - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_ENCRYPT_QUERY_STRINGS);
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.contentInjectionHelper.setEncryptQueryStringInLinks(flag);
            if (this.debug) logLocal("Encrypt query strings: "+flag);
            // this feature depends on another feature:
            if (this.contentInjectionHelper.isEncryptQueryStringInLinks() && !this.contentInjectionHelper.isInjectSecretTokenIntoLinks()) {
                throw new UnavailableException("When 'query string encryption' is activated the feature 'secret token link injection' must be activated also");
            }
        } // ordering is important here, since the features depend on each other
        // Load config: extra encrypted value hash protection - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_HASH_PROTECTION);
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.extraEncryptedValueHashProtection = flag;
            if (this.debug) logLocal("Apply extra encrypted value hash protection: "+flag);
            // this feature depends on another feature:
            if (this.extraEncryptedValueHashProtection && !this.contentInjectionHelper.isEncryptQueryStringInLinks()) {
                throw new UnavailableException("When 'extra encrypted value hash protection' is activated the feature 'query string encryption' must be activated also");
            }
        }
        // Load config: extra encrypted full path resource protection - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_FULL_PATH_PROTECTION);
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.useFullPathForResourceToBeAccessedProtection = flag;
            if (this.debug) logLocal("Apply extra encrypted full path resource protection: "+flag);
            // this feature depends on another feature(s):
            if (this.useFullPathForResourceToBeAccessedProtection) {
                if (!this.contentInjectionHelper.isEncryptQueryStringInLinks()) throw new UnavailableException("When 'extra encrypted full path resource protection' is activated the feature 'query string encryption' must be activated also");
            }
        } // ordering is important here, since the features depend on each other
        // Load config: extra encrypted medium path resource removal - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_MEDIUM_PATH_REMOVAL);
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.additionalMediumResourceRemoval = flag;
            this.contentInjectionHelper.setExtraMediumPathRemoval(flag);
            if (this.debug) logLocal("Apply extra encrypted medium path resource removal: "+flag);
            // this feature depends on another feature:
            if (this.additionalMediumResourceRemoval && !this.contentInjectionHelper.isEncryptQueryStringInLinks()) {
                throw new UnavailableException("When 'extra encrypted medium path resource removal' is activated the feature 'query string encryption' must be activated also");
            }
        } // ordering is important here, since the features depend on each other
        // Load config: extra encrypted full path resource removal - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_FULL_PATH_REMOVAL);
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.additionalFullResourceRemoval = flag;
            this.contentInjectionHelper.setExtraFullPathRemoval(flag);
            if (this.debug) logLocal("Apply extra encrypted full path resource removal: "+flag);
            // this feature depends on another feature:
            if (this.additionalFullResourceRemoval) {
                if (!this.useFullPathForResourceToBeAccessedProtection) throw new UnavailableException("When 'extra encrypted full path resource removal' is activated the feature 'extra encrypted full path resource protection' must be activated also");
                if (!this.contentInjectionHelper.isEncryptQueryStringInLinks()) throw new UnavailableException("When 'extra encrypted full path resource removal' is activated the feature 'query string encryption' must be activated also");
                if (this.additionalMediumResourceRemoval) throw new UnavailableException("When 'extra encrypted full path resource removal' is activated the feature 'extra encrypted medium path resource removal' makes no sense and should be deactivated");
            }
        } // ordering is important here, since the features depend on each other
        // plausibility check
        if (this.useFullPathForResourceToBeAccessedProtection) { 
            // Wenn ExtraEncryptedFullPathProtection aktiv ist auch ein ExtraEncryptedMediumPathRemoval oder ein ExtraEncryptedFullPathRemoval fordern, da ansonsten bei Formularen mit leeren Actions es nur bei ExtraEncryptedFullPathProtection ein Mismatch der Resource-to-be-Accessed gibt... daher einfach diese beiden optionen koppeln...
            if (!this.additionalMediumResourceRemoval && !this.additionalFullResourceRemoval) throw new UnavailableException("When 'extra encrypted full path resource protection' is activated either the feature 'extra encrypted medium path resource removal' or 'extra encrypted full path resource removal' must be activated also");
        }
        if (this.additionalMediumResourceRemoval && this.additionalFullResourceRemoval) {
            throw new UnavailableException("The features 'extra encrypted medium path resource removal' and 'extra encrypted full path resource removal' must not be activated both (does not make sense)");
        }
        // Load config: Block multipart requests for non-multipart forms - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_BLOCK_MULTIPART_REQUESTS_FOR_NON_MULTIPART_FORMS);
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.blockMultipartRequestsForNonMultipartForms = flag;
            if (this.debug) logLocal("Block multipart requests for non-multipart forms: "+flag);
            // this feature depends on another feature:
            if (this.blockMultipartRequestsForNonMultipartForms && !this.contentInjectionHelper.isEncryptQueryStringInLinks()) {
                throw new UnavailableException("When 'block multipart requests for non-multipart forms' is activated the feature 'query string encryption' must be activated also");
            }
        }
        // Load config: parameter and form protection - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PARAMETER_AND_FORM_PROTECTION);
            if (value == null) value = "false";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.contentInjectionHelper.setProtectParametersAndForms(flag);
            if (this.debug) logLocal("Apply parameter and form protection: "+flag);
            // this feature depends on another feature:
            if (this.contentInjectionHelper.isProtectParametersAndForms() && !this.contentInjectionHelper.isEncryptQueryStringInLinks()) {
                throw new UnavailableException("When 'parameter and form protection' is activated the feature 'query string encryption' must be activated also");
            }
        } // ordering is important here, since the features depend on each other
        // Load config: Extra strict parameter checking for encrypted links - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_STRICT_PARAMETER_CHECKING_FOR_ENCRYPTED_LINKS); // wenn aktiviert, dann ist's strenger ud zugleich schneller bei Links als herkoemmliche ParameterAndFormProtection (aber nur bei Links, nicht bei Forms)...
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.contentInjectionHelper.setExtraStrictParameterCheckingForEncryptedLinks(flag);
            if (this.debug) logLocal("Extra strict parameter checking for encrypted links: "+flag);
            // this feature depends on another feature:
            if (this.contentInjectionHelper.isExtraStrictParameterCheckingForEncryptedLinks() && !this.contentInjectionHelper.isProtectParametersAndForms()) {
                throw new UnavailableException("When 'extra strict parameter checking for encrypted links' is activated the feature 'parameter and form protection' must be activated also");
            }
        }
        // Load config: extra disabled form field protection - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_DISABLED_FORM_FIELD_PROTECTION);
            if (value == null) value = "false";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.contentInjectionHelper.setExtraProtectDisabledFormFields(flag);
            if (this.debug) logLocal("Apply extra disabled form field protection: "+flag);
            // this feature depends on another feature:
            if (this.contentInjectionHelper.isExtraProtectDisabledFormFields() && !this.contentInjectionHelper.isProtectParametersAndForms()) {
                throw new UnavailableException("When 'extra disabled form field protection' is activated the feature 'parameter and form protection' must be activated also");
            }
        } // ordering is important here, since the features depend on each other
        // Load config: extra readonly form field protection - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_READONLY_FORM_FIELD_PROTECTION);
            if (value == null) value = "false";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.contentInjectionHelper.setExtraProtectReadonlyFormFields(flag);
            if (this.debug) logLocal("Apply extra readonly form field protection: "+flag);
            // this feature depends on another feature:
            if (this.contentInjectionHelper.isExtraProtectReadonlyFormFields() && !this.contentInjectionHelper.isProtectParametersAndForms()) {
                throw new UnavailableException("When 'extra readonly form field protection' is activated the feature 'parameter and form protection' must be activated also");
            }
        }
        // Load config: extra request-param value count protection - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_REQUEST_PARAM_VALUE_COUNT_PROTECTION);
            if (value == null) value = "false";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.contentInjectionHelper.setExtraProtectRequestParamValueCount(flag);
            if (this.debug) logLocal("Apply extra request-param value count protection: "+flag);
            // this feature depends on another feature:
            if (this.contentInjectionHelper.isExtraProtectRequestParamValueCount() && !this.contentInjectionHelper.isProtectParametersAndForms()) {
                throw new UnavailableException("When 'extra request-param value count protection' is activated the feature 'parameter and form protection' must be activated also");
            }
        }
        // Load config: extra hidden form field protection - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_HIDDEN_FORM_FIELD_PROTECTION);
            if (value == null) value = "false";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.hiddenFormFieldProtection = flag;
            if (this.debug) logLocal("Apply extra hidden form field protection: "+flag);
            // this feature depends on another feature:
            if (flag && !this.contentInjectionHelper.isProtectParametersAndForms()) {
                throw new UnavailableException("When 'extra hidden form field protection' is activated the feature 'parameter and form protection' must be activated also");
            }
        }
        // Load config: extra selectbox protection - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_SELECTBOX_PROTECTION);
            if (value == null) value = "false";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.selectboxProtection = flag;
            if (this.debug) logLocal("Apply extra selectbox protection: "+flag);
            // this feature depends on another feature:
            if (flag && !this.contentInjectionHelper.isProtectParametersAndForms()) {
                throw new UnavailableException("When 'extra selectbox protection' is activated the feature 'parameter and form protection' must be activated also");
            }
        }
        // Load config: extra checkbox protection - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_CHECKBOX_PROTECTION);
            if (value == null) value = "false";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.checkboxProtection = flag;
            if (this.debug) logLocal("Apply extra checkbox protection: "+flag);
            // this feature depends on another feature:
            if (flag && !this.contentInjectionHelper.isProtectParametersAndForms()) {
                throw new UnavailableException("When 'extra checkbox protection' is activated the feature 'parameter and form protection' must be activated also");
            }
        }
        // Load config: extra radiobutton protection - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_RADIOBUTTON_PROTECTION);
            if (value == null) value = "false";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.radiobuttonProtection = flag;
            if (this.debug) logLocal("Apply extra radiobutton protection: "+flag);
            // this feature depends on another feature:
            if (flag && !this.contentInjectionHelper.isProtectParametersAndForms()) {
                throw new UnavailableException("When 'extra radiobutton protection' is activated the feature 'parameter and form protection' must be activated also");
            }
        }
        // Load config: extra iamge map parameter exclude - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_IMAGE_MAP_PARAMETER_EXCLUDE);
            if (value == null) value = "false";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.imageMapParameterExclude = flag;
            if (this.debug) logLocal("Apply extra image map parameter exclude: "+flag);
            // this feature depends on another feature(s):
            if (flag) {
                if (!this.contentInjectionHelper.isProtectParametersAndForms()) throw new UnavailableException("When 'extra image map parameter exclude' is activated the feature 'parameter and form protection' must be activated also");
            }
        }
        // Load config: extra Session Timeout Handling - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_SESSION_TIMEOUT_HANDLING);
            if (value == null) value = "false";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.extraSessionTimeoutHandling = flag;
            if (this.debug) logLocal("Apply extra session timeout handling: "+flag);
        }
        // Load config: extra selectbox value masking - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_SELECTBOX_VALUE_MASKING);
            if (value == null) value = "false";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.selectboxValueMasking = flag;
            if (this.debug) logLocal("Apply extra selectbox value masking: "+flag);
            // this feature depends on another feature(s):
            if (flag) {
                if (!this.contentInjectionHelper.isProtectParametersAndForms()) throw new UnavailableException("When 'extra selectbox value masking' is activated the feature 'parameter and form protection' must be activated also");
                if (!this.selectboxProtection) throw new UnavailableException("When 'extra selectbox value masking' is activated the feature 'extra selectbox protection' must be activated also");
            }
        }
        // Load config: extra checkbox value masking - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_CHECKBOX_VALUE_MASKING);
            if (value == null) value = "false";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.checkboxValueMasking = flag;
            if (this.debug) logLocal("Apply extra checkbox value masking: "+flag);
            // this feature depends on another feature(s):
            if (flag) {
                if (!this.contentInjectionHelper.isProtectParametersAndForms()) throw new UnavailableException("When 'extra checkbox value masking' is activated the feature 'parameter and form protection' must be activated also");
                if (!this.checkboxProtection) throw new UnavailableException("When 'extra checkbox value masking' is activated the feature 'extra checkbox protection' must be activated also");
            }
        }
        // Load config: extra radiobutton value masking - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_EXTRA_RADIOBUTTON_VALUE_MASKING);
            if (value == null) value = "false";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.radiobuttonValueMasking = flag;
            if (this.debug) logLocal("Apply extra radiobutton value masking: "+flag);
            // this feature depends on another feature(s):
            if (flag) {
                if (!this.contentInjectionHelper.isProtectParametersAndForms()) throw new UnavailableException("When 'extra radiobutton value masking' is activated the feature 'parameter and form protection' must be activated also");
                if (!this.radiobuttonProtection) throw new UnavailableException("When 'extra radiobutton value masking' is activated the feature 'extra radiobutton protection' must be activated also");
            }
        }
        
        
        // Load config: treat non-matchig of servletPath as a match for whitelist rules - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_TREAT_NON_MATCHING_SERVLET_PATH_AS_MATCH_FOR_WHITELIST_RULES);
            if (value == null) value = "false";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.treatNonMatchingServletPathAsMatchForWhitelistRules = flag;
            if (this.debug) logLocal("Treat non-matchig of servletPath as a match for whitelist rules: "+flag);
        }
        
        
        // Load config: remember last captcha for multi submits - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_REMEMBER_LAST_CAPTCHA_FOR_MULTI_SUBMITS);
            if (value == null) value = "true";
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.rememberLastCaptchaForMultiSubmits = flag;
            if (this.debug) logLocal("Remember last captcha for multi submits: "+flag);
        }
        
        // Load config: append question mark or ampersand to links - OPTIONAL
        {
            // used to support JavaScript addition of further parameters by the application when the application has a hard-coded "?" as parameter query-string opener since it does not expect WebCastellum to introduce its own params and therefore uses a "?" instead of the correct "&"... using this trick (to append a trailing "&" by WebCastellum) all JavaScripts that append "?" or "&" work... that's the case since ebCastellum removed leading "?" in request-param-names when this flag is activated... therefore it should be set to true when the application uses JavaScript to dynamically append params in URLs. Note: using true might cause some ignorable tomcat warnings that the URL looks strange (contains && or trailing &), but that is cused deliberately when setting this flag to true
            String value = configManager.getConfigurationValue(PARAM_APPEND_QUESTIONMARK_OR_AMPERSAND_TO_LINKS);
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.appendQuestionmarkOrAmpersandToLinks = flag;
            if (this.debug) logLocal("Append questionmark or ampersand to lnks: "+flag);
        }
        
        // Load config: append session-id to links - OPTIONAL
        {
            // nur sinnvoll, wenn der schlampige app-endtiwckler es an vielen URLs vergessen hat ein resposne.encodeURL() aufzurufen... denn wenn dieses feature auto-append aktiviert ist und es zu weit greift und bei externen links die ID dranhaengt, waere das naemlich unnoetig doof....
            String value = configManager.getConfigurationValue(PARAM_APPEND_SESSIONID_TO_LINKS);
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.appendSessionIdToLinks = flag;
            if (this.debug) logLocal("Append session-id to lnks: "+flag);
        }
        
        
        // Load config: use tuned block parser - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_USE_TUNED_BLOCK_PARSER);
            if (value == null) value = ""+true;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.contentInjectionHelper.setUseTunedBlockParser(flag);
            if (this.debug) logLocal("Use tuned block parser: "+flag);
        }
        
        // Load config: use response buffering - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_USE_RESPONSE_BUFFERING);
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.contentInjectionHelper.setUseResponseBuffering(flag);
            if (this.debug) logLocal("Use response buffering: "+flag);
        }
        
        
        // Load config: strip html comments - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_STRIP_HTML_COMMENTS);
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.contentInjectionHelper.setStripHtmlComments(flag);
            if (this.debug) logLocal("Strip HTML comments: "+flag);
        }
        
        // Load config: block invalid encoded query string - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_BLOCK_INVALID_ENCODED_QUERY_STRING);
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.blockInvalidEncodedQueryString = flag;
            if (this.debug) logLocal("Block invalid encoded query string: "+flag);
        }
        
        // Load config: Attack reply HTTP status code or message resource [PROD] - OPTIONAL
        {
            String productionAttackReplyConfigValue = configManager.getConfigurationValue(PARAM_PROD_ATTACK_REPLY_STATUS_CODE_OR_MESSAGE_RESOURCE);
            if (productionAttackReplyConfigValue == null) productionAttackReplyConfigValue = "200"; // we're using HTTP Status-Code 200 (OK) instead of a message file like "org/webcastellum/attack.html" to be even safer and fool attackers
            try {
                this.productionAttackReplyStatusCode = Integer.parseInt(productionAttackReplyConfigValue.trim());
                if (this.productionAttackReplyStatusCode < 0) throw new UnavailableException("Configured HTTP status code to send as reply to attacks (in production mode) must not be negative: "+productionAttackReplyConfigValue);
            } catch(NumberFormatException e) {
                // treat as file pointer into classpath instead of treating it as a status code
                final InputStream input = WebCastellumFilter.class.getClassLoader().getResourceAsStream(productionAttackReplyConfigValue);
                if (input == null) throw new UnavailableException("Unable to number-parse configured HTTP status code to send as reply to attacks (in production mode) as well as unable to locate a resource in classpath with name: "+productionAttackReplyConfigValue);
                BufferedReader buffer = null;
                try {
                    buffer = new BufferedReader( new InputStreamReader(input) );
                    final StringBuilder content = new StringBuilder();
                    String line;
                    while ( (line=buffer.readLine()) != null ) {
                        content.append(line).append("\n");
                    }
                    this.productionAttackReplyMessage = content.toString().trim();
                } catch (Exception ex) {
                    throw new UnavailableException("Unable to load content from the specified resource in classpath with name: "+productionAttackReplyConfigValue);
                } finally {
                    if (buffer != null) try { buffer.close(); } catch (IOException ignored) {}
                }
            }
        }
        
        // Load config: Catch-All reply HTTP status code or message resource [PROD] - OPTIONAL
        {
            String productionExceptionReplyConfigValue = configManager.getConfigurationValue(PARAM_PROD_EXCEPTION_REPLY_STATUS_CODE_OR_MESSAGE_RESOURCE);
            if (productionExceptionReplyConfigValue == null) productionExceptionReplyConfigValue = "503";
            try {
                this.productionExceptionReplyStatusCode = Integer.parseInt(productionExceptionReplyConfigValue.trim());
                if (this.productionExceptionReplyStatusCode < 0) throw new UnavailableException("Configured HTTP status code to send as reply to exceptions (in production mode) must not be negative: "+productionExceptionReplyConfigValue);
            } catch(NumberFormatException e) {
                // treat as file pointer into classpath instead of treating it as a status code
                final InputStream input = WebCastellumFilter.class.getClassLoader().getResourceAsStream(productionExceptionReplyConfigValue);
                if (input == null) throw new UnavailableException("Unable to number-parse configured HTTP status code to send as reply to exceptions (in production mode) as well as unable to locate a resource in classpath with name: "+productionExceptionReplyConfigValue);
                BufferedReader buffer = null;
                try {
                    buffer = new BufferedReader( new InputStreamReader(input) );
                    final StringBuilder content = new StringBuilder();
                    String line;
                    while ( (line=buffer.readLine()) != null ) {
                        content.append(line).append("\n");
                    }
                    this.productionExceptionReplyMessage = content.toString().trim();
                } catch (Exception ex) {
                    throw new UnavailableException("Unable to load content from the specified resource in classpath with name: "+productionExceptionReplyConfigValue);
                } finally {
                    if (buffer != null) try { buffer.close(); } catch (IOException ignored) {}
                }
            }
        }
        
        // Load config: Attack reply HTTP status code or message resource [DEV] - OPTIONAL
        {
            String developmentAttackReplyConfigValue = configManager.getConfigurationValue(PARAM_DEV_ATTACK_REPLY_STATUS_CODE_OR_MESSAGE_RESOURCE);
            if (developmentAttackReplyConfigValue == null) developmentAttackReplyConfigValue = "org/webcastellum/attack.html";
            try {
                this.developmentAttackReplyStatusCode = Integer.parseInt(developmentAttackReplyConfigValue.trim());
                if (this.developmentAttackReplyStatusCode < 0) throw new UnavailableException("Configured HTTP status code to send as reply to attacks (in development mode) must not be negative: "+developmentAttackReplyConfigValue);
            } catch(NumberFormatException e) {
                // treat as file pointer into classpath instead of treating it as a status code
                final InputStream input = WebCastellumFilter.class.getClassLoader().getResourceAsStream(developmentAttackReplyConfigValue);
                if (input == null) throw new UnavailableException("Unable to number-parse configured HTTP status code to send as reply to attacks (in development mode) as well as unable to locate a resource in classpath with name: "+developmentAttackReplyConfigValue);
                BufferedReader buffer = null;
                try {
                    buffer = new BufferedReader( new InputStreamReader(input) );
                    final StringBuilder content = new StringBuilder();
                    String line;
                    while ( (line=buffer.readLine()) != null ) {
                        content.append(line).append("\n");
                    }
                    this.developmentAttackReplyMessage = content.toString().trim();
                } catch (Exception ex) {
                    throw new UnavailableException("Unable to load content from the specified resource in classpath with name: "+developmentAttackReplyConfigValue);
                } finally {
                    if (buffer != null) try { buffer.close(); } catch (IOException ignored) {}
                }
            }
        }
        
        // Load config: Catch-All reply HTTP status code or message resource [DEV] - OPTIONAL
        {
            String developmentExceptionReplyConfigValue = configManager.getConfigurationValue(PARAM_DEV_EXCEPTION_REPLY_STATUS_CODE_OR_MESSAGE_RESOURCE);
            if (developmentExceptionReplyConfigValue == null) developmentExceptionReplyConfigValue = "org/webcastellum/exception.html";
            try {
                this.developmentExceptionReplyStatusCode = Integer.parseInt(developmentExceptionReplyConfigValue.trim());
                if (this.developmentExceptionReplyStatusCode < 0) throw new UnavailableException("Configured HTTP status code to send as reply to exceptions (in development mode) must not be negative: "+developmentExceptionReplyConfigValue);
            } catch(NumberFormatException e) {
                // treat as file pointer into classpath instead of treating it as a status code
                final InputStream input = WebCastellumFilter.class.getClassLoader().getResourceAsStream(developmentExceptionReplyConfigValue);
                if (input == null) throw new UnavailableException("Unable to number-parse configured HTTP status code to send as reply to exceptions (in development mode) as well as unable to locate a resource in classpath with name: "+developmentExceptionReplyConfigValue);
                BufferedReader buffer = null;
                try {
                    buffer = new BufferedReader( new InputStreamReader(input) );
                    final StringBuilder content = new StringBuilder();
                    String line;
                    while ( (line=buffer.readLine()) != null ) {
                        content.append(line).append("\n");
                    }
                    this.developmentExceptionReplyMessage = content.toString().trim();
                } catch (Exception ex) {
                    throw new UnavailableException("Unable to load content from the specified resource in classpath with name: "+developmentExceptionReplyConfigValue);
                } finally {
                    if (buffer != null) try { buffer.close(); } catch (IOException ignored) {}
                }
            }
        }
        
        // Load config: handle uncaught exceptions - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_HANDLE_UNCAUGHT_EXCEPTIONS);
            if (value == null) value = ""+true;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.catchAll = flag;
            if (this.debug) logLocal("Handle uncaught exceptions: "+this.catchAll);
        }
        
        // Load config: reuse session content - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_REUSE_SESSION_CONTENT);
            if (value == null) value = ""+true;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.reuseSessionContent = flag;
            if (this.debug) logLocal("Reuse session content: "+flag);
        }
        
        // Load config: reuse session content - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_HIDE_INTERNAL_SESSION_ATTRIBUTES);
            if (value == null) value = ""+false; // should be left "false" since otherwise (when hidden) session serialization (which is done when the session is stored in the DB or on the Disk) won't work... since the container can't see the attributes when they're hidden
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.hideInternalSessionAttributes = flag;
            if (this.debug) logLocal("Hide internal session attributes: "+flag);
        }
        
        // Load config: parse multipart forms - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PARSE_MULTI_PART_FORMS);
            if (value == null) value = ""+true; // yes, true as default makes much sense here !!!! to avoid being circumvented when the application parses multipart forms and the filter does not
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.parseMultipartForms = flag;
            if (this.debug) logLocal("Parse multipart forms: "+flag);
        }
        // Load config: Allowed request mime types - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_ALLOWED_REQUEST_MIME_TYPES);
            if (value == null) value = "application/x-www-form-urlencoded,multipart/form-data,text/plain,text/xml,application/xml";
            this.allowedRequestMimeTypesLowerCased.clear();
            for (final StringTokenizer tokenizer = new StringTokenizer(value.toLowerCase().trim(), ","); tokenizer.hasMoreTokens();) {
                final String token = tokenizer.nextToken().trim();
                if (token.length() > 0) this.allowedRequestMimeTypesLowerCased.add(token);
            }
            if (this.debug) logLocal("Allowed request mime types: "+this.allowedRequestMimeTypesLowerCased);
        }
        
        // Load config: present multipart form params - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PRESENT_MULTIPART_FORM_PARAMS_AS_REGULAR_PARAMS_TO_APPLICATION);
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.presentMultipartFormParametersAsRegularParametersToApplication = flag;
            if (this.debug) logLocal("Present multipart form params: "+flag);
        }
        
        // Load config: PDF XSS protection - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_PDF_XSS_PROTECTION);
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.pdfXssProtection = flag;
            if (this.debug) logLocal("PDF XSS protection: "+flag);
        }
        
        // Load config: honeylink max per page - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_HONEYLINK_MAX_PER_RESPONSE);
            if (value == null) value = "0";
            try {
                this.honeylinkMaxPerPage = Short.parseShort(value.trim());
                if (this.honeylinkMaxPerPage < 0) throw new UnavailableException("Configured 'honeylink max per response' must not be negative: "+value);
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse (short) configured 'honeylink max per response': "+value);
            }
        }
        
        // Load config: honeylink prefix - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_HONEYLINK_PREFIX);
            if (value == null) value = "";
            this.honeylinkPrefix = value.trim();
            if (this.debug) logLocal("Honeylink prefix: "+value);
        }
        
        // Load config: honeylink suffix - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_HONEYLINK_SUFFIX);
            if (value == null) value = "";
            this.honeylinkSuffix = value.trim();
            if (this.debug) logLocal("Honeylink suffix: "+value);
        }
        
        // Load config: randomize honeylinks on every response - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_RANDOMIZE_HONEYLINKS_ON_EVERY_RESPONSE);
            if (value == null) value = ""+false;
            final boolean flag = (""+true).equals( value.trim().toLowerCase() );
            this.randomizeHoneylinksOnEveryRequest = flag;
            if (this.debug) logLocal("Randomize honeylinks on every response: "+flag);
        }
        
        // Load config: forced session invalidation period - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_FORCED_SESSION_INVALIDATION_PERIOD_MINUTES);
            if (value == null) value = "900";
            try {
                this.forcedSessionInvalidationPeriodMinutes = Integer.parseInt(value.trim());
                if (this.forcedSessionInvalidationPeriodMinutes < 0) throw new UnavailableException("Configured HTTP status code to send as reply to attacks must not be negative: "+value);
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured 'forced session invalidation period': "+value);
            }
        }
        
        // Load config: tie session to client IP address flag - OPTIONAL
        {
            String tieSessionToClientAddressValue = configManager.getConfigurationValue(PARAM_TIE_WEB_SESSION_TO_CLIENT_ADDRESS);
            if (tieSessionToClientAddressValue == null) tieSessionToClientAddressValue = "false";
            this.tieSessionToClientAddress = (""+true).equals( tieSessionToClientAddressValue.trim().toLowerCase() );
            if (this.debug) logLocal("Tie web session to client IP: "+this.tieSessionToClientAddress);
        }
        
        // Load config: tie session to client HTTP headers - OPTIONAL
        {
            String tieSessionToHeaderListValue = configManager.getConfigurationValue(PARAM_TIE_WEB_SESSION_TO_HEADER_LIST);
            if (tieSessionToHeaderListValue == null) tieSessionToHeaderListValue = "User-Agent"; // "User-Agent,Accept-Encoding"     // "Accept" header not used as default, since this can be empty for example when IE requests favicon.ico) "Accept-Encoding" also not used.... if you wish you can present multiple header names comma-delimitd here
            // upper-cased as the header-names are all treated case-insensitive here
            tieSessionToHeaderListValue = tieSessionToHeaderListValue.trim().toUpperCase();
            if (tieSessionToHeaderListValue.length() > 0) {
                this.tieSessionToHeaderList = tieSessionToHeaderListValue.split(",");
                // trim each token
                for (int i=0; i<this.tieSessionToHeaderList.length; i++) {
                    this.tieSessionToHeaderList[i] = this.tieSessionToHeaderList[i].trim();
                }
                // now simply de-dupe the array
                final Set/*<String>*/ set = new HashSet( Arrays.asList(this.tieSessionToHeaderList) );
                this.tieSessionToHeaderList = (String[]) set.toArray( new String[0] );
                if (this.debug) logLocal("Tie web session to header list: "+set);
            } else this.tieSessionToHeaderList = new String[0];
        }
        
        // Load config: force entrance through entry-points flag - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_FORCE_ENTRANCE_THROUGH_ENTRY_POINTS);
            if (value == null) value = "false";
            this.forceEntranceThroughEntryPoints = (""+true).equals( value.trim().toLowerCase() );
            if (this.debug) logLocal("Force entrance through entry-points: "+this.forceEntranceThroughEntryPoints);
        }
        
        // Load config: block response headers with CRLF flag - OPTIONAL
        {
            String blockResponseHeadersValue = configManager.getConfigurationValue(PARAM_BLOCK_RESPONSE_HEADERS_WITH_CRLF);
            if (blockResponseHeadersValue == null) blockResponseHeadersValue = ""+true;
            this.blockResponseHeadersWithCRLF = (""+true).equals( blockResponseHeadersValue.trim().toLowerCase() );
            if (this.debug) logLocal("Block response headers with CRLF: "+this.blockResponseHeadersWithCRLF);
        }
        
        // Load config: block future last-modified response header - OPTIONAL
        {
            String blockFutureLastModifiedHeadersValue = configManager.getConfigurationValue(PARAM_BLOCK_FUTURE_LAST_MODIFIED_HEADERS);
            if (blockFutureLastModifiedHeadersValue == null) blockFutureLastModifiedHeadersValue = ""+false;
            this.blockFutureLastModifiedHeaders = (""+true).equals( blockFutureLastModifiedHeadersValue.trim().toLowerCase() );
            if (this.debug) logLocal("Block future 'Last-Modified' response headers: "+this.blockFutureLastModifiedHeaders);
        }
        
        // Load config: block invalid last-modified response header - OPTIONAL
        {
            String blockInvalidLastModifiedHeadersValue = configManager.getConfigurationValue(PARAM_BLOCK_INVALID_LAST_MODIFIED_HEADERS);
            if (blockInvalidLastModifiedHeadersValue == null) blockInvalidLastModifiedHeadersValue = ""+false;
            this.blockInvalidLastModifiedHeaders = (""+true).equals( blockInvalidLastModifiedHeadersValue.trim().toLowerCase() );
            if (this.debug) logLocal("Block invalid 'Last-Modified' response headers: "+this.blockInvalidLastModifiedHeaders);
        }
        
        // Load config: block requests with unknown referrer - OPTIONAL
        {
            String blockRequestsWithUnknownReferrerValue = configManager.getConfigurationValue(PARAM_BLOCK_REQUESTS_WITH_UNKNOWN_REFERRER);
            if (blockRequestsWithUnknownReferrerValue == null) blockRequestsWithUnknownReferrerValue = ""+false;
            this.blockRequestsWithUnknownReferrer = (""+true).equals( blockRequestsWithUnknownReferrerValue.trim().toLowerCase() );
            if (this.debug) logLocal("Block requests with unknown referrer: "+this.blockRequestsWithUnknownReferrer);
        }
        // Load config: block requests with missing referrer - OPTIONAL
        {
            String blockRequestsWithMissingReferrerValue = configManager.getConfigurationValue(PARAM_BLOCK_REQUESTS_WITH_MISSING_REFERRER);
            if (blockRequestsWithMissingReferrerValue == null) blockRequestsWithMissingReferrerValue = ""+false;
            this.blockRequestsWithMissingReferrer = (""+true).equals( blockRequestsWithMissingReferrerValue.trim().toLowerCase() );
            if (this.debug) logLocal("Block requests with missing referrer: "+this.blockRequestsWithMissingReferrer);
        }
        
        // Load config: block requests with duplicate headers - OPTIONAL
        {
            String blockRequestsWithDuplicateHeadersValue = configManager.getConfigurationValue(PARAM_BLOCK_REQUESTS_WITH_DUPLICATE_HEADERS);
            if (blockRequestsWithDuplicateHeadersValue == null) blockRequestsWithDuplicateHeadersValue = ""+false;
            this.blockRequestsWithDuplicateHeaders = (""+true).equals( blockRequestsWithDuplicateHeadersValue.trim().toLowerCase() );
            if (this.debug) logLocal("Block requests with duplicate headers: "+this.blockRequestsWithDuplicateHeaders);
        }
        
        // Load config: block non-local redirects - OPTIONAL
        {
            String blockNonLocalRedirectsValue = configManager.getConfigurationValue(PARAM_BLOCK_NON_LOCAL_REDIRECTS);
            if (blockNonLocalRedirectsValue == null) blockNonLocalRedirectsValue = ""+false;
            this.blockNonLocalRedirects = (""+true).equals( blockNonLocalRedirectsValue.trim().toLowerCase() );
            if (this.debug) logLocal("Block non-local redirects: "+this.blockNonLocalRedirects);
        }
        
        // Load config: mask ampersands in link additions - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_MASK_AMPERSANDS_IN_LINK_ADDITIONS);
            if (value == null) value = ""+true;
            this.maskAmpersandsInLinkAdditions = (""+true).equals( value.trim().toLowerCase() );
            if (this.debug) logLocal("Mask ampersands in link additions: "+this.maskAmpersandsInLinkAdditions);
        }
        
        // Load config: anti-cache response header injection content types - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_ANTI_CACHE_RESPONSE_HEADER_INJECTION_CONTENT_TYPES);
            if (value == null) value = "text/html,null"; // the word "null" means "match with unset content types"; the most usual case  is though "text/html" only
            // upper-cased as the content-type-names are all treated case-insensitive here
            value = value.trim().toUpperCase();
            if (value.length() > 0) {
                final String[] valuesSplitted = value.split(",");
                for (int i=0; i<valuesSplitted.length; i++) {
                    // trim each token
                    valuesSplitted[i] = valuesSplitted[i].trim();
                }
                // now simply de-dupe the array - and store as set
                this.antiCacheResponseHeaderInjectionContentTypes = new HashSet( Arrays.asList(valuesSplitted) );
                if (this.debug) logLocal("Anti cache response header injection content types: "+this.antiCacheResponseHeaderInjectionContentTypes);
            } else this.antiCacheResponseHeaderInjectionContentTypes = new HashSet();
        }
        
        // Load config: response protection content types - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_RESPONSE_MODIFICATION_CONTENT_TYPES);
            if (value == null) value = "text/html,null"; // the word "null" means "match with unset content types"; the most usual case  is though "text/html" only
            // upper-cased as the content-type-names are all treated case-insensitive here
            value = value.trim().toUpperCase();
            if (value.length() > 0) {
                final String[] valuesSplitted = value.split(",");
                for (int i=0; i<valuesSplitted.length; i++) {
                    // trim each token
                    valuesSplitted[i] = valuesSplitted[i].trim();
                }
                // now simply de-dupe the array - and store as set
                this.responseBodyModificationContentTypes = new HashSet( Arrays.asList(valuesSplitted) );
                if (this.debug) logLocal("Response body modification content types: "+this.responseBodyModificationContentTypes);
            } else this.responseBodyModificationContentTypes = new HashSet();
        }
        
        // Load config: 400or404 attack threshold - OPTIONAL
        {
            // general cluster awareness for this feature
            String value = configManager.getConfigurationValue(PARAM_400_OR_404_ATTACK_THRESHOLD__CLUSTER_AWARE);
            if (value == null) value = ""+false;
            final boolean clusterAware = (""+true).equals( value.trim().toLowerCase() );
            if (clusterAware) initJMS = true;
            // threshold
            String httpInvalidRequestOrNotFoundAttackThresholdValue = configManager.getConfigurationValue(PARAM_400_OR_404_ATTACK_THRESHOLD);
            if (httpInvalidRequestOrNotFoundAttackThresholdValue == null) httpInvalidRequestOrNotFoundAttackThresholdValue = "150";
            try {
                final int httpInvalidRequestOrNotFoundAttackThreshold = Integer.parseInt(httpInvalidRequestOrNotFoundAttackThresholdValue.trim());
                if (httpInvalidRequestOrNotFoundAttackThreshold < 0) throw new UnavailableException("Configured HTTP 400/404 attack threshold must not be negative: "+httpInvalidRequestOrNotFoundAttackThresholdValue);
                this.httpStatusCodeCounter = new HttpStatusCodeTracker(this.attackHandler, httpInvalidRequestOrNotFoundAttackThreshold, this.housekeepingIntervalMinutes*60*1000L, this.resetPeriodMinutesBadResponseCode*60*1000L, 
                        clusterAware?this.clusterBroadcastPeriod*1000:0, clusterInitialContextFactory, clusterJmsProviderUrl, clusterJmsConnectionFactory, clusterJmsTopic); // TODO: ueberall hier statt *60*1000 besser ein *60L*1000L nehmen !
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured HTTP 400/404 attack threshold: "+httpInvalidRequestOrNotFoundAttackThresholdValue);
            }
        }
        
        // Load config: session creation attack threshold - OPTIONAL
        {
            // general cluster awareness for this feature
            String value = configManager.getConfigurationValue(PARAM_SESSION_CREATION_ATTACK_THRESHOLD__CLUSTER_AWARE);
            if (value == null) value = ""+false;
            final boolean clusterAware = (""+true).equals( value.trim().toLowerCase() );
            if (clusterAware) initJMS = true;
            // threshold
            String valueThreshold = configManager.getConfigurationValue(PARAM_SESSION_CREATION_ATTACK_THRESHOLD);
            if (valueThreshold == null) valueThreshold = "0";
            try {
                final int parsedValue = Integer.parseInt(valueThreshold.trim());
                if (parsedValue < 0) throw new UnavailableException("Configured session creation attack threshold must not be negative: "+valueThreshold);
                this.sessionCreationCounter = new SessionCreationTracker(this.attackHandler, parsedValue, this.housekeepingIntervalMinutes*60*1000L, this.resetPeriodMinutesSessionCreation*60*1000L, 
                        clusterAware?this.clusterBroadcastPeriod*1000:0, clusterInitialContextFactory, clusterJmsProviderUrl, clusterJmsConnectionFactory, clusterJmsTopic); // TODO: ueberall hier statt *60*1000 besser ein *60L*1000L nehmen !
            } catch(NumberFormatException e) {
                throw new UnavailableException("Unable to number-parse configured session creation attack threshold: "+valueThreshold);
            }
        }
        
        // Load config: buffer file uploads to disk - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_BUFFER_FILE_UPLOADS_TO_DISK);
            if (value == null) value = "true";
            this.bufferFileUploadsToDisk = (""+true).equals( value.trim().toLowerCase() ); 
            if (this.debug) logLocal("Buffer file uploads to disk: "+this.bufferFileUploadsToDisk);
        }

        // Load config: apply set after session write - OPTIONAL (for session clusters where the session will be replicated to also replicate changes via the container's replication mechnism)
        {
            String value = configManager.getConfigurationValue(PARAM_APPLY_SET_AFTER_SESSION_WRITE);
            if (value == null) value = "false";
            this.applySetAfterWrite = (""+true).equals( value.trim().toLowerCase() );
            if (this.debug) logLocal("Apply set after session write: "+this.applySetAfterWrite);
        }

        // Load config: validate client address format - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_VALIDATE_CLIENT_ADDRESS_FORMAT);
            if (value == null) value = "false";
            this.validateClientAddressFormat = (""+true).equals( value.trim().toLowerCase() );
            if (this.debug) logLocal("Validate client address format: "+this.validateClientAddressFormat);
        }
        
        // Load config: transparent querystring - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_TRANSPARENT_QUERYSTRING);
            if (value == null) value = configManager.getConfigurationValue(LEGACY_PARAM_TRANSPARENT_QUERYSTRING); // only for backwards-compatibility to old param name (upper/lowercase S)
            if (value == null) value = "true";
            this.transparentQuerystring = (""+true).equals( value.trim().toLowerCase() );
            if (this.debug) logLocal("Transparent querystring: "+this.transparentQuerystring);
        }

        // Load config: transparent forwarding - OPTIONAL
        {
            String value = configManager.getConfigurationValue(PARAM_TRANSPARENT_FORWARDING);
            if (value == null) value = "true";
            this.transparentForwarding = (""+true).equals( value.trim().toLowerCase() );
            if (this.debug) logLocal("Transparent forwarding: "+this.transparentForwarding);
        }

        
        // INIT JMS IF REQUIRED TO DO SO
        if (this.jmsUsed) {
            JmsUtils.closeQuietly(false); // to be fresh
        }
        if (initJMS) try {
            JmsUtils.init(this.clusterInitialContextFactory, this.clusterJmsProviderUrl, this.clusterJmsConnectionFactory, this.clusterJmsTopic);
            this.jmsUsed = true;
        } catch (Exception e) {
            JmsUtils.closeQuietly(false); // to be re-initialized on the next call
            logLocal("Unable to initialize JMS: "+e);
        }

    }
    
    
    
    
    
    private void sendProcessingError(final Throwable t, final HttpServletResponse response) {
        logLocal("Unable to process filter chain and unable to handle exception: "+t);
        t.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    // TODO: sendUnavailableMessage und sendUncaughtExceptionResponse in eine parametrisierbare Methode mergen
    private void sendUnavailableMessage(final HttpServletResponse response, final Exception exception) throws IOException {
        // ID
        // this is an uncaught exception so use a generic timestamp
        final String logReferenceId = "time "+System.currentTimeMillis();
        // details
        final StringBuilder exceptionDetails = new StringBuilder();
        final String exceptionMessage = exception == null ? "No exception details available" : exception.getMessage();
        // message and status-code settings
        final String exceptionReplyMessage; final int exceptionReplyStatusCode;
        // if we're in production mode leave "exceptionDetails" as an empty string
        if (this.isProductionMode) {
            exceptionReplyMessage = this.productionConfigurationMissingReplyMessage;
            exceptionReplyStatusCode = this.productionConfigurationMissingReplyStatusCode;
        } else {
            exceptionReplyMessage = this.developmentConfigurationMissingReplyMessage;
            exceptionReplyStatusCode = this.developmentConfigurationMissingReplyStatusCode;
            if (exceptionMessage != null) exceptionDetails.append(exceptionMessage).append("\n");
            // extract exception details
            if (exception != null) {
                CharArrayWriter sink = null;
                PrintWriter printWriter = null;
                Throwable currentExceptionInChain = exception;
                try {
                    sink = new CharArrayWriter();
                    printWriter = new PrintWriter(sink);
                    currentExceptionInChain.printStackTrace(printWriter);
                    do {
                        currentExceptionInChain = currentExceptionInChain.getCause();
                        if (currentExceptionInChain != null) {
                            printWriter.println("\nCaused by: "+currentExceptionInChain.getMessage());
                            currentExceptionInChain.printStackTrace(printWriter);
                        }
                    } while (currentExceptionInChain != null);
                } finally {
                    if (printWriter != null) printWriter.close();
                }
                exceptionDetails.append(sink.toString());
            }
        }
        if (this.attackHandler != null) this.attackHandler.logWarningRequestMessage("Unable to initialize protection layer:\n\t"+exceptionDetails.toString().replaceAll("\n","\n\t"));
        try {
            if (!response.isCommitted()) {
                response.reset();
                if (exceptionReplyMessage != null && exceptionReplyMessage.length() > 0) {
                    String message = exceptionReplyMessage;
                    message = message.replaceAll("\\$\\{id\\}", ServerUtils.quoteReplacement(ServerUtils.escapeSpecialCharactersHTML(logReferenceId)));
                    message = message.replaceAll("\\$\\{message\\}", ServerUtils.quoteReplacement(ServerUtils.escapeSpecialCharactersHTML(exceptionMessage.toString())));
                    message = message.replaceAll("\\$\\{details\\}", ServerUtils.quoteReplacement(ServerUtils.escapeSpecialCharactersHTML(exceptionDetails.toString())));
                    response.setContentType("text/html");
                    response.getWriter().write(message);
                    response.flushBuffer();
                } else response.sendError(exceptionReplyStatusCode);
            }
        } catch (Exception e) {
            logLocal("Unable to send 'unavailable message' in response", e);
            response.sendError(exceptionReplyStatusCode);
        }
    }
    private void sendUncaughtExceptionResponse(final HttpServletResponse response, final Exception exception) throws IOException {
        // ID
        // this is an uncaught exception so use a generic timestamp
        final String logReferenceId = "time "+System.currentTimeMillis();
        // details
        final StringBuilder exceptionDetails = new StringBuilder();
        final String exceptionMessage = exception == null ? "No exception details available" : exception.getMessage();
        // message and status-code settings
        final String exceptionReplyMessage; final int exceptionReplyStatusCode;
        // if we're in production mode leave "exceptionDetails" as an empty string
        if (this.isProductionMode) {
            exceptionReplyMessage = this.productionExceptionReplyMessage;
            exceptionReplyStatusCode = this.productionExceptionReplyStatusCode;
        } else {
            exceptionReplyMessage = this.developmentExceptionReplyMessage;
            exceptionReplyStatusCode = this.developmentExceptionReplyStatusCode;
            if (exceptionMessage != null) exceptionDetails.append(exceptionMessage).append("\n");
            // extract exception details
            if (exception != null) {
                CharArrayWriter sink = null;
                PrintWriter printWriter = null;
                Throwable currentExceptionInChain = exception;
                try {
                    sink = new CharArrayWriter();
                    printWriter = new PrintWriter(sink);
                    currentExceptionInChain.printStackTrace(printWriter);
                    do {
                        currentExceptionInChain = currentExceptionInChain.getCause();
                        if (currentExceptionInChain != null) {
                            printWriter.println("\nCaused by: "+currentExceptionInChain.getMessage());
                            currentExceptionInChain.printStackTrace(printWriter);
                        }
                    } while (currentExceptionInChain != null);
                } finally {
                    if (printWriter != null) printWriter.close();
                }
                exceptionDetails.append(sink.toString());
            }
        }
        this.attackHandler.logWarningRequestMessage("Uncaught exception blocked:\n\t"+exceptionDetails.toString().replaceAll("\n","\n\t"));
        try {
            if (!response.isCommitted()) {
                response.reset();
                if (exceptionReplyMessage != null && exceptionReplyMessage.length() > 0) {
                    String message = exceptionReplyMessage;
                    message = message.replaceAll("\\$\\{id\\}", ServerUtils.quoteReplacement(ServerUtils.escapeSpecialCharactersHTML(logReferenceId)));
                    message = message.replaceAll("\\$\\{message\\}", ServerUtils.quoteReplacement(ServerUtils.escapeSpecialCharactersHTML(exceptionMessage)));
                    message = message.replaceAll("\\$\\{details\\}", ServerUtils.quoteReplacement(ServerUtils.escapeSpecialCharactersHTML(exceptionDetails.toString())));
                    response.setContentType("text/html");
                    response.getWriter().write(message);
                    response.flushBuffer();
                } else response.sendError(exceptionReplyStatusCode);
            }
        } catch (Exception e) {
            if (!(e instanceof IllegalStateException)/*to avoid logs when getWriter() after already getOutputStream() was called*/) logLocal("Unable to send 'uncaught exception' in response", e);
            response.sendError(exceptionReplyStatusCode);
        }
    }
    
    
    private void sendDisallowedResponse(final HttpServletResponse response, final Attack attack) throws IOException {
        if (attack == null) throw new NullPointerException("attack must not be null");
        // ID
        String logReferenceId = attack.getLogReferenceId();
        if (logReferenceId !=null) {
            // this is a logged attack
            logReferenceId = "log "+logReferenceId;
        } else {
            // this is an unlogged attack (like a black-listed client or something) so use a generic timestamp instead
            logReferenceId = "time "+System.currentTimeMillis();
        }
        // details
        final String attackDetails;
        // message and status-code settings
        final String attackReplyMessage; final int attackReplyStatusCode;
        // if we're in production mode set "attackDetails" to empty string
        if (this.isProductionMode) {
            attackDetails = "";
            attackReplyMessage = this.productionAttackReplyMessage;
            attackReplyStatusCode = this.productionAttackReplyStatusCode;
        } else {
            attackDetails = attack.getMessage();
            attackReplyMessage = this.developmentAttackReplyMessage;
            attackReplyStatusCode = this.developmentAttackReplyStatusCode;
        }
        // as attacks are already logged, we don't have to put in extra logging here
        try {
            if (!response.isCommitted()) {
                response.reset();
                if (attackReplyMessage != null && attackReplyMessage.length() > 0) {
                    String message = attackReplyMessage;
                    message = message.replaceAll("\\$\\{id\\}", ServerUtils.quoteReplacement(ServerUtils.escapeSpecialCharactersHTML(logReferenceId)));
                    message = message.replaceAll("\\$\\{message\\}", ServerUtils.quoteReplacement(ServerUtils.escapeSpecialCharactersHTML("Protection rule or security setting match")));
                    message = message.replaceAll("\\$\\{details\\}", ServerUtils.quoteReplacement(ServerUtils.escapeSpecialCharactersHTML(attackDetails)));
                    response.setContentType("text/html");
                    response.getWriter().write(message);
                    response.flushBuffer();
                } else response.sendError(attackReplyStatusCode);
            }
        } catch (Exception e) {
            if (!(e instanceof IllegalStateException)/*to avoid logs when getWriter() after already getOutputStream() was called*/) logLocal("Unable to send 'disallowed message' in response", e);
            response.sendError(attackReplyStatusCode);
        }
    }
    
    private void sendErrorMessageResponse(final HttpServletResponse response, final String title, final String message) throws IOException {
        if (message != null) this.attackHandler.logWarningRequestMessage("Sening error message response:\n\t"+message.replaceAll("\n","\n\t"));
        try {
            if (!response.isCommitted()) {
                response.reset();
                if (message != null && message.trim().length() > 0) {
                    response.setContentType("text/html");
                    final PrintWriter out = response.getWriter();
                    out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
                    out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>");
                    out.write("<meta content=\"text/html; charset=ISO-8859-1\" http-equiv=\"Content-Type\" /><meta content=\"0\" http-equiv=\"Expires\" />");
                    out.write("<meta content=\"no-cache\" http-equiv=\"Pragma\" /><meta content=\"no-cache, no-store, must-revalidate\" http-equiv=\"Cache-control\" />");
                    out.write("<title>"+ ServerUtils.escapeSpecialCharactersHTML(title) +"</title></head>");
                    out.write("<body bgproperties=\"FIXED\" style=\"font-family: Arial, Helvetica, sans-serif, Verdana, Geneva;\">");
                    out.write("<span style=\"background-color: #ffffbb; border: 1px dotted #999999; padding: 2px 4px 2px 12px; width: 90%; margin-top: 7px; margin-left: 5px; display:block; color: black;\">");
                    out.write("<table style=\"border: 1px; border-width: 0px; cell-spacing: 0px; cell-padding: 0px; margin: 0px 0px 0px 0px;\">");
                    out.write("<tr><td style=\"horizontal-align: left; vertical-align: middle; padding-bottom: 8px;\">");
                    out.write("<br/><h2 style=\"margin:0; padding:0; font-size: 0.9em;\">"+ ServerUtils.escapeSpecialCharactersHTML(title) +"</h2></td></tr>");
                    out.write("<tr><td style=\"horizontal-align: left; font-size: 0.8em;\"><ul><li>");
                    out.write( ServerUtils.escapeSpecialCharactersHTML(message) );
                    out.write("<p/></li></ul></td></tr></table></span></body></html>");
                    response.flushBuffer();
                } else response.sendError(500, message);
            }
        } catch (Exception e) {
            if (!(e instanceof IllegalStateException)/*to avoid logs when getWriter() after already getOutputStream() was called*/) logLocal("Unable to send 'error message' in response", e);
            response.sendError(500, message);
        }
    }
    
    
    private void logLocal(final String msg) {
        logLocal(msg, null);
    }
    private void logLocal(final String msg, final Exception e) {
        if (e != null) {
            if (USE_WEB_SERVER_LOG && filterConfig != null && filterConfig.getServletContext() != null) filterConfig.getServletContext().log(msg, e);
            else {
                /*
                final LogRecord record = new LogRecord(Level.WARNING, msg);
                record.setThrown(e);
                record.setSourceClassName("WebCastellumFilter");
                record.setSourceMethodName("message");
                logger.log(record);
                */
                System.out.println(msg+": "+e);
            }
        } else {
            if (USE_WEB_SERVER_LOG && filterConfig != null && filterConfig.getServletContext() != null) filterConfig.getServletContext().log(msg);
            else {
                /*
                final LogRecord record = new LogRecord(Level.INFO, msg);
                record.setSourceClassName("WebCastellumFilter");
                record.setSourceMethodName("message");
                logger.log(record);
                */
                System.out.println(msg);
            }
        }
    }
    
    
    
    /* OLD
    // return true when everything is fine (i.e. all actual submitted values are allowed) - used in selectbox-protection
    private boolean isMatching(final Collection/*<String>* / actualSubmittedValues, final Collection/*<String>* / allowedOptionValues) {
        // TODO: gibts hierfuer nicht ne Methode in Collections ? oder in der Collection selbst? soas wie .containsAll() ?
        for (final Iterator submittedValues = actualSubmittedValues.iterator(); submittedValues.hasNext();) {
            final String submittedValue = (String) submittedValues.next();
            if (!allowedOptionValues.contains(submittedValue)) return false; // = attack detected
        }
        return true; // = everything is fine
    }*/
    
    
    
    
    
    private void removeTemporarilyInjectedParametersFromRequest(final RequestWrapper request, final String cryptoDetectionString) {
        if (request == null) return;
        // remove the encrpyted param
        if (cryptoDetectionString != null && cryptoDetectionString.trim().length() > 0) request.removeEncryptedQueryString(cryptoDetectionString);
        // remove all CAPTCHA related stuff (only form)
        request.removeParameter(CAPTCHA_FORM);
        // remove the rest
        final HttpSession session = request.getSession(false);
        if (session == null) return;
        try {
            // remove the protective parameters from the request (the names of those protective parameters are stored in the session, so fetch them from the session)
            request.removeParameter((String) ServerUtils.getAttributeIncludingInternal(session,SESSION_SECRET_RANDOM_TOKEN_KEY_KEY));
            request.removeParameter((String) ServerUtils.getAttributeIncludingInternal(session,SESSION_PARAMETER_AND_FORM_PROTECTION_RANDOM_TOKEN_KEY_KEY));
        } catch (IllegalStateException irgnored) {} // = session already invalidated
    }
    private void removeTemporarilyInjectedParametersFromMap(final Map/*<String,String[]>*/ parameterMap, final HttpSession session, final String cryptoDetectionString) {
        if (parameterMap == null) return;
        // remove the encrpyted param
        if (cryptoDetectionString != null && cryptoDetectionString.trim().length() > 0) removeKeysContainingCryptoDetectionString(parameterMap, cryptoDetectionString);
        // remove all CAPTCHA related stuff (only form)
        removeKey(parameterMap, CAPTCHA_FORM);
        // remove the rest
        if (session == null) return;
        try {
            // remove the protective parameters from the request (the names of those protective parameters are stored in the session, so fetch them from the session)
            removeKey(parameterMap, (String) ServerUtils.getAttributeIncludingInternal(session,SESSION_SECRET_RANDOM_TOKEN_KEY_KEY));
            removeKey(parameterMap, (String) ServerUtils.getAttributeIncludingInternal(session,SESSION_PARAMETER_AND_FORM_PROTECTION_RANDOM_TOKEN_KEY_KEY));
        } catch (IllegalStateException irgnored) {} // = session already invalidated
    }
    private void removeKey(final Map/*<String,String[]>*/ parameterMap, final String key) {
        if (key == null || key.trim().length() == 0) return;
        parameterMap.remove(key);
    }
    private void removeKeysContainingCryptoDetectionString(final Map/*<String,String[]>*/ parameterMap, final String cryptoDetectionString) {
        if (cryptoDetectionString == null || cryptoDetectionString.trim().length() == 0) return;
        for (final Iterator keys = parameterMap.keySet().iterator(); keys.hasNext();) {
            final String key = (String) keys.next();
            if (key.indexOf(cryptoDetectionString) != -1) keys.remove();
        }
    }
    
    
    
    
    
    
    
    
    
    //1.5@Override
    public String toString() {
        return Version.tagLine();
    }
    


    
    
    
    
    /*
    private final class ReloadRulesTask extends TimerTask {
        public void run() {
            WebCastellumFilter.this.registerRuleReloadOnNextRequest();
        }
    }*/
    
    
    
    
    
    private static final class AllowedFlagWithMessage {
        private final boolean allowed;
        private Attack attack;
        private Captcha captcha;
        public AllowedFlagWithMessage(final boolean allowed) {
            this.allowed = allowed;
        }
        public AllowedFlagWithMessage(final boolean allowed, final Attack attack) {
            this(allowed);
            this.attack = attack;
        }
        public AllowedFlagWithMessage(final boolean allowed, final Captcha captcha) {
            this(allowed);
            this.captcha = captcha;
        }
        public boolean isAllowed() {
            return this.allowed;
        }
        public Attack getAttack() {
            return this.attack;
        }
        public Captcha getCaptcha() {
            return this.captcha;
        }
    }


/*
    private static final class SuccessOrFailure {
    private final boolean success;
        private Exception failure;
        public SuccessOrFailure(final boolean success) {
            this.success = success;
        }
        public SuccessOrFailure(final boolean success, final Exception failure) {
            this(success);
            this.failure = failure;
        }
        public boolean isSucess() {
            return this.success;
        }
        public Exception getFailure() {
            return this.failure;
        }
    }
*/
    static final boolean REMOVE_CONTENT_LENGTH_FOR_MODIFIABLE_RESPONSES = true; // hat was mit Kompatiblitaet von bestimmten Browser-Versionen zu tun...
    static final boolean REMOVE_COMPRESSION_ACCEPT_ENCODING_HEADER_VALUES = false; // um zu verhindern, dass die App selber (z.B. wenn WC ausserhalb der App im Perimeter-Modus per forwarder laeuft) eine Komprimierung der responses macht (z.B. gzip) obwohl WC als Perimeter diese ja noch parsen will)... macht also vor allem bei Perimeter-WC Sinn... dann am besten zusaetzlich noch in WC einbauen, dass WC am Ende als allerletzten schrit bei client-seitiger Akzeptanz die Komprimierung durchfuehrt...
    static final boolean APPEND_EQUALS_SIGN_TO_VALUELESS_URL_PARAM_NAMES = true; // required for WebSphere



    // FIXME: per web.xml eintellbar machen
    public static final Pattern PATTERN_VALID_CLIENT_ADDRESS = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}|((([0-9A-Fa-f]{1,4}:){7}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){6}:[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){5}:([0-9A-Fa-f]{1,4}:)?[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){4}:([0-9A-Fa-f]{1,4}:){0,2}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){3}:([0-9A-Fa-f]{1,4}:){0,3}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){2}:([0-9A-Fa-f]{1,4}:){0,4}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){6}((\\b((25[0-5])|(1\\d{2})|(2[0-4]\\d)|(\\d{1,2}))\\b)\\.){3}(\\b((25[0-5])|(1\\d{2})|(2[0-4]\\d)|(\\d{1,2}))\\b))|(([0-9A-Fa-f]{1,4}:){0,5}:((\\b((25[0-5])|(1\\d{2})|(2[0-4]\\d)|(\\d{1,2}))\\b)\\.){3}(\\b((25[0-5])|(1\\d{2})|(2[0-4]\\d)|(\\d{1,2}))\\b))|(::([0-9A-Fa-f]{1,4}:){0,5}((\\b((25[0-5])|(1\\d{2})|(2[0-4]\\d)|(\\d{1,2}))\\b)\\.){3}(\\b((25[0-5])|(1\\d{2})|(2[0-4]\\d)|(\\d{1,2}))\\b))|([0-9A-Fa-f]{1,4}::([0-9A-Fa-f]{1,4}:){0,5}[0-9A-Fa-f]{1,4})|(::([0-9A-Fa-f]{1,4}:){0,6}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){1,7}:))(%[0-9A-Fa-f]{1,4})?");



    // FIXME: per web.xml setting konfigurierbar machen
    static final File TEMP_DIRECTORY = null;



    // evtl. in einer spaeteren Version per web.xml steuern lassen
    static final int TRIE_MATCHING_THRSHOLD = 60;






    // TODO: Dieses WebServerLog-Flag hier auch per web.xml-basierten WebCastellumFilter-Parameter steuerbar machen !??
    private static final boolean USE_WEB_SERVER_LOG = true;

    /**
     * Should only be enabled for internal debug purposes
     */
    private static final boolean INTERNALLY_DUMP_REQUEST_PARAM_NAMES_VERBOSE = false;
    private static final boolean DEBUG_PRINT_UNCOVERING_DETAILS = false;


    static int customerIdentifier;




    
}



