package sdmarket.servlets;

import SDMImprovedFacade.ShopOwner;
import SDMImprovedFacade.StoreItem;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import manager.SDMarketManager;
import sdmarket.utils.ServletUtils;
import sdmarket.utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class CreateStoreServlet extends HttpServlet {
    private final String DASHBOARD_OWNER_URL = "dashboard-owner.html";

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject jsonObject = new JsonObject();

        try {
            Gson gson = new Gson();
            response.setContentType("application/json");

            SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());
            String currentUserName = SessionUtils.getUsername(request);
            ShopOwner currentShopOwner = (ShopOwner) sdMarketManager.getUser(currentUserName);
            String currentZoneName = SessionUtils.getCurrentZone(request);

            if(currentZoneName != null) {
                String storeName = request.getParameter("storeName");
                int ppk = Integer.parseInt(request.getParameter("ppk"));
                int xCoordinate = Integer.parseInt(request.getParameter("xCoordinate"));
                int yCoordinate = Integer.parseInt(request.getParameter("yCoordinate"));
                Map<Integer, StoreItem> itemsBeingSoldMap = sdMarketManager.createItemsBeingSoldFromJson(request.getParameter("storeItems"), currentZoneName);
                sdMarketManager.createNewStoreAndAddToZoneAndUser(currentShopOwner, currentZoneName, storeName, ppk, xCoordinate, yCoordinate, itemsBeingSoldMap);
                jsonObject.addProperty("message", "The store was created successfully!");
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
