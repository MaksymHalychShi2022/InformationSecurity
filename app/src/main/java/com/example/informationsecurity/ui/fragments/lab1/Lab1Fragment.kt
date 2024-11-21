package com.example.informationsecurity.ui.fragments.lab1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.informationsecurity.R
import com.example.informationsecurity.databinding.FragmentLab1Binding
import com.example.informationsecurity.ui.MainViewModel
import com.example.informationsecurity.utils.OperationState

class Lab1Fragment : Fragment() {

    private var _binding: FragmentLab1Binding? = null
    private val viewModel: Lab1ViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

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
        _binding = FragmentLab1Binding.inflate(inflater, container, false)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.lab1_option_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.estimate_pi -> {
                        findNavController().navigate(R.id.action_nav_lab1_to_estimatePiFragment)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.btnGenerate.setOnClickListener {
            val lengthOfSequence: Long? =
                binding.etHowManyNumbersToGenerate.text.toString().toLongOrNull()
            lengthOfSequence?.let { length ->
                if (length <= 0) {
                    Toast.makeText(context, "Invalid input!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                viewModel.generateRandomNumbers(length).observe(viewLifecycleOwner) {
                    observeForProgressBar(it, "Generated")
                }
            }
        }

        binding.outputGeneratedNumbers.btnSave.setOnClickListener {
            Toast.makeText(requireContext(), "Not implemented", Toast.LENGTH_SHORT).show()
        }

        binding.outputGeneratedNumbers.btnLoad.setOnClickListener {
            Toast.makeText(requireContext(), "Not implemented", Toast.LENGTH_SHORT).show()
        }

        viewModel.generatedNumbers.observe(viewLifecycleOwner) {
            binding.outputGeneratedNumbers.tvScrollableText.text = it
        }
        return binding.root
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

    private fun observeForProgressBar(result: OperationState<*>, successMassage: String? = null) {
        when (result) {
            is OperationState.Loading -> {
                mainViewModel.showProgressBar()
            }

            is OperationState.Success -> {
                successMassage?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
                mainViewModel.hideProgressBar()
            }

            is OperationState.Error -> {
                Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                mainViewModel.hideProgressBar()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}