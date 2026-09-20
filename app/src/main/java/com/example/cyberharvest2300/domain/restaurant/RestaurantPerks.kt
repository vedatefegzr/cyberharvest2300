package com.example.cyberharvest2300.domain.restaurant

/*
 * =========================================================
 * RESTAURANT PERKS
 * =========================================================
 * Restoran seviye atladığında oyuncuya sunulan seçimlik
 * bonuslar. Bu, restaurantLevel'ı pasif bir sayı olmaktan
 * çıkarıp gerçek bir karar noktasına çevirir.
 *
 * Oyuncu her level-up'ta bu listeden BİR tanesini seçer;
 * seçtiği id PlayerProfile.restaurantPerkIds içine virgülle
 * eklenir. Aynı perk birden fazla kez seçilebilir (stack'lenir),
 * istersen RestaurantProgressionEngine.choosePerk() içinde
 * bunu tekil hale getirebilirsin.
 */
data class RestaurantPerk(
    val id: String,
    val name: String,
    val description: String,
    val extraCapacity: Int = 0,
    val priceDiscountPercent: Int = 0,
    val reputationBonusPercent: Int = 0
)

object RestaurantPerks {

    val all = listOf(

        RestaurantPerk(
            id = "PERK_CAPACITY",
            name = "Genişleyen Mutfak",
            description = "Aynı anda 1 fazla müşteriye bakabilirsin.",
            extraCapacity = 1
        ),

        RestaurantPerk(
            id = "PERK_SUPPLIER",
            name = "Toptancı Anlaşması",
            description = "Malzeme satın alma fiyatlarında %15 indirim.",
            priceDiscountPercent = 15
        ),

        RestaurantPerk(
            id = "PERK_REPUTATION",
            name = "Sadık Müşteriler",
            description = "Servis ettiğin siparişlerden %20 daha fazla itibar kazanırsın.",
            reputationBonusPercent = 20
        )
    )

    fun getById(
        id: String
    ): RestaurantPerk? {

        return all.find {
            it.id == id
        }
    }

    /*
     * Virgülle ayrılmış perk id string'ini (PlayerProfile.restaurantPerkIds)
     * gerçek RestaurantPerk listesine çevirir. Bilinmeyen/boş id'ler atlanır.
     */
    fun parse(
        perkIds: String
    ): List<RestaurantPerk> {

        if (perkIds.isBlank()) {
            return emptyList()
        }

        return perkIds
            .split(",")
            .mapNotNull { id ->
                getById(id.trim())
            }
    }
}
