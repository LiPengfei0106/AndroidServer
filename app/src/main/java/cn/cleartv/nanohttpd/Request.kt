package cn.cleartv.nanohttpd

@Target(AnnotationTarget.FUNCTION)
annotation class Request(val method: NanoHTTPD.Method, val uri: String)