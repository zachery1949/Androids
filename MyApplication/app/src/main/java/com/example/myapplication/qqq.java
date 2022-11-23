package com.example.myapplication;

import android.os.Environment;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class qqq {
    public static void main(String []args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {


        int a = 100;
        System.out.println("Hello World!");
        //1.加载Class对象
        Class clazz = null;
        try {
//            clazz = Class.forName("com.example.myapplications.Student");
            clazz = Class.forName("com.example.myapplication.Student");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //2.获取所有公有构造方法
        System.out.println("**********************所有公有构造方法*********************************");
        Constructor[] conArray = clazz.getConstructors();
        for(Constructor c : conArray){
            System.out.println(c);
        }

        System.out.println("*****************获取公有、无参的构造方法*******************************");
        Constructor con = clazz.getConstructor(null);
        //1>、因为是无参的构造方法所以类型是一个null,不写也可以：这里需要的是一个参数的类型，切记是类型
        //2>、返回的是描述这个无参构造函数的类对象。
        System.out.println("con = " + con);

        //调用构造方法
        Object obj = con.newInstance();
        //	System.out.println("obj = " + obj);
        //	Student stu = (Student)obj;

        System.out.println("******************获取私有构造方法，并调用*******************************");
        con = clazz.getDeclaredConstructor(int.class);
        System.out.println(con);
        //调用构造方法
        con.setAccessible(true);//暴力访问(忽略掉访问修饰符)
        obj = con.newInstance(18);

        //2、获取main方法
        Method methodMain = clazz.getMethod("func2", String.class,int.class);//第一个参数：方法名称，第二个参数：方法形参的类型，
        //3、调用main方法
        // methodMain.invoke(null, new String[]{"a","b","c"});
        //第一个参数，对象类型，因为方法是static静态的，所以为null可以，第二个参数是String数组，这里要注意在jdk1.4时是数组，jdk1.5之后是可变参数
        //这里拆的时候将  new Object[]{new String("a"),16} 拆成2个对象。。。所以需要将它强转。
//        methodMain.invoke(null, (Object)new String[]{"a","b","c"});//方式一
         methodMain.invoke(null, new Object[]{new String("a"),16});//方式二
    }
}
