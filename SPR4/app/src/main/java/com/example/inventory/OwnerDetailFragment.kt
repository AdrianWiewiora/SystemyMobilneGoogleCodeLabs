package com.example.inventory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.data.Owner
import com.example.inventory.databinding.FragmentOwnerDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class OwnerDetailFragment : Fragment() {
    private val navigationArgs: OwnerDetailFragmentArgs by navArgs()
    private var _binding: FragmentOwnerDetailBinding? = null
    private val binding get() = _binding!!
    lateinit var owner: Owner
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.itemDao(),
            (activity?.application as InventoryApplication).database.ownerDao()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.ownerId
        viewModel.retrieveOwner(id).observe(this.viewLifecycleOwner) { selectedOwner ->
            owner = selectedOwner
            bind(owner)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOwnerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteOwner()
            }
            .show()
    }

    private fun deleteOwner() {
        viewModel.deleteOwner(owner)
        findNavController().navigateUp()
    }

    private fun bind(owner: Owner) {
        binding.apply {
            ownerFirstName.text = owner.firstName
            ownerSecondName.text = owner.lastName
            ownerPhoneNumber.text = owner.phoneNumber
            ownerAddress.text = owner.address
            ownerEmail.text = owner.email
            deleteOwner.setOnClickListener { showConfirmationDialog() }
            editOwner.setOnClickListener { editOwner() }
        }
    }

    private fun editOwner() {
        val action = OwnerDetailFragmentDirections.actionOwnerDetailFragmentToAddOwnerFragment(
            title = "Edit Owner",
            owner.id
        )
        this.findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
