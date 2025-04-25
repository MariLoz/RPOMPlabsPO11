package com.example.laba7;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }


        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyPagerAdapter(this));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            View customView = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tabText = customView.findViewById(R.id.tabText);

            switch (position) {
                case 0: tabText.setText("Фото"); break;
                case 1: tabText.setText("Видео"); break;
                case 2: tabText.setText("Аудио"); break;
            }
            tab.setCustomView(customView);
        }).attach();
    }

    private static class MyPagerAdapter extends FragmentStateAdapter {
        public MyPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new FragmentPhoto();
                case 1: return new FragmentVideo();
                case 2: return new FragmentAudio();
                default: return null;
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}