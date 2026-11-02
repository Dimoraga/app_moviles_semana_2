class Pedido(
    val cliente: Cliente,
    itemsIngresados: List<ItemPedido>,
    val repartidor: Repartidor,
    val notas: String? = null,                  // observaciones opcionales del cliente
    val correoNotificaciones: String? = null    // solo si el cliente aceptó recibir notificaciones
) {
    // Si se eligió la misma pizza varias veces, se juntan en una sola línea
    val items: List<ItemPedido> = itemsIngresados
        .groupBy { it.pizza }
        .map { (pizza, grupo) -> ItemPedido(pizza, grupo.sumOf { it.cantidad }) }

    init {
        require(items.isNotEmpty()) { "El pedido debe incluir al menos una pizza." }
    }

    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION
        private set

    // Avanza al siguiente estado (solo hacia adelante). Devuelve el nuevo estado, o null si ya era el último.
    fun avanzarEstado(): EstadoPedido? {
        val siguiente = estado.siguiente() ?: return null
        estado = siguiente
        return siguiente
    }

    // Si no hay notas (null) o están en blanco, se muestra "sin notas"
    val notasTexto: String get() = notas?.takeIf { it.isNotBlank() } ?: "sin notas"

    // Total de pizzas del pedido, sumando todos los ítems
    val cantidadTotal: Int get() = items.sumOf { it.cantidad }

    // Ej: "2 x Margarita, 1 x Pepperoni"
    val detallePizzas: String get() = items.joinToString(", ") { "${it.cantidad} x ${it.pizza.nombre}" }

    // El descuento se calcula según el total de pizzas del pedido
    val descuentoPorcentaje: Int = if (cantidadTotal > 3) 10 else 0
    val subtotal: Int get() = items.sumOf { it.subtotal }

    // Valor después del descuento y antes del IVA
    val totalNeto: Int get() = subtotal * (100 - descuentoPorcentaje) / 100

    val montoDescuento: Int get() = subtotal - totalNeto

    // IVA como recargo del 19% sobre el valor total (redondeado al entero más cercano)
    val iva: Int get() = (totalNeto * TASA_IVA + 50) / 100

    // Monto final a pagar, con IVA incluido
    val total: Int get() = totalNeto + iva

    companion object {
        const val TASA_IVA = 19
    }

    fun resumen(): String {
        val descuento = if (descuentoPorcentaje > 0) "$descuentoPorcentaje% (-${montoDescuento.comoMonto()})" else "Sin descuento"
        return """
            |Nombre cliente: ${cliente.nombre}
            |Teléfono cliente: ${cliente.telefono}
            |Correo cliente: ${cliente.correo}
            |Dirección cliente: ${cliente.direccion}
            |Pizzas: $detallePizzas
            |Cantidad total: $cantidadTotal
            |Subtotal: ${subtotal.comoMonto()}
            |Descuento: $descuento
            |Valor neto: ${totalNeto.comoMonto()}
            |IVA ($TASA_IVA%): ${iva.comoMonto()}
            |Notas: $notasTexto
            |Repartidor: ${repartidor.nombre}
            |Estado: ${estado.texto}
            |Total a pagar: ${total.comoMonto()}
        """.trimMargin()
    }
}
