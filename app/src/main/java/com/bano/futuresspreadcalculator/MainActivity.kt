package com.bano.futuresspreadcalculator

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.bano.futuresspreadcalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var kcRate: Double = 21.0
    private var KCstep: Double = 1.0
    val calculator = Calculator()

    private val PREFS_NAME = "MyAppPrefs"
    private val KEY_KC_RATE = "kc_rate"
    private val KEY_ACTIV_PRICE_NO_DIV = "activ_price_no_div"
    private val KEY_FUTURE_PRICE = "future_price"
    private val KEY_BACK_FUTURE = "back_future_price"
    private val KEY_DEVIATION = "deviation"
    private val KEY_CONDITION = "market_condition"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadData()

        binding.incButton.setOnClickListener {
            kcRate += KCstep
            binding.KCTV.text = "КС: $kcRate%"
            saveData()
        }
        binding.decButton.setOnClickListener {
            kcRate -= KCstep
            if (kcRate >= 0) binding.KCTV.text = "КС: $kcRate%"
            else kcRate = 0.0
            saveData()
        }

        setupSpinner()

        binding.calculateButton.setOnClickListener {
            if (isInputsValid()) {
                val activPrice = binding.editText1.text.toString().toDouble()
                val futurePrice = binding.editText2.text.toString().toDouble()
                val dividend = binding.editText3.text.toString().toDouble()
                val daysToExpire = calculator.calculateDaysUntil(binding.editText4.text.toString())
                val kcRateToCalc = kcRate / 100

                val activPriceNoDiv: Double = calculator.calculateActivPriceNoDiv(activPrice, dividend)
                binding.textView1.text = "Актив без дивиденда:\n$activPriceNoDiv"

                val futureCalculated: Double =
                    calculator.calculateFuture(activPriceNoDiv, kcRateToCalc, daysToExpire)
                binding.textView2.text =
                    "Рассчетная цена фьючерса:\n${"%.2f".format(futureCalculated)}"

                val backFutureCalculated: Double =
                    calculator.calculateBackFuture(futurePrice, dividend, kcRateToCalc, daysToExpire)
                binding.textView3.text = "Обратный рассчет:\n${"%.2f".format(backFutureCalculated)}"

                val deviation = backFutureCalculated - activPriceNoDiv
                binding.textView4.text = "Отклонение:\n${"%.2f".format(deviation)}"

                displayMarketCondition(futureCalculated, futurePrice)
                saveData()

            } else Toast.makeText(this, "Введите все данные", LENGTH_SHORT).show()
        }
    }


    private fun saveData() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putFloat(KEY_KC_RATE, kcRate.toFloat())
            putString(KEY_ACTIV_PRICE_NO_DIV, binding.textView1.text.toString())
            putString(KEY_FUTURE_PRICE, binding.textView2.text.toString())
            putString(KEY_BACK_FUTURE, binding.textView3.text.toString())
            putString(KEY_DEVIATION, binding.textView4.text.toString())
            putString(KEY_CONDITION, binding.textView5.text.toString())
            apply()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadData() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        kcRate = sharedPreferences.getFloat(KEY_KC_RATE, 21.0f).toDouble()
        binding.KCTV.text = "КС: $kcRate%"
        binding.textView1.text =
            sharedPreferences.getString(KEY_ACTIV_PRICE_NO_DIV, "Актив без дивиденда:\n0")
                ?: "Актив без дивиденда:\n0"
        binding.textView2.text =
            sharedPreferences.getString(KEY_FUTURE_PRICE, "Рассчетная цена фьючерса:\n0")
                ?: "Рассчетная цена фьючерса:\n0"
        binding.textView3.text =
            sharedPreferences.getString(KEY_BACK_FUTURE, "Обратный рассчет:\n0")
                ?: "Обратный рассчет:\n0"
        binding.textView4.text =
            sharedPreferences.getString(KEY_DEVIATION, "Отклонение:\n0") ?: "Отклонение:\n0"
        binding.textView5.text =
            sharedPreferences.getString(KEY_CONDITION, "Контанго/бэквардация:\n0")
                ?: "Контанго/бэквардация:\n0"
    }

    @SuppressLint("SetTextI18n")
    private fun displayMarketCondition(futureCalculated: Double, futurePrice: Double) {
        val bkValue = futurePrice - futureCalculated
        val condition = if (futureCalculated > futurePrice) "Бэквардация" else "Контанго"
        val percentOfDeviation = "%.2f".format((bkValue / futureCalculated) * 100)

        binding.textView5.text = "$condition: ${"%.2f".format(bkValue)} \n$percentOfDeviation%"
    }

    private fun isInputsValid(): Boolean {
        return listOf(
            binding.editText1,
            binding.editText2,
            binding.editText3,
            binding.editText4
        ).all { it.text.isNotEmpty() }
    }

    private fun setupSpinner() {
        val items = arrayOf(1.0, 0.5, 0.25)
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, items)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.stepSpinner.adapter = adapter
        binding.stepSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                KCstep = selectedItem.toDouble()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                KCstep = 1.0
            }
        }
    }
}