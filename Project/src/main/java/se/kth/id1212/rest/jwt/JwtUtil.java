package se.kth.id1212.rest.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * A service to support JSON Web Tokens related functionalities in applications.
 * 
 * @author Antonio
 *
 */
@Service
public class JwtUtil {
	private final String SECRET = "not_secret"; //store somewhere else in production
	private final int EXPIRATION_TIME = 1000*60*60*10;

	/**
	 * Extracts the email from the given token.
	 * 
	 * @param token The JWT.
	 * @return the extracted email.
	 */
	public String extractEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Extracts the expiration date from the given token.
	 * 
	 * @param token The JWT.
	 * @return the extracted expiration date.
	 */
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Extracts whatever specified from the token.
	 * 
	 * @param <T> Whatever the type to be extracted.
	 * @param token The token.
	 * @param claimsResolver The functionality, i.e what to be extracted from the JWT claims.
	 * @return the extracted request.
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Validates that the given JWT is equal to the user details given; and that
	 * the JWT has not expired.
	 * 
	 * @param token The token.
	 * @param userDetails The <code>UserDetails</code>.
	 * @return <code>true</code> if valid; <code>false</code> otherwise.
	 */
	public Boolean validateJwt(String token, UserDetails userDetails) {
		String email = extractEmail(token);
		return (email.equals(userDetails.getUsername()) && !isJwtExpired(token));
	}

	/**
	 * Generates a token for the given <code>UserDetails</code>.
	 * 
	 * @param userDetails The <code>UserDetails</code> to create a token to.
	 * @return the generated token.
	 */
	public String generateJwt(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return createJwtToken(claims, userDetails.getUsername());
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
	}

	private Boolean isJwtExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private String createJwtToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS256, SECRET).compact();
	}
}
