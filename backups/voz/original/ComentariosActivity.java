package com.aula.tiktoktech;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

public class ComentariosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        ((MaterialToolbar) findViewById(R.id.toolbarComentarios))
                .setNavigationOnClickListener(v -> finish());
        TextInputEditText autor = findViewById(R.id.edtAutor);
        View painel = findViewById(R.id.painelNovoComentario);
        View aviso = findViewById(R.id.txtLoginComentarios);
        boolean logado = UsuarioPrefs.estaLogado(this);
        painel.setVisibility(logado ? View.VISIBLE : View.GONE);
        aviso.setVisibility(logado ? View.GONE : View.VISIBLE);
        if (logado) {
            autor.setText(UsuarioPrefs.obter(this));
            autor.setEnabled(false);
        }
        findViewById(R.id.btnEnviarComentario).setOnClickListener(v -> {
            if (!UsuarioPrefs.estaLogado(this)) {
                Toast.makeText(this, R.string.msg_login_obrigatorio, Toast.LENGTH_LONG).show();
            }
            // A persistência dos comentários pertence à etapa do feed.
        });

    }

}
