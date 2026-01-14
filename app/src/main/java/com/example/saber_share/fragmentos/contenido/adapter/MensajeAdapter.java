package com.example.saber_share.fragmentos.contenido.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saber_share.R;
import com.example.saber_share.model.MensajeDto;

import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {

    private Context context;
    private List<MensajeDto> lista;
    private int miId;

    public MensajeAdapter(Context context, List<MensajeDto> lista, int miId) {
        this.context = context;
        this.lista = lista;
        this.miId = miId;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el diseño de la burbuja individual
        View view = LayoutInflater.from(context).inflate(R.layout.item_mensaje_chat, parent, false);
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        MensajeDto m = lista.get(position);

        // Determinar si el mensaje es mío
        int idRemitente = -1;
        if (m.getRemitente() != null && m.getRemitente().getIdUsuario() != null) {
            idRemitente = m.getRemitente().getIdUsuario();
        }

        if (idRemitente == miId) {
            // ES MÍO: Alinear a la derecha
            holder.contenedor.setGravity(Gravity.END);
            holder.burbuja.setBackgroundResource(R.drawable.bg_chat_mios);
        } else {
            // ES DEL OTRO: Alinear a la izquierda
            holder.contenedor.setGravity(Gravity.START);
            holder.burbuja.setBackgroundResource(R.drawable.bg_chat_otro);
        }

        holder.tvContenido.setText(m.getContenido());
        holder.tvHora.setText(m.getFechaEnvio() != null ? m.getFechaEnvio() : "");
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // Método para agregar un mensaje individualmente sin recargar todo
    public void agregarMensaje(MensajeDto mensaje) {
        this.lista.add(mensaje);
        notifyItemInserted(this.lista.size() - 1);
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder {
        LinearLayout contenedor, burbuja;
        TextView tvContenido, tvHora;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            // Estos IDs deben estar en item_chat_mensaje.xml
            contenedor = itemView.findViewById(R.id.chatContenedor);
            burbuja = itemView.findViewById(R.id.chatBurbuja);
            tvContenido = itemView.findViewById(R.id.tvChatTexto);
            tvHora = itemView.findViewById(R.id.tvChatHora);
        }
    }
}