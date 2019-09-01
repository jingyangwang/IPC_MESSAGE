package com.example.messengclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btn ;

    private  static  final int MSG_FROM_CLIENT=10;
    private  static  final int MSG_FROM_SERVICE=11;
    private  static  final String TAG=MainActivity.class.getSimpleName();


    private  Messenger mGetReplyMsg=new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_FROM_SERVICE:
                    Log.i(TAG,"get msg from service :"+msg.getData().getString("serviceMsg"));
                    break;
            }
            super.handleMessage(msg);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn=findViewById(R.id.start_service);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClassName("com.example.messagerservice","com.example.messagerservice.MessengerService");
                bindService(intent,mConn, Context.BIND_AUTO_CREATE);

            }
        });
    }


    private ServiceConnection mConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Messenger messenger=new Messenger(service);
            Message msg=Message.obtain(null,MSG_FROM_CLIENT);
            Bundle bundle=new Bundle();
            bundle.putString("clientMsg"," this is client send msg");
            msg.setData(bundle);

            //客户端接 服务端msg
            msg.replyTo=mGetReplyMsg;
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MainActivity.super.onDestroy();
            unbindService(mConn);

        }
    };
}
