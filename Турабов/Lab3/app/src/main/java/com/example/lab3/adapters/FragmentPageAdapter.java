package com.example.lab3.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.lab3.fragments.AddFragment;
import com.example.lab3.fragments.DeleteFragment;
import com.example.lab3.fragments.ShowFragment;
import com.example.lab3.fragments.UpdateFragment;

public class FragmentPageAdapter extends FragmentPagerAdapter {

    public FragmentPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Fragment getItem(int position)
    {
       switch (position)
       {
           case 0:
           {
               ShowFragment sh = new ShowFragment();
               return sh;
           }
           case 1:
           {
               AddFragment add = new AddFragment();
               return add;
           }
           case 2:
           {
               UpdateFragment up = new UpdateFragment();
               return up;
           }
           case 3:
           {
               DeleteFragment del = new DeleteFragment();
               return del;
           }
       }
       return new Fragment();
    }
}