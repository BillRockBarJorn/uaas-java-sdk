package com.heredata.uaas.core.transport.functions;

import com.google.common.base.Function;
import com.heredata.uaas.core.transport.HttpResponse;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.openstack.internal.Parser;

import java.util.Map;

/**
 * Takes an HttpResponse as input and returns an ActionResponse as an output
 *
 * @author Jeremy Unruh
 */
public class ResponseToActionResponse implements Function<HttpResponse, ActionResponse> {

    public static final ResponseToActionResponse INSTANCE = new ResponseToActionResponse();

    @Override
    public ActionResponse apply(HttpResponse response) {
        return apply(response, false);
    }

    public ActionResponse apply(HttpResponse response, boolean returnNullIfNotMapped) {
        if (Parser.isContentTypeText(response.getContentType())) {
            return ActionResponse.actionFailed(response.getStatusMessage(), response.getStatus());
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> map = response.readEntity(Map.class);
        ActionResponse ar = new ParseActionResponseFromJsonMap(response).apply(map);
        if (ar != null) {
            return ar;
        }
        if (ar == null && returnNullIfNotMapped) {
            return null;
        }
        return ActionResponse.actionFailed(String.format("Status: %d, Reason: %s", response.getStatus(), response.getStatusMessage()), response.getStatus());
    }
}
