package sdmarket.servlets;

import SDMImprovedFacade.*;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LoadDashboardServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        response.setContentType("application/json");
        SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());

        Zone zone = new Zone("Yaffo", "Moshe");
        Collection<Zone> zones = new ArrayList<>();
        zones.add(zone);

        String currentUserName = SessionUtils.getUsername(request);
        String currentUserType = SessionUtils.getUserType(request);
        User currentUser = sdMarketManager.getUser(currentUserName);
        //Collection<Zone> systemZones = (sdMarketManager.getSystemZones() != null) ? sdMarketManager.getSystemZones().values() : null;
        List<Transaction> userTransactions = currentUser.getUserTransactionsList();
        List<User> otherUsers = sdMarketManager.getOtherUsers(currentUserName);

        try {
            assert currentUserType != null;
            if(currentUserType.equals(Constants.CUSTOMER)) { json = getCustomerJson(currentUser, /*systemZones*/zones, userTransactions, otherUsers, sdMarketManager); }
            else { json = getShopOwnerJson(currentUser, /*systemZones*/zones, userTransactions, otherUsers); }

        } catch (Exception e) {
            json = (gson.toJsonTree(ServletUtils.getJsonResponseString(e.getMessage(), false))).getAsJsonObject();
            System.out.println(e.getMessage());
        } finally {
            PrintWriter out = response.getWriter();
            out.println(json);
            out.flush();
        }
    }

    private JsonObject getCustomerJson(User currentUser, Collection<Zone> systemZones, List<Transaction> userTransactions, List<User> otherUsers, SDMarketManager sdMarketManager) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        String lovedItem = (currentUser.getMostLovedItem() != -1) ?
                sdMarketManager.getSystemItems().get(currentUser.getMostLovedItem()).getName() : "None";
        Collection<Zone> fakeZones = new ArrayList<>();

        jsonObject.add("systemZones", gson.toJsonTree(systemZones, fakeZones.getClass()));
        jsonObject.add("userTransactions", gson.toJsonTree(userTransactions, userTransactions.getClass()));
        jsonObject.add("otherUsers", gson.toJsonTree(otherUsers, otherUsers.getClass()));
        jsonObject.add("currentBalance", gson.toJsonTree(currentUser.getBalance(), Double.class));
        jsonObject.add("totalOrders", gson.toJsonTree(currentUser.getUserOrdersMap().size(), Integer.class));
        jsonObject.add("averageOrderCost", gson.toJsonTree(currentUser.getAverageOrderCost(), Double.class));
        jsonObject.add("mostLovedItem", gson.toJsonTree(lovedItem, lovedItem.getClass()));

        return jsonObject;
    }

    private JsonObject getShopOwnerJson(User currentUser, Collection<Zone> systemZones, List<Transaction> userTransactions, List<User> otherUsers) {
        Gson gson = new Gson();
        ShopOwner shopOwner = (ShopOwner)currentUser;
        JsonObject jsonObject = new JsonObject();
        Collection<Zone> fakeZones = new ArrayList<>();

        jsonObject.add("systemZones", gson.toJsonTree(systemZones, fakeZones.getClass()));
        jsonObject.add("userTransactions", gson.toJsonTree(userTransactions, userTransactions.getClass()));
        jsonObject.add("otherUsers", gson.toJsonTree(otherUsers, otherUsers.getClass()));
        jsonObject.add("totalEarnings", gson.toJsonTree(shopOwner.getBalance(), Double.class));
        jsonObject.add("storesOwned", gson.toJsonTree(shopOwner.getStoresOwned().size(), Integer.class));
        jsonObject.add("ordersMadeFromOwnedStores", gson.toJsonTree(shopOwner.getUserOrdersMap().size(), Integer.class));
        jsonObject.add("averageRating", gson.toJsonTree(shopOwner.getAverageRating(), Double.class));

        return jsonObject;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
