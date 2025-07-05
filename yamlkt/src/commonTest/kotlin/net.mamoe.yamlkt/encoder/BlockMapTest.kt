package net.mamoe.yamlkt.encoder

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import net.mamoe.yamlkt.Yaml
import kotlin.test.Test
import kotlin.test.assertEquals


internal class BlockMapTest {

    @Serializable
    data class MyData(
        var value2: Int = 123,
        var myOtherData: Nested,
        val myList: List<Nested> = listOf(Nested(123))
    ) {
        @Serializable
        data class Nested(
            var v: Int
        )
    }


    @Test
    fun testNestedClass() {
        val yaml = """
           { value2: 1234, myOtherData: { v: 123 }, myList: [ { v: 123 } ] }
 """
        val old = Yaml.decodeFromString(MyData.serializer(), yaml)
        println(allFlow.encodeToString(old))
        val out = Yaml.encodeToString(old)
        val new = Yaml.decodeFromString(MyData.serializer(), out)

        assertEquals(old, new)
    }

    @Serializable
    class Empty {
        override fun equals(other: Any?): Boolean {
            return other != null && other::class == this::class
        }

        override fun hashCode(): Int {
            return this::class.hashCode()
        }
    }

    @Test
    fun testEmptyClass() {
        assertEquals("{}", allBlock.encodeToString(Empty()).trim())
    }

    @Test
    fun testComplexKey() {
        val origin = mapOf(listOf(1,2) to "abc")
        val out = allBlock.encodeToString(origin)
        val new = allBlock.decodeFromString<Map<List<Int>, String>>(out)
        val newFlow = allBlock.decodeFromString<Map<List<Int>, String>>(allFlow.encodeToString(origin))
        assertEquals(origin, new)
        assertEquals(origin, newFlow)
    }
}