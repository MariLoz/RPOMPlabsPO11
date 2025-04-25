package com.example.rpomp_l3;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewPager);
        NotesPagerAdapter adapter = new NotesPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // Устанавливаем слушатель для FragmentAdd
        FragmentAdd fragmentAdd = (FragmentAdd) adapter.getItem(1);
        FragmentShow fragmentShow = (FragmentShow) adapter.getItem(0);
        fragmentAdd.setOnDataChangeListener(fragmentShow);
    }

    private class NotesPagerAdapter extends FragmentPagerAdapter {

        public NotesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
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
                    return  new FragmentLabInfo();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Show";
                case 1:
                    return "Add";
                case 2:
                    return "Del";
                case 3:
                    return "Update";
                case 4:
                    return "Lab Info";
                default:
                    return null;
            }
        }
    }
}