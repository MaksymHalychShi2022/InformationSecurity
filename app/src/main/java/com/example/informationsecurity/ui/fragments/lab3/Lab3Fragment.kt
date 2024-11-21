package com.example.informationsecurity.ui.fragments.lab3

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.informationsecurity.databinding.FragmentLab3Binding
import com.example.informationsecurity.ui.MainViewModel
import com.example.informationsecurity.utils.FilePickerHandler
import com.example.informationsecurity.utils.OperationState

class Lab3Fragment : Fragment() {

    private val lab3ViewModel: Lab3ViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentLab3Binding? = null

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
        _binding = FragmentLab3Binding.inflate(inflater, container, false)

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
            filePickerHandler.onFilePicked = { inputUri ->
                filePickerHandler.onFilePicked = { outputUri ->
                    lab3ViewModel.encryptFile(inputUri, outputUri)
                        .observe(viewLifecycleOwner) { result ->
                            observeForProgressBar(result, "Encrypted!")
                        }
                }
                filePickerHandler.pickFileToWrite("application/octet-stream", "encrypted.txt")
            }
            filePickerHandler.pickFileToRead("*/*")
        }

        binding.btnDecryptFile.setOnClickListener {
            filePickerHandler.onFilePicked = { inputUri ->
                filePickerHandler.onFilePicked = { outputUri ->
                    lab3ViewModel.decryptFile(inputUri, outputUri)
                        .observe(viewLifecycleOwner) { result ->
                            observeForProgressBar(result, "Decrypted!")
                        }
                }
                filePickerHandler.pickFileToWrite("application/octet-stream", "decrypted.txt")
            }
            filePickerHandler.pickFileToRead("*/*")
        }

        return binding.root
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