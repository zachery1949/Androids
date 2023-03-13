package com.zachary.bmodule;

import android.content.Context;
import android.content.Intent;

import com.zachary.common.IUserInstallService;

public class UserInstallService implements IUserInstallService {

    @Override

    public void launch(Context context, String extra) {

        Intent intent = new Intent(context, BModuleMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

}

