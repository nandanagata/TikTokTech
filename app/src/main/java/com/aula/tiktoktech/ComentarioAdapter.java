package com.aula.tiktoktech;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    public interface Acoes { void votar(Comentario comentario, String campo); }

    private final List<Comentario> comentarios = new ArrayList<>();
    private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
    private final Acoes acoes;

    public ComentarioAdapter(Acoes acoes) {
        this.acoes = acoes;
    }

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
        holder.likes.setText(String.valueOf(comentario.getLikes()));
        holder.dislikes.setText(String.valueOf(comentario.getDislikes()));
        holder.like.setOnClickListener(v -> acoes.votar(comentario, "likes"));
        holder.dislike.setOnClickListener(v -> acoes.votar(comentario, "dislikes"));
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        final TextView autor;
        final TextView data;
        final TextView texto;
        final TextView likes;
        final TextView dislikes;
        final ImageButton like;
        final ImageButton dislike;

        ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            autor = itemView.findViewById(R.id.txtAutor);
            data = itemView.findViewById(R.id.txtData);
            texto = itemView.findViewById(R.id.txtTexto);
            likes = itemView.findViewById(R.id.txtLikesComentario);
            dislikes = itemView.findViewById(R.id.txtDislikesComentario);
            like = itemView.findViewById(R.id.btnLikeComentario);
            dislike = itemView.findViewById(R.id.btnDislikeComentario);
        }
    }
}
