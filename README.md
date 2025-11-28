# 📍 Praktikum Location Services - Android

Aplikasi Android sederhana yang mengimplementasikan **Location Services** menggunakan **Fused Location Provider** dari Google Play Services untuk menampilkan lokasi pengguna dan mengubahnya menjadi alamat yang dapat dibaca.

## 🚀 Instalasi

### 1. Clone atau Download Project

```bash
git clone <repository-url>
cd PraktikumLocationServices
```

### 2. Buka di Android Studio

- Buka Android Studio
- Pilih **"Open"**
- Navigasi ke folder project
- Tunggu Gradle sync selesai

### 3. Sync Dependencies

Android Studio akan otomatis sync dependencies. Jika tidak:
- Klik **"Sync Now"** di banner atas
- Atau: **File → Sync Project with Gradle Files**

### 4. Setup Device

#### Menggunakan Emulator:
1. Buat emulator baru (AVD Manager)
2. Pilih device dengan **Google Play**
3. Aktifkan lokasi di emulator:
   - Settings → Location → On
   - Extended Controls (⋮) → Location → Set koordinat

#### Menggunakan Device Fisik:
1. Aktifkan **Developer Options**
2. Aktifkan **USB Debugging**
3. Hubungkan ke komputer via USB
4. Aktifkan **GPS/Location**
5. Aktifkan **Internet** (untuk Geocoding)

### 5. Run Application

```
Shift + F10 (Windows/Linux)
Ctrl + R (Mac)
```

Atau klik tombol **Run ▶️** di toolbar

## 📱 Cara Menggunakan

1. **Pertama Kali Buka**
   - Aplikasi akan meminta izin lokasi
   - Pilih **"Allow"** atau **"While using the app"**

2. **Melihat Lokasi**
   - Aplikasi otomatis mengambil lokasi setelah izin diberikan
   - Menampilkan Latitude, Longitude, dan Alamat

3. **Refresh Lokasi**
   - Tekan tombol **"Refresh / Minta Ulang Izin Lokasi"**
   - Berguna jika lokasi berubah atau ingin update

4. **Jika Izin Ditolak**
   - Aplikasi menampilkan pesan error
   - Tekan tombol refresh untuk meminta izin ulang

## 📁 Struktur Project

```
app/
├── src/
│   └── main/
│       ├── java/com/example/praktikumlocationservices/
│       │   ├── MainActivity.kt          # UI dan Permission handling
│       │   └── LocationViewModel.kt     # Business logic & State
│       ├── res/
│       │   └── values/
│       │       ├── strings.xml
│       │       └── themes.xml
│       └── AndroidManifest.xml          # Permissions
└── build.gradle.kts                     # Dependencies
```

## 💡 Penjelasan Kode

### 1. LocationViewModel.kt

#### Data Class - LocationData
```kotlin
data class LocationData(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "Menunggu lokasi..."
)
```
Menyimpan informasi lokasi dalam satu objek.

#### Sealed Class - LocationState
```kotlin
sealed class LocationState {
    object Idle : LocationState()        // Kondisi awal
    object Loading : LocationState()     // Sedang mengambil lokasi
    data class Success(...)              // Berhasil dapat lokasi
    data class Error(...)                // Terjadi error
}
```
Merepresentasikan berbagai kondisi UI.

#### Key Functions

**startLocationFetch()**
- Memulai proses pengambilan lokasi
- Mengubah state menjadi Loading

**fetchLastLocation()**
- Mengambil lokasi terakhir dari Fused Location Provider
- Handle success dan failure

**getAddressFromCoordinates()**
- Suspend function untuk Reverse Geocoding
- Menggunakan Geocoder API
- Dijalankan di IO Dispatcher (background thread)

### 2. MainActivity.kt

#### Permission Handling
```kotlin
val locationPermissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted -> ... }
```
Menangani request permission secara modern dengan Activity Result API.

#### LaunchedEffect
```kotlin
LaunchedEffect(Unit) {
    // Cek apakah izin sudah diberikan
    // Jika belum, minta izin
}
```
Otomatis cek dan minta izin saat aplikasi pertama kali dibuka.

#### UI State Management
```kotlin
when (state) {
    is LocationState.Idle -> // Tampilkan "Menunggu izin..."
    is LocationState.Loading -> // Tampilkan loading indicator
    is LocationState.Success -> // Tampilkan koordinat & alamat
    is LocationState.Error -> // Tampilkan pesan error
}
```

### 3. AndroidManifest.xml

```xml
<!-- Izin lokasi perkiraan (jaringan) -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Izin lokasi tepat (GPS) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<!-- Izin internet untuk Geocoding -->
<uses-permission android:name="android.permission.INTERNET" />
```
