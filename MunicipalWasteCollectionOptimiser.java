import java.util.Scanner;

public class MunicipalWasteCollectionOptimiser {

    // Maximum number of bins
    static final int MAX_BINS = 10;

    // Bin information
    static String[] binId = new String[MAX_BINS];
    static String[] zone = new String[MAX_BINS];
    static String[] type = new String[MAX_BINS];

    static double[] capacity = new double[MAX_BINS];
    static double[] fillLevel = new double[MAX_BINS];

    // Distance matrix between bins
    static double[][] distance = new double[MAX_BINS][MAX_BINS];

    static int binCount = 0;

    // Truck capacity
    static double truckCapacity = 1000.0;

    // Collection statistics
    static double totalCollected = 0;
    static double recyclingCollected = 0;
    static double landfillCollected = 0;

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        createSampleBins();
        createDistanceMatrix();

        int choice;

        do {
            System.out.println("\n====================================");
            System.out.println("       MUNICIPAL WASTE AUTHORITY");
            System.out.println("====================================");
            System.out.println("1) Update fills");
            System.out.println("2) Build route");
            System.out.println("3) Collect");
            System.out.println("4) Report");
            System.out.println("5) Exit");
            System.out.print("Choice: ");

            choice = scanner.nextInt();

            switch (choice) {

                case 1:
                    updateFills();
                    break;

                case 2:
                    buildRoute();
                    break;

                case 3:
                    collectWaste();
                    break;

                case 4:
                    report();
                    break;

                case 5:
                    System.out.println("Exiting Waste Authority...");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 5);

        scanner.close();
    }

    // ------------------------------------------------
    // SAMPLE BIN DATA
    // ------------------------------------------------

    static void createSampleBins() {

        addBin("B01", "North", "general", 500, 60);
        addBin("B02", "North", "recycling", 400, 75);
        addBin("B03", "South", "organic", 600, 45);
        addBin("B04", "East", "general", 500, 88);
        addBin("B05", "East", "recycling", 400, 55);
        addBin("B06", "West", "organic", 600, 65);
        addBin("B07", "North", "general", 500, 95);
        addBin("B08", "South", "recycling", 400, 35);
        addBin("B09", "West", "general", 500, 72);
        addBin("B10", "East", "organic", 600, 40);
    }

    static void addBin(String id, String binZone, String binType,
                       double binCapacity, double fill) {

        if (binCount < MAX_BINS) {

            binId[binCount] = id;
            zone[binCount] = binZone;
            type[binCount] = binType;
            capacity[binCount] = binCapacity;
            fillLevel[binCount] = fill;

            binCount++;
        }
    }

    // ------------------------------------------------
    // W6 - DISTANCE MATRIX
    // ------------------------------------------------

    static void createDistanceMatrix() {

        for (int i = 0; i < binCount; i++) {

            for (int j = 0; j < binCount; j++) {

                if (i == j) {
                    distance[i][j] = 0;
                } else {
                    // Example distance values
                    distance[i][j] =
                            2 + Math.abs(i - j) * 1.2;
                }
            }
        }
    }

    // ------------------------------------------------
    // W1 + W2 + W3
    // UPDATE BIN FILLS
    // ------------------------------------------------

    static void updateFills() {

        System.out.print("Enter bin ID: ");
        String id = scanner.next();

        int index = findBin(id);

        if (index == -1) {
            System.out.println("Bin not found.");
            return;
        }

        System.out.print("Enter new fill percentage: ");
        double newFill = scanner.nextDouble();

        if (newFill < 0) {
            System.out.println("Fill cannot be negative.");
            return;
        }

        fillLevel[index] = newFill;

        System.out.println(
                "Updated " + binId[index]
                        + " fill to " + fillLevel[index] + "%"
        );
    }

    // ------------------------------------------------
    // W3 + W4
    // BUILD ROUTE
    // ------------------------------------------------

    static void buildRoute() {

        double threshold = 70.0;

        System.out.println("\nTruck capacity: "
                + truckCapacity + " L");

        System.out.println("Collection threshold: "
                + threshold + "%");

        // Temporary route
        int[] route = new int[MAX_BINS];
        int routeCount = 0;

        double currentLoad = 0;

        System.out.println("\nPriority bins:");

        // Select bins above threshold
        for (int i = 0; i < binCount; i++) {

            boolean overflowing = fillLevel[i] > 100;

            if (fillLevel[i] >= threshold || overflowing) {

                double volume =
                        capacity[i] * fillLevel[i] / 100;

                if (currentLoad + volume <= truckCapacity) {

                    route[routeCount] = i;
                    routeCount++;

                    currentLoad += volume;

                    System.out.println(
                            binId[i]
                            + "(" + fillLevel[i] + "%)"
                    );
                }
            }
        }

        if (routeCount == 0) {
            System.out.println("No bins require collection.");
            return;
        }

        // W5 - recursive nearest-stop ordering
        orderRouteRecursively(route, routeCount, 0);

        System.out.println("\nRoute:");

        System.out.print("Depot");

        for (int i = 0; i < routeCount; i++) {
            System.out.print(" -> " + binId[route[i]]);
        }

        System.out.println();

        double totalDistance =
                calculateRouteDistance(route, routeCount);

        System.out.println(
                "Route distance: "
                        + totalDistance + " km"
        );

        System.out.println(
                "Truck load: "
                        + currentLoad + " L / "
                        + truckCapacity + " L"
        );
    }

