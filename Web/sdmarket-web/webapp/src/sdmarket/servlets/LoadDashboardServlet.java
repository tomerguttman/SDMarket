package sdmarket.servlets;

import SDMImprovedFacade.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class LoadDashboardServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String json = null;
        response.setContentType("application/json charset=utf-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate" );
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());

        String currentUserName = SessionUtils.getUsername(request);
        String currentUserType = SessionUtils.getUserType(request);
        User currentUser = sdMarketManager.getUser(currentUserName);
        List<Zone> systemZones = (sdMarketManager.getSystemZones() != null) ? sdMarketManager.getSystemZonesAsList() : null;
        List<Transaction> userTransactions = currentUser.getUserTransactionsList();
        List<User> otherUsers = sdMarketManager.getOtherUsers(currentUserName);

        try {
            assert currentUserType != null;
            if(currentUserType.equals(Constants.CUSTOMER)) { json = getCustomerJson(currentUser, systemZones, userTransactions, otherUsers, sdMarketManager); }
            else { json = getShopOwnerJson(currentUser, systemZones, userTransactions, otherUsers); }

        } catch (Exception e) {
            json = (ServletUtils.getJsonResponseString(e.getMessage(), false));
            System.out.println(e.getMessage());
        } finally {
            PrintWriter out = response.getWriter();
            out.println(json);
            out.flush();
        }
    }

    private String getCustomerJson(User currentUser, List<Zone> systemZones, List<Transaction> userTransactions, List<User> otherUsers, SDMarketManager sdMarketManager) {
        Gson gson = new Gson();
        Customer customer = (Customer)currentUser;
        String lovedItem = (customer.getMostLovedItem() != -1) ?
                sdMarketManager.getSystemItems().get(customer.getMostLovedItem()).getName() : "None";
        CustomerJsonObject customerJson = new CustomerJsonObject(systemZones, userTransactions, otherUsers, customer.getBalance(), customer.getTotalNumberOfOrders(),
                customer.getAverageOrderCost(), lovedItem);

        return gson.toJson(customerJson);
    }

    private String getShopOwnerJson(User currentUser, List<Zone> systemZones, List<Transaction> userTransactions, List<User> otherUsers) {
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        ShopOwner shopOwner = (ShopOwner)currentUser;
        ShopOwnerJsonObject shopOwnerJsonObject = new ShopOwnerJsonObject(systemZones, userTransactions, otherUsers, shopOwner.getBalance(), shopOwner.getStoresOwned().size(),
                shopOwner.getUserOrdersMap().size(), shopOwner.getAverageRating());
        String json = gson.toJson(shopOwnerJsonObject);

        return gson.toJson(shopOwnerJsonObject);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
