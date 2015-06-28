package com.melanie.androidactivities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.melanie.androidactivities.adapters.MelanieTabsAdapter;
import com.melanie.androidactivities.support.NavigationHelper;
import com.melanie.androidactivities.support.ReportSession;
import com.melanie.androidactivities.support.SlidingTabLayout;
import com.melanie.androidactivities.support.Utils;
import com.melanie.entities.User;

import java.util.List;

public class MonthlySalesReportActivity extends AppCompatActivity {

    private View actionMenuItemView;
    private ReportSession reportSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);

        ViewPager viewPager = (ViewPager) findViewById(R.id.reportsViewPager);
        viewPager.setAdapter(new MelanieTabsAdapter(
                getSupportFragmentManager(), NavigationHelper
                .getDailySalesReportFragments(), false));

        SlidingTabLayout tabLayout = (SlidingTabLayout) findViewById(R.id.melanieSlidingTabs);
        tabLayout.setSelectedIndicatorColors(Color.rgb(255, 117, 25));
        tabLayout.setDistributeEvenly(true);
        tabLayout.setViewPager(viewPager);

        initializeFields();
    }

    private void initializeFields() {
        reportSession = ReportSession.getInstance(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        boolean returnValue;

        if (menuItem.getItemId() == R.id.action_user) {
            if (actionMenuItemView == null) {
                actionMenuItemView = findViewById(R.id.action_user);
            }
            PopupMenu popupMenu = new PopupMenu(this, actionMenuItemView);
            List<User> users = reportSession.getUsers();
            Menu userMenu = popupMenu.getMenu();

            int position = 0;
            userMenu.add(Menu.NONE, position, Menu.NONE, Utils.Constants.NONE); //the position is passed on as the id as a means to access which user object is selected

            for (User user : users) {
                userMenu.add(Menu.NONE, ++position, Menu.NONE, user.getName());
            }
            popupMenu.setOnMenuItemClickListener(menuItemClickListener);
            popupMenu.show();
            returnValue = true;
        } else {
            returnValue = super.onOptionsItemSelected(menuItem);
        }
        return returnValue;
    }


    private PopupMenu.OnMenuItemClickListener menuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int position = menuItem.getItemId();
            List<User> users = reportSession.getUsers();

            if (position > 0 && !users.isEmpty() && position < users.size()) {
                String ownerId = users.get(position - 1).getObjectId();
                reportSession.filterDisplayItemsByUser(false, ownerId);
            } else {
                reportSession.filterDisplayItemsByUser(false, Utils.Constants.NONE);
            }
            return true;
        }
    };
}
