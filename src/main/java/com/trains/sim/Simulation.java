package com.trains.sim;

import com.trains.network.Network;
import com.trains.vehicles.Vehicle;

import java.util.*;


public final class Simulation {
    // Should hold the network and a list of vehicles, with a tick method that advances time.
    private final Network network;
    private final List<Vehicle> vehicles;
    private final CargoSpawner cargoSpawner;

    public Simulation() {
        this(new Network(), new ArrayList<>());
    }

    public Simulation(Network network) {
        this(network, new ArrayList<>());
    }

    public Simulation(Network network, List<Vehicle> vehicles) {
        this.network = network;
        this.vehicles = vehicles;
        this.cargoSpawner = new CargoSpawner(network);
    }

    public Network getNetwork() {
        return network;
    }
    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    /*
     * These lock on the Simulation itself, so callers that need several steps to happen together,
     * like iterating getVehicles(), can take the same lock with synchronized (sim).
     */
    public synchronized void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public synchronized void removeVehicle(Vehicle vehicle) {
        vehicles.remove(vehicle);
    }

    public synchronized int vehicleCount() {
        return vehicles.size();
    }

    /**
     * Advances the simulation by one tick
     * @param dt elapsed simulated time since last tick
     */
    public synchronized void tick(double dt) {
        for ( Vehicle vehicle : vehicles) {
            vehicle.update(dt);
        }
    }
}
