package com.example.listdisplay;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class HeroDetailActivity extends AppCompatActivity {
    private ImageView heroImageView;
    private TextView heroNameTextView, heroAttrTextView, heroAttackTypeTextView;
    private TextView heroRolesTextView, heroHealthTextView, heroManaTextView, heroArmorTextView, heroMoveSpeedTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero_detail);

        // Инициализация всех View
        heroImageView = findViewById(R.id.heroImageView);
        heroNameTextView = findViewById(R.id.heroNameTextView);
        heroAttrTextView = findViewById(R.id.heroAttrTextView);
        heroAttackTypeTextView = findViewById(R.id.heroAttackTypeTextView);
        heroRolesTextView = findViewById(R.id.heroRolesTextView);
        heroHealthTextView = findViewById(R.id.heroHealthTextView);
        heroManaTextView = findViewById(R.id.heroManaTextView);
        heroArmorTextView = findViewById(R.id.heroArmorTextView);
        heroMoveSpeedTextView = findViewById(R.id.heroMoveSpeedTextView);

        // Получение данных из Intent
        String heroName = getIntent().getStringExtra("hero_name");
        String heroAttr = getIntent().getStringExtra("hero_attr");
        String heroAttackType = getIntent().getStringExtra("hero_attack_type");
        String heroRoles = getIntent().getStringExtra("hero_roles");
        String heroImage = getIntent().getStringExtra("hero_image");
        int heroHealth = getIntent().getIntExtra("hero_health", 0);
        int heroMana = getIntent().getIntExtra("hero_mana", 0);
        int heroArmor = getIntent().getIntExtra("hero_armor", 0);
        int heroMoveSpeed = getIntent().getIntExtra("hero_move_speed", 0);

        // Установка данных в View
        heroNameTextView.setText(heroName);
        heroAttrTextView.setText(getString(R.string.attribute_label, heroAttr));
        heroAttackTypeTextView.setText(getString(R.string.attack_type_label, heroAttackType));
        heroRolesTextView.setText(getString(R.string.roles_label, heroRoles));
        heroHealthTextView.setText(getString(R.string.health_label, heroHealth));
        heroManaTextView.setText(getString(R.string.mana_label, heroMana));
        heroArmorTextView.setText(getString(R.string.armor_label, heroArmor));
        heroMoveSpeedTextView.setText(getString(R.string.move_speed_label, heroMoveSpeed));

        Glide.with(this)
                .load(heroImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(heroImageView);
    }
}