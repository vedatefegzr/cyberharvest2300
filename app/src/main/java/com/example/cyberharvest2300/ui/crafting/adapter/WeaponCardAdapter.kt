package com.example.cyberharvest2300.ui.crafting.adapter

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.data.game.Ingredient
import com.example.cyberharvest2300.data.game.ItemData
import com.example.cyberharvest2300.data.game.UnlockCondition
import com.example.cyberharvest2300.data.game.Weapon
import com.example.cyberharvest2300.data.local.entity.PlayerWeaponProgress
import com.example.cyberharvest2300.databinding.ItemWeaponCardBinding
import com.example.cyberharvest2300.ui.assets.WeaponAssetMap

/**
 * Tek bir silah kartının ekranda göstermesi gereken her şeyi taşır.
 * Fragment/ViewModel tarafında weapons + weaponProgress + inventory
 * birleştirilip bu modele dönüştürülür, adapter sadece bunu render eder.
 */
data class WeaponCardUiModel(
    val weapon: Weapon,
    val progress: PlayerWeaponProgress?,
    val ownedQuantities: Map<String, Int>
)

class WeaponCardAdapter(
    private val onCraft: (weaponId: String) -> Unit,
    private val onUpgrade: (weaponId: String) -> Unit,
    private val onEquip: (weaponId: String) -> Unit
) : ListAdapter<WeaponCardUiModel, WeaponCardAdapter.WeaponViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeaponViewHolder {
        val binding = ItemWeaponCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeaponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeaponViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WeaponViewHolder(
        private val binding: ItemWeaponCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: WeaponCardUiModel) {
            val weapon = model.weapon
            val progress = model.progress

            binding.tvWeaponName.text = weapon.name

            val iconRes = WeaponAssetMap.getAsset(weapon.id)
            if (iconRes != null) {
                binding.ivWeaponIcon.setImageResource(iconRes)
            } else {
                binding.ivWeaponIcon.setImageDrawable(null)
            }

            if (progress == null || !progress.isOwned) {
                bindLocked(weapon, model)
            } else {
                bindOwned(weapon, progress, model)
            }
        }

        private fun bindLocked(weapon: Weapon, model: WeaponCardUiModel) {
            val context = binding.root.context

            setBadge(context, "LOCKED", R.color.ch_text_secondary)

            binding.tvSubtitle.text = context.getString(R.string.weapon_not_owned)
            binding.tvSubtitle.setTextColor(color(context, R.color.ch_text_secondary))

            if (weapon.unlockConditions.isNotEmpty()) {
                binding.tvRequirements.visibility = View.VISIBLE
                binding.tvRequirements.text = weapon.unlockConditions.joinToString("\n") {
                    describeCondition(context, it)
                }
            } else {
                binding.tvRequirements.visibility = View.GONE
            }

            binding.tvMaxLevel.visibility = View.GONE
            binding.btnSecondaryAction.visibility = View.GONE

            if (weapon.craftCost.isEmpty()) {
                binding.tvCostTitle.visibility = View.GONE
                binding.tvCostList.visibility = View.GONE
                binding.btnPrimaryAction.visibility = View.GONE
                return
            }

            binding.tvCostTitle.visibility = View.VISIBLE
            binding.tvCostTitle.text = context.getString(R.string.craft_cost_title)

            binding.tvCostList.visibility = View.VISIBLE
            binding.tvCostList.text = buildCostText(context, weapon.craftCost, model.ownedQuantities)

            val canCraft = weapon.craftCost.all { ingredient ->
                (model.ownedQuantities[ingredient.itemId] ?: 0) >= ingredient.quantity
            }

            binding.btnPrimaryAction.visibility = View.VISIBLE
            binding.btnPrimaryAction.text = context.getString(R.string.action_craft)
            binding.btnPrimaryAction.isEnabled = canCraft
            binding.btnPrimaryAction.setOnClickListener {
                // Eski koddaki gibi: sonuç dönene kadar çift tıklamayı engelle.
                // Sonraki state emisyonu geldiğinde buton yeniden hesaplanacak.
                it.isEnabled = false
                onCraft(weapon.id)
            }
        }

        private fun bindOwned(
            weapon: Weapon,
            progress: PlayerWeaponProgress,
            model: WeaponCardUiModel
        ) {
            val context = binding.root.context
            binding.tvRequirements.visibility = View.GONE

            val isMaxLevel = progress.level >= weapon.maxLevel

            setBadge(
                context,
                if (isMaxLevel) context.getString(R.string.badge_max) else context.getString(R.string.badge_owned),
                if (isMaxLevel) R.color.ch_accent_yellow else R.color.ch_accent_green
            )

            val currentAttack = weapon.baseAttack + (progress.level - 1) * 2
            binding.tvSubtitle.text = context.getString(
                R.string.weapon_level_attack,
                progress.level,
                currentAttack
            )
            binding.tvSubtitle.setTextColor(color(context, R.color.ch_accent_cyan))

            binding.btnPrimaryAction.visibility = View.VISIBLE
            binding.btnPrimaryAction.text = if (progress.isEquipped) {
                context.getString(R.string.action_equipped)
            } else {
                context.getString(R.string.action_equip)
            }
            binding.btnPrimaryAction.isEnabled = !progress.isEquipped
            binding.btnPrimaryAction.setOnClickListener {
                it.isEnabled = false
                onEquip(weapon.id)
            }

            if (isMaxLevel) {
                binding.tvMaxLevel.visibility = View.VISIBLE
                binding.tvCostTitle.visibility = View.GONE
                binding.tvCostList.visibility = View.GONE
                binding.btnSecondaryAction.visibility = View.GONE
                return
            }

            binding.tvMaxLevel.visibility = View.GONE

            val nextLevel = progress.level + 1
            val upgradeCost = weapon.upgradeCosts[nextLevel]

            if (upgradeCost == null) {
                binding.tvCostTitle.visibility = View.GONE
                binding.tvCostList.visibility = View.GONE
                binding.btnSecondaryAction.visibility = View.GONE
                return
            }

            binding.tvCostTitle.visibility = View.VISIBLE
            binding.tvCostTitle.text = context.getString(R.string.upgrade_cost_title)

            binding.tvCostList.visibility = View.VISIBLE
            binding.tvCostList.text = buildCostText(context, upgradeCost, model.ownedQuantities)

            val canUpgrade = upgradeCost.all { ingredient ->
                (model.ownedQuantities[ingredient.itemId] ?: 0) >= ingredient.quantity
            }

            binding.btnSecondaryAction.visibility = View.VISIBLE
            binding.btnSecondaryAction.text = context.getString(R.string.action_upgrade_to, nextLevel)
            binding.btnSecondaryAction.isEnabled = canUpgrade
            binding.btnSecondaryAction.setOnClickListener {
                it.isEnabled = false
                onUpgrade(weapon.id)
            }
        }

        private fun setBadge(context: Context, text: String, colorRes: Int) {
            binding.tvStatusBadge.text = text
            binding.tvStatusBadge.setTextColor(color(context, colorRes))
        }

        private fun buildCostText(
            context: Context,
            costs: List<Ingredient>,
            owned: Map<String, Int>
        ): SpannableStringBuilder {
            val builder = SpannableStringBuilder()

            costs.forEachIndexed { index, ingredient ->
                val item = ItemData.getById(ingredient.itemId)
                val current = owned[ingredient.itemId] ?: 0
                val sufficient = current >= ingredient.quantity

                val line = context.getString(
                    R.string.cost_line_format,
                    item?.name ?: ingredient.itemId,
                    current,
                    ingredient.quantity
                )

                val start = builder.length
                builder.append(line)
                builder.setSpan(
                    ForegroundColorSpan(
                        color(context, if (sufficient) R.color.ch_accent_green else R.color.ch_accent_red)
                    ),
                    start,
                    builder.length,
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                if (index != costs.lastIndex) {
                    builder.append("\n")
                }
            }

            return builder
        }

        private fun describeCondition(context: Context, condition: UnlockCondition): String = when (condition) {
            is UnlockCondition.MinReputation ->
                context.getString(R.string.req_reputation, condition.value)

            is UnlockCondition.MinMoney ->
                context.getString(R.string.req_money, condition.value)

            is UnlockCondition.MinRestaurantLevel ->
                context.getString(R.string.req_restaurant_level, condition.value)

            is UnlockCondition.CreatureKilled ->
                context.getString(R.string.req_kills, condition.times)

            is UnlockCondition.WeaponLevel ->
                context.getString(R.string.req_weapon_level, condition.level)
        }

        private fun color(context: Context, colorRes: Int): Int =
            ContextCompat.getColor(context, colorRes)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WeaponCardUiModel>() {
            override fun areItemsTheSame(oldItem: WeaponCardUiModel, newItem: WeaponCardUiModel): Boolean =
                oldItem.weapon.id == newItem.weapon.id

            override fun areContentsTheSame(oldItem: WeaponCardUiModel, newItem: WeaponCardUiModel): Boolean =
                oldItem == newItem
        }
    }
}
