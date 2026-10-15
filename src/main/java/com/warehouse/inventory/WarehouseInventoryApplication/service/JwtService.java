package com.warehouse.inventory.WarehouseInventoryApplication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

	private String secretKey;

	public JwtService() {

		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
			SecretKey sk = keyGen.generateKey();
			secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());// bytes to string
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}

	}

	public String generateJwtToken(String username) {

		Map<String, Object> claims = new HashMap<String, Object>();

		return Jwts.builder().claims().add(claims).subject(username).issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 30 * 60 * 60)).and().signWith(getKey()).compact();// ←
																													// serializing
																													// the
																													// Java
																													// Object
		// add the claims AND sign in
		// this token is valid for 30 minutes from system current time
		// milliseconds × seconds × minutes × hours (N)
		// hours × minutes × seconds × milliseconds (Y)
//		return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30";

		/*
		 * compact() working {"alg":"HS256"} {"sub":"Ranni","iat":...,"exp":...}
		 * base64(header) + "." + base64(payload) {header}.{payload}.{signature} return
		 * a final String
		 */
	}

	private SecretKey getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public String extractUsername(String token) {

		return extractClaim(token, Claims::getSubject);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {

		return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
//	1)	.parser() --> returns a JwtParserBuilder;you're just configuring how parsing should work.
//	2)	.verifyWith(getKey()) — tells the builder: "when you parse a signed JWT, use this SecretKey to check the signature." getKey() 
//		rebuilds the SecretKey object from your Base64 secretKey string (via Keys.hmacShaKeyFor(keyBytes)). 
//		This doesn't verify anything yet either — it's still just configuration.
//	3)	.build() — finalizes the configuration and produces an actual JwtParser object, ready to use.
//	4)	.parseSignedClaims(token) --> Splits the token string into parts: header.payload.signature
		// ← deserializing Java Object
//	5)	.getPayload() --> extracts just the Claims object (the payload): subject, issuedAt, expiration

	}

	private boolean isTokenExpired(String token) {

		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {

		return extractClaim(token, Claims::getExpiration);
	}

}

/*
 * 🔑 SecretKey vs 🎫 Token
 * 
 * What is it? A secret cryptographic key A credential given to the client
 * Created by Your server/application Your server Given to client? ❌ Never ✅ Yes
 * Used for Signing/verifying JWT Proving authentication Should be public? ❌
 * Absolutely not It can be sent with requests Example my-super-secret-key...
 * eyJhbGciOiJIUzI1NiJ9... Stored where? Server/environment variable Client side
 * / cookie depending on design Changes frequently? Usually no Yes, tokens
 * expire/rotate
 */
