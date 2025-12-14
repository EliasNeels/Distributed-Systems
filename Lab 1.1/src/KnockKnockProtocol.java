import java.net.*;
import java.io.*;

/*
 * The KnockKnockProtocol class keeps track of the current joke,
 * the current state within the joke, and the jokes themselves.
 */
class KnockKnockProtocol {
    private final static int WAITING = 0;
    private final static int SENTKNOCKKNOCK = 1;
    private final static int SENTCLUE = 2;
    private final static int ANOTHER = 3;

    private final static int NUM_JOKES = 5;
    private final int[] clue = { 0, 1, 2, 3, 4 };
    private final String[] answers = {
            "Turnip the heat, it's cold in here!",
            "Dexter halls with boughs of holly.",
            "Lettuce in, it's raining!",
            "Boo who? Don't cry, it's only a joke!",
            "A little old lady who? I didn't know you could yodel!"
    };
    private int state = WAITING;
    private int currentJoke = 0;

    String processInput(String theInput) {
        String theOutput = null;

        if (state == WAITING) {
            theOutput = "Knock! Knock!";
            state = SENTKNOCKKNOCK;
        } else if (state == SENTKNOCKKNOCK) {
            if (theInput.equalsIgnoreCase("Who's there?")) {
                theOutput = "Arthur.";
                state = SENTCLUE;
            } else {
                theOutput = "You're supposed to say \"Who's there?\" " +
                        "Try again. Knock! Knock!";
            }
        } else if (state == SENTCLUE) {
            if (theInput.equalsIgnoreCase("Arthur who?")) {
                theOutput = answers[currentJoke] + " Want another? (y/n)";
                state = ANOTHER;
            } else {
                theOutput = "You're supposed to say \"" +
                        getClue(currentJoke) +
                        " who?\"" +
                        " Try again. Knock! Knock!";
                state = SENTKNOCKKNOCK;
            }
        } else if (state == ANOTHER) {
            if (theInput.equalsIgnoreCase("y")) {
                theOutput = "Knock! Knock!";
                int newJoke = (int)(Math.random() * NUM_JOKES);
                if (newJoke == currentJoke) {
                    newJoke = (newJoke + 1) % NUM_JOKES;
                }
                currentJoke = newJoke;
                state = SENTKNOCKKNOCK;
            } else {
                theOutput = "Bye.";
                state = WAITING;
            }
        }
        return theOutput;
    }

    private String getClue(int i) {
        switch (i) {
            case 0: return "Turnip";
            case 1: return "Little old lady";
            case 2: return "Atch";
            case 3: return "Who";
            case 4: return "Arthur";
            default: return null;
        }
    }
}
