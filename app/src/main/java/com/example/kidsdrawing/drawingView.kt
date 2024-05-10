package com.example.kidsdrawing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class drawingView(context:Context,attrs: AttributeSet): View(context,attrs) {

    private var drawPath: Custompath?=null
    private var canvasBitmap: Bitmap?=null
    private var drawPaint:Paint?=null
    private var canvasPaint:Paint?=null
    private var brushSize:Float=0.toFloat()
    private var color= Color.BLACK
    private var canvas:Canvas?=null
    private var mpath= ArrayList<Custompath>() //to remember all manauevers and display them
    private val mUndoPaths = ArrayList<Custompath>()



    init{
        setupdrawing()
    }

    fun setupdrawing(){
        drawPaint= Paint()
        drawPath= Custompath(color,brushSize)
        drawPaint!!.color=color
        drawPaint!!.style=Paint.Style.STROKE
        drawPaint!!.strokeJoin=Paint.Join.ROUND
        drawPaint!!.strokeCap=Paint.Cap.ROUND
        canvasPaint=Paint(Paint.DITHER_FLAG)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas=Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(canvasBitmap!!,0f,0f,canvasPaint)

        for(path in mpath){
            drawPaint!!.strokeWidth= path.brushThickness
            drawPaint!!.color= path.color
            canvas.drawPath(path, drawPaint!!)

        }

        if(!drawPath!!.isEmpty()) {
            drawPaint!!.strokeWidth= drawPath!!.brushThickness
            drawPaint!!.color= drawPath!!.color
            canvas.drawPath(drawPath!!, drawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var touchx=event?.x
        val touchy=event?.y

        when (event?.action){
            MotionEvent.ACTION_DOWN->{
                drawPath!!.color=color
                drawPath!!.brushThickness=brushSize

                drawPath!!.reset()
                drawPath!!.moveTo(touchx!!,touchy!!)
            }

            MotionEvent.ACTION_MOVE->{
                if (touchx != null) {
                    if (touchy != null) {
                        drawPath!!.lineTo(touchx,touchy)
                    }
                }
            }
            MotionEvent.ACTION_UP->{
                mpath.add(drawPath!!)
                drawPath=Custompath(color,brushSize)
            }
            else-> return false
        }
        invalidate()
        return true
    }

    fun setBrushSize(newSize:Float){  //to make lines same thickness on all devices
        brushSize= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize,resources.displayMetrics)
        drawPaint!!.strokeWidth=brushSize
    }

    fun setColor(newColor:String){
        color=Color.parseColor(newColor)
        drawPaint!!.color=color
    }
    fun onClickUndo() {
        if (mpath.size > 0) {

            mUndoPaths.add(mpath.removeAt(mpath.size - 1))
            invalidate() // Invalidate the whole view. If the view is visible
        }
    }

    internal inner class Custompath(var color: Int, var brushThickness: Float): Path() {
        
    }

}


