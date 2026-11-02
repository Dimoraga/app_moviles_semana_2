data class Repartidor(val nombre: String, val comunas: List<String>) {
    fun atiende(comuna: String): Boolean = comunas.any { it.normalizado() == comuna.normalizado() }
}
