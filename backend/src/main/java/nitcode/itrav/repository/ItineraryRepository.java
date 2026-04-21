package nitcode.itrav.repository;

import nitcode.itrav.model.Itinerary;
import nitcode.itrav.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    List<Itinerary> findByTripOrderByCreatedAtDesc(Trip trip);
    Optional<Itinerary> findFirstByTripOrderByVersionDesc(Trip trip);
}
