import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SetDemo {
    public static void main(String[] args) {
        ArrayList<String> arl=new ArrayList<>(List.of("ana@x.com", "ben@x.com", "ana@x.com", "carla@x.com", "ben@x.com"));
        HashSet<String> hs=new HashSet<>();
        for(String val:arl)
        {
            hs.add(val);
        }
        System.out.println(hs);
        System.out.println(hs.size());//3
        System.out.println(hs.add("carla@x.com"));//false
        System.out.println(hs.contains("carla@x.com"));//true
        System.out.println(hs.contains("rand@x.com"));//false
        
        
        HashSet<Point> hsp=new HashSet<>();
        hsp.add(new Point(3, 4));
        hsp.add(new Point(3, 4));
        System.out.println(hsp.size());//1

    }
}
