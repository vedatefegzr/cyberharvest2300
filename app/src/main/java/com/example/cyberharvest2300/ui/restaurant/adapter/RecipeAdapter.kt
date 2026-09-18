package com.example.cyberharvest2300.ui.restaurant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.data.game.ItemData
import com.example.cyberharvest2300.data.game.Recipe
import com.example.cyberharvest2300.databinding.ItemRecipeCardBinding
import com.example.cyberharvest2300.ui.assets.ItemAssetMap

data class RecipeCardUiModel(
    val recipe: Recipe,
    val isSelected: Boolean
)

class RecipeAdapter(
    private val onSelect: (recipeId: String) -> Unit
) : ListAdapter<RecipeCardUiModel, RecipeAdapter.RecipeViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeViewHolder(
        private val binding: ItemRecipeCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: RecipeCardUiModel) {
            val context = binding.root.context
            val recipe = model.recipe

            val iconRes = ItemAssetMap.getAsset(recipe.resultItemId)
            if (iconRes != null) {
                binding.ivRecipeIcon.setImageResource(iconRes)
            } else {
                binding.ivRecipeIcon.setImageDrawable(null)
            }

            binding.tvRecipeName.text = if (model.isSelected) {
                context.getString(R.string.recipe_selected_name, recipe.name)
            } else {
                recipe.name
            }
            binding.tvRecipeName.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (model.isSelected) R.color.ch_accent_cyan else R.color.ch_text_primary
                )
            )

            binding.tvIngredients.text = buildString {
                append(context.getString(R.string.recipe_ingredients_prefix))
                recipe.ingredients.forEachIndexed { index, ingredient ->
                    if (index > 0) append("   ")
                    val item = ItemData.getById(ingredient.itemId)
                    append("${item?.name ?: ingredient.itemId} x${ingredient.quantity}")
                }
            }

            binding.tvSellPrice.text = context.getString(R.string.recipe_sell_format, recipe.sellPrice)

            binding.root.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    if (model.isSelected) R.color.ch_selected_surface else R.color.ch_surface
                )
            )

            binding.btnSelectRecipe.text = if (model.isSelected) {
                context.getString(R.string.recipe_selected)
            } else {
                context.getString(R.string.recipe_select)
            }
            binding.btnSelectRecipe.isEnabled = !model.isSelected
            binding.btnSelectRecipe.setOnClickListener { onSelect(recipe.id) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecipeCardUiModel>() {
            override fun areItemsTheSame(oldItem: RecipeCardUiModel, newItem: RecipeCardUiModel) =
                oldItem.recipe.id == newItem.recipe.id

            override fun areContentsTheSame(oldItem: RecipeCardUiModel, newItem: RecipeCardUiModel) =
                oldItem == newItem
        }
    }
}
