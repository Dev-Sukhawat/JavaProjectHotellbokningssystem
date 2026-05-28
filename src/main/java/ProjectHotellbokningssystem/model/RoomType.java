package ProjectHotellbokningssystem.model;

public enum RoomType {
    SINGLE(500, 1, 10),
    DOUBLE(1000, 2, 7),
    FAMILY(1500, 5, 2),
    SUITE(2000, 3, 3);


    private final int price;
    private final int maxCapacity;
    private final int totalRooms;

    RoomType(int price, int maxCapacity, int totalRooms){
        this.price = price;
        this.maxCapacity = maxCapacity;
        this.totalRooms = totalRooms;
    }

    public int getPrice() {
        return price;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getTotalRooms() {
        return totalRooms;
    }
}
