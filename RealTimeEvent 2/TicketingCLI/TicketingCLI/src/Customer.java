import java.util.logging.Logger;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int retrievalRate;
    private final String customerName;
    private static final Logger LOGGER = Logger.getLogger(Customer.class.getName());

    public Customer(TicketPool ticketPool, int retrievalRate, String customerName) {
        this.ticketPool = ticketPool;
        this.retrievalRate = retrievalRate;
        this.customerName = customerName;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000 / retrievalRate);
                Ticket ticket = ticketPool.removeTicket();
                if (ticket == null) {
                    LOGGER.info("Customer " + customerName + ": No tickets available.");
                }
            }
        } catch (InterruptedException e) {
            LOGGER.info("Customer " + customerName + " interrupted.");
        }
    }
}
