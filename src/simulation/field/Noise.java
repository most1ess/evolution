package simulation.field;

import simulation.Coordinates;

import java.util.Random;
import java.util.TreeMap;

public class Noise {
    private final Random rand_;

    /** Amount of roughness */
    float roughness_;

    /** Plasma fractal grid */
    private final float[][] grid_;


    /** Generate a noise source based upon the midpoint displacement fractal.
     *
     * @param rand The random number generator
     * @param roughness a roughness parameter
     * @param width the width of the grid
     * @param height the height of the grid
     */
    public Noise(Random rand, float roughness, int width, int height) {
        roughness_ = roughness / width;
        grid_ = new float[width][height];
        rand_ = (rand == null) ? new Random() : rand;
    }


    public void initialise() {
        int xh = grid_.length - 1;
        int yh = grid_[0].length - 1;

        // set the corner points
        grid_[0][0] = rand_.nextFloat() - 0.5f;
        grid_[0][yh] = rand_.nextFloat() - 0.5f;
        grid_[xh][0] = rand_.nextFloat() - 0.5f;
        grid_[xh][yh] = rand_.nextFloat() - 0.5f;

        // generate the fractal
        generate(0, 0, xh, yh);
    }


    // Add a suitable amount of random displacement to a point
    private float roughen(float v, int l, int h) {
        return v + roughness_ * (float) (rand_.nextGaussian() * (h - l));
    }


    // generate the fractal
    private void generate(int xl, int yl, int xh, int yh) {
        int xm = (xl + xh) / 2;
        int ym = (yl + yh) / 2;
        if ((xl == xm) && (yl == ym)) return;

        grid_[xm][yl] = 0.5f * (grid_[xl][yl] + grid_[xh][yl]);
        grid_[xm][yh] = 0.5f * (grid_[xl][yh] + grid_[xh][yh]);
        grid_[xl][ym] = 0.5f * (grid_[xl][yl] + grid_[xl][yh]);
        grid_[xh][ym] = 0.5f * (grid_[xh][yl] + grid_[xh][yh]);

        float v = roughen(0.5f * (grid_[xm][yl] + grid_[xm][yh]), xl + yl, yh
                + xh);
        grid_[xm][ym] = v;
        grid_[xm][yl] = roughen(grid_[xm][yl], xl, xh);
        grid_[xm][yh] = roughen(grid_[xm][yh], xl, xh);
        grid_[xl][ym] = roughen(grid_[xl][ym], yl, yh);
        grid_[xh][ym] = roughen(grid_[xh][ym], yl, yh);

        generate(xl, yl, xm, ym);
        generate(xm, yl, xh, ym);
        generate(xl, ym, xm, yh);
        generate(xm, ym, xh, yh);
    }

    public TreeMap<Coordinates, Zone> getZoneMap() {
        TreeMap<Coordinates, Zone> zoneMap = new TreeMap<>();

        float maxElement = grid_[0][0];
        float minElement = grid_[0][0];
        for (float[] floats : grid_) {
            for (int j = 0; j < grid_[0].length; j++) {
                if (floats[j] > maxElement) maxElement = floats[j];
                if (floats[j] < minElement) minElement = floats[j];
            }
        }
        float step = (Math.abs(maxElement) + Math.abs(minElement)) / 3.0f;
        float border1 = minElement + step;
        float border2 = border1 + step;

        Zone zone;
        for (int i = 0; i<grid_.length; i++) {
            for (int j = 0; j < grid_[0].length; j++) {
                if(grid_[i][j] < border1) zone = Zone.NORMAL;
                else if(grid_[i][j] < border2) zone = Zone.HEAL;
                else zone = Zone.SPEED;
                zoneMap.put(new Coordinates(i, j), zone);
            }
        }

        return zoneMap;
    }
}
