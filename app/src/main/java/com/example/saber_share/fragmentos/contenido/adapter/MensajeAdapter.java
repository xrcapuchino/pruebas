package com.example.saber_share.fragmentos.contenido.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saber_share.R;
import com.example.saber_share.model.MensajeDto;
import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {

    private List<MensajeDto> mensajes;
    private int miIdUsuario;

    public MensajeAdapter(List<MensajeDto> mensajes, int miIdUsuario) {
        this.mensajes = mensajes;
        this.miIdUsuario = miIdUsuario;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje_chat, parent, false);
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        MensajeDto m = mensajes.get(position);
        holder.tvContenido.setText(m.getContenido());

        // CORREGIDO: Usamos getIdUsuario()
        boolean esMio = (m.getRemitente() != null && m.getRemitente().getIdUsuario() == miIdUsuario);

        if (esMio) {
            holder.layoutPadre.setGravity(Gravity.END);
            holder.cardBurbuja.setCardBackgroundColor(Color.parseColor("#DCF8C6"));
            holder.tvContenido.setTextColor(Color.BLACK);
        } else {
            holder.layoutPadre.setGravity(Gravity.START);
            holder.cardBurbuja.setCardBackgroundColor(Color.WHITE);
            holder.tvContenido.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() { return mensajes.size(); }

    public void agregarMensaje(MensajeDto m) {
        mensajes.add(m);
        notifyItemInserted(mensajes.size() - 1);
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutPadre;
        CardView cardBurbuja;
        TextView tvContenido;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutPadre = itemView.findViewById(R.id.layoutMensajePadre);
            cardBurbuja = itemView.findViewById(R.id.cardMensajeBurbuja);
            tvContenido = itemView.findViewById(R.id.tvMensajeContenido);
        }
    }
}