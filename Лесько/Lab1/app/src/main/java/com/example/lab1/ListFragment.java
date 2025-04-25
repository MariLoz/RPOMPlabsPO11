package com.example.lab1;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ListFragment extends Fragment {

    private ListView listView;
    private Button buttonLoadData;
    private Button buttonSettings;
    private Spinner spinnerQueryType;
    private ArrayList<CountryClass> countryClassList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // Связываем с визуал
        listView = view.findViewById(R.id.listView);
        buttonLoadData = view.findViewById(R.id.buttonLoadData);
        buttonSettings = view.findViewById(R.id.buttonSettings);
        spinnerQueryType = view.findViewById(R.id.spinnerQueryType);
        countryClassList = new ArrayList<>();

        // Настройка Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.query_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQueryType.setAdapter(adapter);

        // Обработчик выбора элемента в Spinner
        spinnerQueryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedQuery = parent.getItemAtPosition(position).toString();
                String url = GetUrlForQuery((selectedQuery));
                LoadJSONFromURL(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Обработчик кнопки "Настройки"
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Обработчик кнопки "Загрузить данные"
        buttonLoadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("Settings", MODE_PRIVATE);
                String customUrl = sharedPreferences.getString("serverUrl", "https://restcountries.com/v3.1/lang/russian");
                LoadJSONFromURL(customUrl);
            }
        });

        // Обработчик клика на элемент списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountryClass selectedUniversity = countryClassList.get(position);
                OpenDetailFragment(selectedUniversity);
            }
        });

        return view;
    }

    private String GetUrlForQuery(String query) {
        switch (query) {
            case "Русскоговорящие страны":
                return "https://restcountries.com/v3.1/lang/russian";
            case "Англоговорящие страны":
                return "https://restcountries.com/v3.1/lang/english";
            case "Испанскоговорящие страны":
                return "https://restcountries.com/v3.1/lang/spanish";
            default:
                return "https://restcountries.com/v3.1/lang/russian";
        }
    }

    private void LoadJSONFromURL(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Очистка списка перед загрузкой новых данных
                            countryClassList.clear();

                            // Парсинг JSON-ответа
                            JSONArray jsonArray = new JSONArray(response);

                            // Применение настроек
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("Settings", MODE_PRIVATE);
                            int rowCount = sharedPreferences.getInt("rowCount", 10);

                            for (int i = 0; i < jsonArray.length() && i < rowCount; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                // Извлечение данных о стране
                                String name = jsonObject.getJSONObject("name").getString("common");

                                // Обработка столицы (может отсутствовать)
                                String capital = "N/A";
                                if (jsonObject.has("capital") && !jsonObject.isNull("capital")) {
                                    capital = jsonObject.getJSONArray("capital").getString(0);
                                }

                                // Обработка флага (может отсутствовать)
                                String flagUrl = "";
                                if (jsonObject.has("flags") && !jsonObject.isNull("flags")) {
                                    flagUrl = jsonObject.getJSONObject("flags").getString("png");
                                }

                                // Обработка валюты (может отсутствовать)
                                String currencyInfo = "N/A";
                                if (jsonObject.has("currencies") && !jsonObject.isNull("currencies")) {
                                    JSONObject currencies = jsonObject.getJSONObject("currencies");
                                    Iterator<String> currencyKeys = currencies.keys();
                                    if (currencyKeys.hasNext()) {
                                        String currencyKey = currencyKeys.next();
                                        JSONObject currencyDetails = currencies.getJSONObject(currencyKey);
                                        String currencyName = currencyDetails.getString("name");
                                        String currencySymbol = currencyDetails.optString("symbol", "");
                                        currencyInfo = currencyName + " (" + currencySymbol + ")";
                                    }
                                }

                                // Добавление данных в список
                                countryClassList.add(new CountryClass(name, capital, currencyInfo, flagUrl));
                            }

                            // Обновление адаптера
                            CustomAdapter adapter = new CustomAdapter(getContext(), R.layout.row, countryClassList);
                            listView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Ошибка при обработке данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Ошибка загрузки данных: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Добавление запроса в очередь
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void OpenDetailFragment(CountryClass сountryClass) {
        // Создание фрагмента
        DetailFragment detailFragment = new DetailFragment();

        // Передача данных через Bundle
        Bundle bundle = new Bundle();
        bundle.putString("name", сountryClass.getName());          // Название страны
        bundle.putString("capital", сountryClass.getCapital());    // Столица
        bundle.putString("currency", сountryClass.getCurrency());  // Валюта
        bundle.putString("flagUrl", сountryClass.getFlagUrl());    // URL флага
        detailFragment.setArguments(bundle);

        // Замена фрагмента
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}