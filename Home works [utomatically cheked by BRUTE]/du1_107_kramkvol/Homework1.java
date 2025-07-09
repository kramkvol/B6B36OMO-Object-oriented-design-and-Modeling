package cz.cvut.k36.omo.hw.hw01;

public class Homework1 {
    protected int countOfCallsMethodH = 0;
    protected static int countOfCallsMethodI = 0;

    public boolean f() {
        return true;
    }

    public static boolean g() {
        return false;
    }

    public int h() {
        System.out.println("Metoda h() byla zavolána");
        countOfCallsMethodH++;
        return countOfCallsMethodH;
    }

    public int i() {
        System.out.println("Metoda i() byla zavolána");
        countOfCallsMethodI++;
        return countOfCallsMethodI;
    }
}


