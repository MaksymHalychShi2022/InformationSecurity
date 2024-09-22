package com.example.informationsecurity.ui.lab1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.informationsecurity.databinding.FragmentLab1Binding

class Lab1Fragment : Fragment() {

    private var _binding: FragmentLab1Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lab1ViewModel =
            ViewModelProvider(this).get(Lab1ViewModel::class.java)

        _binding = FragmentLab1Binding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnGenerate.setOnClickListener {
            Toast.makeText(context, "Hello there!", Toast.LENGTH_SHORT).show()
        }

        binding.btnEstimatePi.setOnClickListener {
            Toast.makeText(context, "Hello there!", Toast.LENGTH_SHORT).show()
        }

        lab1ViewModel.output.observe(viewLifecycleOwner) {
            binding.tvOutput.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}