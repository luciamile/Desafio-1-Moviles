package com.example.calculadora

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private lateinit var etMontoTotal: EditText
    private lateinit var etNumPersonas: EditText
    private lateinit var rgPropina: RadioGroup
    private lateinit var rbOtro: RadioButton
    private lateinit var etPropinaPersonalizada: EditText
    private lateinit var swIncluirIVA: Switch
    private lateinit var tvResultados: TextView
    private lateinit var btnCalcular: Button
    private lateinit var btnLimpiar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        initViews()

        // Configurar listeners
        setupListeners()
    }

    private fun initViews() {
        etMontoTotal = findViewById(R.id.etMontoTotal)
        etNumPersonas = findViewById(R.id.etNumPersonas)
        rgPropina = findViewById(R.id.rgPropina)
        rbOtro = findViewById(R.id.rbOtro)
        etPropinaPersonalizada = findViewById(R.id.etPropinaPersonalizada)
        swIncluirIVA = findViewById(R.id.swIncluirIVA)
        tvResultados = findViewById(R.id.tvResultados)
        btnCalcular = findViewById(R.id.btnCalcular)
        btnLimpiar = findViewById(R.id.btnLimpiar)
    }

    private fun setupListeners() {
        // RadioGroup listener para mostrar/ocultar el campo de propina personalizada
        rgPropina.setOnCheckedChangeListener { _, checkedId ->
            etPropinaPersonalizada.visibility = if (checkedId == R.id.rbOtro) View.VISIBLE else View.GONE
        }

        // Botón Calcular
        btnCalcular.setOnClickListener {
            calcularPropina()
        }

        // Botón Limpiar
        btnLimpiar.setOnClickListener {
            limpiarCampos()
        }
    }

    private fun calcularPropina() {
        // Validar campos
        if (!validarCampos()) {
            return
        }

        // Obtener valores de los campos
        val montoTotal = etMontoTotal.text.toString().toDouble()
        val numPersonas = etNumPersonas.text.toString().toInt()
        val incluirIVA = swIncluirIVA.isChecked

        // Obtener porcentaje de propina
        val porcentajePropina = when (rgPropina.checkedRadioButtonId) {
            R.id.rb10 -> 10.0
            R.id.rb15 -> 15.0
            R.id.rb20 -> 20.0
            R.id.rbOtro -> etPropinaPersonalizada.text.toString().toDouble()
            else -> 0.0
        }

        // Calcular IVA si está activado
        val iva = if (incluirIVA) montoTotal * 0.16 else 0.0

        // Calcular propina
        val propina = montoTotal * porcentajePropina / 100

        // Calcular total a pagar
        val total = montoTotal + iva + propina

        // Calcular monto por persona
        val totalPorPersona = total / numPersonas

        // Formatear resultados
        val df = DecimalFormat("#,##0.00")
        val resultados = """
            ${getString(R.string.resultado_propina)}: $${df.format(propina)}
            ${getString(R.string.resultado_iva)}: $${df.format(iva)}
            ${getString(R.string.resultado_total)}: $${df.format(total)}
            ${getString(R.string.resultado_por_persona)}: $${df.format(totalPorPersona)}
        """.trimIndent()

        // Mostrar resultados
        tvResultados.text = resultados
    }

    private fun validarCampos(): Boolean {
        var isValid = true

        // Validar monto total
        if (TextUtils.isEmpty(etMontoTotal.text)) {
            etMontoTotal.error = getString(R.string.error_monto_vacio)
            isValid = false
        } else if (etMontoTotal.text.toString().toDouble() <= 0) {
            etMontoTotal.error = getString(R.string.error_monto_invalido)
            isValid = false
        }

        // Validar número de personas
        if (TextUtils.isEmpty(etNumPersonas.text)) {
            etNumPersonas.error = getString(R.string.error_personas_vacio)
            isValid = false
        } else if (etNumPersonas.text.toString().toInt() <= 0) {
            etNumPersonas.error = getString(R.string.error_personas_invalido)
            isValid = false
        }

        // Validar propina personalizada si está seleccionada
        if (rgPropina.checkedRadioButtonId == R.id.rbOtro &&
            TextUtils.isEmpty(etPropinaPersonalizada.text)) {
            etPropinaPersonalizada.error = getString(R.string.error_propina_vacia)
            isValid = false
        } else if (rgPropina.checkedRadioButtonId == R.id.rbOtro &&
            etPropinaPersonalizada.text.toString().toDouble() < 0) {
            etPropinaPersonalizada.error = getString(R.string.error_propina_invalida)
            isValid = false
        }

        return isValid
    }

    private fun limpiarCampos() {
        etMontoTotal.text.clear()
        etNumPersonas.text.clear()
        rgPropina.clearCheck()
        etPropinaPersonalizada.text.clear()
        etPropinaPersonalizada.visibility = View.GONE
        swIncluirIVA.isChecked = false
        tvResultados.text = ""

        // Limpiar errores
        etMontoTotal.error = null
        etNumPersonas.error = null
        etPropinaPersonalizada.error = null
    }
}