/**
 * TokenInterceptor.java.java
 * @author FengMy
 * @since 2015年7月24日
 */
package plugin.portal.interceptors;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import plugin.portal.annotations.TokenCreate;
import plugin.portal.annotations.TokenValidate;
import plugin.portal.context.TokenContext;

/**  
 * 功能描述：
 * 
 * @author FengMy
 * @since 2015年7月24日
 */
public class TokenInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(handler != null && handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			Method method = handlerMethod.getMethod();
			if(method.getAnnotation(TokenValidate.class) != null){
				String token = request.getParameter(TokenContext.TOKEN_KEY);
				if(!StringUtils.isEmpty(token)){
					boolean existsToken = TokenContext.validateToken(token);
					if(!existsToken){
						//TODO 转到提示页面
						request.getRequestDispatcher("/WEB-INF/views/common/submitRepeat.jsp").forward(request, response);
						return false;
					}
				}
			}
		}
		return super.preHandle(request, response, handler);
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if(handler != null && handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			Method method = handlerMethod.getMethod();
			if(method.getAnnotation(TokenCreate.class) != null){
				String token = TokenContext.createToken();
				modelAndView.getModelMap().put(TokenContext.TOKEN_KEY, token);
			}
		}
		super.postHandle(request, response, handler, modelAndView);
	}
	
}
