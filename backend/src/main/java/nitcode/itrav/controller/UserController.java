package nitcode.itrav.controller;

import nitcode.itrav.dto.UserDTO;
import nitcode.itrav.service.UserService;
import nitcode.itrav.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(@RequestParam String email) {
        log.info("Obtendo dados do usuário atual: {}", email);
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(user, "Usuário obtido com sucesso"));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        log.info("Obtendo usuário por ID: {}", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(user, "Usuário obtido com sucesso"));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable Long id,
            @RequestBody UserDTO userDTO) {
        log.info("Atualizando usuário com ID: {}", id);
        UserDTO updated = userService.updateUser(id, userDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updated, "Usuário atualizado com sucesso"));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long id) {
        log.info("Deletando usuário com ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(null, "Usuário deletado com sucesso"));
    }
}
