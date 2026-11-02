import java.text.Normalizer
import java.util.Locale

// Formato chileno de montos: 64260 -> "$64.260"
fun Int.comoMonto(): String = "$" + "%,d".format(Locale.forLanguageTag("es-CL"), this)

// Para comparar textos sin importar mayúsculas, tildes ni espacios extra: " Maipú " -> "maipu"
fun String.normalizado(): String =
    Normalizer.normalize(trim().lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{M}"), "")
