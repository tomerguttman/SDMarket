package sdmarket.servlets;

import constants.Constants;
import sdmarket.utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RedirectToRelevantDashboardServlet extends HttpServlet {
    private final String DASHBOARD_OWNER_URL = "dashboard-owner.html";
    private final String DASHBOARD_CUSTOMER_URL = "dashboard-customer.html";

    synchronized private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        try {
            String userType = SessionUtils.getUserType(request);

            assert userType != null;
            if(userType.equals(Constants.CUSTOMER)) { response.sendRedirect(DASHBOARD_CUSTOMER_URL); }
            else { response.sendRedirect(DASHBOARD_OWNER_URL); }
        }
        catch (Exception e) {
            PrintWriter out = response.getWriter();
            out.println(e.getMessage());
            out.flush();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
