package com.example.wot_servient.http.server.route;

import android.util.Log;

import com.example.wot_servient.wot.Servient;
import com.example.wot_servient.wot.content.Content;
import com.example.wot_servient.wot.content.ContentCodecException;
import com.example.wot_servient.wot.content.ContentManager;
import com.example.wot_servient.wot.thing.ExposedThing;
import com.example.wot_servient.wot.thing.action.ExposedThingAction;

import org.eclipse.jetty.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import spark.Request;
import spark.Response;

/**
 * Endpoint for invoking a {@link com.example.wot_servient.wot.thing.action.ThingAction}.
 */
public class InvokeActionRoute extends AbstractInteractionRoute {
    public InvokeActionRoute(Servient servient, String securityScheme,
                             Map<String, ExposedThing> things) {
        super(servient, securityScheme, things);
    }

    @Override
    protected Object handleInteraction(Request request,
                                       Response response,
                                       String requestContentType,
                                       String name,
                                       ExposedThing thing) {
        ExposedThingAction<Object, Object> action = thing.getAction(name);
        if (action != null) {
            try {
                Content content = new Content(requestContentType, request.bodyAsBytes());
                Object input = ContentManager.contentToValue(content, action.getInput());

                Map<String, Map<String, Object>> options = Map.of(
                        "uriVariables", parseUrlParameters(request.queryMap().toMap(), action.getUriVariables())
                );

                Object value = action.invoke(input, options).get();

                return respondWithValue(response, requestContentType, content, value);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
            catch (ContentCodecException | ExecutionException e) {
                response.status(HttpStatus.SERVICE_UNAVAILABLE_503);
                return e;
            }
        }
        else {
            response.status(HttpStatus.NOT_FOUND_404);
            return "Action not found";
        }
    }

    private Map<String, Object> parseUrlParameters(Map<String, String[]> urlParams,
                                                   Map<String, Map<String, Object>> uriVariables) {
        Log.d("InvokeActionRoute", "parse url parameters "+urlParams.keySet()+" with uri variables " + uriVariables);
        Map<String, Object> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : urlParams.entrySet()) {
            String name = entry.getKey();
            String[] urlValue = entry.getValue();

            Map uriVariable = uriVariables.get(name);
            if (uriVariable != null) {
                Object type = uriVariable.get("type");

                if (type != null) {
                    if (type.equals("integer") || type.equals("number")) {
                        Integer value = Integer.valueOf(urlValue[0]);
                        params.put(name, value);
                    }
                    else if (type.equals("string")) {
                        String value = urlValue[0];
                        params.put(name, value);
                    }
                    else {
                        Log.w("InvokeActionRoute", "Not able to read variable "+name+" because variable type "+type+" is unknown");
                    }
                }
            }
        }
        return params;
    }

    private Object respondWithValue(Response response,
                                    String requestContentType,
                                    Content content,
                                    Object value) {
        try {
            Content outputContent = ContentManager.valueToContent(value, requestContentType);

            if (value != null) {
                response.type(content.getType());
                return outputContent;
            }
            else {
                return "";
            }
        }
        catch (ContentCodecException e) {
            response.status(HttpStatus.SERVICE_UNAVAILABLE_503);
            return e;
        }
    }
}
