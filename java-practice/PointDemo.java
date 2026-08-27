import java.util.HashMap;
class Point {
    int x;
    int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
            public boolean equals(Object o) {
                if(this==o)
                    return true;
                if(o==null)
                    return false;
                
                if(this.getClass()!=o.getClass())
                    return false;
                Point p = (Point) o;
                return this.x == p.x && this.y == p.y;
            }

            @Override
            public int hashCode()
            {
                return x * 31 + y;
            }
}
public class PointDemo {
        public static void main(String[] args) {
            Point a = new Point(3, 4);
            Point b = new Point(3, 4);
            System.out.println(a == b); //false 
            System.out.println(a.equals(b)); //true
            System.out.println(a.hashCode() == b.hashCode()); //true
            System.out.println(a.hashCode() + " " + b.hashCode()); //same numbers

            HashMap<Point, String> map = new HashMap<>();

            
            map.put(a, "first point");


            
            System.out.println(map.get(a));        // first point
            System.out.println(map.get(b));        // first point
            System.out.println(map.containsKey(b)); // true
            System.out.println(map.size()); //1

            map.put(b, "second point"); 
            System.out.println(map.size()); //1
            System.out.println("----knowledge check----"); 
            a.x = 99;
            System.out.println(map.get(a));
            System.out.println(map.size());

            for (Point p : map.keySet()) {
            System.out.println(p.x + "," + p.y + " → " + map.get(p));
}

            
    }

    
}