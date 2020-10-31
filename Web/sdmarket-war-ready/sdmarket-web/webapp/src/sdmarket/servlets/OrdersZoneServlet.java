package sdmarket.servlets;

import constants.Constants;
import sdmarket.utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OrdersZoneServlet extends HttpServlet {
    private final String SHOP_OWNER_ORDER_URL = "table-owner.html";
    private final String CUSTOMER_ORDER_URL = "table-customer.html";

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String userType = SessionUtils.getUserType(request);
            String redirectURL = null;
            if(userType.equals(Constants.CUSTOMER)) { redirectURL = CUSTOMER_ORDER_URL; }
            else { redirectURL = SHOP_OWNER_ORDER_URL; }

            request.getSession().setAttribute(Constants.CURRENT_ZONE ,request.getParameter(Constants.ZONE_NAME).replace("_", ""));
            response.sendRedirect(redirectURL);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
