import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends JDialog {
    private JPanel contentPane;
    private JTextField dirPlugins;
    private JButton buttonStart;
    private JTextField lastLogin;
    private JTextField allTime;
    private JButton buttonSort;
    private JTextField immunityDays;
    private JTextArea nicks;
    private JTextArea regions;

    private List<Player> players;
    private Sorter sort;

    public Main() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonStart);

        players = new ArrayList<>();
        sort = new Sorter(lastLogin, allTime, buttonSort, immunityDays, nicks, regions, players);
        sort.show(false);

        buttonStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonSort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSORT();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        Parser par = new Parser(dirPlugins.getText(), contentPane, buttonStart, sort, players);
    }

    private void onSORT() {
        nicks.setText("");
        regions.setText("");
        sort.Sort();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        Main dialog = new Main();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
