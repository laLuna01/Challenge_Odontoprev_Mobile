package com.example.challengeodontoprev

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var editTextName: EditText? = null
    private var editTextBirth: EditText? = null
    private var editTextCpf: EditText? = null
    private var editTextPassword: EditText? = null
    private var editTextPasswordCheck: EditText? = null
    private var signUpButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextName = findViewById(R.id.editTextName)
        editTextBirth = findViewById(R.id.editTextBirth)
        editTextCpf = findViewById(R.id.editTextCpf)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextPasswordCheck = findViewById(R.id.editTextPasswordCheck)
        signUpButton = findViewById(R.id.signUpButton)

        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate(calendar)
        }

        editTextBirth?.setOnClickListener {
            DatePickerDialog(this, datePicker, calendar.get(Calendar.YEAR), calendar.get(
                Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        signUpButton?.setOnClickListener {
            val name = editTextName?.text.toString()
            val cpf = editTextCpf?.text.toString()
            val password = editTextPassword?.text.toString()
            val passwordCheck = editTextPasswordCheck?.text.toString()
            val dayOfBirth = editTextBirth?.text.toString()

            if (name.isNotEmpty() && cpf.isNotEmpty() && password.isNotEmpty() && passwordCheck.isNotEmpty() && dayOfBirth.isNotEmpty()) {
                if (!isValidCpf(cpf)) {
                    Toast.makeText(this, "CPF inválido", Toast.LENGTH_SHORT).show()
                } else if (password != passwordCheck) {
                    Toast.makeText(this, "As senhas devem ser iguais", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Cadastro bem-sucedido!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT)
                    .show()
            }

        }

    }

    private fun updateDate(calendar: Calendar) {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, Locale.ROOT)
        editTextBirth?.setText(sdf.format(calendar.time))
    }

    private fun isValidCpf(cpf: String): Boolean {
        val cleanedCPF = cpf.replace(Regex("[^\\d]"), "")

        if (cleanedCPF.length != 11 || cleanedCPF.all { it == cleanedCPF[0] }) {
            return false
        }

        fun calculateDigit(cpf: String, factor: Int): Int {
            var sum = 0
            cpf.forEachIndexed { index, c ->
                if (factor - index > 1) {
                    sum += (c.toString().toInt() * (factor - index))
                }
            }
            val result = (sum * 10) % 11
            return if (result == 10) 0 else result
        }

        val digit1 = calculateDigit(cleanedCPF, 10)
        val digit2 = calculateDigit(cleanedCPF, 11)

        return cleanedCPF[9].toString().toInt() == digit1 && cleanedCPF[10].toString().toInt() == digit2
    }

}