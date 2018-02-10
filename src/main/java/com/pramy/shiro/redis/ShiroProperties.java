package com.pramy.shiro.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * IntelliJ IDEA 17
 * Created by Pramy on 2018/2/9.
 */
@ConfigurationProperties(prefix = "shiro")
public class ShiroProperties {

    private Map<String,String> filterChain = new LinkedHashMap<>();

    private String loginUrl="/login.jsp";

    private String successUrl="/";

    protected String unauthorizedUrl = null;

    public Map<String,String> getFilterChain() {
        return filterChain;
    }

    public void setFilterChain(Map<String,String> filterChain) {
        this.filterChain = filterChain;
    }
}
