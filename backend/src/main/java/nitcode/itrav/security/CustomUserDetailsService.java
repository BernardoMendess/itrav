package nitcode.itrav.security;

import lombok.extern.slf4j.Slf4j;
import nitcode.itrav.model.User;
import nitcode.itrav.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                getAuthorities(user)
        );
    }
    
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com id: " + id));
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                getAuthorities(user)
        );
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        // Adicionar role baseado em subscription tier
        if (user.getSubscriptionTier() == User.SubscriptionTier.PREMIUM) {
            authorities.add(new SimpleGrantedAuthority("ROLE_PREMIUM_USER"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_FREE_USER"));
        }
        
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        return authorities;
    }
}
