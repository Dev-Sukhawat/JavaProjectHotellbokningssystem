package ProjectHotellbokningssystem.repository;

import ProjectHotellbokningssystem.model.Booking;
import ProjectHotellbokningssystem.model.RoomType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class HotelRepository {
    private final List<Booking> bookingList = new ArrayList<>();
    private final Map<RoomType, Integer> roomInventory = new ConcurrentHashMap<>();
    private  Long nextId = 1L;

    public HotelRepository() {
        // Add the hotel rooms to memory directly at start (10 Single rooms, 7 Double rooms, 3 Suites)
        for (RoomType type : RoomType.values()){
            roomInventory.put(type, type.getTotalRooms());
        }

        Booking adminTestBooking = new Booking();
        adminTestBooking.setId(nextId++);
        adminTestBooking.setBookingAccount("admin");
        adminTestBooking.setGuestName("MrAdmin");
        adminTestBooking.setRoomType("SINGLE");
        adminTestBooking.setNumberOfGuests(1);
        adminTestBooking.setTotalPrice(RoomType.SINGLE.getPrice());
        bookingList.add(adminTestBooking);
        roomInventory.put(RoomType.SINGLE, roomInventory.get(RoomType.SINGLE) - 1);

        // --- TESTBOKNING 2: KALLE ---
        Booking b2 = new Booking();
        b2.setId(nextId++); // ID: 2
        b2.setBookingAccount("kalle");
        b2.setGuestName("Kalle Andersson");
        b2.setRoomType("SINGLE");
        b2.setNumberOfGuests(1);
        b2.setTotalPrice(RoomType.SINGLE.getPrice());
        bookingList.add(b2);
        roomInventory.put(RoomType.SINGLE, roomInventory.get(RoomType.SINGLE) - 1);

        // --- TESTBOKNING 3: SOVEA ---
        Booking b3 = new Booking();
        b3.setId(nextId++); // ID: 3
        b3.setBookingAccount("sovea");
        b3.setGuestName("Sovea Modiferad");
        b3.setRoomType("DOUBLE");
        b3.setNumberOfGuests(2);
        b3.setTotalPrice(RoomType.DOUBLE.getPrice());
        bookingList.add(b3);
        roomInventory.put(RoomType.DOUBLE, roomInventory.get(RoomType.DOUBLE) - 1);
    }

    public List<Booking> findAllBookings(){
        return new ArrayList<>(bookingList);
    }

    public Map<RoomType, Integer> getRoomInventory(){
        return roomInventory;
    }

    public synchronized Booking save(Booking booking){
        booking.setId(nextId++);
        bookingList.add(booking);
        return booking;
    }

    public synchronized Optional<Booking> findById(Long id){
        return bookingList.stream()
                .filter(booking -> booking.getId().equals(id))
                .findFirst();
    }

    public synchronized boolean deleteById(Long id){
        Optional<Booking> bookingOpt = bookingList.stream().filter(b -> b.getId().equals(id)).findFirst();
        if (bookingOpt.isPresent()){
            Booking booking = bookingOpt.get();
            bookingList.remove(booking);
            //Return a room to the hotel when a reservation is deleted!
            RoomType type = RoomType.valueOf(booking.getRoomType().toUpperCase());
            roomInventory.put(type, roomInventory.get(type)+1);
            return true;
        }
        return false;
    }

    public int getAvailableRooms(RoomType type) {
        return roomInventory.getOrDefault(type, 0);
    }

    public void decrementRoomCount(RoomType type) {
        roomInventory.put(type, roomInventory.get(type) - 1);
    }
}
