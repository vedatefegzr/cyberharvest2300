package com.example.cyberharvest2300.ui.assets

import com.example.cyberharvest2300.R
import com.example.cyberharvest2300.data.game.GameIds

/**
 * Her eşya id'sini bir drawable kaynağına bağlar.
 *
 * Şu an tüm görseller res/drawable altında YER TUTUCU (placeholder) vektörlerdir.
 * Gerçek görseli eklemek için: aynı isimde bir PNG/WebP/XML dosyasını
 * res/drawable altına koyup üzerine yazman yeterli — kod tarafında
 * HİÇBİR ŞEY değiştirmene gerek yok.
 *
 * Örn: cyber_rat_meat.xml yerine cyber_rat_meat.png koyabilirsin,
 * ikisi de aynı kaynak adını (R.drawable.cyber_rat_meat) paylaşır.
 */
object ItemAssetMap {

    private val assets = mapOf(
        // NEON FIELDS
        GameIds.Items.CYBER_RAT_MEAT to R.drawable.cyber_rat_meat,
        GameIds.Items.SCRAP to R.drawable.scrap,
        GameIds.Items.DAMAGED_CIRCUIT to R.drawable.damaged_circuit,

        // RUSTLANDS
        GameIds.Items.WOLF_MEAT to R.drawable.wolf_meat,
        GameIds.Items.SCRAP_FANG to R.drawable.scrap_fang,
        GameIds.Items.WOLF_PELT to R.drawable.wolf_pelt,
        GameIds.Items.BOAR_MEAT to R.drawable.boar_meat,
        GameIds.Items.IRON_PLATE to R.drawable.iron_plate,
        GameIds.Items.RUST_CORE to R.drawable.rust_core,
        GameIds.Items.RAVEN_MEAT to R.drawable.raven_meat,
        GameIds.Items.RAVEN_FEATHER to R.drawable.raven_feather,
        GameIds.Items.BOAR_TUSK to R.drawable.boar_tusk,

        // TOXIC WASTES
        GameIds.Items.TOXIC_MEAT to R.drawable.toxic_meat,
        GameIds.Items.TOXIC_GLAND to R.drawable.toxic_gland,
        GameIds.Items.TOXIC_CORE to R.drawable.toxic_core,
        GameIds.Items.MUTANT_HIDE to R.drawable.mutant_hide,
        GameIds.Items.ACID_FANG to R.drawable.acid_fang,
        GameIds.Items.PLASMA_CELL to R.drawable.plasma_cell,
        GameIds.Items.CORRUPTED_CIRCUIT to R.drawable.corrupted_circuit,

        // IRON WASTES
        GameIds.Items.STEEL_PLATE to R.drawable.steel_plate,
        GameIds.Items.MAGNETIC_CORE to R.drawable.magnetic_core,
        GameIds.Items.MACHINE_HEART to R.drawable.machine_heart,
        GameIds.Items.TITANIUM_SHARD to R.drawable.titanium_shard,
        GameIds.Items.ENERGY_CORE to R.drawable.energy_core,

        // FOOD
        GameIds.Items.BASIC_MEAT_STEW to R.drawable.basic_meat_stew,
        GameIds.Items.CYBER_RAT_BURGER to R.drawable.cyber_rat_burger,
        GameIds.Items.WOLF_MEAT_SOUP to R.drawable.wolf_meat_soup,
        GameIds.Items.RUST_BOAR_STEAK to R.drawable.rust_boar_steak,
        GameIds.Items.TOXIC_HOUND_ROAST to R.drawable.toxic_hound_roast,
        GameIds.Items.MUTANT_STAG_STEAK to R.drawable.mutant_stag_steak,
        GameIds.Items.IRON_TITAN_FEAST to R.drawable.iron_titan_feast
    )

    /** Bulunamazsa null döner; çağıran taraf kendi placeholder'ını kullanır. */
    fun getAsset(itemId: String): Int? {
        return assets[itemId]
    }
}
