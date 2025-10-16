package com.example.wastefuelcalculator

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.round

// Класс, описывающий склад палива
data class FuelProperties(
    val qValue: Double,
    val ash: Double,
    val alpha: Double,
    val moist: Double? = null
)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val coalInput = findViewById<EditText>(R.id.inputCoal)
        val oilInput = findViewById<EditText>(R.id.inputOil)
        val gasInput = findViewById<EditText>(R.id.inputGas)
        val resultLabel = findViewById<TextView>(R.id.resultText)
        val calculateBtn = findViewById<Button>(R.id.calcButton)

        calculateBtn.setOnClickListener {
            try {
                // Считування даних, введених користувачем
                val coal = coalInput.text.toString().toDouble()
                val oil = oilInput.text.toString().toDouble()
                val gas = gasInput.text.toString().toDouble()

                // Заданные характеристики для разных видов палива
                val coalProps = FuelProperties(20.47, 25.20, 0.8, 1.5)
                val oilProps = FuelProperties(40.40, 0.15, 1.0, 0.0)
                val gasProps = FuelProperties(0.0, 0.0, 0.0, 0.0)

                val efficiency = 0.985

                // Расчёт показників емісії (г/ГДж)
                val emissionCoal = emissionRate(coalProps, efficiency)
                val emissionOil = emissionRate(oilProps, efficiency)
                val emissionGas = 0.0

                // Расчёт валових викидів (т)
                val grossCoal = totalEmission(emissionCoal, coalProps.qValue, coal)
                val grossOil = totalEmission(emissionOil, oilProps.qValue, oil)
                val grossGas = totalEmission(emissionGas, gasProps.qValue, gas)

                resultLabel.text = buildString {
                    appendLine("~> Показник емісії ТЧ при спалюванні вугілля: ${"%.2f".format(emissionCoal)} г/ГДж")
                    appendLine("-> Валовий викид вугілля: ${"%.2f".format(grossCoal)} т\n")

                    appendLine("~> Показник емісії ТЧ при спалюванні мазуту: ${"%.2f".format(emissionOil)} г/ГДж")
                    appendLine("-> Валовий викид мазуту: ${"%.2f".format(grossOil)} т\n")

                    appendLine("~> Показник емісії ТЧ при спалюванні газу: ${"%.2f".format(emissionGas)} г/ГДж")
                    appendLine("-> Валовий викид газу: ${"%.2f".format(grossGas)} т")
                }

            } catch (_: Exception) {
                Toast.makeText(this, "Перевірте введені дані", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Функція обчислення валового викиду (т)
    private fun totalEmission(rate: Double, q: Double, b: Double): Double =
        (rate * q * b) / 1_000_000

    // Функція визначення показника емісії ТЧ (г/ГДж)
    private fun emissionRate(props: FuelProperties, n: Double): Double {
        val m = props.moist ?: 0.0
        return (1_000_000 / props.qValue) * props.alpha * (props.ash / (100 - m)) * (1 - n)
    }
}
