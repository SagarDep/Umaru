//package cc.haoduoyu.umaru.ui.activities;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.SurfaceView;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//
//import com.github.yoojia.zxing.qrcode.QRCodeSupport;
//
//import cc.haoduoyu.umaru.R;
//
///**
// * 二维码扫描
// * Created by XP on 2016/3/11.
// */
//public class QRCodeScanActivity extends Activity {
//
//    private QRCodeSupport mQRCodeScanSupport;
//
//    private final Handler mHandler = new Handler();
//
//    private final Runnable mDelayAutoTask = new Runnable() {
//        @Override
//        public void run() {
//            mQRCodeScanSupport.startAuto(500);
//        }
//    };
//
//    public static void startIt(Context context) {
//        Intent intent = new Intent(context, QRCodeScanActivity.class);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setContentView(R.layout.activity_qrscan);
//
//        ImageView capturePreview = (ImageView) findViewById(R.id.decode_preview);
//        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview_view);
//
//        mQRCodeScanSupport = new QRCodeSupport(surfaceView, new QRCodeSupport.OnResultListener() {
//            @Override
//            public void onScanResult(String notNullResult) {
//                Toast.makeText(QRCodeScanActivity.this, "扫描结果: " + notNullResult, Toast.LENGTH_SHORT).show();
//            }
//        });
//        mQRCodeScanSupport.setCapturePreview(capturePreview);
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mQRCodeScanSupport.onResume();
//        mHandler.postDelayed(mDelayAutoTask, 500);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mQRCodeScanSupport.onPause();
//        mHandler.removeCallbacks(mDelayAutoTask);
//    }
//}
