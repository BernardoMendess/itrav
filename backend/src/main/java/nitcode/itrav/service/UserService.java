package nitcode.itrav.service;

import lombok.extern.slf4j.Slf4j;
import nitcode.itrav.dto.UserDTO;
import nitcode.itrav.exception.ResourceNotFoundException;
import nitcode.itrav.model.User;
import nitcode.itrav.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public UserDTO getUserById(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        return UserDTO.fromEntity(user);
    }
    
    public UserDTO getUserByEmail(String email) {
        log.info("Buscando usuário por email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", email));
        return UserDTO.fromEntity(user);
    }
    
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("Atualizando usuário com ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        
        if (userDTO.getName() != null && !userDTO.getName().isEmpty()) {
            user.setName(userDTO.getName());
        }
        
        if (userDTO.getAvatarUrl() != null) {
            user.setAvatarUrl(userDTO.getAvatarUrl());
        }
        
        if (userDTO.getPreferences() != null) {
            user.setPreferences(userDTO.getPreferences());
        }
        
        user = userRepository.save(user);
        log.info("Usuário atualizado com sucesso: {}", id);
        
        return UserDTO.fromEntity(user);
    }
    
    public void deleteUser(Long id) {
        log.info("Deletando usuário com ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        
        userRepository.delete(user);
        log.info("Usuário deletado com sucesso: {}", id);
    }
}
