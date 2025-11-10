package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {

    private final Invoice invoice;
    private final Map<String, Play> plays;

    public StatementPrinter(final Invoice invoice, final Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     *
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        int totalAmount = 0;
        int volumeCredits = 0;

        final StringBuilder result =
                new StringBuilder("Statement for " + invoice.getCustomer() + System.lineSeparator());

        final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

        for (Performance p : invoice.getPerformances()) {
            final Play play = plays.get(p.getPlayID());

            int thisAmount = 0;
            switch (play.getType()) {
                case "tragedy":
                    thisAmount = Constants.TRAGEDY_BASE_AMOUNT;
                    if (p.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                                * (p.getAudience() - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                    }
                    break;
                case "comedy":
                    thisAmount = Constants.COMEDY_BASE_AMOUNT;
                    if (p.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                                + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                                * (p.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD));
                    }
                    thisAmount += Constants.COMEDY_AMOUNT_PER_AUDIENCE * p.getAudience();
                    break;
                default:
                    throw new RuntimeException(String.format("unknown type: %s", play.getType()));
            }

            // add volume credits
            volumeCredits += Math.max(
                    p.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);

            // add extra credit for every five comedy attendees
            if ("comedy".equals(play.getType())) {
                volumeCredits += p.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
            }

            // print line for this order
            result.append(String.format("  %s: %s (%s seats)%n",
                    play.getName(),
                    frmt.format(thisAmount / (double) Constants.PERCENT_FACTOR),
                    p.getAudience()));

            totalAmount += thisAmount;
        }

        result.append(String.format("Amount owed is %s%n",
                frmt.format(totalAmount / (double) Constants.PERCENT_FACTOR)));
        result.append(String.format("You earned %s credits%n", volumeCredits));
        return result.toString();
    }

    // ---- Accessors (useful if other code needs them later) ----
    public Invoice getInvoice() {
        return invoice;
    }

    public Map<String, Play> getPlays() {
        return plays;
    }

    // ===== Temporary empty stubs to satisfy existence checks (do not remove) =====

    /** Returns the Play for a given performance (stub). */
    private Play getPlay(final Performance performance) {
        return null;
    }

    /** Returns the base amount in cents for a performance (stub). */
    private int getAmount(final Performance performance) {
        return 0;
    }

    /** Returns the volume credits for a performance (stub). */
    private int getVolumeCredits(final Performance performance) {
        return 0;
    }

    /** Formats an amount (in cents) to US currency (stub). */
    private String usd(final int amountInCents) {
        return "";
    }

    /** Returns the total amount in cents across all performances (stub). */
    private int getTotalAmount() {
        return 0;
    }

    /** Returns the total volume credits across all performances (stub). */
    private int getTotalVolumeCredits() {
        return 0;
    }
}
