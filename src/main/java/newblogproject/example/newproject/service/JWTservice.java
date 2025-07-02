package newblogproject.example.newproject.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTservice {

private String secretkey="bf19fbed1d9d8678478e185aee3c91e20f2aef714a7b9fa47955e72c4d64964d";
//
//    public JWTservice() {
//        try {
//            KeyGenerator KG = KeyGenerator.getInstance("HmacSHA256");
//            KG.init(256);
//            SecretKey sk = KG.generateKey();
////secretkey = Base64.getEncoder().encodeToString(sk.getEncoded());
//            secretkey = Encoders.BASE64.encode(sk.getEncoded());
//            System.out.println(secretkey);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//
//    }

    public String generateToken(String username) {
        Map<String,Object> claims=new HashMap<>();
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration((new Date(System.currentTimeMillis()+1000*60*300)))
                .and()
                .signWith(getKeyy())
                .compact();

    }


    private SecretKey getKeyy()
    {
//    byte[] skey= Decoders.BASE64.decode(secretkey);
        byte[] skey= Base64.getDecoder().decode(secretkey);
        return Keys.hmacShaKeyFor(skey);
    }


    public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKeyy())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }



}
