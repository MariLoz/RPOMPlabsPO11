package com.example.lab2;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int ITEMS_PER_PAGE = 10;
    private ListView listView;
    private TextView footerTextView;
    private Button prevButton, nextButton;
    private ProductAdapter adapter;
    private List<Product> productList;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        footerTextView = findViewById(R.id.footer_text);
        listView = findViewById(R.id.listView);
        prevButton = findViewById(R.id.button_prev);
        nextButton = findViewById(R.id.button_next);

        productList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            productList.add(new Product(i, "Товар " + i, 50 + i * 10));
        }

        updatePage();

        prevButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                updatePage();
            }
        });

        nextButton.setOnClickListener(v -> {
            if ((currentPage + 1) * ITEMS_PER_PAGE < productList.size()) {
                currentPage++;
                updatePage();
            }
        });

        Button showCartButton = findViewById(R.id.show_checked_button);

        showCartButton.setOnClickListener(v -> {

                Toast.makeText(MainActivity.this, "Нажал Сымоник И.А.", LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, CartActivity.class);

                ArrayList<Product> selectedProducts = new ArrayList<>();
                for (Product product : productList)
                {
                    if (product.getQuantity() > 0)
                    {
                        selectedProducts.add(product);
                    }
                }

                intent.putExtra("selectedProducts", selectedProducts);
                startActivity(intent);

        });

    }
        protected void onResume() {
            super.onResume();
            if(CartActivity.getIsOrdered())
            {
                for (int i = 0; i < productList.size(); i++) {
                    productList.get(i).setQuantity(0);
                }
                adapter.notifyDataSetChanged();
                updateFooterText();
                CartActivity.setIsOrdered(false);
            }

        }

    private void updatePage() {
        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, productList.size());

        List<Product> pageItems = productList.subList(start, end);
        adapter = new ProductAdapter(this, pageItems, footerTextView);
        listView.setAdapter(adapter);

        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(end < productList.size());
    }

    public void updateFooterText() {
        int totalSelectedItems = 0;
        for (Product product : productList) {
            totalSelectedItems += product.getQuantity();
        }
        footerTextView.setText("Количество: " + totalSelectedItems);
    }

}
