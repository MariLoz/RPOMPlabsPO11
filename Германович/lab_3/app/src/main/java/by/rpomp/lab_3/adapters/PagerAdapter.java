package by.rpomp.lab_3.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import by.rpomp.lab_3.fragments.FragmentAdd;
import by.rpomp.lab_3.fragments.FragmentDelete;
import by.rpomp.lab_3.fragments.FragmentShow;
import by.rpomp.lab_3.fragments.FragmentUpdate;

public class PagerAdapter extends FragmentStateAdapter {

    public PagerAdapter(@NonNull FragmentManager manager, @NonNull Lifecycle lifecycle) {
        super(manager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new FragmentAdd();
            case 2:
                return new FragmentUpdate();
            case 3:
                return new FragmentDelete();
        }
        return new FragmentShow();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
