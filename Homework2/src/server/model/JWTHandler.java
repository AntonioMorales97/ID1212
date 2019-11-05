package server.model;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

/**
 * This class creates, encodes, decodes, and validates JSON Web Tokens.
 * 
 * @author Antonio
 *
 */
public class JWTHandler {
	private final Key SIGNED_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	private final SignatureAlgorithm SIGN_ALGORITHM = SignatureAlgorithm.HS256;
	private static final long TTL_MILLIS = 600000; //TEN MIN
	
	/**
	 * Generates a simple JSON Web Token
	 * @param username the username to put in the JWT for information
	 * @return the signed and encoded JWT
	 */
	public String generateJWT(String username) {
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
					
		JwtBuilder builder = Jwts.builder()
				.setId(username)
				.setIssuedAt(now)
				.signWith(SIGNED_KEY, SIGN_ALGORITHM);
		
	    long expMillis = nowMillis + JWTHandler.TTL_MILLIS;
	    Date exp = new Date(expMillis);
	    builder.setExpiration(exp);
	    
	    return builder.compact();
	}
	
	/**
	 * Decodes and checks if the encoded JSON Web Token is valid.
	 * @param encodedJwt The encoded JWT
	 * @return true if valid; false otherwise
	 */
	public boolean isValid(String encodedJwt) {
		try {
			   Jws<Claims> jws = decodeJwt(encodedJwt); 
			   return isExpDateValid(jws.getBody().getExpiration());
		   } catch(JwtException exc) {
			   exc.printStackTrace();
			   return false;
		   }	
	}
	
	
	//This will throw an exception if the jwt is invalid
	private Jws<Claims> decodeJwt(String jwt) {
	    Jws<Claims> jws;
		jws = Jwts.parser()
	            .setSigningKey(SIGNED_KEY)
	            .parseClaimsJws(jwt);
	    return jws;
	}
	
	private static boolean isExpDateValid(Date exp) {
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		if(now.before(exp)) {
			return true;
		} else {
			return false;
		}
	}
	
}
