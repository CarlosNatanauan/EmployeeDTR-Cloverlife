package com.mobiledev.employeedtr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;




import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

public class Homepage extends AppCompatActivity {
    Fragment selectedFragment = null;
    private int selectedFragmentPosition = 0;  // add this lin
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);




        BubbleNavigationLinearView bubbleNavigation = findViewById(R.id.bubbleNavigation);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new Home())
                .commit();

        bubbleNavigation.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch (position) {
                    case 0:
                        selectedFragment = new Home();
                        break;
                    case 1:
                        selectedFragment = new Records();
                        break;
                    case 2:
                        selectedFragment = new Manage();
                        break;
                    case 3:
                        selectedFragment = new Profile();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
            }
        });


    }


    public void onBackPressed() {
        // Do nothing
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the back button press on the action bar
        return true;
    }
}