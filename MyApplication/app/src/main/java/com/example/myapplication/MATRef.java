package com.example.myapplication;

/**
 * Incoming Vs Outgoing References
 */

class AA {
    private CC c1 = CC.getInstance();
}
class BB {
    private CC c2 = CC.getInstance();
}
class CC {
    private static CC myC = new CC();
    public static CC getInstance() {
        return myC;
    }
    private DD d1 = new DD();
    private EE e1 = new EE();
}
class DD {
}
class EE {
}
public class MATRef {
    public static void main(String[] args) throws Exception {
        AA a = new AA();
        BB b = new BB();
        Thread.sleep(Integer.MAX_VALUE);//线程休眠
    }
}
