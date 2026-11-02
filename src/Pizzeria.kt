class Pizzeria {

    private val menu = listOf(
        Pizza("Margarita", 10000, mapOf("Masa" to 1, "Salsa de tomate" to 80, "Mozzarella" to 120, "Tomate" to 50)),
        Pizza("Pepperoni", 12000, mapOf("Masa" to 1, "Salsa de tomate" to 80, "Mozzarella" to 120, "Pepperoni" to 60)),
        Pizza("Cuatro Quesos", 15000, mapOf("Masa" to 1, "Mozzarella" to 100, "Gorgonzola" to 50, "Parmesano" to 40, "Provolone" to 50)),
        Pizza("Napolitana", 11000, mapOf("Masa" to 1, "Salsa de tomate" to 80, "Mozzarella" to 100, "Tomate" to 60, "Aceitunas" to 30))
    )

    private val repartidores = listOf(
        Repartidor("Marcelo Guerra", listOf("La Florida", "Puente Alto", "Macul", "Peñalolén", "La Granja", "San Ramón")),
        Repartidor("Gonzalo Espinosa", listOf("La Reina", "Providencia", "Las Condes", "Vitacura", "Lo Barnechea")),
        Repartidor("María José Valenzuela", listOf("Santiago", "Pedro Aguirre Cerda", "San Miguel", "Recoleta", "Independencia", "Huechuraba", "Estación Central")),
        Repartidor("Bryan Jiménez", listOf("Maipú", "Cerrillos", "Lo Prado", "Lo Espejo")),
        Repartidor("Pablo Soto", listOf("San Bernardo", "El Bosque", "La Cisterna", "La Pintana")),
        Repartidor("Ximena Salvatierra", listOf("Quilicura", "Pudahuel", "Quinta Normal", "Cerro Navia"))
    )

    private val inventario = Inventario(
        mutableMapOf(
            "Masa" to 20, "Salsa de tomate" to 2000, "Mozzarella" to 3000,
            "Pepperoni" to 600, "Tomate" to 1000, "Gorgonzola" to 500,
            "Parmesano" to 500, "Provolone" to 500, "Aceitunas" to 300
        )
    )

    // Todos los pedidos del día
    private val pedidosDelDia = mutableListOf<Pedido>()

    fun abrir() {
        println("::::::::::::::::::::::::::::::::::::::::::::")
        println(":::: Bienvenido a la Pizzaria Il Nono:::::::")
        println("::::::::::::::::::::::::::::::::::::::::::::")
        println("--- Tradición Italiana ---------------------")
        println("::::::::::::::::::::::::::::::::::::::::::::")

        while (true) {
            if (!inventario.hayStockParaAlguna(menu)) {
                println("Lo sentimos, nos quedamos sin ingredientes por hoy.")
                break
            }

            atenderCliente()

            if (!Consola.preguntarSiNo("¿Hay otro cliente por atender?")) break
        }

        actualizarEstados()
        Reporte.generar(pedidosDelDia, inventario)

        println(":::: Cerrando la pizzería. ¡Hasta mañana! ::::")
    }

    // Los estados solo avanzan en orden: En preparación -> Listo -> En camino
    private fun actualizarEstados() {
        if (pedidosDelDia.isEmpty()) return

        println("\n--- Actualización de estados ---")
        for (pedido in pedidosDelDia) {
            while (true) {
                val siguiente = pedido.estado.siguiente() ?: break
                val avanzar = Consola.preguntarSiNo(
                    "Pedido de ${pedido.cliente.nombre} (${pedido.detallePizzas}) está '${pedido.estado.texto}'. ¿Avanzar a '${siguiente.texto}'?"
                )
                if (!avanzar) break

                pedido.avanzarEstado()
                notificar(pedido, "Tu pedido ahora está: ${siguiente.texto}.")
            }
        }
    }

    private fun atenderCliente() {
        val cliente = pedirDatosCliente()
        val repartidor = elegirRepartidor()
        val items = reservarPizzas()
        val notas = Consola.leerNotas()
        val correoNotificaciones =
            if (Consola.preguntarSiNo("¿Deseas recibir notificaciones del estado de tu pedido por correo?")) cliente.correo else null

        val pedido = Pedido(cliente, items, repartidor, notas, correoNotificaciones)
        pedidosDelDia.add(pedido)

        println("*******************************")
        println("RESUMEN DEL PEDIDO: ")
        println(pedido.resumen())

        notificar(pedido, "Recibimos tu pedido de ${pedido.detallePizzas}. Total: ${pedido.total.comoMonto()}.")

        println("Te garantizamos que tu pedido llegará en menos de 40 minutos.")
        println("::::: MUCHAS GRACIAS POR SU COMPRA ::::::::")
    }

    // Elige las pizzas y descuenta el inventario. Si algo falla, solo se vuelven a pedir las pizzas:
    // los datos del cliente y la comuna ya ingresados no se pierden.
    private fun reservarPizzas(): List<ItemPedido> {
        while (true) {
            val items = elegirItems()
            try {
                inventario.descontar(items)
                return items
            } catch (e: IllegalStateException) {
                println("\nNo se pudo reservar el pedido: ${e.message}")
            } catch (e: IllegalArgumentException) {
                println("\nNo se pudo reservar el pedido: ${e.message}")
            }
            println("Elige tus pizzas nuevamente.\n")
        }
    }

    private fun pedirDatosCliente(): Cliente {
        while (true) {
            val nombre = Consola.leerTexto("Por favor, ingresa tu nombre: ")
            val direccion = Consola.leerTexto("$nombre, por favor, ingresa tu dirección: ")
            val telefono = Consola.leerTelefono()
            val correo = Consola.leerCorreo()

            val cliente = Cliente(nombre, direccion, telefono, correo)
            println("Confirmación.\n" +
                    "Los datos ingresados son:\n" +
                    "Nombre: $nombre\n" +
                    "Dirección: $direccion\n" +
                    "Teléfono: $telefono\n" +
                    "email: $correo\n")

            if (Consola.preguntarSiNo("¿Son correctos los datos ingresados?")) return cliente
            println("Volvamos a ingresar los datos nuevamente\n")
        }
    }

    // Arma la lista de pizzas del pedido. El stock se valida con todo lo ya elegido más la nueva pizza.
    private fun elegirItems(): List<ItemPedido> {
        val items = mutableListOf<ItemPedido>()
        while (true) {
            val pizza = leerPizza()
            val cantidad = leerCantidad()

            val faltantes = inventario.faltantes(items + ItemPedido(pizza, cantidad))
            if (faltantes.isNotEmpty()) {
                println("\nNo podemos agregar esa pizza: hay ingredientes insuficientes.")
                for (f in faltantes) {
                    val u = inventario.unidadDe(f.ingrediente)
                    println("- ${f.ingrediente}: necesitas ${f.necesario} $u, hay ${f.disponible} $u (faltan ${f.diferencia} $u)")
                }
                println("Elige otra pizza o una cantidad menor.\n")
                if (items.isNotEmpty() && Consola.preguntarSiNo("¿Deseas continuar solo con las pizzas ya elegidas (${items.joinToString { "${it.cantidad} x ${it.pizza.nombre}" }})?")) {
                    return items
                }
                continue
            }

            items.add(ItemPedido(pizza, cantidad))
            if (!Consola.preguntarSiNo("¿Deseas agregar otra pizza al pedido?")) return items
        }
    }

    // El cliente escribe su comuna y se busca el repartidor que la atiende
    private fun elegirRepartidor(): Repartidor {
        val comunasDisponibles = repartidores.flatMap { it.comunas }.sorted()
        while (true) {
            println("Solo repartimos en Santiago. Comunas con reparto:")
            println(comunasDisponibles.joinToString(", "))

            val comuna = Consola.leerTexto("Escribe tu comuna:")
            val repartidor = repartidores.firstOrNull { it.atiende(comuna) }
            if (repartidor != null) {
                println("Tu pedido será entregado por ${repartidor.nombre}.\n")
                return repartidor
            }

            println("No repartimos en '$comuna'. Revisa la lista e inténtalo nuevamente.\n")
        }
    }

    private fun leerPizza(): Pizza {
        while (true) {
            println("Seleccione la pizza que desee: ")
            menu.forEachIndexed { i, p -> println("${i + 1}. ${p.nombre} (${p.precio.comoMonto()})") }

            val indice = Consola.leerEnteroEnRango("Opción", 1, menu.size) ?: continue
            return menu[indice - 1]
        }
    }

    private fun leerCantidad(): Int {
        while (true) {
            println("¿Cuántas pizzas quiere?")
            val cantidad = Consola.leerEnteroEnRango("Cantidad", 1, Int.MAX_VALUE)
            if (cantidad != null) return cantidad
        }
    }

    // Solo notifica si el cliente dejó un correo para notificaciones (let se ejecuta únicamente si no es null)
    private fun notificar(pedido: Pedido, mensaje: String) {
        pedido.correoNotificaciones?.let { correo ->
            println("[Notificación enviada a $correo] $mensaje")
        }
    }
}
