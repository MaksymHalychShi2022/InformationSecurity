package com.example.informationsecurity.ui.fragments.lab3

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.informationsecurity.databinding.FragmentLab3Binding
import com.example.informationsecurity.utils.OperationState

class Lab3Fragment : Fragment() {

    private lateinit var encryptFilePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var encryptOutputFilePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var decryptFilePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var decryptOutputFilePickerLauncher: ActivityResultLauncher<Intent>

    private var _binding: FragmentLab3Binding? = null
    private lateinit var lab3ViewModel: Lab3ViewModel
    private val mainViewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        lab3ViewModel = ViewModelProvider(this).get(Lab3ViewModel::class.java)

        _binding = FragmentLab3Binding.inflate(inflater, container, false)
        val root: View = binding.root

        lab3ViewModel.passphrase.observe(viewLifecycleOwner) {
            if (binding.etPassphrase.text.toString() != it) {
                binding.etPassphrase.setText(it)
                binding.etPassphrase.setSelection(it.length) // Move cursor to the end
            }
        }

        // Add a TextWatcher to listen for key taps and update ViewModel
        binding.etPassphrase.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newText = s.toString()
                if (newText != lab3ViewModel.passphrase.value) {
                    lab3ViewModel.updatePassphrase(newText) // Update ViewModel
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnEncryptFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // You can change this MIME type if you want to filter file types
            }
            encryptFilePickerLauncher.launch(intent)
        }


        binding.btnDecryptFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // You can change this MIME type if you want to filter file types
            }
            decryptFilePickerLauncher.launch(intent)
        }
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var inputUri: Uri? = null

        encryptFilePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    inputUri = result.data?.data

                    inputUri?.let {
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/octet-stream" // MIME type for binary files
                            putExtra(Intent.EXTRA_TITLE, "encrypted.txt") // Default file name
                        }
                        encryptOutputFilePickerLauncher.launch(intent)
                    }
                }
            }

        encryptOutputFilePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val outputUri: Uri? = result.data?.data
                    outputUri?.let {
                        // Process the selected file's Uri for reading
                        lab3ViewModel.encryptFile(inputUri!!, it)
                            .observe(viewLifecycleOwner) { result ->
                                observeForProgressBar(result, "Encrypted!")
                            }
                    }
                }
                inputUri = null // so it could not be used in later calls
            }


        decryptFilePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    inputUri = result.data?.data
                    inputUri?.let {
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/octet-stream" // MIME type for binary files
                            putExtra(Intent.EXTRA_TITLE, "decrypted.txt") // Default file name
                        }
                        decryptOutputFilePickerLauncher.launch(intent)
                    }
                }
            }

        decryptOutputFilePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        lab3ViewModel.decryptFile(inputUri!!, it)
                            .observe(viewLifecycleOwner) { result ->
                                observeForProgressBar(result, "Decrypted!")
                            }
                    }
                }
                inputUri = null
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