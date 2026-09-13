package com.example.Restaurant.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    // Khóa bí mật (Chuỗi Hex 256-bit). Thực tế sẽ giấu trong application.yml
    private static final String SECRET_KEY = "357638792F423F4528482B4D6251655468576D5A7134743777217A25432A462D";
    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 giờ

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Tạo Token có nhúng kèm branchId
    public String generateToken(String username, Long branchId, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("branchId", branchId)
                .claim("role", role) // Nhét thêm role vào payload của JWT
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Thêm hàm mới: Bóc tách Role từ Token
    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

    // Bóc tách branchId từ Token gửi lên
    public Long getBranchIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        // Lấy claim "branchId" ép kiểu về Long
        return claims.get("branchId", Long.class);
    }

    // Bóc tách username từ Token gửi lên
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}