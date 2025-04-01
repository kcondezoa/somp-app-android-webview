package pe.com.bn.app.pagalo.connections.retrofit

import pe.com.bn.app.pagalo.connections.entity.VersionEntity
import pe.com.bn.app.pagalo.connections.response.GeneralResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("operaciones/controlVersion")
    fun getVersion(@Query("canalConsulta") canalConsulta: String,@Query("device") device: String): Call<GeneralResponse<VersionEntity>>
}