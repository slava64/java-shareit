package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // ALL
    List<Booking> findByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    // PAST
    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    // CURRENT
    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Long userId, LocalDateTime start, LocalDateTime end);

    // FUTURE
    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime start);

    // WAITING AND REJECTED
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    // ALL FOR OWNER
    @Query(value =  " SELECT b.* FROM bookings AS b " +
            "LEFT JOIN items AS i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllByOwner(Long userId, Pageable pageable);

    // PAST FOR OWNER
    @Query(value =  " SELECT b.* FROM bookings AS b " +
            "LEFT JOIN items AS i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.end_date < NOW() " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllByOwnerInPast(Long userId);

    // CURRENT FOR OWNER
    @Query(value =  " SELECT b.* FROM bookings AS b " +
            "LEFT JOIN items AS i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.start_date < NOW() " +
            "AND b.end_date > NOW()" +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllByOwnerInCurrent(Long userId);

    // FUTURE FOR OWNER
    @Query(value =  " SELECT b.* FROM bookings AS b " +
            "LEFT JOIN items AS i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.start_date > NOW() " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllByOwnerInFuture(Long userId);

    // Find all bookings for owner where status waiting or rejected
    @Query(value =  " SELECT b.* FROM bookings AS b " +
            "LEFT JOIN items AS i ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 AND b.status = ?2 " +
            "ORDER BY b.start_date DESC", nativeQuery = true)
    List<Booking> findAllByOwnerByStatus(Long userId, String status);

    // Find last booking
    Optional<Booking> findFirstByItemIdAndEndIsBeforeOrderByStartDesc(Long itemId, LocalDateTime start);

    // Find next booking
    Optional<Booking> findFirstByItemIdAndStartIsAfterOrderByStart(Long itemId, LocalDateTime start);

    // Count bookings where end date is before
    Long countByBookerIdAndItemIdAndStatusAndEndIsBefore(
            Long userId,
            Long itemId,
            BookingStatus status,
            LocalDateTime end
    );
}
