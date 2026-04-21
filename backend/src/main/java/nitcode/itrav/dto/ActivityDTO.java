package nitcode.itrav.dto;

import nitcode.itrav.model.Activity;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDTO {
    private Long id;
    private Long itineraryId;
    
    @NotBlank
    private String title;
    
    private Integer dayNumber;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private Double latitude;
    private Double longitude;
    private BigDecimal cost;
    private String currency;
    private String provider;
    private String externalId;
    private Integer orderIndex;
    
    public static ActivityDTO fromEntity(Activity activity) {
        return ActivityDTO.builder()
                .id(activity.getId())
                .itineraryId(activity.getItinerary().getId())
                .title(activity.getTitle())
                .dayNumber(activity.getDayNumber())
                .description(activity.getDescription())
                .startTime(activity.getStartTime())
                .endTime(activity.getEndTime())
                .durationMinutes(activity.getDurationMinutes())
                .latitude(activity.getLatitude())
                .longitude(activity.getLongitude())
                .cost(activity.getCost())
                .currency(activity.getCurrency())
                .provider(activity.getProvider())
                .externalId(activity.getExternalId())
                .orderIndex(activity.getOrderIndex())
                .build();
    }
}
