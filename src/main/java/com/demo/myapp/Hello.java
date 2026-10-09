package com.demo.myapp;

public class Hello {
    public static void main(String[] args) {
        Hey hey = new Hey();
        hey.p = 20;
        System.out.println(hey.p);

        int a = 5;
        System.out.println(a);
        test(a, hey);
        System.out.println(a);

        System.out.println(hey.p);
    }

    static void test(int x, Hey hey) {
        x++;
        System.out.println(x);

        hey.p++;
        System.out.println(hey.p);
    }
}

class Hey {
    int p;
}