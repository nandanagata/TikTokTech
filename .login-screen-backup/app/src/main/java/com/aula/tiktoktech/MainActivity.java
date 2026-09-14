package com.aula.tiktoktech;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
            return true;
        }
        return false;
    }

    /** Serve tanto para o primeiro login quanto para trocar de usuário. */
    private void abrirLogin() {
        EditText campo = new EditText(this);
        campo.setHint(R.string.hint_login);
        campo.setSingleLine(true);
        campo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        campo.setText(UsuarioPrefs.obter(this));
        campo.setSelection(campo.length());
        int espaco = Math.round(24 * getResources().getDisplayMetrics().density);
        androidx.appcompat.widget.LinearLayoutCompat caixa =
                new androidx.appcompat.widget.LinearLayoutCompat(this);
        caixa.setPadding(espaco, 0, espaco, 0);
        caixa.addView(campo, new androidx.appcompat.widget.LinearLayoutCompat.LayoutParams(
                androidx.appcompat.widget.LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                androidx.appcompat.widget.LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle(UsuarioPrefs.estaLogado(this)
                        ? R.string.titulo_trocar_usuario : R.string.titulo_login)
                .setMessage(R.string.login_explicacao)
                .setView(caixa)
                .setNegativeButton(R.string.acao_cancelar, null)
                .setPositiveButton(R.string.acao_salvar, null)
                .create();
        dialogo.setOnShowListener(ignorado -> dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String login = campo.getText().toString().trim();
                    if (login.isEmpty()) {
                        campo.setError(getString(R.string.msg_login_vazio));
                        return;
                    }
                    UsuarioPrefs.salvar(this, login);
                    atualizarUsuario();
                    Toast.makeText(this, getString(R.string.msg_boas_vindas, login), Toast.LENGTH_SHORT).show();
                    dialogo.dismiss();
                }));
        dialogo.show();
    }

    private void atualizarUsuario() {
        String login = UsuarioPrefs.obter(this);
        boolean identificado = !login.isEmpty();
        toolbar.setSubtitle(identificado
                ? getString(R.string.usuario_identificado, login)
                : getString(R.string.usuario_visitante));
        MenuItem usuario = toolbar.getMenu().findItem(R.id.acaoUsuario);
        MenuItem sair = toolbar.getMenu().findItem(R.id.acaoSair);
        usuario.setTitle(identificado ? R.string.acao_trocar_usuario : R.string.acao_entrar);
        sair.setVisible(identificado);
        // Visitante visualiza o feed, mas não inicia POST/upload.
        fabNovaFoto.setVisibility(identificado ? View.VISIBLE : View.GONE);
    }
}
