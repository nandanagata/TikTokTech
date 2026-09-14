package com.aula.tiktoktech;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private FloatingActionButton fabNovaFoto;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.txtVazio).setVisibility(View.VISIBLE);
        toolbar = findViewById(R.id.toolbar);
        fabNovaFoto = findViewById(R.id.fabNovaFoto);
        toolbar.inflateMenu(R.menu.menu_usuario);
        toolbar.setOnMenuItemClickListener(this::aoClicarMenu);
        fabNovaFoto.setOnClickListener(v -> {
            if (UsuarioPrefs.estaLogado(this)) {
                startActivity(new Intent(this, SelfActivity.class));
            } else {
                Toast.makeText(this, R.string.msg_login_obrigatorio, Toast.LENGTH_LONG).show();
                abrirLogin();
            }
        });
        if (!UsuarioPrefs.estaLogado(this)) abrirLogin();
    }

    @Override protected void onResume() {
        super.onResume();
        atualizarUsuario();
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