    // ------------------------------------------------
    // W5 - RECURSIVE NEAREST STOP ORDERING
    // ------------------------------------------------

    static void orderRouteRecursively(
            int[] route,
            int routeCount,
            int currentPosition) {

        if (currentPosition >= routeCount - 1) {
            return;
        }

        int nearestPosition = currentPosition + 1;

        double shortestDistance =
                distance[route[currentPosition]]
                        [route[nearestPosition]];

        for (int i = currentPosition + 1;
             i < routeCount;
             i++) {

            double currentDistance =
                    distance[route[currentPosition]]
                            [route[i]];

            if (currentDistance < shortestDistance) {

                shortestDistance = currentDistance;
                nearestPosition = i;
            }
        }

        // Swap nearest bin
        int temp = route[currentPosition];

        route[currentPosition] =
                route[nearestPosition];

        route[nearestPosition] = temp;

        // Recursive call
        orderRouteRecursively(
                route,
                routeCount,
                currentPosition + 1
        );
    }

    // ------------------------------------------------
    // W6 - CALCULATE ROUTE DISTANCE
    // ------------------------------------------------

    static double calculateRouteDistance(
            int[] route,
            int routeCount) {

        double totalDistance = 0;

        // Depot to first bin
        if (routeCount > 0) {
            totalDistance += 2.0;
        }

        for (int i = 0; i < routeCount - 1; i++) {

            totalDistance +=
                    distance[route[i]]
                            [route[i + 1]];
        }

        // Last bin back to depot
        if (routeCount > 0) {
            totalDistance += 2.0;
        }

        return totalDistance;
    }

    // ------------------------------------------------
    // W3 + W4
    // COLLECT WASTE
    // ------------------------------------------------

    static void collectWaste() {

        System.out.print("Enter bin ID to collect: ");
        String id = scanner.next();

        int index = findBin(id);

        if (index == -1) {
            System.out.println("Bin not found.");
            return;
        }

        double volume =
                capacity[index] *
                fillLevel[index] / 100;

        if (volume <= 0) {
            System.out.println("Bin is already empty.");
            return;
        }

        totalCollected += volume;

        if (type[index].equals("recycling")) {

            recyclingCollected += volume;

        } else {

            landfillCollected += volume;
        }

        // Empty the bin
        fillLevel[index] = 0;

        System.out.println(
                "Collected " + volume
                        + " L from " + binId[index]
        );

        System.out.println(
                "Bin " + binId[index]
                        + " is now empty."
        );
    }

    // ------------------------------------------------
    // W1 + W2
    // REPORT
    // ------------------------------------------------

    static void report() {

        System.out.println("\n====================================");
        System.out.println("          WASTE REPORT");
        System.out.println("====================================");

        System.out.println(
                "Total collected: "
                        + totalCollected + " L"
        );

        System.out.println(
                "Recycling: "
                        + recyclingCollected + " L"
        );

        System.out.println(
                "Landfill: "
                        + landfillCollected + " L"
        );

        double diversionRate = 0;

        if (totalCollected > 0) {

            diversionRate =
                    recyclingCollected /
                    totalCollected;
        }

        System.out.printf(
                "Diversion rate: %.2f%n",
                diversionRate
        );

        System.out.println(
                "Diversion percentage: "
                        + (diversionRate * 100) + "%"
        );

        System.out.println("\nBin Status:");

        for (int i = 0; i < binCount; i++) {

            boolean overflowing =
                    fillLevel[i] > 100;

            String priority;

            if (overflowing) {
                priority = "URGENT";
            } else if (fillLevel[i] >= 70) {
                priority = "HIGH";
            } else if (fillLevel[i] >= 50) {
                priority = "MEDIUM";
            } else {
                priority = "LOW";
            }

            System.out.println(
                    binId[i]
                    + " | Zone: " + zone[i]
                    + " | Type: " + type[i]
                    + " | Fill: " + fillLevel[i] + "%"
                    + " | Priority: " + priority
            );
        }
    }

    // ------------------------------------------------
    // FIND BIN
    // ------------------------------------------------

    static int findBin(String id) {

        for (int i = 0; i < binCount; i++) {

            if (binId[i].equalsIgnoreCase(id)) {
                return i;
            }
        }

        return -1;
    }
}


