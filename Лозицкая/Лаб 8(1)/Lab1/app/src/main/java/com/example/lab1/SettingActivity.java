package com.example.lab1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    private EditText urlEditText;
    private Spinner itemsPerPageSpinner;

    private String selectedUrl = "";
    private final String urlFormApiWorldOfTanks = "https://api.worldoftanks.eu/wot/encyclopedia/vehicles/?application_id=80f4df6eb27bcc0459b275324f8cdc8e&language=en&fields=name,nation,tier,type,default_profile.armor.hull.front,default_profile.gun.name,default_profile.speed_forward,price_credit";
    private final String urlFromGitHub = "https://raw.githubusercontent.com/Artem646/Temp/refs/heads/main/Json.json";
    private int selectedItemsPerPage = 5;
    private TextWatcher textWatcher = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settingsUrl), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        urlEditText = findViewById(R.id.urlEditText);
        RadioGroup urlRadioGroup = findViewById(R.id.urlRadioGroup);
        itemsPerPageSpinner = findViewById(R.id.itemsPerPageSpinner);

        Intent intent = getIntent();
        selectedUrl = intent.getStringExtra("currentUrl");
        int currentItemsPerPage = intent.getIntExtra("currentItemsPerPage", 5);
        urlEditText.setText(selectedUrl);
        selectRadioButtonBasedOnUrl();

        List<Integer> itemsPerPageOptions = new ArrayList<>();
        itemsPerPageOptions.add(1);
        itemsPerPageOptions.add(2);
        itemsPerPageOptions.add(3);
        itemsPerPageOptions.add(5);
        itemsPerPageOptions.add(7);
        itemsPerPageOptions.add(9);
        itemsPerPageOptions.add(10);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsPerPageOptions);
        itemsPerPageSpinner.setAdapter(adapter);

        setSpinnerSelection(adapter, currentItemsPerPage);
        itemsPerPageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemsPerPage = (int) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                RadioButton customRadioButton = findViewById(R.id.customRadioButton);
                if (customRadioButton != null) {
                    customRadioButton.setChecked(true);
                    selectedUrl = urlEditText.getText().toString();
                }
            }
        };
        urlEditText.addTextChangedListener(textWatcher);

        urlRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(textWatcher != null){
                urlEditText.removeTextChangedListener(textWatcher);
            }

            if (checkedId == R.id.apiWorldOfTanksRadioButton) {
                selectedUrl = urlFormApiWorldOfTanks;
                urlEditText.setText(selectedUrl);
            } else if (checkedId == R.id.fileFromGitHubRadioButton) {
                selectedUrl = urlFromGitHub;
                urlEditText.setText(selectedUrl);
            } else if (checkedId == R.id.customRadioButton) {
                selectedUrl = urlEditText.getText().toString();
            }

            urlEditText.addTextChangedListener(textWatcher);
        });
    }

    public void setSpinnerSelection(ArrayAdapter adapter, int number) {
        int spinnerPosition = adapter.getPosition(number);
        itemsPerPageSpinner.setSelection(spinnerPosition);
    }

    private void selectRadioButtonBasedOnUrl() {
        if (selectedUrl.equals(urlFormApiWorldOfTanks)) {
            RadioButton apiRadioButton = findViewById(R.id.apiWorldOfTanksRadioButton);
            apiRadioButton.setChecked(true);
        } else if (selectedUrl.equals(urlFromGitHub)) {
            RadioButton githubRadioButton = findViewById(R.id.fileFromGitHubRadioButton);
            githubRadioButton.setChecked(true);
        } else {
            RadioButton customRadioButton = findViewById(R.id.customRadioButton);
            customRadioButton.setChecked(true);
        }
    }

    public void saveChanges(View v) {
        selectedUrl = urlEditText.getText().toString();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("url", selectedUrl);
        resultIntent.putExtra("itemsPerPage", selectedItemsPerPage);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}