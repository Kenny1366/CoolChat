package com.cooloongwu.coolchat.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;

import com.apkfuns.logutils.LogLevel;
import com.apkfuns.logutils.LogUtils;
import com.cooloongwu.coolchat.utils.AsyncHttpClientUtils;
import com.cooloongwu.qupai.QupaiAuth;
import com.cooloongwu.qupai.QupaiSetting;
import com.duanqu.qupai.engine.session.MovieExportOptions;
import com.duanqu.qupai.engine.session.ProjectOptions;
import com.duanqu.qupai.engine.session.ThumbnailExportOptions;
import com.duanqu.qupai.engine.session.UISettings;
import com.duanqu.qupai.engine.session.VideoSessionCreateInfo;
import com.duanqu.qupai.sdk.android.QupaiManager;
import com.duanqu.qupai.sdk.android.QupaiService;
import com.loopj.android.http.AsyncHttpClient;

/**
 * 自定义Application类
 * Created by CooLoongWu on 2016-9-26 14:53.
 */

public class MyApplication extends Application {

    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        Intent i = new Intent(context, NetService.class);
        startService(i);

        LogUtils.getLogConfig()
                .configAllowLog(true)
                .configTagPrefix("CoolChat")
                .configShowBorders(true)
                .configFormatTag("%d{HH:mm:ss:SSS} %t %c{-5}")
                .configLevel(LogLevel.TYPE_VERBOSE);

        //MultiDex.install(this);
        initClient();
        initQupaiAuth();
        initQupaiSetting();
    }

    public static synchronized MyApplication context() {
        return (MyApplication) context;
    }

    /**
     * 初始化全局静态Client
     */
    private void initClient() {
        AsyncHttpClientUtils.setClientGeneral(new AsyncHttpClient());
    }

    /**
     * 趣拍云鉴权
     */
    private void initQupaiAuth() {
        QupaiAuth auth = QupaiAuth.getInstance();
        auth.initAuth(getApplicationContext());
    }

    /**
     * 初始化趣拍的设置
     */
    private void initQupaiSetting() {
        QupaiService qupaiService = QupaiManager.getQupaiService(this);
        UISettings uiSettings = new UISettings() {

            @Override
            public boolean hasEditor() {
                return true;
            }

            @Override
            public boolean hasImporter() {
                return super.hasImporter();
            }

            @Override
            public boolean hasGuide() {
                return true;
            }

        };

        MovieExportOptions movie_options = new MovieExportOptions.Builder()
                .setVideoBitrate(QupaiSetting.DEFAULT_BITRATE)
                .configureMuxer("movflags", "+faststart")
                .build();

        ProjectOptions projectOptions = new ProjectOptions.Builder()
                .setVideoSize(480, 520)
                .setVideoFrameRate(30)
                .setDurationRange(QupaiSetting.DEFAULT_MIN_DURATION_LIMIT, QupaiSetting.DEFAULT_DURATION_LIMIT)
                .get();

        ThumbnailExportOptions thumbnailExportOptions = new ThumbnailExportOptions.Builder()
                .setCount(1).get();

        VideoSessionCreateInfo info = new VideoSessionCreateInfo.Builder()
                //.setWaterMarkPath(QupaiSetting.WATER_MARK_PATH)
                .setWaterMarkPosition(1)
                .setCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK)
                .setBeautyProgress(80)
                .setBeautySkinOn(true)
                .setMovieExportOptions(movie_options)
                .setThumbnailExportOptions(thumbnailExportOptions)
                .build();

        qupaiService.initRecord(info, projectOptions, uiSettings);

        qupaiService.addMusic(0, "Athena", "assets://Qupai/music/Athena");
    }
}
