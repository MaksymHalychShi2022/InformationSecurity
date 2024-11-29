package com.example.informationsecurity.ui.fragments.lab3

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.informationsecurity.databinding.FragmentLab3Binding
import com.example.informationsecurity.ui.MainViewModel
import com.example.informationsecurity.ui.fragments.BaseFragment

class Lab3Fragment : BaseFragment<FragmentLab3Binding>() {

    private val lab3ViewModel: Lab3ViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflateBinding(inflater, container, FragmentLab3Binding::inflate).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPassphraseListener()
        setupFileEncryption()
        setupFileDecryption()
    }

    private fun setupPassphraseListener() {
        // Observe changes to passphrase from ViewModel and update the EditText
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
    }

    private fun setupFileEncryption() {
        binding.btnEncryptFile.setOnClickListener {
            filePickerHandler.onFilePicked = { inputUri ->
                filePickerHandler.onFilePicked = { outputUri ->
                    mainViewModel.runWithProgress(
                        task = { lab3ViewModel.encryptFile(inputUri, outputUri) },
                        onSuccessMessage = "Encrypted!"
                    )
                }
                filePickerHandler.pickFileToWrite("application/octet-stream", "encrypted.txt")
            }
            filePickerHandler.pickFileToRead("*/*")
        }
    }

    private fun setupFileDecryption() {
        binding.btnDecryptFile.setOnClickListener {
            filePickerHandler.onFilePicked = { inputUri ->
                filePickerHandler.onFilePicked = { outputUri ->
                    mainViewModel.runWithProgress(
                        task = { lab3ViewModel.decryptFile(inputUri, outputUri) },
                        onSuccessMessage = "Decrypted!"
                    )
                }
                filePickerHandler.pickFileToWrite("application/octet-stream", "decrypted.txt")
            }
            filePickerHandler.pickFileToRead("*/*")
        }
    }
}
