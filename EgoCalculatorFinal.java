import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class EgoCalculatorFinal {

    private static final String USER_DATA_FOLDER = "users/";
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static final Map<String, Badge> BADGE_DEFINITIONS = new LinkedHashMap<>();
    static {
        BADGE_DEFINITIONS.put("Mini but Mighty", new Badge("Mini but Mighty", "Ego < 60", "Small but packs a punch. Who knew?"));
        BADGE_DEFINITIONS.put("Rising Star", new Badge("Rising Star", "60 ≤ Ego < 61", "Modestly competent. Shine, little one."));
        BADGE_DEFINITIONS.put("Confident Programmer", new Badge("Confident Programmer", "61 ≤ Ego < 70", "Swagger in code. Not cocky… okay, maybe a little."));
        BADGE_DEFINITIONS.put("Sky High Ego", new Badge("Sky High Ego", "70 ≤ Ego < 83", "Tall enough to see over your ego… barely."));
        BADGE_DEFINITIONS.put("Stack Overflowing", new Badge("Stack Overflowing", "Ego ≥ 83", "Ego > Sanity. Maybe tone it down before you break something. You already crashed Stack Overflow."));
        BADGE_DEFINITIONS.put("Napoleon Complex", new Badge("Napoleon Complex", "Name = Napoleon", "The ego makes up the height difference."));
        BADGE_DEFINITIONS.put("The Intern", new Badge("The Intern", "Name = Intern & Ego = 62", "There Can Only Be One."));
        BADGE_DEFINITIONS.put("Self-Conscious", new Badge("Self-Conscious", "Skipped leaderboard", "You didn’t save to the leaderboard. We noticed."));
        BADGE_DEFINITIONS.put("Mirror Mirror", new Badge("Mirror Mirror", "View profile", "Viewing your own profile. Reflect much?"));
        BADGE_DEFINITIONS.put("How Did We End Up Here?", new Badge("How Did We End Up Here?", "Viewed calculation", "You wanted to see how the score was calculated."));
        BADGE_DEFINITIONS.put("Egomaniac", new Badge("Egomaniac", "Calculated ego 10 times", "Calculated ego 10 times. We get it already."));
    }

    public static void main(String[] args) {
        new File(USER_DATA_FOLDER).mkdirs();
        System.out.println("=== Welcome to the Ego Calculator ===");
        System.out.print("Enter your name: ");
        String name = SCANNER.nextLine().trim();

        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Calculate Ego");
            System.out.println("2. View Profile / Achievements");
            System.out.println("3. View Full Badge List");
            System.out.println("4. Leaderboard");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1" -> calculateEgo(name);
                case "2" -> viewProfile(name);
                case "3" -> displayFullBadgeList(name);
                case "4" -> displayLeaderboard();
                case "5" -> running = false;
                default -> System.out.println("Invalid option, try again.");
            }
        }
        System.out.println("Thanks for playing!");
    }

    private static void calculateEgo(String name) {
        boolean continueCalculating = true;
        while (continueCalculating) {
            System.out.println("\n--- Ego Calculation ---");
            double heightInches = 0;
            while (true) {
                System.out.println("Enter height in: 1) inches  2) feet & inches  3) cm");
                String option = SCANNER.nextLine();
                try {
                    switch (option) {
                        case "1" -> {
                            System.out.print("Height (inches): ");
                            heightInches = Double.parseDouble(SCANNER.nextLine());
                        }
                        case "2" -> {
                            System.out.print("Feet: ");
                            double ft = Double.parseDouble(SCANNER.nextLine());
                            System.out.print("Inches: ");
                            double in = Double.parseDouble(SCANNER.nextLine());
                            heightInches = ft * 12 + in;
                        }
                        case "3" -> {
                            System.out.print("Height (cm): ");
                            double cm = Double.parseDouble(SCANNER.nextLine());
                            heightInches = cm / 2.54;
                        }
                        default -> {
                            System.out.println("Invalid choice.");
                            continue;
                        }
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number, try again.");
                }
            }

            double base = (2 * 5.892) * heightInches - 6.243;
            boolean isProgrammerSpecial = name.equalsIgnoreCase("Michael") || name.equalsIgnoreCase("Adrian");

            double ego = (base * 4.0 / 5.0) / 10.0;
            if (isProgrammerSpecial) ego += 14.04;
            ego = Math.ceil(ego);

            System.out.printf("\nYour Ego Score: %.0f\n", ego);

            if (ego < 60) System.out.println("A little self-confidence wouldn't hurt. Neither would some platform shoes.");
            else if (ego < 70) System.out.println("Decent. Not enough to break anything, though.");
            else if (ego < 84) System.out.println("Careful, your ego is starting to rival your height.");
            else System.out.println("Wow, ego over your own head, somehow. Tone it down before a plane flies into your confidence.");

            System.out.print("\nView how your score is calculated? (y/n): ");
            String viewCalc = SCANNER.nextLine().trim().toLowerCase();
            if (viewCalc.equals("y")) showScience(heightInches, ego, isProgrammerSpecial);

            System.out.print("Save this score to leaderboard? (y/n): ");
            boolean save = SCANNER.nextLine().trim().equalsIgnoreCase("y");
            saveScore(name, ego, save);

            checkBadges(name, ego);

            System.out.print("\nWould you like to calculate another ego score? (y/n): ");
            continueCalculating = SCANNER.nextLine().trim().equalsIgnoreCase("y");
        }
        System.out.println("Returning to main menu...");
    }

    private static void saveScore(String name, double ego, boolean save) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_DATA_FOLDER + name + "_scores.txt", true))) {
            String timestamp = FORMAT.format(new Date());
            bw.write(String.format("%s | %.0f | %s\n", timestamp, ego, save ? "Saved" : "Not Saved"));
        } catch (IOException e) {
            System.out.println("Error saving score.");
        }
    }

    private static void checkBadges(String name, double ego) {
        if (name.equalsIgnoreCase("Napoleon")) triggerBadge(name, "Napoleon Complex");
        if ((name.equalsIgnoreCase("Intern") || name.equalsIgnoreCase("The Intern") || name.equalsIgnoreCase("Ruthie")) && Math.abs(ego - 382.63) < 0.01)
            triggerBadge(name, "The Intern");

        if (ego < 60) triggerBadge(name, "Mini but Mighty");
        else if (ego < 61) triggerBadge(name, "Rising Star");
        else if (ego < 70) triggerBadge(name, "Confident Programmer");
        else if (ego < 83) triggerBadge(name, "Sky High Ego");
        else if (ego >= 83) triggerBadge(name, "Stack Overflowing");
    }

    private static void triggerBadge(String name, String badgeTitle) {
        Set<String> earned = loadUserBadges(name);
        if (earned.contains(badgeTitle)) return;
        earned.add(badgeTitle);
        String timestamp = FORMAT.format(new Date());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_DATA_FOLDER + name + "_badges.txt", true))) {
            bw.write(timestamp + " | " + badgeTitle + "\n");
        } catch (IOException ignored) {}
    }

    private static Set<String> loadUserBadges(String name) {
        Set<String> badges = new LinkedHashSet<>();
        File f = new File(USER_DATA_FOLDER + name + "_badges.txt");
        if (!f.exists()) return badges;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                badges.add(line.split(" \\| ")[1]);
            }
        } catch (IOException ignored) {}
        return badges;
    }

    private static List<Double> loadScores(String name) {
        List<Double> scores = new ArrayList<>();
        File f = new File(USER_DATA_FOLDER + name + "_scores.txt");
        if (!f.exists()) return scores;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" \\| ");
                scores.add(Double.parseDouble(parts[1]));
            }
        } catch (IOException ignored) {}
        return scores;
    }

    private static void viewProfile(String name) {
        boolean continueViewing = true;
        while (continueViewing) {
            System.out.println("\n=== Profile / Achievements ===");
            triggerBadge(name, "Mirror Mirror");

            List<Double> scores = loadScores(name);
            if (!scores.isEmpty()) {
                System.out.printf("Lowest Ego: %.0f | Highest Ego: %.0f | Average Ego: %.0f\n",
                        Collections.min(scores),
                        Collections.max(scores),
                        scores.stream().mapToDouble(d -> d).average().orElse(0));
            } else System.out.println("No scores yet.");

            System.out.println("\nEarned Badges:");
            displayBadges(name, loadUserBadges(name));

            System.out.print("\nReturn to main menu? (y/n): ");
            continueViewing = !SCANNER.nextLine().trim().equalsIgnoreCase("y");
        }
    }

    private static void displayBadges(String name, Set<String> badges) {
        for (String badgeTitle : badges) {
            Badge b = BADGE_DEFINITIONS.get(badgeTitle);
            String dateEarned = getBadgeTimestamp(name, badgeTitle);
            System.out.printf("%s: %s (Earned: %s)\n", b.title, b.flavor, dateEarned);
        }
    }

    private static String getBadgeTimestamp(String name, String badge) {
        String file = USER_DATA_FOLDER + name + "_badges.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(" | " + badge)) return line.split(" \\| ")[0];
            }
        } catch (IOException ignored) {}
        return "-";
    }

    private static void displayFullBadgeList(String name) {
        System.out.println("\n=== Full Badge List ===\n");
        Set<String> earned = loadUserBadges(name);
        System.out.printf("%-25s | %-25s | %-40s | %-7s | %-19s | %-5s\n",
                "Badge Name", "How to Earn", "Flavor Text", "Earned?", "Date Earned", "Count");
        System.out.println("-".repeat(145));

        for (Badge b : BADGE_DEFINITIONS.values()) {
            boolean hasIt = earned.contains(b.title);
            String date = hasIt ? getBadgeTimestamp(name, b.title) : "-";
            long total = countBadgeEarners(b.title);
            System.out.printf("%-25s | %-25s | %-40s | %-7s | %-19s | %-5d\n",
                    b.title, b.criteria, b.flavor, hasIt ? "YES" : "NO", date, total);
        }
        triggerBadge(name, "Are you sure that you should be aspiring towards these?");
        System.out.println("Returning to main menu...");
    }

    private static long countBadgeEarners(String badge) {
        long count = 0;
        File folder = new File(USER_DATA_FOLDER);
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith("_badges.txt")) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) if (line.contains(" | " + badge)) count++;
                } catch (IOException ignored) {}
            }
        }
        return count;
    }

    private static void displayLeaderboard() {
        System.out.println("\n=== Leaderboard ===");
        File folder = new File(USER_DATA_FOLDER);
        List<LeaderboardEntry> entries = new ArrayList<>();

        for (File file : folder.listFiles()) {
            if (file.getName().endsWith("_scores.txt")) {
                String name = file.getName().replace("_scores.txt", "");
                List<Double> scores = loadScores(name);
                if (!scores.isEmpty()) entries.add(new LeaderboardEntry(name, Collections.max(scores)));
            }
        }

        entries.sort((a, b) -> Double.compare(b.score, a.score));
        System.out.printf("%-15s | %-10s\n", "Name", "Top Ego");
        System.out.println("-".repeat(30));
        for (LeaderboardEntry e : entries) {
            System.out.printf("%-15s | %-10.0f\n", e.name, e.score);
        }
        System.out.println("Returning to main menu...");
    }

    private static void showScience(double heightInches, double ego, boolean isProgrammerSpecial) {
        System.out.println("\n=== How Your Ego Was Calculated ===");
        System.out.printf("Height in inches: %.2f\n", heightInches);
        if (isProgrammerSpecial) {
            System.out.println("Formula: Ego = ceil(((2(5.892) * height_in_inches - 6.243) * 4/5 ÷ 10) + 14.04)");
            System.out.println("Explanation: This includes a bonus based on the difference between 62.43 and 58.92");
            System.out.println("multiplied by the total number of times 5892 programmers have violated GitHub Terms of Service (4),");
            System.out.println("to account for the noticeably heightened ego of tall FRC programmers such as Michael and Adrian.");
        } else {
            System.out.println("Formula: Ego = ceil((2(5.892) * height_in_inches - 6.243) * 4/5 ÷ 10)");
        }
        System.out.printf("Result: %.0f\n", ego);
    }

    static class Badge {
        String title, criteria, flavor;
        public Badge(String title, String criteria, String flavor) {
            this.title = title;
            this.criteria = criteria;
            this.flavor = flavor;
        }
    }

    static class LeaderboardEntry {
        String name;
        double score;
        public LeaderboardEntry(String name, double score) {
            this.name = name;
            this.score = score;
        }
    }
}
