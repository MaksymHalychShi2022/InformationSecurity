package com.example.informationsecurity.ui.fragments.lab5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.example.informationsecurity.R
import com.example.informationsecurity.databinding.FragmentLab5Binding
import com.example.informationsecurity.ui.MainViewModel
import com.example.informationsecurity.ui.fragments.BaseFragment

class Lab5Fragment : BaseFragment<FragmentLab5Binding>() {

    private val lab5ViewModel: Lab5ViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflateBinding(inflater, container, FragmentLab5Binding::inflate).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sign
        binding.btnSign.setOnClickListener {
            val inputString = binding.etInputString.text.toString()
            if (inputString.isEmpty()) {
                showToast("Empty String!")
                return@setOnClickListener
            }
            mainViewModel.runWithProgress(
                task = { lab5ViewModel.sign(inputString) },
                onSuccessMessage = "Signed!"
            )
        }

        setupOutputSections()
        setupOptionMenu()
    }


    private fun setupOutputSections() {
        setupOutputSection(
            outputView = binding.outputSignature,
            labelResId = R.string.signature,
            data = lab5ViewModel.signature,
            saveTask = { lab5ViewModel.saveSignature(it) },
            loadTask = { lab5ViewModel.loadSignature(it) },
            suggestedFileName = "signature.pub"
        )
        setupOutputSection(
            outputView = binding.outputPublicKey,
            labelResId = R.string.public_key,
            data = lab5ViewModel.publicKey,
            saveTask = { lab5ViewModel.savePublicKey(it) },
            loadTask = { lab5ViewModel.loadPublicKey(it) },
            suggestedFileName = "id_rsa.pub"
        )
        setupOutputSection(
            outputView = binding.outputPrivateKey,
            labelResId = R.string.private_key,
            data = lab5ViewModel.privateKey,
            saveTask = { lab5ViewModel.savePrivateKey(it) },
            loadTask = { lab5ViewModel.loadPrivateKey(it) },
            suggestedFileName = "id_rsa"
        )
    }

    private fun setupOptionMenu() = requireActivity().addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.lab5_option_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.generate_keys -> {
                    mainViewModel.runWithProgress(
                        task = { lab5ViewModel.generateKeys("DSA") },
                        onSuccessMessage = "Keys generated!"
                    )
                    true
                }

                R.id.verify_signature -> handleVerification()
                R.id.sign_file -> handleFileSignature()
                R.id.verify_file_signature -> handleFileVerification()
                else -> false
            }
        }

        private fun handleVerification(): Boolean {
            val inputString = binding.etInputString.text.toString()
            if (inputString.isEmpty()) {
                Toast.makeText(requireContext(), "Empty String!", Toast.LENGTH_SHORT).show()
            } else {
                mainViewModel.runWithProgress(
                    task = { lab5ViewModel.verifySignature(inputString) },
                    onSuccess = { verified ->
                        showToast(if (verified) "Verified" else "Verification failed")
                    }
                )
            }
            return true
        }

        private fun handleFileSignature(): Boolean {
            filePickerHandler.onFilePicked = { uri ->
                mainViewModel.runWithProgress(
                    task = { lab5ViewModel.sign(uri) },
                    onSuccessMessage = "Signed!"
                )
            }
            filePickerHandler.pickFileToRead()
            return true
        }

        private fun handleFileVerification(): Boolean {
            filePickerHandler.onFilePicked = { uri ->
                mainViewModel.runWithProgress(
                    task = { lab5ViewModel.verifySignature(uri) },
                    onSuccess = { verified ->
                        showToast(if (verified) "Verified" else "Verification failed")
                    }
                )
            }
            filePickerHandler.pickFileToRead()
            return true
        }
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)
}