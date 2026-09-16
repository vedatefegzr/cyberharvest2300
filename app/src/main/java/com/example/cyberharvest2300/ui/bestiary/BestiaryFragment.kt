package com.example.cyberharvest2300.ui.bestiary

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.cyberharvest2300.data.game.Creature
import com.example.cyberharvest2300.data.game.ItemData
import com.example.cyberharvest2300.data.local.AppDatabase
import com.example.cyberharvest2300.data.repository.CreatureProgressRepository
import com.example.cyberharvest2300.databinding.FragmentBestiaryBinding
import com.example.cyberharvest2300.viewmodel.BestiaryViewModel
import com.example.cyberharvest2300.viewmodel.BestiaryViewModelFactory
import kotlinx.coroutines.launch

class BestiaryFragment : Fragment() {

    private var _binding: FragmentBestiaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BestiaryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentBestiaryBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        setupViewModel()
        observeBestiary()

        viewModel.loadBestiary()
    }

    private fun setupViewModel() {

        val database =
            AppDatabase.getDatabase(
                requireContext()
            )

        val repository =
            CreatureProgressRepository(
                database.playerCreatureProgressDao()
            )

        val factory =
            BestiaryViewModelFactory(
                repository
            )

        viewModel =
            ViewModelProvider(
                this,
                factory
            )[BestiaryViewModel::class.java]
    }

    private fun observeBestiary() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.creatures.collect {
                    renderBestiary()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.progress.collect {
                    renderBestiary()
                }
            }
        }
    }

    private fun renderBestiary() {

        if (_binding == null) {
            return
        }

        binding.creatureContainer.removeAllViews()

        viewModel.creatures.value.forEach { creature ->

            val progress =
                viewModel.getProgress(
                    creature.id
                )

            val encounterCount =
                progress?.encounterCount ?: 0

            val killCount =
                progress?.killCount ?: 0

            val creatureCard =
                LinearLayout(
                    requireContext()
                )

            creatureCard.orientation =
                LinearLayout.VERTICAL

            creatureCard.setPadding(
                24,
                24,
                24,
                24
            )

            /*
             * CREATURE NAME
             */
            val title =
                TextView(
                    requireContext()
                )

            title.text =
                if (encounterCount == 0) {
                    "???"
                } else {
                    creature.name
                }

            title.textSize = 21f

            title.setTextColor(
                Color.WHITE
            )

            title.setTypeface(
                null,
                Typeface.BOLD
            )

            title.setPadding(
                0,
                0,
                0,
                12
            )

            creatureCard.addView(
                title
            )

            /*
             * UNKNOWN CREATURE
             */
            if (encounterCount == 0) {

                val unknown =
                    TextView(
                        requireContext()
                    )

                unknown.text =
                    "UNKNOWN CREATURE\n" +
                            "Encounter this creature to discover it."

                unknown.textSize = 15f

                unknown.setTextColor(
                    Color.LTGRAY
                )

                creatureCard.addView(
                    unknown
                )

            } else {

                /*
                 * ENCOUNTERS
                 */
                creatureCard.addView(
                    createInfoText(
                        "Encounters: $encounterCount"
                    )
                )

                /*
                 * REGION
                 */
                creatureCard.addView(
                    createInfoText(
                        "Region: ${creature.regionId}"
                    )
                )

                /*
                 * KILLS
                 */
                creatureCard.addView(
                    createInfoText(
                        "Kills: $killCount"
                    )
                )

                /*
                 * STATS ARE REVEALED AFTER FIRST KILL
                 */
                if (killCount == 0) {

                    val locked =
                        TextView(
                            requireContext()
                        )

                    locked.text =
                        "\nKill this creature to reveal its combat data."

                    locked.textSize = 15f

                    locked.setTextColor(
                        Color.LTGRAY
                    )

                    creatureCard.addView(
                        locked
                    )

                } else {

                    /*
                     * HP
                     */
                    creatureCard.addView(
                        createInfoText(
                            "HP: ${creature.hp}"
                        )
                    )

                    /*
                     * ATTACK
                     */
                    creatureCard.addView(
                        createInfoText(
                            "Attack: ${creature.attack}"
                        )
                    )

                    /*
                     * WEAKNESS
                     */
                    creatureCard.addView(
                        createInfoText(
                            "Weakness: ${
                                creature.weakness?.name
                                    ?: "None"
                            }"
                        )
                    )

                    /*
                     * RESISTANCE
                     */
                    creatureCard.addView(
                        createInfoText(
                            "Resistance: ${
                                creature.resistance?.name
                                    ?: "None"
                            }"
                        )
                    )

                    /*
                     * LOOT UNLOCKS AT 3 KILLS
                     */
                    if (killCount >= 3) {

                        val lootTitle =
                            TextView(
                                requireContext()
                            )

                        lootTitle.text =
                            "\nLOOT"

                        lootTitle.textSize =
                            17f

                        lootTitle.setTextColor(
                            Color.WHITE
                        )

                        lootTitle.setTypeface(
                            null,
                            Typeface.BOLD
                        )

                        creatureCard.addView(
                            lootTitle
                        )

                        creatureCard.addView(
                            createLootView(
                                creature
                            )
                        )

                    } else {

                        val lootLocked =
                            TextView(
                                requireContext()
                            )

                        lootLocked.text =
                            "\nLoot data locked.\n" +
                                    "Kill this creature ${
                                        3 - killCount
                                    } more time(s)."

                        lootLocked.textSize =
                            14f

                        lootLocked.setTextColor(
                            Color.LTGRAY
                        )

                        creatureCard.addView(
                            lootLocked
                        )
                    }
                }
            }

            /*
             * DIVIDER
             */
            val divider =
                View(
                    requireContext()
                )

            divider.setBackgroundColor(
                Color.DKGRAY
            )

            divider.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
                ).apply {
                    topMargin = 24
                    bottomMargin = 24
                }

            creatureCard.addView(
                divider
            )

            binding.creatureContainer.addView(
                creatureCard
            )
        }
    }

    private fun createInfoText(
        text: String
    ): TextView {

        return TextView(
            requireContext()
        ).apply {

            this.text =
                text

            textSize =
                15f

            setTextColor(
                Color.LTGRAY
            )

            setPadding(
                0,
                4,
                0,
                4
            )
        }
    }

    private fun createLootView(
        creature: Creature
    ): TextView {

        val builder =
            StringBuilder()

        creature.lootTable.forEach { drop ->

            val itemName =
                ItemData
                    .getById(
                        drop.itemId
                    )
                    ?.name
                    ?: drop.itemId

            val chance =
                drop.dropChance * 100f

            builder.append(
                "$itemName — " +
                        "${formatChance(chance)}%\n"
            )
        }

        return TextView(
            requireContext()
        ).apply {

            text =
                builder.toString()

            textSize =
                14f

            setTextColor(
                Color.LTGRAY
            )

            setPadding(
                0,
                8,
                0,
                0
            )
        }
    }

    private fun formatChance(
        value: Float
    ): String {

        return if (
            value % 1f == 0f
        ) {

            value.toInt().toString()

        } else {

            String.format(
                "%.1f",
                value
            )
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}