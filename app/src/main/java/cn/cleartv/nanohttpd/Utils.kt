package cn.cleartv.nanohttpd

import cn.cleartv.nanohttpd.NanoHTTPD.Response
import org.json.JSONObject

object Utils {

    fun String.normalizeUri(): String {
        var realValue = this.trim()
        if (realValue.startsWith("/")) {
            realValue = realValue.substring(1)
        }
        if (realValue.endsWith("/")) {
            realValue = realValue.substring(0, realValue.length - 1)
        }
        return realValue
    }

    fun JSONObject.toResponse(): Response {
        return NanoHTTPD.newFixedLengthResponse(
            NanoHTTPD.Response.Status.lookup(this.optInt("code", 500))
                ?: NanoHTTPD.Response.Status.INTERNAL_ERROR,
            "application/json",
            this.toString()
        )
    }
}