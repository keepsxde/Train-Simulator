package com.trains.sim;

import java.util.List;

/**
 * A rule for choosing where a vehicle that has finished its route should go next.
 * <p>
 *     The simulation scores every reachable station as a {@link Candidate} and leaves the choice
 *     to the strategy, so the policy can be swapped without touching the dispatching itself.
 * </p>
 */
public interface DispatchStrategy {

    /**
     * Picks the candidate a vehicle should be dispatched to
     * @param candidates every station the vehicle can reach, never empty
     * @return the chosen candidate, one of those supplied
     */
    Candidate choose(List<Candidate> candidates);
}
