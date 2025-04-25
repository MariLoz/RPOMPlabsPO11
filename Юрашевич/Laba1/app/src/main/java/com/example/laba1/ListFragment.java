package com.example.laba1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragment extends Fragment {
    private static final String TAG = "ListFragment";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private File cryptoFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cryptoFile = new File(requireContext().getFilesDir(), "crypto_data.txt");
        loadCryptocurrencies();

        setHasOptionsMenu(true);
        return view;
    }

    private void loadCryptocurrencies() {
        progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getApiService();
        Call<CryptoResponse> call = apiService.getCryptocurrencies();

        call.enqueue(new Callback<CryptoResponse>() {
            @Override
            public void onResponse(Call<CryptoResponse> call, Response<CryptoResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Cryptocurrency> cryptos = response.body().getCryptocurrencies();
                    if (cryptos != null && !cryptos.isEmpty()) {
                        saveToFile(cryptos);
                        setupAdapter(cryptos);
                    } else {
                        showError("Список криптовалют пуст");
                    }
                } else {
                    showError("Ошибка сервера: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CryptoResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void saveToFile(List<Cryptocurrency> cryptos) {
        try (FileWriter writer = new FileWriter(cryptoFile)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            writer.write("Курсы криптовалют на " + sdf.format(new Date()) + "\n\n");

            for (Cryptocurrency crypto : cryptos) {
                writer.write(String.format(Locale.US,
                        "▸ %s: %s\nКапитализация: %s\n\n",
                        crypto.getName(),
                        crypto.getPrice(),
                        crypto.getMarketCap()
                ));
            }
            writer.write("Данные предоставлены API криптовалют");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка записи файла", e);
            Toast.makeText(getContext(), "Ошибка сохранения файла", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAdapter(List<Cryptocurrency> cryptos) {
        CryptoAdapter adapter = new CryptoAdapter(cryptos, crypto -> {
            DetailFragment detailFragment = DetailFragment.newInstance(crypto);
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.crypto_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_share) {
            shareToTelegram();
            return true;
        } else if (item.getItemId() == R.id.menu_view_file) {
            openFile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareToTelegram() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, readFileContent());
        intent.setPackage("org.telegram.messenger");

        try {
            startActivity(intent);
        } catch (Exception e) {
            Intent shareIntent = Intent.createChooser(intent, "Поделиться через");
            startActivity(shareIntent);
        }
    }

    private String readFileContent() {
        try {
            return new java.util.Scanner(cryptoFile).useDelimiter("\\Z").next();
        } catch (Exception e) {
            return "Не удалось прочитать данные";
        }
    }

    private void openFile() {
        Uri uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".provider",
                cryptoFile
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "text/plain");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Не найдено приложение для просмотра", Toast.LENGTH_SHORT).show();
        }
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        Log.e(TAG, message);
    }
}