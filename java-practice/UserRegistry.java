import java.util.HashMap;
import java.util.Map;
public class UserRegistry{
    
    public static void main(String[] args)
    {
        HashMap<String,String> map=new HashMap<>();

        map.put("maya","maya@gmail.com");
        map.put("annie","annie@gmail.com");
        map.put("ben","ben@gmail.com");
        map.put("carla","carla@gmail.com");
        /*if(map.containsKey("maya"))
            System.out.println(map.get("maya"));
        else
            System.out.println("doesn'exist");
        System.out.println(map.getOrDefault("bri","bri doesnotexit"));
        System.out.println("ben's email"+map.get("ben"));
        System.out.println(map.get("sri"));// expecting null
        map.put("maya","mayamaya@gmail.com");
        System.out.println(map.get("maya"));*/

        for(String username:map.keySet())
        {
            System.out.println(username);
        }

        for(String email:map.values())
        {
            System.out.println(email);
        }

        for(Map.Entry<String,String> entry: map.entrySet())
        {
            System.out.println("username-> "+entry.getKey()+ "    email-> "+entry.getValue());
        }


    }
}