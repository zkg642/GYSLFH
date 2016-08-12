package com.otitan.gyslfh.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.otitan.customui.DropdownEdittext;
import com.otitan.gyslfh.R;
import com.otitan.util.PadUtil;
import com.otitan.util.ParseXmlService;
import com.otitan.util.ResourcesManager;
import com.otitan.util.ToastUtil;
import com.otitan.util.WebServiceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import Util.ProgressDialogUtil;
import jsqlite.Callback;
import jsqlite.Database;

public class LoginActivity extends Activity
{

	/* 登录 */
	private static final int LOGIN = 0;
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 密码错误 */
	private static final int LOGINfail = 3;
	/*网络异常 */
	private static final int NetWorkfail = 5;
	/* 密码错误 */
	private static final int loadhistorylist = 4;
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 登录返回结果 */
	private JSONObject login_jsonobject;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;
	/* 更新进度条 */
	private ProgressBar mProgress;
	private Dialog downApkDialog;
	// 登陆等待进度条
	DropdownEdittext login_name;
	private EditText login_password;// login_name,
	private CheckBox pwdRemember, zidongLogin;
	// private com.otitan.util.Network_Service networkService;
	private String loginResult = null;
	private String loginName, userID, UNITID, TELNO;// ,
	private String loginPassword;
	private String DQLEVEL, REALNAME;
	private boolean qiehuan = false;
	//private MainFrameTask mMainFrameTask = null;
	// private CustomProgressDialog progressDialog = null;
	//private ProgressDialog progressDialog = null;
	private SharedPreferences sharedPreferences;
	// 定义获取的历史用户登录名
	ArrayList<String> mList = new ArrayList<String>();
	WebServiceUtil websUtil;
	Context mcontext;
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				// 登录
				case LOGIN:
					loginMethod();
					break;
				// 正在下载
				case DOWNLOAD:
					// 设置进度条位置
					mProgress.setProgress(progress);
					break;
				case DOWNLOAD_FINISH:
					// 安装文件
					installApk();
					break;
				case loadhistorylist:
					// 加载历史登录用户
					login_name.setAdapter(mList);
					break;
				case LOGINfail:
					ProgressDialogUtil.stopProgressDialog();
					Toast.makeText(LoginActivity.this, "登录失败",
							Toast.LENGTH_SHORT).show();
					break;
				case NetWorkfail:

