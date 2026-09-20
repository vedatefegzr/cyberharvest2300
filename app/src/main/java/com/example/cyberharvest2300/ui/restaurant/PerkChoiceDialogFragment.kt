package com.example.cyberharvest2300.ui.restaurant

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.databinding.FragmentPerkChoiceDialogBinding
import com.example.cyberharvest2300.domain.restaurant.RestaurantPerk

/*
 * =========================================================
 * PERK SEÇİM DİYALOĞU
 * =========================================================
 * RestaurantFragment, viewModel.pendingPerkChoice true olduğunda
 * bunu gösterir. isCancelable = false: oyuncu bir seçim yapmadan
 * dialog'u kapatamaz (dışarı tıklayarak ya da geri tuşuyla).
 *
 * Perk listesi ve seçim callback'i dışarıdan (RestaurantFragment)
 * enjekte edilir, böylece bu dialog ViewModel'e doğrudan bağımlı
 * olmaz - test etmesi ve tekrar kullanması kolaylaşır.
 */
class PerkChoiceDialogFragment(
    private val perks: List<RestaurantPerk>,
    private val onPerkChosen: (perkId: String) -> Unit
) : DialogFragment() {

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

                onPerkChosen(perk.id)

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
}
