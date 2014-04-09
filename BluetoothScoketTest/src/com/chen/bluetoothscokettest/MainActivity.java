package com.chen.bluetoothscokettest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.R.drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {
	
	Button button_connect;
	Button button_disconnect;
	EditText edit_receive;
	EditText edit_send;
	Button button_send;
	
	SoundPool snd = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
    int hitOk;
    int sendOk;
    int disconnectOk;
    int hitButton;
    
    String address=null;
	public static BluetoothDevice btDev;
	public static BluetoothSocket btSocket;
	InputStream is =null;
    OutputStream os = null;
    static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
//    static final String UUU_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	private BluetoothAdapter myBluetoothAdapter=null; 
	BluetoothAdapter adapter;
	boolean bluetoothConnectFlag=false;
	boolean hitFlag=false;
	Method m;
	
	boolean bluetoothRequestFlag=false;
	private Timer timer = new Timer();
	private TimerTask task;
	private Timer timer_surveillance=new Timer();
	private TimerTask task_surveillance;
	byte[] cmd=new byte[]{0x23,0x30,0x04,0x0D};
	private String receiver;
	private int leng;
	byte[] buffer=new byte[20];
	Thread thread;
	thread_receive runn;
//	thread_receive runn=new thread_receive();
//	Thread thread=new Thread(runn);
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        button_connect=(Button)findViewById(R.id.bn1);
        button_disconnect=(Button)findViewById(R.id.bn2);
        button_send=(Button)findViewById(R.id.bn3);
        edit_send=(EditText)findViewById(R.id.ed2);
        edit_receive=(EditText)findViewById(R.id.ed1);
        
        hitOk=snd.load(MainActivity.this, R.raw.ping_short, 5);
        sendOk=snd.load(MainActivity.this, R.raw.send, 5);
        disconnectOk=snd.load(MainActivity.this, R.raw.button20, 5);
        hitButton=snd.load(MainActivity.this, R.raw.button44, 5);
        
        connectBluetooth();
        
        button_connect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				snd.play(hitOk, 1f, 1f, 0, 0, 1);
				
			}
		});
        
        button_disconnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				snd.play(disconnectOk, 1f, 1f, 0, 0, 1);
			}
		});
        
        button_send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				if(timer==null&&task==null){
			    	task=new TimerTask(){

						@Override
						public void run() {
							try{
								os=btSocket.getOutputStream();
								os.write(cmd);
								os.flush();
							}catch(IOException e){
								e.printStackTrace();
							}
							Log.d("发送计时器","在运行.........");
						}};			
						timer.schedule(task, 0,1000);
//			    	}
//				snd.play(sendOk, 1f, 1f, 0, 0, 1);
				snd.play(hitButton, 0.5f, 0.5f, 0, 0, 1);
//				allwayssend();
			}
		});
        
        task_surveillance=new TimerTask() {
			
			@Override
			public void run() {
				if(!bluetoothConnectFlag){
					connectBluetooth();
					Log.d("监视计时器", "运行中.......");
				}
			}
		};
		timer_surveillance.schedule(task_surveillance, 0,1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void connectBluetooth(){
    	myBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    	if(myBluetoothAdapter==null){
    		return;
    	}
    	if(!myBluetoothAdapter.isEnabled()&&!hitFlag){//&&!bluetoothRequestFlag){
//    		myBluetoothAdapter.enable();
    		Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		startActivity(intent);
    		hitFlag=true;
//    		bluetoothRequestFlag=true;
    	}else{
    		try{
	    		 address = "98:D3:31:B0:26:0A";//新模块地址
			     UUID uuid = UUID.fromString(SPP_UUID);  
			     adapter =BluetoothAdapter.getDefaultAdapter();
			     btDev = adapter.getRemoteDevice(address);                
	             adapter.cancelDiscovery();                         	                	                                   	                	
		    	 btSocket=null;   
		    	 btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
		    	 Log.d("运行标记", "运行到了btSocket.connect()之前......");
		    	 btSocket.connect();
		    	 
//			     try {
//		    			m = btDev.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
//		    	 try {
//		    			btSocket = (BluetoothSocket) m.invoke(btDev, 1);
//		    			adapter.cancelDiscovery();
//		    	 try {
//		    			btSocket.connect();
//		    	 } catch (IOException e) {
//		    		 	bluetoothConnectFlag=false;
//		    		 	Log.e("蓝牙连接异常","异常情况");
//		    			e.printStackTrace();
//		    	 }
//		    	 } catch (IllegalArgumentException e) {
//		    			e.printStackTrace();
//		    	 } catch (IllegalAccessException e) {
//		    			e.printStackTrace();
//		    	 } catch (InvocationTargetException e) {
//		    			e.printStackTrace();
//		    	 }
//		    	 } catch (SecurityException e) {
//		    			e.printStackTrace();
//		    	 } catch (NoSuchMethodException e) {
//		    			e.printStackTrace();
//		    	 }
		    	 
		    	 bluetoothConnectFlag=btSocket.isConnected();
		    	 Log.d("是否执行到了","..........这儿的标记........."+btSocket.isConnected());
		    	 //allwayssend();
//		    	 runn=new thread_receive();
//		    	 Thread thread=new Thread(runn);
//		         new Thread(runn).start();		    	 
	    	 }catch(IOException e){
	    		 bluetoothConnectFlag=false;
//	    		 Log.e("蓝牙连接异常", e.getStackTrace().toString());
	    		 Log.e("蓝牙连接异常","异常情况");
	    		 e.printStackTrace();
	    	 }
    	}
    	
    }
    
    private void allwayssend(){
    	if(timer==null&&task==null){
    	task=new TimerTask(){

			@Override
			public void run() {
				try{
					os=btSocket.getOutputStream();
					os.write(cmd);
					os.flush();
				}catch(IOException e){
					e.printStackTrace();
				}
				Log.d("发送计时器","在运行.........");
			}};			
			timer.schedule(task, 0,1000);
    	}
    }
    
    class thread_receive implements Runnable{
//    	volatile boolean threadStopFlag=false;
		@Override
		public void run() {
			while(true){
				try{
					is = btSocket.getInputStream();
					leng=is.read(buffer);
					if(leng!=0){
						receiver=String.valueOf(buffer[0]);
						receiver=receiver+"  ";
						for(int i=0;i<buffer.length;i++){
							buffer[i]=0;
						}
						handler.sendEmptyMessage(1);
					}
				}catch(IOException E){
					E.printStackTrace();
					Log.e("接收线程出错","出错");
				}
				Log.d("接收线程", "在运行......");
				Log.d("btSocket的状态","  "+btSocket.isConnected());
			}
		}}
    
    Handler handler=new Handler(){
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
            edit_receive=(EditText)findViewById(R.id.ed1);
            switch(msg.what){
            case 1:
            	edit_receive.append(receiver);
            	break;
            }
    	}
    };
    
    /**覆写返回键监听**/
    public void onBackPressed(){
//    	runn.threadStopFlag=true;
    	ext();
    }
    
    /**该方法用于退出程序**/
    private void ext(){
    	new AlertDialog.Builder(MainActivity.this).setTitle("退出系统提示：")
    	.setMessage("确定要退出系统吗？").setPositiveButton("是", 
    			new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {				
			try {
				   if(timer!=null){
	        		   timer.cancel();
	        		   timer=null;
	        	   }
	        	   if(task!=null){
	        		   task=null;
	        	   }
	        	   
	        	   if(timer_surveillance!=null){
	        		   timer_surveillance.cancel();
	        		   timer_surveillance=null;
	        	   }
	        	   if(task_surveillance!=null){
	        		   task_surveillance=null;
	        	   }
	        	   
	        	   if(btSocket!=null){
						try {	
							btSocket.close();}
						catch(Exception e){
							e.printStackTrace();
						}
					}
	        	   if(is!=null){
	        		   is.close();
	        		   is=null;
	        	   }
	        	   if(os!=null){
	        		   os.close();
	        		   os=null;
	        	   }
	        	   
	        	   if(myBluetoothAdapter.isEnabled()){
		   				try {	
		   					myBluetoothAdapter.disable();}
		   				catch(Exception e){
		   					e.printStackTrace();
		   				}
		   			}
	        	   
					int nPid = android.os.Process.myPid();
					android.os.Process.killProcess(nPid);
			ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		     am.restartPackage(getPackageName());		      
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}).setNegativeButton("否",null).show();
    }
    
}
