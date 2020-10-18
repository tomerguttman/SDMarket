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

public class OrderHistoryForStoreServlet extends HttpServlet {
    synchronized private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject jsonObject = new JsonObject();
        response.setContentType("application/json");

        try {
            String userType = SessionUtils.getUserType(request);
            String username = SessionUtils.getUsername(request);
            String storeName = request.getParameter("selectedStore");
            String selectedZone = SessionUtils.getCurrentZone(request);
            SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());

            assert userType != null;
            if(userType.equals(Constants.CUSTOMER)) { jsonObject = sdMarketManager.getOrderHistoryJsonForCustomer(username, selectedZone); }
            else { jsonObject = sdMarketManager.getOrderHistoryJsonForShopOwner(username, storeName); }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            jsonObject.addProperty("message", e.getMessage());
        }
        finally {
            PrintWriter out = response.getWriter();
            out.println(jsonObject);
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
