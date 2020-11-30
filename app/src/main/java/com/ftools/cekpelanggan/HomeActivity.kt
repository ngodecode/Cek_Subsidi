package com.ftools.cekpelanggan

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ftools.cekpelanggan.ui.main.SectionsPagerAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class HomeActivity : AppCompatActivity() {

    companion object {
        val adsRewads = MutableLiveData<Boolean>()
    }

    private lateinit var mInterstitialAd: InterstitialAd
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter

        MobileAds.initialize(this)

        adsRewads?.observe(this, Observer {
            if (it) {
                showRewardAds()
            }
        })

        showDialogTerm()
    }

    override fun onResume() {
        super.onResume()
    }

    var rewardedAd: RewardedAd? = null
    fun showRewardAds() {
        if (rewardedAd == null) {
            rewardedAd = RewardedAd(this, resources.getString(R.string.ads_unit_banner_reward))
        }
        val adLoadCallback = object: RewardedAdLoadCallback() {
            override fun onRewardedAdFailedToLoad(p0: Int) {
                super.onRewardedAdFailedToLoad(p0)
            }

            override fun onRewardedAdLoaded() {
                super.onRewardedAdLoaded()
                val adCallback = object: RewardedAdCallback() {
                    override fun onRewardedAdOpened() {
                    }
                    override fun onRewardedAdClosed() {
                    }
                    override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                        Toast.makeText(this@HomeActivity, "Terimakasih sudah menonton", Toast.LENGTH_SHORT);
                    }
                    override fun onRewardedAdFailedToShow(p0: Int) {
                    }
                }
                rewardedAd?.show(this@HomeActivity, null)
            }
        }
        rewardedAd?.loadAd(AdRequest.Builder().build(), adLoadCallback)
    }

    private fun showDialogTerm() {
        if (this.getSharedPreferences("SYS", Context.MODE_PRIVATE).getBoolean("TERM_AGREE", false)) {
            return
        }

        var builder = AlertDialog.Builder(this)
        builder.setOnCancelListener {
            finish()
        }
        var view = LayoutInflater.from(this).inflate(R.layout.term_of_service, null)
        view.findViewById<TextView>(R.id.txtTerm).text = Html.fromHtml(getTerms().replace("\n", ""))
        view.findViewById<CheckBox>(R.id.chkAgree).setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { btn, isChecked ->
            view.findViewById<Button>(R.id.btnNext).isEnabled = isChecked
        })
        builder.setView(view)
        var dialog = builder.create()
        view.findViewById<Button>(R.id.btnNext).setOnClickListener {
            this.getSharedPreferences("SYS", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("TERM_AGREE", true).commit()
            dialog.dismiss() }
        dialog.show()

    }

    fun getTerms() : String {
        return "<h2> <b> Persyaratan dan Ketentuan </b> </h2>\n" +
                "<p> Selamat datang di Cek Subsidi dan Stimulus PLN! </p>\n" +
                "<p> Syarat dan ketentuan ini menguraikan aturan dan ketentuan penggunaan aplikasi Cek dan Stimulus PLN, yang terletak di google play store </p>\n" +
                "<p> Dengan mengakses aplikasi ini, kami menganggap Anda menerima syarat dan ketentuan ini. Jangan terus menggunakan aplikasi Cek Subsidi dan Stimulus PLN jika Anda tidak setuju untuk mengikuti semua syarat dan ketentuan yang tercantum di halaman ini. </p>\n" +
                "<p> Terminologi berikut berlaku untuk Syarat dan Ketentuan, Pernyataan Privasi dan Pemberitahuan Sanggahan dan semua Perjanjian: \"Klien\", \"Anda\" dan \"Milik Anda\" mengacu pada Anda, orang yang menggunakan aplikasi ini dan mematuhi persyaratan Perusahaan dan kondisi. \"Perusahaan\", \"Diri Kami\", \"Kami\", \"Milik Kami\", dan \"Kami\", mengacu pada Perusahaan kami. \"Pihak\", \"Pihak\", atau \"Kami\", mengacu pada Klien dan diri kami sendiri. Semua istilah mengacu pada penawaran, penerimaan dan pertimbangan pembayaran yang diperlukan untuk melakukan proses bantuan kami kepada Klien dengan cara yang paling tepat untuk tujuan yang jelas untuk memenuhi kebutuhan Klien sehubungan dengan penyediaan layanan yang dinyatakan Perusahaan, sesuai dengan dan tunduk pada, hukum yang berlaku di Indonesia. Setiap penggunaan terminologi di atas atau kata lain dalam bentuk tunggal, jamak, huruf besar dan / atau dia, dianggap dapat dipertukarkan dan oleh karena itu merujuk pada yang sama.\n" +
                "\n" +
                "<h3><li><b>Paket Data Internet</b></i></h3>\n" +
                "<p> Kami menggunakan penggunaan paket data internet. Dengan mengakses Cek Subsidi dan Stimulus PLN, Anda setuju untuk memperbolehkan kami menggunakan paket data internet sesuai dengan Kebijakan Privasi Cek Subsidi dan Stimulus PLN. </p>\n" +
                "<p> Aplikasi ini membutuhkan koneksi internet untuk dapat mengolah isian pengguna dan menampilkan informasi yang dibutuhkan </p>\n" +
                "\n" +
                "<h3><li><b>Local Preference</b></i> </h3>\n" +
                "<p> Kami menggunakan penggunaan local preference. Dengan mengakses Cek Subsidi dan Stimulus PLN, Anda setuju untuk memperbolehkan kami menggunakan lokal preference sesuai dengan Kebijakan Privasi Cek Subsidi dan Stimulus PLN. </p>\n" +
                "<p> Lokal preference digunakan untuk mengingat input pengguna untuk setiap penggunaan layanan, dengan begitu pengguna tidak perlu repot untuk mengisi kembali saat akan digunakan pada sesi berikutnya. </p>\n" +
                "\n" +
                "<h3><li><b>License</b></i> </h3>\n" +
                "<p> Cek Subsidi dan Stimulus PLN dan / atau pemberi lisensinya memiliki hak kekayaan intelektual untuk semua materi di Cek Subsidi dan Stimulus PLN. Semua hak kekayaan intelektual dilindungi. Anda dapat mengakses ini dari Cek Subsidi dan Stimulus PLN untuk penggunaan pribadi Anda dengan tunduk pada batasan yang ditetapkan dalam syarat dan ketentuan ini. </p>\n" +
                "\n" +
                "<p> <b>Anda tidak diperbolehkan untuk:</b> </p>\n" +
                "<ul>\n" +
                "    <li> Publikasikan ulang materi dari Cek Subsidi dan Stimulus PLN </li>" +
                "    <li> Menjual, menyewakan atau mensublisensikan materi dari Cek Subsidi dan Stimulus PLN </li>\n" +
                "    <li> Mereproduksi, menggandakan, atau menyalin materi dari Cek Subsidi dan Stimulus PLN </li>\n" +
                "    <li> Mendistribusikan kembali konten dari Cek Subsidi dan Stimulus PLN </li>\n" +
                "</ul>\n" +
                "\n" +
                "<p> <b>Perjanjian ini akan dimulai pada tanggal Perjanjian ini.</b> </p>\n" +
                "\n" +
                "<p> Bagian dari aplikasi ini menawarkan kesempatan bagi pengguna untuk mendapatkan informasi subsidi dan token sesuai dengan informasi yang tertera pada situs resmi PLN. Kami tidak akan bertanggung jawab atas Komentar atau kewajiban, kerusakan atau biaya yang disebabkan dan / atau diderita sebagai akibat dari penggunaan dan / atau tampilan dari aplikasi ini. </p>\n" +
                "\n" +
                "<p> <b>Anda menjamin dan menyatakan bahwa:</b> </p>\n" +
                "\n" +
                "<ul>\n" +
                "    <li> Anda bersedia mengisi data pribadi termasuk Kartu Identitas dan / atau ID Pelanggan dan / atau KTP pada aplikasi kami, dan aplikasi berhak untuk menggunakan informasi tersebut sebagai syarat penggunaan layanan yang dibutuhkan </li>\n" +
                "    <li> Isian data pengguna tidak melanggar hak kekayaan intelektual apa pun, termasuk tanpa batasan hak cipta, paten, atau merek dagang pihak ketiga mana pun </li>\n" +
                "\t<li> Memperbolehkan akses penggunaan paket data internet untuk penggunaan aplikasi </li>\n" +
                "</ul>\n" +
                "\n" +
                "<p> <b>Dengan ini Anda memberi Cek Subsidi dan Stimulus PLN lisensi non-eksklusif untuk menggunakan, mereproduksi, mengedit, dan mengizinkan orang lain untuk menggunakan, mereproduksi, dan mengedit informasi yang anda masukan kedalam aplikasi dalam segala bentuk, format, atau media. </b></p>\n" +
                "\n"
    }
}