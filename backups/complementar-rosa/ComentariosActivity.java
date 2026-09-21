package com.aula.tiktoktech;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.tiktoktech.model.Comentario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

public class ComentariosActivity extends AppCompatActivity {

    private FirebaseFirestore banco;
    private ComentarioAdapter adapter;
    private ListenerRegistration listenerComentarios;
    private String postId;
    private TextInputEditText edtAutor;
    private TextInputEditText edtComentario;
    private View painelNovoComentario;
    private View avisoLogin;
    private View txtVazio;
    private MaterialButton btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        postId = getIntent().getStringExtra(MainActivity.EXTRA_POST_ID);
        if (postId == null || postId.trim().isEmpty()) {
            Toast.makeText(this, R.string.msg_post_invalido, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        banco = FirebaseFirestore.getInstance();
        ((MaterialToolbar) findViewById(R.id.toolbarComentarios))
                .setNavigationOnClickListener(v -> finish());

        edtAutor = findViewById(R.id.edtAutor);
        edtComentario = findViewById(R.id.edtComentario);
        painelNovoComentario = findViewById(R.id.painelNovoComentario);
        avisoLogin = findViewById(R.id.txtLoginComentarios);
        txtVazio = findViewById(R.id.txtVazioComentarios);
        btnEnviar = findViewById(R.id.btnEnviarComentario);

        RecyclerView recyclerComentarios = findViewById(R.id.recyclerComentarios);
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ComentarioAdapter(this::votarComentario);
        recyclerComentarios.setAdapter(adapter);

        atualizarPainelUsuario();
        btnEnviar.setOnClickListener(v -> enviarComentario());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (postId != null) {
            ouvirComentarios();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarPainelUsuario();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listenerComentarios != null) {
            listenerComentarios.remove();
            listenerComentarios = null;
        }
    }

    private void atualizarPainelUsuario() {
        boolean logado = UsuarioPrefs.estaLogado(this);
        painelNovoComentario.setVisibility(logado ? View.VISIBLE : View.GONE);
        avisoLogin.setVisibility(logado ? View.GONE : View.VISIBLE);
        if (logado) {
            edtAutor.setText(UsuarioPrefs.obter(this));
            edtAutor.setEnabled(false);
        }
    }

    private void ouvirComentarios() {
        listenerComentarios = banco.collection("POSTS_2G")
                .document(postId)
                .collection("comentarios")
                .orderBy("criadoEm", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshot, erro) -> {
                    if (erro != null || snapshot == null) {
                        return;
                    }
                    adapter.atualizar(snapshot.toObjects(Comentario.class));
                    txtVazio.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                });
    }

    private void enviarComentario() {
        if (!UsuarioPrefs.estaLogado(this)) {
            Toast.makeText(this, R.string.msg_login_obrigatorio, Toast.LENGTH_LONG).show();
            return;
        }

        String texto = edtComentario.getText() == null ? "" : edtComentario.getText().toString().trim();
        if (texto.isEmpty()) {
            edtComentario.setError(getString(R.string.msg_comentario_vazio));
            edtComentario.requestFocus();
            return;
        }

        btnEnviar.setEnabled(false);
        DocumentReference post = banco.collection("POSTS_2G").document(postId);
        WriteBatch lote = banco.batch();
        lote.set(post.collection("comentarios").document(),
                new Comentario(UsuarioPrefs.obter(this), texto));
        lote.update(post, "comentarios", FieldValue.increment(1));
        lote.commit()
                .addOnSuccessListener(ignorado -> edtComentario.setText(""))
                .addOnFailureListener(erro -> Toast.makeText(this,
                        R.string.msg_erro_comentario, Toast.LENGTH_LONG).show())
                .addOnCompleteListener(tarefa -> btnEnviar.setEnabled(true));
    }

    private void votarComentario(Comentario comentario, String campo) {
        if (!UsuarioPrefs.estaLogado(this)) {
            Toast.makeText(this, R.string.msg_login_obrigatorio, Toast.LENGTH_LONG).show();
            return;
        }
        if (comentario.getId() == null || !(campo.equals("likes") || campo.equals("dislikes"))) return;
        banco.collection("POSTS_2G").document(postId).collection("comentarios")
                .document(comentario.getId()).update(campo, FieldValue.increment(1))
                .addOnFailureListener(erro -> Toast.makeText(this,
                        getString(R.string.msg_erro_voto, erro.getMessage()), Toast.LENGTH_LONG).show());
    }
}
