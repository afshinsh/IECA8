package ir.ac.ut.ece.ie.Security;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        System.out.println(
                "Logging Request  {} : {}" + req.getMethod());
        chain.doFilter(servletRequest, servletResponse);
        System.out.println(
                "Logging Response :{}" +
                res.getContentType());
    }

    @Override
    public void destroy() {

    }

/*    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HttpServletRequest request = (HttpServletRequest) httpServletRequest;
        HttpServletResponse response = (HttpServletResponse) httpServletResponse;

        String url = request.getRequestURI();
        System.out.println("jwt filter url " + url);
        String method = request.getMethod();

        if(url.equals("/auth/login/") || url.equals("/auth/signup/") ||
                url.equals("/auth/forget/") || url.equals("/auth/changePassword/"))
            chain.doFilter(request, response);
        else {
            String token = request.getHeader("Authorization");
            if(token == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("You have not authorized yet!");
            }
            else {
                String email = JwtUtils.verifyJWT(token);
                if(email == null) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().println("The JWT token is invalidated!");
                }
                else {
                    //Student student = new StudentMapper().getStudentByEmail(email);
                    //request.setAttribute("student", student.getEmail());
                    chain.doFilter(request, response);
                }
            }
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }*/
}