package com.example.lab2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab2.Adapters.CartAdapter;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    CartAdapter cartAdapter;
    ArrayList<Item> selectedGoods = new ArrayList<Item>();
    ListView goodsList;
    ArrayList<Item> goods = new ArrayList<Item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();

        if(extras!=null)
        {
            goods = (ArrayList<Item>)extras.getSerializable("goods");

            if(goods == null)
            {
                Toast.makeText(this, "bla", Toast.LENGTH_LONG).show();
            }

            for (int i = 0; i < goods.size(); i++)
            {
                if(goods.get(i).getAmount() != 0)
                {
                    selectedGoods.add(goods.get(i));
                }
            }
        }

        goodsList = findViewById(R.id.selected_goods);
        cartAdapter = new CartAdapter(this, R.layout.cart_item, selectedGoods);
        goodsList.setAdapter(cartAdapter);


    }

    public void pay(View view)
    {
        EditText emailEdit = (EditText) findViewById(R.id.editEmail);
        String email = emailEdit.getText().toString();
        //mail
        sendEmail(email);


        //zero
        for (int i = 0 ; i < goods.size() ; i++ )
        {
            goods.get(i).setAmount(0);
        }

        cartAdapter.notifyDataSetChanged();
        returnToGoods(view);
    }

    public void returnToGoods(View view)
    {
        selectedGoods = (ArrayList<Item>) cartAdapter.getGoods();

        for (int i = 0; i < goods.size(); i++)
        {
            for(int j = 0; j < selectedGoods.size(); j++)
            {
                if(selectedGoods.get(j).getId()== goods.get(i).getId())
                {
                    goods.get(i).setAmount(selectedGoods.get(j).getAmount());
                }
            }
        }

        Intent data = new Intent();
        data.putExtra("goods", goods);
        setResult(RESULT_OK, data);
        finish();
    }

    private void sendEmail(String email)
    {
        String msg = "We received your order and confirmed it\nYour order is\n\n";
        for(int i = 0; i < selectedGoods.size(); i++)
        {
            msg += ((Integer)selectedGoods.get(i).getAmount()).toString()+"x"+selectedGoods.get(i).getProduct_name()+"\n";
        }
        msg += "\nThank you for your choice";
        String subject = "Your purchase is confirmed";

        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email, subject, msg);
        javaMailAPI.execute();
    }
}