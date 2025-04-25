package com.example.mynotes;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Show");
                    break;
                case 1:
                    tab.setText("Add");
                    break;
                case 2:
                    tab.setText("Del");
                    break;
                case 3:
                    tab.setText("Update");
                    break;
                case 4:
                    tab.setText("Info");
                    break;
                default:
                    throw new IllegalArgumentException("Invalid position: " + position);
            }
        }).attach();
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new FragmentShow();
                case 1:
                    return new FragmentAdd();
                case 2:
                    return new FragmentDel();
                case 3:
                    return new FragmentUpdate();
                case 4:
                    return new FragmentInfo();
                default:
                    throw new IllegalArgumentException("Invalid position: " + position);
            }
        }

        @Override
        public int getItemCount() {
            return 5; // Теперь у нас 5 вкладок
        }
    }
}