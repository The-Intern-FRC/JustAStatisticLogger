import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class EgoCalculatorV6 {

    private static final String USER_DATA_FOLDER = "user_data/";
    private static final String LEADERBOARD_FILE = "leaderboard.txt";

    private static final Map<String, Badge> BADGE_DEFINITIONS = new HashMap<>() {{
        put("Mini but Mighty", new Badge("Mini but Mighty", "Small but packs a punch. Who knew?", "Easy"));
        put("Rising Star", new Badge("Rising Star", "Modestly competent. Shine, little one.", "Medium"));
        put("Confident Programmer", new Badge("Confident Programmer", "Swagger in code. Not cocky… okay, maybe a little.", "Hard"));
        put("Sky High Ego", new Badge("Sky High Ego", "Tall enough to see over your ego… barely.", "Hard"));
        put("Overachiever", new Badge("Overachiever", "Ego > 450. Maybe tone it down before you break something.", "Extreme"));
        put("Napoleon Complex", new Badge("Napoleon Complex", "The ego makes up the height difference.", "Hidden"));
        put("The Intern", new Badge("The Intern", "There Can Only Be One.", "Hidden"));
        put("Self-Conscious", new Badge("Self-Conscious", "You didn’t save to the leaderboard. We noticed.", "Meta"));
        put("Egomaniac", new Badge("Egomaniac", "Calculated your ego 10 times in one sitting. Are you okay?", "Meta"));
        put("Mirror Mirror", new Badge("Mirror Mirror", "Checked your profile/stats. Narcissism level: medium.", "Meta"));
        put("Are you sure that you should be aspiring towards these?", new Badge("Are you sure that you should be aspiring towards these?", "Viewing the full badge list. Question your life choices.", "Meta"));
    }};

    private static Scanner scanner = new Scanner(System.in);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        new File(USER_DATA_FOLDER).mkdirs();
        System.out.println("=== Welcome to EgoCalculatorV6 ===\n");

        boolean playAgain = true;
        Map<String, Integer> sessionCalcCounter = new HashMap<>();

        while (playAgain) {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine().trim();

            // Count calculations
            sessionCalcCounter.put(name, sessionCalcCounter.getOrDefault(name, 0) + 1);

            double heightInches = getHeightFromUser();
            double egoScore = calculateEgo(heightInches);

            System.out.printf("\n=== Ego Score for %s: %.2f ===\n", name, egoScore);
            printScoreCommentary(egoScore);

            // Assign badges
            Set<String> earnedBadges = assignBadges(name, egoScore, sessionCalcCounter.get(name));

            // Display badges
            displayBadges(name, earnedBadges);

            // Leaderboard option
            boolean saveLeaderboard = promptYesNo("\nSave score to leaderboard? (y/n): ");
            if (!saveLeaderboard) {
                if (!earnedBadges.contains("Self-Conscious")) {
                    earnedBadges.add("Self-Conscious");
                    appendBadge(name, "Self-Conscious");
                }
            } else if (saveLeaderboard && !name.equalsIgnoreCase("Napoleon")) {
                appendToFile(LEADERBOARD_FILE, name + " | " + String.format("%.2f", egoScore) + " | " + dateFormat.format(new Date()));
            }

            // Save scores & badges
            appendScore(name, egoScore);
            for (String badge : earnedBadges) appendBadge(name, badge);

            // Show stats option
            if (promptYesNo("\nView achievements/stats? (y/n): ")) {
                // Automatic Mirror Mirror badge
                if (!earnedBadges.contains("Mirror Mirror")) {
                    earnedBadges.add("Mirror Mirror");
                    appendBadge(name, "Mirror Mirror");
                }
                displayStats(name);
            }

            // Show full badge list option
            if (promptYesNo("\nView full badge list? (y/n): ")) {
                displayFullBadgeList(name);
                if (!earnedBadges.contains("Are you sure that you should be aspiring towards these?")) {
                    earnedBadges.add("Are you sure that you should be aspiring towards these?");
                    appendBadge(name, "Are you sure that you should be aspiring towards these?");
                }
            }

            // Show science
            if (promptYesNo("\nSee how your score is calculated? (y/n): ")) {
                showScience(heightInches, egoScore);
            }

            playAgain = promptYesNo("\nPlay again? (y/n): ");
            System.out.println();
        }

        System.out.println("Thanks for playing EgoCalculatorV6! Remember: Your ego is your own problem.\n");
    }

    private static double getHeightFromUser() {
        System.out.println("\nChoose height input method:");
        System.out.println("1. Inches only");
        System.out.println("2. Feet + Inches");
        System.out.println("3. Centimeters");

        int choice;
        while (true) {
            System.out.print("Choice (1-3): ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= 3) break;
            } catch (Exception ignored) {}
            System.out.println("Invalid input.");
        }

        double inches = 0;
        switch (choice) {
            case 1 -> {
                System.out.print("Enter height in inches: ");
                inches = Double.parseDouble(scanner.nextLine());
            }
            case 2 -> {
                System.out.print("Feet: ");
                int ft = Integer.parseInt(scanner.nextLine());
                System.out.print("Inches: ");
                int in = Integer.parseInt(scanner.nextLine());
                inches = ft * 12 + in;
            }
            case 3 -> {
                System.out.print("Centimeters: ");
                double cm = Double.parseDouble(scanner.nextLine());
                inches = cm / 2.54;
            }
        }
        return inches;
    }

    private static double calculateEgo(double inches) {
        return 5.892 * inches - 6.243;
    }

    private static void printScoreCommentary(double ego) {
        if (ego < 250) System.out.println("A little self-confidence wouldn't hurt. Neither would some platform shoes.");
        else if (ego < 300) System.out.println("Not bad for a beginner. Keep dreaming.");
        else if (ego < 350) System.out.println("Impressive… for someone who still thinks 1+1 is hard.");
        else if (ego < 400) System.out.println("Hmm… competent. Just don’t get a big head.");
        else if (ego < 450) System.out.println("Almost insufferable. Careful, you might be noticed.");
        else System.out.println("Wow. That’s… alarmingly high. Maybe tone it down before you break something.");
    }

    private static Set<String> assignBadges(String name, double ego, int calcCount) {
        Set<String> badges = loadUserBadges(name);

        // Score-based
        if (ego < 250) addBadgeIfNew(name, badges, "Mini but Mighty");
        else if (ego < 300) addBadgeIfNew(name, badges, "Rising Star");
        else if (ego < 350) addBadgeIfNew(name, badges, "Confident Programmer");
        else if (ego < 400) addBadgeIfNew(name, badges, "Sky High Ego");
        else if (ego >= 450) addBadgeIfNew(name, badges, "Overachiever");

        // Secret hidden name badges
        if (name.equalsIgnoreCase("Napoleon")) addBadgeIfNew(name, badges, "Napoleon Complex");
        if ((name.equalsIgnoreCase("Intern") || name.equalsIgnoreCase("Ruthie") || name.equalsIgnoreCase("The Intern"))
                && Math.abs(ego - 382.63) < 0.01) addBadgeIfNew(name, badges, "The Intern");

        // Meta badges
        if (calcCount >= 10) addBadgeIfNew(name, badges, "Egomaniac");

        return badges;
    }

    private static void addBadgeIfNew(String name, Set<String> badges, String badge) {
        if (!badges.contains(badge)) {
            badges.add(badge);
            appendBadge(name, badge);
        }
    }

    private static boolean promptYesNo(String msg) {
        while (true) {
            System.out.print(msg);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            else if (input.equals("n") || input.equals("no")) return false;
            System.out.println("Please enter y or n.");
        }
    }

    private static void appendToFile(String filename, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException ignored) {}
    }

    private static void appendScore(String name, double score) {
        appendToFile(USER_DATA_FOLDER + name + "_scores.txt", dateFormat.format(new Date()) + " | " + score);
    }

    private static void appendBadge(String name, String badge) {
        appendToFile(USER_DATA_FOLDER + name + "_badges.txt", dateFormat.format(new Date()) + " | " + badge);
    }

    private static Set<String> loadUserBadges(String name) {
        Set<String> badges = new HashSet<>();
        String file = USER_DATA_FOLDER + name + "_badges.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" \\| ");
                if (parts.length > 1) badges.add(parts[1]);
            }
        } catch (IOException ignored) {}
        return badges;
    }

    private static void displayBadges(String name, Set<String> badges) {
        System.out.println("\n=== Badges Earned ===");
        if (badges.isEmpty()) {
            System.out.println("None this time. Try harder, genius.");
        } else {
            for (String badge : badges) {
                if (BADGE_DEFINITIONS.containsKey(badge))
                    System.out.printf("- %s: %s\n", badge, BADGE_DEFINITIONS.get(badge).flavor);
            }
        }
    }

    private static void displayStats(String name) {
        List<Double> scores = loadScores(name);
        if (scores.isEmpty()) {
            System.out.println("No scores yet.");
            return;
        }
        double min = Collections.min(scores);
        double max = Collections.max(scores);
        double avg = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        System.out.printf("\nStats for %s:\n", name);
        System.out.printf("Lowest Ego: %.2f\n", min);
        System.out.printf("Highest Ego: %.2f\n", max);
        System.out.printf("Average Ego: %.2f\n", avg);

        System.out.println("\nEarned Badges:");
        Set<String> badges = loadUserBadges(name);
        displayBadges(name, badges);
    }

    private static List<Double> loadScores(String name) {
        List<Double> scores = new ArrayList<>();
        String file = USER_DATA_FOLDER + name + "_scores.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" \\| ");
                if (parts.length > 1) scores.add(Double.parseDouble(parts[1]));
            }
        } catch (IOException ignored) {}
        return scores;
    }

    private static void displayFullBadgeList(String name) {
        System.out.println("\n=== Full Badge List ===");
        Set<String> earned = loadUserBadges(name);
        for (Badge badge : BADGE_DEFINITIONS.values()) {
            long totalEarned = countBadgeEarners(badge.title);
            boolean hasIt = earned.contains(badge.title);
            System.out.printf("%s | Earned: %s | Times Earned Overall: %d | How to earn: %s | Flavor: %s\n",
                    badge.title, hasIt ? "YES" : "NO", totalEarned, badge.criteria, badge.flavor);
        }
    }

    private static long countBadgeEarners(String badge) {
        long count = 0;
        File folder = new File(USER_DATA_FOLDER);
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith("_badges.txt")) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.contains(" | " + badge)) count++;
                    }
                } catch (IOException ignored) {}
            }
        }
        return count;
    }

    private static void showScience(double heightInches, double ego) {
        System.out.println("\n=== How Your Ego Was Calculated ===");
        System.out.printf("Height in inches: %.2f\n", heightInches);
        System.out.printf("Formula: Ego = 5.892 * height_in_inches - 6.243\n");
        System.out.printf("Result: %.2f\n", ego);
    }

    static class Badge {
        String title, flavor, criteria;
        public Badge(String title, String flavor, String criteria) {
            this.title = title;
            this.flavor = flavor;
            this.criteria = criteria;
        }
    }
}
