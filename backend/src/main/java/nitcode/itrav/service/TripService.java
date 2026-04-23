package nitcode.itrav.service;

import lombok.extern.slf4j.Slf4j;
import nitcode.itrav.dto.TripDTO;
import nitcode.itrav.exception.BusinessException;
import nitcode.itrav.exception.ResourceNotFoundException;
import nitcode.itrav.model.Trip;
import nitcode.itrav.model.User;
import nitcode.itrav.repository.TripRepository;
import nitcode.itrav.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    public TripDTO createTrip(String userEmail, TripDTO tripDTO) {
        log.info("Criando viagem para usuário: {}", userEmail);

        validateTripDates(tripDTO);
        User user = getUserByEmail(userEmail);

        Trip trip = Trip.builder()
                .user(user)
                .title(tripDTO.getTitle())
                .destination(tripDTO.getDestination())
                .startDate(tripDTO.getStartDate())
                .endDate(tripDTO.getEndDate())
                .budget(tripDTO.getBudget())
                .groupComposition(tripDTO.getGroupComposition())
                .objective(tripDTO.getObjective())
                .status(parseStatus(tripDTO.getStatus()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        trip = tripRepository.save(trip);
        log.info("Viagem criada com sucesso. ID: {}", trip.getId());
        return TripDTO.fromEntity(trip);
    }

    @Transactional(readOnly = true)
    public List<TripDTO> getUserTrips(String userEmail) {
        log.info("Listando viagens do usuário: {}", userEmail);

        User user = getUserByEmail(userEmail);
        return tripRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(TripDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TripDTO getTripById(String userEmail, Long tripId) {
        log.info("Obtendo viagem {} do usuário {}", tripId, userEmail);
        Trip trip = getTripOwnedByUser(userEmail, tripId);
        return TripDTO.fromEntity(trip);
    }

    public TripDTO updateTrip(String userEmail, Long tripId, TripDTO tripDTO) {
        log.info("Atualizando viagem {} do usuário {}", tripId, userEmail);

        validateTripDates(tripDTO);
        Trip trip = getTripOwnedByUser(userEmail, tripId);

        trip.setTitle(tripDTO.getTitle());
        trip.setDestination(tripDTO.getDestination());
        trip.setStartDate(tripDTO.getStartDate());
        trip.setEndDate(tripDTO.getEndDate());
        trip.setBudget(tripDTO.getBudget());
        trip.setGroupComposition(tripDTO.getGroupComposition());
        trip.setObjective(tripDTO.getObjective());

        if (tripDTO.getStatus() != null && !tripDTO.getStatus().isBlank()) {
            trip.setStatus(parseStatus(tripDTO.getStatus()));
        }

        trip.setUpdatedAt(LocalDateTime.now());
        Trip updated = tripRepository.save(trip);
        log.info("Viagem atualizada com sucesso. ID: {}", updated.getId());
        return TripDTO.fromEntity(updated);
    }

    public void deleteTrip(String userEmail, Long tripId) {
        log.info("Deletando viagem {} do usuário {}", tripId, userEmail);
        Trip trip = getTripOwnedByUser(userEmail, tripId);
        tripRepository.delete(trip);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", email));
    }

    private Trip getTripOwnedByUser(String userEmail, Long tripId) {
        User user = getUserByEmail(userEmail);
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Viagem", "id", tripId));

        if (!trip.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Você não tem permissão para acessar esta viagem");
        }

        return trip;
    }

    private void validateTripDates(TripDTO tripDTO) {
        if (tripDTO.getStartDate() != null && tripDTO.getEndDate() != null
                && tripDTO.getEndDate().isBefore(tripDTO.getStartDate())) {
            throw new BusinessException("A data final não pode ser anterior à data inicial");
        }
    }

    private Trip.TripStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return Trip.TripStatus.PLANNING;
        }

        try {
            return Trip.TripStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Status de viagem inválido: " + status);
        }
    }
}
