package com.bano.futuresspreadcalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bano.futuresspreadcalculator.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var kcRate: Int = 10

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.incButton.setOnClickListener {
            kcRate += 1
            binding.KCTV.text = "КС: $kcRate%"
        }
        binding.decButton.setOnClickListener {
            kcRate -= 1
            if (kcRate >= 0) binding.KCTV.text = "КС: $kcRate%"
            else kcRate = 0
        }

        binding.calculateButton.setOnClickListener {
            if (binding.textView1.text.isNotEmpty() &&
                binding.editText2.text.isNotEmpty() &&
                binding.editText3.text.isNotEmpty() &&
                binding.editText4.text.isNotEmpty()
            ) {
                val activPrice = binding.editText1.text.toString().toInt()
                val futurePrice = binding.editText2.text.toString().toInt()
                val dividend = binding.editText3.text.toString().toInt()
                val daysToExpire = calculateDaysUntil(binding.editText4.text.toString())
                val kcRateToCalc = "0.$kcRate".toFloat()

                //Актив без дивиденда
                val activPriceNoDiv:Int = (activPrice - dividend)
                binding.textView1.text = "Актив без дивиденда:\n$activPriceNoDiv"


                //Рассчетная цена фьючерса
                val futureCalculated:Double = activPriceNoDiv * (1 + kcRateToCalc * (daysToExpire.toDouble() / 365))
                binding.textView2.text = "Рассчетная цена фьючерса:\n${"%.2f".format(futureCalculated)}"

            } else Toast.makeText(this, "Введите все данные", LENGTH_SHORT).show()

        }


    }

    private fun calculateDaysUntil(inputDate: String): Long {
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

        try {
            val targetDate: Date = dateFormat.parse(inputDate)
                ?: throw IllegalArgumentException("Неправильный формат даты")

            val currentDate = Date()
            val differenceInMillis = targetDate.time - currentDate.time
            val differenceInDays = TimeUnit.DAYS.convert(differenceInMillis, TimeUnit.MILLISECONDS)

            return differenceInDays
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка: ${e.message}", LENGTH_SHORT).show()
        }
        return -1
    }
}