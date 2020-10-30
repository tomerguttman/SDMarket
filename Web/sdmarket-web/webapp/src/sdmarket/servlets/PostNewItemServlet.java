package sdmarket.servlets;

import SDMImprovedFacade.ShopOwner;
import SDMImprovedFacade.Store;
import SDMImprovedFacade.StoreItem;
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
import java.util.Map;

public class PostNewItemServlet extends HttpServlet {
    private final String DASHBOARD_OWNER_URL = "dashboard-owner.html";

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject jsonObject = new JsonObject();

        try {
            response.setContentType("application/json");

            SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());
            String currentUserName = SessionUtils.getUsername(request);
            ShopOwner currentShopOwner = (ShopOwner) sdMarketManager.getUser(currentUserName);
            String currentZoneName = SessionUtils.getCurrentZone(request);

            if(currentZoneName != null) {
                String itemName = request.getParameter("itemName");
                String purchaseCategory = request.getParameter("purchaseCategory");
                Map<Integer, Integer> storesToAddItemToList = sdMarketManager.createStoreListFromJson(request.getParameter("storesToAddItemToList"), currentZoneName);
                sdMarketManager.createNewItemAndAddToStoresAndZone(currentShopOwner, currentZoneName, itemName, purchaseCategory,  storesToAddItemToList);
                jsonObject.addProperty("message", String.format("The item '%s' was added successfully!", itemName));
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
