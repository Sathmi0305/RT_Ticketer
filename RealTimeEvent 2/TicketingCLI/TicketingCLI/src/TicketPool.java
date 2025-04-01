import java.util.Vector;
import java.util.logging.Logger;

public class TicketPool {
    private final Vector<Ticket> tickets;
    private final int maxCapacity;
    private int remainingTotalTickets;
    private static final Logger LOGGER = Logger.getLogger(TicketPool.class.getName());

    public TicketPool(int maxCapacity, int totalTickets) {
        this.tickets = new Vector<>();
        this.maxCapacity = maxCapacity;
        this.remainingTotalTickets = totalTickets;
    }

    public synchronized boolean addTickets(Ticket ticket) {
        if (tickets.size() < maxCapacity && remainingTotalTickets > 0) {
            tickets.add(ticket);
            remainingTotalTickets--;
            LOGGER.info( Thread.currentThread().getName()+ " Ticket added: " + ticket);
            return true;
        }
        return false;
    }

    public synchronized Ticket removeTicket() {
        if (!tickets.isEmpty()) {
            Ticket ticket = tickets.remove(0);
            LOGGER.info("Ticket sold: " + ticket+ "to "+Thread.currentThread().getName());
            return ticket;
        }
        return null;
    }

    public  int getTicketCount() {
        return tickets.size();
    }

    public  int getRemainingTotalTickets() {
        return remainingTotalTickets;
    }
}
