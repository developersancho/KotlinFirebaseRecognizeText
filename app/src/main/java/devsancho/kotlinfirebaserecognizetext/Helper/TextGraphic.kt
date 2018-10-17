package devsancho.kotlinfirebaserecognizetext.Helper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.firebase.ml.vision.text.FirebaseVisionText

class TextGraphic internal constructor(overlay: GraphicOverlay, private val text: FirebaseVisionText.Element?) :
    GraphicOverlay.Graphic(overlay) {

    private val rectPaint: Paint
    private val textPaint: Paint

    companion object {
        private val TEXT_COLOR = Color.BLUE
        private val STROKE_WITDH = 4.0f
        private val TEXT_SIZE = 40.0f
    }

    init {
        rectPaint = Paint()
        rectPaint.color = TEXT_COLOR
        rectPaint.strokeWidth = STROKE_WITDH
        rectPaint.style = Paint.Style.STROKE

        textPaint = Paint()
        textPaint.color = TEXT_COLOR
        textPaint.textSize = TEXT_SIZE
    }

    override fun draw(canvas: Canvas) {
        if (text == null)
            throw IllegalStateException("Attempting to draw a null text")

        val rect = RectF(text.boundingBox)
        rect.left = translateX(rect.left)
        rect.right = translateX(rect.right)
        rect.top = translateY(rect.top)
        rect.bottom = translateY(rect.bottom)
        canvas.drawText(text.text, rect.left, rect.bottom, textPaint)
    }
}