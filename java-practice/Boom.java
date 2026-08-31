public class Boom {

    static void level3() {
        System.out.println("level3 start");
        throw new IllegalStateException("port 99999 out of range");
        
    }

    static void level2() {
        System.out.println("level2 start");
        level3();
        System.out.println("level2 end");
    }

    static void level1() {
        System.out.println("level1 start");
        try {
            level2();
            
        }
        catch(IllegalArgumentException e)
        {
            System.out.println("IllegalArgumentException caught: "+e.getMessage());
        }
        catch (Exception e) {
            System.out.println("exception caught: "+e.getMessage());
        }
        
        System.out.println("level1 end");
    }

    public static void main(String[] args) {
        System.out.println("main start");
        level1();
        System.out.println("main end");
    }
}