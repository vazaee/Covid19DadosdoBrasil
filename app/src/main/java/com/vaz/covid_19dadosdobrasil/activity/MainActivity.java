package com.vaz.covid_19dadosdobrasil.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vaz.covid_19dadosdobrasil.ClickListener;
import com.vaz.covid_19dadosdobrasil.R;
import com.vaz.covid_19dadosdobrasil.adapter.AdapterState;
import com.vaz.covid_19dadosdobrasil.model.Connection;
import com.vaz.covid_19dadosdobrasil.model.State;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText search;
    private TextView emptyView;
    private ImageButton config;
    private AdapterState adapterState;
    private List<State> states = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView    = findViewById(R.id.recyclerView);
        search          = findViewById(R.id.search_bar);
        config          = findViewById(R.id.more);
        emptyView       = findViewById(R.id.empty_view);

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                List<State> filtered = filter(editable.toString());
                hideOrShowRecycler(filtered);
            }
        });


        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(getApplicationContext(), config);
                popup.getMenuInflater().inflate(R.menu.settings_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        CharSequence title = item.getTitle();
                        if ("Saiba mais".equals(title)) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setData(Uri.parse("https://covid.saude.gov.br/"));
                            startActivity(intent);
                        }

                        return true;
                    }
                });

                popup.show();
            }
        });

        Task task = new Task();
        task.execute("https://covid19-brazil-api.now.sh/api/report/v1");

    }

    private void hideOrShowRecycler(List<State> states){
        if(states.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private List<State> filter(String text){

        List<State> filtered = new ArrayList<>();

        if (adapterState != null){
            text = normalizeString(text);


            for (State state : states){
                String stateName    = normalizeString(state.getState());
                String ufName       = normalizeString(state.getUf());
                text                = text.toLowerCase();

                if (stateName.toLowerCase().contains(text)
                        || ufName.toLowerCase().contains(text)){
                    filtered.add(state);
                }
            }
            adapterState.filterList(filtered);
        }

        return filtered;
    }

    private String normalizeString(String string){
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        string = string.replaceAll("[^\\p{ASCII}]", "");
        return string;
    }

    private class Task extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String data = Connection.getData(strings[0]);
            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            Gson gson = new Gson();

            try {

                JSONObject object = new JSONObject(s);
                String location = object.getString("data");
                states = gson.fromJson(location, new TypeToken<List<State>>(){}.getType());

                hideOrShowRecycler(states);

                for( State state : states){
                    String uf = state.getUf().toLowerCase();
                    state.setImage(uf);
                }

                adapterState = new AdapterState(states, getApplicationContext());

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapterState);

                recyclerView.addOnItemTouchListener(new ClickListener(
                        getApplicationContext(),
                        recyclerView,
                        new ClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                createDialog(position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) { }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { }
                        }
                ));

            }catch (JSONException err){
                err.printStackTrace();
            }

        }

        private void createDialog(int position){

            State state = states.get(position);

            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
            dialog.setTitle(state.getState());
            dialog.setCancelable(false);
            dialog.setIcon(R.drawable.ic_baseline_place_24);
            dialog.setPositiveButton("Fechar", null);

            View customLayout = getLayoutInflater().inflate(R.layout.dialog_layout, null);

            TextView cases      =   (TextView) customLayout.findViewById(R.id.cases_data);
            TextView deaths     =   (TextView) customLayout.findViewById(R.id.deaths_data);
            TextView suspects   =   (TextView) customLayout.findViewById(R.id.suspects_data);
            TextView refuses    =   (TextView) customLayout.findViewById(R.id.refuses_data);
            TextView date       =   (TextView) customLayout.findViewById(R.id.date_data);

            deaths.setText(state.getDeaths());
            cases.setText(state.getCases());
            suspects.setText(state.getSuspects());
            refuses.setText(state.getRefuses());
            date.setText(state.getDatetime());

            dialog.setView(customLayout);
            dialog.create();
            dialog.show();
        }
    }
}