package net.mamoe.yamlkt.decoder

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import net.mamoe.yamlkt.Yaml
import net.mamoe.yamlkt.Yaml.Default
import net.mamoe.yamlkt.toContentMap
import kotlin.test.Test
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
    fun testDescriptorBlockMap() {
        println(
            Yaml.decodeFromString(
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
    fun testDynamicBlockMapNull() {
        val map = Default.decodeYamlMapFromString(
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
            mapOf<String?, String?>(
                "part_no" to "A4786",
                "descrip2" to "null",
                "descrip" to "null",
                "quantity" to null
            ),
            map.toContentMap()
        )
    }

    @Test
    fun testDynamicBlockMap() {
        val map = Default.decodeYamlMapFromString(
            """
part_no:   A4786
descrip:   Water Bucket (Filled)
price:     1.47
quantity:  4
    """
        )
        assertEquals(
            mapOf<String?, String?>(
                "part_no" to "A4786",
                "descrip" to "Water Bucket (Filled)",
                "price" to "1.47",
                "quantity" to "4"
            ),
            map.toContentMap()
        )
    }

    @Test
    fun testDynamicBlockMapNested1() {
        val map = Default.decodeYamlMapFromString(
            """
t:
  part_no:   A4786
  descrip:   Water Bucket (Filled)
  price:     1.47
  quantity:  4
    """
        )

        assertEquals(
            mapOf<String?, Map<String?, String?>>(
                "t" to mapOf<String?, String?>(
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
    fun testDynamicBlockMapNested2() {
        val map = Default.decodeYamlMapFromString(
            """
t:
- part_no:   A4786
  descrip:   Water Bucket (Filled)
  price:     1.47
  quantity:  4
    """
        )

        assertEquals(
            mapOf<String?, List<Map<String?, String?>?>?>(
                "t" to listOf(
                    mapOf<String?, String?>(
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
    fun testDescriptorBlockMapNested2() {
        val map = Yaml.decodeFromString(
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

    @Serializable
    data class TestData(
        val nullable: String?,
        val nonnull: String,
        val nullableMap: Map<String, String>?,
        val nullableList: List<String>?,
    )

    // from https://github.com/Him188/yamlkt/issues/3
    @Test
    fun testNullValue() {
        assertEquals(
            TestData(null, "value", null, null), Yaml.decodeFromString(
                TestData.serializer(), """
                        nullable:
                        nonnull: value
                        nullableMap:
                        nullableList: 
                    """.trimIndent()
            )
        )
    }

    //from https://github.com/Him188/yamlkt/issues/45
    @Test
    fun testComplexKey() {
        assertEquals(
            mapOf(
                listOf(
                    listOf("flow,item1", "flow,item2"),
                    listOf(
                        "block,item1",
                        listOf("nested", "flow,item")
                    ),
                    mapOf("key1" to "value,1", "key2" to "value2")
                ) to "ValueOfComplex"
            ),
            Yaml.decodeFromString<Map<List<Any>, String>>(
                """
                    ? 
                      - [ "flow,item1", "flow,item2" ]
                      -
                          - "block,item1"
                          - [ nested, "flow,item" ]
                      - { key1: "value,1", key2: value2 }
                    : "ValueOfComplex"
                """.trimIndent()
            )
        )
    }

    /*
    @Test
    fun testForceSpaceAfterColon() {
        assertFailsWith<YamlDecodingException> {
            allBlock.decodeMapFromString(
                """
                nonnull: value
                s:v
                foo: bar
                """
            )
        }
    }*/
}