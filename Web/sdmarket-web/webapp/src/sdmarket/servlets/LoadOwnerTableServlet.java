package sdmarket.servlets;

import SDMImprovedFacade.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

public class LoadOwnerTableServlet extends HttpServlet {
    private final String DASHBOARD_OWNER_URL = "dashboard-owner.html";

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject jsonObject = new JsonObject();

        try {
            Gson gson = new Gson();
            response.setContentType("application/json charset=utf-8");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate" );
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());


            String currentUserName = SessionUtils.getUsername(request);
            ShopOwner currentShopOwner = (ShopOwner) sdMarketManager.getUser(currentUserName);
            String currentZoneName = SessionUtils.getCurrentZone(request);

            if(currentZoneName != null) {
                List<Feedback> zoneFeedbackList = currentShopOwner.getZoneFeedbacks(currentZoneName);
                List<Store> zoneStores = currentShopOwner.getZoneStores(currentZoneName);
                List<StoreItem> zoneItemsList = currentShopOwner.getZoneItems(currentZoneName);
                int amountOfNotifications = Integer.parseInt(request.getParameter("amountOfNotifications"));
                List<Notification> newestNotifications = currentShopOwner.getNewestNotifications(amountOfNotifications);

                if (zoneFeedbackList != null) { jsonObject.add("feedbacks", gson.toJsonTree(zoneFeedbackList)); }
                if (zoneStores != null) { jsonObject.add("storesAvailable", gson.toJsonTree(zoneStores)); }
                if(zoneItemsList != null) { jsonObject.add("zoneItems", gson.toJsonTree(zoneItemsList)); }
                jsonObject.add("notifications", gson.toJsonTree(newestNotifications));
                jsonObject.addProperty("userName", currentUserName);
            }
            else { response.sendRedirect(DASHBOARD_OWNER_URL); } //Arrived without selecting a zone

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
