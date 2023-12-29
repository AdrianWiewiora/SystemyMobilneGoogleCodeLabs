package com.example.inventory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.data.Owner
import com.example.inventory.databinding.FragmentAddOwnerBinding


class AddOwnerFragment : Fragment() {

    private val navigationArgs: OwnerDetailFragmentArgs by navArgs()

    private var _binding: FragmentAddOwnerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database
                .itemDao(),
            (activity?.application as InventoryApplication).database
                .ownerDao()
        )
    }
    lateinit var owner: Owner


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddOwnerBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun isEntryValidOwner(): Boolean {
        return viewModel.isEntryValidOwner(
            binding.firstName.text.toString(),
            binding.lastName.text.toString(),
            binding.phoneNumber.text.toString(),
            binding.address.text.toString(),
            binding.email.text.toString()
        )
    }

    private fun addNewOwner() {
        if (isEntryValidOwner()) {
            viewModel.addNewOwner(
                binding.firstName.text.toString(),
                binding.lastName.text.toString(),
                binding.phoneNumber.text.toString(),
                binding.address.text.toString(),
                binding.email.text.toString()
            )
            val action = AddOwnerFragmentDirections.actionAddOwnerFragmentToOwnerListFragment(title = "Owners List")
            findNavController().navigate(action)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.ownerId
        if (id > 0) {
            viewModel.retrieveOwner(id).observe(this.viewLifecycleOwner) { selectedOwner ->
                owner = selectedOwner
                bind(owner)
            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewOwner()
            }
        }
    }

    private fun bind(owner: Owner) {
        binding.apply {
            firstName.setText(owner.firstName, TextView.BufferType.SPANNABLE)
            lastName.setText(owner.lastName, TextView.BufferType.SPANNABLE)
            phoneNumber.setText(owner.phoneNumber, TextView.BufferType.SPANNABLE)
            address.setText(owner.address, TextView.BufferType.SPANNABLE)
            email.setText(owner.email, TextView.BufferType.SPANNABLE)
            saveAction.setOnClickListener { updateOwner() }
        }
    }

    private fun updateOwner() {
        if (isEntryValidOwner()) {

            viewModel.updateOwner(
                this.navigationArgs.ownerId,
                this.binding.firstName.text.toString(),
                this.binding.lastName.text.toString(),
                this.binding.phoneNumber.text.toString(),
                this.binding.address.text.toString(),
                this.binding.email.text.toString(),
            )

            val action = AddOwnerFragmentDirections.actionAddOwnerFragmentToOwnerListFragment(title = "Add Owner Fragment")
            findNavController().navigate(action)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}