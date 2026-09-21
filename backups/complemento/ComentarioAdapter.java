package com.aula.tiktoktech;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.tiktoktech.model.Comentario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> {
    private final List<Comentario> comentarios = new ArrayList<>();
    private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

    public void atualizar(List<Comentario> novosComentarios) {
        comentarios.clear();
        comentarios.addAll(novosComentarios);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = comentarios.get(position);
        holder.autor.setText(comentario.getAutor());
        holder.texto.setText(comentario.getTexto());
        Date criadoEm = comentario.getCriadoEm();
        holder.data.setText(criadoEm == null ? "" : formatoData.format(criadoEm));
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        final TextView autor;
        final TextView data;
        final TextView texto;

        ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            autor = itemView.findViewById(R.id.txtAutor);
            data = itemView.findViewById(R.id.txtData);
            texto = itemView.findViewById(R.id.txtTexto);
        }
    }
}
