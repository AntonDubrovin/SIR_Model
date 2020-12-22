package com.example.sir_model

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    var drr: Int = 0
    var dim: Int = 10
    var epidemic = true
    private var time = 0
    private var r = 0
    private var length = 0
    private var bet: Float = 0f
    private var gam: Float = 0f
    private var pop: Int = 0
    private var flag = true
    private var svalues: ArrayList<DataPoint> = ArrayList()
    private var rvalues: ArrayList<DataPoint> = ArrayList()
    private var ivalues: ArrayList<DataPoint> = ArrayList()

    var list: ArrayList<Triple<Int, Int, Int>> = ArrayList()

    companion object {
        lateinit var instance: MainActivity
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        instance = this
        s.visibility = View.GONE
        s.viewport.setScrollableY(true)
    }

    fun start(view: View) {
        drr = 1
        if (flag) {
            dim = dimension.text.toString().toInt()
            bet = betta.text.toString().toFloat()
            gam = gamma.text.toString().toFloat()
            pop = population.text.toString().toInt()
            flag = false
            initialize()
        }
    }

    fun restart(view: View) {
        drr = 0
        flag = true
        list = ArrayList()
        epidemic = true
        length = 0
        r = 0
        time = 0
        s.removeAllSeries()
        s.visibility = View.GONE
        board.visibility = View.VISIBLE
        svalues = ArrayList()
        rvalues = ArrayList()
        ivalues = ArrayList()
    }

    fun pause(view: View) {
        drr = 0
    }

    private fun initialize() {
        for (i in 1..pop) {
            var row = Random.nextInt(1, dim + 1)
            var column = Random.nextInt(1, dim + 1)
            var p: Triple<Int, Int, Int> = Triple(row, column, 0)
            while (list.contains(p)) {
                row = Random.nextInt(1, dim + 1)
                column = Random.nextInt(1, dim + 1)
                p = Triple(row, column, 0)
            }
            list.add(p)
        }
        plague()
    }

    private fun days(): Int {
        val days: Float = 1 / gam
        return days.toInt()
    }

    private fun plague() {
        var cnt = 1
        if (pop / 20 > cnt) {
            cnt = pop / 20
        }
        ivalues.add(DataPoint(0.0, cnt.toDouble()))
        svalues.add(DataPoint(0.0, pop - cnt.toDouble()))
        rvalues.add(DataPoint(0.0, r.toDouble()))

        for (i in 1..cnt) {
            var current = list[0]
            list.remove(current)
            current = Triple(current.first, current.second, days())
            list.add(current)
        }
    }

    fun move() {
        time++
        length++
        for (i in list) {
            var time = i.third
            if (time > 0) {
                time--
                if (time == 0) {
                    time = -1
                    r++
                }
            }
            val chance = Random.nextFloat()
            if (chance < bet) {
                var flag = true
                var cnt = 0
                while (flag) {
                    var p = 0
                    val dx = Random.nextInt(0, 3) - 1
                    val dy = Random.nextInt(0, 3) - 1
                    if (i.first + dx >= 1 && i.first + dx <= dim && i.second + dy >= 1 && i.second + dy <= dim) {
                        var f = false
                        for (j in -1..days()) {
                            if (list.contains(Triple(i.first + dx, i.second + dy, j))) {
                                f = true
                            }
                        }
                        if (!f) {
                            list[list.indexOf(i)] = Triple(i.first + dx, i.second + dy, time)
                            flag = false
                            p = 1
                        }
                    }
                    cnt++
                    if (cnt == 5 && p == 0) {
                        flag = false
                        list[list.indexOf(i)] = Triple(i.first, i.second, time)
                    }
                }
            } else {
                list[list.indexOf(i)] = Triple(i.first, i.second, time)
            }
        }
        rvalues.add(DataPoint(time.toDouble(), r.toDouble()))
        contact()
    }


    private fun contact() {
        val mark = Array(pop) { 0 }
        var cnt = 0
        var imm = 0
        for (i in list) {
            if (i.third > 0 && mark[list.indexOf(i)] == 0) {
                cnt++
                for (x in -1..1) {
                    for (y in -1..1) {
                        if (list.contains(Triple(i.first + x, i.second + y, 0))) {
                            list[list.indexOf(Triple(i.first + x, i.second + y, 0))] =
                                Triple(i.first + x, i.second + y, days())
                            mark[list.indexOf(Triple(i.first + x, i.second + y, days()))] = 1
                        }
                    }
                }
            }
        }
        for (i in list) {
            if (i.third < 0) {
                imm++
            }
        }
        var scnt = 0
        var ill = 0
        for (i in list) {
            if (i.third == 0) {
                scnt++
            } else if (i.third > 0) {
                ill++
            }
        }
        svalues.add(DataPoint(time.toDouble(), scnt.toDouble()))
        ivalues.add(DataPoint(time.toDouble(), ill.toDouble()))

        if (cnt == 0) {
            epidemic = false
            val sicknt = pop - imm
            Toast.makeText(
                this,
                "Ended in $length days\n Immune - $imm \n No sick - $sicknt",
                Toast.LENGTH_LONG
            ).show()
            s.visibility = View.VISIBLE
            board.visibility = View.INVISIBLE
            s.viewport.isXAxisBoundsManual = true
            s.viewport.setMinX(0.0)
            s.viewport.setMaxX(time.toDouble())
            val sser = LineGraphSeries(svalues.toTypedArray())
            val rser = LineGraphSeries(rvalues.toTypedArray())
            val iser = LineGraphSeries(ivalues.toTypedArray())
            sser.color = Color.parseColor("#00FA9A")
            rser.color = Color.parseColor("#00BFFF")
            iser.color = Color.parseColor("#DC143C")
            s.addSeries(iser)
            s.addSeries(sser)
            s.addSeries(rser)
        }
    }
}