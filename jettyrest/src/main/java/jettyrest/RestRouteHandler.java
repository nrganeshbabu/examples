package jettyrest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.glassfish.jersey.uri.UriTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonObject;

public class RestRouteHandler extends Handler.Abstract {
    private static Logger log = LoggerFactory.getLogger(RestRouteHandler.class);

    public record HandlerDetails(Class<? extends RestResource> handler, String methodName) {
    }

    private final Table<UriTemplate, String, HandlerDetails> mappings = HashBasedTable.create();

    public void register(String httpMethod, String template, Class<? extends RestResource> handler,
            String methodName) {
        mappings.put(new UriTemplate(template), httpMethod, new HandlerDetails(handler, methodName));
    }

    @Override
    public boolean handle(Request request, Response response, Callback callback) throws Exception {
        String httpRequestMethod = request.getMethod();
        String requestPath = Request.getPathInContext(request);
        HandlerDetails handlerDetails = null;
        Iterator<Entry<UriTemplate, Map<String, HandlerDetails>>> iter = mappings.rowMap().entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<UriTemplate, Map<String, HandlerDetails>> me = iter.next();
            Map<String, String> map = new HashMap<>(0);
            UriTemplate u = me.getKey();
            Map<String, HandlerDetails> v = me.getValue();
            boolean isMatch = u.match(requestPath, map);

            if (isMatch && v.containsKey(httpRequestMethod)) {
                handlerDetails = v.get(httpRequestMethod);
                log.debug("Matched: {}", handlerDetails);
                break;
            }
        }

        if (handlerDetails != null) {
            RestResource clazz = handlerDetails.handler.getDeclaredConstructor().newInstance();
            Method method = clazz.getClass().getDeclaredMethod(handlerDetails.methodName(), Request.class,
                    Response.class, Callback.class);
            return (Boolean) method.invoke(clazz, request, response, callback);
        } else {
            /** SET FAILURE MESSAGE IN THE RESPONSE STRING */
            response.setStatus(HttpStatus.BAD_REQUEST_400);

            JsonObject jo = new JsonObject();
            JsonObject requestparams = new JsonObject();

            /** Add the Request-Params to the Json */
            Utils.getPathParameters(request, requestparams);
            Utils.getQueryFields(request, requestparams);
            Utils.getFormFields(request, requestparams);
            jo.add("requestparams", requestparams);

            jo.addProperty("message", "Invalid Request");
            jo.addProperty("httpRequestMethod", httpRequestMethod);
            jo.addProperty("uri", request.getHttpURI().toString());

            response.getHeaders().put(HttpHeader.CONTENT_TYPE, "application/json; charset=UTF-8");
            Content.Sink.write(response, true, jo.toString(), callback);
        }

        callback.succeeded();
        return true;
    }

}
