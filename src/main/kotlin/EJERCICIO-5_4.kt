package un5.eje5_4

import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.FileNotFoundException
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.*

class CatalogoLibrosXML(val pathName: String) {

    private lateinit var document: Document
    private lateinit var listaLibros: MutableList<Node>

    private val log = LogManager.getLogManager().getLogger("").apply { level = Level.OFF }

    init {
        log.info("Se ejecuta el init de la clase CatalogoLibrosXML.")
        require(pathName.isNotEmpty()) { "La ruta no puede estar vacía." }
        constructor(pathName)
        log.info("Se termina la ejecución del init.")
    }

    //No entiendo para qué hace falta el parámetro cargador. Lo uso porque lo especifica el enunciado.

    private fun constructor(cargador: String) {
        log.info("Se ejecuta fun constructor. El path recibido es: $cargador")
        try {
            document = readXml(cargador)
            log.info("El archivo se ha encontrado y se ha cargado con éxito.")
            log.info("Se intenta extraer la lista de Nodos (Libros) del documento.")
            listaLibros = obtenerListaNodosPorNombre(document, "book")
            log.info("Se ha extraído la lista correctamente.")
            log.info("Termina la ejecución de fun constructor.")
        } catch (e: FileNotFoundException) {
            log.warning("El programa terminará tras no encontrar el archivo.")
            throw FileNotFoundException("No se ha encontrado un archivo con esa ruta.")
        }
    }

    fun existeLibro(idLibro: String): Boolean {
        log.info("Se ejecuta fun existeLibro. El idLibro recibido es $idLibro.")
        return listaLibros.any {
            it.attributes.getNamedItem("id").toString() == "id=\"$idLibro\""
        }
    }

    fun infoLibro(idLibro: String): Map<String, Any> {
        log.info("Se ejecuta fun infoLibro. El idLibro recibido es $idLibro")
        val libro = listaLibros.find { it.attributes.getNamedItem("id").toString() == "id=\"$idLibro\"" }
        if (libro != null) {
            log.info("Se ha encontrado el libro con ese id.")
            var infoDelLibro = libro.firstChild
            val mapaConInfoDelLibro: MutableMap<String, Any> = mutableMapOf()
            mapaConInfoDelLibro["id"] = idLibro
            log.info("Se han creado variables para el bucle.")

            //El bucle recorrerá todos los elementos del libro encontrado con ese id, y asignará sus nombres a sus correspondientes valores en un map.
            log.info("Entrando en el bucle.")
            while (infoDelLibro != null) {
                if (infoDelLibro.nodeName != "#text") {
                    log.info("Se asigna ${infoDelLibro.nodeName} a ${infoDelLibro.firstChild.nodeValue} en el map según el tipo que le corresponda.")
                    when (infoDelLibro.nodeName) {
                        "author" -> mapaConInfoDelLibro["author"] = infoDelLibro.firstChild.nodeValue.split(",").first()
                        "price" -> mapaConInfoDelLibro["price"] = infoDelLibro.firstChild.nodeValue.toDouble()
                        "publish_date" -> infoDelLibro.firstChild.nodeValue.split("-").let {
                            mapaConInfoDelLibro["publish_date"] = Date(it[0].toInt(), it[1].toInt() - 1, it[2].toInt())
                        }
                        else -> mapaConInfoDelLibro[infoDelLibro.nodeName] = infoDelLibro.firstChild.nodeValue
                    }
                }
                log.info("Se pasa al siguiente elemento del libro.")
                infoDelLibro = infoDelLibro.nextSibling
            }
            log.info("Se sale del bucle.")
            log.info("Termina la ejecución de infoLibro. Devuelve el mapa con información del libro:")
            log.info("$mapaConInfoDelLibro")
            return mapaConInfoDelLibro

        } else {
            log.info("No se ha encontrado el libro con ese id. Se termina la ejecución de fun infoLibro.")
            return emptyMap()
        }
    }

}

fun main() {
    val catalogo =
        CatalogoLibrosXML("D:\\Usuarios\\alexg\\Documents\\instituto\\superior 1\\DAM1-5_4-AGP\\src\\xml\\Catalog.xml")

    println("Introduce el id del libro.")

    val idLibro = readLine() ?: ""

    if (catalogo.existeLibro(idLibro)) {
        println("El libro existe")
        println(catalogo.infoLibro(idLibro))
    } else println("El libro no existe")
}
