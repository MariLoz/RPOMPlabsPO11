package by.rpomp.lab_2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import by.rpomp.lab_2.adapters.ProductListAdapter;
import by.rpomp.lab_2.models.Product;

public class MainActivity extends AppCompatActivity {
    private List<Product> products;
    private ListView productsListView;
    private ProductListAdapter productListAdapter;

    private EditText editTextProductName;
    private EditText editTextProductPrice;
    private Button buttonAddProduct;
    private Button buttonShowCheckedItems;
    private TextView textViewCheckedCount;
    private TextView textViewTotalSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        productsListView = findViewById(R.id.productList);
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);
        buttonShowCheckedItems = findViewById(R.id.buttonShowCheckedItems);
        textViewCheckedCount = findViewById(R.id.textViewCheckedCount);
        textViewTotalSum = findViewById(R.id.textViewTotalSum);

        products = new ArrayList<>();
        productListAdapter = new ProductListAdapter(this, products);
        productsListView.setAdapter(productListAdapter);

        products.add(new Product(1, "Product 1", 10.0));
        products.add(new Product(2, "Product 2", 20.0));
        products.add(new Product(3, "Product 3", 30.0));
        productListAdapter.notifyDataSetChanged();

        productListAdapter.setOnCheckboxClickListener(this::updateCheckedCountAndSum);
        buttonAddProduct.setOnClickListener(view -> onClickButtonAddProduct());
        buttonShowCheckedItems.setOnClickListener(view -> onClickButtonShowCheckedItems());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateCheckedCountAndSum() {
        int count = 0;
        double totalSum = 0;

        for (Product product : products) {
            if (product.isChecked()) {
                count++;
                totalSum += product.getPrice();
            }
        }

        textViewCheckedCount.setText("Checked items: " + count);
        textViewTotalSum.setText("Total sum: $" + totalSum);
    }

    private void onClickButtonShowCheckedItems() {
        List<Product> checkedProducts = new ArrayList<>();
        products.forEach(product -> {
            if (product.isChecked()) {
                checkedProducts.add(product);
            }
        });

        Intent intent = new Intent(MainActivity.this, BasketActivity.class);
        intent.putParcelableArrayListExtra("checkedProducts", new ArrayList<>(checkedProducts));
        startActivity(intent);
    }

    private void onClickButtonAddProduct() {
        int id = products.size() + 1;
        String productName = editTextProductName.getText().toString();
        double productPrice = Double.parseDouble(editTextProductPrice.getText().toString());

        if (productName.isEmpty() || Double.isNaN(productPrice)) {
            Toast.makeText(MainActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        products.add(new Product(id, productName, productPrice));
        productListAdapter.notifyDataSetChanged();

        editTextProductPrice.setText("");
        editTextProductName.setText("");
    }
}