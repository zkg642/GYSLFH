package com.titan.gyslfh.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.titan.ViewModelHolder;
import com.titan.gyslfh.MainActivity;
import com.titan.gyslfh.TitanApplication;
import com.titan.newslfh.R;
import com.titan.newslfh.databinding.ActivityLoginBinding;
import com.titan.push.GeTui;
import com.titan.push.GeTuiIntentService;
import com.titan.push.GeTuiPushService;
import com.titan.util.ActivityUtils;
import com.titan.util.TitanTextUtils;

import static android.Manifest.permission.READ_CONTACTS;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements ILogin {


    public static final String LOGIN_VIEWMODEL_TAG = "LOGIN_VIEWMODEL_TAG";


    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    private EditText mName;
    private EditText mPassword;
    private View mProgressView;
    private View mLoginFormView;
    Context mContext;

    // DemoPushService.class 自定义服务名称, 核心服务
    private Class userPushService = GeTuiPushService.class;
    
    private String appkey = "";
    private String appsecret = "";
    private String appid = "";

    private LoginViewModel mViewModel;
    private ActivityLoginBinding mViewDataBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        //boolean isfirstlogin= TitanApplication.getInstance().sharedPreferences.getBoolean("isfirstlogin",true);
        //TitanApplication.getInstance().addActivity(this);

        setContentView(R.layout.activity_login);
        //final View root = inflater.inflate(R.layout.activity_login, false);
        if (mViewDataBinding == null) {
            mViewDataBinding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        }
        mViewModel = findOrCreateViewModel();
        mViewDataBinding.setViewmodel(mViewModel);
    }


    private LoginViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
        @SuppressWarnings("unchecked")
        ViewModelHolder<LoginViewModel> retainedViewModel =
                (ViewModelHolder<LoginViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(LOGIN_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
            LoginViewModel viewModel = new LoginViewModel(getApplicationContext(), this);
            // and bind it to this Activity's lifecycle using the Fragment Manager.
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    LOGIN_VIEWMODEL_TAG);
            return viewModel;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        parseManifests();
        intiPermisson();
        //个推初始化
        PushManager.getInstance().registerPushIntentService(mContext.getApplicationContext(), GeTuiIntentService.class);
    }

    /**
     * 初始化权限
     */
    private void intiPermisson() {
        boolean sdCardWritePermission = ContextCompat.checkSelfPermission(mContext, GeTui.GTreqPermission[0]) ==
                PackageManager.PERMISSION_GRANTED;
        boolean phoneSatePermission = ContextCompat.checkSelfPermission(mContext,  GeTui.GTreqPermission[1]) ==
                PackageManager.PERMISSION_GRANTED;
        /**个推权限请求*/
        if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission || !phoneSatePermission) {
            ActivityCompat.requestPermissions(this, GeTui.GTreqPermission,
                    GeTui.GTrequestCode);
        }else {
            PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
        }
    }


    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mName, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                            }
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,
                                           int[] grantResults) {
        if (requestCode == GeTui.GTrequestCode) {
            if ((grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
            } /*else {
                Log.e(TAG, "We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
                        + "functions will not work");
                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
            }*/
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mName.setError(null);
        mPassword.setError(null);

        // Store values at the time of the login attempt.
        String name = mName.getText().toString();
        String phone = mPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
       /* if (!TextUtils.isEmpty(name) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }*/

        // Check for a valid email address.
        if (TextUtils.isEmpty(name)) {
            mName.setError(getString(R.string.error_field_required));
            focusView = mName;
            cancel = true;
        }
        if (TextUtils.isEmpty(phone)) {
            mPassword.setError(getString(R.string.error_field_phone_required));
            focusView = mPassword;
            cancel = true;
        }else if(!TitanTextUtils.isMobileNO(phone)){
            mPassword.setError(getString(R.string.error_field_phonenumber));
            focusView = mPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            if(TitanApplication.IntetnetISVisible){
                mAuthTask = new UserLoginTask(name, phone);
                mAuthTask.execute((Void) null);
            }else{
                Toast.makeText(mContext,"当前网络异常，请检查网络后重试",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mViewDataBinding.loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mViewDataBinding.loginForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mViewDataBinding.loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mViewDataBinding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mViewDataBinding.loginProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mViewDataBinding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mViewDataBinding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            mViewDataBinding.loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 跳转主界面
     */
    @Override
    public void onNext() {
        Intent intent=new Intent(mContext,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void showProgress() {
        showProgress(true);
    }

    @Override
    public void stopProgress() {
        showProgress(false);

    }

    @Override
    public void showToast(String info,int type) {
        Toast.makeText(mContext, info, type).show();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mPassword;

        UserLoginTask(String name, String phone) {
            mName = name;
            mPassword = phone;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                //Thread.sleep(2000);

                return  registerInfo(mName,mPassword);
            } catch (Exception e) {
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                TitanApplication.getInstance().sharedPreferences.edit().putBoolean("isfirstlogin",false).apply();
                Toast.makeText(mContext,"信息注册成功",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(mContext,"信息注册失败，请检查网络",Toast.LENGTH_SHORT).show();
                /*mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();*/
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /** 注册用户设备信息*/
    public boolean registerInfo(String name,String phonenumber)
    {
        //获取个推id
        String cid=PushManager.getInstance().getClientid(mContext);
        /*WebService websUtil = new WebService(mContext);
        return websUtil.registerInfo(TitanApplication.SBH, TitanApplication.XLH, TitanApplication.MOBILE_MODEL,name,phonenumber,cid);*/
        return true;
    }

    /**
     * 配置个推信息
     */
    private void parseManifests() {
        String packageName = getApplicationContext().getPackageName();
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                appid = appInfo.metaData.getString("PUSH_APPID");
                appsecret = appInfo.metaData.getString("PUSH_APPSECRET");
                appkey = appInfo.metaData.getString("PUSH_APPKEY");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
