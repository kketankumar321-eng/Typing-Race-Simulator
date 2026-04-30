## Dependencies

- Java Development Kit (JDK) 11 or higher
- No external libraries required for either part
- Part 2 uses Java Swing which is included in the standard JDK

## Part 1 — Textual Simulation

### How to compile

```bash
cd Part1
javac Typist.java TypingRace.java 
```

### How to run the race

```bash
java TypingRace
```

This starts a race with three typists with different accuracies. The race animates
in the terminal turn by turn until one typist finishes the passage.


## Part 2 — GUI Simulation

### How to compile

Open a terminal and navigate to the Part2 folder:

```bash
cd Part2
javac Part1/Typist.java Part2/TypingRaceGUI.java -d Part2
```

The `-cp ../Part1` flag tells Java where to find the Typist class from Part 1.
Part 2 reuses it

### How to run

```bash
java Part1/Typist.java Part2/TypingRaceGUI.java -d Part2
```


### How to use the GUI

1. Choose a passage from the dropdown (Short / Medium / Long) or type your own
2. Tick the difficulty modifiers (Autocorrect / Caffeine / Night Shift)
3. Select how many typists (2-6) using the dropdown
4. Fill in each typist's name, symbol, accuracy (0.0-1.0), colour, typing style, keyboard,
   accessory and sponsor
5. Click Start Race
6. Watch the animated progress bars with status labels, symbols, colour show burnouts and mistypes
7. When a typist finishes, a results popup shows WPM, accuracy, earnings
   and the leaderboard
8. Click OK to go back to the setup screen and run another race

### Difficulty Modifiers

- Autocorrect: halves the slide-back distance when a typist mistypes
- Caffeine Mode: boosts accuracy in the first 10 turns, doubles burnout
  risk afterwards
- Night Shift: reduces all typists' accuracy by 0.05 at the start

### Customisation

- Typing Style: affects accuracy (Touch Typist +0.10, Hunt & Peck -0.10, etc)
- Keyboard: affects accuracy (Mechanical +0.05, Stenography +0.10, etc)
- Accessory: Wrist Support shortens burnout, Noise-Cancel HP halves mistype chance
- Sponsor: KeyCorp pays +50 coins for finishing without burning out,
  TypeFast pays +100 coins for finishing first

## Notes

- All code compiles and runs using standard command-line tools
- No IDE-specific configuration is required
- Part 2 reuses the Typist class from Part 1 unchanged