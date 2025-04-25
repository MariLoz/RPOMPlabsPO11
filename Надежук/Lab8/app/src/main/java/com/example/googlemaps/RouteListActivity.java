package com.example.googlemaps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RouteListActivity extends AppCompatActivity {
    private ListView routeListView;
    private static final String ROUTE_LIST_KEY = "routeList";
    private ArrayAdapter<String> routeListAdapter;
    private List<Route> routeList = new ArrayList<>();
    private List<String> routeNames = new ArrayList<>();
    private Route selectedRoute = null;
    private Boolean isDeleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);

        routeListView = findViewById(R.id.fileListView);
        loadRouteListFromMainActivity();

        routeListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, routeNames);
        routeListView.setAdapter(routeListAdapter);

        routeListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedRoute = routeList.get(position);
            updateResultIntent();
            finish();
        });

        routeListView.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(RouteListActivity.this)
                    .setTitle("Удалить маршрут")
                    .setMessage("Вы уверены, что хотите удалить этот маршрут?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        routeNames.remove(routeList.remove(position).getName());
                        routeListAdapter.notifyDataSetChanged();
                        isDeleted = true;
                        Toast.makeText(RouteListActivity.this, "Маршрут удален", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        });

    }

    private void loadRouteListFromMainActivity() {
        String routeListJsonString = getIntent().getStringExtra(ROUTE_LIST_KEY);
        if (routeListJsonString != null) {
            try {
                routeList.clear();
                JSONArray jsonArray = new JSONArray(routeListJsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonRoute = jsonArray.getJSONObject(i);
                    Route route = Route.fromJson(jsonRoute);
                    routeList.add(route);
                }
            } catch (JSONException e) {
                Log.e("RouteListActivity", "Ошибка преобразования JSON в List<Route>", e);
            }
        } else {
            Log.e("RouteListActivity", "Список маршрутов в Intent не найден!");
        }

        for (Route route : routeList) {
            routeNames.add(route.getName());
        }
    }

    private void updateResultIntent() {
        Intent resultIntent = new Intent();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonRoute;
        if (isDeleted)
        {
            for (Route route : routeList) {
                try {
                    jsonArray.put(route.toJson());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            resultIntent.putExtra("updatedRouteList", jsonArray.toString());
        }

        if (selectedRoute != null)
        {
            try {
                jsonRoute = selectedRoute.toJson();
                resultIntent.putExtra("selectedRoute", jsonRoute.toString());
            } catch (JSONException e) {
                Log.e("FileListActivity", "Ошибка преобразования Route в JSON", e);
                return;
            }
        }

        setResult(Activity.RESULT_OK, resultIntent);
        getIntent().putExtras(resultIntent);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        updateResultIntent();
        setResult(Activity.RESULT_OK, getIntent());
        super.onBackPressed();
        finish();
    }
}