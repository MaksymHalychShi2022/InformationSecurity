package com.example.informationsecurity.ui.fragments.lab1

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
import com.example.informationsecurity.utils.FilePickerHandler

class Lab1Fragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val lab1ViewModel: Lab1ViewModel by viewModels()
    private var _binding: FragmentLab1Binding? = null

    private lateinit var filePickerHandler: FilePickerHandler

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filePickerHandler = FilePickerHandler(
            launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                filePickerHandler.handleResult(result.resultCode, result.data)
            }
        )
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
                mainViewModel.runWithProgress(
                    task = { lab1ViewModel.generateRandomNumbers(length) },
                    onSuccessMessage = "Generated!"
                )
            }
        }

        // Generated Numbers Output
        binding.outputGeneratedNumbers.tvLabel.text =
            requireContext().getString(R.string.generated_numbers)
        lab1ViewModel.generatedNumbers.observe(viewLifecycleOwner) {
            binding.outputGeneratedNumbers.tvScrollableText.text = it
        }

        binding.outputGeneratedNumbers.btnSave.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                mainViewModel.runWithProgress(
                    task = { lab1ViewModel.saveGeneratedNumbers(uri) },
                    onSuccessMessage = "Saved!"
                )
            }
            filePickerHandler.pickFileToWrite(suggestedFileName = "numbers.txt")
        }

        binding.outputGeneratedNumbers.btnLoad.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                mainViewModel.runWithProgress(
                    task = { lab1ViewModel.loadGeneratedNumbers(uri) },
                    onSuccessMessage = "Loaded!"
                )
            }
            filePickerHandler.pickFileToRead("*/*")
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}