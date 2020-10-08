package sdmarket.utils;


import manager.SDMarketManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Path;

public class ServletUtils {

    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";

    /*
    Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
    the actual fetch of them is remained un-synchronized for performance POV
     */
    private static final Object userManagerLock = new Object();

    public static SDMarketManager getSDMarketManager(ServletContext servletContext) {
        if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
            synchronized (userManagerLock) {
                if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                    servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new SDMarketManager());
                }
            }
        }

        return (SDMarketManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }

    public static String getJsonResponseString(String message, boolean success) {
        return "{" +
                "\"message\":\"" + message + "\"," +
                "\"success\":" + success +
                "}";
    }

}