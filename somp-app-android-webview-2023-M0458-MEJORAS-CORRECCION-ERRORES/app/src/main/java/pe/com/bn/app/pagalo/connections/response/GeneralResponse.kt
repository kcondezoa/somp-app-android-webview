package pe.com.bn.app.pagalo.connections.response

data class GeneralResponse<T>(
    val codResult: String,
    val msg: String,
    val msgError: String,
    var data: T
)