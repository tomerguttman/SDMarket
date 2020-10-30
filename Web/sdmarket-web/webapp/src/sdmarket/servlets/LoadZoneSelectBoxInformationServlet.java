package sdmarket.servlets;

import SDMImprovedFacade.ShopOwner;
import SDMImprovedFacade.Zone;
import com.google.gson.Gson;
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
import java.util.List;

public class LoadZoneSelectBoxInformationServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        response.setContentType("application/json charset=utf-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate" );
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());

        String currentUserName = SessionUtils.getUsername(request);
        String currentUserType = SessionUtils.getUserType(request);

        if(currentUserName == null){
            response.sendRedirect(Constants.LOGIN_URL);
        }

        List<String> availableZonesNames = sdMarketManager.getZonesNames();

        try {
            if(currentUserType.equals(Constants.SHOP_OWNER)){
                int amountOfNotifications = Integer.parseInt(request.getParameter("amountOfNotifications"));
                ShopOwner currentShopOwner = (ShopOwner)sdMarketManager.getUser(currentUserName);
                jsonObject.add("notifications", gson.toJsonTree(currentShopOwner.getNewestNotifications(amountOfNotifications)));
            }

            jsonObject.add("zonesAvailable", gson.toJsonTree(availableZonesNames));
            jsonObject.addProperty("userType", currentUserType);
            //
            jsonObject.addProperty("userName", currentUserName);
            //
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
