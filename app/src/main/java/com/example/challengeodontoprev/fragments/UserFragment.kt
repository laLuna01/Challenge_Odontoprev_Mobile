package com.example.challengeodontoprev.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.challengeodontoprev.MainActivity
import com.example.challengeodontoprev.R
import com.example.challengeodontoprev.databinding.FragmentUserBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentUserBinding
    private lateinit var database: DatabaseReference
    private lateinit var nameRef: DatabaseReference
    private lateinit var userName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getUserName()

        (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE

        binding.signOutButton.setOnClickListener {
            auth.signOut()
            navController.navigate(R.id.action_userFragment_to_splashFragment)
            (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
        }
    }

    private fun getUserName() {
        nameRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.getValue(String::class.java)
                userName?.let {
                    Log.d("Nome do Usuário", it)
                    binding.userNameText.setText(userName)
                } ?: run {
                    Log.e("Erro", "Nome não encontrado")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Erro ao buscar os dados: ${error.message}")
            }

        })
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://challenge-odontoprev-default-rtdb.firebaseio.com/")
        val userEmail = auth.currentUser?.email?.replace(".", "_")
        if (userEmail != null) {
            nameRef = database.child("users").child(userEmail).child("name")
        }
    }

}