package com.example.informationsecurity.ui.lab2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.informationsecurity.databinding.FragmentLab2Binding

class Lab2Fragment : Fragment() {

    private var _binding: FragmentLab2Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val lab2ViewModel = ViewModelProvider(this).get(Lab2ViewModel::class.java)

        _binding = FragmentLab2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnHash.setOnClickListener {
            val inputString = binding.etInputString.text.toString()
            if (inputString.isEmpty()) {
                Toast.makeText(requireContext(), "Empty String!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lab2ViewModel.md5(inputString)
        }

        binding.btnChooseFile.setOnClickListener {
            notImplemented()
        }

        binding.btnSaveOutputToFile.setOnClickListener {
            notImplemented()
        }

        lab2ViewModel.output.observe(viewLifecycleOwner) {
            binding.tvOutput.text = it
        }

        return root
    }

    private fun notImplemented() {
        Toast.makeText(requireContext(), "Not implemented!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}