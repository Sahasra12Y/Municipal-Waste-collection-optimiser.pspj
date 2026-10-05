import java.io.*;
import java.util.*;

class WasteBin {
    private String area;
    private int waste;
    private boolean collected;

    WasteBin(String area, int waste) {
        this.area = area;
        this.waste = waste;
        this.collected = false;
    }

    public String getArea() {
        return area;
    }

    public int getWaste() {
        return waste;
    }

    public void collect() {
        collected = true;
    }

    public void display() {
        System.out.println(area + " : " + waste + " kg");
    }
}

public class WasteOptimizer {

    // CO3: Method
    static int totalWaste(int[] waste) {
        int total = 0;
        for (int x : waste)
            total += x;
        return total;
    }

    // CO3: Recursion
    static int countBins(int[] waste, int i) {
        if (i == waste.length)
            return 0;
        return 1 + countBins(waste, i + 1);
    }

    // CO3: Search
    static int searchBin(int[] waste, int value) {
        for (int i = 0; i < waste.length; i++) {
            if (waste[i] == value)
                return i;
        }
        return -1;
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // ================= CO1 =================
        System.out.println("MUNICIPAL WASTE COLLECTION OPTIMISER");

        System.out.print("Enter waste in Bin 1: ");
        int w1 = sc.nextInt();

        double capacity = 100.0;
        double remaining = capacity - w1;

        System.out.println("Remaining capacity: " + remaining + " kg");

        // ================= CO2 =================
        if (w1 >= 80)
            System.out.println("Bin 1: URGENT COLLECTION");
        else if (w1 >= 50)
            System.out.println("Bin 1: COLLECTION SOON");
        else
            System.out.println("Bin 1: NORMAL");

        System.out.print("Enter collection priority (1-3): ");
        int priority = sc.nextInt();

        switch (priority) {
            case 1:
                System.out.println("Priority: High");
                break;
            case 2:
                System.out.println("Priority: Medium");
                break;
            case 3:
                System.out.println("Priority: Low");
                break;
            default:
                System.out.println("Invalid priority");
        }

        int[] waste = {w1, 60, 90, 45, 75};

        System.out.println("\nWaste levels:");
        for (int x : waste)
            System.out.println(x + " kg");

        // ================= CO3 =================
        System.out.println("\nTotal waste: "
                + totalWaste(waste) + " kg");

        System.out.println("Number of bins: "
                + countBins(waste, 0));

        int position = searchBin(waste, 90);

        if (position != -1)
            System.out.println("90 kg found at bin: " + (position + 1));

        // 2D Array
        int[][] weeklyWaste = {
            {40, 50, 60},
            {70, 80, 90}
        };

        System.out.println("Weekly waste data:");
        for (int i = 0; i < weeklyWaste.length; i++) {
            for (int j = 0; j < weeklyWaste[i].length; j++)
                System.out.print(weeklyWaste[i][j] + " ");
            System.out.println();
        }

        // ================= CO4 =================
        WasteBin bin1 = new WasteBin("Area A", w1);
        WasteBin bin2 = new WasteBin("Area B", 90);

        System.out.println("\nBin Objects:");
        bin1.display();
        bin2.display();

        bin1.collect();

        // Exception handling
        try {
            System.out.print("Enter a test waste value: ");
            int test = sc.nextInt();

            if (test < 0)
                throw new IllegalArgumentException("Waste cannot be negative");

            System.out.println("Valid waste value.");
        } catch (InputMismatchException e) {
            System.out.println("Please enter a number.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        // ================= CO5 =================
        String area = "Municipal Area A";

        StringBuilder report = new StringBuilder();
        report.append("Waste Collection Report\n");
        report.append(area).append("\n");
        report.append("Total Waste: ")
              .append(totalWaste(waste))
              .append(" kg\n");

        System.out.println("\n" + report);

        // Sorting
        Arrays.sort(waste);

        System.out.println("Sorted waste:");
        for (int x : waste)
            System.out.print(x + " ");

        // File writing
        try {
            FileWriter file = new FileWriter("waste.txt");
            file.write(report.toString());
            file.close();

            System.out.println("\nData saved to file.");

            // File reading
            BufferedReader reader =
                    new BufferedReader(new FileReader("waste.txt"));

            System.out.println("File content:");
            String line;

            while ((line = reader.readLine()) != null)
                System.out.println(line);

            reader.close();

        } catch (IOException e) {
            System.out.println("File error.");
        }

        // ================= CO6 =================
        ArrayList<Integer> wasteList = new ArrayList<>();

        for (int x : waste)
            wasteList.add(x);

        System.out.println("\nHigh waste bins:");

        wasteList.stream()
                .filter(x -> x >= 80)
                .forEach(x -> System.out.println(x + " kg"));

        int total = wasteList.stream()
                .mapToInt(Integer::intValue)
                .sum();

        System.out.println("Stream total: " + total + " kg");

        sc.close();
    }
}

                    

        

