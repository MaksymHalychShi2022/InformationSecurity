package com.example.informationsecurity.ui.fragments.lab1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.informationsecurity.R
import com.example.informationsecurity.databinding.FragmentEstimatePiBinding
import com.example.informationsecurity.ui.MainViewModel

class EstimatePiFragment : Fragment() {

    private var _binding: FragmentEstimatePiBinding? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private val lab1ViewModel: Lab1ViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEstimatePiBinding.inflate(inflater, container, false)

        binding.btnGenerate.setOnClickListener {
            val useStandardLib: Boolean = binding.cbUseStandardLib.isChecked
            val numberOfPairs: Long? =
                binding.etHowManyPairsToGenerate.text.toString().toLongOrNull()
            if (numberOfPairs != null && numberOfPairs > 0) {
                mainViewModel.runWithProgress(
                    task = { lab1ViewModel.estimatePi(numberOfPairs, useStandardLib) }
                )
            } else {
                Toast.makeText(context, "Invalid input!", Toast.LENGTH_LONG).show()
            }
        }

        lab1ViewModel.estimatedPi.observe(viewLifecycleOwner) {
            binding.tvOutput.text = it.toString()
        }

        binding.cbUseStandardLib.setOnCheckedChangeListener { _, isChecked ->
            binding.tvGeneratorUsedTitle.setText(
                if (isChecked) R.string.standard_lib_generator else R.string.lehmer_generator
            )
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}