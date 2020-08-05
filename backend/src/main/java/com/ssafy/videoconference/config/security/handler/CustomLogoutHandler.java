package com.ssafy.videoconference.config.security.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.ssafy.videoconference.model.user.bean.UserDetail;

public class CustomLogoutHandler implements LogoutHandler{

	@Autowired
    RedisTemplate<String,Object> redisTemplate;

	
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		 
		UserDetail userDetail = (UserDetail) authentication.getPrincipal();
		String accessToken = userDetail.getId()+"access_Token";
		String refreshToken = userDetail.getId()+"refresh_Token";
		
		if (redisTemplate.opsForValue().get(accessToken) != null) {
             redisTemplate.delete(accessToken);
        }
		
		if (redisTemplate.opsForValue().get(refreshToken) != null) {
            redisTemplate.delete(refreshToken);
       }
	
		response.setStatus(HttpStatus.OK.value());
	}

}
