package nitcode.itrav.service;

import lombok.extern.slf4j.Slf4j;
import nitcode.itrav.dto.AuthRequestDTO;
import nitcode.itrav.dto.AuthResponseDTO;
import nitcode.itrav.dto.UserDTO;
import nitcode.itrav.exception.BusinessException;
import nitcode.itrav.exception.ResourceNotFoundException;
import nitcode.itrav.model.Subscription;
import nitcode.itrav.model.User;
import nitcode.itrav.repository.SubscriptionRepository;
import nitcode.itrav.repository.UserRepository;
import nitcode.itrav.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    public AuthResponseDTO register(AuthRequestDTO request) {
        log.info("Registrando novo usuário com email: {}", request.getEmail());
        
        // Verificar se email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já está registrado");
        }
        
        String defaultName = request.getEmail().split("@")[0];
        String userName = request.getName() != null && !request.getName().isBlank()
                ? request.getName().trim()
                : defaultName;

        // Criar novo usuário
        User user = User.builder()
                .email(request.getEmail())
                .name(userName)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .subscriptionTier(User.SubscriptionTier.FREE)
                .build();
        
        user = userRepository.save(user);
        
        // Criar subscription padrão (FREE)
        Subscription subscription = Subscription.builder()
                .user(user)
                .tier(User.SubscriptionTier.FREE)
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .build();
        
        subscriptionRepository.save(subscription);
        
        // Gerar token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
        );
        
        String token = jwtTokenProvider.generateToken(authentication);
        
        log.info("Usuário registrado com sucesso: {}", request.getEmail());
        
        return AuthResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .user(UserDTO.fromEntity(user))
                .build();
    }
    
    public AuthResponseDTO login(AuthRequestDTO request) {
        log.info("Login do usuário: {}", request.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", request.getEmail()));
            
            String token = jwtTokenProvider.generateToken(authentication);
            
            log.info("Login bem-sucedido: {}", request.getEmail());
            
            return AuthResponseDTO.builder()
                    .token(token)
                    .type("Bearer")
                    .expiresIn(jwtTokenProvider.getExpirationTime())
                    .user(UserDTO.fromEntity(user))
                    .build();
                    
        } catch (Exception ex) {
            log.error("Falha no login: {}", ex.getMessage());
            throw new BusinessException("Email ou senha inválidos");
        }
    }
}
