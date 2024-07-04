package com.heredata.ncdfs.utils;

import com.heredata.ncdfs.exception.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

import static com.heredata.ncdfs.internal.NCDFSConstants.LOGGER_PACKAGE_NAME;


public class LogUtils {

    private static final Log log = LogFactory.getLog(LOGGER_PACKAGE_NAME);

    // Set logger level to INFO specially if reponse error code is 404 in order
    // to
    // prevent from dumping a flood of logs when trying access to none-existent
    // resources.
    private static List<String> errorCodeFilterList = new ArrayList<String>();

    public static Log getLog() {
        return log;
    }

    public static <ExType> void logException(String messagePrefix, ExType ex) {
        logException(messagePrefix, ex, true);
    }

    public static <ExType> void logException(String messagePrefix, ExType ex, boolean logEnabled) {

        assert (ex instanceof Exception);

        String detailMessage = messagePrefix + ((Exception) ex).getMessage();
        if (ex instanceof ServiceException && errorCodeFilterList.contains(((ServiceException) ex).getErrorCode())) {
            if (logEnabled) {
                log.debug(detailMessage);
            }
        } else {
            if (logEnabled) {
                log.warn(detailMessage);
            }
        }
    }
}
