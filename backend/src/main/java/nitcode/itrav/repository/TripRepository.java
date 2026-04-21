package nitcode.itrav.repository;

import nitcode.itrav.model.Trip;
import nitcode.itrav.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserOrderByCreatedAtDesc(User user);
    List<Trip> findByUser(User user);
}
