package com.example.challengeodontoprev.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.challengeodontoprev.R
import com.example.challengeodontoprev.databinding.FragmentSignUpBinding
import com.example.challengeodontoprev.utils.RetrofitInstance
import com.example.challengeodontoprev.utils.ViaCepResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SignUpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var database: DatabaseReference
    private var inputCep: EditText? = null
    private var inputNumber: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        datePicker()
        adressPicker()

        binding.linkSignIn.setOnClickListener {
            navController.navigate(R.id.action_signUpFragment_to_signInFragment2)
        }
        binding.signUpButton.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val dayOfBirth = binding.editTextBirth.text.toString().trim()
            val cpf = binding.editTextCpf.text.toString().trim()
            val address = binding.editTextAddress.text.toString().trim()
            val gender = getSelectedGender()
            val password = binding.editTextPassword.text.toString().trim()
            val passwordCheck = binding.editTextPasswordCheck.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && dayOfBirth.isNotEmpty() && cpf.isNotEmpty() && address.isNotEmpty() && gender!!.isNotEmpty() && password.isNotEmpty() && passwordCheck.isNotEmpty()) {
                if (isValidEmail(email)) {
                    if (isValidCpf(cpf)) {
                        if (password == passwordCheck) {
                            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(requireContext(), "Cadastro bem-sucedido!", Toast.LENGTH_SHORT).show()

                                    val userEmail = email.replace(".", "_")
                                    database.child("users").child(userEmail).child("name").setValue(name)

                                    navController.navigate(R.id.action_signUpFragment_to_signInFragment2)
                                } else {
                                    Toast.makeText(requireContext(), it.exception?.message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "As senhas devem ser iguais", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Cpf inválido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Email inválido", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://challenge-odontoprev-default-rtdb.firebaseio.com/")
    }

    private fun datePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate(calendar)
        }

        binding.editTextBirth.setOnClickListener {
            DatePickerDialog(requireContext(), datePicker, calendar.get(Calendar.YEAR), calendar.get(
                Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun adressPicker() {
        binding.editTextAddress.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_cep, null)

            inputCep = dialogView.findViewById(R.id.editTextCEP)
            inputNumber = dialogView.findViewById(R.id.editTextNumber)

            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Buscar endereço")
                .setView(dialogView)
                .setPositiveButton("Enviar") { dialog, _ ->
                    val cep = inputCep?.text.toString().trim()
                    val number = inputNumber?.text.toString().trim()
                    if (cep.isNotEmpty()) {
                        if (number.isNotEmpty()) {
                            if (isCepValid(cep)) {
                                RetrofitInstance.service.getEndereco(formatCep(cep) ?: "")
                                    .enqueue(object : Callback<ViaCepResponse> {
                                        override fun onResponse(call: Call<ViaCepResponse>, response: Response<ViaCepResponse>) {
                                            if (response.isSuccessful) {
                                                val endereco = response.body()
                                                endereco?.let {
                                                    val enderecoCompleto = "${it.logradouro}, " + number +
                                                            " - ${it.bairro} - " +
                                                            "${it.localidade}/" +
                                                            it.uf

                                                    binding.editTextAddress.setText(enderecoCompleto)
                                                }
                                            } else {
                                                Toast.makeText(requireContext(), "CEP não encontrado", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onFailure(call: Call<ViaCepResponse>, t: Throwable) {
                                            Toast.makeText(requireContext(), "Erro ao buscar o CEP", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                            } else {
                                Toast.makeText(requireContext(), "CEP inválido", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Por favor, insira o número", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Por favor, insira um CEP", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .create()

            dialog.show()
        }
    }

    private fun updateDate(calendar: Calendar) {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, Locale.ROOT)
        binding.editTextBirth.setText(sdf.format(calendar.time))
    }

    fun getSelectedGender(): String? {
        val selectedGenderId = binding.radioGroupGender.checkedRadioButtonId
        return when (selectedGenderId) {
            R.id.radioMale -> "Masculino"
            R.id.radioFemale -> "Feminino"
            R.id.radioOther -> "Outro"
            else -> null
        }
    }

    fun formatCep(cep: String): String {
        return cep.replace(Regex("[^0-9]"), "")
            .let {
                when (it.length) {
                    in 1..5 -> it
                    in 6..7 -> it.substring(0, 5) + "-" + it.substring(5)
                    else -> it.substring(0, 5) + "-" + it.substring(5, 8)
                }
            }
    }

    private fun isCepValid(cep: String?): Boolean {
        val cepPattern = Regex("^[0-9]{8}$")
        return !cep.isNullOrBlank() && cep.matches(cepPattern)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
        return email.matches(emailRegex.toRegex())
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