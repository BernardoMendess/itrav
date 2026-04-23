package nitcode.itrav.controller;

import lombok.extern.slf4j.Slf4j;
import nitcode.itrav.dto.ItineraryDTO;
import nitcode.itrav.service.ItineraryService;
import nitcode.itrav.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ItineraryController {

    @Autowired
    private ItineraryService itineraryService;

    @PostMapping("/trips/{tripId}/itineraries/generate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ItineraryDTO>> generateItinerary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long tripId) {
        String email = userDetails.getUsername();
        log.info("Gerando itinerário para trip {} do usuário {}", tripId, email);
        ItineraryDTO itinerary = itineraryService.generateBasicItinerary(email, tripId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(itinerary, "Itinerário gerado com sucesso"));
    }

    @GetMapping("/itineraries/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ItineraryDTO>> getItineraryById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        log.info("Buscando itinerário {} do usuário {}", id, email);
        ItineraryDTO itinerary = itineraryService.getItineraryById(email, id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(itinerary, "Itinerário obtido com sucesso"));
    }

    @GetMapping("/trips/{tripId}/itineraries/latest")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ItineraryDTO>> getLatestItineraryByTrip(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long tripId) {
        String email = userDetails.getUsername();
        log.info("Buscando último itinerário da trip {} do usuário {}", tripId, email);
        ItineraryDTO itinerary = itineraryService.getLatestItineraryByTripId(email, tripId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(itinerary, "Último itinerário obtido com sucesso"));
    }

    @PutMapping("/itineraries/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ItineraryDTO>> updateItinerary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody ItineraryDTO itineraryDTO) {
        String email = userDetails.getUsername();
        log.info("Atualizando itinerário {} do usuário {}", id, email);
        ItineraryDTO updated = itineraryService.updateItinerary(email, id, itineraryDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updated, "Itinerário atualizado com sucesso"));
    }
}
