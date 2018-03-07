package org.toilelibre.libe.athg2sms.androidstuff.materialdesign;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import org.toilelibre.libe.athg2sms.EntryPoint;
import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ConversionFormUI;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ExportFormUI;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ProceedHandler;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ProcessRealTimeFeedback;
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsApplicationToggle;

import java.util.logging.Logger;

public class Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        try {
            toolbar.setNavigationIcon(R.drawable.ic_logo);
        } catch (Resources.NotFoundException vectorDrawableNotSupportedException){
            toolbar.setNavigationIcon(R.drawable.icon);
        }
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(this.getText(R.string.convert)));
        tabLayout.addTab(tabLayout.newTab().setText(this.getText(R.string.export)));
        tabLayout.addTab(tabLayout.newTab().setText(this.getText(R.string.patternmaker)));
        tabLayout.addTab(tabLayout.newTab().setText(this.getText(R.string.convsets)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int screen = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (screen == 0) {
                    FloatingActionButton startButton = (FloatingActionButton) Screen.this.findViewById(R.id.start);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        startButton.setX(viewPager.getScrollX() + 200);
                    }
                }
                if (screen == 1) {
                    FloatingActionButton startButton = (FloatingActionButton) Screen.this.findViewById(R.id.start);
                    FloatingActionButton exportButton = (FloatingActionButton) Screen.this.findViewById(R.id.exportfile);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        exportButton.setX( viewPager.getWidth() / 4 + viewPager.getScrollX() / 3 - 140);
                    }
                    if (startButton != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        startButton.setScaleX(0);
                        startButton.setScaleY(0);
                    }
                }
                if (screen == 3) {
                    FloatingActionButton addOneButton = (FloatingActionButton) Screen.this.findViewById(R.id.addone);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        addOneButton.setY((int)(-3.0 / 8 * viewPager.getScrollX() + 1.74 * viewPager.getHeight()));
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                screen = position;
                FloatingActionButton startButton = (FloatingActionButton) viewPager.findViewById(R.id.start);
                if (screen == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        startButton.setScaleX(0);
                        startButton.setScaleY(0);
                    }
                    ViewCompat.animate(startButton).scaleX(1).scaleY(1).alpha(1.0F)
                            .setInterpolator(new FastOutSlowInInterpolator()).withLayer().setStartDelay(100).start();

                    if (ProcessRealTimeFeedback.getInstance() != null &&
                            ProcessRealTimeFeedback.getInstance().getType() == ProcessRealTimeFeedback.Type.IMPORT) {
                        new ConversionFormUI().becomeStopButton(Screen.this);
                    }
                }else {
                    if (startButton != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        startButton.setScaleX(0);
                        startButton.setScaleY(0);
                    }
                }
                if (screen == 1 && ProcessRealTimeFeedback.getInstance() != null &&
                        ProcessRealTimeFeedback.getInstance().getType() == ProcessRealTimeFeedback.Type.EXPORT) {
                    new ExportFormUI().becomeStopButton(Screen.this);
                }

                if (screen == 0 && ProcessRealTimeFeedback.getInstance() != null && ProcessRealTimeFeedback.getInstance().getType() == ProcessRealTimeFeedback.Type.IMPORT) {
                    viewPager.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                    ProcessRealTimeFeedback.getInstance().updateHandler(new ProceedHandler((ProgressBar) viewPager.findViewById(R.id.progress),
                            (TextView) viewPager.findViewById(R.id.current), (TextView) viewPager.findViewById(R.id.inserted)));
                }
                if (screen == 1 && ProcessRealTimeFeedback.getInstance() != null && ProcessRealTimeFeedback.getInstance().getType() == ProcessRealTimeFeedback.Type.EXPORT) {
                    viewPager.findViewById(R.id.exportprogress).setVisibility(View.VISIBLE);
                    ProcessRealTimeFeedback.getInstance().updateHandler(new ProceedHandler((ProgressBar) viewPager.findViewById(R.id.exportprogress),
                            (TextView) viewPager.findViewById(R.id.exportcurrent), (TextView) viewPager.findViewById(R.id.exportinserted)));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.rate_this_app).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + EntryPoint.class.getPackage().getName()));
                startActivity(intent);
                return true;
            }
        });
        menu.findItem(R.id.go_to_permissions).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Integer[] grantResults1 = new Integer[grantResults.length];
        for (int i = 0 ; i < grantResults1.length ; i++)grantResults1[i] = grantResults [i];
        new ConversionFormUI().onRequestPermissionsResult(this, permissions, grantResults1);
        new ExportFormUI().onRequestPermissionsResult(this, permissions, grantResults1);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onActivityResult (final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if ((requestCode == SmsApplicationToggle.DONT_RETRY_CONVERT ||
                requestCode == SmsApplicationToggle.RETRY_CONVERT) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            boolean checked =
                    EntryPoint.class.getPackage().getName().equals(new SmsApplicationToggle().getDefaultSmsPackage(this));
            ((Switch)this.findViewById(R.id.toggledefaultapp)).setChecked(checked);
            if (requestCode == SmsApplicationToggle.RETRY_CONVERT) {
                new ConversionFormUI().start(this, this.findViewById(R.id.conversionForm));
            }
        }
    }
}