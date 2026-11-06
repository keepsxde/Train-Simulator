package com.trains.sim;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Picks any reachable station with equal probability.
 * <p>
 *     Ignores demand, distance and the starvation
 * </p>
 */
public final class RandomDispatch implements DispatchStrategy {
    private final Random random;

    /**
     * @param random the source the choice is drawn from
     * @throws NullPointerException if random is null
     */
    public RandomDispatch(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public Candidate choose(List<Candidate> candidates) {
        return candidates.get(random.nextInt(candidates.size()));
    }
}
