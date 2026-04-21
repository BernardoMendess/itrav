package nitcode.itrav.dto;

import nitcode.itrav.model.Booking;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {
    private Long id;
    private Long tripId;
    private String type;
    private String provider;
    private String externalId;
    private BigDecimal price;
    private String currency;
    private String status;
    private String paymentId;
    private String metadata;
    
    public static BookingDTO fromEntity(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .tripId(booking.getTrip().getId())
                .type(booking.getType().toString())
                .provider(booking.getProvider())
                .externalId(booking.getExternalId())
                .price(booking.getPrice())
                .currency(booking.getCurrency())
                .status(booking.getStatus().toString())
                .paymentId(booking.getPaymentId())
                .metadata(booking.getMetadata())
                .build();
    }
}
