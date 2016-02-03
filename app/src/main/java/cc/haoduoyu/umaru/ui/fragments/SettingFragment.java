package cc.haoduoyu.umaru.ui.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import cc.haoduoyu.umaru.BuildConfig;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.ui.activities.AboutActivity;
import cc.haoduoyu.umaru.ui.activities.SettingActivity;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.ToastUtils;
import cc.haoduoyu.umaru.utils.Utils;

/**
 * Created by XP on 2016/2/3.
 */
public class SettingFragment extends PreferenceFragment {

    private static final String COLOR_PRIMARY = "color_primary";
    private static final String COLOR_ACCENT = "color_accent";
    private static final String ANIMATION = "animation";
    private static final String EQUALIZER = "equalizer";
    private static final String ABOUT = "about";
    private static final String GITHUB = "github";
    private static final String DONATE = "donate";


    Preference colorPrimary;
    Preference colorAccent;
    SwitchPreference animation;
    Preference equalizer;
    Preference about, github, donate;

    ClipboardManager clipboardManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        colorPrimary = findPreference(COLOR_PRIMARY);
        colorAccent = findPreference(COLOR_ACCENT);
        animation = (SwitchPreference) findPreference(ANIMATION);
        equalizer = findPreference(EQUALIZER);
        about = findPreference(ABOUT);
        github = findPreference(GITHUB);
        donate = findPreference(DONATE);

        about.setIntent(new Intent(getActivity(), AboutActivity.class));
        about.setSummary("Version " + BuildConfig.VERSION_NAME);
        github.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getActivity().getString(R.string.github))));

        setClickListener();
    }


    private void setClickListener() {
        colorPrimary.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new ColorChooserDialog.Builder((SettingActivity) getActivity(), R.string.color_primary)
                        .titleSub(R.string.colors)
                        .preselect(PreferencesUtils.getInteger(getActivity(), getString(R.string.color_primary), R.color.colorPrimary))
                        .show();
                return false;
            }
        });

        colorAccent.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new ColorChooserDialog.Builder((SettingActivity) getActivity(), R.string.color_accent)
                        .titleSub(R.string.colors)
                        .accentMode(true)
                        .preselect(PreferencesUtils.getInteger(getActivity(), getString(R.string.color_primary), R.color.colorAccent))
                        .show();
                return false;
            }
        });

        equalizer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utils.startEqualizer(getActivity());
                return true;
            }
        });

        donate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clipboardManager.setPrimaryClip(ClipData.newPlainText(getActivity().getString(R.string.donate),
                        getActivity().getString(R.string.donate_id)));
                ToastUtils.showToast(getActivity().getString(R.string.donate_done));
                return false;
            }
        });


    }
}
