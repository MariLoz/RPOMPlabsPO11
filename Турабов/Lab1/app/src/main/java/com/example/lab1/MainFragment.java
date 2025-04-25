package com.example.lab1;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

public class MainFragment extends Fragment {

    LinkedList<Benchmark> benchmarks = new LinkedList<Benchmark>();
    LinkedList<Benchmark> benchmarks_backup = new LinkedList<Benchmark>();
    ListView benchmarksList;
    BenchmarkAdapter adapter;

    Spinner name;
    String filename;

    public MainFragment() {
        // Required empty public constructor
    }
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        benchmarksList = view.findViewById(R.id.benchmarksList);
        adapter = new BenchmarkAdapter(getActivity(), R.layout.benchmark_item, benchmarks);
        benchmarksList.setAdapter(adapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Benchmark selected = (Benchmark)parent.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(Benchmark.class.getSimpleName(), selected);
                startActivity(intent);
            }
        };
        benchmarksList.setOnItemClickListener(itemClickListener);

        Button testButton = view.findViewById(R.id.sort);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox amd = view.findViewById(R.id.amd);
                CheckBox nvidia = view.findViewById(R.id.nvidia);
                boolean amd_allowed = amd.isChecked();
                boolean nvidia_allowed = nvidia.isChecked();

                benchmarks.clear();
                for(int i = 0; i < benchmarks_backup.size(); i++)
                {
                    Benchmark current = benchmarks_backup.get(i);
                    if(current.getDevice().contains("AMD") && amd_allowed || current.getDevice().contains("NVIDIA") && nvidia_allowed)
                    {
                        benchmarks.add(current);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        String[] versions= {"3-2-0","3-2-1","3-3-0","3-4-0","3-5-0","3-6-0","4-0-0","4-1-0","4-2-0","4-3-0"};

        name = getActivity().findViewById(R.id.download_name);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, versions);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        name.setAdapter(adapter);

        Button downloadButton = view.findViewById(R.id.download);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ver = name.getSelectedItem().toString();
                filename = ver;
                String fileUrl = "https://raw.githubusercontent.com/Andrey-Turabov/Labs/refs/heads/main/Benchmark-"+ver+".json";
                new DownloadFileTask().execute(fileUrl);
            }
        });

        Button uploadButton = view.findViewById(R.id.upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit = view.findViewById(R.id.upload_name);
                String name = edit.getText().toString();
                uploadData(name);
            }
        });
    }

    private class DownloadFileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String fileUrl = urls[0];
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }
                else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                }

            } catch (Exception e) {
                return null;
            }
            return result.toString();
        }
        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                Toast.makeText(getActivity(), "JSON загружен с сайта.", Toast.LENGTH_SHORT).show();
                parseData(json);
                saveData(json);
            } else {
                Toast.makeText(getActivity(), "Ошибка загрузки JSON.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void parseData(String json) {
        benchmarks.clear();
        benchmarks_backup.clear();
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray dataArray = jsonObject.getJSONArray("body");
            for (int i = 0; i < dataArray.length(); i++)
            {
                JSONArray benchmarkObj = dataArray.getJSONArray(i);
                String name = benchmarkObj.get(0).toString();
                String score = benchmarkObj.get(1).toString();
                String num_of_benchmarks = benchmarkObj.get(2).toString();

                benchmarks.add(new Benchmark(name, score, num_of_benchmarks, (name.contains("NVIDIA"))? R.drawable.nvidia:R.drawable.amd));

            }
            benchmarks_backup.addAll(benchmarks);
            adapter.notifyDataSetChanged();
        }
        catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_LONG).show();
            Log.println(Log.ERROR, "",e.getMessage());
        }

    }

    private void saveData(String json)
    {
        try {
            FileOutputStream fos = getActivity().openFileOutput(filename+".json", Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadData(String name)
    {
        FileInputStream is= null;
        try {
            is = getActivity().openFileInput(name + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer);
            parseData(json);
        } catch (IOException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            // throw new RuntimeException(e);
        }
    }


}


