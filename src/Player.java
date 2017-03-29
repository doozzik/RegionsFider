import java.util.ArrayList;
import java.util.List;

/**
 * Created by doozzik on 20.07.16.
 */
public class Player {
    public String nick;
    public String uuid;
    public int playedTimeInDays;
    public int lastSeenTimeInDays;
    public List<String> regions;

    public Player(){
        nick = "";
        uuid = "";
        playedTimeInDays = 0;
        lastSeenTimeInDays = 0;
        regions = new ArrayList<>();
    }
}
