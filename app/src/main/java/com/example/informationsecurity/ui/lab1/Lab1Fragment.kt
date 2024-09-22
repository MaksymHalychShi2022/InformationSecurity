package com.example.informationsecurity.ui.lab1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.informationsecurity.databinding.FragmentLab1Binding
import com.example.informationsecurity.utils.LehmerRandomNumberGenerator

class Lab1Fragment : Fragment() {

    private var _binding: FragmentLab1Binding? = null
    private lateinit var viewModel: Lab1ViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(Lab1ViewModel::class.java)

        _binding = FragmentLab1Binding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnGenerate.setOnClickListener {
            val n: Long = binding.etHowManyNumbersToGenerate.text.toString().toLong()
            val generator = LehmerRandomNumberGenerator()
            val generatedNumbers = generator.generateSequence(n)
            viewModel.updateOutput(generatedNumbers.joinToString("\n"))
        }

        binding.btnEstimatePi.setOnClickListener {
            Toast.makeText(context, "Hello there!", Toast.LENGTH_SHORT).show()
        }

        viewModel.output.observe(viewLifecycleOwner) {
            binding.tvOutput.text = it
        }
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}