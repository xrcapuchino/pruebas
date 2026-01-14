package com.example.saber_share.fragmentos.contenido.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saber_share.R;
import com.example.saber_share.model.MensajeDto;
import java.util.List;

public class BandejaAdapter extends RecyclerView.Adapter<BandejaAdapter.ViewHolder> {

    private List<MensajeDto> chats;
    private int miId;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(int otroId, String otroNombre);
    }

    public BandejaAdapter(List<MensajeDto> chats, int miId, OnChatClickListener listener) {
        this.chats = chats;
        this.miId = miId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Usamos un layout simple de Android o uno personalizado si tienes
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MensajeDto m = chats.get(position);

        // Identificar quién es la "otra persona"
        String nombreMostrar;
        int idOtro;

        if (m.getRemitente().getIdUsuario() == miId) {
            // Yo envié el último, muestro al destinatario
            nombreMostrar = m.getDestinatario().getNombre() + " " + m.getDestinatario().getApellido();
            idOtro = m.getDestinatario().getIdUsuario();
            holder.text2.setText("Tú: " + m.getContenido());
        } else {
            // Me enviaron, muestro al remitente
            nombreMostrar = m.getRemitente().getNombre() + " " + m.getRemitente().getApellido();
            idOtro = m.getRemitente().getIdUsuario();
            holder.text2.setText(m.getContenido());
        }

        holder.text1.setText(nombreMostrar);

        holder.itemView.setOnClickListener(v -> listener.onChatClick(idOtro, nombreMostrar));
    }

    @Override
    public int getItemCount() { return chats.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}