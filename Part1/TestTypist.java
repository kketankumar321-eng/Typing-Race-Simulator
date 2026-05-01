/**
 * Test class for Typist class
 *
 * Runs all five test plans required for Typist Class 
 *
 * @author Krish Ketankumar
 * @version 1.0
 */

public class TestTypist
{
    public static void main(String[] args)
    {
        Typist t1 = new Typist('h', "t1", 0.5);
        Typist t2 = new Typist('k', "t12", 0.4);

        // TEST 1: Movement
        System.out.println("==== TEST 1: Movement ====");
        System.out.println("Current position (Expected: 0): " + t2.getProgress());
        int startingPosition = t2.getProgress();
        System.out.println("Moves 1 Spaces forward");
        t2.typeCharacter(); 
        System.out.println("Current Progress (Expected: 1): " + t2.getProgress());
        t2.typeCharacter(); // Move forward twice
        System.out.println("Moves 1 Spaces forward");
        System.out.println("Moved from " + startingPosition + " to (Expected: 2) " + t2.getProgress());
        System.out.println();

        // TEST 2: slideBack() boundary
        System.out.println("==== TEST 2: slideBack() boundary ====");
        t1.typeCharacter();
        t1.typeCharacter();
        t1.typeCharacter();
        t1.typeCharacter();
        t1.typeCharacter();
        System.out.println("Current Progress: " + t1.getProgress());

        t1.slideBack(2); // Move backwards by 3
        System.out.println("Progress after slideBack(2) (Expected: 3): " + t1.getProgress());
        t1.slideBack(5); // Move backwards by 5
        System.out.println("Progress after slideBack(5) (Expected: 0): " + t1.getProgress());
        System.out.println();

        // TEST 3: Burnout Countdown
        System.out.println("==== TEST 3: Burnout Countdown ====");
        t1.burnOut(3); // Get burnt out for 3 turns

        System.out.println("Initial Burnout (Expected: 3): " + t1.getBurnoutTurnsRemaining());
        System.out.println("Burnout (T/F) (Expected: true): " + t1.isBurntOut());
        t1.recoverFromBurnout(); 
        System.out.println("After 1 turn (Expected: 2): " + t1.getBurnoutTurnsRemaining());
        System.out.println("Burnout (T/F) (Expected: true): " + t1.isBurntOut());
        t1.recoverFromBurnout();
        System.out.println("After 2 turns (Expected: 1): " + t1.getBurnoutTurnsRemaining()); 
        System.out.println("Burnout (T/F) (Expected: true): " + t1.isBurntOut());
        t1.recoverFromBurnout();
        System.out.println("After final burnout turn (Expected: 0): " + t1.getBurnoutTurnsRemaining());
        System.out.println("Burnout (T/F) (Expected: false): " + t1.isBurntOut()); 
        t1.recoverFromBurnout();
        System.out.println("Extra Recovery test (Expected: 0): " + t1.getBurnoutTurnsRemaining()); 
        System.out.println("Burnout (T/F) (Expected: false): " + t1.isBurntOut());
        System.out.println();

        // TEST 4: resetToStart()
        System.out.println("==== TEST 4: resetToStart() ====");
        t1.typeCharacter(); // Move forward once
        t1.typeCharacter(); // Move forward once
        System.out.println("Current Progress: " + t1.getProgress());
        t1.burnOut(5); // Get burnt out for 5 turns
        System.out.println("Current burnout turns: " + t1.getBurnoutTurnsRemaining());
        System.out.println("Burnout (T/F) (Expected: true): " + t1.isBurntOut());
        
        t1.resetToStart(); 
        System.out.println("Passage RESET");
        System.out.println("Progress (Expected: 0): " + t1.getProgress());
        System.out.println("Burnout turns (Expected: 0): " + t1.getBurnoutTurnsRemaining());
        System.out.println("isBurnout (T/F) (Expected: false): " + t1.isBurntOut());
        System.out.println();

        // TEST 5: Accuracy Range (0.0 - 1.0)
        System.out.println("==== TEST 5: Accuracy Range (0.0 - 1.0) ====");
        t1.setAccuracy(1.5); 
        System.out.println("Accuracy set to 1.5."); 
        System.out.println("Accuracy (Expected: 1.0): " + t1.getAccuracy()); 
        t1.setAccuracy(-0.5);
        System.out.println("Accuracy set to -0.5."); 
        System.out.println("Accuracy (Expected: 0.0): " + t1.getAccuracy());

        t1.setAccuracy(-200);
        System.out.println("Accuracy set to -200."); 
        System.out.println("Accuracy (Expected: 0.0): " + t1.getAccuracy());

        t1.setAccuracy(100);
        System.out.println("Accuracy set to 100."); 
        System.out.println("Accuracy (Expected: 1.0): " + t1.getAccuracy());
    }
}

