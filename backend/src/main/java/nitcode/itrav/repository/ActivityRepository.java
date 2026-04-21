package nitcode.itrav.repository;

import nitcode.itrav.model.Activity;
import nitcode.itrav.model.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByItineraryOrderByDayNumberAscOrderIndexAsc(Itinerary itinerary);
    List<Activity> findByItinerary(Itinerary itinerary);
}
