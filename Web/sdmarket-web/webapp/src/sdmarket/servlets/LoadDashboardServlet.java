package sdmarket.servlets;

import SDMImprovedFacade.*;
import com.google.gson.Gson;
import manager.SDMarketManager;
import sdmarket.utils.ServletUtils;
import sdmarket.utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

public class LoadDashboardServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String json = null;
        response.setContentType("application/json");
        SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());
        String currentUserName = SessionUtils.getUsername(request);
        String currentUserType = SessionUtils.getUserType(request);
        User currentUser = sdMarketManager.getUser(currentUserName);
        Collection<Zone> systemZones = sdMarketManager.getSystemZones().values();
        List<Transaction> userTransactions = currentUser.getUserTransactionsList();
        List<User> otherUsers = sdMarketManager.getOtherUsers(currentUserName);
        String lovedItem = (currentUser.getMostLovedItem() != -1) ?
                sdMarketManager.getSystemItems().get(currentUser.getMostLovedItem()).getName() : "None";

        try {
            json = "{" +
                        "systemZones:" + gson.toJson(systemZones) +
                        ", userTransactions: " + gson.toJson(userTransactions) +
                        ", otherUsers:" + gson.toJson(otherUsers) +
                        ", currentBalance:" + currentUser.getBalance() +
                        ", totalOrders:" + currentUser.getUserOrdersMap().size() +
                        ", averageOrderCost:" + currentUser.getAverageOrderCost() +
                        ", mostLovedItem:" + lovedItem +
                    "}";
        } catch (Exception e) {
            json = ServletUtils.getJsonResponseString(e.getMessage(), false);
            System.out.println(e.getMessage());
        } finally {
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
