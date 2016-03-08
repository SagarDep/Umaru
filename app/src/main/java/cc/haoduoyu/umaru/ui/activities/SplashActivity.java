package cc.haoduoyu.umaru.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.apkfuns.logutils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.base.BaseActivity;
import cc.haoduoyu.umaru.utils.Utils;

/**
 * Created by XP on 2016/1/25.
 */
public class SplashActivity extends BaseActivity {

    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView imageView = (ImageView) findViewById(R.id.splash);

        ScaleAnimation animation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        animation.setDuration(1288);
        animation.setFillAfter(true);//不写动画会恢复原位置
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (Utils.isAndroid6()) {
                    checkPermission();
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(animation);

    }

    /**
     * Android M检查权限
     */
    private void checkPermission() {
        final List<String> permissionsList = new ArrayList<>();
        //需要添加的权限
        addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE);
        addPermission(permissionsList, Manifest.permission.RECORD_AUDIO);
        LogUtils.d(getString(R.string.denied_permission) + permissionsList);
        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE);
        } else {
            MainActivity.startIt(0, SplashActivity.this);
            finish();
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            Map<String, Integer> perms = new HashMap<>();
            //在只有某些权限需要处理时防止NullPointerException，因为这些权限已经被允许不在permissions中
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);

            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);
            LogUtils.d("permissions: " + permissions);
            LogUtils.d("grantResults: " + grantResults);
            LogUtils.d("perms: " + perms);
            // 检查
            if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                //Granted
                MainActivity.startIt(0, SplashActivity.this);
                finish();
            } else {
                //Denied
                new MaterialDialog.Builder(this)
                        .title(R.string.request_permission)
                        .content(R.string.permission_content)
                        .positiveText(R.string.go_setting)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                Utils.openAppSettings(SplashActivity.this);
                                finish();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                finish();
                            }
                        })
                        .show();
            }
        }
    }
}
