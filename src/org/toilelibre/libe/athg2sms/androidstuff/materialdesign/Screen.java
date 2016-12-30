package org.toilelibre.libe.athg2sms.androidstuff.materialdesign;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ConversionFormUI;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ExportFormUI;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ProceedHandler;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ProcessRealTimeFeedback;

public class Screen  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_logo);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(this.getText(R.string.convert)));
        tabLayout.addTab(tabLayout.newTab().setText(this.getText(R.string.export)));
        tabLayout.addTab(tabLayout.newTab().setText(this.getText(R.string.convsets)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

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
                        startButton.setX(viewPager.getWidth() + viewPager.getScrollX() - 140);
                    }
                }
                if (screen == 1) {
                    FloatingActionButton exportButton = (FloatingActionButton) Screen.this.findViewById(R.id.exportfile);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        exportButton.setX( viewPager.getWidth() / 4 + viewPager.getScrollX() / 3 - 140);
                    }
                }
                if (screen == 2) {
                    FloatingActionButton addOneButton = (FloatingActionButton) Screen.this.findViewById(R.id.addone);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        addOneButton.setY((int)(viewPager.getHeight() - ((viewPager.getScrollX() - viewPager.getWidth()) * 1.0 / viewPager.getWidth() * 400 - 260)));
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.new_one || super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        new ConversionFormUI().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        new ExportFormUI().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}