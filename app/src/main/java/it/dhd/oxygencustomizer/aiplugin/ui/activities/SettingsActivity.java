package it.dhd.oxygencustomizer.aiplugin.ui.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.color.DynamicColors;

import it.dhd.oxygencustomizer.aiplugin.R;
import it.dhd.oxygencustomizer.aiplugin.databinding.ActivityMainBinding;
import it.dhd.oxygencustomizer.aiplugin.ui.fragments.SettingsFragment;
import it.dhd.oxygencustomizer.aiplugin.utils.AppUtils;

public class SettingsActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private FragmentManager fragmentManager;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDarkTheme();
        DynamicColors.applyToActivityIfAvailable(this);
        fragmentManager = getSupportFragmentManager();
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        replaceFragment(new SettingsFragment(), false);

        if (!AppUtils.hasStoragePermission()) {
            AppUtils.requestStoragePermission(this);
        }

        setSupportActionBar(mBinding.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void setDarkTheme() {
        if (isNightMode()) {
            int darkStyle = Settings.System.getInt(getContentResolver(), "DarkMode_style_key", 0);
            switch (darkStyle) {
                case 0:
                    setTheme(R.style.Theme_OxygenCustomizer_AIPlugin_DarkHard);
                    break;
                case 1:
                    setTheme(R.style.Theme_OxygenCustomizer_AIPlugin_DarkMedium);
                    break;
                case 2:
                    setTheme(R.style.Theme_OxygenCustomizer_AIPlugin_DarkSoft);
                    break;
            }
        }
    }

    private boolean isNightMode() {
        return (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemID = item.getItemId();

        if (itemID == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return false;
    }

    public void replaceFragment(Fragment newFragment) {
        replaceFragment(newFragment, true);
    }

    private void replaceFragment(Fragment newFragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out, R.anim.fragment_fade_in, R.anim.fragment_fade_out);
        fragmentTransaction.replace(R.id.frame_layout, newFragment, newFragment.getTag());
        if (addToBackStack) fragmentTransaction.addToBackStack(newFragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }

}

