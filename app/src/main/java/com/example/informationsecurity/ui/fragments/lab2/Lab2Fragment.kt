package com.example.informationsecurity.ui.fragments.lab2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.informationsecurity.ui.MainViewModel
import com.example.informationsecurity.databinding.FragmentLab2Binding
import com.example.informationsecurity.utils.OperationState

class Lab2Fragment : Fragment() {

    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileSaveLauncher: ActivityResultLauncher<Intent>
    private lateinit var compareWithHashInFileLauncher: ActivityResultLauncher<Intent>
    private lateinit var lab2ViewModel: Lab2ViewModel
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentLab2Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        lab2ViewModel = ViewModelProvider(this).get(Lab2ViewModel::class.java)

        _binding = FragmentLab2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnHash.setOnClickListener {
            val inputString = binding.etInputString.text.toString()
            if (inputString.isEmpty()) {
                Toast.makeText(requireContext(), "Empty String!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lab2ViewModel.hash(inputString).observe(viewLifecycleOwner, ::observeForProgressBar)
        }

        binding.btnChooseFile.setOnClickListener {
            // Open the file picker when button is clicked
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // You can change this MIME type if you want to filter file types
            }
            filePickerLauncher.launch(intent)
        }

        binding.btnSaveOutputToFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type =
                    "application/octet-stream"  // You can change this MIME type based on your needs
                putExtra(Intent.EXTRA_TITLE, "hash.md5")  // Suggested filename
            }
            fileSaveLauncher.launch(intent)
        }

        binding.btnCompareWithHashInFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // You can change this MIME type if you want to filter file types
            }
            compareWithHashInFileLauncher.launch(intent)
        }

        lab2ViewModel.output.observe(viewLifecycleOwner) {
            binding.tvOutput.text = it
        }
        return root
    }

    // Register the ActivityResultLauncher to open the file picker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Launcher for opening a document (reading file)
        filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        // Process the selected file's Uri for reading
                        lab2ViewModel.hash(uri).observe(viewLifecycleOwner, ::observeForProgressBar)
                    }
                }
            }

        // Launcher for creating a document (writing file)
        fileSaveLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        // Write data to the selected Uri
                        lab2ViewModel.output.value?.let { output ->
                            lab2ViewModel.writeToFileUri(it, output).observe(
                                viewLifecycleOwner,
                                ::observeForProgressBar
                            )
                        }
                    }
                }
            }

        // Launcher for comparing hash in file with those in buffer (reading file)
        compareWithHashInFileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        // Write data to the selected Uri
                        lab2ViewModel.compareWithHashInFile(uri).observe(
                            viewLifecycleOwner,
                            ::observeForProgressBar
                        )
                    }
                }
            }
    }

    private fun observeForProgressBar(result: OperationState<*>) {
        when (result) {
            is OperationState.Loading -> {
                mainViewModel.showProgressBar()
            }

            is OperationState.Success -> {
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