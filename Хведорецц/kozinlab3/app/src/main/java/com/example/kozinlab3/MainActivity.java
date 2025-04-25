package com.example.kozinlab3;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBInit dbHelper = new DBInit(this);
        dbHelper.initializeDatabase();

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Устанавливаем адаптер для ViewPager2
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Привязываем TabLayout к ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Show"); break;
                case 1: tab.setText("Add"); break;
                case 2: tab.setText("Del"); break;
                case 3: tab.setText("Update"); break;
            }
        }).attach();
    }
}
