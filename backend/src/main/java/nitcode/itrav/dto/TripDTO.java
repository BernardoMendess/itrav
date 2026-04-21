package nitcode.itrav.dto;

import nitcode.itrav.model.Trip;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDTO {
    private Long id;
    
    @NotBlank
    private String title;
    
    @NotBlank
    private String destination;
    
    @NotNull
    private LocalDate startDate;
    
    @NotNull
    private LocalDate endDate;
    
    private BigDecimal budget;
    private String groupComposition;
    private String objective;
    private String status;
    
    public static TripDTO fromEntity(Trip trip) {
        return TripDTO.builder()
                .id(trip.getId())
                .title(trip.getTitle())
                .destination(trip.getDestination())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .budget(trip.getBudget())
                .groupComposition(trip.getGroupComposition())
                .objective(trip.getObjective())
                .status(trip.getStatus().toString())
                .build();
    }
}
