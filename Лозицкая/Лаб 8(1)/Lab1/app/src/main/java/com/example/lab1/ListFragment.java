package com.example.lab1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {
    interface OnFragmentSendDataListener {
        void onSendData(Tank tank);
    }
    private OnFragmentSendDataListener fragmentSendDataListener;
    private final List<Tank> tanks = new ArrayList<>();
    private List<Tank> currentTanks = new ArrayList<>();
    private TankAdapter tankAdapter;
    private int itemsPerPage = 5;
    private int currentPage = 1;
    private int totalPages = 1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            fragmentSendDataListener = (OnFragmentSendDataListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " должен реализовывать интерфейс OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView tankListRecyclerView = view.findViewById(R.id.tankList);

        currentTanks = new ArrayList<>();
        tankAdapter = new TankAdapter(getContext(), R.layout.list_item_tank, currentTanks);
        tankListRecyclerView.setAdapter(tankAdapter);
        tankListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ButtonPaginationClickListener btnClickListener = new ButtonPaginationClickListener();
        view.findViewById(R.id.prevButton).setOnClickListener(btnClickListener);
        view.findViewById(R.id.nextButton).setOnClickListener(btnClickListener);

        if (getArguments() != null) {
            itemsPerPage = getArguments().getInt("itemsPerPage", 5);
        }

        tankAdapter.setOnItemClickListener(new TankAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Tank selectedTank = currentTanks.get(position);
                fragmentSendDataListener.onSendData(selectedTank);
            }
        });

        updatePageNumberText();
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadTanksFromJson(JSONArray tanksJsonArray) {
        tanks.clear();
        currentTanks.clear();

        try {
            for (int i = 0; i < tanksJsonArray.length(); i++) {
                JSONObject tankObject = tanksJsonArray.getJSONObject(i);

                String name = tankObject.getString("name");
                String nation = tankObject.getString("nation");
                int level = tankObject.getInt("level");
                String type = tankObject.getString("type");
                int armor = tankObject.getInt("armor");
                String gun = tankObject.getString("gun");
                int speed = tankObject.getInt("speed");
                int cost = tankObject.getInt("cost");
                String photoName = tankObject.getString("photo");

                @SuppressLint("DiscouragedApi")
                int photoResource = getResources().getIdentifier(photoName.substring(0, photoName.lastIndexOf('.')), "drawable", requireContext().getPackageName());

                if (photoResource != 0) {
                    Tank tank;
                    if (photoName.equals("110.png"))
                        tank = new Tank(name, nation, level, type, armor, gun, speed, cost, R.drawable.i110);
                    else
                        tank = new Tank(name, nation, level, type, armor, gun, speed, cost, photoResource);
                    tanks.add(tank);
                }
                else {
                    Tank tank = new Tank(name, nation, level, type, armor, gun, speed, cost, R.drawable.t34);
                    tanks.add(tank);
                }
            }

            currentPage = 1;
            calculateTotalPages();
            updateTankList();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show();
        } finally {
            tankAdapter.notifyDataSetChanged();
            updatePageNumberText();
            Toast.makeText(getContext(), "Данные успешно добавлены", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateTotalPages() {
        totalPages = (int) Math.ceil((double) tanks.size() / itemsPerPage);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateTankList() {
        currentTanks.clear();
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, tanks.size());

        for (int i = startIndex; i < endIndex; i++) {
            currentTanks.add(tanks.get(i));
        }
        tankAdapter.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void updatePageNumberText() {
        View view = getView();
        if (view == null) {
            return;
        }
        TextView pageNumberTextView = view.findViewById(R.id.pageNumber);
        if (pageNumberTextView == null) {
            return;
        }
        pageNumberTextView.setText("Страница " + currentPage + " из " + totalPages);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearTanks() {
        tanks.clear();
        currentTanks.clear();
        tankAdapter.notifyDataSetChanged();
        currentPage = 0;
        totalPages = 0;
        updatePageNumberText();
    }

    private class ButtonPaginationClickListener implements View.OnClickListener {
        @Override
        public void onClick(@NonNull View v) {
            int id = v.getId();
            if (id == R.id.prevButton) {
                if (currentPage > 1) {
                    currentPage--;
                    updateTankList();
                    updatePageNumberText();
                }
            } else if (id == R.id.nextButton) {
                if (currentPage < totalPages) {
                    currentPage++;
                    updateTankList();
                    updatePageNumberText();
                }
            }
        }
    }
}
