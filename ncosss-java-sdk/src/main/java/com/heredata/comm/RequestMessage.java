package com.heredata.comm;

import com.heredata.model.WebServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TODO
 * @author wuzz
 * @since 2022/10/19
 */
@Data
@AllArgsConstructor
public class RequestMessage extends HttpMesssage {

    /* Indicate whether using url signature */
    private boolean useUrlSignature = false;

    /* Indicate whether using chunked encoding */
    private boolean useChunkEncoding = false;

    /* The original request provided by user */
    private final WebServiceRequest originalRequest;

    public RequestMessage(WebServiceRequest originalRequest) {
        this.originalRequest = (originalRequest == null) ? WebServiceRequest.NOOP : originalRequest;
    }

    /* The HTTP method to use when sending this request */
    private HttpMethod method = HttpMethod.GET;


    /* The absolute url to which the request should be sent */
    private URL absoluteUrl;

    /* The service endpoint to which this request should be sent */
    private URI endpoint;

    /* The resource path being requested */
    private String resourcePath;

    /* Use a LinkedHashMap to preserve the insertion order. */
    private Map<String, String> parameters = new LinkedHashMap<>();

    /**
     * Indicate whether the request should be repeatedly sent.
     */
    public boolean isRepeatable() {
        return this.getContent() == null || this.getContent().markSupported();
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters.clear();
        if (parameters != null && !parameters.isEmpty()) {
            this.parameters.putAll(parameters);
        }
    }

    public void addParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    public void removeParameter(String key) {
        this.parameters.remove(key);
    }

}
