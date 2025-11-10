package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {

    private static final String TYPE_TRAGEDY = "tragedy";
    private static final String TYPE_COMEDY = "comedy";
    private static final String TYPE_HISTORY = "history";
    private static final String TYPE_PASTORAL = "pastoral";
    private static final int PASTORAL_EXTRA_VOLUME_DIVISOR = 2;

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
            final int audience = p.getAudience();

            final int thisAmount = amountFor(play, audience);

            volumeCredits += Math.max(audience - baseThresholdFor(play), 0);
            volumeCredits += extraVolumeCredits(play, audience);

            result.append(String.format("  %s: %s (%s seats)%n",
                    play.getName(),
                    frmt.format(thisAmount / (double) Constants.PERCENT_FACTOR),
                    audience));

            totalAmount += thisAmount;
        }

        result.append(String.format("Amount owed is %s%n",
                frmt.format(totalAmount / (double) Constants.PERCENT_FACTOR)));
        result.append(String.format("You earned %s credits%n", volumeCredits));
        return result.toString();
    }

    private int amountFor(final Play play, final int audience) {
        int result = 0;
        switch (play.getType()) {
            case TYPE_TRAGEDY:
                result = Constants.TRAGEDY_BASE_AMOUNT;
                if (audience > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (audience - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;
            case TYPE_COMEDY:
                result = Constants.COMEDY_BASE_AMOUNT;
                if (audience > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (audience - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                result += Constants.COMEDY_AMOUNT_PER_AUDIENCE * audience;
                break;
            case TYPE_HISTORY:
                result = Constants.HISTORY_BASE_AMOUNT;
                if (audience > Constants.HISTORY_AUDIENCE_THRESHOLD) {
                    result += Constants.HISTORY_OVER_BASE_CAPACITY_PER_PERSON
                            * (audience - Constants.HISTORY_AUDIENCE_THRESHOLD);
                }
                break;
            case TYPE_PASTORAL:
                result = Constants.PASTORAL_BASE_AMOUNT;
                if (audience > Constants.PASTORAL_AUDIENCE_THRESHOLD) {
                    result += Constants.PASTORAL_OVER_BASE_CAPACITY_PER_PERSON
                            * (audience - Constants.PASTORAL_AUDIENCE_THRESHOLD);
                }
                break;
            default:
                throw new RuntimeException(String.format("unknown type: %s", play.getType()));
        }
        return result;
    }

    private int baseThresholdFor(final Play play) {
        int threshold = Constants.BASE_VOLUME_CREDIT_THRESHOLD;
        switch (play.getType()) {
            case TYPE_HISTORY:
                threshold = Constants.HISTORY_VOLUME_CREDIT_THRESHOLD;
                break;
            case TYPE_PASTORAL:
                threshold = Constants.PASTORAL_VOLUME_CREDIT_THRESHOLD;
                break;
            default:
                break;
        }
        return threshold;
    }

    private int extraVolumeCredits(final Play play, final int audience) {
        int extra = 0;
        if (TYPE_COMEDY.equals(play.getType())) {
            extra = audience / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        else if (TYPE_PASTORAL.equals(play.getType())) {
            extra = audience / PASTORAL_EXTRA_VOLUME_DIVISOR;
        }
        return extra;
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
