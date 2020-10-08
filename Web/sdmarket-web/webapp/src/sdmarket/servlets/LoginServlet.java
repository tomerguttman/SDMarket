package sdmarket.servlets;

import constants.Constants;
import manager.SDMarketManager;
import sdmarket.utils.ServletUtils;
import sdmarket.utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class LoginServlet extends HttpServlet {

    private final String DASHBOARD_OWNER_URL = "dashboard-owner.html";
    private final String DASHBOARD_CUSTOMER_URL = "dashboard-customer.html";
    private final String LOGIN_URL = "login.html";
    private final String LOGIN_ERROR_URL = "login_attempt_after_error.jsp";  // must start with '/' since will be used in request dispatcher...
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);

        SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());

        if (usernameFromSession == null) {
            //User is not logged in yet
            String usernameFromParameter = request.getParameter(Constants.USERNAME);
            String userTypeFromParameter = request.getParameter(Constants.USER_TYPE);
            if (usernameFromParameter == null) {
                response.sendRedirect(LOGIN_URL);
            }
            else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();

                synchronized (this) {
                    request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
                    request.getSession(true).setAttribute(Constants.USER_TYPE, userTypeFromParameter);

                    if (sdMarketManager.isUserExists(usernameFromParameter))
                    {
                        System.out.println("Login in to an existing user " + usernameFromParameter);
                        redirectToRelevantURLByType(userTypeFromParameter, response);
                    } else {
                        //add the new user to the users list
                        sdMarketManager.addUser(usernameFromParameter, userTypeFromParameter);
                        //set the username in a session so it will be available on each request
                        //the true parameter - means that if a session object does not exists yet create a new one
                        System.out.println("On login, request URI is: " + request.getRequestURI());
                        redirectToRelevantURLByType(userTypeFromParameter, response);
                    }
                }
            }
        }
        else {
            //user is already logged in
            redirectToRelevantURLByType(Objects.requireNonNull(SessionUtils.getUserType(request)), response);
        }
    }

    private void redirectToRelevantURLByType(String userTypeFromParameter, HttpServletResponse response) throws IOException {
        if(userTypeFromParameter.equals(Constants.CUSTOMER)) { response.sendRedirect(DASHBOARD_CUSTOMER_URL); }
        else { response.sendRedirect(DASHBOARD_OWNER_URL); }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
