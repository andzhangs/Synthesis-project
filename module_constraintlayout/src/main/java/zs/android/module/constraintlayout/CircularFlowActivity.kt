package zs.android.module.constraintlayout

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.CircularFlow

class CircularFlowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circular_flow)

        val mCircularFlow = findViewById<CircularFlow>(R.id.circularFlow)

        findViewById<View>(R.id.view6).setOnClickListener {
            mCircularFlow.addViewToCircularFlow(
                it, 130, 160F
            )
            Toast.makeText(
                it.context,
                "addViewToCircularFlow - Radius: " + 130 + " Angle: " + 160,
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<View>(R.id.view7).setOnClickListener {
            mCircularFlow.addViewToCircularFlow(
                it, 140, 200F
            )
            Toast.makeText(
                it.context,
                "AddViewToCircularFlow - Radius: " + 140 + " Angle: " + 200,
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<View>(R.id.view8).setOnClickListener {
            mCircularFlow.addViewToCircularFlow(
                it, 150, 240F
            )
            Toast.makeText(
                it.context,
                "addViewToCircularFlow - Radius: " + 150 + " Angle: " + 240,
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<View>(R.id.view2).setOnClickListener {
            mCircularFlow.updateAngle(
                it, 90F
            )
            Toast.makeText(it.context, "UpdateAngle Angle: " + 90, Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.view3).setOnClickListener {
            mCircularFlow.updateRadius(
                it, 150
            )
            Toast.makeText(it.context, "UpdateRadius Radius: " + 150, Toast.LENGTH_SHORT).show()

        }

        findViewById<View>(R.id.view4).setOnClickListener {
            mCircularFlow.updateReference(
                it, 160, 135F
            )
            Toast.makeText(
                it.context,
                "UpdateReference - Radius: " + 160 + " Angle: " + 135,
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<View>(R.id.view5).setOnClickListener {
            mCircularFlow.removeView(
                it
            )
        }
    }
}