package org.example.expert.aop;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.expert.config.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Component
@Aspect
public class AspectAdmin {

    private final JwtUtil jwtUtil;

    public AspectAdmin(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Pointcut("execution(* org.example.expert.domain.comment.controller.CommentAdminController.*(..))")
    private void deleteComment() {

    }

    @Pointcut("execution(* org.example.expert.domain.user.controller.UserAdminController.*(..))")
    private void changeUserRole() {

    }


    @Before("changeUserRole() || deleteComment()")
    public void logAdmin() {
        log.info("Admin Access Log");

        // 현재 요청에 대한 HttpServletRequest 가져옴
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // JWT 토큰 가져오기
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                log.warn("HEADER 에서 유효한 jwt 토큰을 찾을 수 없습니다.");
            }
            token = token.substring(7);

            try {

                Long userId = jwtUtil.getUserIdFromToken(token);
                LocalDateTime requestTime = LocalDateTime.now();
                String requestUrl = request.getRequestURL().toString();

                log.info("UserId = {}, Request Time = {}, URL = {} ", userId, requestTime, requestUrl);
            } catch (Exception e) {
                log.warn("userId 토큰을 찾을 수 없습니다. : {} ", e.getMessage());
            }
        }
    }
}
