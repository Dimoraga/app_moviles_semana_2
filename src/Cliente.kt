data class Cliente(
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val correo: String
) {
    companion object {
        private val regexTelefono = Regex("""^(\+?56)?\d{9}$""")
        private val regexCorreo = Regex("""^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*\.[A-Za-z]{2,}$""")

        // Acepta 9 dígitos, con o sin prefijo +56 (se ignoran espacios).
        // Devuelve "+56 XXX XXX XXX", o null si el formato es inválido.
        fun validarTelefono(entrada: String): String? {
            val limpio = entrada.replace(" ", "")
            if (!regexTelefono.matches(limpio)) return null
            val d = limpio.takeLast(9)
            return "+56 ${d.substring(0, 3)} ${d.substring(3, 6)} ${d.substring(6, 9)}"
        }

        // Devuelve el correo sin espacios, o null si el formato es inválido.
        fun validarCorreo(entrada: String): String? =
            entrada.trim().takeIf { regexCorreo.matches(it) }
    }
}
