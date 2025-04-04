package com.hachther.mesomb;

public class MeSomb {
    /** The MeSomb API key to be used for requests. */
    public static String apiKey;

    /** The MeSomb client key to be used for Connect requests. */
    public static String clientKey;

    /** he MeSomb secret key to be used for Connect requests. */
    public static String secretKey;

    /** The base URL for the MeSomb API. */
    public static String apiBase = "https://mesomb.hachther.com";

    /** The version of the MeSomb API to use for requests. */
    public static String apiVersion = "v1.1";
    public static String version = "2.0.2";

    /** The language to use for requests. */
    public static String language = "en";

    /** The application's information (name, version, URL) */
    public static String appInfo;

    /** Algorithm to used for signature */
    public static String algorithm = "HMAC-SHA1";

    /** Maximum number of request retries */
    public static int maxNetworkRetries = 0;

    /** Initial delay between retries, in seconds */
    public static int requestTimeout = 30;

    /** Whether client telemetry is enabled. Defaults to true. */
    public static boolean enableTelemetry = true;

    /** Maximum delay between retries, in seconds */
    private static double maxNetworkRetryDelay = 2.0;

    /** Maximum delay between retries, in seconds, that will be respected from the MeSomb API */
    private static double maxRetryAfter = 60.0;

    /** Initial delay between retries, in seconds */
    private static double initialNetworkRetryDelay = 0.5;
}
