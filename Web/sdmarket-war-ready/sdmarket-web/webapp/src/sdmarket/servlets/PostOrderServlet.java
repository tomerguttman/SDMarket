package sdmarket.servlets;

import com.google.gson.JsonObject;
import constants.Constants;
import manager.SDMarketManager;
import sdmarket.utils.ServletUtils;
import sdmarket.utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class PostOrderServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject jsonObject = new JsonObject();

        try {
            response.setContentType("application/json");
            SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());
            String currentUserName = SessionUtils.getUsername(request);
            String currentZoneName = SessionUtils.getCurrentZone(request);

            if(currentZoneName != null) {
                String purchaseMethod = request.getParameter("purchaseMethod");
                String orderString = request.getParameter("order");
                sdMarketManager.addNewOrder(purchaseMethod, orderString, currentUserName, currentZoneName);
                jsonObject.addProperty("message", "The order was made successfully!");
            }
            else { response.sendRedirect(Constants.DASHBOARD_CUSTOMER_URL); } //Arrived without selecting a zone
        } catch (Exception e) {
            jsonObject.addProperty("message", e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            PrintWriter out = response.getWriter();
            out.println(jsonObject);
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
