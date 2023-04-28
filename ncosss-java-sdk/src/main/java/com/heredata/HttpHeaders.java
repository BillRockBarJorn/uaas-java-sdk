package com.heredata;

/**
 * <p>Title: HttpHeaders</p>
 * <p>Description: http请求协议中请求头常量接口 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 13:56
 */
public interface HttpHeaders {

    String AUTHORIZATION = "Authorization";
    String CACHE_CONTROL = "Cache-Control";
    String CONTENT_DISPOSITION = "Content-Disposition";
    String CONTENT_ENCODING = "Content-Encoding";
    String CONTENT_LENGTH = "Content-Length";
    String CONTENT_MD5 = "Content-MD5";
    String CONTENT_TYPE = "Content-Type";
    String TRANSFER_ENCODING = "Transfer-Encoding";
    String DATE = "Date";
    String ETAG = "ETag";
    String EXPIRES = "Expires";
    String HOST = "Host";
    String LAST_MODIFIED = "Last-Modified";
    String RANGE = "Range";
    String LOCATION = "Location";
    String CONNECTION = "Connection";
}
