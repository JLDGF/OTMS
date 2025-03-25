package com.zjz.onlinetutoringmanagementsystem.filter;

import com.alibaba.fastjson.JSONObject;
import com.zjz.pojo.Result;
import com.zjz.onlinetutoringmanagementsystem.service.impl.MyUserDetailsServiceImpl;
import com.zjz.utils.JwtUtils;
import com.zjz.utils.SecurityUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("JWT Filter");
        String uri = request.getRequestURI();
        log.info("uri:{}",uri);

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String token = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        //没有token时
        if (!StringUtils.hasText(token)) {
            // 允许登录和注册接口绕过认证过滤
            if (uri.contains("register") || uri.contains("login") || uri.contains("public")) {               filterChain.doFilter(request, response);
                return;
            }

            // 返回未登录信息
            Result error = Result.error(401,"NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);

            // 设置响应状态码为401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(notLogin);
            return;
        }

        try {
            // 解析JWT，只解析一次，避免重复调用
            Claims claims = JwtUtils.parseJWT(token);

            log.info("JWT Claims: {}", claims);
            System.out.println(claims.get("username"));
            // 从JWT中提取用户名
            username = claims.get("username",String.class);

        } catch (Exception e) {
            log.error("解析JWT失败: {}", e.getMessage());

            // 返回未登录信息
            Result error = Result.error(401,"NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);

            // 设置响应状态码为401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(notLogin);

            // 结束请求，避免继续执行过滤器链
            return;
        }


        // 如果用户名不为null且没有认证，进行认证
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // 构建认证信息
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置认证信息到SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }catch (Exception e){
                log.error("认证失败: {}", e.getMessage());
                // 返回未登录信息
                Result error = Result.error(401,"LOGIN_ERROR");
                String notLogin = JSONObject.toJSONString(error);

                // 设置响应状态码为401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(notLogin);

                // 结束请求，避免继续执行过滤器链
                return;
            }

            log.info("用户 '{}' 已认证,权限：{}", username, SecurityUtils.getCurrentUserRole());
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}
