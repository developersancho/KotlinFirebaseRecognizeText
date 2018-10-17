package devsancho.kotlinfirebaserecognizetext

import android.app.AlertDialog
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.wonderkiln.camerakit.*
import devsancho.kotlinfirebaserecognizetext.Helper.TextGraphic
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var waitingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        waitingDialog = SpotsDialog.Builder().setContext(this)
            .setCancelable(false)
            .setMessage("please wait...")
            .build()

        btn_recognize.setOnClickListener {
            camera_view.start()
            camera_view.captureImage()
            graphic_overlay.clear()
        }

        camera_view.addCameraKitListener(object : CameraKitEventListener {
            override fun onVideo(p0: CameraKitVideo?) {
            }

            override fun onEvent(p0: CameraKitEvent?) {
            }

            override fun onImage(p0: CameraKitImage?) {
                waitingDialog.show()
                var bitmap = p0!!.bitmap
                bitmap = Bitmap.createScaledBitmap(bitmap, camera_view.width, camera_view.height, false)
                camera_view.stop()
                recognizeText(bitmap)
            }

            override fun onError(p0: CameraKitError?) {
            }

        })
    }

    private fun recognizeText(bitmap: Bitmap?) {
        val image = FirebaseVisionImage.fromBitmap(bitmap!!)
        val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
            .setLanguageHints(Arrays.asList("en"))
            .build()
        val textRecognizer = FirebaseVision.getInstance().getCloudTextRecognizer(options)
        textRecognizer.processImage(image)
            .addOnSuccessListener { result -> processResult(result) }
            .addOnFailureListener { e ->
                Log.d("ERROR", e.message)
                waitingDialog.dismiss()
            }

    }

    private fun processResult(result: FirebaseVisionText?) {
        val blocks = result!!.textBlocks
        if (blocks.size == 0) {
            Toast.makeText(this, "No text found", Toast.LENGTH_LONG).show()
            return
        }

        graphic_overlay.clear()

        for (i in blocks.indices) {
            val lines = blocks[i].lines
            for (j in lines.indices) {
                val elements = lines[j].elements
                for (k in elements.indices) {
                    var textGraphic = TextGraphic(graphic_overlay, elements[k])
                    graphic_overlay.add(textGraphic)
                }
            }
        }

        waitingDialog.dismiss()
    }

    override fun onResume() {
        super.onResume()
        camera_view.start()
    }

    override fun onPause() {
        super.onPause()
        camera_view.stop()
    }
}
