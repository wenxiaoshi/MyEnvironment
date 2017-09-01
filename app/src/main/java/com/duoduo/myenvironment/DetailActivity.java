package com.duoduo.myenvironment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import java.util.ArrayList;
import java.util.Date;

public class DetailActivity extends Activity implements AMapLocationListener{
	private final static String TAG = "DetailActivity";
	public static final int REFRESH = 0x000001;
	private Handler mHandler = null;
	public static  TextView tv,address,location;
	private Button send,sendToWeixin,share;
	private IWXAPI iwxapi,api;
	public static final String APP_ID="wx93112a097d37ed29";

	//短信发送模块初始化
	PendingIntent paIntent;
	SmsManager smsManager;
	//定位模块初始化
	LocationManagerProxy mLocationManagerProxy;
	private LocationManagerProxy mAMapLocManager = null;
	private AMapLocation aMapLocation;// 用于判断定位超时
	@Override
	//主函数开始
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);// 本段代码的功能实现基于datail页面

        //使用微信接口的初始化定义
		api = WXAPIFactory.createWXAPI(this,APP_ID);
		// 将APP_ID注册到微信中
		api.registerApp(APP_ID);


		send=(Button) findViewById(R.id.button_send);
		share = (Button) findViewById(R.id.button_sendToWX);
		LocationManagerProxy mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork,2000,15,this);
//		第一个参数是定位类型，现在是混合定位，第二是定位周期，多久回调一次，-1为单次定位，第三个只在gps定位有效，移动15米回调
		paIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0);
		smsManager = SmsManager.getDefault();
//		Log.i(TAG, "send按钮之前");

		tv = (TextView) findViewById(R.id.textView_detail);
		address=(TextView) findViewById(R.id.textView_address);
		location=(TextView) findViewById(R.id.textView_jianduan_location);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == REFRESH) {
					// Log.i(TAG, "handleMessage");
				final String gpsLocation = location.getText().toString();
				final	String str = AmoComActivity.GetLastData();
				final  String sendTo =gpsLocation+"\n"+str;
					tv.setText("空气质量指数 ：\n" + str);
                    String sendtext =tv.getText().toString();
				//	Log.v(TAG, "获得的内容：" + in);


					send.setOnClickListener(new View.OnClickListener() {
						String pm25 = AmoComActivity.GetLastData();
						final EditText txtPhoneNo = (EditText) findViewById(R.id.editText_receiverID);
						final String txtSend = pm25 + "结束";
						@Override
						public void onClick(View v) {

							// TODO 自动生成的方法存根
							Toast.makeText(DetailActivity.this,
									"点击send按钮",
									Toast.LENGTH_LONG).show();
							String phoneNo = txtPhoneNo.getText().toString();

							Log.i(TAG, "条件判断之前");
							if (phoneNo.length() == 11) {

//								Log.v(TAG, "will begin sendSMS");
//								Toast.makeText(DetailActivity.this,
//										"即将开始发送",
//										Toast.LENGTH_LONG).show();
								//自动拆分短信
								ArrayList<String> texts = smsManager.divideMessage(sendTo);
								//迭代发送
								for (String text : texts) {
//									Log.v(TAG, "before sendSMS");
										smsManager.sendTextMessage(phoneNo, null, sendTo, paIntent,
												null);

//									Log.v(TAG, "SMS内容：" + sendTo);
									Toast.makeText(DetailActivity.this,
											"发送成功"+sendTo,
											Toast.LENGTH_LONG).show();
								}
							} else
								Toast.makeText(DetailActivity.this,
										"请重新输入",
										Toast.LENGTH_LONG).show();
						}
					});

					share.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
                        //  wechatShare();
						//	Launch_weixin();
							Send_Weixin();
							// TODO 自动生成的方法存根
						}
					});
				}
				super.handleMessage(msg);
			}
		};

		new MyThread().start();


	}
	// send to weixin



