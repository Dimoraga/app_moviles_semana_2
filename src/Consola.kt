// Lectura y validación de datos ingresados por consola
object Consola {

    fun leerTexto(mensaje: String): String {
        while (true) {
            println(mensaje)
            val texto = readln().trim()
            if (texto.isNotBlank()) return texto
            println("Este campo no puede estar vacío.\n")
        }
    }

    // Lee un entero y valida su rango con try-catch. Devuelve null (tras explicar el error) si es inválido.
    fun leerEnteroEnRango(nombre: String, minimo: Int, maximo: Int): Int? {
        val entrada = readln().trim()
        return try {
            val valor = entrada.toInt()
            require(valor in minimo..maximo) {
                when {
                    valor < 0 -> "$nombre no válida: $valor. No se permiten números negativos."
                    maximo == Int.MAX_VALUE -> "$nombre no válida: $valor. Debe ser mayor o igual a $minimo."
                    else -> "$nombre no válida: $valor. Elige un número del $minimo al $maximo."
                }
            }
            valor
        } catch (e: NumberFormatException) {
            println("'$entrada' no es un número entero válido. Ingresa solo dígitos, por ejemplo: $minimo.\n")
            null
        } catch (e: IllegalArgumentException) {
            println("${e.message}\n")
            null
        }
    }

    fun leerTelefono(): String {
        while (true) {
            println("Por favor, ingresa tu número de contacto (9 dígitos, ej: 912345678): ")
            val telefono = Cliente.validarTelefono(readln())
            if (telefono != null) return telefono
            println("Teléfono inválido. Debe tener exactamente 9 dígitos numéricos, por ejemplo: 912345678.\n")
        }
    }

    fun leerCorreo(): String {
        while (true) {
            println("Por favor, ingresa tu correo electrónico (ej: nombre@dominio.com): ")
            val correo = Cliente.validarCorreo(readln())
            if (correo != null) return correo
            println("Correo inválido. Debe contener un @ y un punto (.) después del @, por ejemplo: nombre@dominio.com.\n")
        }
    }

    // Campo opcional: Enter lo deja en null
    fun leerNotas(): String? {
        println("¿Alguna nota u observación para tu pedido? (Enter para omitir)")
        return readln().trim().takeIf { it.isNotEmpty() }
    }

    fun preguntarSiNo(pregunta: String): Boolean {
        while (true) {
            println("$pregunta Escribe SI o NO.")
            when (readln().trim().uppercase()) {
                "SI" -> return true
                "NO" -> return false
                else -> println("Responde SI o NO.")
            }
        }
    }
}