					ProgressDialogUtil.stopProgressDialog();
					Toast.makeText(LoginActivity.this, "网络错误",
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		};
	};

	// onCreate
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mcontext=this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (PadUtil.isPad(this))
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		setContentView(R.layout.activity_login);

		sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
		websUtil = new WebServiceUtil(getApplicationContext());
	/*	progressDialog = new ProgressDialog(this,
				ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.hide();*/

		// 获取上一个页面传过来的值
		Intent intent = getIntent();
		qiehuan = intent.getBooleanExtra("isqiehuan", false);

		// 获取页面控件
		login_name = (DropdownEdittext) findViewById(R.id.login_name);
		//login_name = (EditText) findViewById(R.id.login_name);
		initAutoComplete();
		login_password = (EditText) findViewById(R.id.login_password);
		pwdRemember = (CheckBox) findViewById(R.id.checkBoxPass);
		zidongLogin = (CheckBox) findViewById(R.id.checkBoxlogin);

		if (!MyApplication.IntetnetISVisible)
		{
			// 无网络时跳转手机网络设置界面
			ToastUtil.setToast(LoginActivity.this, "网络未连接");
			login_name.setText(sharedPreferences.getString("name", ""));
			login_password.setText(sharedPreferences.getBoolean("remember",
					false) ? sharedPreferences.getString("pwd", "") : "");
			pwdRemember.setChecked(sharedPreferences.getBoolean("remember",
					false));
			zidongLogin.setChecked(sharedPreferences
					.getBoolean("zidong", false));
			// 跳到网络连接设置
			startActivityForResult(new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS), 1);
			Toast.makeText(LoginActivity.this, "进行网络连接设置", Toast.LENGTH_SHORT)
					.show();
		} else
		{
			// 有网络
			init();
		}
	}

	// 设置横竖屏切换布局
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * 获取assets文件夹下db.sqlite文件中的历史用户
	 *
	 * @return
	 */
	public ArrayList<String> getUserList()
	{
		String filename = null;
		try
		{
			filename = ResourcesManager.getInstance(this).getDataBase(
					LoginActivity.this, "db.sqlite");
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		final ArrayList<String> list = new ArrayList<String>();
		try
		{
			Class.forName("jsqlite.JDBCDriver").newInstance();
			Database db = new jsqlite.Database();
			db.open(filename, jsqlite.Constants.SQLITE_OPEN_READWRITE);
			String sql = "select * from user ";
			db.exec(sql, new Callback()
			{

				@Override
				public boolean newrow(String[] data)
				{
					list.add(data[0]);
					return false;
				}

				@Override
				public void columns(String[] arg1)
				{

				}

				@Override
				public void types(String[] arg2)
				{

				}
			});
			db.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	public void initAutoComplete()
	{

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				mList.clear();
				mList = getUserList();
				Message msg = new Message();
				msg.what = loadhistorylist;
				mHandler.sendMessage(msg);
			}
		}).start();
		login_name.setAdapter(mList);
	}

	private void loginMethod()
	{
		// 没有网络
		if (loginResult == null
				|| loginResult
				.equalsIgnoreCase(WebServiceUtil.NetworkException))
		{
			Toast.makeText(LoginActivity.this, WebServiceUtil.NetworkException,
					Toast.LENGTH_SHORT).show();
		} else if (loginResult.equalsIgnoreCase(WebServiceUtil.CharException))
		{
			Toast.makeText(LoginActivity.this, WebServiceUtil.CharException,
					Toast.LENGTH_SHORT).show();
		} else
		{
			final JSONObject object;
			try
			{
				object = new JSONObject(loginResult);
				if (object.getString("ds") != null)
				{
					JSONObject result = object.optJSONArray("ds")
							.optJSONObject(0);
					DQLEVEL = result.getString("DQLEVEL");// 地区级别
					REALNAME = result.getString("REALNAME");// 真实姓名
					userID = result.getString("ID");// 用户id
					UNITID = result.getString("UNITID");//
					TELNO = result.getString("TELNO");

					Intent intent = new Intent(LoginActivity.this,
							MapActivity.class);
					LoginActivity.this.startActivity(intent);
					LoginActivity.this.finish();
					// 记住密码和自动登入
					sharedPreferences.edit().putString("DQLEVEL", DQLEVEL)
							.commit();
					sharedPreferences.edit().putString("REALNAME", REALNAME)
							.commit();
					sharedPreferences.edit().putString("TELNO", TELNO).commit();
					sharedPreferences.edit().putString("userID", userID)
							.commit();
					sharedPreferences.edit().putString("UNITID", UNITID)
							.commit();
					sharedPreferences.edit().putString("name", loginName)
							.commit();
					sharedPreferences
							.edit()
							.putString(
									"pwd",
									pwdRemember.isChecked() ? loginPassword
											: "").commit();
					sharedPreferences.edit()
							.putBoolean("remember", pwdRemember.isChecked())
							.commit();
					sharedPreferences.edit()
							.putBoolean("zidong", zidongLogin.isChecked())
							.commit();
					boolean flag = false;
					for (int i = 0; i < mList.size(); i++)
					{
						if (loginName.equals(mList.get(i)))
						{
							flag = true;
							return;
						}
					}
					if (!flag)
					{
						addData();
					}
					return;
				}
				if (object.getString("re") == null
						|| object.getString("re").equals("fail"))
				{
					String eMsg = object.getString("eMsg").toString();
					Toast.makeText(getApplicationContext(), eMsg,
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		ProgressDialogUtil.stopProgressDialog();
	}

	// 确定按钮
	public void loginSure(View view)
	{

		loginName = login_name.getText().toString();
		loginPassword = login_password.getText().toString();
		if (loginName == null || loginName.equals(""))
		{
			Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (loginPassword == null || loginPassword.equals(""))
		{
			Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		//progressDialog.show();
		ProgressDialogUtil.startProgressDialog(mcontext,"登录中....");
		new Thread(new Runnable() {

			@Override
			public void run() {
				checkLogin(loginName, loginPassword);
			}
		}).start();
		return;
	}

	// 初始化
	private void init()
	{
		// initView();
		try
		{
			boolean flag = isUpdate();
			if (flag)
			{
				// 显示提示对话框
				showVersionDialog();
			} else
			{
				initView();
			}
			return;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void initView()
	{
		if (qiehuan)
		{
			login_name.setText("");
			login_password.setText("");
			pwdRemember.setChecked(false);
			zidongLogin.setChecked(false);
		} else
		{
			login_name.setText(sharedPreferences.getString("name", ""));
			login_password.setText(sharedPreferences.getBoolean("remember",
					false) ? sharedPreferences.getString("pwd", "") : "");
			pwdRemember.setChecked(sharedPreferences.getBoolean("remember",
					false));
			zidongLogin.setChecked(sharedPreferences
					.getBoolean("zidong", false));
			if (sharedPreferences.getBoolean("zidong", false))
			{
				loginName = sharedPreferences.getString("name", "");
				loginPassword = sharedPreferences.getString("pwd", "");

				ProgressDialogUtil.startProgressDialog(mcontext,"登录中...");
				new Thread(new Runnable() {

					@Override
					public void run() {
						checkLogin(loginName, loginPassword);
					}
				}).start();

				return;
			}
			pwdRemember
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
					{

						public void onCheckedChanged(CompoundButton buttonView,
													 final boolean isChecked)
						{
							// 当记住密码选择为false时，自动登入也要false
							if (!isChecked)
							{
								runOnUiThread(new Runnable()
								{
									public void run()
									{
										zidongLogin.setChecked(isChecked);
									}
								});
							}
						}
					});
			zidongLogin
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
					{

						public void onCheckedChanged(CompoundButton buttonView,
													 final boolean isChecked)
						{
							// 当自动登入为true，记住密码也要为true
							if (isChecked)
							{
								runOnUiThread(new Runnable()
								{
									public void run()
									{
										pwdRemember.setChecked(isChecked);
									}
								});
							}
						}
					});
		}
	}

	// 后台连接登陆
	public void checkLogin(String loginName, String loginPassword)
	{
		Message msg = new Message();
		loginResult = websUtil.CheckLogin(loginName, loginPassword);
		try
		{
			login_jsonobject = new JSONObject(loginResult);

			if (login_jsonobject.getString("re").equals("fail"))
			{
				msg.what = LOGINfail;
			} else if(loginResult.equals(WebServiceUtil.netException)){
				msg.what=NetWorkfail;
			}else
			{
				msg.what = LOGIN;
			}
		} catch (JSONException e)
		{
			msg.what=NetWorkfail;
		}

		mHandler.sendMessage(msg);
	/*	Intent intent = new Intent(LoginActivity.this,
				MapActivity.class);
		LoginActivity.this.startActivity(intent);
		LoginActivity.this.finish();*/
	}

	@Override
	protected void onDestroy()
	{
		ProgressDialogUtil.stopProgressDialog();

		/*if (mMainFrameTask != null && !mMainFrameTask.isCancelled())
		{
			mMainFrameTask.cancel(true);
		}*/
		super.onDestroy();
	}

/*	private void startProgressDialog()
	{
		if (progressDialog == null)
		{
			 //progressDialog = CustomProgressDialog.createDialog(mcontext);
			progressDialog.setMessage("正在加载中...");
		}
		progressDialog.show();
	}*/

	/*private void stopProgressDialog()
	{
		if (progressDialog != null)
		{
			progressDialog.dismiss();
			progressDialog = null;
		}
	}*/

/*	public class MainFrameTask extends AsyncTask<Integer, String, Integer>
	{
		@SuppressWarnings("unused")
		private LoginActivity mainFrame = null;

		public MainFrameTask(LoginActivity mainFrame)
		{
			this.mainFrame = mainFrame;
		}

		protected void onCancelled()
		{
			ProgressDialogUtil.stopProgressDialog();
			super.onCancelled();
		}

		protected Integer doInBackground(Integer... params)
		{

			try
			{
				Thread.sleep(100 * 1000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			return null;
		}

		protected void onPreExecute()
		{
			startProgressDialog();
		}

		protected void onPostExecute(Integer result)
		{
			stopProgressDialog();
		}
	}*/

	/**
	 * 检查软件是否有更新版本
	 *
	 * @throws IOException
	 */
	private boolean isUpdate() throws IOException
	{
		// 获取当前软件版本
		double versionCode = getVersionCode(this);
		// 把version.xml放到网络上，然后获取文件信息
		/* 获取xml */
		// http://gis.gyforest.com:8088/fireservice/apk/version.xml
		URL url = new URL(getResources().getString(R.string.versionxml));
		URLConnection connection = url.openConnection();

		HttpURLConnection httpConnection = (HttpURLConnection) connection;
		int responseCode = 1;
		try
		{
			responseCode = httpConnection.getResponseCode();
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
		InputStream inStream = null;
		if (responseCode == HttpURLConnection.HTTP_OK)
		{
			inStream = httpConnection.getInputStream();
		}
		// InputStream inStream =
		// ParseXmlService.class.getClassLoader().getResourceAsStream("version.xml");
		// 解析XML文件。 由于XML文件比较小，因此使用DOM方式进行解析
		ParseXmlService service = new ParseXmlService();
		try
		{
			mHashMap = service.parseXml(inStream);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if (null != mHashMap)
		{
			double serviceCode = Double.valueOf(mHashMap.get("version"));
			// 版本判断
			if (serviceCode > versionCode)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取软件版本号
	 *
	 * @param context
	 * @return
	 */
	private double getVersionCode(Context context)
	{
		double versionCode = 0;
		try
		{
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = Double.parseDouble(context.getPackageManager()
					.getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 显示软件更新对话框，使用系统控件写的 不要删除 做参
	 */
	// private void showNoticeDialog() {
	// // 构造对话框
	// AlertDialog.Builder builder = new Builder(mContext,R.style.DialogTheme);
	// builder.setTitle(R.string.soft_update_title);
	// builder.setMessage(R.string.soft_update_info);
	// // 更新
	// builder.setPositiveButton(R.string.soft_update_updatebtn,
	// new OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// // 显示下载对话框
	// showDownloadDialog();
	// }
	// });
	// // 稍后更新
	// builder.setNegativeButton(R.string.soft_update_later,
	// new OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// initView();
	// }
	// });
	// Dialog noticeDialog = builder.create();
	// noticeDialog.show();
	// }
	// 自定义版本更新提示dialog
	public void showVersionDialog()
	{
		final LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
		View view = inflater.inflate(R.layout.dialog_appversionupdate, null);
		final Dialog updateDialog = new Dialog(LoginActivity.this,
				R.style.Dialog);
		updateDialog.setContentView(view);
		updateDialog.setCanceledOnTouchOutside(false);
		Button updateBtn = (Button) updateDialog
				.findViewById(R.id.update_now_btn);
		// 更新
		updateBtn.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				updateDialog.dismiss();
				// 显示下载对话框
				showDownloadDialog();
			}
		});
		Button unUpdateBtn = (Button) updateDialog
				.findViewById(R.id.unUpdate_now_btn);
		unUpdateBtn.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				updateDialog.dismiss();
				initView();
			}
		});
		updateDialog.show();
	}

	/**
	 * 显示软件下载对话框,使用系统控件写的 不要删除 做参考
	 */
	// private void showDownloadDialog() {
	// // 构造软件下载对话框
	// AlertDialog.Builder builder = new Builder(mContext,R.style.DialogTheme);
	// builder.setTitle(R.string.soft_updating);
	// // 给下载对话框增加进度条
	// final LayoutInflater inflater = LayoutInflater.from(mContext);
	// View v = inflater.inflate(R.layout.softupdate_progress, null);
	// mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
	// builder.setView(v);
	// // 取消更新
	// builder.setNegativeButton(R.string.btn_edit_discard,
	// new OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// // 设置取消状态
	// cancelUpdate = true;
	// }
	// });
	// mDownloadDialog = builder.create();
	// mDownloadDialog.show();
	// // 下载文件
	// downloadApk();
	// }
	/**
	 * 显示软件下载对话框
	 */
	public void showDownloadDialog()
	{
		final LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
		View view = inflater.inflate(R.layout.dialog_download_apk, null);
		downApkDialog = new Dialog(LoginActivity.this, R.style.Dialog);
		downApkDialog.setContentView(view);
		mProgress = (ProgressBar) downApkDialog
				.findViewById(R.id.update_progress);
		Button downCancleBtn = (Button) downApkDialog
				.findViewById(R.id.down_cancle_btn);
		downCancleBtn.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				downApkDialog.dismiss();
				// 设置取消状态
				cancelUpdate = true;
			}
		});
		downApkDialog.show();
		// 下载文件
		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk()
	{
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 *
	 */
	private class downloadApkThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED))
				{
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "download";
					URL url = new URL(mHashMap.get("url"));
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do
					{
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
					cancelUpdate = false;
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			// 取消下载对话框显示
			downApkDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk()
	{
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists())
		{
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		startActivity(i);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{

		return false;
		// return super.onPrepareOptionsMenu(menu);
	}

	public void addData()
	{
		String filename = null;
		try
		{
			filename = ResourcesManager.getInstance(this).getDataBase(
					LoginActivity.this, "db.sqlite");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			Class.forName("jsqlite.JDBCDriver").newInstance();
			Database db = new jsqlite.Database();
			db.open(filename, jsqlite.Constants.SQLITE_OPEN_READWRITE);
			String sql = "insert into user values('" + loginName + "','"
					+ loginPassword + "') ";
			db.exec(sql, null);
			db.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}