package ProjectHotellbokningssystem.controller;

import ProjectHotellbokningssystem.util.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody(required = false) Map<String, String> credentials, Authentication authentication) {
        try {
            String username;
            List<String> roles;

            // Option A: If the user already authenticated via Basic Auth (e.g., in Insomnia)
            if (authentication != null && authentication.isAuthenticated()) {
                username = authentication.getName();
                roles = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();
            }
            // Option B: If the user instead sent the credentials as a JSON body
            else if (credentials != null && credentials.containsKey("username")) {
                String jsonUsername = credentials.get("username");
                String jsonPassword = credentials.get("password");

                Authentication auth = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(jsonUsername, jsonPassword)
                );

                username = auth.getName();
                roles = auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();
            } else {
                // If neither Basic Auth nor a valid JSON body was provided
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Missing credentials"));
            }

            // Generate the token dynamically regardless of the login method used
            String token = jwtService.generateToken(username, roles);
            return ResponseEntity.ok(Map.of("token", token));

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Wrong user or password"));
        }
    }
}