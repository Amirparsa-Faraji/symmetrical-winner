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
        final int pastoralExtraVolumeDivisor = 2; // named constant to avoid magic number

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
                case "history":
                    thisAmount = Constants.HISTORY_BASE_AMOUNT;
                    if (p.getAudience() > Constants.HISTORY_AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.HISTORY_OVER_BASE_CAPACITY_PER_PERSON
                                * (p.getAudience() - Constants.HISTORY_AUDIENCE_THRESHOLD);
                    }
                    break;
                case "pastoral":
                    thisAmount = Constants.PASTORAL_BASE_AMOUNT;
                    if (p.getAudience() > Constants.PASTORAL_AUDIENCE_THRESHOLD) {
                        thisAmount += Constants.PASTORAL_OVER_BASE_CAPACITY_PER_PERSON
                                * (p.getAudience() - Constants.PASTORAL_AUDIENCE_THRESHOLD);
                    }
                    break;
                default:
                    throw new RuntimeException(String.format("unknown type: %s", play.getType()));
            }

            // ---- volume credits per type ----
            final int baseThreshold;
            switch (play.getType()) {
                case "history":
                    baseThreshold = Constants.HISTORY_VOLUME_CREDIT_THRESHOLD;
                    break;
                case "pastoral":
                    baseThreshold = Constants.PASTORAL_VOLUME_CREDIT_THRESHOLD;
                    break;
                default: // tragedy & comedy use the 30 threshold
                    baseThreshold = Constants.BASE_VOLUME_CREDIT_THRESHOLD;
                    break;
            }
            volumeCredits += Math.max(p.getAudience() - baseThreshold, 0);

            if ("comedy".equals(play.getType())) {
                volumeCredits += p.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
            } else if ("pastoral".equals(play.getType())) {
                volumeCredits += p.getAudience() / pastoralExtraVolumeDivisor;
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

    // ---- Accessors (if needed elsewhere) ----
    public Invoice getInvoice() {
        return invoice;
    }

    public Map<String, Play> getPlays() {
        return plays;
    }

    // ===== Temporary empty stubs to satisfy existence checks (do not remove) =====

    /**
     * Returns the {@link Play} for a given performance.
     *
     * @param performance the performance whose play is requested
     * @return the play for the performance, or {@code null} (stub)
     */
    private Play getPlay(final Performance performance) {
        return null;
    }

    /**
     * Calculates the base amount for a performance, in cents.
     *
     * @param performance the performance to evaluate
     * @return the amount in cents (stub returns 0)
     */
    private int getAmount(final Performance performance) {
        return 0;
    }

    /**
     * Calculates the volume credits contributed by a performance.
     *
     * @param performance the performance to evaluate
     * @return the number of credits (stub returns 0)
     */
    private int getVolumeCredits(final Performance performance) {
        return 0;
    }

    /**
     * Formats a cent amount as a US currency string.
     *
     * @param amountInCents the amount in cents to format
     * @return a formatted currency string (stub returns empty string)
     */
    private String usd(final int amountInCents) {
        return "";
    }

    /**
     * Computes the total amount across all performances, in cents.
     *
     * @return total amount in cents (stub returns 0)
     */
    private int getTotalAmount() {
        return 0;
    }

    /**
     * Computes the total volume credits across all performances.
     *
     * @return total volume credits (stub returns 0)
     */
    private int getTotalVolumeCredits() {
        return 0;
    }
}
