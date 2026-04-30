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

    private static final String[] COLOR_NAMES =
        {"Red", "Blue", "Green", "Orange", "Purple", "Cyan"};
    private static final Color[] LANE_COLORS =
        {Color.RED, Color.BLUE, new Color(0, 150, 0),
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
    private JTextField[] symbolFields = new JTextField[MAX_TYPISTS];
    private JComboBox<String>[] colorBoxes = new JComboBox[MAX_TYPISTS];
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
        symbolFields[i] = new JTextField(String.valueOf((char)('1' + i)));
        colorBoxes[i] = new JComboBox<String>(COLOR_NAMES);
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
        symbolFields[i].setPreferredSize(new Dimension(40, 26));
        symbolFields[i].setMaximumSize(new Dimension(40, 26));
        colorBoxes[i].setMaximumSize(new Dimension(100, 26));

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
        topRow.add(Box.createHorizontalStrut(10));
        topRow.add(new JLabel("Acc: "));
        topRow.add(accuracyFields[i]);
        topRow.add(Box.createHorizontalStrut(10));
        topRow.add(new JLabel("Symbol: "));
        topRow.add(symbolFields[i]);
        topRow.add(Box.createHorizontalStrut(10));
        topRow.add(new JLabel("Colour: "));
        topRow.add(colorBoxes[i]);
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
            String symbolText = symbolFields[i].getText().trim();
            char symbol;
            if (symbolText.length() == 1)
            {
                symbol = symbolText.charAt(0);
            }
            else
            {
                symbol = (char) ('1' + i);
            }

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
            int colorIdx = colorBoxes[i].getSelectedIndex();
            Color laneColor = LANE_COLORS[colorIdx];

            lane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, laneColor),
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
            bar.setForeground(laneColor);
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

    // One turn of the race

    /**
     * Processes on turn of the race every time the timer ticks.
     * Moves the typists, updates the window and checks winner
     */
    private void doOneTurn()
    {
        turnCount++;

        // Resets the mistype flags every turn
        for (int i = 0; i < mistypedThisTurn.size(); i++)
        {
            mistypedThisTurn.set(i, false);
        }

        // Moves every typist
        for (int i = 0; i < typists.size(); i++)
        {
            if (finishOrder.contains(i)) continue;
            advanceOne(i);
        }

        refreshLanes();

        // Checks to see if anyone has finished in this turn
        for (int i = 0; i < typists.size(); i++)
        {
            if (typists.get(i).getProgress() >= passageText.length()
                && !finishOrder.contains(i))
            {
                finishOrder.add(i);

                // If they are a winner, they will gain an accuracy boost
                if (finishOrder.size() == 1)
                {
                    Typist w = typists.get(i);
                    w.setAccuracy(w.getAccuracy() + WIN_ACCURACY_BOOST);
                }
            }
        }

        // Once the first person finishes race ends 
        if (finishOrder.size() >= 1)
        {
            for (int i = 0; i < typists.size(); i++)
            {
                if (!finishOrder.contains(i))
                {
                    // Adds everyone else to the list
                    int insertAt = finishOrder.size();
                    for (int j = 1; j < finishOrder.size(); j++)
                    {
                        if (typists.get(i).getProgress()
                            > typists.get(finishOrder.get(j)).getProgress())
                        {
                            insertAt = j;
                            break;
                        }
                    }
                    finishOrder.add(insertAt, i);
                }
            }

            // Stop the race timer to show the progress bars for short time
            raceTimer.stop();
            Timer pause = new Timer(700, e -> showResultsAndReturn());
            pause.setRepeats(false);
            pause.start();
        }
    }

    /**
     * Handles the maths for each typist's turn
     * Deals with typing speed, mistypes, burnout risks
     */
    private void advanceOne(int i)
    {
        Typist t = typists.get(i);

        // If they are burnt out, they will wait and recover
        if (t.isBurntOut())
        {
            t.recoverFromBurnout();
            return;
        }

        // Keeps track of how many time they have tried to type
        attempts.set(i, attempts.get(i) + 1);

        // Caffeine modifier which gives boost for 10 rounds
        double effectiveAcc = t.getAccuracy();
        if (caffeineOn && turnCount <= 10)
        {
            effectiveAcc += 0.10;
        }
        // Keeps accuracy between 0 and 1
        if (effectiveAcc > 1.0) effectiveAcc = 1.0;
        if (effectiveAcc < 0.0) effectiveAcc = 0.0;

        // Attempts to type a character
        if (Math.random() < effectiveAcc)
        {
            t.typeCharacter();
        }

        // Mistype Logic checked + noise cancelling headphones debuff
        double mistypeChance = (1.0 - t.getAccuracy()) * MISTYPE_BASE_CHANCE;
        if (accessoriesList.get(i).equals("Noise-Cancel HP"))
        {
            mistypeChance *= 0.5;
        }

        if (Math.random() < mistypeChance)
        {
            // Autocorrect halves the slide back distance
            int slide = SLIDE_BACK_AMOUNT;
            if (autocorrectOn) slide = slide / 2;
            t.slideBack(slide);
            mistypes.set(i, mistypes.get(i) + 1);
            mistypedThisTurn.set(i, true);
        }

        // Burnout check
        double burnoutChance = 0.05 * t.getAccuracy() * t.getAccuracy();
        if (caffeineOn && turnCount > 10)
        {
            // After the caffeine boost wears off, burnout risk doubles.
            burnoutChance *= 2.0;
        }

        if (Math.random() < burnoutChance)
        {
            // Wrist Support shortens burnout by one turn
            int duration = BURNOUT_DURATION;
            if (accessoriesList.get(i).equals("Wrist Support"))
            {
                duration--;
            }
            if (duration < 1) duration = 1;
            t.burnOut(duration);
            t.setAccuracy(t.getAccuracy() - BURNOUT_ACCURACY_DROP);
            burnouts.set(i, burnouts.get(i) + 1);
        }
    }

    /** 
     * This method refreshes the screen every turn so the progress bars 
     * and status labels stay up to date
     */
    private void refreshLanes()
    {
        // Updates the turns counter
        turnLabel.setText("Turn: " + turnCount);

        int max = passageText.length();
        for (int i = 0; i < typists.size(); i++)
        {
            Typist t = typists.get(i);

            // Gets the current progress and ensures it doesnt go past the end
            int p = t.getProgress();
            if (p > max) p = max;

            // Updates the progress bar and the text
            progressBars.get(i).setValue(p);
            progressBars.get(i).setString(p + " / " + max);

            // Checks what status message to show 
            String status;
            if (t.isBurntOut())
            {
                status = "BURNT OUT (" + t.getBurnoutTurnsRemaining() + ")";
                statusLabels.get(i).setForeground(Color.RED);
            }
            else if (mistypedThisTurn.get(i))
            {
                status = "[mistype]";
                statusLabels.get(i).setForeground(new Color(180, 100, 0));
            }
            else
            {
                status = " ";
                statusLabels.get(i).setForeground(Color.DARK_GRAY);
            }
            statusLabels.get(i).setText(status);

            // Refreshes the accuracy text whenever it changes
            nameLabels.get(i).setText(t.getSymbol() + " " + t.getName()
                + "  (Acc: " + String.format("%.2f", t.getAccuracy()) + ")");
        }
    }

    // Results popup window

    /**
     * Builds the results table, and updates the overall leaderboard,
     * and shows a popup when the race ends
     */
    private void showResultsAndReturn()
    {
        // Calculates how long the race took in minutes
        long elapsedMs = System.currentTimeMillis() - raceStartMs;
        double minutes = elapsedMs / 60000.0;

        // Using StringBuilder and String.format to make a neat table
        StringBuilder sb = new StringBuilder();
        sb.append("=== Race Results ===\n\n");
        sb.append(String.format("%-4s %-12s %-7s %-7s %-9s %s%n",
            "Pos", "Name", "WPM", "Acc%", "Burnouts", "Earnings"));
        sb.append("-----------------------------------------------------\n");

        for (int rank = 0; rank < finishOrder.size(); rank++)
        {
            int idx = finishOrder.get(rank);
            int pos = rank + 1;
            Typist t = typists.get(idx);

            // Calculates Words Per Minute (WPM)
            double wpm = 0;
            if (minutes > 0)
            {
                wpm = (passageText.length() / 5.0) / minutes;
            }

            // Calculating accuracy percentage with validation
            int att = attempts.get(idx);
            int mis = mistypes.get(idx);
            double acc = 0;
            if (att > 0)
            {
                acc = (1.0 - (double) mis / att) * 100.0;
            }
            if (acc < 0) acc = 0;
            if (acc > 100) acc = 100;

            // Getting the coins and leaderboard points for the typist
            int earned = computeEarnings(idx, pos, wpm);
            int points = computePoints(pos, burnouts.get(idx));

            updateLeaderboard(t.getName(), points, earned);

            // Adds the typists stats to the result table
            sb.append(String.format("%-4d %-12s %-7.1f %-7.1f %-9d %d coins%n",
                pos, trim(t.getName(), 12), wpm, acc, burnouts.get(idx), earned));
        }

        sb.append("\n=== Global Leaderboard ===\n\n");
        sb.append(buildLeaderboardText());

        // Builds a text area for the results
        JTextArea resultsArea = new JTextArea(sb.toString());
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        resultsArea.setBackground(BG_COLOUR);
        resultsArea.setForeground(TEXT_COLOUR);
        resultsArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JOptionPane.showMessageDialog(frame, resultsArea,
            "Race Finished", JOptionPane.INFORMATION_MESSAGE);

        // Switches back to the setup window
        frame.setContentPane(setupPanel);
        frame.setSize(950, 720);
        frame.setLocationRelativeTo(null);
        frame.revalidate();
        frame.repaint();
    }

    /** 
     * handles the amount of coin earnings including 
     * a bonus for speed, but a penalty for burning out.
     */
    private int computeEarnings(int idx, int pos, double wpm)
    {
        // Prize money for each position
        int coins;
        if (pos == 1) coins = 100;
        else if (pos == 2) coins = 60;
        else if (pos == 3) coins = 30;
        else coins = 10;

        // Speed bonus and burnout penalty
        if (wpm > 50) coins += 25;
        if (burnouts.get(idx) > 0) coins -= 10 * burnouts.get(idx);
        if (coins < 0) coins = 0;

        // Sponsor bonus
        String sp = sponsorsList.get(idx);
        if (sp.startsWith("KeyCorp") && burnouts.get(idx) == 0) coins += 50;
        if (sp.startsWith("TypeFast") && pos == 1) coins += 100;

        return coins;
    }

    /** 
     * Handles the leaderboard points.
     * 3 for 1st, 2 for 2nd, and 1 for 3rd.
     */
    private int computePoints(int pos, int b)
    {
        int p = 0;
        if (pos == 1) p = 3;
        else if (pos == 2) p = 2;
        else if (pos == 3) p = 1;

        if (b > 0) p--;
        if (p < 0) p = 0;
        return p;
    }

    // Leaderboard

    /**
     * Adds the points and coins form the current race to leaderboard
     * For Existing names, coins will be added to their total 
     */
    private void updateLeaderboard(String name, int points, int coins)
    {
        int idx = leaderboardNames.indexOf(name);
        if (idx < 0)
        {
            // First time they have raced
            leaderboardNames.add(name);
            leaderboardPoints.add(points);
            leaderboardCoins.add(coins);
        }
        else
        {
            // Typist has raced before (updates their scores)
            leaderboardPoints.set(idx, leaderboardPoints.get(idx) + points);
            leaderboardCoins.set(idx, leaderboardCoins.get(idx) + coins);
        }
    }

    /**
     * Builds the text table for the leaderboard
     * Sorts typist with the most points
     */
    private String buildLeaderboardText()
    {
        // Start with a list of numbers [0, 1, 2...] representing typists
        ArrayList<Integer> order = new ArrayList<Integer>();
        for (int i = 0; i < leaderboardNames.size(); i++)
        {
            order.add(i);
        }

        // Sorts by points
        for (int i = 0; i < order.size(); i++)
        {
            int best = i;
            for (int j = i + 1; j < order.size(); j++)
            {
                if (leaderboardPoints.get(order.get(j))
                    > leaderboardPoints.get(order.get(best)))
                {
                    best = j;
                }
            }
            int tmp = order.get(i);
            order.set(i, order.get(best));
            order.set(best, tmp);
        }

        // Builds the text table using sorted order
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-4s %-12s %-7s %s%n",
            "Rank", "Name", "Points", "Coins"));
        sb.append("--------------------------------------\n");
        for (int rank = 0; rank < order.size(); rank++)
        {
            int i = order.get(rank);
            sb.append(String.format("%-4d %-12s %-7d %d%n",
                rank + 1, trim(leaderboardNames.get(i), 12),
                leaderboardPoints.get(i), leaderboardCoins.get(i)));
        }
        return sb.toString();
    }

    // Cuts strings down so it can fit into tables
    private String trim(String s, int len)
    {
        if (s.length() <= len)
        {
            return s;
        }
        return s.substring(0, len);
    }
}