data class ItemPedido(val pizza: Pizza, val cantidad: Int) {
    init {
        require(cantidad > 0) { "Cantidad no válida: $cantidad. Debe pedir al menos 1 pizza de '${pizza.nombre}'." }
    }

    val subtotal: Int get() = pizza.precio * cantidad
}
