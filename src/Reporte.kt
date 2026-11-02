// Reporte de cierre del día
object Reporte {

    fun generar(pedidos: List<Pedido>, inventario: Inventario) {
        println("\n========== REPORTE DE PEDIDOS DEL DÍA ==========")
        if (pedidos.isEmpty()) {
            println("No se registraron pedidos hoy.")
        } else {
            detallePorPedido(pedidos)
            totalesDelDia(pedidos)
            agruparPorEstado(pedidos)
        }
        stockRestante(inventario)
        println("================================================")
    }

    private fun detallePorPedido(pedidos: List<Pedido>) {
        for ((i, pedido) in pedidos.withIndex()) {
            println("Pedido #${i + 1}")
            println("  Cliente: ${pedido.cliente.nombre}")
            println("  Pizzas:  ${pedido.detallePizzas}")
            println("  Total:   ${pedido.total.comoMonto()}")
            println("  Estado:  ${pedido.estado.texto}")
            println("  Notas:   ${pedido.notasTexto}")
            println("------------------------------------------------")
        }
    }

    private fun totalesDelDia(pedidos: List<Pedido>) {
        println("Subtotal ventas:  ${pedidos.sumOf { it.subtotal }.comoMonto()}")
        println("Descuentos:      -${pedidos.sumOf { it.montoDescuento }.comoMonto()}")
        println("Valor neto:       ${pedidos.sumOf { it.totalNeto }.comoMonto()}")
        println("IVA (${Pedido.TASA_IVA}%):        ${pedidos.sumOf { it.iva }.comoMonto()}")
        println("Total vendido:    ${pedidos.sumOf { it.total }.comoMonto()}")
    }

    private fun agruparPorEstado(pedidos: List<Pedido>) {
        val completados = pedidos.filter { it.estado.esCompletado }
        val pendientes = pedidos.filter { !it.estado.esCompletado }

        println("\n--- Pedidos completados (${completados.size}) ---")
        completados.forEach { println("- ${it.cliente.nombre}: ${it.detallePizzas} [${it.estado.texto}] ${it.total.comoMonto()}") }

        println("\n--- Pedidos pendientes (${pendientes.size}) ---")
        pendientes.forEach { println("- ${it.cliente.nombre}: ${it.detallePizzas} [${it.estado.texto}] ${it.total.comoMonto()}") }
    }

    private fun stockRestante(inventario: Inventario) {
        println("\n--- Stock restante ---")
        for (s in inventario.estadoStock()) {
            val unidad = inventario.unidadDe(s.ingrediente)
            val alerta = if (s.bajo) "  ¡STOCK BAJO!" else ""
            println("- ${s.ingrediente}: ${s.disponible} de ${s.inicial} $unidad$alerta")
        }
    }
}
