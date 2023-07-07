package zs.android.module.java8.newapi

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import zs.android.module.java8.BuildConfig
import zs.android.module.java8.MainActivity
import zs.android.module.java8.R
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.zone.ZoneRules
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class NewDateApiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_date_api)
        clock()
        timezones()
        localTime()
        localDate()
        localDateTime()
    }

    /**
     * Clock
     * 提供对当前日期和时间的访问。我们可以利用它来替代 System.currentTimeMillis() 方法。
     * 另外，通过 clock.instant() 能够获取一个 instant 实例，
     * 此实例能够方便地转换成老版本中的 java.util.Date 对象
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun clock() {
        val clock = Clock.systemDefaultZone()
        val millis = clock.millis()
        val instant = clock.instant()
        val legacyDate = Date.from(instant) // 老版本 java.util.Date
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "clock: $millis, $legacyDate")
        }
    }

    /**
     * Timezones 时区
     * ZoneId 代表时区类。通过静态工厂方法方便地获取它，入参我们可以传入某个时区编码。
     * 另外，时区类还定义了一个偏移量，用来在当前时刻或某时间 与目标时区时间之间进行转换。
     */
    private fun timezones() {
        val zone1 = ZoneId.of("Europe/Berlin")
        val zone2 = ZoneId.of("Brazil/East")
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "timezones: ${ZoneId.getAvailableZoneIds()}")
            Log.i("print_logs", "rules: ${zone1.rules}, ${zone2.rules}")
        }
    }

    /**
     * LocalTime
     * 表示一个没有指定时区的时间类，
     * 例如，10 p.m.或者 17：30:15，下面示例代码中，将会使用上面创建的 时区对象创建两个 LocalTime。
     * 然后我们会比较两个时间，并计算它们之间的小时和分钟的不同。
     */
    private fun localTime() {
        val zone1 = ZoneId.of("Europe/Berlin")
        val zone2 = ZoneId.of("Brazil/East")

        val now1 = LocalTime.now(zone1)
        val now2 = LocalTime.now(zone2)
        Log.i("print_logs", "localTime.now ${now1.isBefore(now2)}")

        val hoursBetween = ChronoUnit.HOURS.between(now1, now2)
        val minutesBetween = ChronoUnit.MINUTES.between(now1, now2)
        Log.i("print_logs", "localTime.hours: $hoursBetween, minutes：$minutesBetween")

        val late = LocalTime.of(23, 59, 59)
        Log.i("print_logs", "localTime.of: $late")

        val germanFormatter =
            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.GERMAN)
        val localTime = LocalTime.parse("13:37", germanFormatter)
        Log.i("print_logs", "localTime: $localTime")
    }

    /**
     * LocalDate
     * 是一个日期对象，
     * 例如：2014-03-11。它和 LocalTime 一样是个 final 类型对象。下面的例子演示了如何通过加减日，月，年等来计算一个新的日期。
     *
     * 也可以直接解析日期字符串，生成 LocalDate 实例。（和 LocalTime 操作一样简单）
     */
    private fun localDate() {
        Log.i("print_logs", "LocalTime.now: ${LocalDate.now()}")
        Log.i("print_logs", "明天: ${LocalDate.now().plus(1, ChronoUnit.DAYS)}")
        Log.i("print_logs", "昨天: ${LocalDate.now().plus(1, ChronoUnit.DAYS).minusDays(2)}")

        val independenceDay = LocalDate.of(2014, Month.JULY, 4)
        val dayOfWeek = independenceDay.dayOfWeek
        Log.i("print_logs", "计算周几：$dayOfWeek ")


        val germanFormatter =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.GERMAN)
        val xmas = LocalDate.parse("24.12.2014", germanFormatter)
        Log.i("print_logs", "日期字符串: $xmas")
    }

    /**
     * LocalDateTime
     * 是一个日期-时间对象。你也可以将其看成是 LocalDate 和 LocalTime 的结合体。操作上，也大致相同。
     * LocalDateTime 同样是一个 final 类型对象。
     *
     * 如果再加上的时区信息，LocalDateTime 还能够被转换成 Instance 实例。Instance 能够被转换成老版本中 java.util.Date 对象
     */
    private fun localDateTime() {
        val sylvester = LocalDateTime.of(2014, Month.DECEMBER, 31, 23, 59, 59)
        val dayOfWeek = sylvester.dayOfWeek
        val dayOfMonth = sylvester.dayOfMonth
        val minuteOfDay = sylvester.getLong(ChronoField.MINUTE_OF_DAY)
        Log.i(
            "print_logs",
            "localDateTime.dayOfWeek: $dayOfWeek, $dayOfMonth, 当前第 $minuteOfDay 分钟"
        )


        //格式化 LocalDateTime 对象就和格式化 LocalDate 或者 LocalTime 一样。除了使用预定义的格式以外，也可以自定义格式化输出。
        val instant = sylvester.atZone(ZoneId.systemDefault()).toInstant()
        val legacyDate = Date.from(instant)
        Log.i("print_logs", "instant to Date: $legacyDate")

        // FIXME: Nov 是按照英系来计算的，在formatter的时候，JVM会自动加载出自己的时区，并按照此时区进行字符串对比，所以将最开始的Nov换成11月就可以了，
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm").withLocale(Locale.ENGLISH)
        val parsed = LocalDateTime.parse("Nov 03, 2014 - 07:13", formatter)
        val string = formatter.format(parsed)
        Log.i("print_logs", "localDateTime: $string")

    }


    override fun onResume() {
        super.onResume()
        setResult(RESULT_OK, Intent().apply {
            putExtra(MainActivity.RESULT_VALUE, this@NewDateApiActivity::class.java.simpleName)
        })
    }

}