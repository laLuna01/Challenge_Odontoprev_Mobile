package com.example.cp1_mobile.fragments

import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.cp1_mobile.MainActivity
import com.example.cp1_mobile.R
import com.example.cp1_mobile.databinding.FragmentMedicineBinding
import com.example.cp1_mobile.model.Medicine
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar

class MedicineFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var medicinesRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentMedicineBinding
    private var medicines = mutableListOf<Medicine>()
    private var medicineTimeEditText: EditText? = null
    private var medicineNameEditText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMedicineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        getMedicines()

        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE

        binding.addMedicineButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())

            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.add_medicine_dialog, null)

            builder.setView(dialogView)

            medicineNameEditText = dialogView.findViewById(R.id.medicineNameEditText)
            medicineTimeEditText = dialogView.findViewById(R.id.medicineTimeEditText)

            medicineTimeEditText?.setOnClickListener {
                val calendar = Calendar.getInstance()
                val timePicker = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    medicineTimeEditText?.setText(SimpleDateFormat("HH:mm").format(calendar.time))
                }
                TimePickerDialog(requireContext(), timePicker, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(
                    Calendar.MINUTE), true).show()
            }

            builder.setTitle("Adicionar Novo Remédio")

            builder.setPositiveButton("Salvar") { dialog, _ ->
                val medicineName = medicineNameEditText?.text.toString()
                val medicineTime = medicineTimeEditText?.text.toString()

                if (medicineName.isNotEmpty() && medicineTime.isNotEmpty()) {
                    medicines.add(Medicine(medicineName, medicineTime))
                    medicinesRef.setValue(medicines)
                    updateMedicineList()
                } else {
                    Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun getMedicines() {
        medicines.clear()
        medicinesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (medicineSnapshot in snapshot.children) {
                        val medicine = medicineSnapshot.getValue(Medicine::class.java)
                        if (medicine != null) {
                            medicines.add(medicine)
                        }
                    }
                    println(medicines)
                    medicines.forEach {
                        println("Medicamento: ${it.name}, Hora: ${it.time}")
                    }
                    updateMedicineList()
                } else {
                    println("Nenhum medicamento encontrado.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Erro ao buscar os dados: ${error.message}")
            }

        })
    }

    private fun updateMedicineList() {
        binding.medicinesList.removeAllViews()
        medicines.forEach { medicine ->
            val textView = TextView(requireContext())
            textView.text = "${medicine.name} - ${medicine.time}"
            textView.textSize = 18f
            textView.setTextColor(Color.parseColor("#FF6A00"))
            textView.setPadding(4, 0, 4, 15)
            binding.medicinesList.addView(textView)
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://cuidadoplus-97add-default-rtdb.firebaseio.com/")
        val userEmail = auth.currentUser?.email?.replace(".", "_")
        if (userEmail != null) {
            medicinesRef = database.child("users").child(userEmail).child("medicines")
        }
    }

}