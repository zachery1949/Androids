package com.zachary.bmodule;

import com.zachary.common.IAppComponent;
import com.zachary.common.ServiceFactory;

public class BModule implements IAppComponent {



    public void init() {
        initialize();
    }

    @Override

    public void initialize() {
        ServiceFactory.getInstance().setIUserInstallService(new UserInstallService());
    }

}

