package ui;

import model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static model.Vehicle.Direction.*;

// Representation of the Game environment
public class Game implements Writable {
    public static final long MAX_GENERATIONS = 1000;
    public static final int HANDICAP = 66;
    static final boolean APE_MODE = false;
    static final boolean WRAP_TANK_MOTION = true;

    private boolean suspendedEvolution = false;
    private int counter = HANDICAP;


    private final int width;
    private final int height;
    private long score;
    private boolean gameOver;

    private final Life life;
    private final Tank tank;
    private Set<Projectile> projectiles;

    // Sets up the game board, by initializing the (default) tank
    // and a life-form specified by the footprint function
    public Game(Surface s, Function<Surface, Set<Position>> footprintFn) {
        gameOver = false;
        score = 0;
        width = s.getWidth();
        height = s.getHeight();
        projectiles = new HashSet<>();
        Position initTankPosition = new Position(width / 2, height - 2);
        tank = new Tank(initTankPosition);
        life = new Life(footprintFn.apply(s)
                .stream().filter(p -> !(tank.getLocation().contains(p)))
                .collect(Collectors.toSet()));
    }

    // Duplicates given game
    public Game(Game g) {
        gameOver = g.isGameOver();
        score = g.getScore();
        width = g.getHeight();
        height = g.getWidth();
        projectiles = g.getProjectiles().stream()
                .map(p -> new Projectile(p.getPosition(), p.getDirection(), p.getType())).collect(Collectors.toSet());
        tank = new Tank(g.getTank().getPosition());
        tank.turn(g.getTank().getDirection());
        life = new Life(g.getLife().getFootprint());
        life.setGen(g.getLife().getGen());
    }

    // Modifies counter, life, gameOver
    // Ticks the game internally
    public void tick() {
        tickProjectiles();
        if (!suspendedEvolution) {
            if (counter == 0) {
                life.evolve();
                collectGarbage();
                counter = HANDICAP;
            } else {
                counter--;
            }
        }
        this.gameOver = endConditions();
    }

    // Modifies tank
    // Implements ui control of tank direction
    public void directTank(Vehicle.Direction dir) {
        if (!dir.equals(tank.getDirection())) {
            tank.turn(dir);
            if (APE_MODE) {
                tank.move();
            }
        } else {
            tank.move();
        }
        if (WRAP_TANK_MOTION) {
            constrainTank(tank);
        } else {
            constrain(tank);
        }
    }

    // Constrains tank by wrapping around board
    private void constrainTank(Tank t) {
        if ((int) t.nextLocation().stream().filter(this::isInBounds)
                .count() <= t.getLocation().size() / 3) {
            int tx = t.getPosition().getX();
            int ty = t.getPosition().getY();
            switch (t.getDirection()) {
                case NORTH:
                    t.setPosition(new Position(tx, ty + height));
                    t.turn(NORTH);
                    break;
                case EAST:
                    t.setPosition(new Position(tx - width, ty));
                    t.turn(EAST);
                    break;
                case SOUTH:
                    t.setPosition(new Position(tx, ty - height));
                    t.turn(SOUTH);
                    break;
                case WEST:
                    t.setPosition(new Position(tx + width, ty));
                    t.turn(WEST);
                    break;
            }
        }
    }

    // Constrains vehicle to board by making stationary at edge
    private void constrain(Vehicle o) {
        if ((int) o.nextLocation().stream()
                .filter(this::isInBounds)
                .count() != o.getLocation().size()) {
            o.setDirection(Vehicle.Direction.NONE);
        }
    }


    // Modifies projectiles
    // Creates projectile of specified type at location of cannon with direction of tank
    public void newProjectile(Projectile.Type typ) {
        Projectile o = new Projectile(tank.getCanonPos(), tank.getDirection(), typ);
        projectiles.add(o);
    }

