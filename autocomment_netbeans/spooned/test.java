

// default package (CtPackage.TOP_LEVEL_PACKAGE_NAME in Spoon= unnamed package)


import java.util.Scanner;

public class test {
    /**
     * This javadoc is for main
     */
    public static void main(String[] args) {
    }

    /**
     * This javadoc is for hello
     */
    public void hello() {
        Scanner input = new Scanner(System.in);
        String a = input.nextLine();
    }

    /**
     * This javadoc is for foo
     */
    private int foo(int boo) {
        return boo - 1;
    }

    /**
     * This javadoc is for koo
     */
    public String koo() {
        return "poo";
    }

    /**
     * This javadoc is for moo
     */
    public void moo(boolean loo) {
        System.out.println("Coo");
    }
}

