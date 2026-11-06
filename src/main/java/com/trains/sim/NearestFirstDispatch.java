package com.trains.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Always heads for the closest reachable station, by route length.
 * <p>
 *     Ignores the starvation flag, so a distant station can go unserved for as long as nearer ones
 *     keep winning.
 * </p>
 */
public final class NearestFirstDispatch implements DispatchStrategy {
    private static final Comparator<Candidate> BY_DISTANCE =
            Comparator.comparingDouble(candidate -> candidate.route().getTotalLength());

    private final Random random;

    /**
     * @param random the source used to break ties between equally distant candidates
     * @throws NullPointerException if random is null
     */
    public NearestFirstDispatch(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public Candidate choose(List<Candidate> candidates) {
        List<Candidate> ranked = new ArrayList<>(candidates);

        // Shuffle before sorting
        Collections.shuffle(ranked, random);
        ranked.sort(BY_DISTANCE);

        return ranked.get(0);
    }
}
