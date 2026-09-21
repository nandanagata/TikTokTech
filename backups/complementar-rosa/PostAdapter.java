package com.aula.tiktoktech;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.tiktoktech.model.Post;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    public interface Acoes {
        void votar(Post post, String campo);
        void comentar(Post post);
    }

    private final List<Post> posts = new ArrayList<>();
    private final Acoes acoes;

    public PostAdapter(Acoes acoes) {
        this.acoes = acoes;
    }

    public void atualizar(List<Post> novosPosts) {
        posts.clear();
        posts.addAll(novosPosts);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.autor.setText(post.getAutor() == null || post.getAutor().isEmpty()
                ? holder.itemView.getContext().getString(R.string.autor_anonimo) : post.getAutor());
        holder.descricao.setText(post.getDescricao());
        holder.likes.setText(String.valueOf(post.getLikes()));
        holder.dislikes.setText(String.valueOf(post.getDislikes()));
        holder.comentarios.setText(String.valueOf(post.getComentarios()));
        if ("video".equals(post.getTipo())) {
            holder.foto.setVisibility(View.GONE);
            holder.video.setVisibility(View.VISIBLE);
            holder.video.setVideoURI(android.net.Uri.parse(post.getUrl()));
            holder.video.setMediaController(new MediaController(holder.itemView.getContext()));
            holder.video.seekTo(1);
        } else {
            holder.video.stopPlayback();
            holder.video.setVisibility(View.GONE);
            holder.foto.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView)
                    .load(post.getUrl())
                    .placeholder(R.drawable.ic_imagem_vazia)
                    .error(R.drawable.ic_imagem_vazia)
                    .centerCrop()
                    .into(holder.foto);
        }
        holder.like.setOnClickListener(v -> acoes.votar(post, "likes"));
        holder.dislike.setOnClickListener(v -> acoes.votar(post, "dislikes"));
        holder.comentar.setOnClickListener(v -> acoes.comentar(post));
    }

    @Override public int getItemCount() { return posts.size(); }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        final ImageView foto;
        final VideoView video;
        final TextView descricao;
        final TextView autor;
        final TextView likes;
        final TextView dislikes;
        final TextView comentarios;
        final ImageButton like;
        final ImageButton dislike;
        final ImageButton comentar;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.imgFoto);
            video = itemView.findViewById(R.id.videoPost);
            descricao = itemView.findViewById(R.id.txtDescricao);
            autor = itemView.findViewById(R.id.txtAutorPost);
            likes = itemView.findViewById(R.id.txtLikes);
            dislikes = itemView.findViewById(R.id.txtDislikes);
            comentarios = itemView.findViewById(R.id.txtComentarios);
            like = itemView.findViewById(R.id.btnLike);
            dislike = itemView.findViewById(R.id.btnDislike);
            comentar = itemView.findViewById(R.id.btnComentario);
        }
    }
}
