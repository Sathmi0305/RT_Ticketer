import com.google.gson.Gson;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TicketingSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static List<Thread> vendorThreads = new ArrayList<>();
    private static List<Thread> customerThreads = new ArrayList<>();
    private static TicketPool ticketPool;
    private static TicketConfiguration config;
    private static boolean isRunning = false;
    private static final int VENDOR_COUNT = 2;
    private static final int CUSTOMER_COUNT = 4;

    private static void setupLogging() {
        try {
            FileHandler fileHandler = new FileHandler("ticketing_system.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            Logger.getLogger("").addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Could not create log file: " + e.getMessage());
        }
    }

    private static int getValidIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.println("Input must be between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static void saveConfigurationToFile(TicketConfiguration config) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("ticket_config.json")) {
            gson.toJson(config, writer);
            System.out.println("Configuration written to file.");
        } catch (IOException e) {
            System.out.println("Error saving configuration: " + e.getMessage());
        }
    }

    private static TicketConfiguration getTicketConfiguration() {
        try {
            System.out.println("Real-Time Event Ticketing System Configuration");

            // Total Tickets
            int totalTickets = getValidIntInput("Enter total number of tickets (1-1000): ", 1, 1000);

            // Ticket Release Rate
            int ticketReleaseRate = getValidIntInput("Enter ticket release rate per second (1-10): ", 1, 10);

            // Customer Retrieval Rate
            int customerRetrievalRate = getValidIntInput("Enter customer ticket retrieval rate per second (1-10): ", 1, 10);

            // Maximum Ticket Capacity
            int maxTicketCapacity = getValidIntInput("Enter maximum ticket capacity (1-2000): ", 1, 2000);

            // Create and save configuration
            TicketConfiguration config = new TicketConfiguration(
                    totalTickets, ticketReleaseRate, customerRetrievalRate, maxTicketCapacity
            );

            saveConfigurationToFile(config);

            return config;
        } catch (Exception e) {
            System.out.println("Error in configuration: " + e.getMessage());
            return null;
        }
    }

    private static void startTicketOperations() {
        if (isRunning) {
            System.out.println("Ticket operations are already running.");
            return;
        }

        ticketPool = new TicketPool(config.getMaxTicketCapacity(), config.getTotalTickets());

        vendorThreads.clear();
        customerThreads.clear();

//        assuming no of vendors to be 1
        for (int i = 0; i < VENDOR_COUNT; i++) {
            Vendor vendor = new Vendor(ticketPool, config.getTicketReleaseRate(), "Vendor-" + (i + 1));
            Thread vendorThread = new Thread(vendor);
            vendorThread.setName("Vendor "+(i+1));
            vendorThread.start();
            vendorThreads.add(vendorThread);
        }

//        assuming no of customers to be 5
        for (int i = 0; i < CUSTOMER_COUNT; i++) {
            Customer customer = new Customer(ticketPool, config.getCustomerRetrievalRate(), "Customer-" + (i + 1));
            Thread customerThread = new Thread(customer);
            customerThread.setName("customer "+(i+1));
            customerThread.start();
            customerThreads.add(customerThread);
        }


        isRunning = true;
        System.out.println("Ticket operations started with total tickets: " + config.getTotalTickets() +
                " and max capacity: " + config.getMaxTicketCapacity() +
                "\nRelease rate: " + config.getTicketReleaseRate() + " tickets/second" +
                "\nRetrieval rate: " + config.getCustomerRetrievalRate() + " tickets/second");
    }

    private static void stopTicketOperations() {
        if (!isRunning) {
            System.out.println("Ticket operations are not running.");
            return;
        }

        vendorThreads.forEach(Thread::interrupt);
        customerThreads.forEach(Thread::interrupt);

        try {
            for (Thread vendorThread : vendorThreads) {
                vendorThread.join();
            }
            for (Thread customerThread : customerThreads) {
                customerThread.join();
            }
        } catch (InterruptedException e) {
            System.out.println("Interruption during thread termination: " + e.getMessage());
        }

        System.out.println("Final ticket count for purchase: " + ticketPool.getTicketCount()
                + "\nFinal remaining ticket count: " + ticketPool.getRemainingTotalTickets());

        isRunning = false;
        vendorThreads.clear();
        customerThreads.clear();
        ticketPool = null;

        System.out.println("Ticket operations stopped.");

    }

    private static void displayMenu() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║       Ticket System Command Menu      ║");
        System.out.println("╚═══════════════════════════════════════╝");
        if (isRunning) {
            System.out.println("1 or STOP: Stop Ticket Operations");
        } else {
            System.out.println("1 or START: Start Ticket Operations");
        }
        System.out.println("2 or EXIT: Exit the Program");
        System.out.print("Choose Option: ");
    }



    public static void main(String[] args) {
        config = getTicketConfiguration();

        if (config == null) {
            System.out.println("Failed to create ticket configuration. Exiting");
            return;
        }

        boolean exit = false;

        while (!exit) {
            displayMenu();

            if (scanner.hasNextLine()) {
                String choice = scanner.nextLine().toUpperCase().trim(); // Read input and convert to uppercase

                switch (choice) {
                    case "1", "START", "STOP" -> {
                        if (isRunning) {
                            stopTicketOperations();
                        } else {
                            startTicketOperations();
                        }
                    }
                    case "2", "EXIT" -> {
                        if (isRunning) {
                            stopTicketOperations(); // Ensure operations stop before exiting
                        }
                        System.out.println("Exiting Ticketing System. Goodbye!");
                        return; // Terminate the program
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }
}