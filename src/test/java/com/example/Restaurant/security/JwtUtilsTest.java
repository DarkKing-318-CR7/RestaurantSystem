package com.example.Restaurant.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
    }

    @Test
    void testGenerateAndParseToken() {
        String token = jwtUtils.generateToken("testuser", 5L, "STAFF");
        assertNotNull(token);
        assertFalse(token.isEmpty());

        Long branchId = jwtUtils.getBranchIdFromToken(token);
        assertEquals(5L, branchId);

        String role = jwtUtils.getRoleFromToken(token);
        assertEquals("STAFF", role);
    }

    @Test
    void testAdminToken() {
        String token = jwtUtils.generateToken("adminuser", 0L, "ADMIN");
        assertNotNull(token);

        Long branchId = jwtUtils.getBranchIdFromToken(token);
        assertEquals(0L, branchId);

        String role = jwtUtils.getRoleFromToken(token);
        assertEquals("ADMIN", role);
    }
}
