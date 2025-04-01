package pe.com.bn.app.pagalo

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.net.http.SslError
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import pe.com.bn.app.pagalo.BuildConfig.VERSION_CODE
import pe.com.bn.app.pagalo.components.dialog.MyDialog
import pe.com.bn.app.pagalo.connections.entity.VersionEntity
import pe.com.bn.app.pagalo.connections.response.GeneralResponse
import pe.com.bn.app.pagalo.connections.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class MainActivity : AppCompatActivity() {


    var mWebview: WebView? = null
    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(applicationContext, "Descargando Completada", Toast.LENGTH_SHORT).show()
            Log.i("MainActivity", "Descargando Completada")
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    private val BASE_URL = BuildConfig.URL_WEB

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val call = ApiClient.apiService.getVersion("2",BuildConfig.DEVICE)

        call.enqueue(object : Callback<GeneralResponse<VersionEntity>> {
            override fun onResponse(call: Call<GeneralResponse<VersionEntity>>, response: Response<GeneralResponse<VersionEntity>>) {
                if (response.isSuccessful) {
                    val version = response.body()
                    if(version?.codResult == "00000"){
                        val versionCodeCurrent = Integer.parseInt(version.data.version.toString())
                        if(VERSION_CODE < versionCodeCurrent){
                            val message = "Hay una nueva versión de la aplicación, ¿desea descargarlo?"
                            if(version.data.flgObligatorio == "1"){
                                MyDialog.showMessage(this@MainActivity, getAppName(),"Descargar","Salir",message,{
                                    goUpdate()
                                },{
                                    System.exit(0)
                                })
                                return
                            }
                            MyDialog.showMessage(this@MainActivity, getAppName(),"Descargar","Luego",message,{
                                goUpdate()
                            },{
                                initWebView()
                            })
                            return
                        }
                        initWebView()
                        return
                    }
                    Toast.makeText(this@MainActivity, "Error: ${version?.msg}", Toast.LENGTH_SHORT).show()
                } else {
                    println("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GeneralResponse<VersionEntity>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })


    }
    private fun initWebView(){
        webView.webChromeClient =  object : WebChromeClient() {

        }

        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?,
            ) {
                handler?.cancel()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return handleUrl(view, request?.url.toString())
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return handleUrl(view, url)
            }

            private fun handleUrl(view: WebView?, url: String?): Boolean {
                if(url?.contains("seguridad/logindes.action") == true){
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view?.context?.startActivity(intent)
                    return true
                }
                return false
            }
        }

        val settings = webView.settings
        settings.javaScriptEnabled = true
        webView.loadUrl(BASE_URL)
    }
    private fun getAppName(): String {
        val appPackageName = packageName
        val packageManager: PackageManager = packageManager
        val applicationInfo = packageManager.getApplicationInfo(appPackageName, 0)
        return packageManager.getApplicationLabel(applicationInfo).toString()
    }
    private fun goUpdate() {
        val packageName = packageName

        if(BuildConfig.DEVICE == "HUA"){
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("appmarket://details?id=$packageName")
            startActivity(intent)
            return
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=$packageName")
        startActivity(intent)
    }
    override fun onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}