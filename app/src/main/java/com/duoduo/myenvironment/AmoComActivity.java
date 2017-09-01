package com.duoduo.myenvironment;

import android.Manifest;
import android.app.Activity;
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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.gsm.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import cn.bmob.v3.BmobPushManager;

public class AmoComActivity extends Activity implements View.OnClickListener {
	private final static String TAG = "AmoComActivity";// DeviceScanActivity.class.getSimpleName();

	static TextView Text_Recv;
	static String Str_Recv;
    static public String information;
	static String ReciveStr;
	static ScrollView scrollView;
	static Handler mHandler = new Handler();
	static boolean ifDisplayInHexStringOnOff = true;
	static boolean ifDisplayTimeOnOff = true;
	static TextView textview_recive_send_info;
	static int Totol_Send_bytes = 0;
	static int Totol_recv_bytes = 0;

	private static final String FAG = "AmoComActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other);
		findViewById(R.id.button_clear).setOnClickListener(this);
		findViewById(R.id.button_detail).setOnClickListener(this);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String mac_addr = bundle.getString("mac_addr");
		String char_uuid = bundle.getString("char_uuid");
		TextView tv_mac_addr = (TextView) this
				.findViewById(R.id.textview_mac_addr);
		TextView tv_char_uuid = (TextView) this
				.findViewById(R.id.textview_char_uuid);

		tv_mac_addr.setText("设备地址:" + mac_addr);
		tv_char_uuid.setText("特征值UUID:\n" + char_uuid);

		textview_recive_send_info = (TextView) this
				.findViewById(R.id.textview_recive_send_info);

		Text_Recv = (TextView) findViewById(R.id.device_address);
		Text_Recv.setGravity(Gravity.CLIP_VERTICAL | Gravity.CLIP_HORIZONTAL);
		ReciveStr = "";
		Text_Recv.setMovementMethod(ScrollingMovementMethod.getInstance());
		scrollView = (ScrollView) findViewById(R.id.scroll);
		Totol_Send_bytes = 0;
		Totol_recv_bytes = 0;
		update_display_send_recv_info(Totol_Send_bytes, Totol_recv_bytes);
		ifDisplayInHexStringOnOff = true;
		ifDisplayTimeOnOff = true;

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.button_clear:
			Text_Recv.setText("");
			ReciveStr = "";
			Totol_Send_bytes = 0;
			Totol_recv_bytes = 0;
			update_display_send_recv_info(Totol_Send_bytes, Totol_recv_bytes);
			scrollView.fullScroll(ScrollView.FOCUS_UP);// 滚动到顶
			break;
		case R.id.button_detail:
			startActivity(new Intent(AmoComActivity.this, DetailActivity.class));
			 startActivity(new Intent (AmoComActivity.this,
					 DetailActivity.class) );
			break;
		}
	}
	static float stringToFloat(String floatstr)
	{
		Float floatee;
		floatee = Float.valueOf(floatstr);
		return floatee.floatValue();
	}
	public static synchronized void char6_display(String str, byte[] data,
			String uuid) {

		Log.i(TAG, "char6_display str = " + str);
		if (uuid.equals(DeviceScanActivity.UUID_HERATRATE)) {
			Log.i(TAG, "if (uuid.equals(DeviceScanActivity.UUID_HERATRATE)) ");
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss ");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String TimeStr = formatter.format(curDate);

			byte[] ht = new byte[str.length()];

			String DisplayStr = "[" + TimeStr + "]pm2.5浓度： " + "HeartRate : " + data[0]
					+ "=" + data[1];
			Str_Recv = DisplayStr + "\r\n";
		} else if (uuid.equals(DeviceScanActivity.UUID_TEMPERATURE)) // pm2.5测量
		{
			byte[] midbytes = str.getBytes();
			String HexStr = Utils.bytesToHexString(midbytes);
			Str_Recv = HexStr;
		} else if (uuid.equals(DeviceScanActivity.UUID_CHAR6)) // amomcu 的串口透传
		{
					SimpleDateFormat formatter = new SimpleDateFormat(
							"HH:mm:ss ");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String TimeStr = formatter.format(curDate);

		    	double pm25 = stringToFloat(str);

		        	if (pm25>=0&&pm25<=50)
					{
						  information = str+" 空气质量：优";
					}else if(pm25>50&&pm25<=100)
					{
						information = str+" 空气质量：良";
					}
					else if(pm25>100&&pm25<=150)
					{
						information = str+" 轻度污染";

					}
					else if(pm25>150&&pm25<=200)
					{
						information = str+" 不健康";
					}
					else if(pm25>200&&pm25<=300)
					{
						information = str+" 危险";
					}
					else if(pm25>300&&pm25<=500)
					{
						information = str+" 有毒";
					}
					else if(pm25>500)
					{
						information = "啊哈哈哈哈出错了 ";
					}
					String DisplayStr = "[" + TimeStr + "] " + information;
					Str_Recv = DisplayStr + "\r\n";

		} else
		{

					SimpleDateFormat formatter = new SimpleDateFormat(
							"HH:mm:ss ");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String TimeStr = formatter.format(curDate);

					String DisplayStr = "[" + TimeStr + "] " + str;
					Text_Recv.append(DisplayStr + "\r\n");
					Str_Recv = DisplayStr + "\r\n";

		}

		Totol_recv_bytes += str.length();

		mHandler.post(new Runnable() {
			@Override
			public synchronized void run() {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);// 滚动到底
				Text_Recv.append(Str_Recv);
				update_display_send_recv_info(Totol_Send_bytes,
						Totol_recv_bytes);
			}
		});

	}
	public synchronized static String GetLastData() {
		String string = Str_Recv;
		return string;
	}
	public synchronized static void update_display_send_recv_info(int send,
			int recv) {
		String info1 = String.format("发送%4d,接收%4d [字节]", send, recv);
		textview_recive_send_info.setText(info1);
	}
}