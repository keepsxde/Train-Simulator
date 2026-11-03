package com.trains.sim;

import com.trains.network.Network;
import com.trains.network.Station;
import com.trains.routing.Router;
import com.trains.utils.GridPos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DispatchStrategyTest {
    private static final int PICKS = 200;

    private Router router;
    private Station origin;
    private Station near;
    private Station sameDistanceAsNear;
    private Station far;

    // every station is joined straight to the origin, so each route is a single segment
    @BeforeEach
    void setUp() {
        Network network = new Network();
        origin = network.addStation(new GridPos(0, 0), 10);
        near = network.addStation(new GridPos(10, 0), 10);
        sameDistanceAsNear = network.addStation(new GridPos(0, 10), 10);
        far = network.addStation(new GridPos(30, 30), 10);
        network.connectNodes(origin, near);
        network.connectNodes(origin, sameDistanceAsNear);
        network.connectNodes(origin, far);
        router = new Router(network);
    }

    private Candidate candidate(Station station, int demand, int pickup, double staleness, boolean starving) {
        return new Candidate(station, router.findRoute(origin, station), demand, pickup, staleness, starving);
    }

    private Set<Station> stationsChosen(DispatchStrategy strategy, List<Candidate> candidates) {
        Set<Station> chosen = new HashSet<>();
        for (int i = 0; i < PICKS; i++) {
            chosen.add(strategy.choose(candidates).station());
        }
        return chosen;
    }

    @Test
    void standardPrefersStarvingStationOverHigherDemand() {
        Candidate starving = candidate(near, 0, 0, 90, true);
        Candidate busy = candidate(far, 9, 9, 10, false);

        assertSame(starving, new StandardDispatch(new Random(1)).choose(List.of(busy, starving)));
    }

    @Test
    void standardPrefersHigherDemandWhenNobodyIsStarving() {
        Candidate quiet = candidate(near, 0, 0, 10, false);
        Candidate busy = candidate(far, 5, 0, 10, false);

        assertSame(busy, new StandardDispatch(new Random(1)).choose(List.of(quiet, busy)));
    }

    @Test
    void standardUsesRouteLengthWhenEverythingElseIsEqual() {
        Candidate closer = candidate(near, 2, 2, 10, false);
        Candidate further = candidate(far, 2, 2, 10, false);

        assertSame(closer, new StandardDispatch(new Random(1)).choose(List.of(further, closer)));
    }

    @Test
    void standardBreaksFullTiesAtRandom() {
        List<Candidate> tied = List.of(
                candidate(near, 1, 1, 10, false),
                candidate(sameDistanceAsNear, 1, 1, 10, false));

        Set<Station> chosen = stationsChosen(new StandardDispatch(new Random(1)), tied);

        assertEquals(Set.of(near, sameDistanceAsNear), chosen);
    }

    @Test
    void standardLeavesTheSuppliedListUntouched() {
        Candidate first = candidate(far, 0, 0, 10, false);
        Candidate second = candidate(near, 9, 0, 10, false);
        List<Candidate> candidates = List.of(first, second);

        new StandardDispatch(new Random(1)).choose(candidates);

        assertEquals(List.of(first, second), candidates);
    }

    @Test
    void nearestFirstPicksShortestRouteIgnoringDemandAndStarvation() {
        Candidate closer = candidate(near, 0, 0, 0, false);
        Candidate starvingAndBusy = candidate(far, 9, 9, 90, true);

        assertSame(closer, new NearestFirstDispatch(new Random(1)).choose(List.of(starvingAndBusy, closer)));
    }

    @Test
    void nearestFirstBreaksTiesAtRandom() {
        List<Candidate> tied = List.of(
                candidate(near, 0, 0, 10, false),
                candidate(sameDistanceAsNear, 0, 0, 10, false));

        Set<Station> chosen = stationsChosen(new NearestFirstDispatch(new Random(1)), tied);

        assertEquals(Set.of(near, sameDistanceAsNear), chosen);
    }

    @Test
    void longestWaitingPicksMostStaleIgnoringDemandAndDistance() {
        Candidate stale = candidate(far, 0, 0, 50, false);
        Candidate busyButRecent = candidate(near, 9, 9, 10, false);

        assertSame(stale, new LongestWaitingDispatch(new Random(1)).choose(List.of(busyButRecent, stale)));
    }

    @Test
    void longestWaitingBreaksTiesAtRandom() {
        List<Candidate> tied = List.of(
                candidate(near, 0, 0, 30, false),
                candidate(far, 0, 0, 30, false));

        Set<Station> chosen = stationsChosen(new LongestWaitingDispatch(new Random(1)), tied);

        assertEquals(Set.of(near, far), chosen);
    }

    @Test
    void randomEventuallyPicksEveryCandidate() {
        List<Candidate> candidates = List.of(
                candidate(near, 0, 0, 10, false),
                candidate(sameDistanceAsNear, 0, 0, 10, false),
                candidate(far, 0, 0, 10, false));

        Set<Station> chosen = stationsChosen(new RandomDispatch(new Random(1)), candidates);

        assertEquals(Set.of(near, sameDistanceAsNear, far), chosen);
    }

    @Test
    void randomIgnoresTheStarvationFlag() {
        List<Candidate> candidates = List.of(
                candidate(near, 0, 0, 90, true),
                candidate(far, 0, 0, 10, false));

        Set<Station> chosen = stationsChosen(new RandomDispatch(new Random(1)), candidates);

        assertTrue(chosen.contains(far), "a starving candidate should not always win");
    }

    @Test
    void everyStrategyRejectsANullRandom() {
        assertThrows(NullPointerException.class, () -> new StandardDispatch(null));
        assertThrows(NullPointerException.class, () -> new NearestFirstDispatch(null));
        assertThrows(NullPointerException.class, () -> new LongestWaitingDispatch(null));
        assertThrows(NullPointerException.class, () -> new RandomDispatch(null));
    }
}
