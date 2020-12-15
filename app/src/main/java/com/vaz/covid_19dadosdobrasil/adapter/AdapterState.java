package com.vaz.covid_19dadosdobrasil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vaz.covid_19dadosdobrasil.R;
import com.vaz.covid_19dadosdobrasil.activity.MainActivity;
import com.vaz.covid_19dadosdobrasil.model.State;

import java.util.List;

public class AdapterState extends RecyclerView.Adapter<AdapterState.MyViewHolder> {

    private Context context;
    private List<State> states;

    public AdapterState(List<State> states, Context context){
        this.states = states;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_list_card, parent, false);

        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        State state = states.get(position);

        holder.state.setText(state.getState());
        holder.uf.setText(state.getUf());
        holder.deaths.setText("Mortes: "+state.getDeaths());
        holder.cases.setText("Confirmados: "+state.getCases());

        Glide.with(holder.itemView.getContext())
                .load(getImage(state.getImage()))
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return states.size();
    }

    public int getImage(String imageName) {

        int drawableResourceId = context.getResources()
                .getIdentifier(
                        imageName,
                        "drawable",
                        context.getApplicationContext().getPackageName()
                );

        return drawableResourceId;
    }

    public void filterList(List<State> filteredList){
        states = filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView state;
        private TextView uf;
        private TextView deaths;
        private TextView cases;
        private ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            state = itemView.findViewById(R.id.textState);
            uf = itemView.findViewById(R.id.textUf);
            deaths = itemView.findViewById(R.id.textDeaths);
            cases = itemView.findViewById(R.id.textCases);
            image = itemView.findViewById(R.id.image);
        }
    }

}
