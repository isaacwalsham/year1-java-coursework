package arcade;
import java.lang.*;

public abstract class ArcadeGame {

    private String id;
    private String gameTitle;
    private int priceInPence;
    private int minAge;

    public ArcadeGame(String id, String gameTitle, int priceInPence, int minAge) throws InvalidGameIdException {
        if (id == null || !id.matches("^[a-zA-Z0-9]{10}$")) {
            throw new InvalidGameIdException("Invalid Game ID: " + id);
        }
        this.id = id;
        this.gameTitle = gameTitle;
        this.priceInPence = priceInPence;
        this.minAge = minAge;
    }

    public String getId() {
        return id;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public int getPriceInPence() {
        return priceInPence;
    }

    public int getMinAge() {
        return minAge;
    }

    @Override
    public String toString() {
        return "Game ID: " + id + ", Title: " + gameTitle + ", Price: " + priceInPence + " pence";
    }

    public abstract int calculatePrice(boolean peak);
}