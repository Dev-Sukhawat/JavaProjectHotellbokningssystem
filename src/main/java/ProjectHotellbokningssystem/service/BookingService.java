package ProjectHotellbokningssystem.service;

import ProjectHotellbokningssystem.exception.GuestCapacityException;
import ProjectHotellbokningssystem.exception.RoomFullyBookedException;
import ProjectHotellbokningssystem.model.Booking;
import ProjectHotellbokningssystem.model.RoomType;
import ProjectHotellbokningssystem.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private HotelRepository repository;

    public Map<RoomType, Integer> getAvailableRooms(){
        return repository.getRoomInventory();
    }

    public List<Booking> getBookingsForUser(Authentication authentication){
        String loggedInUser = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        // Hämta precis alla bokningar från vårt in-memory repository
        List<Booking> allBookings = repository.findAllBookings();

        // SCENARIO 1: Om användaren är Admin, ge dem hela listan direkt
        if (isAdmin) {
            return allBookings;
        }

        // SCENARIO 2: Om det är en vanlig User, filtrera listan i minnet
        return allBookings.stream()
                .filter(booking -> booking.getBookingAccount().equals(loggedInUser))
                .collect(Collectors.toList());
    }

    public synchronized Booking createBooking(Booking booking) {
        RoomType type;
        try {
            type = RoomType.valueOf(booking.getRoomType().toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid room type! Must be Single, Double or Suite.");
        }

        // 1. Check capacity
        if (booking.getNumberOfGuests() > type.getMaxCapacity()) {
            throw new GuestCapacityException(booking.getRoomType() + " accommodates a maximum of " + type.getMaxCapacity() + " guests.");
        }

        if (repository.getAvailableRooms(type) <= 0) {
            throw new RoomFullyBookedException("Unfortunately, " + booking.getRoomType() + " is fully booked!");
        }

        booking.setTotalPrice(type.getPrice());

        repository.decrementRoomCount(type);

        return repository.save(booking);
    }

    public synchronized Booking updateBooking(Long bookingId, Booking updatedData, String loggedInUser) {
        Booking existingBooking = repository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking could not be found!"));

        if (!existingBooking.getBookingAccount().equals(loggedInUser)) {
            throw new RuntimeException("You don't have permission to update this booking.");
        }

        // Validate the NEW room type so it actually exists in your Enum
        RoomType newType;
        try {
            newType = RoomType.valueOf(updatedData.getRoomType().toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid room type! Must be Single, Double or Suite.");
        }

        if (updatedData.getNumberOfGuests() > newType.getMaxCapacity()) {
            throw new RuntimeException(updatedData.getRoomType() + " accommodates a maximum of " + newType.getMaxCapacity() + " guests.");
        }

        // MANAGE THE ROOM LAYER (If the room type has actually changed)
        RoomType oldType = RoomType.valueOf(existingBooking.getRoomType().toUpperCase().trim());
        if (oldType != newType) {
            if (repository.getAvailableRooms(newType) <= 0) {
                throw new RuntimeException("Unfortunately, " + updatedData.getRoomType() + " is fully booked!");
            }

            // Return the old room to the warehouse
            repository.getRoomInventory().put(oldType, repository.getRoomInventory().get(oldType) + 1);

            // Grab a room of the new type
            repository.decrementRoomCount(newType);
        }

        existingBooking.setGuestName(updatedData.getGuestName());
        existingBooking.setRoomType(updatedData.getRoomType());
        existingBooking.setNumberOfGuests(updatedData.getNumberOfGuests());
        existingBooking.setTotalPrice(newType.getPrice());

        return existingBooking;
    }

    public synchronized void deleteBooking(Long bookingId, Authentication authentication) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("No booking found with ID: " + bookingId));
        String loggedInUser = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!loggedInUser.equals(booking.getBookingAccount()) && !isAdmin) {
            throw new RuntimeException("You don't have permission to delete this booking.");
        }
        repository.deleteById(bookingId);
    }


}
