package cn.cleartv.nanohttpd

import android.util.Log
import cn.cleartv.nanohttpd.Utils.normalizeUri
import org.json.JSONObject
import java.io.IOException

class WSServer(port: Int) : NanoWSD(port) {
    val TAG: String = "WSServer"

    override fun openWebSocket(handshake: IHTTPSession): WebSocket {
        return object : WebSocket(handshake) {

            override fun onOpen() {
                // mount -o remount /system
                Log.d(TAG, "onOpen")
            }

            override fun onClose(
                code: WebSocketFrame.CloseCode?,
                reason: String?,
                initiatedByRemote: Boolean
            ) {
                Log.d(
                    TAG,
                    "onClose [${if (initiatedByRemote) "Remote" else "Self"}] $code: $reason"
                )
            }

            override fun onMessage(message: WebSocketFrame) {
                try {
                    message.setUnmasked()
                    val text = message.textPayload
                    Log.d(TAG, "onMessage: $text")
                    val frame = WebSocketFrame(
                        WebSocketFrame.OpCode.Text,
                        message.isFin,
                        handleMessage(JSONObject(text)).toString()
                    )
                    sendFrame(frame)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onPong(pong: WebSocketFrame?) {
                Log.d(TAG, "onPong: $pong")
            }

            override fun onException(exception: IOException?) {
                Log.w(TAG, "ws error", exception)
            }

        }
    }

    private fun handleMessage(message: JSONObject): JSONObject {
        val path = message.optString("path")
        val data = message.optJSONObject("data")?.toString()
        return ServerService.invoke(path.normalizeUri(), null, data)
    }
}