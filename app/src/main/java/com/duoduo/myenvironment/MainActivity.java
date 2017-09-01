package com.duoduo.myenvironment;

import android.*;
import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends AppCompatActivity {
    EditText LoginUserName,LoginPassword;
    Button Login,Register;
    private static final String FAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "6a34bd76a31afa5e4b3e3a5cbb877417");
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, "6a34bd76a31afa5e4b3e3a5cbb877417");

        LoginUserName=(EditText) findViewById(R.id.editText_login_username);
        LoginPassword=(EditText) findViewById(R.id.editText_login_password);
        Register=(Button) findViewById(R.id.button_login_register);
        Login=(Button) findViewById(R.id.button_login);
        Register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent2);
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String Login_userName=LoginUserName.getText().toString();
                String Login_passWord=LoginPassword.getText().toString();
                if (Login_userName.length()!=0&&Login_passWord.length()!=0) {
                    login(Login_userName,Login_passWord);

                }
                else{
                    return;
                }

            }
        });
    }

    protected void login(String login_userName, String login_passWord) {
        BmobUser login=new BmobUser();
        login.setUsername(login_userName);
        login.setPassword(login_passWord);
        login.login(this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO 自动生成的方法存根
                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(MainActivity.this, DeviceScanActivity.class);
                startActivity(intent3);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO 自动生成的方法存根
                Toast.makeText(MainActivity.this, "登录失败,请检查网络或用户信息", Toast.LENGTH_LONG).show();
            }
        });

    }



}