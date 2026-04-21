package nitcode.itrav.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    @JsonIgnore
    private Trip trip;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingType type;
    
    @NotBlank
    @Column(nullable = false)
    private String provider; // Booking, Skyscanner, Google Places
    
    @Column(name = "external_id")
    private String externalId;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private String currency;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;
    
    @Column(name = "payment_id")
    private String paymentId; // Stripe or Mercado Pago payment ID
    
    @Column(columnDefinition = "jsonb")
    private String metadata; // Additional booking info as JSON
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum BookingType {
        HOTEL, FLIGHT, ACTIVITY, OTHER
    }
    
    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, FAILED
    }
}
