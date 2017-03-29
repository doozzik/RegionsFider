import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by doozzik on 20.07.16.
 */
public class Parser implements Runnable {
    private String dirEssentials;
    private String dirOnTime;
    private String dirWorld;
    //private String dirWorldNether;
    //private String dirWorldTheEnd;

    private JPanel contentPane;
    private JButton buttonStart;
    private Sorter sort;

    private Thread t;

    private List<Player> players;
    private List<Region> regions;

    public Parser(String dir, JPanel contentPane, JButton buttonStart, Sorter sort, List<Player> players){
        this.dirEssentials = dir + "/Essentials/userdata/";
        this.dirOnTime = dir + "/OnTime/";
        this.dirWorld = dir + "/WorldGuard/worlds/world/";
        //this.dirWorldNether = dir + "/WorldGuard/worlds/world_nether/";
        //this.dirWorldTheEnd = dir + "/WorldGuard/worlds/world_the_end/";

        this.contentPane = contentPane;
        this.buttonStart = buttonStart;
        this.sort = sort;

        this.players = players;
        regions = new ArrayList<>();

        start();
    }

    public void start()
    {
        if (t == null)
        {
            t = new Thread (this);
            t.start();
        }
    }

    @Override
    public void run() {
        try {
            buttonStart.setText("Парсим essentials ...");
            nicknamesAndUuid(dirEssentials);

            buttonStart.setText("Парсим ontime ...");
            onlineTime(dirOnTime);

            buttonStart.setText("Парсим worldguard ...");
            getRegions(dirWorld);

            buttonStart.setText("База собрана. " + players.size() + " игроков.");
            sort.show(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(contentPane, "Ошибка. Смотри консоль.");
        }

        for (Player p : players){

        }
    }

    private void nicknamesAndUuid(String dir) throws IOException {
        List<String> files = new ArrayList<>();

        Files.walk(Paths.get(dir)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                files.add(filePath.toAbsolutePath().toString());
            }
        });

        for (String file : files){
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            br.close();
            String all = sb.toString();

            if (all.contains("lastAccountName: ") && all.contains("  logout: ")){
                Player p = new Player();

                String str = "lastAccountName: ";
                int startIndex = all.indexOf(str) + str.length();
                int endIndex = all.indexOf("\n" + "timestamps:");
                p.nick = all.substring(startIndex, endIndex);

                str = "\n" + "  logout: ";
                startIndex = all.indexOf(str) + str.length();
                endIndex = startIndex + 13;
                p.lastSeenTimeInDays = toDays(all.substring(startIndex, endIndex));

                str = "/";
                startIndex = file.lastIndexOf(str) + str.length();
                endIndex = file.length() - 4;
                p.uuid = file.substring(startIndex, endIndex);

                if (p.nick.length() > 3 && !p.nick.contains("geolocation:")){
                    players.add(p);
                }
            }
        }
    }

    private void onlineTime(String dir) throws IOException {
        List<String> files = new ArrayList<>();

        Files.walk(Paths.get(dir)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                String fileName = filePath.toAbsolutePath().toString();
                fileName = fileName.substring(0, fileName.length() - 4);
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                if (fileName.contains("DailyReport")){
                    files.add(filePath.toAbsolutePath().toString());
                }
            }
        });

        for (String file : files){
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
                String check = "";

                if (line != null && line.contains("Day")){
                    check = line.substring(line.indexOf("Day") + 3);
                }

                if (check.contains("Day") && line.charAt(0) == '#'){
                    line = line.substring(line.indexOf(" ") + 1);

                    String name = line.substring(0, line.indexOf(" "));
                    String str1 = "Total:";
                    String str2 = "Day";
                    String time = line.substring(line.indexOf(str1) + str1.length());
                    time = time.replace(" ", "");
                    time = time.substring(0, time.indexOf(str2));

                    int playedDays = Integer.parseInt(time);

                    for (Player p : players){
                        if (p.nick.equals(name)){
                            if (p.playedTimeInDays < playedDays){
                                p.playedTimeInDays = playedDays;
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private void getRegions(String dir) throws IOException {
        String file = dir + "regions.yml";
        String rawText = "";

        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();

            if (line != null && !line.contains("min:") && !line.contains("max:")
                && !line.contains("#") && !line.contains("regions:") && !line.contains("members:")
                && !line.contains("flags:") && !line.contains("owners:") && !line.contains("type:")
                && !line.contains("priority:"))
            {
                if (!line.contains(":") || line.contains("unique-ids:")){
                    rawText += line.replace("unique-ids:", "").replace(" ", "").replace("[", "").replace("]", "")
                            .replace(",", "\n").replace("'", "") + "\n";
                }
                else{
                    if (line.length() < line.lastIndexOf(":") + 2){
                        rawText += line.replace("unique-ids:", "").replace(" ", "").replace("[", "").replace("]", "")
                                .replace(",", "\n").replace("'", "") + "\n";
                    }
                }
            }
        }

        List<String> text = new ArrayList<>();
        for (String str : rawText.split("\n")){
            if (str != "" && str.length() > 1){
                text.add(str.replace("\n", ""));
            }
        }

        Region r = new Region();
        for (String str : text){
            if (str.contains(":")){
                if (!r.name.equals("")){
                    regions.add(r);
                }
                r = new Region();
                r.name = str.replace(":", "");
            }else{
                r.players.add(str);
            }
        }
        if (!r.name.equals("")){
            regions.add(r);
        }

        JOptionPane.showMessageDialog(contentPane, "Всего регионов: " + regions.size());

        for (Region reg : regions){
            for (String player : reg.players){
                for (Player p : players){
                    if (p.uuid.equals(player)){
                        p.regions.add(reg.name);
                    }
                }
            }
        }
    }

    private int toDays(String time){
        int normalTime = Integer.parseInt(time.substring(0,10));
        int unixTime = (int) (System.currentTimeMillis() / 1000L);
        int timeDiff = unixTime - normalTime;
        int days = timeDiff / 60 / 60 / 24;

        return days;
    }
}
