package nitcode.itrav.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "itinerary_id", nullable = false)
    @JsonIgnore
    private Itinerary itinerary;
    
    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "text")
    private String description;
    
    @Column(name = "start_time")
    private LocalTime startTime;
    
    @Column(name = "end_time")
    private LocalTime endTime;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal cost;
    
    @Column(name = "currency")
    private String currency; // USD, BRL, EUR, etc
    
    @Column(name = "provider")
    private String provider; // Booking, Skyscanner, Google Places, Manual
    
    @Column(name = "external_id")
    private String externalId;
    
    @Column(name = "order_index", nullable = false)
    @Builder.Default
    private Integer orderIndex = 0;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
