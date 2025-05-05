package com.example.lab3.mynotes; // Убедись, что этот package совпадает с твоим!

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ObjectShow();
            case 1:
                return new ObjectAdd();
            case 2:
                return new ObjectDelete();
            case 3:
                return new ObjectUpdate();
            default:
                return new ObjectShow();
        }
    }

    @Override
    public int getCount() {
        return 4; // Всего 4 фрагмента
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
            default:
                return null;
        }
    }
}
