package com.zachary.common;

public class ServiceFactory {

    private static ServiceFactory instance;//单例模式

    private ServiceFactory() {

    }

    public static ServiceFactory getInstance() {

        if (instance == null) {

            synchronized (ServiceFactory.class) {

                if (instance == null)

                    instance = new ServiceFactory();

            }

        }

        return instance;

    }

// 安装用户组件的跳转服务的注册和获取

    private IUserInstallService mIUserInstallService;

    public IUserInstallService getIUserInstallService() {

        return mIUserInstallService;

    }

    public void setIUserInstallService(IUserInstallService mIUserInstallService) {

        this.mIUserInstallService = mIUserInstallService;

    }

// 其他组件的跳转服务的注册和获取，与A组件的一样

}

