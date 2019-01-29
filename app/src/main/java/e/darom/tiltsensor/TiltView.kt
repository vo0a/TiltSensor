package e.darom.tiltsensor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.view.View

//커스텀 뷰 작성하기
/*
1. View 클래스를 상속받는 새로운 클래스 생성합니다.
2. 필요한 메서드를 오버라이드 합니다. 여기서는 화면에 그리는 onDraw() 메서드를 오버라이드합니다.

일반적인 뷰와 같이 디자인 에디터에서 사용하면 더 복잡한 과정을 수행하기 때문에 이 책에서는 다루지 않고
커스텀 뷰를 생성하고 onCreate() 메서드에서 직접 인스턴트를 생성하여 사용합니다.

새로운 클래스를 생성하고 View를 상속받오록 합니다. 빨간줄이 뜨고 제안을 확인하여 Context를 인자로 받는 생성자를 클릭합니다.
에러가 사라지면 MainActivity.kt 파일에서 TiltView 를 생성자를 사용해 인스턴트화 하여 화면에 배치할 수 있습니다.
 */
/*
그래픽 API를 다루는 기초
그래픽을 다루려면 다음과 같은 클래스를 사용해야 합니다.
Canvas : 도화지 ( 뷰의 도면)
Paint : 붓 (색, 굵기, 스타일 정의)

도화지가 있어야 그림을 그릴 수 있습니다.
 */
class TiltView(context: Context?) : View(context) {

    //그리기 메서드에는 Paint 객체가 필요합니다.
    /*
    원을 그립니다. (x좌표, y좌표, 반지름, Paint 객체)
    draw(cx: Float, cy: Float, radius: Floate, paint: Paint!) :

    선을 그립니다. (한 점의 X 좌표, 한 점의 Y 좌표, 다른 점의 X 좌표, 다른 점의 Y 좌표, Paint 객체)
    drawLine(startX: Float, startY: Float, stopX: Float, stapY: Float, paint: Paint!) :
     */

    private val greenPaint: Paint = Paint()
    private val blackPaint: Paint = Paint()

    private var cX: Float = 0f
    private var cY: Float = 0f

    private var xCoord: Float = 0f
    private var yCoord: Float = 0f

    init{
        //녹색 페인트
        greenPaint.color = Color.GREEN          //1. 프로퍼티의 기본값은 검은색이고, Color 클래스에 선언된 색상들을 지정합니다.

        //검은 테두리 포인트
        blackPaint.style = Paint.Style.STROKE   //2. style 프로퍼티는 다음 속성 중에서 하나를 선택합니다.
                                                    // FILL: 색을 채웁니다. 획 관련된 설정을 무시합니다. (기본값)
                                                    //FILL_AND_STROKE : 획과 관련된 설정을 유지하면서 색을 채웁니다.
                                                    //STROKE: 획 관련 설정을 유지하여 외곽선만 그립니다.
    }

    //뷰의 크기가 결정되면 호출한 onSizeChanged() 메서드에서 중점 좌표를 계산합니다.
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        cX = w / 2f
        cY = h / 2f
    }

    //수평계 그래픽 구상
    override fun onDraw(canvas: Canvas?) {
        //바깥 원 : x, y, 반지름 색
        canvas?.drawCircle(cX, cY, 100f, blackPaint)
        //녹색 원
        //canvas?.drawCircle(cX, cY, 100f, greenPaint)
        //센서값을 뷰에 반영한 후 녹색 원 값
        canvas?.drawCircle(xCoord + cX, yCoord + cY, 100f, greenPaint)

        //가운데 십자가
        canvas?.drawLine(cX - 20, cY, cX + 20, cY, blackPaint)
        canvas?.drawLine(cX, cY - 20, cX, cY + 20, blackPaint)
    }

    //센서값을 뷰에 반영하기 : 센서값에 따라 녹색원이 움직이도록 구현.
    fun onSensorEvent(event: SensorEvent){
        //화면을 가로로 돌렸으므로 X축과 Y축을 서로 바꿈
        yCoord = event.values[0] * 20   //20을 곱한 이유는 센서값의 범위를 그대로 좌표로 사용하면 너무 범위가 적어서 녹색원의 움직임을 알아보기 어렵기 때문입니다.
        xCoord = event.values[1] * 20   //그래서 적당한 값을 곱해 보정하여 녹색원이 이동하는 범위를 넓혔습니다.

        invalidate() // 뷰의 onDraw() 메서드를 다시 호출하는 메서드입니다. 즉 뷰를 다시 그리게 됩니다.
    }

}