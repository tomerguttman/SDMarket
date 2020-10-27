package sdmarket.servlets;

import SDMImprovedFacade.Store;
import SDMImprovedFacade.StoreItem;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GetAvailableItemsForPurchaseServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap<Integer, StoreItem> newStoreItemMap = new HashMap<>();

        Gson gson = new Gson();
        Store selectedStore;
        JsonObject json = new JsonObject();
        String pickedStoreName = "";
        response.setContentType("application/json charset=utf-8");
        SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());

        String currentUserName = SessionUtils.getUsername(request);
        String currentUserType = SessionUtils.getUserType(request);
        String currentZoneName = Objects.requireNonNull(SessionUtils.getCurrentZone(request)).replace(" ", "");
        String purchaseMethod = request.getParameter("currentPurchaseMethod");

        assert currentUserType != null;
        if(currentUserName == null) {
            response.sendRedirect(Constants.LOGIN_URL);
        }
        if(currentUserType.equals(Constants.SHOP_OWNER)) { response.sendRedirect(Constants.DASHBOARD_OWNER_URL); }

        if(purchaseMethod.equals("static")) {
            pickedStoreName = request.getParameter("pickedStore");
            selectedStore = sdMarketManager.getSelectedStoreByName(currentZoneName, pickedStoreName);
            newStoreItemMap.putAll(selectedStore.getItemsBeingSold());
            pushNonAvailableItemsToMap(sdMarketManager.getSystemZones().get(currentZoneName).getItemsAvailableInZone(), newStoreItemMap);
        }
        else {
            newStoreItemMap.putAll(sdMarketManager.getSystemZones().get(currentZoneName).getItemsAvailableInZone());
        }

        try {
            json.add("availableItems", gson.toJsonTree(newStoreItemMap));
        } catch (Exception e) {
            json.addProperty("message",e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            PrintWriter out = response.getWriter();
            out.println(json);
            out.flush();
        }
    }

    private void pushNonAvailableItemsToMap(Map<Integer, StoreItem> zoneItems, HashMap<Integer, StoreItem> newStoreItemMap) {
        for (StoreItem sItem : zoneItems.values() ) {
            if(!newStoreItemMap.containsKey(sItem.getId())) {
                newStoreItemMap.put(sItem.getId(), sItem);
                newStoreItemMap.get(sItem.getId()).setIsAvailable(false);
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
