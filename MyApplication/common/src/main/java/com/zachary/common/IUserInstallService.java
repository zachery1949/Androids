package com.zachary.common;

import android.content.Context;

/**

 安装用户组件对外暴露的接口

 */

public interface IUserInstallService {

// 跳转到安装用户页面，其中extra是要传递的数据

    void launch(Context context, String extra);

}
