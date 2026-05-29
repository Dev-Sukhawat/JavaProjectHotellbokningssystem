package ProjectHotellbokningssystem.controller;

import ProjectHotellbokningssystem.model.Booking;
import ProjectHotellbokningssystem.model.RoomType;
import ProjectHotellbokningssystem.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BookingController {


    @Autowired
    private BookingService bookingService;

    @GetMapping("/rooms")
    public ResponseEntity<Map<RoomType, Integer>> getRooms(){
        return ResponseEntity.ok(bookingService.getAvailableRooms());
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings(Authentication authentication){
        List<Booking> bookings = bookingService.getBookingsForUser(authentication);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(@Valid @RequestBody Booking booking, Principal principal) {
        if (principal == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You need to be login!");
        }
        try {
            String loggedInUser = principal.getName();

            booking.setBookingAccount(loggedInUser);

            Booking newBooking = bookingService.createBooking(booking);

            return ResponseEntity.status(HttpStatus.CREATED).body(newBooking);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }


    @PutMapping("/bookings/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody Booking updatedBooking, Principal principal){
        String loggedInUser = principal.getName();

        try {
            Booking booking = bookingService.updateBooking(id, updatedBooking, loggedInUser);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e){
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));

        }
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id, Authentication authentication){
        try {
        bookingService.deleteBooking(id, authentication);
        return ResponseEntity.ok("Booking with ID: " + id + " was successfully deleted.");
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }


}
