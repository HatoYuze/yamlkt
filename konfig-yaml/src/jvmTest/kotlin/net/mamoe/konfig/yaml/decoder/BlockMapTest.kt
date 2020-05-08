package net.mamoe.konfig.yaml.decoder

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import net.mamoe.konfig.yaml.Yaml
import net.mamoe.konfig.yaml.parseYamlMap
import net.mamoe.konfig.yaml.toContentMap
import org.junit.Test
import kotlin.test.assertEquals


internal class BlockMapTest {

    @Serializable
    data class Config(val bot: Bot, val owner: Owner) {
        @Serializable
        data class Bot(val account: Long, val password: String)

        @Serializable
        data class Owner(val account: Long)
    }

    @Test
    fun `test descriptor block map`() {
        println(
            Yaml.default.parse(
                Config.serializer(), """
bot:
  account: 12345678910
  password: "=w==w==w="
owner:
  account: 12345678910
"""
            )
            )
    }


    @Test
    fun `test dynamic block map null`() {
        val map = Yaml.default.parseYamlMap(
            """
     #test
part_no:   A4786    #test
descrip2:   'null'  #test
descrip:   "null"
 #test
quantity:  null   #test
    """
        )
        assertEquals(
            mapOf(
                "part_no" to "A4786",
                "descrip2" to "null",
                "descrip" to "null",
                "quantity" to null
            ),
            map.toContentMap()
        )
    }

    @Test
    fun `test dynamic block map`() {
        val map = Yaml.default.parseYamlMap(
            """
part_no:   A4786
descrip:   Water Bucket (Filled)
price:     1.47
quantity:  4
    """
        )
        assertEquals(
            mapOf(
                "part_no" to "A4786",
                "descrip" to "Water Bucket (Filled)",
                "price" to "1.47",
                "quantity" to "4"
            ),
            map.toContentMap()
        )
    }

    @Test
    fun `test dynamic block map nested 1`() {
        val map = Yaml.default.parseYamlMap(
            """
t:
  part_no:   A4786
  descrip:   Water Bucket (Filled)
  price:     1.47
  quantity:  4
    """
        )

        assertEquals(
            mapOf(
                "t" to mapOf(
                    "part_no" to "A4786",
                    "descrip" to "Water Bucket (Filled)",
                    "price" to "1.47",
                    "quantity" to "4"
                )
            ),
            map.toContentMap()
        )
    }

    @Test
    fun `test dynamic block map nested 2`() {
        val map = Yaml.default.parseYamlMap(
            """
t:
- part_no:   A4786
  descrip:   Water Bucket (Filled)
  price:     1.47
  quantity:  4
    """
        )

        assertEquals(
            mapOf(
                "t" to listOf(
                    mapOf(
                        "part_no" to "A4786",
                        "descrip" to "Water Bucket (Filled)",
                        "price" to "1.47",
                        "quantity" to "4"
                    )
                )
            ),
            map.toContentMap()
        )
    }

    @Test
    fun `test descriptor block map nested 2`() {
        val map = Yaml.default.parse(
            MapSerializer(String.serializer(), ListSerializer(MapSerializer(String.serializer(), String.serializer()))),
            """
t:
  - part_no:   A4786
    descrip:   Water Bucket (Filled)
    price:     1.47
    quantity:  4
    """
        )

        assertEquals(
            mapOf(
                "t" to listOf(
                    mapOf(
                        "part_no" to "A4786",
                        "descrip" to "Water Bucket (Filled)",
                        "price" to "1.47",
                        "quantity" to "4"
                    )
                )
            ), map
        )
    }
}