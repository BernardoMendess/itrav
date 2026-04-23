package nitcode.itrav.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nitcode.itrav.dto.TripDTO;
import nitcode.itrav.service.TripService;
import nitcode.itrav.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TripDTO>> createTrip(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TripDTO tripDTO) {
        String email = userDetails.getUsername();
        log.info("Criando viagem para usuário autenticado: {}", email);
        TripDTO created = tripService.createTrip(email, tripDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Viagem criada com sucesso"));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<TripDTO>>> getUserTrips(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        log.info("Listando viagens do usuário autenticado: {}", email);
        List<TripDTO> trips = tripService.getUserTrips(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(trips, "Viagens obtidas com sucesso"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TripDTO>> getTripById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        log.info("Obtendo viagem {} do usuário autenticado: {}", id, email);
        TripDTO trip = tripService.getTripById(email, id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(trip, "Viagem obtida com sucesso"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TripDTO>> updateTrip(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TripDTO tripDTO) {
        String email = userDetails.getUsername();
        log.info("Atualizando viagem {} do usuário autenticado: {}", id, email);
        TripDTO updated = tripService.updateTrip(email, id, tripDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updated, "Viagem atualizada com sucesso"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<?>> deleteTrip(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        log.info("Deletando viagem {} do usuário autenticado: {}", id, email);
        tripService.deleteTrip(email, id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(null, "Viagem deletada com sucesso"));
    }
}
