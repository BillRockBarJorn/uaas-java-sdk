/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.heredata.uaas.openstack.common;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import static com.heredata.uaas.core.transport.ClientConstants.*;

/**
 * A generic result that contains some basic response options, such as
 * requestId.
 */
@JsonRootName("head")
@JsonIgnoreProperties(ignoreUnknown = true)
public class HeadResult {

    @JsonProperty(required = false, value = HEADER_X_SUBJECT_TOKEN)
    private String xSubjectToken;
    @JsonProperty(required = false)
    private String date;
    @JsonProperty(required = false, value = HEADER_CONTENT_TYPE)
    private String contentType;
    @JsonProperty(required = false, value = HEADER_CONTENT_LENGTH)
    private String contentLength;

    public String getxSubjectToken() {
        return xSubjectToken;
    }

    public void setxSubjectToken(String xSubjectToken) {
        this.xSubjectToken = xSubjectToken;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentLength() {
        return contentLength;
    }

    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }
}
