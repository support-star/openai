package com.kalkulation.matrix.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kalkulation.matrix.R;
import com.kalkulation.matrix.model.KalkulationsPosition;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PositionAdapter extends RecyclerView.Adapter<PositionAdapter.ViewHolder> {

    private List<KalkulationsPosition> positionen = new ArrayList<>();
    private OnPositionClickListener clickListener;
    private final NumberFormat waehrungsFormat;

    public interface OnPositionClickListener {
        void onPositionClick(KalkulationsPosition position);
    }

    public PositionAdapter() {
        waehrungsFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
    }

    public void setOnPositionClickListener(OnPositionClickListener listener) {
        this.clickListener = listener;
    }

    public void setPositionen(List<KalkulationsPosition> positionen) {
        this.positionen = positionen != null ? positionen : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_position, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        KalkulationsPosition pos = positionen.get(position);

        holder.txtPosNr.setText(pos.getPositionsNummer());
        holder.txtBezeichnung.setText(pos.getBezeichnung());
        holder.txtKategorie.setText(pos.getKategorie());

        String kosten = waehrungsFormat.format(pos.getGesamtKosten()) + " / " + pos.getEinheit();
        holder.txtKosten.setText(kosten);

        String stunden = String.format(Locale.GERMANY, "%.2f h", pos.getStundenSatz());
        holder.txtStunden.setText(stunden);

        if (pos.getGewerk() != null && !pos.getGewerk().isEmpty()) {
            holder.txtGewerk.setText(pos.getGewerk());
            holder.txtGewerk.setVisibility(View.VISIBLE);
        } else {
            holder.txtGewerk.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onPositionClick(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return positionen.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPosNr, txtBezeichnung, txtKategorie, txtKosten, txtStunden, txtGewerk;

        ViewHolder(View itemView) {
            super(itemView);
            txtPosNr = itemView.findViewById(R.id.txtPosNr);
            txtBezeichnung = itemView.findViewById(R.id.txtBezeichnung);
            txtKategorie = itemView.findViewById(R.id.txtKategorie);
            txtKosten = itemView.findViewById(R.id.txtKosten);
            txtStunden = itemView.findViewById(R.id.txtStunden);
            txtGewerk = itemView.findViewById(R.id.txtGewerk);
        }
    }
}
