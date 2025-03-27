package com.hussain.spark_expo.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.hussain.spark_expo.R
import com.hussain.spark_expo.databinding.DialogInvoiceBinding
import com.hussain.spark_expo.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.invoiceBtn.setOnClickListener {
            showInvoiceDialog()
        }
    }

    private fun showInvoiceDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // View Binding
        val binding = DialogInvoiceBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


        binding.btnProceed.setOnClickListener {
            Toast.makeText(requireContext(), "Proceeding...", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        binding.btnPrint.setOnClickListener {
            Toast.makeText(requireContext(), "Printing Invoice...", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}