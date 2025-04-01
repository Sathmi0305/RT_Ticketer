import java.util.UUID;
import java.util.logging.Logger;

public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int releaseRate;
    private final String vendorName;
    private static final Logger LOGGER = Logger.getLogger(Vendor.class.getName());

    public Vendor(TicketPool ticketPool, int releaseRate, String vendorName) {
        this.ticketPool = ticketPool;
        this.releaseRate = releaseRate;
        this.vendorName = vendorName;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000 / releaseRate);
                Ticket ticket = new Ticket(UUID.randomUUID().hashCode());
                if (!ticketPool.addTickets(ticket)) {
                    LOGGER.warning("Vendor " + vendorName + ": Ticket pool is full or no more tickets to release.");
                }
            }
        } catch (InterruptedException e) {
            LOGGER.info("Vendor " + vendorName + " interrupted.");
        }
    }
}
