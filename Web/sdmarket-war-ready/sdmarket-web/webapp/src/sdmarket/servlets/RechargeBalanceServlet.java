package sdmarket.servlets;

import SDMImprovedFacade.Customer;
import SDMImprovedFacade.Transaction;
import com.google.gson.Gson;
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

public class RechargeBalanceServlet extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String json = "";
        response.setContentType("application/json");
        SDMarketManager sdMarketManager = ServletUtils.getSDMarketManager(getServletContext());
        String currentUserName = SessionUtils.getUsername(request);
        Customer currentUser = (Customer) sdMarketManager.getUser(currentUserName);
        String amountToRecharge = request.getParameter(Constants.AMOUNT_TO_RECHARGE);
        String dateOfRecharge = request.getParameter(Constants.DATE_RECHARGE);
        double beforeBalance = currentUser.getBalance();
        currentUser.addTransaction("Recharge", amountToRecharge, dateOfRecharge);

        try {
            int lastTransactionIndex = currentUser.getUserTransactionsList().size() - 1;
            Transaction transaction = currentUser.getUserTransactionsList().get(lastTransactionIndex);
            json = gson.toJson(transaction);
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
