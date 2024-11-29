package com.example.informationsecurity.ui.fragments.lab2

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
import com.example.informationsecurity.databinding.FragmentLab2Binding
import com.example.informationsecurity.ui.MainViewModel
import com.example.informationsecurity.ui.fragments.BaseFragment

class Lab2Fragment : BaseFragment<FragmentLab2Binding>() {

    private val lab2ViewModel: Lab2ViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflateBinding(inflater, container, FragmentLab2Binding::inflate).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHashButton()
        setupHashOutput()
        setupOptionMenu()
    }

    private fun setupHashButton() {
        binding.btnHash.setOnClickListener {
            val inputString = binding.etInputString.text.toString()
            if (inputString.isEmpty()) {
                showToast("Empty String!")
                return@setOnClickListener
            }

            mainViewModel.runWithProgress(
                task = { lab2ViewModel.hash(inputString) }
            )
        }
    }

    private fun setupHashOutput() {
        // Setup the hash output section
        setupOutputSection(
            outputView = binding.outputHash,
            labelResId = R.string.hash,
            data = lab2ViewModel.hash,
            saveTask = { lab2ViewModel.saveHash(it) },
            loadTask = { lab2ViewModel.loadHash(it) },
            suggestedFileName = "hash.md5"
        )
    }

    private fun setupOptionMenu() = requireActivity().addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.lab2_option_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.hash_file -> handleHashFile()
                R.id.verify_file_hash -> handleVerifyFileHash()
                else -> false
            }
        }

        private fun handleHashFile(): Boolean {
            filePickerHandler.onFilePicked = { uri ->
                mainViewModel.runWithProgress(
                    task = { lab2ViewModel.hash(uri) }
                )
            }
            filePickerHandler.pickFileToRead()
            return true
        }

        private fun handleVerifyFileHash(): Boolean {
            showToast("Not implemented")
            return true
        }
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)
}