    // Implements simple "physics" of the projectiles
    private void tickProjectiles() {
        for (Projectile o : projectiles) {
            o.move();
        }
        Set<Position> hits = projectiles.stream()
                .filter(o -> o.getType().equals(Projectile.Type.BULLET))
                .map(Vehicle::getPosition)
                .filter(p -> this.life.getFootprint().contains(p))
                .collect(Collectors.toSet());

        Set<Position> sticks = projectiles.stream()
                .filter(o -> o.getType().equals(Projectile.Type.SEED))
                .filter(o -> (o.nextLocation().stream().anyMatch(p -> this.life.getFootprint().contains(p))))
                .map(Vehicle::getPosition)
                .collect(Collectors.toSet());

        Set<Position> currentDeathRay = projectiles.stream()
                .filter(o -> o.getType().equals(Projectile.Type.DEATH_RAY))
                .map(Vehicle::getPosition).collect(Collectors.toSet());

        Set<Position> currentLifeRay = projectiles.stream()
                .filter(o -> o.getType().equals(Projectile.Type.LIFE_RAY))
                .map(Vehicle::getPosition).collect(Collectors.toSet());

        handleHits(hits);
        handleSticks(sticks);
        handleRays(currentDeathRay, currentLifeRay);
        expireProjectiles(hits, sticks);
    }

    // Modifies life
    // Kills all cells at positions in the given set
    private void handleHits(Set<Position> hits) {
        long before = this.life.getFootprint().size();
        for (Position p : hits) {
            this.life.removeCell(p);
        }
        long after = this.life.getFootprint().size();
        if (!suspendedEvolution) {
            score = (score + (before - after));
        }
    }

    // Modifies life
    // Adds new cells at all positions in the given set
    private void handleSticks(Set<Position> sticks) {
        for (Position p : sticks) {
            this.life.addCell(p);
            if (score > 0 && !suspendedEvolution) {
                score -= 1;
            }
        }
    }

    // Modifies life
    // Updates life-form with impurities
    private void handleRays(Set<Position> currentDeathRay, Set<Position> currentLifeRay) {
        this.life.updateRays(currentDeathRay, currentLifeRay);
    }

    // Modifies projectiles
    // Removes all bullets/seeds which have hit cells
    // and removes all projectiles at or exceeding max distance from source
    private void expireProjectiles(Set<Position> hits, Set<Position> sticks) {
        projectiles = this.projectiles.stream()
                .filter(o -> !(hits.contains(o.getPosition()) || sticks.contains(o.getPosition()))
                        && o.getDistance() <= Projectile.MAX_DISTANCE)
                .collect(Collectors.toSet());
    }

    // Modifies life
    // Removes all live cells that are out of bounds
    private void collectGarbage() {
        Set<Position> garbage = life.getFootprint().stream()
                .filter(pos -> !(isInBounds(pos)))
                .collect(Collectors.toSet());
        life.filterOut(garbage);
    }

    // Predicate for conditions under which to signal this.isGameOver
    private boolean endConditions() {
        return (!suspendedEvolution)
                && ((this.life.getFootprint().size() == 0)
                || this.life.getGen() > MAX_GENERATIONS
                || this.tank.getLocation().stream().anyMatch(pos -> this.life.getFootprint().contains(pos)));
    }

    // True iff position is within bounds of this game
    private boolean isInBounds(Position p) {
        return p.getX() >= 0
                && p.getY() >= 0
                && p.getX() <= this.width
                && p.getY() <= this.height;
    }

    // Getters and Setters
    public Set<Projectile> getProjectiles() {
        return this.projectiles;
    }

    public Life getLife() {
        return this.life;
    }

