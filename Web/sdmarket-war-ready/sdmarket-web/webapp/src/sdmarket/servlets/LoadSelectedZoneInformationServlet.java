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

public class LoadSelectedZoneInformationServlet extends HttpServlet {
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String json = null;
        response.setContentType("application/json charset=utf-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate" );
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());

        String currentUserName = SessionUtils.getUsername(request);

        if(currentUserName == null){
            response.sendRedirect(Constants.LOGIN_URL);
        }

        String zoneName = request.getParameter("selectedZone");
        Zone selectedZone = sdMarketManager.getSystemZones().get(zoneName);

        try {
            json = gson.toJson(selectedZone);
        } catch (Exception e) {
            json = (ServletUtils.getJsonResponseString(e.getMessage(), false));
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
