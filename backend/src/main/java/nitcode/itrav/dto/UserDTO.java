package nitcode.itrav.dto;

import nitcode.itrav.model.User;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    
    @Email
    private String email;
    
    @NotBlank
    private String name;
    
    private String avatarUrl;
    private String subscriptionTier;
    private String preferences;
    
    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .subscriptionTier(user.getSubscriptionTier().toString())
                .preferences(user.getPreferences())
                .build();
    }
}
