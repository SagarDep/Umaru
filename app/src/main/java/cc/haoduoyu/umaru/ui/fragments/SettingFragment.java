package cc.haoduoyu.umaru.ui.fragments;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

import cc.haoduoyu.umaru.BuildConfig;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.event.MessageEvent;
import cc.haoduoyu.umaru.ui.activities.AboutActivity;
import cc.haoduoyu.umaru.ui.activities.SettingActivity;
import cc.haoduoyu.umaru.utils.CrashHandler;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.SettingUtils;
import cc.haoduoyu.umaru.utils.ShareUtils;
import cc.haoduoyu.umaru.utils.ToastUtils;
import cc.haoduoyu.umaru.utils.Utils;
import de.greenrobot.event.EventBus;

/**
 * Created by XP on 2016/2/3.
 */
public class SettingFragment extends PreferenceFragment {

    private static final String COLOR_PRIMARY = "color_primary";
    private static final String COLOR_ACCENT = "color_accent";
    private static final String AVATAR = "avatar";
    private static final String SHAKE = "shake";
    private static final String FLOAT_VIEW = "floatview";
    private static final String ANIMATION = "animation";
    private static final String ENABLE_CACHE = "enable_cache";
    private static final String ENABLE_GUIDE = "enable_guide";
    private static final String CACHE = "cache";
    private static final String PIC = "pic";
    private static final String CLEAR = "clear";
    private static final String ACCOUNT = "account";
    private static final String CRASH = "crash";
    private static final String EQUALIZER = "equalizer";
    private static final String ABOUT = "about";
    private static final String GITHUB = "github";
    private static final String DONATE = "donate";
    private static final String UPDATE = "update";

//    private static final int REQUEST_SELECT_PICTURE = 1;

    Preference colorPrimary, colorAccent;
    Preference avatar;
    SwitchPreference shake, floatView, animation, enableCache, enableGuide;
    ListPreference cache, pic;
    Preference clear;
    Preference account;
    Preference crash;
    Preference equalizer;
    Preference about, github, donate;
    Preference update;

    ClipboardManager clipboardManager;
    EditText accountEt, passwordEt;
    TextInputLayout accountLayout, passwordLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        colorPrimary = findPreference(COLOR_PRIMARY);
        colorAccent = findPreference(COLOR_ACCENT);
        avatar = findPreference(AVATAR);
        shake = (SwitchPreference) findPreference(SHAKE);
        floatView = (SwitchPreference) findPreference(FLOAT_VIEW);
        animation = (SwitchPreference) findPreference(ANIMATION);
        enableCache = (SwitchPreference) findPreference(ENABLE_CACHE);
        cache = (ListPreference) findPreference(CACHE);
        pic = (ListPreference) findPreference(PIC);
        clear = findPreference(CLEAR);
        account = findPreference(ACCOUNT);
        crash = findPreference(CRASH);
        enableGuide = (SwitchPreference) findPreference(ENABLE_GUIDE);
        equalizer = findPreference(EQUALIZER);
        about = findPreference(ABOUT);
        github = findPreference(GITHUB);
        donate = findPreference(DONATE);
        update = findPreference(UPDATE);

        if (!TextUtils.isEmpty(PreferencesUtils.getString(getActivity(), getString(R.string.account)))) {
            account.setSummary(getString(R.string.now_bind_account) + PreferencesUtils.getString(getActivity(), getString(R.string.account)));
        } else {
            account.setSummary(R.string.account_not_bind);
        }
        cache.setSummary(showCache(SettingUtils.getInstance(getActivity()).getCache()));
        pic.setSummary(showPicQuality(SettingUtils.getInstance(getActivity()).getPicQuality()));
        about.setIntent(new Intent(getActivity(), AboutActivity.class));
        about.setSummary("Version " + BuildConfig.VERSION_NAME);
        github.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(getActivity().getString(R.string.github))));
        crash.setSummary(getString(R.string.path) + CrashHandler.PATH);

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

        if (colorAccent != null)
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

        floatView.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.SHOW_OR_HIDE_FLOATVIEW));
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

        if (clear != null)
            clear.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Utils.openAppSettings(getActivity());
                    return true;
                }
            });

        account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title(R.string.account_bind)
                        .customView(R.layout.dialog_account_bind, true)
                        .positiveText(R.string.agree)
                        .negativeText(R.string.cancel)
                        .autoDismiss(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                saveAccount(dialog);
                            }
                        }).onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        }).build();

                accountLayout = (TextInputLayout) dialog.getCustomView().findViewById(R.id.til_account);
                accountLayout.setHint(getString(R.string.account_hint));
                passwordLayout = (TextInputLayout) dialog.getCustomView().findViewById(R.id.til_password);
                passwordLayout.setHint(getString(R.string.password_hint));
                accountEt = accountLayout.getEditText();
                accountEt.setText(PreferencesUtils.getString(getActivity(), getString(R.string.account)));
                passwordEt = passwordLayout.getEditText();
                passwordEt.setText(PreferencesUtils.getString(getActivity(), getString(R.string.password)));

                dialog.show();
                return true;
            }
        });

        crash.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ShareUtils.sendCrash(getActivity());
                return true;
            }
        });

        if (avatar != null)
            avatar.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
//                pickFromGallery();
                    return true;
                }
            });

        if (equalizer != null)
            equalizer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Utils.startEqualizer(getActivity());
                    return true;
                }
            });

        if (donate != null) {
            donate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Utils.copyToClipBoard(getActivity(), getString(R.string.umaru), getString(R.string.donate_done));
                    return true;
                }
            });
        }

        if (update != null)
            update.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AppUpdater(getActivity())
//                            .setUpdateFrom(UpdateFrom.GITHUB)
//                            .setGitHubUserAndRepo(getString(R.string.bigggge), getString(R.string.umaru))
//                            .setGitHubUserAndRepo("javiersantos", "AppUpdater")
                            .setUpdateFrom(UpdateFrom.XML)
                            .setUpdateXML("https://raw.githubusercontent.com/javiersantos/AppUpdater/master/app/update.xml")
                            .setDisplay(Display.DIALOG)
                            .showAppUpdated(true)
                            .start();
                    return true;
                }
            });
    }


    private void saveAccount(MaterialDialog dialog) {
        if (accountEt.getText().toString().length() < 10) {
            accountLayout.setErrorEnabled(true);
            passwordLayout.setErrorEnabled(false);
            accountLayout.setError(getString(R.string.account_error));
        } else if (TextUtils.isEmpty(passwordEt.getText().toString())) {
            passwordLayout.setErrorEnabled(true);
            accountLayout.setErrorEnabled(false);
            passwordLayout.setError(getString(R.string.password_error));
        } else {
            accountLayout.setErrorEnabled(false);
            passwordLayout.setErrorEnabled(false);
            PreferencesUtils.setString(getActivity(), getString(R.string.account), accountEt.getText().toString());
            PreferencesUtils.setString(getActivity(), getString(R.string.password), passwordEt.getText().toString());
            dialog.dismiss();
            account.setSummary(getString(R.string.now_bind_account) + PreferencesUtils.getString(getActivity(), getString(R.string.account)));
            ToastUtils.showToast(getString(R.string.bind_success));
        }
    }
}
