package nitcode.itrav.controller;

import nitcode.itrav.dto.AuthRequestDTO;
import nitcode.itrav.dto.AuthResponseDTO;
import nitcode.itrav.service.AuthService;
import nitcode.itrav.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@Valid @RequestBody AuthRequestDTO request) {
        log.info("Registrando novo usuário");
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Usuário registrado com sucesso"));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody AuthRequestDTO request) {
        log.info("Login do usuário");
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response, "Login realizado com sucesso"));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refreshToken() {
        // TODO: Implementar refresh token
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.error("Refresh token não implementado ainda"));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout() {
        // Logout é stateless, apenas remove o token no frontend
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(null, "Logout realizado com sucesso"));
    }
}
