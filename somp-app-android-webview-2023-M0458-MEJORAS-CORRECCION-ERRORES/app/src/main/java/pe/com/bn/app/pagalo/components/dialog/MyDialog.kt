package pe.com.bn.app.pagalo.components.dialog

import android.content.Context
import android.app.AlertDialog

class MyDialog {
    companion object {
        fun showMessage(context: Context, title: String, positiveButton: String, negativeButton: String, mensaje: String,postiveAction: () -> Unit, negativeAction: () -> Unit) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(mensaje)

            builder.setPositiveButton(positiveButton) { dialog, which ->
                postiveAction.invoke()
                dialog.dismiss()
            }

            builder.setNegativeButton(negativeButton) { dialog, which ->
                negativeAction.invoke()
                dialog.dismiss()
            }

            val dialogo = builder.create()
            dialogo.show()
        }
    }
}