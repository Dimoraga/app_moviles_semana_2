enum class EstadoPedido(val texto: String) {
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    EN_CAMINO("En camino");

    // Completado = ya salió de cocina (listo o en camino). Pendiente = en preparación.
    val esCompletado: Boolean get() = this != EN_PREPARACION

    // Los estados solo avanzan en orden; null si ya es el último
    fun siguiente(): EstadoPedido? = entries.getOrNull(ordinal + 1)
}
