package com.example.workwithbd_lab3;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private int numOfTabs;

    public ViewPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                return new FragmentShow(); // Первый фрагмент - отображение записей
            case 1:
                return new FragmentAdd(); // Второй фрагмент - добавление заметки
            case 2:
                return new FragmentDel(); // Третий фрагмент - удаление заметки
            case 3:
                return new FragmentUpdate(); // Четвертый фрагмент - обновление заметки
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs; // Количество вкладок (фрагментов)
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Заметки"; // Заголовок для FragmentShow
            case 1:
                return "Добавить"; // Заголовок для FragmentAdd
            case 2:
                return "Удалить"; // Заголовок для FragmentDel
            case 3:
                return "Изменить"; // Заголовок для FragmentUpdate
            default:
                return null;
        }
    }
}
