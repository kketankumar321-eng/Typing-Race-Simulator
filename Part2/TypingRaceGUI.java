import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Graphical User Interface for Typing Race Simulator (Part 2).
 *
 * Reuses the Part 1 Typist class logic and adds a GUI 
 * using Java swing to handle bauttons and windows.
 *
 * @author Krish Ketankumar
 * @version 1.0.1
 */
public class TypingRaceGUI
{
    // Adjustable constants similar to Part 1
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int SLIDE_BACK_AMOUNT = 2;
    private static final int BURNOUT_DURATION = 3;
    private static final double WIN_ACCURACY_BOOST = 0.02;
    private static final double BURNOUT_ACCURACY_DROP = 0.01;
    private static final int TURN_INTERVAL_MS = 200;
    private static final int MAX_TYPISTS = 6;

    // Customisation choices for the dropdowns
    private static final String[] STYLES = {"Touch Typist", "Hunt & Peck", "Phone Thumbs",
     "Voice-to-Text"};
    private static final String[] KEYBOARDS = {"Mechanical", "Membrane", "Touchscreen",
     "Stenography"};
    private static final String[] ACCESSORIES = {"None", "Wrist Support",
     "Noise-Cancel HP"};
    private static final String[] SPONSORS = {"None", "KeyCorp (no burnout +50)",
     "TypeFast (1st place +100)"};

    // Colour palette to be used throughout the GUI
    // Consists of similar colour shades of Green/Blue
    private static final Color TEXT_COLOUR = new Color(0x0B, 0x2E, 0x33);
    private static final Color BUTTON_COLOUR = new Color(0x4F, 0x7C, 0x82);
    private static final Color BORDER_COLOUR = new Color(0x93, 0xB1, 0xB5);
    private static final Color BG_COLOUR = new Color(0xB8, 0xE3, 0xE9);

    private static final Color[] LANE_COLORS = {Color.RED, Color.BLUE, new Color(0, 150, 0),
          Color.ORANGE, new Color(150, 0, 200), Color.CYAN};

    // Main windows
    private JFrame frame;
    private JPanel setupPanel;
    private JPanel racePanel;

    // Setup window widgets
    private JComboBox<String> passageDropdown;
    private JTextField passageField;
    private JComboBox<Integer> typistCountDropdown;
    private JCheckBox autocorrectBox;
    private JCheckBox caffeineBox;
    private JCheckBox nightShiftBox;

    // 6 Rows created for the typists and other rows are hidden if not needed,
    // which is simpler compared to adding and removing rows.
    private JPanel[] seatRows = new JPanel[MAX_TYPISTS];
    private JTextField[] nameFields = new JTextField[MAX_TYPISTS];
    private JTextField[] accuracyFields = new JTextField[MAX_TYPISTS];
    private JComboBox<String>[] styleBoxes = new JComboBox[MAX_TYPISTS];
    private JComboBox<String>[] keyboardBoxes = new JComboBox[MAX_TYPISTS];
    private JComboBox<String>[] accessoryBoxes = new JComboBox[MAX_TYPISTS];
    private JComboBox<String>[] sponsorBoxes = new JComboBox[MAX_TYPISTS];

    // Race window widgets
    private JLabel turnLabel;
    private JPanel laneContainer;
    private ArrayList<JProgressBar> progressBars;
    private ArrayList<JLabel> statusLabels;
    private ArrayList<JLabel> nameLabels;

    // Keeps track of all the typist's data.
    // Each typist has an ArrayList that has one entry.
    private ArrayList<Typist> typists;
    private ArrayList<String> accessoriesList;
    private ArrayList<String> sponsorsList;
    private ArrayList<Integer> burnouts;
    private ArrayList<Integer> mistypes;
    private ArrayList<Integer> attempts;
    private ArrayList<Boolean> mistypedThisTurn;
    private ArrayList<Integer> finishOrder;
    private long raceStartMs;
    private int turnCount;
    private Timer raceTimer;

    // Keeps track of global modifiers stored (If turned on or not)
    private boolean autocorrectOn;
    private boolean caffeineOn;
    private boolean nightShiftOn;
    private String passageText;

    // Leaderboard data that remains saved
    private ArrayList<String> leaderboardNames = new ArrayList<String>();
    private ArrayList<Integer> leaderboardPoints = new ArrayList<Integer>();
    private ArrayList<Integer> leaderboardCoins = new ArrayList<Integer>();
}