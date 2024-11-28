package simulation;

public record Coordinates(int x, int y) implements Comparable<Coordinates> {

    @Override
    public int compareTo(Coordinates o) {
        if (x == o.x()) {
            return Integer.compare(y, o.y());
        } else {
            return Integer.compare(x, o.x());
        }
    }
}
