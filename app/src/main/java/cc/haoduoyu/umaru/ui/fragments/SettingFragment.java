package cc.haoduoyu.umaru.ui.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import cc.haoduoyu.umaru.BuildConfig;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.ui.activities.AboutActivity;
import cc.haoduoyu.umaru.ui.activities.SettingActivity;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.SettingUtils;
import cc.haoduoyu.umaru.utils.ToastUtils;
import cc.haoduoyu.umaru.utils.Utils;

/**
 * Created by XP on 2016/2/3.
 */
public class SettingFragment extends PreferenceFragment {

    private static final String COLOR_PRIMARY = "color_primary";
    private static final String COLOR_ACCENT = "color_accent";
    private static final String AVATAR = "avatar";
    private static final String ANIMATION = "animation";
    private static final String ENABLE_CACHE = "enable_cache";
    private static final String CACHE = "cache";
    private static final String PIC = "pic";
    private static final String CLEAR = "clear";
    private static final String EQUALIZER = "equalizer";
    private static final String ABOUT = "about";
    private static final String GITHUB = "github";
    private static final String DONATE = "donate";

//    private static final int REQUEST_SELECT_PICTURE = 1;

    Preference colorPrimary, colorAccent;
    Preference avatar;
    SwitchPreference animation, enableCache;
    ListPreference cache, pic;
    Preference clear;
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
        avatar = findPreference(AVATAR);
        animation = (SwitchPreference) findPreference(ANIMATION);
        enableCache = (SwitchPreference) findPreference(ENABLE_CACHE);
        cache = (ListPreference) findPreference(CACHE);
        pic = (ListPreference) findPreference(PIC);
        clear = findPreference(CLEAR);
        equalizer = findPreference(EQUALIZER);
        about = findPreference(ABOUT);
        github = findPreference(GITHUB);
        donate = findPreference(DONATE);

        cache.setSummary(showCache(SettingUtils.getInstance(getActivity()).getCache()));
        pic.setSummary(showPicQuality(SettingUtils.getInstance(getActivity()).getPicQuality()));
        about.setIntent(new Intent(getActivity(), AboutActivity.class));
        about.setSummary("Version " + BuildConfig.VERSION_NAME);
        github.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getActivity().getString(R.string.github))));

        setClickListener();
    }

    String showCache(String value) {
        switch (value) {
            case "0":
                value = getResources().getStringArray(R.array.settings_entries_cache)[0];
                break;
            case "1":
                value = getResources().getStringArray(R.array.settings_entries_cache)[1];
                break;
            default:
                break;
        }
        return value;
    }

    String showPicQuality(String value) {
        switch (value) {
            case "0":
                value = getResources().getStringArray(R.array.settings_entries_pic)[0];
                break;
            case "1":
                value = getResources().getStringArray(R.array.settings_entries_pic)[1];
                break;
            case "2":
                value = getResources().getStringArray(R.array.settings_entries_pic)[2];
                break;
            case "3":
                value = getResources().getStringArray(R.array.settings_entries_pic)[3];
                break;
            default:
                break;
        }
        return value;
    }

    private void setClickListener() {
        colorPrimary.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new ColorChooserDialog.Builder((SettingActivity) getActivity(), R.string.color_primary)
                        .titleSub(R.string.colors)
                        .customButton(R.string.color_custom)
                        .cancelButton(R.string.cancel)
                        .doneButton(R.string.agree)
                        .presetsButton(R.string.presets)
                        .backButton(R.string.back)
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
                        .customButton(R.string.color_custom)
                        .cancelButton(R.string.cancel)
                        .doneButton(R.string.agree)
                        .presetsButton(R.string.presets)
                        .backButton(R.string.back)
                        .accentMode(true)
                        .preselect(PreferencesUtils.getInteger(getActivity(), getString(R.string.color_accent), R.color.colorAccent))
                        .show();
                return true;
            }
        });

        enableCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (SettingUtils.getInstance(getActivity()).isEnableCache()) {
                    cache.setEnabled(true);
                } else {
                    cache.setEnabled(false);
                }
                return true;
            }
        });

        cache.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference.equals(cache)) {
                    String value = newValue.toString();
                    cache.setSummary(showCache(value));
                }
                return true;
            }
        });

        pic.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference.equals(pic)) {
                    String value = newValue.toString();
                    pic.setSummary(showPicQuality(value));
                }
                return true;
            }
        });

        clear.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utils.openAppSettings(getActivity());
                return true;
            }
        });

        avatar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
//                pickFromGallery();
                return true;
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
                return true;
            }
        });
    }
}
