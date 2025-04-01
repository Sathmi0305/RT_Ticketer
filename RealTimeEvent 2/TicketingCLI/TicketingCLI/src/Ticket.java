import java.io.Serializable;

public class Ticket {
    private int TicketId;

    public Ticket(int ticketId) {
        TicketId = ticketId;
    }

    public void setTicketId(int ticketId) {
        TicketId = ticketId;
    }


    @Override
    public String toString() {
        return "Ticket{" +
                "TicketId=" + TicketId +
                '}';
    }

}
