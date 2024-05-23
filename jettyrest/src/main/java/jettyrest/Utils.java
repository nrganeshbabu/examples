package jettyrest;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.Fields;
import org.glassfish.jersey.uri.UriTemplate;

import com.google.gson.JsonObject;

public class Utils {

    public static final void getPathParameters(Request request, JsonObject requestparams) {
        String requestPath = Request.getPathInContext(request);
        UriTemplate ut = new UriTemplate(Constants.TEMPLATE_GET_EMPLOYEE_BY_ID_BY_CITY);
        Map<String, String> map = new HashMap<>(0);
        boolean isMatch = ut.match(requestPath, map);

        if (isMatch) {
            map.forEach((k, v) -> {
                requestparams.addProperty(k, v);
            });
        }
    }

    public static final void getQueryFields(Request request, JsonObject requestparams) {
        Fields queryFiels = Request.extractQueryParameters(request, StandardCharsets.UTF_8);
        queryFiels.forEach(f -> {
            requestparams.addProperty(f.getName(), f.getValue());
        });
    }

    public static final void getFormFields(Request request, JsonObject requestparams) {
        Fields formFields;
        try {
            formFields = Request.getParameters(request);
            formFields.forEach(f -> {
                requestparams.addProperty(f.getName(), f.getValue());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
