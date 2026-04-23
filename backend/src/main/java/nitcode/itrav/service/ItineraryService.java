package nitcode.itrav.service;

import lombok.extern.slf4j.Slf4j;
import nitcode.itrav.dto.ItineraryDTO;
import nitcode.itrav.exception.BusinessException;
import nitcode.itrav.exception.ResourceNotFoundException;
import nitcode.itrav.model.Itinerary;
import nitcode.itrav.model.Trip;
import nitcode.itrav.model.User;
import nitcode.itrav.repository.ItineraryRepository;
import nitcode.itrav.repository.TripRepository;
import nitcode.itrav.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class ItineraryService {

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    public ItineraryDTO generateBasicItinerary(String userEmail, Long tripId) {
        log.info("Gerando itinerário básico para trip {} do usuário {}", tripId, userEmail);
        Trip trip = getTripOwnedByUser(userEmail, tripId);

        int nextVersion = itineraryRepository.findFirstByTripOrderByVersionDesc(trip)
                .map(itinerary -> itinerary.getVersion() + 1)
                .orElse(1);

        String defaultContent = String.format(
                "{\"destination\":\"%s\",\"message\":\"Itinerário inicial gerado\",\"days\":[]}",
                trip.getDestination()
        );

        Itinerary itinerary = Itinerary.builder()
                .trip(trip)
                .content(defaultContent)
                .version(nextVersion)
                .generatedByAI(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        itinerary = itineraryRepository.save(itinerary);
        return ItineraryDTO.fromEntity(itinerary);
    }

    @Transactional(readOnly = true)
    public ItineraryDTO getItineraryById(String userEmail, Long itineraryId) {
        log.info("Obtendo itinerário {} do usuário {}", itineraryId, userEmail);
        Itinerary itinerary = getItineraryOwnedByUser(userEmail, itineraryId);
        return ItineraryDTO.fromEntity(itinerary);
    }

    public ItineraryDTO updateItinerary(String userEmail, Long itineraryId, ItineraryDTO itineraryDTO) {
        log.info("Atualizando itinerário {} do usuário {}", itineraryId, userEmail);
        Itinerary itinerary = getItineraryOwnedByUser(userEmail, itineraryId);

        if (itineraryDTO.getContent() == null || itineraryDTO.getContent().isBlank()) {
            throw new BusinessException("Conteúdo do itinerário é obrigatório");
        }

        itinerary.setContent(itineraryDTO.getContent());
        itinerary.setVersion(itinerary.getVersion() + 1);
        itinerary.setGeneratedByAI(Boolean.TRUE.equals(itineraryDTO.getGeneratedByAI()));
        itinerary.setUpdatedAt(LocalDateTime.now());

        Itinerary updated = itineraryRepository.save(itinerary);
        return ItineraryDTO.fromEntity(updated);
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

    private Itinerary getItineraryOwnedByUser(String userEmail, Long itineraryId) {
        User user = getUserByEmail(userEmail);
        Itinerary itinerary = itineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerário", "id", itineraryId));

        if (!itinerary.getTrip().getUser().getId().equals(user.getId())) {
            throw new BusinessException("Você não tem permissão para acessar este itinerário");
        }

        return itinerary;
    }
}
