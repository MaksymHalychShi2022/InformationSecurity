package com.example.informationsecurity.ui.fragments.lab4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.example.informationsecurity.R
import com.example.informationsecurity.databinding.FragmentLab4Binding
import com.example.informationsecurity.utils.FilePickerHandler

class Lab4Fragment : Fragment() {

    private val lab4ViewModel: Lab4ViewModel by viewModels()
    private var _binding: FragmentLab4Binding? = null

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
        _binding = FragmentLab4Binding.inflate(inflater, container, false)

        // Public Key Output
        binding.outputPublicKey.tvLabel.text = requireContext().getString(R.string.public_key)
        lab4ViewModel.publicKey.observe(viewLifecycleOwner) {
            binding.outputPublicKey.tvScrollableText.text = it
        }

        binding.outputPublicKey.btnSave.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                lab4ViewModel.runWithProgress(
                    task = { lab4ViewModel.savePublicKey(uri) },
                    onSuccessMessage = "Public Key Saved!"
                )
            }
            filePickerHandler.pickFileToWrite(suggestedFileName = "id_rsa.pub")
        }

        binding.outputPublicKey.btnLoad.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                lab4ViewModel.runWithProgress(
                    task = { lab4ViewModel.loadPublicKey(uri) },
                    onSuccessMessage = "Public Key Loaded!"
                )
            }
            filePickerHandler.pickFileToRead()
        }

        // Private Key Output
        binding.outputPrivateKey.tvLabel.text = requireContext().getString(R.string.private_key)
        lab4ViewModel.privateKey.observe(viewLifecycleOwner) {
            binding.outputPrivateKey.tvScrollableText.text = it
        }

        binding.outputPrivateKey.btnSave.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                lab4ViewModel.runWithProgress(
                    task = { lab4ViewModel.savePrivateKey(uri) },
                    onSuccessMessage = "Private Key Saved!"
                )
            }
            filePickerHandler.pickFileToWrite(suggestedFileName = "id_rsa")
        }

        binding.outputPrivateKey.btnLoad.setOnClickListener {
            filePickerHandler.onFilePicked = { uri ->
                lab4ViewModel.runWithProgress(
                    task = { lab4ViewModel.loadPrivateKey(uri) },
                    onSuccessMessage = "Private Key Loaded!"
                )
            }
            filePickerHandler.pickFileToRead()
        }

        setupOptionMenu()

        return binding.root
    }

    private fun setupOptionMenu() = requireActivity().addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.lab4_option_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.generate_keys -> {

                    lab4ViewModel.runWithProgress(
                        task = { lab4ViewModel.generateKeys() },
                        onSuccessMessage = "Keys generated!"
                    )

                    true
                }

                R.id.encrypt_file -> {

                    filePickerHandler.onFilePicked = { inputUri ->
                        filePickerHandler.onFilePicked = { outputUri ->
                            lab4ViewModel.runWithProgress(
                                task = { lab4ViewModel.encryptFile(inputUri, outputUri) },
                                onSuccessMessage = "Encrypted!"
                            )
                        }
                        filePickerHandler.pickFileToWrite(
                            suggestedFileName = "encrypted.txt"
                        )
                    }
                    filePickerHandler.pickFileToRead("*/*")
                    true
                }

                R.id.decrypt_file -> {

                    filePickerHandler.onFilePicked = { inputUri ->
                        filePickerHandler.onFilePicked = { outputUri ->
                            lab4ViewModel.runWithProgress(
                                task = { lab4ViewModel.decryptFile(inputUri, outputUri) },
                                onSuccessMessage = "Decrypted!"
                            )

                        }
                        filePickerHandler.pickFileToWrite(
                            suggestedFileName = "decrypted.txt"
                        )
                    }
                    filePickerHandler.pickFileToRead("*/*")
                    true
                }

                else -> false
            }
        }
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}