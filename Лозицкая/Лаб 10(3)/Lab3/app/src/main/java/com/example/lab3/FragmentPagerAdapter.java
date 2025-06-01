package com.example.lab3;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentPagerAdapter extends FragmentStateAdapter {
    static final int PAGE_COUNT = 4;

    public FragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return new FragmentAdd();
            case 2: return new FragmentDelete();
            case 3: return new FragmentUpdate();
            default: return new FragmentShow();
        }
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }
}
