package sdmarket.servlets;

import SDMImprovedFacade.Store;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import constants.Constants;
import manager.SDMarketManager;
import netscape.javascript.JSObject;
import sdmarket.utils.ServletUtils;
import sdmarket.utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Objects;

public class GetAvailableStoresInZoneServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        response.setContentType("application/json charset=utf-8");
        SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());

        String currentUserName = SessionUtils.getUsername(request);
        String currentUserType = SessionUtils.getUserType(request);

        assert currentUserType != null;
        if(currentUserName == null) {
            response.sendRedirect(Constants.LOGIN_URL);
        }
        if(currentUserType.equals(Constants.SHOP_OWNER)) { response.sendRedirect(Constants.DASHBOARD_OWNER_URL); }

        String currentZoneName = Objects.requireNonNull(SessionUtils.getCurrentZone(request));
        HashMap<Integer, Store> currentZoneStoresMap = sdMarketManager.getSystemZones().get(currentZoneName).getStoresInZone();

        try {
            json.add("availableStores", gson.toJsonTree(currentZoneStoresMap));
        } catch (Exception e) {
            json.addProperty("message", e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            PrintWriter out = response.getWriter();
            out.println(json);
            out.flush();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
