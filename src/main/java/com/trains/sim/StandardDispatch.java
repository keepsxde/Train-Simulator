package com.trains.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * The default dispatch rule.
 * <p>
 *     A starving station beats everything, which is the fairness bound. Otherwise it is demand
 *     first, and route length only ever separates otherwise equal candidates.
 * </p>
 */
public final class StandardDispatch implements DispatchStrategy {
    private static final Comparator<Candidate> BY_PRIORITY =
            Comparator.comparing(Candidate::starving).reversed()
                    .thenComparing(Comparator.comparingInt(Candidate::demand).reversed())
                    .thenComparing(Comparator.comparingInt(Candidate::pickup).reversed())
                    .thenComparing(Comparator.comparingDouble(Candidate::staleness).reversed())
                    .thenComparingDouble(candidate -> candidate.route().getTotalLength());

    private final Random random;

    /**
     * @param random the source used to break ties between equally ranked candidates
     * @throws NullPointerException if random is null
     */
    public StandardDispatch(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public Candidate choose(List<Candidate> candidates) {
        List<Candidate> ranked = new ArrayList<>(candidates);

        // Shuffle before sorting
        Collections.shuffle(ranked, random);
        ranked.sort(BY_PRIORITY);

        return ranked.get(0);
    }
}
