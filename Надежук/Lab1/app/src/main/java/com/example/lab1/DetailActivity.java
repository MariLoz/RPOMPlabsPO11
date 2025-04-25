package com.example.lab1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;

public class DetailActivity extends AppCompatActivity {
    public static final String SELECTED_TANK = "SELECTED_TANK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Tank selectedTank = getIntent().getParcelableExtra(SELECTED_TANK);
        if (selectedTank != null) {
            DetailFragment detailFragment = new DetailFragment();
            Bundle bundle = getBundle(selectedTank);
            detailFragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.detailFragment, detailFragment);
            transaction.commit();
        }
    }

    @NonNull
    private Bundle getBundle(Tank selectedTank) {
        Bundle bundle = new Bundle();
        bundle.putString("name", selectedTank.getName());
        bundle.putString("nation", selectedTank.getNation());
        bundle.putInt("level", selectedTank.getLevel());
        bundle.putString("type", selectedTank.getType());
        bundle.putInt("armor", selectedTank.getArmor());
        bundle.putString("gun", selectedTank.getGun());
        bundle.putInt("speed", selectedTank.getSpeed());
        bundle.putInt("cost", selectedTank.getCost());
        bundle.putInt("photoResource", selectedTank.getPhotoResource());
        return bundle;
    }
}