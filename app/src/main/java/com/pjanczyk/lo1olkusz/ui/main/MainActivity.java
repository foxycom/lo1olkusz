/*
 * Copyright 2016 Piotr Janczyk
 *
 * This file is part of lo1olkusz unofficial app.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pjanczyk.lo1olkusz.ui.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.pjanczyk.lo1olkusz.R;
import com.pjanczyk.lo1olkusz.model.News;
import com.pjanczyk.lo1olkusz.storage.SavedTimetables;
import com.pjanczyk.lo1olkusz.synchronization.SyncService;
import com.pjanczyk.lo1olkusz.synchronization.SyncTimingHelper;
import com.pjanczyk.lo1olkusz.ui.firstrun.FirstRunActivity;
import com.pjanczyk.lo1olkusz.ui.settings.SettingsActivity;
import com.pjanczyk.lo1olkusz.utils.AppVersion;
import com.pjanczyk.lo1olkusz.utils.Settings;
import com.pjanczyk.lo1olkusz.utils.Urls;
import com.pjanczyk.lo1olkusz.utils.network.NetworkAvailability;

import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SyncService.SyncListener {

    public static final String EXTRA_CLASS = "class";
    public static final String EXTRA_DATE = "date";

    private static final String TAG = "MainActivity";
    private static final String STATE_FRAGMENT_ID = "fragmentId";
    private static final String STATE_CLASS_MODE = "classMode";

    private TabLayout tabs;
    private View mainLayout;
    private MenuItem menuItemRefresh;
    private MenuItem menuItemClassMode;
    private HashMap<String, MenuItem> menuItemsClassModes;
    private SwipeRefreshLayout swipeRefreshLayout;

    private SyncService boundService;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            SyncService.Binder binder = (SyncService.Binder) service;
            boundService = binder.getService();
            boundService.setStatusListener(MainActivity.this);
            if (boundService.isWorking()) {
                MainActivity.this.onSyncBegin();
            }
            Log.d(TAG, "Connected to service");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundService.setStatusListener(null);
            boundService = null;
            Log.d(TAG, "Disconnected from service");
        }
    };

    private Settings settings;

    private int currentFragmentId = -1;
    private FragmentBase currentFragment;

    private String classMode;

    private String userClass;
    private Set<String> userGroups;

    @Nullable
    public String getCurrentClass() {
        return classMode != null ? classMode : userClass;
    }

    @Nullable
    public Set<String> getCurrentGroups() {
        return classMode != null ? null : userGroups;
    }

    public void showDaily(LocalDate date, String className) {
        if (className != null && className.equals(userClass)) {
            className = null;
        }

        switchClassMode(className);
        switchFragment(0, date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = new Settings(this);

        if (settings.getFirstRun()) {
            Intent intent = new Intent(this, FirstRunActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }

        if (settings.getSettingsChanged()) {
            settings.edit()
                    .setSettingsChanged(false)
                    .apply();
        }

        setContentView(R.layout.activity_main);

        mainLayout = findViewById(android.R.id.content);
        tabs = (TabLayout) findViewById(R.id.tabs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        tabs.addTab(tabs.newTab().setIcon(R.drawable.ic_daily));
        tabs.addTab(tabs.newTab().setIcon(R.drawable.ic_notifications));
        tabs.addTab(tabs.newTab().setIcon(R.drawable.ic_timetable));

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int id = tab.getPosition();
                if (id != currentFragmentId) {
                    switchFragment(id, null);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        toolbar.inflateMenu(R.menu.activity_main);
        menuItemClassMode = toolbar.getMenu().findItem(R.id.action_select_class);
        menuItemRefresh = toolbar.getMenu().findItem(R.id.action_refresh);
        menuItemRefresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sync();
                return true;
            }
        });
        MenuItem menuItemSettings = toolbar.getMenu().findItem(R.id.action_settings);
        menuItemSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false); //disable swipe gesture

        userClass = settings.getUserClass();
        userGroups = settings.getUserGroups();

        if (savedInstanceState != null) {
            currentFragmentId = savedInstanceState.getInt(STATE_FRAGMENT_ID);
            tabs.getTabAt(currentFragmentId).select();
            classMode = savedInstanceState.getString(STATE_CLASS_MODE);
        }

        menuItemsClassModes = new HashMap<>();
        reloadClassList();

        if (savedInstanceState == null) {
            Intent intent = getIntent();

            if (intent.hasExtra(EXTRA_DATE)) {
                String extraClass = intent.getStringExtra(EXTRA_CLASS);
                LocalDate extraDate = LocalDate.parse(intent.getStringExtra(EXTRA_DATE));
                showDaily(extraDate, extraClass);
            } else {
                switchFragment(0, null);
            }
            showDialogIfNewApkAvailable();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        currentFragment = (FragmentBase) fragment;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //bind service
        Intent intent = new Intent(this, SyncService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        SyncTimingHelper.handleAction(getApplicationContext(), SyncTimingHelper.ACTIVITY_START);

        //if settings had been changed update drawer header and notify fragment
        if (settings.getSettingsChanged()) {
            userClass = settings.getUserClass();
            userGroups = settings.getUserGroups();

            reloadClassList();
            if (currentFragment != null) {
                currentFragment.onClassOrGroupsChanged();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //unbind service
        if (boundService != null) {
            unbindService(serviceConnection);
            boundService = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_FRAGMENT_ID, currentFragmentId);
        outState.putString(STATE_CLASS_MODE, classMode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSyncBegin() {
        showRefreshing(true);
    }

    @Override
    public void onSyncSuccess(News news) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showRefreshing(false);
            }
        }, 400);

        if (!news.timetables.isEmpty()) {
            reloadClassList();
        }

        if (currentFragment != null) {

            if (news.bells != null || !news.timetables.isEmpty() ||
                    !news.luckyNumbers.isEmpty() || !news.replacements.isEmpty()) {
                currentFragment.onSyncCompleted();
            }
        }
    }

    @Override
    public void onSyncError(String errorMsg) {
        showRefreshing(false);
        Snackbar.make(mainLayout, errorMsg, Snackbar.LENGTH_LONG).show();
    }

    private void sync() {
        if (NetworkAvailability.isConnected(this)) {
            SyncTimingHelper.handleAction(getApplicationContext(), SyncTimingHelper.USER_REQUEST);
        } else {
            Snackbar.make(mainLayout, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show();
        }
    }

    private void showRefreshing(boolean refreshing) {
        menuItemRefresh.setEnabled(!refreshing);
        swipeRefreshLayout.setRefreshing(refreshing);
    }

    private void reloadClassList() {
        List<String> availableClasses = new SavedTimetables(this).getAvailableTimetables();

        SubMenu menu = menuItemClassMode.getSubMenu();
        //recreate all menu items
        menu.clear();
        menuItemsClassModes.clear();

        MenuItem item0 = menu.add(userClass);
        item0.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switchClassMode(null);
                return true;
            }
        });
        menuItemsClassModes.put(null, item0);

        MenuItem.OnMenuItemClickListener clickListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switchClassMode(item.getTitle().toString());
                return true;
            }
        };

        for (String className : availableClasses) {
            if (!className.equals(userClass)) {
                MenuItem item = menu.add(className);
                item.setOnMenuItemClickListener(clickListener);
                menuItemsClassModes.put(className, item);
            }
        }

        //radio checking mode
        menu.setGroupCheckable(0, true, true);

        //if current class has been deleted
        if (classMode != null && !availableClasses.contains(classMode)) {
            switchClassMode(null);
        } else {
            updateClassIndicators();
        }
    }

    public void updateClassIndicators() {
        String currentClass = getCurrentClass();
        if (currentClass == null) {
            menuItemClassMode.setTitle("KLASA");
        } else {
            menuItemClassMode.setTitle(getCurrentClass());
        }

        MenuItem item = menuItemsClassModes.get(classMode);
        item.setChecked(true);
    }

    private void switchClassMode(String classMode) {
        this.classMode = classMode;

        updateClassIndicators();

        if (currentFragment != null) {
            currentFragment.onClassOrGroupsChanged();
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void switchFragment(int id, LocalDate argDate) {
        FragmentBase fragment = null;

        switch (id) {
            case 0:
                menuItemClassMode.setVisible(true);
                fragment = DailyScheduleFragment.newInstance(argDate);
                break;
            case 1:
                menuItemClassMode.setVisible(false);
                fragment = new NotificationsFragment();
                break;
            case 2:
                menuItemClassMode.setVisible(true);
                fragment = new TimetableFragment();
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_content, fragment)
                .commit();

        currentFragmentId = id;
        currentFragment = fragment;

        tabs.getTabAt(id).select();
    }

    private void showDialogIfNewApkAvailable() {
        boolean upToDate = AppVersion.isUpToDate(this);

        if (!upToDate && NetworkAvailability.isWifiAvailable(this)) {
            Snackbar.make(findViewById(android.R.id.content),
                    R.string.new_app_version_available,
                    Snackbar.LENGTH_LONG)
                    .setAction("Pobierz", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Urls.DOWNLOAD));
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }
}
