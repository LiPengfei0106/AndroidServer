package cn.cleartv.nanohttpd

import android.util.Log
import cn.cleartv.nanohttpd.Utils.normalizeUri
import cn.cleartv.nanohttpd.Utils.toResponse
import java.io.FileNotFoundException

class HttpServer(port: Int) : NanoHTTPD(port) {

    val TAG = "HttpServer"

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri
        val realUri = uri?.normalizeUri() ?: ""
        val method = session.method
        val parameters = session.parameters
        val remoteHostName = session.remoteHostName
        val remoteIpAddress = session.remoteIpAddress
        val headers = session.headers
        val bodyMap = hashMapOf<String, String>()
        session.parseBody(bodyMap)
        Log.d(
            TAG, "uri: $uri, \n" +
                    "realUri: $realUri, \n" +
                    "method: $method, \n" +
                    "headers: $headers, \n" +
                    "parameters: $parameters, \n" +
                    "bodyMap: $bodyMap, \n" +
                    "remoteHostName: $remoteHostName, \n" +
                    "remoteIpAddress: $remoteIpAddress"
        )
        val postData = bodyMap["postData"]
        val contentType = headers["content-type"]
        if (method == Method.GET && realUri.startsWith("frontend")) {
            // 返回assets目录下的网页
            return frontendFromAssets(realUri)
        }
        return ServerService.invoke(realUri, method, postData).toResponse()
    }

    /**
     * 获取assets/frontend下的文件，可用于显示网页
     */
    fun frontendFromAssets(realUri: String): Response {
        var mimeType = "text/plain"
        var path = realUri
        if (!realUri.contains(".")) {
            path = "$path/index.html"
        }
        val suffix = path.split(".").last().lowercase()
        // 获取本地网页资源
        when (suffix) {
            "html" -> {
                mimeType = "text/html"
            }

            "png" -> {
                mimeType = "image/png"
            }

            "jpg" -> {
                mimeType = "image/jpg"
            }

            "gif" -> {
                mimeType = "image/gif"
            }

            "css" -> {
                mimeType = "text/css"
            }

            "js" -> {
                mimeType = "text/javascript"
            }

            "svg" -> {
                mimeType = "text/svg+xml"
            }

            "woff" -> {
                mimeType = "font/woff"
            }

            "woff2" -> {
                mimeType = "font/woff2"
            }

            "ttf" -> {
                mimeType = "font/ttf"
            }

            "eot" -> {
                mimeType = "application/vnd.ms-fontobject"
            }
        }
        return try {
            newChunkedResponse(
                Response.Status.OK,
                mimeType,
                App.instance.assets.open(path)
            )
        } catch (e: FileNotFoundException) {
            newFixedLengthResponse(
                Response.Status.NOT_FOUND,
                mimeType,
                "Not Found!\n" + "${e.message}"
            )
        } catch (e: Exception) {
            newFixedLengthResponse(
                Response.Status.INTERNAL_ERROR,
                mimeType,
                "Error!\n${e.message}"
            )
        }
    }

}