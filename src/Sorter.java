import javax.swing.*;
import java.util.List;

/**
 * Created by doozzik on 21.07.16.
 */
public class Sorter {

    private JTextField lastLogin;
    private JTextField allTime;
    private JButton buttonSort;
    private JTextField immunityDays;
    private JTextArea nicks;
    private JTextArea regions;
    private List<Player> players;

    public Sorter(JTextField lastLogin, JTextField allTime, JButton buttonSort, JTextField immunityDays, JTextArea nicks, JTextArea regions, List<Player> players){
        this.lastLogin = lastLogin;
        this.allTime = allTime;
        this.buttonSort = buttonSort;
        this.immunityDays = immunityDays;
        this.nicks = nicks;
        this.regions = regions;
        this.players = players;
    }

    public void Sort(){
        int lastSeenTimeInDays = Integer.parseInt(lastLogin.getText());
        int playedTimeInDays = Integer.parseInt(allTime.getText());
        int immunityDays = Integer.parseInt(this.immunityDays.getText());

        int i = 0;
        int k = 0;

        String text = "";
        String nick = "";
        for (Player p : players){
            if (p.lastSeenTimeInDays <= immunityDays){
                nick += p.nick + "\n";
                i++;
                for (String region : p.regions){
                    text += "- " + region + "\n";
                    k++;
                }
            }else{
                if (p.lastSeenTimeInDays <= lastSeenTimeInDays){
                    if (p.playedTimeInDays >= playedTimeInDays){
                        nick += p.nick + "\n";
                        i++;
                        for (String region : p.regions){
                            text += "- " + region + "\n";
                            k++;
                        }
                    }
                }
            }
        }

        text = text.substring(0, text.length() - 1);
        nick = nick.substring(0, nick.length() - 1);

        nicks.setText("Игроков: " + i + "\n" + nick);
        regions.setText("Приватов: " + k + "\n" + text);
    }

    public void show(boolean action){
        lastLogin.setEditable(action);
        allTime.setEditable(action);
        buttonSort.setEnabled(action);
        immunityDays.setEditable(action);
        nicks.setEditable(action);
        regions.setEditable(action);
    }
}
