package com.example.myapplication;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;

public class UpdateUtil {
//    private Context mContext;
    //UpdateUtil(Context context){
//        mContext = context;
//    }
    final static String TAG = UpdateUtil.class.getSimpleName();
    //获取本应用的apk包路径
    public static String getSelfApkPath(Context context) {
        List<ApplicationInfo> installList=context.getPackageManager().getInstalledApplications(
                PackageManager.GET_UNINSTALLED_PACKAGES);
        for (int i = 0; i<installList.size();i++){
            ApplicationInfo info=installList.get(i);
            if(info.packageName.equals(context.getPackageName())){
                Log.i(TAG,"publicdir:"+info.publicSourceDir+",sourcedir:"+info.sourceDir);
                return info.sourceDir;}
        }
        return null;}
}
