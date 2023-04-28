package com.heredata.hos.utils;

import com.heredata.hos.HOSErrorCode;
import com.heredata.hos.comm.HOSConstants;
import com.heredata.utils.LogUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: LogUtils</p>
 * <p>Description: jar包日志工具类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 14:52
 */
public class HOSLogUtils extends LogUtils {

    private static final Log log = LogFactory.getLog(HOSConstants.LOGGER_PACKAGE_NAME);
    // Set logger level to INFO specially if reponse error code is 404 in order
    // to
    // prevent from dumping a flood of logs when trying access to none-existent
    // resources.
    private static List<String> errorCodeFilterList = new ArrayList<String>();

    static {
        errorCodeFilterList.add(HOSErrorCode.NO_SUCH_BUCKET);
        errorCodeFilterList.add(HOSErrorCode.NO_SUCH_KEY);
        errorCodeFilterList.add(HOSErrorCode.NO_SUCH_UPLOAD);
        errorCodeFilterList.add(HOSErrorCode.NO_SUCH_CORS_CONFIGURATION);
        errorCodeFilterList.add(HOSErrorCode.NO_SUCH_WEBSITE_CONFIGURATION);
        errorCodeFilterList.add(HOSErrorCode.NO_SUCH_LIFECYCLE);
    }
}
