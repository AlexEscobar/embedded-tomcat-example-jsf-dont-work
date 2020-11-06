/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.filter;

import com.innotech.xraymanagerapp.controller.LoginController;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import com.innotech.xraymanagerapp.model.Users;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Alexander Escobar L.
 */
public class SysFilter implements Filter {
    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 

    private FilterConfig filterConfig = null;
    private static final String INVALID_SESSION = "/login/";//If the session is not created, will redirect to the login page
    private static final String REGISTRATION_PAGE = "/worklist";
    private static final String STUDIES_PAGE = "/studies";
//    private static final String STUDIES_PAGE = "/registration";
    private static final String SERVICE_URL = "/webresources";
    private static final String DICOM_VIEWER = "/viewer";
    private static boolean isRedirect = false;
    private String JSIDCookieValue = "";

//    private final String sesionInvalida = "/CompetenciasProyecto/faces/sesionInvalida.xhtml";
    public SysFilter() {
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path = ((HttpServletRequest) request).getRequestURI();
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
//        System.out.println("path on the filteRRR: "+path);
//         Anyone can use Faces resources (css, images, javascript)

//        if (path.startsWith("/")) {
//            chain.doFilter(request, response);
//            return;
//        }
        if (path.startsWith("/faces/javax.faces.resource/")) {
            chain.doFilter(request, response);
            return;
        }
//         Anyone can use omnifaces push web sockets
        if (path.startsWith("/a.push")) {
//            System.out.println("allowing omnifaces.push to be accessed");
            chain.doFilter(request, response);
            return;
        }
//         Anyone can use omnifaces push web sockets
        if (path.startsWith("/omnifaces.push/")) {
//            System.out.println("allowing omnifaces.push to be accessed");
            chain.doFilter(request, response);
            return;
        }
//         Anyone can use the dicom studies rest API service
        if (path.contains("/webresources/dicomService")) {
            chain.doFilter(request, response);
            return;
        }
//         Anyone can use the dicom studies rest API service
        if (path.contains("/webresources/dicomSendService")) {
//            System.out.println("allowing omnifaces.push to be accessed");
            chain.doFilter(request, response);
            return;
        }

        //ssl certificate validation file
        if (path.contains("DC8CB4851F61887D")) {
            chain.doFilter(request, response);
            return;
        }

//        if the user is trying to access login page, just forward the request
        if (path.contains(INVALID_SESSION)) {
//            Pass to the next filter
            //expireJSessionIdCookie(req, res);
            chain.doFilter(request, response);
            return;
        }

        StringBuilder rootPath = new StringBuilder().append("/");
        boolean hasPermission = false;

        Users user = null;
        try {
            user = (Users) req.getSession().getAttribute("user");
//            user = JsfUtil.getHc02SessionBean();
        } catch (Exception e) {
            Logger.getLogger(SysFilter.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        try {
            if (user != null) {
                if (user.getUsername().equals("root") || path.contains(DICOM_VIEWER) || path.contains(STUDIES_PAGE)
                        || path.contains(SERVICE_URL)) {
                    //                  Pass to the next filter
                    try {

                        chain.doFilter(request, response);//2  
                    } catch (IOException | ServletException | IndexOutOfBoundsException e) {
                        Logger.getLogger(SysFilter.class.getName()).log(Level.SEVERE, e.getMessage());
                    }
                    return;
                }
                LoginController lc = (LoginController) req.getSession().getAttribute("loginController");
//                System.out.println("Has URLS Permission?::::::: " + lc.hasPermission(52));
                // if the current server is an xray server
                // or if the server is a cloud-viewer server but the user is an administrator of a clinic which profile has the permission with id = 25
                // then the filter system allows the user to reach different pages already related to it's profile.
                if (AuthenticationUtils.isXrayServer() || lc.hasPermission(52)) {// 52 is the permission to access the urls the profile has access to
                    List<String> urls = lc.getAu().getUrls();
//                  Iterate throught urls the user is allowed to access
                    for (String url : urls) {
//                        System.out.println("The CURRENT url :  "+url);

//                      if the requested url is in the list, then the system knows the user has the permission
                        if (path.replace(rootPath, "").replace("/", "").equals(url.replace("/", ""))) {
                            hasPermission = true;

                            //                        create the url to be redirected
                            rootPath.append(url);
                            break;
                        }
                    }
                    // if the user has permission to the requested url, redirects it
                    if (hasPermission) {
                        chain.doFilter(request, response);//2        
                    } else {

//                        System.out.println("filter 5");
//                     Otherwise redirects the user to the login page
                        ((HttpServletResponse) response).sendRedirect(STUDIES_PAGE);//2                        
                    }
                } else {
//                    System.out.println("filter 6");
//                     Otherwise redirects the user to the Studies page
                    ((HttpServletResponse) response).sendRedirect(STUDIES_PAGE);//2 
                    chain.doFilter(request, response);
                }
            } else {
//                System.out.println("filter 7");
//              Otherwise redirect to login page
                ((HttpServletResponse) response).sendRedirect(INVALID_SESSION);//2
            }

        } catch (Exception e) {
//            System.out.println("filter 8");
            Logger.getLogger(SysFilter.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            ((HttpServletResponse) response).sendRedirect(INVALID_SESSION);
        }

    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    /**
     * Init method for this filter
     *
     * @param filterConfig
     */
    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public static boolean isIsRedirect() {
        return isRedirect;
    }

    public static void setIsRedirect(boolean aIsRedirect) {
        isRedirect = aIsRedirect;
    }

    /**
     * Deletes the JSESSIONID cookie from the browser to avoid the error
     * /login.xhtml @121,97 value="#{loginController.selected.username}": Target
     * Unreachable, 'null' returned null when the user by mistake has typed a
     * wrong user or password and then types the correct credentials.
     *
     * @param request
     * @param response
     */
    public void expireJSessionIdCookie(HttpServletRequest request, HttpServletResponse response) {
        //remove single signon cookie if it hasn't been validated yet
        try {

            response.setContentType("text/html");
            Cookie[] allCookies = request.getCookies();

            for (Cookie cookieToDelete : allCookies) {
                String name = cookieToDelete.getName();
                if (name.equalsIgnoreCase("JSESSIONID")) {
                    System.out.println(" Name=" + name + " Value=" + cookieToDelete.getValue());
                    System.out.println(" Value=" + JSIDCookieValue);
                    if (cookieToDelete.getValue().equals(JSIDCookieValue)) {
                        cookieToDelete.setValue("");
                        cookieToDelete.setMaxAge(0);
                        cookieToDelete.setVersion(0);
                        cookieToDelete.setPath("/");
                        response.addCookie(cookieToDelete);
                        JSIDCookieValue = "";
                    } else {
                        JSIDCookieValue = cookieToDelete.getValue();
                    }
                }
            }
        } catch (NullPointerException e) {
            Logger.getLogger(SysFilter.class.getName()).log(Level.SEVERE, e.getMessage());
        }
    }
}
