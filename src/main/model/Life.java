package model;

import logging.Event;
import logging.Observable;
import logging.Observer;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// A twist on Conway's Game of Life, represented as an evolving set of Cells
public class Life extends Observable implements Writable {

    private long gen; // Generation
    private Set<Cell> cells;
    private Set<Position> deathRay = new HashSet<>();
    private Set<Position> lifeRay = new HashSet<>();

    // Constructs life-form from a set of positions, initializing and integrating cells
    public Life(Set<Position> initFootprint) {
        gen = 0;

        cells = initFootprint.stream()
                .map(pos -> (new Cell(pos.getX(), pos.getY())))
                .collect(Collectors.toSet());

        for (Cell c : cells) {
            c.integrate(cells);
        }
    }

    // Modifies this
    // Increments state of life-form by one generation of cells
    public void evolve() {
        this.gen++;
        this.cells = this.nextCells();
        for (Cell c : this.cells) {
            c.integrate(this.cells);
        }
        this.lifeRay = new HashSet<>();
        this.deathRay = new HashSet<>();
    }

    // Inserts impurities then produces next generation of cells in accordance with Conway's rules
    private Set<Cell> nextCells() {

        Set<Position> footprint = this.adjustedFootprint(deathRay, lifeRay); // "impurities"

        Set<Set<Position>> projection = neighbourhoods(footprint);

        Set<Position> frontier = projection.stream()
                .flatMap(neighbourhood -> neighbourhood.stream()
                        .filter(pos -> !(footprint.contains(pos))))
                .collect(Collectors.toSet());

        Set<Cell> newCells = frontier.stream()
                .filter(pos -> (pointsOfContact(pos, projection) == 3))
                .map(pos -> new Cell(pos.getX(), pos.getY()))
                .collect(Collectors.toSet());

        Set<Cell> survivors = this.cells.stream()
                .filter(cell -> (cell.getNumLiveNeighbours() == 2 || cell.getNumLiveNeighbours() == 3))
                .collect(Collectors.toSet());

        Set<Cell> result = new HashSet<>();
        result.addAll(survivors);
        result.addAll(newCells);

        return result;
    }

    // Returns footprint as it would be if, at time of regeneration, death rays became bullets and seeds became cells
    private Set<Position> adjustedFootprint(Set<Position> deathray, Set<Position> liferay) {
        Set<Position> result = new HashSet<>();
        result.addAll(this.getFootprint().stream()
                .filter(pos -> !(deathray.contains(pos)))
                .collect(Collectors.toSet()));
        result.addAll(liferay);
        return result;
    }

    // Produces set-of-sets of neighbours of positions in given set
    private Set<Set<Position>> neighbourhoods(Set<Position> footprint) {
        return footprint.stream().map(Position::neighbourhood).collect(Collectors.toSet());
    }

    // Produces count of sets, in given set-of-sets, of which given position is a member
    private int pointsOfContact(Position pos, Set<Set<Position>> projection) {
        return (int) projection.stream().filter(sop -> sop.contains(pos)).count();
    }

    // Modifies this
    // Setter; field stores set of positions to pass as impurities to the life-form in the nextCells() method
    public void updateRays(Set<Position> currentDeathRay, Set<Position> currentLifeRay) {
        this.deathRay = currentDeathRay;
        this.lifeRay = currentLifeRay;
    }

    // Modifies this
    // Adds new cell at given position and signals observers
    public void addCell(Position pos) {
        cells.add(new Cell(pos.getX(), pos.getY()));
        signal("Cell inserted at (" + pos.getX() + "," + pos.getY() + ")");
    }

    // Modifies this
    // Removes cell at given position and signals observers
    public void removeCell(Position pos) {
        cells = cells.stream().filter(c -> !(c.getPosition().equals(pos))).collect(Collectors.toSet());
        signal("Cell deleted at (" + pos.getX() + "," + pos.getY() + ")");
    }

    // Modifies this
    // Removes cells at positions in given set from this.cells
    public void filterOut(Set<Position> sop) {
        cells = cells.stream().filter(cell -> !(sop.contains(cell.getPosition()))).collect(Collectors.toSet());
    }

    // Modifies this
    // Adds n generations to the counter (primarily for testing purposes)
    public void addGenerations(Integer n) {
        gen += n;
    }

    // Returns set of positions currently occupied by this life-form
    public Set<Position> getFootprint() {
        return cells.stream().map(Cell::getPosition).collect(Collectors.toSet());
    }

    // Returns the number of generations comprising this life-form
    public long getGen() {
        return this.gen;
    }

    // Set this.num generations to n
    public void setGen(long n) {
        this.gen = n;
    }

    public Set<Position> getDeathRay() {
        return this.deathRay;
    }

    public Set<Position> getLifeRay() {
        return this.lifeRay;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonLife = new JSONObject();
        JSONArray jsonCells = new JSONArray();
        JSONObject o;
        for (Position p : this.getFootprint()) {
            o = new JSONObject();
            o.put("x", p.getX());
            o.put("y", p.getY());
            jsonCells.put(o);
        }
        jsonLife.put("generations", this.gen);
        jsonLife.put("cells", jsonCells);
        return jsonLife;
    }

    @Override
    public void signal(String s) {
        for (Observer o : this.observers) {
            o.update(new Event(s));
        }
    }
}
