package com.example.demo.filter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

@ConditionalOnProperty(prefix = "filter", name = "paramTrim", havingValue = "true", matchIfMissing = true)
@Configuration
public class TrimFilterConfiguration {
 
    /**
     * 过滤请求参数中的前后空格
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean trimFilter() {
        FilterRegistrationBean fitler = new FilterRegistrationBean<>();
        fitler.setFilter(new TrimFilter());
        fitler.addUrlPatterns("/*");
        fitler.setName("TrimFilter");
        fitler.setDispatcherTypes(DispatcherType.REQUEST);
 
        return fitler;
    }
}