package com.example.book.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private Long jwtExpiration;

    //get username from request token from claim
    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T > claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String,Object> extraClaims,UserDetails userDetails){
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public long getExpirationTime(){
        return jwtExpiration;
    }

    public String buildToken(Map<String,Object> extraClaims, UserDetails userDetails, long jwtExpiration){
        JwtBuilder jwtBuilder = Jwts
                .builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration));

        if(extraClaims != null){
            extraClaims.forEach(jwtBuilder::claim);
        }

        return jwtBuilder.signWith(getSignInKey()).compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Checks to see if the token is expired and that our current users username would match the token subject.
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }


}
