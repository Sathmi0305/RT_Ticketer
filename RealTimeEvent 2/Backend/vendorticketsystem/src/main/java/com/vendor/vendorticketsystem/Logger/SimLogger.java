package com.vendor.vendorticketsystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimLogger.class);

    public static void info(String message) {
        LOGGER.info("[INFO] " + message);
    }

    public static void warn(String message) {
        LOGGER.warn("[WARN] " + message);
    }

    public static void error(String message) {
        LOGGER.error("[ERROR] " + message);
    }

    public static void debug(String message) {
        LOGGER.debug("[DEBUG] " + message);
    }

    public static void logThreadStatus(String threadName, String message) {
        LOGGER.info("[THREAD: " + threadName + "] " + message);
    }
}
