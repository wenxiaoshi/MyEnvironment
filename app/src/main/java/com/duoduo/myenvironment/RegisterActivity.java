package com.duoduo.myenvironment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity {
    EditText username,password,passwrord2,phone;
    Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自动生成的方法存根
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username=(EditText) findViewById(R.id.editText_uesrname);
        password=(EditText) findViewById(R.id.editText_pass);
        passwrord2=(EditText) findViewById(R.id.editText_pass2);
        phone=(EditText) findViewById(R.id.editText_phonenum);
        register=(Button) findViewById(R.id.button_register);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String userName = username.getText().toString();
                String passWord = password.getText().toString();
                String phoneNumber = phone.getText().toString();
                String passWord2 = passwrord2.getText().toString();
                if (userName.length() != 0 && passWord.equals(passWord2) && phoneNumber.length() == 11) {
                    register(userName, passWord);
                } else {
                    return;
                }
            }
            private void register(String userName, String passWord) {
                // TODO 自动生成的方法存根
                MyUser user = new MyUser();
                user.setUsername(userName);
                user.setPassword(passWord);
                user.setDeviceType("android");
                user.signUp(RegisterActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        // TODO 自动生成的方法存根
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO 自动生成的方法存根
                        Toast.makeText(RegisterActivity.this, "注册失败,请检查网络或更换用户名", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
