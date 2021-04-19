package com.example.readalquran.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.readalquran.custom.BaseBottomSheetDialogFragmentBinding
import com.example.readalquran.databinding.DialogGotoAyatBinding

class GotoAyatDialog(val gotoAyatDialogListener: GotoAyatDialogListener) :
    BaseBottomSheetDialogFragmentBinding<DialogGotoAyatBinding>() {

    val TAG = "GotoAyatDialog"

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DialogGotoAyatBinding
        get() = DialogGotoAyatBinding::inflate

    override fun setupView(binding: DialogGotoAyatBinding) {
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            attributes.windowAnimations = android.R.style.Animation_Translucent
        }
        binding.apply {
            editTextNumberSigned.addTextChangedListener { editable ->
                if (!editable.isNullOrBlank()) {
                    editTextNumberSigned.setError(null)
                }
            }
            button.setOnClickListener {
                if (TextUtils.isEmpty(editTextNumberSigned.text.toString())) {
                    editTextNumberSigned.setError("error field required")
                    editTextNumberSigned.requestFocus()
                } else {
                    gotoAyatDialogListener.gotoAyat(Integer.parseInt(editTextNumberSigned.text.toString()))
                    dismiss()
                }
            }
        }
    }

    interface GotoAyatDialogListener {
        fun gotoAyat(numberAyat: Int)
    }

}