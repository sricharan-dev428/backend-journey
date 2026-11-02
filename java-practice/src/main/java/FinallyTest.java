import java.io.IOException;

public class FinallyTest {
    static String test(int mode) {
        try {
            System.out.println("try start");
            if (mode == 1) return "returned from try";
            if (mode == 2) throw new IllegalStateException("boom");
            if (mode == 3) throw new java.io.IOException("checked boom");
            System.out.println("try end");
            return "normal";
        } catch (IllegalStateException e) {
            System.out.println("caught: " + e.getMessage());
            return "returned from catch";
        }
        catch(IOException e)
        {
            System.out.println("caught: "+e.getMessage());
            return "returned from catch";
        }
        finally {
            System.out.println("FINALLY");
        }
    }

        static int weird() {
        try {
            return 1;
        } finally {
            System.out.println("finally ran");
        }
    }

        static int weirder() {
        try {
            return 1;
        } finally {
            return 2;
        }
    }

    static int weirdest() {
        try {
            throw new RuntimeException("boom");
        } finally {
            return 3;
        }
    }

    public static void main(String[] args) {
        /*est(0);//prints trystart and try end, finally
        test(1);//try start.. (returns.. but) prints finally
        test(2);//try start.. caught "Boom".. finally
        test(3);// handled by adding catch(IOException e).. so it prints, try start caucht: checked boom finally 
        System.out.println(weird());//1,finally ran*/
        System.out.println(weirder());//2,1
        System.out.println(weirdest());//3
    }
    
}
