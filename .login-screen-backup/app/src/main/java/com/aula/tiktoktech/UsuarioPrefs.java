package com.aula.tiktoktech;

import android.content.Context;

/** Único ponto de acesso ao login persistido do aplicativo. */
public final class UsuarioPrefs {
    private static final String ARQUIVO = "usuario";
    private static final String CHAVE_LOGIN = "login";

    private UsuarioPrefs() { }

    public static String obter(Context context) {
        return context.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE)
                .getString(CHAVE_LOGIN, "").trim();
    }

    public static boolean estaLogado(Context context) {
        return !obter(context).isEmpty();
    }

    public static void salvar(Context context, String login) {
        context.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE)
                .edit().putString(CHAVE_LOGIN, login.trim()).apply();
    }

    public static void sair(Context context) {
        context.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE)
                .edit().remove(CHAVE_LOGIN).apply();
    }
}
