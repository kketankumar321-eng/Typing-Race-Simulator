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

    /**
     * Builds a single row for one typist.
     * Each row is a small panel containing 2 horizontal rows 
     * containing two horizontal rows where the top row has the seat label,
     * name and accuracy fields, and the bottom row has the four dropdowns.
     */
    private JPanel buildSeatRow(int i)
    {
        // Outer panel with two rows inside with
        // a line at the bottom for seperation.
        JPanel seat = new JPanel();
        seat.setLayout(new BoxLayout(seat, BoxLayout.Y_AXIS));
        seat.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOUR),
            BorderFactory.createEmptyBorder(6, 4, 6, 4)));
        seat.setBackground(BG_COLOUR);
        seat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Sets up the text boxes and dropdown menus
        nameFields[i] = new JTextField("Typist" + (i + 1));
        accuracyFields[i] = new JTextField("0.7");
        styleBoxes[i] = new JComboBox<String>(STYLES);
        keyboardBoxes[i] = new JComboBox<String>(KEYBOARDS);
        accessoryBoxes[i] = new JComboBox<String>(ACCESSORIES);
        sponsorBoxes[i] = new JComboBox<String>(SPONSORS);

        // All sizes are adjusted to make it alligned
        nameFields[i].setPreferredSize(new Dimension(180, 26));
        nameFields[i].setMaximumSize(new Dimension(180, 26));
        accuracyFields[i].setPreferredSize(new Dimension(60, 26));
        accuracyFields[i].setMaximumSize(new Dimension(60, 26));
        styleBoxes[i].setMaximumSize(new Dimension(160, 26));
        keyboardBoxes[i].setMaximumSize(new Dimension(160, 26));
        accessoryBoxes[i].setMaximumSize(new Dimension(170, 26));
        sponsorBoxes[i].setMaximumSize(new Dimension(220, 26));

        // Top row which includes seat label + name + accuracy
        JPanel topRow = new JPanel();
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        topRow.setOpaque(false);

        JLabel seatLabel = new JLabel("Seat " + (i + 1));
        seatLabel.setFont(new Font("Arial", Font.BOLD, 13));
        seatLabel.setForeground(TEXT_COLOUR);
        seatLabel.setPreferredSize(new Dimension(60, 26));

        topRow.add(seatLabel);
        topRow.add(new JLabel("Name: "));
        topRow.add(nameFields[i]);
        topRow.add(Box.createHorizontalStrut(16));
        topRow.add(new JLabel("Accuracy: "));
        topRow.add(accuracyFields[i]);
        topRow.add(Box.createHorizontalGlue());

        // Bottom row which includes the 4 dropdown options
        JPanel bottomRow = new JPanel();
        bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.X_AXIS));
        bottomRow.setOpaque(false);
        bottomRow.setBorder(BorderFactory.createEmptyBorder(4, 60, 0, 0));

        bottomRow.add(new JLabel("Style: "));
        bottomRow.add(styleBoxes[i]);
        bottomRow.add(Box.createHorizontalStrut(10));
        bottomRow.add(new JLabel("Keyboard: "));
        bottomRow.add(keyboardBoxes[i]);
        bottomRow.add(Box.createHorizontalStrut(10));
        bottomRow.add(new JLabel("Accessory: "));
        bottomRow.add(accessoryBoxes[i]);
        bottomRow.add(Box.createHorizontalStrut(10));
        bottomRow.add(new JLabel("Sponsor: "));
        bottomRow.add(sponsorBoxes[i]);
        bottomRow.add(Box.createHorizontalGlue());

        // Adds both rows to the main seat window
        seat.add(topRow);
        seat.add(bottomRow);
        return seat;
    }

    /**
     * When the user changes the passage dropdown, it will
     * fill the text field with a predefined passage
     * Picking "Custom" allows user to type anything.
     */
    private void updatePassageField()
    {
        String choice = (String) passageDropdown.getSelectedItem();
        if (choice.equals("Short"))
        {
            passageField.setText("The quick brown fox jumps over the lazy dog.");
        }
        else if (choice.equals("Medium"))
        {
            passageField.setText("Programming is the art of telling another human "
                + "being what one wants the computer to do.");
        }
        else if (choice.equals("Long"))
        {
            passageField.setText("It is a truth universally acknowledged that a "
                + "single man in possession of a good fortune must be in want of a wife.");
        }
    }

    // Shows the seat rows and hides the rest based on the count dropdown
    private void updateSeatVisibility()
    {
        int count = (Integer) typistCountDropdown.getSelectedItem();
        for (int i = 0; i < MAX_TYPISTS; i++)
        {
            seatRows[i].setVisible(i < count);
        }
    }


    /**
     * Validates the inputs, builds the typist list, siwtches to race window
     * Returns with error message if anything is wrong.
     */
    private void tryStartRace()
    {
        // Makes sure passage is long enough to start race
        String passage = passageField.getText().trim();
        if (passage.length() < 10)
        {
            JOptionPane.showMessageDialog(frame,"Passage must be at least 10 characters long.");
            return;
        }
        passageText = passage;
        autocorrectOn = autocorrectBox.isSelected();
        caffeineOn = caffeineBox.isSelected();
        nightShiftOn = nightShiftBox.isSelected();

        int count = (Integer) typistCountDropdown.getSelectedItem();

        // Resets all lists so data from previous races dont
        // carry over to the current race and mess up
        typists = new ArrayList<Typist>();
        accessoriesList = new ArrayList<String>();
        sponsorsList = new ArrayList<String>();
        burnouts = new ArrayList<Integer>();
        mistypes = new ArrayList<Integer>();
        attempts = new ArrayList<Integer>();
        mistypedThisTurn = new ArrayList<Boolean>();
        finishOrder = new ArrayList<Integer>();

        for (int i = 0; i < count; i++)
        {
            String name = nameFields[i].getText().trim();
            if (name.length() == 0)
            {
                JOptionPane.showMessageDialog(frame,
                    "Seat " + (i + 1) + ": name cannot be empty.");
                return;
            }

            // Checks if the accuracy is a valid number
            double accuracy;
            try
            {
                accuracy = Double.parseDouble(accuracyFields[i].getText().trim());
            }
            catch (NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(frame,
                    "Seat " + (i + 1) + ": accuracy must be a number.");
                return;
            }

            // Adding bonuses or debuffs based on the style or keyboard picked
            accuracy += styleDelta((String) styleBoxes[i].getSelectedItem());
            accuracy += keyboardDelta((String) keyboardBoxes[i].getSelectedItem());

            // Night Shift modifier, small accuracy reduction for everyone
            if (nightShiftOn)
            {
                accuracy -= 0.05;
            }

            // Using the seat number 1-6 as the same symbol on the window
            char symbol = (char) ('1' + i);
            Typist t = new Typist(symbol, name, accuracy);

            typists.add(t);
            accessoriesList.add((String) accessoryBoxes[i].getSelectedItem());
            sponsorsList.add((String) sponsorBoxes[i].getSelectedItem());
            burnouts.add(0);
            mistypes.add(0);
            attempts.add(0);
            mistypedThisTurn.add(false);
        }

        raceStartMs = System.currentTimeMillis();
        turnCount = 0;

        buildLanes();
        frame.setContentPane(racePanel);

        int raceHeight = 130 + (count * 58);
        frame.setSize(950, raceHeight);
        frame.setLocationRelativeTo(null);

        frame.revalidate();
        frame.repaint();

        // Stops any old timers before starting a new race
        if (raceTimer != null)
        {
            raceTimer.stop();
        }
        raceTimer = new Timer(TURN_INTERVAL_MS, e -> doOneTurn());
        raceTimer.start();
    }

    // Gives accuracy change depending on the typing style
    private double styleDelta(String s)
    {
        if (s.equals("Touch Typist")) return 0.10;
        if (s.equals("Hunt & Peck")) return -0.10;
        if (s.equals("Phone Thumbs")) return -0.05;
        if (s.equals("Voice-to-Text")) return 0.05;
        return 0.0;
    }

    // Gives accuracy change depending on keyboard type
    private double keyboardDelta(String k)
    {
        if (k.equals("Mechanical")) return 0.05;
        if (k.equals("Membrane")) return 0.00;
        if (k.equals("Touchscreen")) return -0.05;
        if (k.equals("Stenography")) return 0.10;
        return 0.0;
    }

    // Race Window
    /**
     * Builds the window where the actual race happens.
     * Includes a title at the top and a place for all the racer progress bars.
     */
    private JPanel buildRacePanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_COLOUR);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("Race in Progress");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(TEXT_COLOUR);
        top.add(title, BorderLayout.WEST);

        // Turn counter styled
        turnLabel = new JLabel("Turn: 0");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 14));
        turnLabel.setForeground(Color.WHITE);
        turnLabel.setBackground(BUTTON_COLOUR);
        turnLabel.setOpaque(true);
        turnLabel.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        top.add(turnLabel, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);

        laneContainer = new JPanel();
        laneContainer.setLayout(new BoxLayout(laneContainer, BoxLayout.Y_AXIS));
        laneContainer.setBackground(BG_COLOUR);

        // All lanes inside a wrapper to keep at top all times
        JPanel laneWrapper = new JPanel(new BorderLayout());
        laneWrapper.setBackground(BG_COLOUR);
        laneWrapper.add(laneContainer, BorderLayout.NORTH);

        // Scroll bar in case of lots of racers 
        JScrollPane scroll = new JScrollPane(laneWrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_COLOUR);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    /** 
     * Adds a progress bar, name, and status for every typist
     * and clears the old lanes for fresh reset.
     */
    private void buildLanes()
    {
        laneContainer.removeAll();
        progressBars = new ArrayList<JProgressBar>();
        statusLabels = new ArrayList<JLabel>();
        nameLabels = new ArrayList<JLabel>();

        int max = passageText.length();

        for (int i = 0; i < typists.size(); i++)
        {
            Typist t = typists.get(i);

            // Each lane is a white frame with the typist's colour as the left border.
            JPanel lane = new JPanel(new BorderLayout(10, 4));
            lane.setBackground(Color.WHITE);
            lane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0,
                    LANE_COLORS[i % LANE_COLORS.length]),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
            lane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            JLabel nameLabel = new JLabel(t.getSymbol() + " " + t.getName()
                + "  (Acc: " + String.format("%.2f", t.getAccuracy()) + ")");
            nameLabel.setPreferredSize(new Dimension(220, 24));
            nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
            nameLabel.setForeground(TEXT_COLOUR);
            lane.add(nameLabel, BorderLayout.WEST);
            nameLabels.add(nameLabel);

            JProgressBar bar = new JProgressBar(0, max);
            bar.setValue(0);
            bar.setStringPainted(true);
            bar.setForeground(LANE_COLORS[i % LANE_COLORS.length]);
            bar.setBackground(new Color(240, 245, 245));
            lane.add(bar, BorderLayout.CENTER);
            progressBars.add(bar);

            JLabel status = new JLabel(" ");
            status.setPreferredSize(new Dimension(120, 24));
            status.setFont(new Font("Arial", Font.PLAIN, 12));
            lane.add(status, BorderLayout.EAST);
            statusLabels.add(status);

            laneContainer.add(lane);
            laneContainer.add(Box.createVerticalStrut(8));
        }

        laneContainer.revalidate();
        laneContainer.repaint();
    }
}