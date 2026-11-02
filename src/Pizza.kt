data class Pizza(
    val nombre: String,
    val precio: Int,
    val receta: Map<String, Int>
) {
    init {
        require(nombre.isNotBlank()) { "La pizza debe tener un nombre." }
        require(precio > 0) { "Costo no válido para '$nombre': $precio. El precio debe ser mayor a 0." }
        require(receta.values.all { it > 0 }) { "La receta de '$nombre' tiene cantidades de ingredientes no válidas (deben ser mayores a 0)." }
    }
}
