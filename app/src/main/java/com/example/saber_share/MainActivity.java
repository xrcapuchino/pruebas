package com.example.saber_share;

import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.saber_share.util.local.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        setupNavigation();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            bottomNav = findViewById(R.id.bottomBar);

            // 1. Vinculación automática estándar
            NavigationUI.setupWithNavController(bottomNav, navController);

            // 2. CORRECCIÓN DE LA "SOMBRA" (Selección manual para sub-menús)
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();

                // --- FLUJO COMPRAR ---
                if (id == R.id.detallePublicacion ||
                        id == R.id.agendarClase ||
                        id == R.id.editarPublicacion) {

                    if (bottomNav.getSelectedItemId() != R.id.comprar) {
                        bottomNav.getMenu().findItem(R.id.comprar).setChecked(true);
                    }
                }

                // --- FLUJO PERFIL (NUEVO) ---
                // Aquí agregamos todos los fragmentos que nacen del Perfil
                else if (id == R.id.historial ||
                        id == R.id.administrarTarjetas ||
                        id == R.id.agregarTarjeta ||
                        id == R.id.misClases) { // La agenda general

                    if (bottomNav.getSelectedItemId() != R.id.perfil) {
                        bottomNav.getMenu().findItem(R.id.perfil).setChecked(true);
                    }
                }

                // --- FLUJO VENDER ---
                else if (id == R.id.resumenPublicacion ||
                        id == R.id.gestionarAgenda) { // La gestión de horarios del profe

                    // OJO: gestionarAgenda viene de DetallePublicacion (Comprar),
                    // pero conceptualmente es una acción de venta.
                    // Si prefieres que se ilumine 'Comprar' (porque estás viendo tu producto), muévelo al bloque de arriba.
                    // Si prefieres 'Vender', déjalo aquí. Yo lo pondría en 'Comprar' si el acceso es desde la lista de productos.

                    if (bottomNav.getSelectedItemId() != R.id.vender) {
                        bottomNav.getMenu().findItem(R.id.vender).setChecked(true);
                    }
                }

                // Ocultar menú en Login/Registro
                if (id == R.id.inicioSesion || id == R.id.registroSesion || id == R.id.cuentaAutentificacion) {
                    bottomNav.setVisibility(View.GONE);
                } else {
                    bottomNav.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}