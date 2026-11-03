package com.trains.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Heads for the station that has gone longest without a visit.
 * <p>
 *     Demand and distance are ignored. A starving station is by definition the stalest
 * </p>
 */
public final class LongestWaitingDispatch implements DispatchStrategy {
    private static final Comparator<Candidate> BY_STALENESS =
            Comparator.comparingDouble(Candidate::staleness).reversed();

    private final Random random;

    /**
     * @param random the source used to break ties between equally stale candidates
     * @throws NullPointerException if random is null
     */
    public LongestWaitingDispatch(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public Candidate choose(List<Candidate> candidates) {
        List<Candidate> ranked = new ArrayList<>(candidates);

        // Shuffle before sorting
        Collections.shuffle(ranked, random);
        ranked.sort(BY_STALENESS);

        return ranked.get(0);
    }
}
