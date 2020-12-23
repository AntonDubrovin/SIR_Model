package com.example.sir_model

import android.graphics.Color
import android.os.Bundle
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation
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
    private var timef = 0
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
        board.visibility = View.INVISIBLE
        instance = this
        s.visibility = View.GONE
        restart.visibility = View.GONE
        pause.visibility = View.GONE
        s.viewport.setScrollableY(true)
    }

    fun start(view: View) {
        restart.visibility = View.VISIBLE
        pause.visibility = View.VISIBLE
        board.visibility = View.VISIBLE
        information.visibility = View.INVISIBLE
        start.visibility = View.VISIBLE
        s.visibility = View.INVISIBLE
        resume.visibility = View.INVISIBLE
        drr = 1
        if (flag) {
            dim = dimension.text.toString().toInt()
            if(dim > 40){
                dim = 40
            } else if (dim < 5){
                dim = 5
            }
            bet = betta.text.toString().toFloat()
            if(bet - 1.0f > 0.0000005f){
                bet = 1.0f
            } else if (bet < 0.0f){
                bet = 0.00001f
            }
            gam = gamma.text.toString().toFloat()
            if(gam - 1.0f > 0.0000005f){
                gam = 1.0f
            } else if (gam <= 0.0f){
                gam = 0.00001f
            }
            pop = population.text.toString().toInt()
            if(pop > dim*dim){
                pop = dim*dim
            } else if (pop < 0){
                pop = 1
            }
            flag = false
            initialize()
        }
    }

    fun restart(view: View) {
        restart.visibility = View.GONE
        pause.visibility = View.GONE
        board.visibility = View.INVISIBLE
        information.visibility = View.VISIBLE
        drr = 0
        flag = true
        list = ArrayList()
        epidemic = true
        length = 0
        r = 0
        timef = 0
        s.removeAllSeries()
        s.visibility = View.GONE
        svalues = ArrayList()
        rvalues = ArrayList()
        ivalues = ArrayList()
        graphics.visibility = View.INVISIBLE
    }

    fun pause(view: View) {
        drr = 0
        start.visibility = View.INVISIBLE
        resume.visibility = View.VISIBLE
    }

    fun seeGraphics(view: View) {
        s.visibility = View.VISIBLE
        board.visibility = View.INVISIBLE
        s.viewport.isXAxisBoundsManual = true
        s.viewport.setMinX(0.0)
        s.viewport.setMaxX(timef.toDouble())
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

    fun information(view: View) {
        board.visibility = View.INVISIBLE

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
            current = Triple(current.first, current.second, 1)
            list.add(current)
        }
    }

    fun move() {
        timef++
        length++
        for (i in list) {
            val chance = Random.nextFloat()
            if (chance < 0.5) {
                var flag = true
                var cnt = 0
                while (flag) {
                    var p = 0
                    val dx = Random.nextInt(0, 3) - 1
                    val dy = Random.nextInt(0, 3) - 1
                    if (i.first + dx >= 1 && i.first + dx <= dim && i.second + dy >= 1 && i.second + dy <= dim) {
                        var f = false
                        for (j in -1..1) {
                            if (list.contains(Triple(i.first + dx, i.second + dy, j))) {
                                f = true
                            }
                        }
                        if (!f) {
                            list[list.indexOf(i)] = Triple(i.first + dx, i.second + dy, i.third)
                            flag = false
                            p = 1
                        }
                    }
                    cnt++
                    if (cnt == 5 && p == 0) {
                        flag = false
                        list[list.indexOf(i)] = Triple(i.first, i.second, i.third)
                    }
                }
            } else {
                list[list.indexOf(i)] = Triple(i.first, i.second, i.third)
            }
        }
        contact()
    }


    private fun contact() {
        /*val mark = Array(pop) { 0 }
        for (i in list) {
            var infect = 0f
            if (i.third > 0 && mark[list.indexOf(i)] == 0) {
                cnt++
                for (x in -1..1) {
                    for (y in -1..1) {
                        if (list.contains(Triple(i.first + x, i.second + y, 0))) {
                            if(infect < 1f/bet) {
                                list[list.indexOf(Triple(i.first + x, i.second + y, 0))] =
                                    Triple(i.first + x, i.second + y, days())
                                mark[list.indexOf(Triple(i.first + x, i.second + y, days()))] = 1
                                infect +=1
                            }
                        }

                    }
                }
            }
        }*/
        var imm = 0
        var scnt = 0
        var ill = 0
        for (i in list) {
            if (i.third == 0) {
                scnt++
            } else if (i.third > 0) {
                ill++
            } else {
                imm++
            }
        }

        var infects: Int = (ill.toFloat() * bet * scnt.toFloat() / pop.toFloat()).toInt()
        for (i in list) {
            if (i.third == 0 && infects > 0) {
                list[list.indexOf(i)] = Triple(i.first, i.second, 1)
                infects--
            }
        }

        var rescue: Int = (ill.toFloat() * gam).toInt()
        if (rescue == 0) {
            rescue = 1
        }
        for (i in list) {
            if (i.third == 1 && rescue > 0) {
                list[list.indexOf(i)] = Triple(i.first, i.second, -1)
                rescue--
            }
        }
        imm = 0
        scnt = 0
        ill = 0
        for (i in list) {
            if (i.third == 0) {
                scnt++
            } else if (i.third > 0) {
                ill++
            } else {
                imm++
            }
        }

        svalues.add(DataPoint(timef.toDouble(), scnt.toDouble()))
        ivalues.add(DataPoint(timef.toDouble(), ill.toDouble()))
        rvalues.add(DataPoint(timef.toDouble(), imm.toDouble()))

        if (ill == 0 || timef == 100) {
            epidemic = false
            val sicknt = pop - imm
            Toast.makeText(
                this,
                "Ended in $length days\n Immune - $imm \n No sick - $sicknt",
                Toast.LENGTH_LONG
            ).show()
            pause.visibility = View.INVISIBLE
            graphics.visibility = View.VISIBLE
        }
    }
}