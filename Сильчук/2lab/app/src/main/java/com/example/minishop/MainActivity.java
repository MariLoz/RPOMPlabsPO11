package com.example.minishop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private TextView tvSelectedCount;
    private TextView tvTotalPrice;
    private Button btnShowChecked;
    private ImageButton btnInfo;
    private List<Product> productList;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnShowChecked = findViewById(R.id.btnShowChecked);
        btnInfo = findViewById(R.id.btnInfo);

        productList = new ArrayList<>();
        productList.add(new Product(1, "Product 1", 10.0));
        productList.add(new Product(2, "Product 2", 20.0));
        productList.add(new Product(3, "Product 3", 30.0));
        productList.add(new Product(4, "Product 4", 40.0));
        productList.add(new Product(5, "Product 5", 50.0));
        productList.add(new Product(6, "Product 6", 60.0));
        productList.add(new Product(7, "Product 7", 70.0));
        productList.add(new Product(8, "Product 8", 80.0));
        productList.add(new Product(9, "Product 9", 90.0));
        productList.add(new Product(10, "Product 10", 100.0));
        productList.add(new Product(11, "Product 11", 110.0));
        productList.add(new Product(12, "Product 12", 120.0));

        // Создание адаптера
        adapter = new ProductAdapter(productList);
        listView.setAdapter(adapter);

        // Обновление количества выбранных товаров и общей суммы
        updateSelectedCount();
        updateTotalPrice();

        // Обработчик кнопки перехода к корзине
        btnShowChecked.setOnClickListener(v -> {
            List<MainActivity.Product> selectedProducts = adapter.getSelectedProducts();
            if (selectedProducts.isEmpty()) {
                Toast.makeText(MainActivity.this, "No items selected", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                intent.putParcelableArrayListExtra("selectedProducts", new ArrayList<>(selectedProducts));
                startActivity(intent);
            }
        });

        // Обработчик кнопки информации
        btnInfo.setOnClickListener(v -> showInfoDialog());
    }

    private void updateSelectedCount() {
        int count = adapter.getSelectedProducts().size();
        tvSelectedCount.setText("Selected: " + count);
    }

    private void updateTotalPrice() {
        double total = 0;
        for (Product product : adapter.getSelectedProducts()) {
            total += product.getPrice();
        }
        tvTotalPrice.setText("Total: $" + total);
    }

    private void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Информация о лабораторной работе №9");
        builder.setMessage("Выполнил студент: Сильчук Д.А.\nГруппа: ПО-11\nТема: Списки. Создание собственного адаптера. Механизмы обратного вызова.\nЗадание:\n"
                + "1. Разработать приложение MiniShop, состоящее из двух Activity (см. рисунки 3.3, 3.4 в источнике).\n" +
                "2. В первом Activity создать список ListView с Header и Footer.\n" +
                "3. В Footer разместить текстовое поле (TextView) для ввода количества активированных пользователем товаров, кнопку Show Checked Items для перехода в корзину товаров.\n" +
                "4. Реализовать кастомизированный список ListView с помощью собственного адаптера, наследующего класс BaseAdapter.\n" +
                "5. В каждом пункте списка отобразить следующую информацию о товаре:\n" +
                "идентификационный номер, название, стоимость, чекбокс для возможности выбора товара пользователем.\n" +
                "6. В текстовом поле (TextView) Footer списка динамически отображать общее текущее количество активированных товаров.\n" +
                "7. При нажатии кнопки Show Checked Items реализовать переход во второе Activity с корзиной товаров.\n" +
                "8. Корзину товаров реализовать в виде нового кастомизированного списка с выбранными товарами.\n" +
                "9. Продемонстрировать работу приложения MiniShop на эмуляторе или реальном устройстве.\n");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // Модель данных для товара
    public static class Product implements Parcelable {
        private int id;
        private String name;
        private double price;
        private boolean isChecked;

        public Product(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.isChecked = false;
        }

        protected Product(Parcel in) {
            id = in.readInt();
            name = in.readString();
            price = in.readDouble();
            isChecked = in.readByte() != 0;
        }

        public static final Creator<Product> CREATOR = new Creator<Product>() {
            @Override
            public Product createFromParcel(Parcel in) {
                return new Product(in);
            }

            @Override
            public Product[] newArray(int size) {
                return new Product[size];
            }
        };

        public int getId() { return id; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public boolean isChecked() { return isChecked; }
        public void setChecked(boolean checked) { isChecked = checked; }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(name);
            dest.writeDouble(price);
            dest.writeByte((byte) (isChecked ? 1 : 0));
        }
    }

    // Кастомизированный адаптер
    private class ProductAdapter extends BaseAdapter {
        private List<Product> products;
        private List<Product> selectedProducts;

        public ProductAdapter(List<Product> products) {
            this.products = products;
            this.selectedProducts = new ArrayList<>();
        }

        @Override
        public int getCount() { return products.size(); }

        @Override
        public Object getItem(int position) { return products.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_product, parent, false);
            }

            Product product = products.get(position);
            TextView tvName = convertView.findViewById(R.id.tvName);
            TextView tvPrice = convertView.findViewById(R.id.tvPrice);
            CheckBox cbSelect = convertView.findViewById(R.id.cbSelect);

            tvName.setText(product.getName());
            tvPrice.setText("$" + product.getPrice());
            cbSelect.setChecked(product.isChecked());

            cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                product.setChecked(isChecked);
                if (isChecked) {
                    selectedProducts.add(product);
                } else {
                    selectedProducts.remove(product);
                }
                updateSelectedCount();
                updateTotalPrice();
            });

            return convertView;
        }

        public List<Product> getSelectedProducts() { return selectedProducts; }
    }
}