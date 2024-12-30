import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        String filePath = "input.json"; // Path to your JSON file
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject json = new JSONObject(content);
            
            int n = json.getJSONObject("keys").getInt("n");
            int k = json.getJSONObject("keys").getInt("k");
            
            List<Point> points = new ArrayList<>();
            
            for (int i = 1; i <= n; i++) {
                JSONObject root = json.getJSONObject(String.valueOf(i));
                int base = root.getInt("base");
                String value = root.getString("value");
                
                // Decode y value
                long y = decodeValue(base, value);
                points.add(new Point(i, y));
            }

            // Find constant term c
            long c = findConstantTerm(points, k);
            System.out.println("The constant term c is: " + c);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long decodeValue(int base, String value) {
        return Long.parseLong(value, base);
    }

    private static long findConstantTerm(List<Point> points, int k) {
        // Evaluate polynomial at x = 0 to find constant term c
        return lagrangeInterpolation(points, 0);
    }

    private static long lagrangeInterpolation(List<Point> points, int x) {
        long total = 0;
        int n = points.size();
        
        for (int i = 0; i < n; i++) {
            long xi = points.get(i).x;
            long yi = points.get(i).y;

            long term = yi;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    long xj = points.get(j).x;
                    term *= (x - xj) * modInverse(xi - xj);
                    term %= 1_000_000_007; // Use a large prime to avoid overflow
                }
            }
            total += term;
            total %= 1_000_000_007; // Use a large prime to avoid overflow
        }
        
        return total;
    }

    private static long modInverse(long a) {
        return modInverse(a, 1_000_000_007);
    }

    private static long modInverse(long a, long m) {
        a %= m;
        for (long x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return -1; // Should not reach here if inputs are valid
    }

    static class Point {
        long x;
        long y;

        Point(long x, long y) {
            this.x = x;
            this.y = y;
        }
    }
}
