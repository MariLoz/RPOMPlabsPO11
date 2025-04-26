package by.rpomp.lab_2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import by.rpomp.lab_2.adapters.BasketListAdapter;
import by.rpomp.lab_2.models.Product;

public class BasketActivity extends AppCompatActivity {
    private List<Product> checkedProducts;
    private TextView textViewCartCount, textViewCartSum;
    private BasketListAdapter basketListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        ListView listViewCart = findViewById(R.id.listViewCart);
        Button buttonBack = findViewById(R.id.buttonBack);
        textViewCartCount = findViewById(R.id.textViewCartCount);
        textViewCartSum = findViewById(R.id.textViewCartSum);

        checkedProducts = getIntent().getParcelableArrayListExtra("checkedProducts");
        if (checkedProducts == null) {
            checkedProducts = new ArrayList<>();
        }

        basketListAdapter = new BasketListAdapter(this, checkedProducts);
        listViewCart.setAdapter(basketListAdapter);

        updateCartCountAndSum();

        buttonBack.setOnClickListener(view -> finish());
    }

    @SuppressLint("SetTextI18n")
    private void updateCartCountAndSum() {
        int count = checkedProducts.size();
        double totalSum = 0;

        for (Product product : checkedProducts) {
            totalSum += product.getPrice();
        }

        textViewCartCount.setText("Checked Items: " + count);
        textViewCartSum.setText("Total Sum: $" + totalSum);
    }
}
