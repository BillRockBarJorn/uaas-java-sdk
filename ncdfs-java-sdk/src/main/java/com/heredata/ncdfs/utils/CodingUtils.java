package com.heredata.ncdfs.utils;

import com.heredata.ncdfs.internal.NCDFSUtils;

import java.util.List;

import static com.heredata.ncdfs.internal.NCDFSUtils.COMMON_RESOURCE_MANAGER;


/**
 * Utils for common coding.
 */
public class CodingUtils {

    public static void assertParameterNotNull(Object param, String paramName) {
        if (param == null) {
            throw new NullPointerException(NCDFSUtils.COMMON_RESOURCE_MANAGER.getFormattedString("ParameterIsNull", paramName));
        }
    }

    public static void assertParameterInRange(long param, long lower, long upper) {
        if (!checkParamRange(param, lower, true, upper, true)) {
            throw new IllegalArgumentException(String.format("%d not in valid range [%d, %d]", param, lower, upper));
        }
    }

    public static void assertStringNotNullOrEmpty(String param, String paramName) {
        assertParameterNotNull(param, paramName);
        if (param.trim().length() == 0) {
            throw new IllegalArgumentException(
                    NCDFSUtils.COMMON_RESOURCE_MANAGER.getFormattedString("ParameterStringIsEmpty", paramName));
        }
    }

    public static void assertListNotNullOrEmpty(List<?> param, String paramName) {
        assertParameterNotNull(param, paramName);
        if (param.size() == 0) {
            throw new IllegalArgumentException(
                    NCDFSUtils.COMMON_RESOURCE_MANAGER.getFormattedString("ParameterListIsEmpty", paramName));
        }
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean checkParamRange(long param, long from, boolean leftInclusive, long to,
            boolean rightInclusive) {

        if (leftInclusive && rightInclusive) { // [from, to]
            if (from <= param && param <= to) {
                return true;
            } else {
                return false;
            }
        } else if (leftInclusive && !rightInclusive) { // [from, to)
            if (from <= param && param < to) {
                return true;
            } else {
                return false;
            }
        } else if (!leftInclusive && !rightInclusive) { // (from, to)
            if (from < param && param < to) {
                return true;
            } else {
                return false;
            }
        } else { // (from, to]
            if (from < param && param <= to) {
                return true;
            } else {
                return false;
            }
        }
    }
}
