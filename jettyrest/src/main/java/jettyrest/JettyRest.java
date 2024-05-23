package jettyrest;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class JettyRest {
    private static Logger log = LoggerFactory.getLogger(JettyRest.class);

    public static void main(String[] args) {
        System.out.println("Hello World!");
        Server server = JettyRest.newServer();

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception while starting jetty server", e);
        }
    }

    private static Server newServer() {
        Server server = new Server();

        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setFormEncodedMethods("POST");
        httpConfig.setRelativeRedirectAllowed(true);

        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        connector.setPort(8080);
        server.addConnector(connector);

        ContextHandler ach = new ContextHandler("/");
        RestRouteHandler restRouterHandler = new RestRouteHandler();
        restRouterHandler.register("GET", Constants.TEMPLATE_GET_EMPLOYEE_BY_ID, Employee.class, "getEmployeeById");
        restRouterHandler.register("GET", Constants.TEMPLATE_GET_EMPLOYEE_BY_ID_BY_CITY, Employee.class, "getEmployeeByIdByCity");
        restRouterHandler.register("POST", Constants.TEMPLATE_SAVE_EMPLOYEE, Employee.class, "saveEmployee");
        ach.setHandler(restRouterHandler);

        server.setHandler(ach);
        return server;
    }

}
