package nitcode.itrav.repository;

import nitcode.itrav.model.Booking;
import nitcode.itrav.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByTrip(Trip trip);
    List<Booking> findByTripOrderByCreatedAtDesc(Trip trip);
}
