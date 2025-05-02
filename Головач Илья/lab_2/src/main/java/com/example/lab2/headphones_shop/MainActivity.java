package com.example.lab2.headphones_shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lab2.R;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView headphonesListView;
    private TextView selectedCount;
    private Button showCartButton;
    private ArrayList<Headphones> headphones = new ArrayList<>();
    private ArrayList<Headphones> selectedProducts = new ArrayList<>();
    private HeadphonesAdapter adapter;

    private static final int PRODUCT_DETAIL_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headphonesListView = findViewById(R.id.productListView);
        selectedCount = findViewById(R.id.selectedCount);
        showCartButton = findViewById(R.id.showCartButton);

        // Добавляем товары в список
        headphones.add(new Headphones("Наушники JBL Tune 110 BLK", 39.00, R.drawable.jbl_tune110, "Наушники JBL Tune 110 BLK"));
        headphones.add(new Headphones("Гарнитура XIAOMI Mi In-Ear Headphones", 25.00, R.drawable.xiaomi_mi_basic_black, "Гарнитура XIAOMI Mi In-Ear Headphones Basic Black (ZBW4354TY)"));
        headphones.add(new Headphones("Наушники Philips TAE1105WT/00", 19.90, R.drawable.philips_tae_wt00, "Наушники Philips TAE1105WT/00"));
        headphones.add(new Headphones("Наушники PANASONIC", 35.00, R.drawable.panasonic_rp_125e_w, "Наушники PANASONIC RP-HJE125E-W"));

        headphones.add(new Headphones("Наушники Celebrat E400 (белый)", 15.90, R.drawable.celebrat_e400, "Наушники Celebrat E400 (белый)"));
        headphones.add(new Headphones("Наушники Defender Pulse 427", 11.00, R.drawable.defender_pulse_427, "Наушники Defender Pulse 427"));
        headphones.add(new Headphones("Наушники Celebrat G27 (белый)", 7.90, R.drawable.celebrat_g27, "Наушники Celebrat G27 (белый)"));
        headphones.add(new Headphones("Наушники FiiO FD11", 176.00, R.drawable.fiio_fd11, "Наушники FiiO FD11"));
        headphones.add(new Headphones("Наушники с микрофоном SVEN", 25.00, R.drawable.sven_ap_320m, "Наушники с микрофоном SVEN AP-320M"));

        adapter = new HeadphonesAdapter(this, headphones, selectedProducts);
        headphonesListView.setAdapter(adapter);

        // Кнопка "Перейти в корзину"
        showCartButton.setOnClickListener(v -> {
            Cart cart = Cart.getInstance();
            cart.clearCart(); // Чтобы корзина не дублировалась при повторном добавлении

            for (Headphones product : selectedProducts) {
                cart.addProduct(product);
            }

            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }


    // Метод для обновления счетчика выбранных товаров
    public void updateSelectedCount() {
        selectedCount.setText("Выбрано товаров: " + selectedProducts.size());
    }


}
