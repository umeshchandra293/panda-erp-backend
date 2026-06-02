package com.hst.materialmgmt.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hst.materialmgmt.service.auth.AuthService;
import com.hst.materialmgmt.service.auth.AuthService.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/material/mgmt/auth")
public class AuthController {

    @Autowired private AuthService authService;

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest req) {
        return authService.login(req)
            .map(r -> ResponseEntity.ok(r))
            .onErrorResume(e -> {
                // Log the REAL error so we can see it in Render logs
                System.err.println("=== LOGIN ERROR ===");
                System.err.println("Class: " + e.getClass().getName());
                System.err.println("Message: " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("Cause: " + e.getCause().getMessage());
                }
                System.err.println("==================");
                return Mono.just(
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .<LoginResponse>build());
            });
    }

    @PostMapping("/change-password")
    public Mono<ResponseEntity<Void>> changePassword(
            @RequestBody ChangePasswordRequest req) {
        return authService.changePassword(req.username(), req.oldPassword(), req.newPassword())
            .<ResponseEntity<Void>>thenReturn(ResponseEntity.ok().build())
            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()));
    }

    @GetMapping("/hash")
    public Mono<String> hash(@RequestParam String password) {
        return Mono.just(authService.hashPassword(password));
    }

    public record ChangePasswordRequest(String username, String oldPassword, String newPassword) {}
}