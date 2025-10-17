public class EgoCalculatorTest {

    public static void main(String[] args) {
        // Test height in all units: 5'6" = 66 inches = 167.64 cm
        double inches = 66;
        double feetInches = 5 * 12 + 6;
        double cm = 167.64;

        double egoInches = calculateEgo(inches);
        double egoFeetInches = calculateEgo(feetInches);
        double egoCm = calculateEgo(cm / 2.54);

        System.out.println("Ego (inches): " + egoInches);
        System.out.println("Ego (feet+inches): " + egoFeetInches);
        System.out.println("Ego (cm): " + egoCm);

        if (egoInches == egoFeetInches && egoFeetInches == egoCm) {
            System.out.println("PASS: All input methods produce the same Ego Score!");
        } else {
            System.out.println("FAIL: Inconsistent Ego Scores!");
        }
    }

    private static double calculateEgo(double heightInInches) {
        final double MULTIPLIER = 5.892;
        final double OFFSET = 6.243;
        return MULTIPLIER * heightInInches - OFFSET;
    }
}
