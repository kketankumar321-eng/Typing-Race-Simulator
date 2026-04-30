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

    // Constructor and main method
    public TypingRaceGUI()
    {
        frame = new JFrame("Typing Race Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 720);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        setupPanel = buildSetupPanel();
        racePanel = buildRacePanel();

        // Starts on the setup window
        frame.setContentPane(setupPanel);
    }
 
    // Shows the window up on the screen
    public void startRaceGUI()
    {
        frame.setVisible(true);
    }

    // Calls the start method and creates a new object
    public static void main(String[] args)
    {
        new TypingRaceGUI().startRaceGUI();
    }


    /**
     * Builds a border with a title that uses the colour palette and a bold font.
     * Used by every header on the setup window.
     */
    private javax.swing.border.Border styledBorder(String title)
    {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOUR, 1), title,
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 13), TEXT_COLOUR);
    }

    // Setup Window
    private JPanel buildSetupPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_COLOUR);

        // Title and mini description
        JLabel title = new JLabel("Typing Race Simulator");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(TEXT_COLOUR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Configure your race below, then start typing!");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 13));
        subtitle.setForeground(BUTTON_COLOUR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(2));
        titleBlock.add(subtitle);
        titleBlock.add(Box.createVerticalStrut(10));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleRow.setOpaque(false);
        titleRow.add(titleBlock);
        panel.add(titleRow);

        // Passage section
        JPanel passageRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passageRow.setBorder(styledBorder("1. Passage"));
        passageRow.setBackground(BG_COLOUR);
        passageDropdown = new JComboBox<String>(new String[] { "Short", "Medium", "Long", "Custom" });
        passageDropdown.addActionListener(e -> updatePassageField());
        passageField = new JTextField("The quick brown fox jumps over the lazy dog.", 35);
        passageRow.add(new JLabel("Choose:"));
        passageRow.add(passageDropdown);
        passageRow.add(passageField);
        panel.add(passageRow);

        // Modifiers section
        JPanel modRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modRow.setBorder(styledBorder("2. Modifiers"));
        modRow.setBackground(BG_COLOUR);
        autocorrectBox = new JCheckBox("Autocorrect");
        caffeineBox = new JCheckBox("Caffeine Mode");
        nightShiftBox = new JCheckBox("Night Shift");
        autocorrectBox.setOpaque(false);
        caffeineBox.setOpaque(false);
        nightShiftBox.setOpaque(false);
        modRow.add(autocorrectBox);
        modRow.add(caffeineBox);
        modRow.add(nightShiftBox);
        panel.add(modRow);

        // Typist count
        JPanel countRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        countRow.setBorder(styledBorder("3. Number of Typists"));
        countRow.setBackground(BG_COLOUR);
        typistCountDropdown = new JComboBox<Integer>(new Integer[] { 2, 3, 4, 5, 6 });
        typistCountDropdown.setSelectedItem(3);
        typistCountDropdown.addActionListener(e -> updateSeatVisibility());
        countRow.add(new JLabel("How many?"));
        countRow.add(typistCountDropdown);
        panel.add(countRow);

        // Seat rows
        JPanel seatsPanel = new JPanel();
        seatsPanel.setLayout(new BoxLayout(seatsPanel, BoxLayout.Y_AXIS));
        seatsPanel.setBorder(styledBorder("4. Typists"));
        seatsPanel.setBackground(BG_COLOUR);
        for (int i = 0; i < MAX_TYPISTS; i++)
        {
            seatRows[i] = buildSeatRow(i);
            seatsPanel.add(seatRows[i]);
        }
        panel.add(seatsPanel);

        // Start button
        JButton startButton = new JButton("Start Race");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBackground(BUTTON_COLOUR);
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(160, 38));
        startButton.addActionListener(e -> tryStartRace());

        JPanel startRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startRow.setOpaque(false);
        startRow.add(startButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(startRow);

        // Hides unused seats based on the typist count
        updateSeatVisibility();

        return panel;
    }
}