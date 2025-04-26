    package com.example.minishop;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ListView;
    import android.widget.TextView;
    import android.widget.Toast;
    import androidx.appcompat.app.AppCompatActivity;
    import com.example.minishop.adapters.GoodsAdapter;
    import com.example.minishop.interfaces.OnChangeListener;
    import com.example.minishop.models.Good;
    import java.util.ArrayList;
    import java.util.Random;

    public class MainActivity extends AppCompatActivity implements OnChangeListener, View.OnClickListener {
        private ListView listView;
        private ArrayList<Good> goodsList = new ArrayList<>();
        private GoodsAdapter goodsAdapter;
        private LayoutInflater layoutInflater;
        private View headerView, footerView;
        private Button btnShow, taskInfoButton, authorButton;
        private TextView tvCount;
        private Random random = new Random();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            initView();
            createMyListView();
            setupButtons();
        }

        private void initView() {
            listView = findViewById(R.id.listView);
        }

        private void createMyListView() {
            fillData();
            goodsAdapter = new GoodsAdapter(this, goodsList, this);
            layoutInflater = LayoutInflater.from(this);
            headerView = layoutInflater.inflate(R.layout.header_mygoods, listView, false);
            footerView = layoutInflater.inflate(R.layout.footer_mygoods, listView, false);
            btnShow = footerView.findViewById(R.id.btnShow);
            btnShow.setOnClickListener(this);
            tvCount = footerView.findViewById(R.id.tv_count);
            listView.addHeaderView(headerView);
            listView.addFooterView(footerView);
            listView.setAdapter(goodsAdapter);
        }

        private void fillData() {
            for (int i = 1; i <= 25; i++) {
                double cost = 10.0 + random.nextDouble() * 90.0;
                goodsList.add(new Good(i, "My good №" + i, cost, false));
            }
        }

        private void setupButtons() {
            taskInfoButton = findViewById(R.id.taskInfoButton);
            authorButton = findViewById(R.id.authorButton);

            taskInfoButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, TaskInfoActivity.class);
                startActivity(intent);
            });
            authorButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());
        }

        private ArrayList<Good> getCheckedGoods() {
            ArrayList<Good> checkedGoods = new ArrayList<>();
            for (Good good : goodsList) {
                if (good.isCheck()) {
                    checkedGoods.add(good);
                }
            }
            return checkedGoods;
        }

        @Override
        public void onDataChanged() {
            int count = (int) goodsList.stream().filter(Good::isCheck).count();
            tvCount.setText("Count of goods = " + count);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnShow) {
                Intent intent = new Intent(this, SecondActivity.class);
                intent.putParcelableArrayListExtra("MyList", getCheckedGoods());
                startActivity(intent);
            }
        }
    }