package ProjectHotellbokningssystem.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class Booking {
    private Long id;

    private String bookingAccount;

    @NotBlank(message = "GuestName can not be blank")
    private String guestName;

    @NotBlank(message = "Room type must be specified")
    @Pattern(regexp = "^(SINGLE|DOUBLE|SUITE|FAMILY)$", message = "Room type must be SINGLE, DOUBLE, SUITE or FAMILY")
    private String roomType;

    @Min(value = 1, message = "A minimum of 1 guest is required to book")
    @JsonProperty("numberOfGuests")
    private Integer numberOfGuests;

    private Integer totalPrice;

    public Booking(){}

    public Booking(Long id, String bookingAccount, String guestName, String roomType, Integer numberOfGuests, Integer totalPrice) {
        this.id = id;
        this.bookingAccount = bookingAccount;
        this.guestName = guestName;
        this.roomType = roomType;
        this.numberOfGuests = numberOfGuests;
        this.totalPrice = totalPrice;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBookingAccount() {
        return bookingAccount;
    }
    public void setBookingAccount(String bookingAccount) {
        this.bookingAccount = bookingAccount;
    }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public Integer getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(Integer numberOfGuests) { this.numberOfGuests = numberOfGuests; }
    public Integer getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Integer totalPrice) { this.totalPrice = totalPrice; }
}
