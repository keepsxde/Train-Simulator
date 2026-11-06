package com.trains.sim;

import com.trains.network.Station;
import com.trains.routing.Route;

/**
 * A possible next destination for a vehicle, with everything a dispatch decision needs.
 * @param station the candidate destination
 * @param route how to get there from where the vehicle is now
 * @param demand units waiting at the vehicle's current station bound for this candidate
 * @param pickup units waiting at the candidate itself
 * @param staleness simulated seconds since the candidate was last called at
 * @param starving whether that staleness has passed the starvation limit
 */
public record Candidate(Station station, Route route, int demand, int pickup,
                        double staleness, boolean starving) {
}