    public Tank getTank() {
        return this.tank;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public long getScore() {
        return this.score;
    }

    public void setScore(long n) {
        this.score = n;
    }

    public boolean isGameOver() {
        return this.gameOver;
    }

    public void suspendEvolution() {
        this.suspendedEvolution = true;
    }

    public void resumeEvolution() {
        this.suspendedEvolution = false;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonGame = new JSONObject();
        jsonGame.put("life", life.toJson());
        jsonGame.put("tank", tank.toJson());
        JSONArray jsonProjectiles = new JSONArray();
        for (Projectile p : projectiles) {
            jsonProjectiles.put(p.toJson());
        }
        jsonGame.put("projectiles", jsonProjectiles);
        jsonGame.put("score", score);
        return jsonGame;
    }

    // Generates "default" game board (pulsar)
    public static final Function<Surface, Set<Position>> DEMO_FOOTPRINT_FN = s -> {
        final int CTR_X = s.getWidth() / 2;
        final int CTR_Y = s.getHeight() / 2;
        Position ctr = new Position(CTR_X, CTR_Y);
        Set<Position> ul = ctr.neighbourhood().stream()
                .map(pos -> new Position(pos.getX() - 3, pos.getY() - 3))
                .collect(Collectors.toSet());
        Set<Position> ur = ctr.neighbourhood().stream()
                .map(pos -> new Position(pos.getX() + 3, pos.getY() - 3))
                .collect(Collectors.toSet());
        Set<Position> ll = ctr.neighbourhood().stream()
                .map(pos -> new Position(pos.getX() - 3, pos.getY() + 3))
                .collect(Collectors.toSet());
        Set<Position> lr = ctr.neighbourhood().stream()
                .map(pos -> new Position(pos.getX() + 3, pos.getY() + 3))
                .collect(Collectors.toSet());
        return Stream.of(
                ul.stream(),
                ur.stream(),
                ll.stream(),
                lr.stream()
        ).flatMap(i -> i).collect(Collectors.toSet());
    };

    // Generates Sierp. Carpet
    // WARNING: buggy -- will not render correctly w/ some surfaces
    public static final Function<Surface, Set<Position>> GEN_REC = s -> {
        int softMax = s.getHeight() - 4;
        Function<Pair<Position, Integer>, List<Pair<Position, Integer>>> subs = pair -> {
            Position ul = pair.getKey();
            int dim = pair.getValue();
            int third = dim / 3;
            int mod = dim % 3;
            List<Pair<Position, Integer>> out = new LinkedList<>();
            Pair<Position, Integer> cur;
            Position pos;
            for (int i = 0; i <= 2; i++) {
                for (int j = 0; j <= 2; j++) {
                    if (i != 1 || j != 1) {
                        pos = new Position(ul.getX() + (i * third), ul.getY() + (j * third));
                        cur = new Pair(pos, third);
                        out.add(cur);
                    }
                }
            }
            return out;
        };
        Function<Pair<Position, Integer>, List<Position>> core = pair -> {
            Position ul = pair.getKey();
            int dim = pair.getValue();
            int third = dim / 3;
            int mod = (dim % 3) == 2 ? 0 : 1;
            List<Position> out = new LinkedList<>();
            for (int i = ul.getX() + third; i < ul.getX() + (2 * third); i++) {
                for (int j = ul.getY() + third; j < ul.getY() + (2 * third); j++) {
                    out.add(new Position(i, j));
                }
            }
            return out;
        };
        return rec(subs, core, new Pair(new Position((s.getWidth() - softMax) / 2, 0), softMax), new HashSet<>(), new LinkedList<>(), (softMax % 3) / 2);
    };

    public static Set<Position> rec(Function<Pair<Position, Integer>, List<Pair<Position, Integer>>> sub, Function<Pair<Position, Integer>, List<Position>> cor, Pair<Position, Integer> sqr, Set<Position> acc, List<Pair> wl, int shift) {
        if (cor.apply(sqr).size() <= 1) {
            Set<Position> out = cor.apply(sqr).stream().map(p -> new Position(p.getX() + shift, p.getY() + shift)).collect(Collectors.toSet());
            out.addAll(acc);
            return out;
        } else {
            Set<Position> nextAcc = new HashSet<>();
            nextAcc.addAll(acc);
            nextAcc.addAll(cor.apply(sqr).stream().map(p -> new Position(p.getX() + ((sqr.getValue() % 3) / 2), p.getY() + (sqr.getValue() % 3) / 2)).collect(Collectors.toSet()));
            List<Pair> nextWL = new LinkedList<>();
            nextWL.addAll(wl);
            nextWL.addAll(sub.apply(sqr).stream().map(p ->
                    new Pair(new Position(p.getKey().getX() + shift, p.getKey().getY() + shift), p.getValue())).collect(Collectors.toList()));
            return rec(sub, cor, nextWL.get(0), nextAcc, nextWL.subList(1, nextWL.size()), (sqr.getValue() % 3) / 2);
        }
    }

    public static class Pair<A, B> {
        A key;
        B value;

        public Pair(A a, B b) {
            key = a;
            value = b;
        }

        public A getKey() {
            return key;
        }

        public B getValue() {
            return value;
        }
    }
}
