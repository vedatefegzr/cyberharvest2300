package com.example.cyberharvest2300.ui.bestiary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cyberharvest2300.data.game.Creature
import com.example.cyberharvest2300.data.game.ItemData
import com.example.cyberharvest2300.data.local.entity.PlayerCreatureProgress
import com.example.cyberharvest2300.databinding.ItemCreatureCardBinding
import com.example.cyberharvest2300.ui.assets.CreatureAssetMap

data class CreatureCardUiModel(
    val creature: Creature,
    val progress: PlayerCreatureProgress?
)

class CreatureCardAdapter : ListAdapter<CreatureCardUiModel, CreatureCardAdapter.CreatureViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreatureViewHolder {
        val binding = ItemCreatureCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CreatureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CreatureViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CreatureViewHolder(
        private val binding: ItemCreatureCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: CreatureCardUiModel) {
            val context = binding.root.context
            val creature = model.creature
            val encounterCount = model.progress?.encounterCount ?: 0
            val killCount = model.progress?.killCount ?: 0

            val isDiscovered = encounterCount > 0
            val isKilled = killCount > 0

            // Görsel: keşfedilmemişse "?" overlay göster
            val iconRes = CreatureAssetMap.getAsset(creature.id)
            if (iconRes != null) {
                binding.ivCreature.setImageResource(iconRes)
            } else {
                binding.ivCreature.setImageDrawable(null)
            }
            binding.tvUnknownOverlay.visibility = if (isDiscovered) View.GONE else View.VISIBLE

            binding.tvCreatureName.text = if (isDiscovered) creature.name else "???"

            binding.tvUndiscovered.visibility = if (!isDiscovered) View.VISIBLE else View.GONE
            binding.tvBasicStats.visibility = if (isDiscovered) View.VISIBLE else View.GONE
            binding.tvCombatLocked.visibility = if (isDiscovered && !isKilled) View.VISIBLE else View.GONE
            binding.tvCombatStats.visibility = if (isDiscovered && isKilled) View.VISIBLE else View.GONE
            binding.tvLootLocked.visibility = if (isDiscovered && isKilled && killCount < 3) View.VISIBLE else View.GONE
            binding.tvLootTitle.visibility = if (isDiscovered && killCount >= 3) View.VISIBLE else View.GONE
            binding.tvLootList.visibility = if (isDiscovered && killCount >= 3) View.VISIBLE else View.GONE

            if (!isDiscovered) return

            binding.tvBasicStats.text = context.getString(
                com.example.cyberharvest2300.R.string.bestiary_stat_line,
                encounterCount,
                creature.regionId,
                killCount
            )

            if (!isKilled) return

            binding.tvCombatStats.text = context.getString(
                com.example.cyberharvest2300.R.string.bestiary_combat_line,
                creature.hp,
                creature.attack,
                creature.weakness?.name ?: context.getString(com.example.cyberharvest2300.R.string.bestiary_none),
                creature.resistance?.name ?: context.getString(com.example.cyberharvest2300.R.string.bestiary_none)
            )

            if (killCount < 3) {
                binding.tvLootLocked.text = context.getString(
                    com.example.cyberharvest2300.R.string.bestiary_loot_locked,
                    3 - killCount
                )
                return
            }

            binding.tvLootList.text = creature.lootTable.joinToString("\n") { drop ->
                val itemName = ItemData.getById(drop.itemId)?.name ?: drop.itemId
                val chance = drop.dropChance * 100f
                val chanceText = if (chance % 1f == 0f) {
                    chance.toInt().toString()
                } else {
                    String.format("%.1f", chance)
                }
                "$itemName — $chanceText%"
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CreatureCardUiModel>() {
            override fun areItemsTheSame(oldItem: CreatureCardUiModel, newItem: CreatureCardUiModel): Boolean =
                oldItem.creature.id == newItem.creature.id

            override fun areContentsTheSame(oldItem: CreatureCardUiModel, newItem: CreatureCardUiModel): Boolean =
                oldItem == newItem
        }
    }
}
