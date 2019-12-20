package se.kth.id1212.rest.jwt.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import se.kth.id1212.rest.jwt.JwtUtil;
import se.kth.id1212.rest.security.MyUserDetailsService;

/**
 * A filter used in this application's filter chain to verify JSON Web Tokens in the requests.
 * 
 * @author Antonio
 *
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter{

	private final String AUTH_HEADER = "Authorization";
	private final String BEARER_START  = "Bearer ";


	@Autowired
	private MyUserDetailsService userDetailssService;

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader(AUTH_HEADER);
		String email = null;
		String jwt = null;
		if(authHeader != null && authHeader.startsWith(BEARER_START)) {
			jwt = authHeader.substring(BEARER_START.length());
			email = jwtUtil.extractEmail(jwt);
		}

		if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userDetailssService.loadUserByUsername(email);
			if(jwtUtil.validateJwt(jwt, userDetails)) {
				UsernamePasswordAuthenticationToken userPassAuthToken = new UsernamePasswordAuthenticationToken(userDetails
						, null, userDetails.getAuthorities());
				userPassAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(userPassAuthToken);
			}
		}

		filterChain.doFilter(request, response);
	}

}
