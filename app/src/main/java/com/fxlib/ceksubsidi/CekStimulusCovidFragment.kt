package com.fxlib.ceksubsidi

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.cek_stimulus_covid_fragment.*


class CekStimulusCovidFragment : Fragment() {

    companion object {
        fun newInstance() = CekStimulusCovidFragment()
    }

    private lateinit var viewModel: CekStimulusCovidViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cek_stimulus_covid_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CekStimulusCovidViewModel::class.java)
        viewModel.initClient()

        btnCekIdPel.setOnClickListener {
            if (edtIdPel.text.toString().trim().isEmpty()) {
                Toast.makeText(activity!!, "Isi ID Pelanggan terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.getCapcha()
            try {
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
            } catch (e:Exception) {}
        }
        viewModel.captcha.observe(activity!!, Observer<Bitmap> {
                    imgCaptcha.setImageBitmap(it)
                    lyt_idPelanggan.visibility = View.GONE
                    lyt_captcha.visibility     = View.VISIBLE
                    btnClear.visibility = View.VISIBLE
            })

        viewModel.result.observe(activity!!, Observer<String> {
                txtResult.text = it
        })

        viewModel.success.observe(activity!!, Observer {
            if (it) {
                activity
                    ?.getSharedPreferences("SYS", Context.MODE_PRIVATE)
                    ?.edit()?.putBoolean("ADS_PERIODIC_ENABLED", true)?.commit()

                lyt_captcha.visibility = View.GONE
                btnClear.visibility = View.VISIBLE
            }
        })

        viewModel.dialog.observe(activity!!, Observer {
            if (it) {
                showDialog()
            }
            else {
                dismissDialog()
            }
        })

        btnCek.setOnClickListener {
            if (edtCaptcha.text.toString().trim().isEmpty()) {
                Toast.makeText(activity!!, "Isi KODE terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.getInfo(edtCaptcha.text.toString(), edtIdPel.text.toString())
            try {
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
            } catch (e:Exception) {}
        }

        btnClear.setOnClickListener {
            edtCaptcha.text.clear()
            txtResult.text = ""
            lyt_captcha.visibility = View.GONE
            lyt_idPelanggan.visibility = View.VISIBLE
            btnClear.visibility = View.GONE
            try {
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
            } catch (e:Exception) {}
        }
    }

    var mDialogLoading : Dialog? = null
    fun isDialogShowing() : Boolean {
        return mDialogLoading?.isShowing ?: false
    }

    private fun showDialog() {
        if (isDialogShowing()) {
            return
        }
        mDialogLoading = setProgressDialog(activity!!, "Memuat Informasi")
        mDialogLoading?.setCanceledOnTouchOutside(false)
        mDialogLoading?.setCancelable(true)
        mDialogLoading?.show()
    }

    fun setProgressDialog(context:Context, message:String): AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER_VERTICAL
        val tvText = TextView(context)
        tvText.text = message
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20.toFloat()
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)
        builder.setNegativeButton("BATAL", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dismissDialog()
            }
        })

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }
        return dialog
    }

    fun dismissDialog() {
        mDialogLoading?.dismiss()
    }

}