// 向朋友圈发送文本
	private void Send_Weixin() {
		final EditText editor = new EditText(this);
		editor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		final String gpsLocation = location.getText().toString();
		final	String str = AmoComActivity.GetLastData();
		final  String sendTo =gpsLocation+"\n"+str;
		editor.setText(sendTo);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.sym_def_app_icon);
		builder.setTitle("分享我的环境信息");
		// 将EditText控件与对话框绑定
		builder.setView(editor);
		builder.setMessage("请输入要分享的文本");
		builder.setPositiveButton("朋友圈", new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 获取待分享的文本
				String text = editor.getText().toString();
				if (text == null || text.length() == 0) {
					return;
				}
				//第一步，创建一个用于封装待分享的WXTextObject对象
				WXTextObject textObj = new WXTextObject();
				textObj.text = text;
				//第二步，创建	WXMediaMessage对象，该对象用于android客户端向微信发送数据
				WXMediaMessage msg = new WXMediaMessage();
				msg.mediaObject = textObj;
				msg.description = text;
				//第三步，创建一个用于请求微信客户端的SendMessageToWX.req对象
				SendMessageToWX.Req req = new SendMessageToWX.Req();

// 构造一个Req
				req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
				req.message = msg;
				req.scene = SendMessageToWX.Req.WXSceneTimeline;

				// 调用api接口发送数据到微信
				api.sendReq(req);
				Toast.makeText(DetailActivity.this,
						String.valueOf(api.sendReq(req)),
						Toast.LENGTH_LONG).show();

			}
		});
		builder.setNegativeButton("微信好友", new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 获取待分享的文本
				String text = editor.getText().toString();
				if (text == null || text.length() == 0) {
					return;
				}
				//第一步，创建一个用于封装待分享的WXTextObject对象
				WXTextObject textObj = new WXTextObject();
				textObj.text = text;
				//第二步，创建	WXMediaMessage对象，该对象用于android客户端向微信发送数据
				WXMediaMessage msg = new WXMediaMessage();
				msg.mediaObject = textObj;
				msg.description = text;
				//第三步，创建一个用于请求微信客户端的SendMessageToWX.req对象
				SendMessageToWX.Req req = new SendMessageToWX.Req();

// 构造一个Req
				req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
				req.message = msg;
				req.scene = SendMessageToWX.Req.WXSceneSession;

				// 调用api接口发送数据到微信
				api.sendReq(req);
				Toast.makeText(DetailActivity.this,
						String.valueOf(api.sendReq(req)),
						Toast.LENGTH_LONG).show();

			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}



	private Runnable stop = new Runnable() {

		@Override
		public void run() {
			if (aMapLocation == null) {
				Toast.makeText(getApplicationContext(), "12秒内还没有定位成功，停止定位", Toast.LENGTH_SHORT).show();
				stopLocation();// 销毁掉定位
			}
		}
	};
	@Override
	protected void onPause() {
		super.onPause();
		stopLocation();// 停止定位
	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (mAMapLocManager != null) {
			mAMapLocManager.removeUpdates(this);
			mAMapLocManager.destory();
		}
		mAMapLocManager = null;
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {
	//回调用到的定位方法
		if (aMapLocation!=null&&aMapLocation.getAMapException().getErrorCode()==0){
			Log.e("my environment", aMapLocation.toString());


			double geoLat = aMapLocation.getLatitude();
			double geoLng = aMapLocation.getLongitude();
			double geoAcc = aMapLocation.getAccuracy();
			String geoPro = aMapLocation.getProvider();


			String cityCode = "";
		    String desc = "正在获取";
			Bundle locBundle = aMapLocation.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}

			String str2 = ("经度：" + geoLng + "\n纬度：" + geoLat + "\n精度:" + geoAcc+ "米"
					+ "\n定位方式:" +geoPro+ "\n定位时间:"
					+ new Date(aMapLocation.getTime()).toLocaleString() + "\n城市编码:"
					+ cityCode + "\n区域编码:" + aMapLocation.getAdCode());
		 	String display = ( "详细地址：\n"+desc );
			address.setText(str2);
			location.setText(display);
		}

		//	tv_address.setText(str);
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}
	@Override
	public void onDestroy() {
		super.onDestroy();
//		mLocationManagerProxy.destroy();
		stopLocation();
	}

	public class MyThread extends Thread {
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {

				Message msg = new Message();
				msg.what = REFRESH;
				mHandler.sendMessage(msg);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}



		}
	}
	public void onClick(View v) {
		// TODO 自动生成的方法存根
	}
	//为请求生成一个唯一的标识 在代码最后
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}