package com.example.kozinlab2;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    private TextView totalQuantityTextView;
    private List<Product> selectedItems;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        totalQuantityTextView = findViewById(R.id.textViewTotalQuantity);

        selectedItems = (List<Product>) getIntent().getSerializableExtra("selectedItems");
        productAdapter = new ProductAdapter(this, selectedItems, true);  // передаем текущий контекст
        ListView listViewCart = findViewById(R.id.listViewCart);
        listViewCart.setAdapter(productAdapter);
    }


}

