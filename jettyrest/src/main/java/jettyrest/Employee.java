package jettyrest;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class Employee implements RestResource {
    private static Logger log = LoggerFactory.getLogger(Employee.class);

    /**
     * Fetches Employee Details by Id
     * 
     * @param request Handles GET Request of type /employee/id/{id}
     * @return JSON String with the details
     */
    public boolean getEmployeeById(Request request, Response response, Callback callback) {
        JsonObject jo = new JsonObject();
        JsonObject requestparams = new JsonObject();

        /** Add the Request-Params to the Json */
        Utils.getPathParameters(request, requestparams);
        Utils.getQueryFields(request, requestparams);
        Utils.getFormFields(request, requestparams);
        jo.add("requestparams", requestparams);

        /** Generate the Response */

        String empId = String.valueOf(new Random().nextInt(999, 99999));

        jo.addProperty("empId", empId);
        jo.addProperty("name", "John Doe");
        jo.addProperty("City", "Boston");

        response.setStatus(200);
        response.getHeaders().put(HttpHeader.CONTENT_TYPE, "application/json; charset=UTF-8");
        Content.Sink.write(response, true, jo.toString(), callback);
        callback.succeeded();
        return true;
    }

    /**
     * Fetches Employee Details by Id and City
     * 
     * @param request Handles GET Request of type /employee/id/{id}/city/{city}
     * @return JSON String with the details
     */
    public boolean getEmployeeByIdByCity(Request request, Response response, Callback callback) {
        JsonObject jo = new JsonObject();
        JsonObject requestparams = new JsonObject();
        try {
            log.debug("*** Query-Fields ***");
            Fields queryFiels = Request.extractQueryParameters(request, StandardCharsets.UTF_8);
            queryFiels.forEach(f -> {
                requestparams.addProperty(f.getName(), f.getValue());
            });

            /** Add the Request-Params to the Json */
            Utils.getPathParameters(request, requestparams);
            Utils.getQueryFields(request, requestparams);
            Utils.getFormFields(request, requestparams);

            jo.add("requestparams", requestparams);

        } catch (Exception e) {
            log.error("Execption while parsing request parameters", e);
        }

        String empId = String.valueOf(new Random().nextInt(999, 99999));

        jo.addProperty("empId", empId);
        jo.addProperty("name", "John Doe");
        jo.addProperty("City", "Boston");

        response.setStatus(200);
        response.getHeaders().put(HttpHeader.CONTENT_TYPE, "application/json; charset=UTF-8");
        Content.Sink.write(response, true, jo.toString(), callback);
        callback.succeeded();
        return true;
    }

    /**
     * 
     * @param request Handles POST Request of type /employee
     * @return JSON String with the details
     */
    public boolean saveEmployee(Request request, Response response, Callback callback) {
        JsonObject jo = new JsonObject();
        JsonObject requestparams = new JsonObject();
        Utils.getFormFields(request, requestparams);

        /** Generate the Response */
        String empId = String.valueOf(new Random().nextInt(999, 99999));
        jo.addProperty("message", "Employee Has Been Created Successfully");
        jo.addProperty("empId", empId);

        response.setStatus(200);
        response.getHeaders().put(HttpHeader.CONTENT_TYPE, "application/json; charset=UTF-8");
        Content.Sink.write(response, true, jo.toString(), callback);
        callback.succeeded();
        return true;
    }
}
