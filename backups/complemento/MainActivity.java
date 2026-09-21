package com.aula.tiktoktech;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.tiktoktech.model.Post;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PostAdapter.Acoes {
    public static final String EXTRA_POST_ID = "postId";
    private MaterialToolbar toolbar;
    private FloatingActionButton fabNovaFoto;
    private TextView txtVazio;
    private ProgressBar progress;
    private PostAdapter adapter;
    private FirebaseFirestore banco;
    private ListenerRegistration feedListener;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        banco = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.toolbar);
        fabNovaFoto = findViewById(R.id.fabNovaFoto);
        txtVazio = findViewById(R.id.txtVazio);
        progress = findViewById(R.id.progress);
        RecyclerView recycler = findViewById(R.id.recyclerPosts);
        adapter = new PostAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        toolbar.inflateMenu(R.menu.menu_usuario);
        toolbar.setOnMenuItemClickListener(this::aoClicarMenu);
        fabNovaFoto.setOnClickListener(v -> {
            if (UsuarioPrefs.estaLogado(this)) {
                startActivity(new Intent(this, SelfActivity.class));
            } else {
                exigirLogin();
            }
        });
        if (!UsuarioPrefs.estaLogado(this)) abrirLogin();
    }

    @Override protected void onStart() {
        super.onStart();
        ouvirFeed();
    }

    @Override protected void onStop() {
        if (feedListener != null) {
            feedListener.remove();
            feedListener = null;
        }
        super.onStop();
    }

    @Override protected void onResume() {
        super.onResume();
        atualizarUsuario();
    }

    /** Listener contínuo: qualquer alteração no Firestore atualiza a lista aberta. */
    private void ouvirFeed() {
        progress.setVisibility(View.VISIBLE);
        feedListener = banco.collection("posts")
                .orderBy("criadoEm", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, erro) -> {
                    progress.setVisibility(View.GONE);
                    if (erro != null) {
                        Toast.makeText(this,
                                getString(R.string.msg_erro_feed, erro.getMessage()),
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    List<Post> posts = snapshot == null
                            ? java.util.Collections.emptyList()
                            : snapshot.toObjects(Post.class);
                    adapter.atualizar(posts);
                    txtVazio.setVisibility(posts.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    @Override public void votar(Post post, String campo) {
        if (!UsuarioPrefs.estaLogado(this)) {
            exigirLogin();
            return;
        }
        if (post.getId() == null || !(campo.equals("likes") || campo.equals("dislikes"))) return;
        // Incremento atômico evita que votos simultâneos sobrescrevam um ao outro.
        banco.collection("posts").document(post.getId())
                .update(campo, FieldValue.increment(1))
                .addOnFailureListener(erro -> Toast.makeText(this,
                        getString(R.string.msg_erro_voto, erro.getMessage()), Toast.LENGTH_LONG).show());
    }

    @Override public void comentar(Post post) {
        if (post.getId() == null) return;
        startActivity(new Intent(this, ComentariosActivity.class)
                .putExtra(EXTRA_POST_ID, post.getId()));
    }

    private boolean aoClicarMenu(MenuItem item) {
        if (item.getItemId() == R.id.acaoUsuario) {
            abrirLogin();
            return true;
        }
        if (item.getItemId() == R.id.acaoSair) {
            UsuarioPrefs.sair(this);
            atualizarUsuario();
            Toast.makeText(this, R.string.msg_logout, Toast.LENGTH_SHORT).show();
            abrirLogin();
            return true;
        }
        return false;
    }

    private void exigirLogin() {
        Toast.makeText(this, R.string.msg_login_obrigatorio, Toast.LENGTH_LONG).show();
        abrirLogin();
    }

    private void abrirLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void atualizarUsuario() {
        String email = UsuarioPrefs.obter(this);
        boolean identificado = !email.isEmpty();
        toolbar.setSubtitle(identificado
                ? getString(R.string.usuario_identificado, email)
                : getString(R.string.usuario_visitante));
        toolbar.getMenu().findItem(R.id.acaoUsuario).setTitle(
                identificado ? R.string.acao_trocar_usuario : R.string.acao_entrar);
        toolbar.getMenu().findItem(R.id.acaoSair).setVisible(identificado);
        fabNovaFoto.setVisibility(identificado ? View.VISIBLE : View.GONE);
    }
}
