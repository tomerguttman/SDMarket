package sdmarket.servlets;

import SDMImprovedFacade.ShopOwner;
import SDMImprovedFacade.Zone;
import manager.SDMarketManager;
import sdmarket.utils.ServletUtils;
import sdmarket.utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;

@MultipartConfig(fileSizeThreshold = 1024*1024, maxFileSize = 1024*1024*5, maxRequestSize = 1024*1024*5*5)
public class UploadZoneXMLServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder sBuilder = new StringBuilder();

        try {
            response.setContentType("application/json");
            String usernameFromSession = SessionUtils.getUsername(request);
            SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());
            Part fileContent = request.getParts().iterator().next();
            Zone zoneToAdd = sdMarketManager.getSDMLogic().validateZoneXML(fileContent.getInputStream(), usernameFromSession, sBuilder);
            if(sBuilder.toString().equals("")) {
                sBuilder.append("The file was uploaded successfully");
                ((ShopOwner)sdMarketManager.getUser(usernameFromSession)).addZoneToShopOwner(zoneToAdd);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            sBuilder.setLength(0);
            sBuilder.append(e.getMessage());
        } finally {
            PrintWriter out = response.getWriter();
            out.println(ServletUtils.getJsonResponseString(sBuilder.toString(), false));
            out.flush();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
