package com.example.informationsecurity.ui.fragments.lab1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.informationsecurity.R
import com.example.informationsecurity.databinding.FragmentLab1Binding
import com.example.informationsecurity.utils.LehmerRandomNumberGenerator

class Lab1Fragment : Fragment() {

    private var _binding: FragmentLab1Binding? = null
    private lateinit var viewModel: Lab1ViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Register the file picker
    private val createFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.also { uri ->
                    saveFile(uri, viewModel.generatedNumbers.value ?: "")
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(Lab1ViewModel::class.java)

        _binding = FragmentLab1Binding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnGenerate.setOnClickListener {
            val lengthOfSequence: Long? =
                binding.etHowManyNumbersToGenerate.text.toString().toLongOrNull()
            if (lengthOfSequence != null && lengthOfSequence > 0) {
                val generator = LehmerRandomNumberGenerator()
                val generatedNumbers = generator.generateSequence(lengthOfSequence)
                viewModel.updateGeneratedNumbers(generatedNumbers.joinToString("\n"))

                Toast.makeText(context, "Generated!", Toast.LENGTH_LONG).show()
                Log.d("Period", "Period of generated sequence: ${generator.getPeriod()}")
            } else {
                Toast.makeText(context, "Invalid input!", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnSaveToFile.setOnClickListener {
            openSaveFileDialog()
        }

        binding.btnEstimatePi.setOnClickListener {
            findNavController().navigate(R.id.action_nav_lab1_to_estimatePiFragment)
        }

        viewModel.generatedNumbers.observe(viewLifecycleOwner) {
            binding.tvOutput.text = it
        }
        return root
    }

    // Open the file picker dialog to save the file
    private fun openSaveFileDialog() {

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain" // You can specify other types like "application/json" for JSON
            putExtra(Intent.EXTRA_TITLE, "random_numbers.txt") // Default filename
        }
        createFileLauncher.launch(intent)
    }

    // Save the file to the chosen location
    private fun saveFile(uri: Uri, content: String) {
        try {
            // Use requireContext() to get the context from the Fragment
            requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }
            Toast.makeText(requireContext(), "File saved successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to save file!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}