package com.zjz.onlinetutoringmanagementsystem.config;

import com.zjz.onlinetutoringmanagementsystem.filter.JwtRequestFilter;
import com.zjz.onlinetutoringmanagementsystem.service.impl.MyUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private MyUserDetailsServiceImpl userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF
                .csrf().disable()
                //开启跨域请求
                .cors()
                .and()
                //不使用session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                //登录接口允许匿名访问
                .antMatchers("/user/login", "/register/**", "/public/**").permitAll()
                .antMatchers("/userInfo/**", "/user/**").hasAnyRole("student", "teacher", "parent","admin")
                .antMatchers("/activity").hasAnyRole("teacher", "parent")
                //教师角色接口
                .antMatchers("/teachers/**").hasRole("teacher")
                //家长角色接口
                .antMatchers("/parents/**").hasRole("parent")
                //学生角色接口
                .antMatchers("/students/**").hasRole("student")
                //管理员角色接口
                .antMatchers("/admin/**").hasRole("admin")
                // 允许所有 OPTIONS 请求
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                //除了上面的接口请求,全部都要进行认证
                .anyRequest().authenticated()
                //登出设置
                .and()
                .logout()
                .logoutUrl("/logout")  // 自定义注销请求路径
                .logoutSuccessUrl("/")  // 注销成功后的重定向地址
                .invalidateHttpSession(true)  // 注销时使 HTTP 会话无效
                .clearAuthentication(true)  // 清除认证信息
                .permitAll();

        // 添加JWT过滤器
        http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    // JWT过滤器配置（需要实现JwtRequestFilter类）
    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter();
    }

    //暴露AuthenticationManager bean
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                // 从数据库中获取
                .dataSource(dataSource)
                // 登录
                .usersByUsernameQuery("SELECT username, password, enabled FROM users WHERE username = ?")
                // 加密方式
                .passwordEncoder(passwordEncoder())
                // 返回用户名和角色
                .authoritiesByUsernameQuery("SELECT username, role FROM users WHERE username = ?");
    }
}
