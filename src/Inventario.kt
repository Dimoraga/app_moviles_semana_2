data class Faltante(val ingrediente: String, val necesario: Int, val disponible: Int) {
    val diferencia: Int get() = necesario - disponible
}

data class StockIngrediente(val ingrediente: String, val disponible: Int, val inicial: Int) {
    // Alerta cuando queda menos del 20% de lo que había al abrir
    val bajo: Boolean get() = disponible * 100 < inicial * UMBRAL_BAJO_PORCENTAJE

    companion object {
        const val UMBRAL_BAJO_PORCENTAJE = 20
    }
}

class Inventario(private val stock: MutableMap<String, Int>) {

    private val inicial: Map<String, Int> = stock.toMap()

    fun estadoStock(): List<StockIngrediente> =
        stock.map { (ingrediente, disponible) -> StockIngrediente(ingrediente, disponible, inicial.getValue(ingrediente)) }

    init {
        for ((ingrediente, cantidad) in stock) {
            require(cantidad >= 0) { "Inventario inválido: '$ingrediente' tiene cantidad negativa ($cantidad)." }
        }
    }

    private fun disponible(ingrediente: String): Int =
        stock[ingrediente] ?: throw IllegalStateException("El ingrediente '$ingrediente' no existe en el inventario.")

    // Suma los ingredientes de todos los ítems (una misma pizza o ingrediente puede repetirse)
    // y devuelve los que no alcanzan.
    fun faltantes(items: List<ItemPedido>): List<Faltante> {
        val necesarios = mutableMapOf<String, Int>()
        for (item in items) {
            for ((ingrediente, porPizza) in item.pizza.receta) {
                necesarios[ingrediente] = (necesarios[ingrediente] ?: 0) + porPizza * item.cantidad
            }
        }
        return necesarios.mapNotNull { (ingrediente, necesario) ->
            val disponible = disponible(ingrediente)
            if (disponible < necesario) Faltante(ingrediente, necesario, disponible) else null
        }
    }

    fun faltantes(pizza: Pizza, cantidad: Int): List<Faltante> = faltantes(listOf(ItemPedido(pizza, cantidad)))

    fun alcanza(pizza: Pizza, cantidad: Int): Boolean = faltantes(pizza, cantidad).isEmpty()

    fun hayStockParaAlguna(pizzas: List<Pizza>): Boolean = pizzas.any { alcanza(it, 1) }

    fun descontar(items: List<ItemPedido>) {
        require(items.isNotEmpty()) { "No hay pizzas para descontar del inventario." }

        // Se valida todo antes de modificar, para que el stock nunca quede negativo ni a medias.
        val faltantes = faltantes(items)
        if (faltantes.isNotEmpty()) {
            val detalle = faltantes.joinToString(", ") { "${it.ingrediente} (faltan ${it.diferencia} ${unidadDe(it.ingrediente)})" }
            throw IllegalStateException("Inventario insuficiente para el pedido: $detalle.")
        }

        for (item in items) {
            for ((ingrediente, porPizza) in item.pizza.receta) {
                stock[ingrediente] = disponible(ingrediente) - porPizza * item.cantidad
            }
        }
    }

    fun reabastecer(ingrediente: String, cantidad: Int) {
        require(cantidad > 0) { "No se puede reabastecer '$ingrediente' con una cantidad no positiva ($cantidad)." }
        stock[ingrediente] = disponible(ingrediente) + cantidad
    }

    fun unidadDe(ingrediente: String): String = when (ingrediente) {
        "Masa" -> "unidades"
        else -> "g"
    }

    override fun toString(): String = stock.toString()
}
