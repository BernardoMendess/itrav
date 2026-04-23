package nitcode.itrav.service;

import lombok.extern.slf4j.Slf4j;
import nitcode.itrav.dto.ActivityDTO;
import nitcode.itrav.exception.BusinessException;
import nitcode.itrav.exception.ResourceNotFoundException;
import nitcode.itrav.model.Activity;
import nitcode.itrav.model.Itinerary;
import nitcode.itrav.model.User;
import nitcode.itrav.repository.ActivityRepository;
import nitcode.itrav.repository.ItineraryRepository;
import nitcode.itrav.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private UserRepository userRepository;

    public ActivityDTO createActivity(String userEmail, Long itineraryId, ActivityDTO activityDTO) {
        log.info("Criando atividade no itinerário {} para usuário {}", itineraryId, userEmail);
        Itinerary itinerary = getItineraryOwnedByUser(userEmail, itineraryId);
        validateActivityPayload(activityDTO);

        Integer orderIndex = activityDTO.getOrderIndex();
        if (orderIndex == null) {
            List<Activity> existing = activityRepository.findByItineraryOrderByDayNumberAscOrderIndexAsc(itinerary);
            orderIndex = existing.size();
        }

        Activity activity = Activity.builder()
                .itinerary(itinerary)
                .title(activityDTO.getTitle())
                .dayNumber(activityDTO.getDayNumber() != null ? activityDTO.getDayNumber() : 1)
                .description(activityDTO.getDescription())
                .startTime(activityDTO.getStartTime())
                .endTime(activityDTO.getEndTime())
                .durationMinutes(activityDTO.getDurationMinutes())
                .latitude(activityDTO.getLatitude())
                .longitude(activityDTO.getLongitude())
                .cost(activityDTO.getCost())
                .currency(activityDTO.getCurrency())
                .provider(activityDTO.getProvider())
                .externalId(activityDTO.getExternalId())
                .orderIndex(orderIndex)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Activity saved = activityRepository.save(activity);
        touchItinerary(itinerary);
        return ActivityDTO.fromEntity(saved);
    }

    public ActivityDTO updateActivity(String userEmail, Long activityId, ActivityDTO activityDTO) {
        log.info("Atualizando atividade {} para usuário {}", activityId, userEmail);
        Activity activity = getActivityOwnedByUser(userEmail, activityId);
        validateActivityPayload(activityDTO);

        activity.setTitle(activityDTO.getTitle());
        activity.setDayNumber(activityDTO.getDayNumber() != null ? activityDTO.getDayNumber() : activity.getDayNumber());
        activity.setDescription(activityDTO.getDescription());
        activity.setStartTime(activityDTO.getStartTime());
        activity.setEndTime(activityDTO.getEndTime());
        activity.setDurationMinutes(activityDTO.getDurationMinutes());
        activity.setLatitude(activityDTO.getLatitude());
        activity.setLongitude(activityDTO.getLongitude());
        activity.setCost(activityDTO.getCost());
        activity.setCurrency(activityDTO.getCurrency());
        activity.setProvider(activityDTO.getProvider());
        activity.setExternalId(activityDTO.getExternalId());

        if (activityDTO.getOrderIndex() != null) {
            activity.setOrderIndex(activityDTO.getOrderIndex());
        }

        activity.setUpdatedAt(LocalDateTime.now());
        Activity updated = activityRepository.save(activity);
        touchItinerary(activity.getItinerary());
        return ActivityDTO.fromEntity(updated);
    }

    public void deleteActivity(String userEmail, Long activityId) {
        log.info("Removendo atividade {} para usuário {}", activityId, userEmail);
        Activity activity = getActivityOwnedByUser(userEmail, activityId);
        Itinerary itinerary = activity.getItinerary();
        activityRepository.delete(activity);
        touchItinerary(itinerary);
    }

    public ActivityDTO reorderActivity(String userEmail, Long activityId, Integer newOrderIndex) {
        log.info("Reordenando atividade {} para índice {} (usuário {})", activityId, newOrderIndex, userEmail);
        if (newOrderIndex == null || newOrderIndex < 0) {
            throw new BusinessException("orderIndex deve ser maior ou igual a 0");
        }

        Activity activity = getActivityOwnedByUser(userEmail, activityId);
        activity.setOrderIndex(newOrderIndex);
        activity.setUpdatedAt(LocalDateTime.now());

        Activity updated = activityRepository.save(activity);
        touchItinerary(activity.getItinerary());
        return ActivityDTO.fromEntity(updated);
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> listActivitiesByItinerary(String userEmail, Long itineraryId) {
        log.info("Listando atividades do itinerário {} para usuário {}", itineraryId, userEmail);
        Itinerary itinerary = getItineraryOwnedByUser(userEmail, itineraryId);
        return activityRepository.findByItineraryOrderByDayNumberAscOrderIndexAsc(itinerary)
                .stream()
                .map(ActivityDTO::fromEntity)
                .toList();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "email", email));
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

    private Activity getActivityOwnedByUser(String userEmail, Long activityId) {
        User user = getUserByEmail(userEmail);
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Atividade", "id", activityId));

        if (!activity.getItinerary().getTrip().getUser().getId().equals(user.getId())) {
            throw new BusinessException("Você não tem permissão para acessar esta atividade");
        }

        return activity;
    }

    private void validateActivityPayload(ActivityDTO activityDTO) {
        if (activityDTO.getStartTime() != null && activityDTO.getEndTime() != null
                && activityDTO.getEndTime().isBefore(activityDTO.getStartTime())) {
            throw new BusinessException("Horário final não pode ser anterior ao horário inicial");
        }
    }

    private void touchItinerary(Itinerary itinerary) {
        itinerary.setVersion(itinerary.getVersion() + 1);
        itinerary.setUpdatedAt(LocalDateTime.now());
        itineraryRepository.save(itinerary);
    }
}
