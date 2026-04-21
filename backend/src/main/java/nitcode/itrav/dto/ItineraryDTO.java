package nitcode.itrav.dto;

import nitcode.itrav.model.Itinerary;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryDTO {
    private Long id;
    private Long tripId;
    private String content;
    private Integer version;
    private Boolean generatedByAI;
    private List<ActivityDTO> activities;
    
    public static ItineraryDTO fromEntity(Itinerary itinerary) {
        List<ActivityDTO> activityDTOs = itinerary.getActivities() != null
                ? itinerary.getActivities().stream()
                    .map(ActivityDTO::fromEntity)
                    .collect(Collectors.toList())
                : null;
        
        return ItineraryDTO.builder()
                .id(itinerary.getId())
                .tripId(itinerary.getTrip().getId())
                .content(itinerary.getContent())
                .version(itinerary.getVersion())
                .generatedByAI(itinerary.getGeneratedByAI())
                .activities(activityDTOs)
                .build();
    }
}
