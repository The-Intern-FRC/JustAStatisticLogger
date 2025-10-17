import java.io.*;
import java.util.*;

public class EgoCalculator {

    private static final double MULTIPLIER = 5.892;
    private static final double OFFSET = 6.243;
    private static final String LOG_FILE = "ego_log.txt";
    private static final String LEADERBOARD_FILE = "ego_leaderboard.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean playAgain = true;

        System.out.println("=== Ego Calculator ===");

        while (playAgain) {
            System.out.print("\nEnter your name: ");
            String name = scanner.nextLine();

            System.out.println("Choose height input method:");
            System.out.println("1: Inches");
            System.out.println("2: Feet + Inches");
            System.out.println("3: Centimeters");
            System.out.print("Your choice: ");
            int choice = scanner.nextInt();

            double heightInInches = 0;

            switch (choice) {
                case 1:
                    System.out.print("Enter your height in inches: ");
                    heightInInches = scanner.nextDouble();
                    break;
                case 2:
                    System.out.print("Enter feet: ");
                    int feet = scanner.nextInt();
                    System.out.print("Enter inches: ");
                    int inches = scanner.nextInt();
                    heightInInches = feet * 12 + inches;
                    break;
                case 3:
                    System.out.print("Enter height in cm: ");
                    double cm = scanner.nextDouble();
                    heightInInches = cm / 2.54;
                    break;
                default:
                    System.out.println("Invalid choice. Exiting.");
                    System.exit(1);
            }

            // Consume leftover newline
            scanner.nextLine();

            double ego = calculateEgo(heightInInches);
            String output = String.format("%s: Height = %.2f inches, Ego Score = %.2f",
                    name, heightInInches, ego);

            System.out.println("\n" + output);

            saveToLog(output);
            updateLeaderboard(name, ego);

            System.out.println("\n=== Ego Leaderboard ===");
            displayLeaderboard();

            // Ask to play again
            System.out.print("\nDo you want to calculate another Ego Score? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            playAgain = response.equals("y") || response.equals("yes");
        }

        System.out.println("\nThanks for playing! Ego Scores saved in " + LOG_FILE);
        scanner.close();
    }

    private static double calculateEgo(double heightInInches) {
        return MULTIPLIER * heightInInches - OFFSET;
    }

    private static void saveToLog(String output) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(output);
        } catch (IOException e) {
            System.out.println("Error saving to log: " + e.getMessage());
        }
    }

    private static void updateLeaderboard(String name, double ego) {
        Map<String, Double> leaderboard = new HashMap<>();

        // Read previous leaderboard if exists
        try (BufferedReader br = new BufferedReader(new FileReader(LEADERBOARD_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(": ");
                if (parts.length == 2) {
                    leaderboard.put(parts[0], Double.parseDouble(parts[1]));
                }
            }
        } catch (IOException ignored) {
        }

        // Update current user
        leaderboard.put(name, ego);

        // Sort leaderboard by descending ego
        List<Map.Entry<String, Double>> sorted = new ArrayList<>(leaderboard.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Write updated leaderboard
        try (PrintWriter pw = new PrintWriter(new FileWriter(LEADERBOARD_FILE))) {
            for (Map.Entry<String, Double> entry : sorted) {
                pw.printf("%s: %.2f%n", entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("Error updating leaderboard: " + e.getMessage());
        }
    }

    private static void displayLeaderboard() {
        try (BufferedReader br = new BufferedReader(new FileReader(LEADERBOARD_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("No leaderboard available yet.");
        }
    }
}
