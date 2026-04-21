package nitcode.itrav.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    private String token;
    private String refreshToken;
    private String type;
    private Long expiresIn;
    private UserDTO user;
}
