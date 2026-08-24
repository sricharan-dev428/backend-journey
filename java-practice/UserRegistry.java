import java.util.HashMap;
public class UserRegistry{
    
    public static void main(String[] args)
    {
        HashMap<String,String> map=new HashMap<>();

        map.put("maya","maya@gmail.com");
        map.put("annie","annie@gmail.com");
        map.put("ben","ben@gmail.com");
        map.put("carla","carla@gmail.com");
        if(map.containsKey("maya"))
            System.out.println(map.get("maya"));
        else
            System.out.println("doesn'exist");
        System.out.println(map.getOrDefault("bri","bri doesnotexit"));
        System.out.println("ben's email"+map.get("ben"));
        System.out.println(map.get("sri"));// expecting null
        map.put("maya","mayamaya@gmail.com");
        System.out.println(map.get("maya"));
    }
}