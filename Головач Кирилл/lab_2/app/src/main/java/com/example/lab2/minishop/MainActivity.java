package com.example.lab2.minishop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lab2.R;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView productListView;
    private TextView selectedCount;
    private Button showCartButton;
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Product> selectedProducts = new ArrayList<>();
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productListView = findViewById(R.id.productListView);
        selectedCount = findViewById(R.id.selectedCount);
        showCartButton = findViewById(R.id.showCartButton);

        // Добавляем товары в список
        products.add(new Product("Приложение Головача Кирилла ПО-11 - -", 125874.07, R.drawable.app, "Не все дома"));
        products.add(new Product("Comics Человек-паук. Время славы. Том 4 | Слотт Д. -", 46.92, R.drawable.com_1, "Время славы подходит к концу!\n" +
                "Морбиус создал лекарство для Ящера, но не поздно ли спасать доктора Курта Коннорса?\n"));
        products.add(new Product("Comics Человек-паук Нуар. Сумерки в Вавилоне. Том 1 | Неизвестный. - -", 57.69, R.drawable.com_2, "Убийства и загадки в классическом стиле Marvel!На дворе 1939 год. Нуарный Человек-паук продолжает сражаться с преступностью, но война уже на пороге. "));
        products.add(new Product("Фигурка Nendoroid Spider-Man: Homecoming chibi | art:049504 - -", 68, R.drawable.chibi_1, "Этого товара нет в наличии, но вы можете оформить заказ до 25 апреля, и мы привезём его для вас примерно 01-10 июня!"));
        products.add(new Product("Карнавальная детская маска Человек Паук с управлением движения глаз - -", 69.13, R.drawable.mask_1, "Для самых главных фанатов Марвел - новый карнавальный аксессуар - маска супергероя Человек паук ! Объединяющая в себе сразу все лучшие качества, которые порадуют не только детей, но и взрослых."));
        products.add(new Product("Comics Человек-Паук: Заклятые враги | Кванц Дэниел - -", 39.57, R.drawable.com_3, "Что объединяет Стервятника, Доктора Осьминога, Жестянщика, Песочного Человека, Доктора Дума и Ящера? Правильно! Человек-Паук! Именно он сразится с суперзлодеями, чтобы на улицах Нью-Йорка было тихо и спокойно."));
        products.add(new Product("Костюм карнавальный SMClother Человек-Паук - - -", 115.64, R.drawable.mask_2, "Изготовителя, импортера, гарантийный срок (если применимо) и иную информацию по товару можно запросить у продавца"));
        products.add(new Product("Раскраска для малышей, Марвел Человек паук, Укус паука- - -", 3.26, R.drawable.com_4, "Раскраска для малышей Marvel Человек-паук Укус паука А5 16 страниц - идеальное творческое развлечение для ваших малышей. "));
        products.add(new Product("Игра MARVEL Человек-паук 2 (PlayStation 5, Русская версия) - - -", 207.25, R.drawable.game, "Важные моменты по установке игры с диска.\n" +
                "Полная русская версия (русская озвучка и субтитры) будет только после бесплатного обновления установленной игры через интернет.\n" +
                "Мы проверяли наш товар - всё работает. Игра устанавливается, бесплатно автоматически обновляется после установки, после обновления можно выбрать в опциях русский язык."));
        products.add(new Product("Брелок-игрушка Человек Паук - -", 9.60, R.drawable.chibi_2, "Брелок Человек Паук подойдет в качестве украшения ключей, сумки, рюкзака, школьного портфеля\n" +
                "\n" +
                "Брелок выполнен из качественного силикона приятного на ощупь, яркий и стильный, станет отличным дополнением к любому образу."));
        products.add(new Product("Брелок для ключей Человек паук черный. - - - -", 11.10, R.drawable.chibi_3, "Брелок Человек паук- это стильный и привлекающий внимание окружающих миниатюрный аксессуар, который приносит радость и улыбку своим обладателям. "));

        adapter = new ProductAdapter(this, products, selectedProducts);
        productListView.setAdapter(adapter);

        // Кнопка "Перейти в корзину"
        showCartButton.setOnClickListener(v -> {
            Trash cart = Trash.getInstance();
            cart.clearCart(); // Чтобы корзина не дублировалась при повторном добавлении

            for (Product product : selectedProducts) {
                cart.addProduct(product);
            }

            Intent intent = new Intent(MainActivity.this, TrachActivity.class);
            startActivity(intent);
        });
    }

    // Метод для обновления счетчика выбранных товаров
    public void updateSelectedCount() {
        selectedCount.setText("Выбрано товаров: " + selectedProducts.size());
    }
}
