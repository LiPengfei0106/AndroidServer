package cn.cleartv.nanohttpd

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.cleartv.nanohttpd.webserver.SimpleWebServer
import java.io.File

class MainActivity : AppCompatActivity() {

    private var httpServer: HttpServer? = null
    private var webServer: SimpleWebServer? = null
    private var wsServer: WSServer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnHttpServer)?.setOnClickListener { startHttpServer() }
        findViewById<Button>(R.id.btnWebsocketServer)?.setOnClickListener { startWebsocketServer() }
        findViewById<Button>(R.id.btnWebFileServer)?.setOnClickListener { startWebFileServer() }
    }

    override fun onDestroy() {
        super.onDestroy()
        httpServer?.stop()
        webServer?.stop()
        wsServer?.stop()
    }

    fun startHttpServer(port: Int = 20000) {
        if (httpServer?.isAlive == true) {
            Toast.makeText(this, "HttpServer already started at port $port ", Toast.LENGTH_SHORT)
                .show()
            return
        }
        try {
            httpServer = HttpServer(port)
            httpServer?.start()
            Toast.makeText(this, "HttpServer start success at port $port ", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            httpServer?.stop()
            httpServer = null
            Toast.makeText(this, "HttpServer start failed at port $port ", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun startWebsocketServer(port: Int = 20001) {
        if (wsServer?.isAlive == true) {
            Toast.makeText(
                this,
                "WebSocketServer already started at port $port ",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        try {
            wsServer = WSServer(port)
            wsServer?.start()
            Toast.makeText(this, "WebSocketServer start success at port $port ", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            wsServer?.stop()
            wsServer = null
            Toast.makeText(
                this,
                "WebSocketServer start failed at port $port :${e}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun startWebFileServer(port: Int = 20002) {
        if (webServer?.isAlive == true) {
            Toast.makeText(this, "WebFileServer already started at port $port ", Toast.LENGTH_SHORT)
                .show()
            return
        }
        try {
            // 只有第一个存在并可读的文件夹生效
            val wwwroots = arrayListOf<File>()
            wwwroots.add(File("/")) // 优先尝试根目录，需要root权限
            wwwroots.add(File(App.instance.applicationInfo.dataDir))
            webServer = SimpleWebServer(null, port, wwwroots, true)
            webServer?.start()
            Toast.makeText(this, "WebFileServer start success at port $port ", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            webServer?.stop()
            webServer = null
            Toast.makeText(
                this,
                "WebFileServer start failed at port $port :${e}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}