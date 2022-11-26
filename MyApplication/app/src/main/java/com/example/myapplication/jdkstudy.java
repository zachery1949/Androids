package com.example.myapplication;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class jdkstudy {
    public static void main(String []args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        System.out.println("String == 的使用");
        String s1 = "abc";//指向字符常量区的同一块位置
        String s2 = "abc";
        String s3 = new String("abc");//new的，指向堆区的不同位置
        String s4 = new String("abc");
        System.out.println(s1 == s2);//比较s1和s2的地址值，指向字符常量区的同一块位置，true
        System.out.println(s3 == s4);//比较s3和s4的地址值，new的指向堆区的不同位置，false
        System.out.println("*****************");

        System.out.println("length length() size()的区别");
        int[] arr1 = new int[6];
        int[] arr2 = new int[]{1,2,3};
        ArrayList<Integer> list = new ArrayList<>();
        String s = new String("qwer");
        System.out.println("a1:"+arr1.length);
        System.out.println("a2:"+arr2.length);
        System.out.println("list:"+list.size());
        System.out.println("s:"+s.length());
        System.out.println("*****************");

        System.out.println("hashmap的使用");
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("zhangsan","123");
        hashMap.put("lisi","456");
        System.out.println("zhangsan："+hashMap.get("zhangsan"));
        System.out.println("*****************");
    }
}
