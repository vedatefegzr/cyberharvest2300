package com.example.cyberharvest2300.ui.restaurant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cyberharvest2300.data.game.RestaurantOrder
import com.example.cyberharvest2300.databinding.ItemOrderCardBinding

/*
 * =========================================================
 * ORDER CARD UI MODEL
 * =========================================================
 * order: DB'deki ham sipariş.
 * customerName / recipeName: RestaurantFragment tarafında
 * RestaurantData/RecipeData'dan çözülüp buraya hazır olarak
 * verilir, adapter kendi başına repository'ye bakmaz.
 */
data class OrderCardUiModel(
    val order: RestaurantOrder,
    val customerName: String,
    val recipeName: String,
    val isServable: Boolean
)

class OrderAdapter(
    private val onServe: (order: RestaurantOrder) -> Unit
) : ListAdapter<OrderCardUiModel, OrderAdapter.OrderViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {

        val binding =
            ItemOrderCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OrderViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: OrderCardUiModel) {

            binding.tvOrderCustomer.text =
                model.customerName

            binding.tvOrderWants.text =
                "İstiyor: ${model.recipeName}"

            binding.tvOrderRewards.text =
                "Ödül: ${model.order.reward} ₡    " +
                        "İtibar: +${model.order.reputationReward}"

            binding.btnServeOrder.isEnabled =
                model.isServable

            binding.btnServeOrder.setOnClickListener {
                onServe(model.order)
            }
        }
    }

    companion object {

        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<OrderCardUiModel>() {

                override fun areItemsTheSame(
                    oldItem: OrderCardUiModel,
                    newItem: OrderCardUiModel
                ) = oldItem.order.id == newItem.order.id

                override fun areContentsTheSame(
                    oldItem: OrderCardUiModel,
                    newItem: OrderCardUiModel
                ) = oldItem == newItem
            }
    }
}
