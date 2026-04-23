package nitcode.itrav.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nitcode.itrav.dto.ActivityDTO;
import nitcode.itrav.service.ActivityService;
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
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping("/itineraries/{id}/activities")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ActivityDTO>> createActivity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ActivityDTO activityDTO) {
        String email = userDetails.getUsername();
        log.info("Criando atividade no itinerário {} para usuário {}", id, email);
        ActivityDTO created = activityService.createActivity(email, id, activityDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Atividade criada com sucesso"));
    }

    @PutMapping("/activities/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ActivityDTO>> updateActivity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ActivityDTO activityDTO) {
        String email = userDetails.getUsername();
        log.info("Atualizando atividade {} para usuário {}", id, email);
        ActivityDTO updated = activityService.updateActivity(email, id, activityDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updated, "Atividade atualizada com sucesso"));
    }

    @DeleteMapping("/activities/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<?>> deleteActivity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        log.info("Deletando atividade {} para usuário {}", id, email);
        activityService.deleteActivity(email, id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(null, "Atividade deletada com sucesso"));
    }

    @PatchMapping("/activities/{id}/reorder")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ActivityDTO>> reorderActivity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam Integer orderIndex) {
        String email = userDetails.getUsername();
        log.info("Reordenando atividade {} para índice {} (usuário {})", id, orderIndex, email);
        ActivityDTO updated = activityService.reorderActivity(email, id, orderIndex);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updated, "Atividade reordenada com sucesso"));
    }

    @GetMapping("/itineraries/{id}/activities")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> listActivitiesByItinerary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        String email = userDetails.getUsername();
        log.info("Listando atividades do itinerário {} para usuário {}", id, email);
        List<ActivityDTO> activities = activityService.listActivitiesByItinerary(email, id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(activities, "Atividades obtidas com sucesso"));
    }
}
