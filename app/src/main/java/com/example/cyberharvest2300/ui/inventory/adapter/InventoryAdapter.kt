package com.example.cyberharvest2300.ui.inventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.data.game.ItemData
import com.example.cyberharvest2300.data.local.entity.InventoryItem
import com.example.cyberharvest2300.databinding.ItemInventoryRowBinding
import com.example.cyberharvest2300.ui.assets.ItemAssetMap

class InventoryAdapter(
    private val onSell: (InventoryItem) -> Unit
) : ListAdapter<InventoryItem, InventoryAdapter.RowViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val binding = ItemInventoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RowViewHolder(
        private val binding: ItemInventoryRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InventoryItem) {
            val context = binding.root.context
            val itemData = ItemData.getById(item.itemId)

            val iconRes = ItemAssetMap.getAsset(item.itemId)
            if (iconRes != null) {
                binding.ivItemIcon.setImageResource(iconRes)
            } else {
                binding.ivItemIcon.setImageDrawable(null)
            }

            binding.tvItemName.text = context.getString(
                R.string.inventory_name_format,
                itemData?.name ?: item.itemId,
                item.quantity
            )

            binding.tvItemMeta.text = context.getString(
                R.string.inventory_meta_format,
                itemData?.type?.name ?: "UNKNOWN",
                itemData?.rarity?.name ?: "UNKNOWN"
            )

            binding.tvSecuredBadge.visibility = if (item.isSecured) View.VISIBLE else View.GONE

            if (item.isSecured) {
                binding.btnSell.visibility = View.GONE
            } else {
                binding.btnSell.visibility = View.VISIBLE
                binding.btnSell.text = context.getString(
                    R.string.inventory_sell_format,
                    itemData?.sellValue ?: 0
                )
                binding.btnSell.setOnClickListener { onSell(item) }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<InventoryItem>() {
            override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem) = oldItem == newItem
        }
    }
}
