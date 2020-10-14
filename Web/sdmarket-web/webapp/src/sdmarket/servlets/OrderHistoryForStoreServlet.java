package sdmarket.servlets;

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
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String json = null;
        
        try {
            String userType = SessionUtils.getUserType(request);
            String username = SessionUtils.getUsername(request);
            String storeName = request.getParameter("selectedStore");
            SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());

            if(userType.equals(Constants.CUSTOMER)) { json = sdMarketManager.getOrderHistoryJsonForCustomer(username); }
            else { json = sdMarketManager.getOrderHistoryJsonForShopOwner(username, storeName); }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            json = ServletUtils.getJsonResponseString(e.getMessage(), false);
        }
        finally {
            PrintWriter out = response.getWriter();
            out.println(json);
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
