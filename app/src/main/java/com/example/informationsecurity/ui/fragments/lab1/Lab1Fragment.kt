package com.example.informationsecurity.ui.fragments.lab1

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
import androidx.navigation.fragment.findNavController
import com.example.informationsecurity.R
import com.example.informationsecurity.databinding.FragmentLab1Binding
import com.example.informationsecurity.ui.MainViewModel
import com.example.informationsecurity.ui.fragments.BaseFragment

class Lab1Fragment : BaseFragment<FragmentLab1Binding>() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val lab1ViewModel: Lab1ViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflateBinding(inflater, container, FragmentLab1Binding::inflate).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupGenerateButton()
        setupGeneratedNumbersOutput()
    }

    private fun setupMenu() {
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
    }

    private fun setupGenerateButton() {
        binding.btnGenerate.setOnClickListener {
            val lengthOfSequence = binding.etHowManyNumbersToGenerate.text.toString().toLongOrNull()
            if (lengthOfSequence == null || lengthOfSequence <= 0) {
                showToast("Invalid input!")
                return@setOnClickListener
            }
            mainViewModel.runWithProgress(
                task = { lab1ViewModel.generateRandomNumbers(lengthOfSequence) },
                onSuccessMessage = "Generated!"
            )
        }
    }

    private fun setupGeneratedNumbersOutput() {
        setupOutputSection(
            outputView = binding.outputGeneratedNumbers,
            labelResId = R.string.generated_numbers,
            data = lab1ViewModel.generatedNumbers,
            saveTask = { lab1ViewModel.saveGeneratedNumbers(it) },
            loadTask = { lab1ViewModel.loadGeneratedNumbers(it) },
            suggestedFileName = "numbers.txt"
        )
    }
}
