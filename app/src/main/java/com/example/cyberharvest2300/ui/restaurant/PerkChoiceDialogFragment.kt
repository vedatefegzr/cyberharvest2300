package com.example.cyberharvest2300.ui.restaurant

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.databinding.FragmentPerkChoiceDialogBinding
import com.example.cyberharvest2300.domain.restaurant.RestaurantPerk
import com.example.cyberharvest2300.domain.restaurant.RestaurantPerks

/*
 * =========================================================
 * PERK SEÇİM DİYALOĞU
 * =========================================================
 * RestaurantFragment, viewModel.pendingPerkChoice true olduğunda
 * bunu gösterir. isCancelable = false: oyuncu bir seçim yapmadan
 * dialog'u kapatamaz (dışarı tıklayarak ya da geri tuşuyla).
 *
 * Parametresiz constructor kullanılır (ekran döndürme / process death
 * sonrası sistem fragment'i yeniden oluşturabilsin diye). Seçim,
 * Fragment Result API ile RestaurantFragment'a iletilir.
 */
class PerkChoiceDialogFragment : DialogFragment() {

    private val perks: List<RestaurantPerk>
        get() = RestaurantPerks.all

    private var _binding: FragmentPerkChoiceDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {

        _binding =
            FragmentPerkChoiceDialogBinding.inflate(
                LayoutInflater.from(requireContext())
            )

        buildPerkButtons()

        return android.app.AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun buildPerkButtons() {

        binding.perkOptionsContainer.removeAllViews()

        perks.forEach { perk ->

            val button = Button(requireContext())

            button.text =
                "${perk.name}\n${perk.description}"

            button.isAllCaps = false

            button.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.ch_text_primary
                )
            )

            val paddingPx =
                (12 * resources.displayMetrics.density).toInt()

            button.setPadding(
                paddingPx,
                paddingPx,
                paddingPx,
                paddingPx
            )

            val params =
                android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.topMargin =
                (8 * resources.displayMetrics.density).toInt()

            button.layoutParams = params

            button.setOnClickListener {

                parentFragmentManager.setFragmentResult(
                    RESULT_KEY,
                    bundleOf(KEY_PERK_ID to perk.id)
                )

                dismiss()
            }

            binding.perkOptionsContainer.addView(button)
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }

    override fun onCancel(dialog: android.content.DialogInterface) {

        super.onCancel(dialog)

        // isCancelable(false) sayesinde normalde buraya düşülmez;
        // ekstra bir güvenlik önlemi olarak bırakıldı.
    }

    init {
        isCancelable = false
    }

    companion object {
        const val RESULT_KEY = "perk_choice_result"
        const val KEY_PERK_ID = "perk_id"
    }
}
