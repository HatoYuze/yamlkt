package net.mamoe.yamlkt

public actual fun YamlDecodingException.cleanStack() {
    stackTrace = stackTrace
        .dropWhile { frame ->
            frame.className.startsWith("net.mamoe.yamlkt.internal.YamlUtils")
                    && frame.methodName.startsWith("contextualDecodingException")
        }
        .toTypedArray()
}