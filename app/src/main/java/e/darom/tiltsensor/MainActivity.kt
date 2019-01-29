package e.darom.tiltsensor

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager

class MainActivity : AppCompatActivity(), SensorEventListener {

    //센서 정밀도가 변경되면 호출됩니다.
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    //센서값이 변경되면 호출됩니다. : 가속도 센서값 읽기
    /*
    values[0] : x축 값 : 위로 기울이면 -10~0, 아래로 기울이면 0~10
    values[1] : y축 값 : 왼쪽으로 기울이면 -10~0, 오른쪽으로 기울이면 0~10
    values[2] : z축 값 : 미사용

    //디버그용 로그를 표시할 때 사용합니다.
    Log.d([태그], [메시지]) :
    태그 : 로그캣에는 많은 내용이 표시되므로 필터링할 때 사용합니다.
    메시지 : 출력할 메시지를 작성합니다.

    이 외에도 다음과 같은 로그 메서드가 있습니다.
    Log.e() : 에러를 표시할 때 사용합니다.
    Log.w() : 경고를 표시할 때 사용합니다.
    Log.i() : 정보성 로그를 표시할 때 사용합니다.
    Log.v() : 모든 로그를 표시할 때 사용합니다.

    앱을 실행하고 하단의 Logcat을 클릭한 후 기기와 실행 중인 프로세서 명을 확인하고 MainActivity 태그를 필터링하면 우리가 작성한 로그만 볼 수 있습니다.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let{
            Log.d("MainActivity", "onSensorChanged: x :" +
            " ${event.values[0]}, y : ${event.values[1]}, z : ${event.values[2]}")

            //센서값이 변경되면 TiltView에 전달하기
            tiltView.onSensorEvent(event)   //액티비티에서 센서값이 변경되면 onSensorChanged() 메서드가 호출되므로
                                            //여기서 TiltView 에 센서값을 전달합니다.
        }
    }

    //SensorManager 준비
    /*
    센서를 사용하려면 안드로이드가 제공하는 센서 매니저 서비스 객체가 필요합니다.
    센서 매니저는 안드로이드 기기의 각 센서 접근 및 리스너의 등록 및 취소, 이벤트를 수집하는 방법을 제공합니다.

    장치에 있는 센서를 사용하려면 먼저 센서 매니저에 대한 참조를 얻어야 합니다.
    이렇게 하려면 getSystemService() 메서드에 SENSOR_SERVICE 상수를 전달하여 SensorManager 클래스의 인스턴트를만듭니다.

    지연된 초기화를 사용하여 sensorManager 변수를 처음 사용할 때 getSystemService() 메서드로 SensorManager 객체를 얻습니다.
     */
    private val sensorManager by lazy{
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    //센서 등록
    /*
    SensorManager 객체가 준비되면 액티비티가 동작할 때만 센서가 동작해야 배터리를 아낄 수 있습니다.
    일반적으로 센서의 사용 등록은 액티비티의 onResume() 메서드에서 수행합니다.

    1. registerListener() 메서드로 사용할 센서를 등록합니다. 첫 번째 인자는 센서값을 받을 SensorEventListener 입니다.
    여기서 this를 지정하여 액티비티에서 센서값을 받도록 합니다.
        @ MainActivity 클래스가 SensorEventListener를 구현하도록 추가합니다. @
        추가하면 빨간 전구가 생기고, Alt + Enter를 클리면하면 나오는 메뉴에서 Implement members를 클릭하며 Ctrl + A 를 눌러 모두 추가합니다.
        onAccuacyChanged() : 센서 정밀도가 변경되면 호출됩니다.
        onSensorChanged() : 센서값이 변경되면 호출됩니다.

    2. getDefaultSensor() 메서드로 사용할 센서 종류를 지정합니다. 여기서는 Sensor 클래스에 상수로 정의된 가속도 센서 (TYPE_ACCELEROMETER)를 지정했습니다.

    3. 세 번째 인자는 센서값을 얼마나 자주 받을지를 지정합니다. SensorManager 클래스에 정의된 상수 중 하나를 선택합니다.
    SENSOR_DELAY_FASTEST : 가능한 가장 자주 센서값을 얻습니다.
    SENSOR_DELAY_GAME : 게임에 적합한 정도로 센서값을 얻습니다.
    SENSOR_DELAY_NORMAL : 화면 방향이 전환될 때 적합한 정도로 센서값을 얻습니다.
    SENSOR_DELAY_UI : 사용자 인터페이스를 표시하기에 적합한 정도로 센서값을 얻습니다.

    너무 빈번하게 센서값을 읽으면 시스템 리소스를 낭비하고 배터리 전원을 사용합니다.
     */
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this,                     //1.
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  //2.
            SensorManager.SENSOR_DELAY_NORMAL)                          //3.
    }

    //센서 해제
    /*
    액티비티가 동작 중일 때만 센서를 사용하려면 화면이 꺼지기 직전인 onPause() 메서드에서 센서를 해제합니다.
    unresiterListener() 메서드를 이용하여 센서를 사용을 해제할 수 있으며 인자로 SensorEventListener 객체를 지정합니다.
    MainActivity 클래스에서 이 객체를 구현 중이므로 this를 지정합니다.
     */
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    //---------이제 센서를 사용할 준비가 되었습니다.---------
    /*
    @ 에뮬레이터에서 센서 테스트하기 @
    확장컨드롤 -> virtual sensors -> Accelerometer 탭에서 센서를 테스트할 수 있습니다. 기기를 마우스로 움직여 값이 변하는 것을 확인하세요.
    x축과 y축이 0, 0 이면 수평입니다. 이제 센서값에 따라서 화면에 수평계를 그리면 됩니다.
     */

    private lateinit var tiltView: TiltView       //1. 늦은 초기화 선언을 합니다.

    /*
    참고로 기기 방향을 한 방향으로 고정하면 좌표축도 돌아가므로 일반적인 x축과 y축 방향과 달라지게 된다는 사실을 기억해 둡시다.

    1. 기기의 방향을 가로로 고정하려면 슈퍼클래스의 생성자를 호출하기 전에 requestedOrientation 프로퍼티값에
    가로 방향을 나타내는 ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE 값을 설정합니다.

    2. window.addFlags() 메서드에는 액티비티 상태를 지정하는 여러 플래그를 설정할 수 있습니다.
    여기서는 FLAG_KEEP_SCREEN_ON 플래그를 지정하여 화면이 항상 켜지도록 설정했습니다.
    앱을 다시 실행하면 자동으로 화면이 꺼지지 않기 때문에 센서를 테스트하기가 편해집니다.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //2. 화면이 꺼지지 않게 하기
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //1. 화면이 가로 모드로 고정되게 하기
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tiltView = TiltView(this)        //2. onCreate() 메서드에 this 를 넘겨서 TiltView 를 초기화합니다.
        setContentView(tiltView)                //3. 기존의 R.layout.activity_main 대신에 tiltView 를 setContentView() 메서드에 전달합니다.
                                                    //tiltView 가 전체 레이아웃이 되었습니다.
    }



}

