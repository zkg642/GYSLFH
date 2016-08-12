package com.otitan.gyslfh.activity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;

import com.esri.android.map.FeatureLayer;
import com.igexin.sdk.PushManager;
import com.otitan.DataBaseHelper;
import com.otitan.util.NetUtil;
import com.otitan.util.ResourcesManager;
import com.otitan.util.ScreenTool;
import com.otitan.util.WebServiceUtil;
import com.tencent.bugly.crashreport.CrashReport;

public class MyApplication extends Application

	{// FrontiaApplication  implements netEventHandler
		public static int mNetWorkState;
		/** 是否有网络 */
		public static boolean IntetnetISVisible = false;
		public static String SBH, XLH;
		public static String MOBILE_MODEL, MOBILE_MANUFACTURER;
		/** 设备屏幕信息 */
		public static ScreenTool.Screen screen;
		/** 移动设备唯一号 */
		public static String macAddress;
		/** 用于查询的图层url */
		public static String featureurl;
		public static FeatureLayer queryfeature;

		SharedPreferences sharedPreferences;
		//屏幕宽高
		int window_width,window_height;
		Context mcontext;

	/* 注册网络 */
		//private ConnectionChangeReceiver mNetworkStateReceiver;
		//private Logger log;
		@Override
		public void onCreate()
		{
			super.onCreate();
		/*log=LoggerManager.getLoggerInstance();
		log.debug("MyAPP启动\n");*/
		/* Bugly SDK初始化
	        * 参数1：上下文对象
	        * 参数2：APPID，平台注册时得到,注意替换成你的appId
	        * 参数3：是否开启调试模式，调试模式下会输出'CrashReport'tag的日志
	        * 发布新版本时需要修改以及bugly isbug需要改成false等部分
	        */
			CrashReport.initCrashReport(getApplicationContext(), "900039321", true);
			mcontext=this.getApplicationContext();
			//个推推送初始化
			PushManager.getInstance().initialize(this.getApplicationContext());
		/* 注册网络监听 */
			//initNetworkReceiver();
			/** 获取当前网络状态 */
			getNetState();

			// 初始化应用信息
      /*  AVOSCloud.initialize(this, "wzJcyDMaPYYirJrkMQFzKGkI-gzGzoHsz",
            "grm35ru5eSU3nf8q38jKOyVo");
        // 启用崩溃错误统计
        //AVAnalytics.enableCrashReport(this.getApplicationContext(), true);
        AVOSCloud.setLastModifyEnabled(true);
        AVOSCloud.setDebugLogEnabled(true);*/
			// GYSLFH
		/*AVOSCloud.initialize(this, "wzJcyDMaPYYirJrkMQFzKGkI-gzGzoHsz",
				"grm35ru5eSU3nf8q38jKOyVo");
        AVOSCloud.initialize(this, "wAkH3Uo0iLtI1EH0YMshUBfg-gzGzoHsz",
				"kJPuRiK1CsedHcnXR5omf9os");

		// 这段代码应该在应用启动的时候调用一次，保证设备注册到 LeanCloud 平台
		AVInstallation.getCurrentInstallation().saveInBackground(
				new SaveCallback()
				{
					@Override
					public void done(AVException e)
					{
						String installationId = AVInstallation
								.getCurrentInstallation().getInstallationId();
						System.out.println(installationId);
					}
				});
		// 通过调用以下代码启动推送服务，同时设置默认打开的 Activity。
		PushService.setDefaultPushCallback(this, MapActivity.class);
		// 启用崩溃错误统计
		AVAnalytics.enableCrashReport(this.getApplicationContext(), true);
		AVOSCloud.setDebugLogEnabled(true);*/

			new Thread(new Runnable() {

				@Override
				public void run() {
					initData();
					/** 获取屏幕分辨率 */
					screen = ScreenTool.getScreenPix(mcontext);
					if (IntetnetISVisible)
					{
						registerSBH();
						//在有网络情况下上传本地的历史轨迹
					}
				}
			}).start();
			//getWindowInfo();
		}
		/** 获取屏幕信息 */
/*	private void getWindowInfo() {
		     WindowManager wm = (WindowManager) mcontext.getSystemService(Context.WINDOW_SERVICE);
             window_width = wm.getDefaultDisplay().getWidth();
             window_height = wm.getDefaultDisplay().getHeight();
             //log.i("windowinfo："+window_width+"_"+window_height);
	}*/


	/* 注册网络监听 实时监控网络状态 */
	/*public void initNetworkReceiver()
	{
		ConnectionChangeReceiver.mListeners.add(this);
		mNetworkStateReceiver = new ConnectionChangeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mNetworkStateReceiver, filter);
	}*/

	/*public void checkData()
	{
		try
		{
			ResourcesManager manager = ResourcesManager.getInstance(this);
			manager.createFolder();// 初始化文件目录
			String path = manager.getPath()[0] + ResourcesManager.PATH_MAPS
					+ ResourcesManager.sqlite;
			if (!checkDataBase(path, "db.sqlite"))
			{
				CopyDatabase(path, "db.sqlite");
			}
			if (!checkDataBase(path, "guiji.sqlite"))
			{
				CopyDatabase(path, "guiji.sqlite");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}*/
		/** 注册用户设备信息*/
	public void registerSBH()
	{
		// 用户信息存储
		sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
		//初始化系统设置参数
		//sharedPreferences.edit().putInt("time", 1000).commit();
		//sharedPreferences.edit().putInt("distance", 100).commit();
		//是否标绘轨迹
		//sharedPreferences.edit().putBoolean("zongji", false).commit();
		//是否上传轨迹
		//sharedPreferences.edit().putBoolean("guiji", false).commit();
		// 获取唯一识表示 mac地址
		WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (!wifiMgr.isWifiEnabled())
		{
			wifiMgr.setWifiEnabled(true);
		}
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		if (null != info)
		{
			SBH = info.getMacAddress();
			sharedPreferences.edit().putString("SBH", SBH).commit();
		} else
		{
			SBH = sharedPreferences.getString("SBH", "");
		}
		XLH = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		// MOBILE_MODEL = android.os.Build.MODEL;// SM-P601 型号
		// MOBILE_MANUFACTURER = android.os.Build.MANUFACTURER;// samsung 厂商
		MOBILE_MODEL = android.os.Build.MODEL + "-"
				+ android.os.Build.MANUFACTURER;
		WebServiceUtil websUtil = new WebServiceUtil(this);
		String result = websUtil.addMacAddress(SBH, XLH, MOBILE_MODEL);
		if (result.equals(WebServiceUtil.netException))
		{
			// 网络异常
			sharedPreferences.edit().putBoolean(SBH, false).commit();
		} else if (result.equals("已录入"))
		{
			// 设备信息已经录入
			sharedPreferences.edit().putBoolean(SBH, true).commit();
		} else if (result.equals("录入成功"))
		{
			sharedPreferences.edit().putBoolean(SBH, true).commit();
			// 设备信息录入成功
		} else if (result.equals("录入失败"))
		{
			// 设备信息录入失败
			sharedPreferences.edit().putBoolean(SBH, false).commit();
		}
	}

	/** 初始化本地数据*/
	public void initData()
	{
		//mNetWorkState = NetUtil.getNetworkState(this);
		ResourcesManager manager;
		try
		{
			manager = ResourcesManager.getInstance(mcontext);
			manager.createFolder();// 创建相关文件夹
			// 初始化轨迹存储表
			String path = ResourcesManager.getDataPath(ResourcesManager.sqlite) ;
			if (!DataBaseHelper.checkDataBase(path, "guiji.sqlite"))
			{
				DataBaseHelper.CopyDatabase(MyApplication.this,path, "guiji.sqlite");
			}
			//检查小地名数据库
			if (!DataBaseHelper.checkDataBase(path, "db.sqlite"))
			{
				DataBaseHelper.CopyDatabase(MyApplication.this,path, "db.sqlite");
			}
			//检查空间数据库
			/*if (!DataBaseHelper.checkDataBase(path, "gy.geodatabase"))
			{

				DataBaseHelper.CopyDatabase(MyApplication.this,path, "gy.geodatabase");
				String geodatabaseName =  ResourcesManager.getDataBase("gy.geodatabase");
				queryfeature=GeodatabaseHelper.loadGeodatabase(geodatabaseName);

			}else{
				String geodatabaseName =  ResourcesManager.getDataBase("gy.geodatabase");
				queryfeature=GeodatabaseHelper.loadGeodatabase(geodatabaseName);

			}*/
		} catch (Exception e)
		{
			e.printStackTrace();
			//log.debug(e.toString());
			//ToastUtil.setToast(mcontext, "")
		}

	}





	/*	@Override
        public boolean onNetChange()
        {
            if (NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE)
            {
                IntetnetISVisible = false;
            } else
            {
                IntetnetISVisible = true;
            }
            return IntetnetISVisible;
        }*/
	public boolean getNetState () {
		if (NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE)
		{
			IntetnetISVisible = false;
		} else
		{
			IntetnetISVisible = true;
		}
		return IntetnetISVisible;
	}
}
