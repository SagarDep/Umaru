package cc.haoduoyu.umaru.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import cc.haoduoyu.umaru.BuildConfig;
import cc.haoduoyu.umaru.Constants;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.player.PlayerController;
import cc.haoduoyu.umaru.player.PlayerLib;
import cc.haoduoyu.umaru.ui.activities.AboutActivity;
import cc.haoduoyu.umaru.ui.activities.ChatActivity;
import cc.haoduoyu.umaru.ui.activities.CityPickerActivity;
import cc.haoduoyu.umaru.ui.activities.MainActivity;
import cc.haoduoyu.umaru.ui.activities.NowPlayingActivity;
import cc.haoduoyu.umaru.ui.activities.SettingActivity;
import cc.haoduoyu.umaru.ui.activities.SplashActivity;
import cc.haoduoyu.umaru.ui.activities.WebViewActivity;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.SettingUtils;
import cc.haoduoyu.umaru.utils.Utils;
import cc.haoduoyu.umaru.widgets.zbar.CaptureActivity;

/**
 * Created by XP on 2016/3/21.
 */
public class DialogUtils {


    public static void showChatGuide(final Context context) {
        if (SettingUtils.getInstance(context).isEnableChatGuide()) {
            MaterialDialog dialog = new MaterialDialog.Builder(context)
                    .iconRes(R.mipmap.dialog_help)
                    .limitIconToDefaultSize() //48dp
                    .title(R.string.ask_me)
                    .customView(R.layout.dialog_chat_guide, true)
                    .positiveText(R.string.go_chat)
                    .negativeText(R.string.close)
                    .autoDismiss(false)
                    .cancelable(false)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            ChatActivity.startIt(context);
                        }
                    }).onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.dismiss();
                        }
                    }).build();
            CheckBox checkbox = (CheckBox) dialog.getCustomView().findViewById(R.id.showGuide);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SettingUtils.getInstance(context).setEnableChatGuide(!isChecked);
                }
            });
            dialog.show();
        }
    }

    public static void showSimilarSingers(final Context context, CharSequence showContent) {
        new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.artist))
                .content(showContent)
                .positiveText(R.string.agree)
                .negativeText(R.string.similar_singers)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        WebViewActivity.startIt(context, context.getString(R.string.singer_similar_url)
                                + PlayerController.getNowPlaying().getArtistName() + "/+similar", null);
                    }
                })
                .show();
    }

    public static void showPanelSong(Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.panel_song)
                .content(Utils.getSongContent(PlayerController.getNowPlaying()))
                .positiveText(R.string.agree)
                .show();

    }

    public static void showRequestPermission(final Activity context) {
        new MaterialDialog.Builder(context)
                .title(R.string.request_permission)
                .content(R.string.permission_content)
                .positiveText(R.string.go_setting)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        Utils.openAppSettings(context);
                        context.finish();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        context.finish();
                    }
                })
                .show();

    }

    public static void showSP(final Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.develop_by)
                .content(context.getString(R.string.version)
                        + BuildConfig.VERSION_NAME
                        + "\n\n" + PreferencesUtils.getAll(context)
                        + "\n\n" + SettingUtils.getAll())
                .positiveText(R.string.close)
                .negativeText(R.string.blog)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        WebViewActivity.startIt(context, Constants.HAO_DUO_YU, null);
                    }
                })
                .show();
    }

    /**
     * <item>聊天测试</item>
     * <item>选择城市测试</item>
     * <item>音乐测试</item>
     * <p/>
     * <item>正在播放音乐测试</item>
     * <item>WebView测试</item>
     * <item>欢迎页测试</item>
     * <p/>
     * <item>意外终止测试</item>
     * <item>二维码测试</item>
     * <item>电话测试</item>
     * <p/>
     * <item>短信测试</item>
     * <item>设置测试</item>
     * <item>关于测试</item>
     *
     * @param context
     */
    public static void showTestList(final Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.test)
                .items(R.array.testItems)
                .autoDismiss(false)
                .itemsColorRes(R.color.white)
                .backgroundColorRes(R.color.md_grey_800)
                .titleColorRes(R.color.umaru)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                ChatActivity.startIt(context);
                                break;
                            case 1:
                                CityPickerActivity.startIt(context);
                                break;
                            case 2:
                                MainActivity.startIt(1, (Activity) context);
                                break;
                            case 3:
                                if (PlayerLib.getSongs().size() != 0) {
                                    PlayerController.setQueueAndPosition(PlayerLib.getSongs(), 0);
                                    PlayerController.begin();
                                    NowPlayingActivity.startIt(PlayerLib.getSongs().get(0), (Activity) context);
                                } else {
                                    ToastUtils.showToast(context.getString(R.string.fab_no_music));
                                }
                                break;
                            case 4:
                                WebViewActivity.startIt(context, Constants.HAO_DUO_YU, null);
                                break;
                            case 5:
                                SplashActivity.startIt(context);
                                break;
                            case 6:
                                throw new RuntimeException(context.getString(R.string.crash_test));
                            case 7:
                                CaptureActivity.startIt(context);
                                break;
                            case 8:
                                Utils.dial(context, null);
                                break;
                            case 9:
                                Utils.sms(context, null);
                                break;
                            case 10:
                                SettingActivity.startIt(context);
                                break;
                            case 11:
                                AboutActivity.startIt(context);
                                break;

                        }
                    }
                })
                .show();
    }
}
