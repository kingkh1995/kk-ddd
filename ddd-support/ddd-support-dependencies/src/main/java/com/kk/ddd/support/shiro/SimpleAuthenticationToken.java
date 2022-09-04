package com.kk.ddd.support.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 *
 * <br/>
 *
 * @author KaiKoo
 */
public record SimpleAuthenticationToken(String principal, String credential) implements AuthenticationToken {

    @Override
    public String getPrincipal() {
        return principal();
    }

    @Override
    public String getCredentials() {
        return credential();
    }
}
