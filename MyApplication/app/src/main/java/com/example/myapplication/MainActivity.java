package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.enjoy.fix.patch.ShareReflectUtil;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
String TAG = MainActivity.class.getSimpleName();
    public ArrayList<String> getAllDataFileName(String folderPath){
        ArrayList<String> fileList = new ArrayList<>();
        //folderPath = "/mnt/sdcard"+File.separator+ "ZEGOLog/";
        File file = new File(folderPath);
        if(!file.exists()){
            return fileList;
        }
        if(!file.isDirectory()){
            return fileList;
        }
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                System.out.println("文件：" + tempList[i].getName());
                String fileName = tempList[i].getName();
                if (fileName.endsWith(".bin")){    //  根据自己的需要进行类型筛选
                    fileList.add(fileName);
                }
            }
        }
        return fileList;
    }
    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    int PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP){
            if(ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS ,PERMISSION_CODE );
            }

        }

        String path = new String();
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            Log.d(TAG, "onCreate path1: ");
            path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + "ZEGOLog" + File.separator;
            getAllDataFileName(path);
        } else {// 如果SD卡不存在，就保存到本应用的目录下
            Log.d(TAG, "onCreate path2: ");
            path = this.getFilesDir().getAbsolutePath()
                    + File.separator + "ZEGOLog";
        }
        String path1 = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "ZEGOLog";
        String path2 = this.getFilesDir().getAbsolutePath()
                + File.separator + "ZEGOLog";
        Log.d(TAG, "onCreate path1: "+path1);
        Log.d(TAG, "onCreate path2: "+path2);
        //PathClassLoader
        ClassLoader classLoader = getClassLoader();
        //找到 pathList
        Field pathListField = null;
        try {
            pathListField = ShareReflectUtil.findField(classLoader, "pathList");
            Object dexPathList = pathListField.get(classLoader);

            Method makePathElements = null;
            try {
                makePathElements = ShareReflectUtil.findMethod(dexPathList, "makePathElements", List.class, File.class,
                        List.class);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "NoSuchMethodException: makePathElements(List,File,List) failure");
                try {
                    makePathElements = ShareReflectUtil.findMethod(dexPathList, "makePathElements", ArrayList.class, File.class, ArrayList.class);
                } catch (NoSuchMethodException e1) {
                    Log.e(TAG, "NoSuchMethodException: makeDexElements(ArrayList,File,ArrayList) failure");
                }
            }
            List<File> files = new ArrayList<>();
            files.add(null);
            File dexOptDir = this.getCacheDir();
            ArrayList<IOException> suppressedExceptions = new ArrayList<>();
            Object[] qqq = (Object[]) makePathElements.invoke(dexPathList, files, dexOptDir, suppressedExceptions);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }


        //classLoader： element=》[classes.dex，fix.dex]


        //BootClassLoader
//        ClassLoader classLoader1 = Activity.class.getClassLoader();
//        DexClassLoader dexClassLoader = new DexClassLoader("/sdcard/fix.dex", getCodeCacheDir().getAbsolutePath(), null, getClassLoader());
//        PathClassLoader pathClassLoader = new PathClassLoader("/sdcard/fix.dex", getClassLoader());
//
//        System.out.println("getClassLoader:"+classLoader);
//        System.out.println("getClassLoader 的父亲 :"+classLoader.getParent());
//        System.out.println("getClassLoader 的父亲的父亲 :"+classLoader.getParent().getParent());
//        System.out.println("dexClassLoader 的父亲 :"+dexClassLoader.getParent());
//        System.out.println("dexClassLoader 的父亲的父亲 :"+dexClassLoader.getParent().getParent());
//        System.out.println("dexClassLoader 的父亲的父亲的父亲 :"+dexClassLoader.getParent().getParent().getParent());
//        System.out.println("pathClassLoader  :"+pathClassLoader);
//        System.out.println("pathClassLoader 的父亲 :"+pathClassLoader.getParent());
//        System.out.println("pathClassLoader 的父亲的父亲 :"+pathClassLoader.getParent().getParent());
//        System.out.println("pathClassLoader 的父亲的父亲的父亲 :"+pathClassLoader.getParent().getParent().getParent());
//        System.out.println("Activity.class :"+classLoader1);
//
//
//        try {
//            DexFile dexFile = new DexFile("/xxx/xx.dex");
//            Enumeration<String> entries = dexFile.entries();
//            //遍历  dex中所有的Class
//            while (entries.hasMoreElements()){
//                String clsName = entries.nextElement();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        // 演示classloader
//
//        try {
//            Class<?> aClass = classLoader.loadClass("com.enjoy.enjoyfix.BugPatch");
//            System.out.println(aClass);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            classLoader.loadClass("android.app.Activity");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        /**
//         * 1、dex路径
//         */
//        PathClassLoader pathClassLoader = new PathClassLoader("/sdcard/fix.dex", getClassLoader());
//
//        // /data/data/packagename : 私有目录
//        // 2: dex优化为odex之后保存的目录，必须是私有目录，不能是sd卡的目录
//        DexClassLoader dexClassLoader = new DexClassLoader("/sdcard/fix.dex", getCodeCacheDir().getAbsolutePath(), null, getClassLoader());
//
//
//        try {
//            Class<?> aClass = pathClassLoader.loadClass("com.enjoy.enjoyfix.BugPatch");
//            System.out.println(aClass);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            Class<?> aClass = dexClassLoader.loadClass("com.enjoy.enjoyfix.BugPatch");
//            System.out.println(aClass);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        setSupportActionBar(binding.toolbar);
//
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//
//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}