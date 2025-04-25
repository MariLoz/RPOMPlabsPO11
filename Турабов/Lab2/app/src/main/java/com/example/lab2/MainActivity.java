package com.example.lab2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab2.Adapters.GoodsAdapter;
import com.example.lab2.interfaces.OnChangeListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnChangeListener {

    private GoodsAdapter goodsAdapter;
    private ArrayList<Item> goods = new ArrayList<Item>();
    private ListView goodsList;
    //ArrayList<Item> selected;
    private View header, footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        header = getLayoutInflater().inflate(R.layout.header,null);
        footer = getLayoutInflater().inflate(R.layout.footer,null);
        TextView amount = footer.findViewById(R.id.amount);
        amount.setText("0");
        TextView total = footer.findViewById(R.id.total);
        total.setText("0");

//        Button go_to_cart = footer.findViewById(R.id.go_to_cart);
//        go_to_cart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });



        goodsList = findViewById(R.id.goods);
        goodsAdapter = new GoodsAdapter(this, R.layout.goods_item, goods, this);
        goodsList.addHeaderView(header);
        goodsList.addFooterView(footer);
        goodsList.setAdapter(goodsAdapter);
        setInitData();

    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        Log.println(Log.ERROR, "My", ((Integer)goods.get(0).getAmount()).toString());
                        goods = (ArrayList<Item>) intent.getSerializableExtra("goods");
                        Log.println(Log.ERROR, "My", ((Integer)goods.get(0).getAmount()).toString());

                        goodsAdapter.setArr_goods_adapter(goods);

                        goodsAdapter.notifyDataSetChanged();
                        onDataChanged();
                    }
                    else{
                        goodsAdapter.notifyDataSetChanged();
                        onDataChanged();
                    }
                }
            });

    @Override
    public void onDataChanged() {
        List<Item> goods = goodsAdapter.getArr_checked_goods_adapter();
        int size = goods.size();
        TextView amountView = footer.findViewById(R.id.amount);


        int total = 0;
        int amount = 0;
        for(int i = 0; i < size; i++)
        {
            total += goods.get(i).getAmount()*goods.get(i).getPrice();
            amount +=goods.get(i).getAmount();
        }
        TextView totalView = footer.findViewById(R.id.total);
        amountView.setText(((Integer)amount).toString());
        totalView.setText("$"+((Integer)total).toString());
    }

    public void goToCart(View view) {
        Intent intent = new Intent(this, CartActivity.class);

        intent.putExtra("goods", goods);

        mStartForResult.launch(intent);
    }

    private void setInitData()
    {
        goods.add(new Item("SIDLUXE ULTIMATE ISOSTRUT FLIGHT ATTENDANT", 949, 1234567, R.drawable.sidelux_ultimate_isotrust, 0));
        goods.add(new Item("SCOTT SPARK SIDLUXE ULTIMATE FLIGHT ATTENDANT", 949, 1234568, R.drawable.scott_spark_sidelux, 0));
        goods.add(new Item("SIDLUXE ULTIMATE FLIGHT ATTENDANT", 949, 1234569, R.drawable.sideluxe_ultimate_flight_attendant, 0));
        goods.add(new Item("SIDLUXE ISOTRUST", 849, 1234570, R.drawable.sidelux_isotrust, 0));
        goods.add(new Item("SIDLUXE ULTIMATE 3P", 609, 1234571, R.drawable.sideluxe_ultimate_3p, 0));
        goods.add(new Item("SIDLUXE ULTIMATE 2P", 569, 1234572, R.drawable.sideluxe_ultimate_2p, 0));
        goods.add(new Item("SUPER DELUXE COIL ULTIMATE", 1049, 1234573, R.drawable.super_deluxe_coil_ultimate, 0));
        goods.add(new Item("SUPER DELUXE COIL SELECT+", 999, 1234574, R.drawable.super_deluxe_coil_select_plus, 0));
        goodsAdapter.notifyDataSetChanged();
    }
}