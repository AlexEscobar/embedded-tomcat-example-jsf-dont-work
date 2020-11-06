/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.filter;

import com.innotech.xraymanagerapp.model.Users;
import java.io.IOException;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Alexander Escobar L.
 */
@PreMatching             // (1)
@Provider              // (2)
public class IncomingRequestSecurityFilter implements ContainerRequestFilter {

    @EJB
    private com.innotech.xraymanagerapp.dto.UsersFacade ejbUsersFacade;
    int counter = 0;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final MultivaluedMap<String, String> requestHeaders = requestContext.getHeaders();
        counter++;
        requestHeaders.entrySet().forEach((entry) -> {
            Object key = entry.getKey();
            Object val = entry.getValue();
//            System.out.println("Header key: " + key);
//            System.out.println("Header value: " + val);
        });
        String xAuth = requestContext.getHeaderString("Authorization");
        System.out.println("x-auth: " + xAuth + "|" + counter);
//        xAuth = xAuth.replace("Bearer", "").trim();
//        System.out.println("x-auth: " + xAuth);
        // No auth - abort
//        if (counter % 2 == 0) {
//            if (xAuth == null || xAuth.isEmpty()) {      // (3)
//                requestContext.abortWith(Response.status(
//                        Response.Status.UNAUTHORIZED).build());
//                // (4)
//            } else {
//                Users user = ejbUsersFacade.getUserByToken(xAuth);
//                System.out.println("com.innotech.xraymanagerapp.filter.IncomingRequestSecurityFilter says The user found is: " + user);
//                if (Objects.isNull(user)) {
//                    requestContext.abortWith(Response.status(
//                            Response.Status.UNAUTHORIZED).build());
//                }
//            }
//        }
    }
}
