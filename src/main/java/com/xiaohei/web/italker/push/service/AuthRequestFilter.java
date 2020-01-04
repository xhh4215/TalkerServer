package com.xiaohei.web.italker.push.service;

import com.google.common.base.Strings;
import com.xiaohei.web.italker.push.bean.api.base.ResponseModel;
import com.xiaohei.web.italker.push.bean.db.User;
import com.xiaohei.web.italker.push.factory.UserFactory;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

@Provider
public class AuthRequestFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String relationPath = ((ContainerRequest)requestContext).getPath(false);
        if (relationPath.startsWith("account/login")|| relationPath.startsWith("account/register")){
            return;
        }
        String token = requestContext.getHeaders().getFirst("token");
        if (!Strings.isNullOrEmpty(token)){
            final User self = UserFactory.foundByToken(token);
            if (self!=null){
                requestContext.setSecurityContext(new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                         return self;
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        // 可以写入用户权限
                        return true;
                    }

                    @Override
                    public boolean isSecure() {
                        return false;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return null;
                    }
                });
                return;
            }
        }
        ResponseModel responseModel = ResponseModel.buildAccountError();
        Response response = Response.status(Response.Status.OK).entity(responseModel).build();
       requestContext.abortWith(response);
    }
}
