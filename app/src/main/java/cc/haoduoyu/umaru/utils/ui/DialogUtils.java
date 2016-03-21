package cc.haoduoyu.umaru.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.player.PlayerController;
import cc.haoduoyu.umaru.ui.activities.ChatActivity;
import cc.haoduoyu.umaru.ui.activities.WebViewActivity;
import cc.haoduoyu.umaru.utils.SettingUtils;
import cc.haoduoyu.umaru.utils.Utils;

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

    public static void show() {

    }
}
