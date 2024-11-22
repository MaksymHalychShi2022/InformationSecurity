package com.example.informationsecurity.ui.fragments.lab4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.example.informationsecurity.R
import com.example.informationsecurity.databinding.FragmentLab4Binding
import com.example.informationsecurity.ui.MainViewModel
import com.example.informationsecurity.ui.fragments.BaseFragment

class Lab4Fragment : BaseFragment<FragmentLab4Binding>() {

    private val lab4ViewModel: Lab4ViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflateBinding(inflater, container, FragmentLab4Binding::inflate).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOutputSections()
        setupOptionMenu()
    }

    private fun setupOutputSections() {
        // Public Key Output Section
        setupOutputSection(
            outputView = binding.outputPublicKey,
            labelResId = R.string.public_key,
            data = lab4ViewModel.publicKey,
            saveTask = { lab4ViewModel.savePublicKey(it) },
            loadTask = { lab4ViewModel.loadPublicKey(it) },
            suggestedFileName = "id_rsa.pub"
        )

        // Private Key Output Section
        setupOutputSection(
            outputView = binding.outputPrivateKey,
            labelResId = R.string.private_key,
            data = lab4ViewModel.privateKey,
            saveTask = { lab4ViewModel.savePrivateKey(it) },
            loadTask = { lab4ViewModel.loadPrivateKey(it) },
            suggestedFileName = "id_rsa"
        )
    }

    private fun setupOptionMenu() = requireActivity().addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.lab4_option_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.generate_keys -> {
                    mainViewModel.runWithProgress(
                        task = { lab4ViewModel.generateKeys("RSA") },
                        onSuccessMessage = "Keys generated!"
                    )
                    true
                }

                R.id.encrypt_file -> handleFileEncryption()
                R.id.decrypt_file -> handleFileDecryption()
                else -> false
            }
        }

        private fun handleFileEncryption(): Boolean {
            filePickerHandler.onFilePicked = { inputUri ->
                filePickerHandler.onFilePicked = { outputUri ->
                    mainViewModel.runWithProgress(
                        task = { lab4ViewModel.encryptFile(inputUri, outputUri) },
                        onSuccessMessage = "Encrypted!"
                    )
                }
                filePickerHandler.pickFileToWrite(suggestedFileName = "encrypted.txt")
            }
            filePickerHandler.pickFileToRead("*/*")
            return true
        }

        private fun handleFileDecryption(): Boolean {
            filePickerHandler.onFilePicked = { inputUri ->
                filePickerHandler.onFilePicked = { outputUri ->
                    mainViewModel.runWithProgress(
                        task = { lab4ViewModel.decryptFile(inputUri, outputUri) },
                        onSuccessMessage = "Decrypted!"
                    )
                }
                filePickerHandler.pickFileToWrite(suggestedFileName = "decrypted.txt")
            }
            filePickerHandler.pickFileToRead("*/*")
            return true
        }
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)
}
