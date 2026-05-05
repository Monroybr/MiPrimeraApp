package com.liseth.miprimeraapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario, etPassword;
    private Button btnIngresar;
    private TextView tvMensajeLogin, tvIrRegistro;

    // Aquí declaro el DAO para consultar los usuarios guardados en SQLite
    private UsuarioDAO usuarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Aquí inicializo el DAO de usuarios
        usuarioDAO = new UsuarioDAO(this);

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnIngresar = findViewById(R.id.btnIngresar);
        tvMensajeLogin = findViewById(R.id.tvMensajeLogin);
        tvIrRegistro = findViewById(R.id.tvIrRegistro);

        // Aquí envío al usuario a la pantalla de registro
        tvIrRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Aquí valido el inicio de sesión con SQLite
        btnIngresar.setOnClickListener(v -> {
            String correo = etUsuario.getText().toString().trim();
            String contrasena = etPassword.getText().toString().trim();

            if (correo.isEmpty() || contrasena.isEmpty()) {
                tvMensajeLogin.setText("Por favor completa usuario y contraseña.");
                return;
            }

            boolean accesoValido = usuarioDAO.validarLogin(correo, contrasena);

            if (accesoValido) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                tvMensajeLogin.setText("Usuario o contraseña incorrectos.");
            }
        });
    }
}