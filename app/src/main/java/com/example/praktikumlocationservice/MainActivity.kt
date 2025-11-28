package com.example.praktikumlocationservice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        enableEdgeToEdge()
        setContent {
            LocationApp(fusedLocationClient)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationApp(
    fusedLocationClient: FusedLocationProviderClient,
    viewModel: LocationViewModel = viewModel()
) {
    val context = LocalContext.current

    // 1. Inisialisasi Launcher untuk Izin Lokasi
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Izin diberikan, panggil fungsi fetch di ViewModel
            viewModel.startLocationFetch(fusedLocationClient)
        } else {
            // Izin ditolak
            viewModel.updateLocationStateToError("Izin lokasi diperlukan untuk melanjutkan.")
        }
    }

    // 2. Panggil Izin saat Composable pertama kali muncul
    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        when {
            // Jika izin sudah diberikan sebelumnya
            ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED -> {
                viewModel.startLocationFetch(fusedLocationClient)
            }
            // Jika izin belum diberikan, minta izin
            else -> {
                locationPermissionLauncher.launch(permission)
            }
        }
    }

    // 3. UI Berdasarkan State
    val state = viewModel.locationState.value

    Scaffold(
        topBar = { TopAppBar(title = { Text("Lokasi Saya (Compose)") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            when (state) {
                is LocationState.Idle -> Text("Menunggu izin...")
                is LocationState.Loading ->
                    CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                is LocationState.Success -> {
                    Text("✅ Lokasi Ditemukan!", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Latitude: ${state.location.latitude}")
                    Text("Longitude: ${state.location.longitude}")
                    Spacer(Modifier.height(16.dp))
                    // Alamat hasil Reverse Geocoding
                    Text("Alamat:", fontWeight = FontWeight.SemiBold)
                    Text(viewModel.addressResult.value)
                }
                is LocationState.Error -> {
                    Text("❌ Gagal: ${state.message}", color = Color.Red)
                }
            }

            // Tombol untuk memuat ulang lokasi
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Refresh / Minta Ulang Izin Lokasi")
            }
        }
    }
}