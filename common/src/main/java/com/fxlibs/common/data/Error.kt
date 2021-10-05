package com.fxlibs.common.data

object Error {
    object ErrorData: Throwable("Gagal memuat, coba beberapa saat lagi")
    object ErrorConnection: Throwable("Kesalahan koneksi, pastikan Anda terhubung ke internet")
}

