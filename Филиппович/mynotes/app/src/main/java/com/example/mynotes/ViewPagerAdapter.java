package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(MainActivity activity) {
        super(activity);
    }

    @NonNull
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
                return new FragmentShowMedia(); // Показываем заметки с медиафайлами
            case 5:
                return new FragmentAddMedia(); // Фрагмент для добавления медиафайлов
            default:
                return new FragmentShow();
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
