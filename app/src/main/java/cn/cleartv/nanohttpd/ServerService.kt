package cn.cleartv.nanohttpd

import cn.cleartv.nanohttpd.Utils.normalizeUri
import org.json.JSONObject

object ServerService {

    val methods = ServerService.javaClass.methods

    @Request(NanoHTTPD.Method.GET, "/get")
    fun get(): Any {
        return "Get"
    }

    @Request(NanoHTTPD.Method.POST, "/post")
    fun post(postData: String): Any {
        return postData
    }

    fun invoke(realUri: String, method: NanoHTTPD.Method?, postData: String?): JSONObject {
        try {
            for (m in methods) {
                val request = m.getAnnotation(Request::class.java)
                if (method == null || request?.method == method) {
                    if (request?.uri?.normalizeUri() == realUri) {
                        m.isAccessible = true
                        val response = when {
                            m.parameterTypes.isEmpty() -> {
                                m.invoke(ServerService)
                            }

                            m.parameterTypes.first() == JSONObject::class.java -> {
                                m.invoke(ServerService, JSONObject(postData ?: "{}"))
                            }

                            m.parameterTypes.first() == String::class.java -> {
                                m.invoke(ServerService, postData ?: "")
                            }

                            else -> {
                                return JSONObject().apply {
                                    put("code", 405)
                                    put("msg", "Method Not Allowed")
                                    put("data", "error methods with parameters: ${m.name}")
                                }
                            }
                        }
                        if (response != null) {
                            return JSONObject().apply {
                                put("code", 200)
                                put("message", "success")
                                put("data", response)
                            }
                        }
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return JSONObject().apply {
                put("code", 500)
                put("message", e.cause?.message ?: "Internal error")
                put("data", e.cause.toString())
            }
        }
        return JSONObject().apply {
            put("code", 400)
            put("message", "Unknown path: $realUri\"")
            put("data", realUri)
        }
    }

}