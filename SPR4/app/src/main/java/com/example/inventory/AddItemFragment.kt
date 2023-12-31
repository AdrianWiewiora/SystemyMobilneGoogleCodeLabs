/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inventory

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.data.Item
import com.example.inventory.data.Owner
import com.example.inventory.databinding.FragmentAddItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.text.ParseException
import java.util.Date

class AddItemFragment : Fragment() {

    private val navigationArgs: ItemDetailFragmentArgs by navArgs()
    private lateinit var ownerSpinner: Spinner

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database
                .itemDao(),
            (activity?.application as InventoryApplication).database
                .ownerDao()
        )
    }
    lateinit var item: Item

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.itemPrice.text.toString(),
            binding.itemCount.text.toString(),
            binding.itemCategory.text.toString(),
            binding.itemColor.text.toString(),
            binding.itemLocation.text.toString(),
            binding.itemPurchaseLocation.text.toString()
        )
    }
    private fun addNewItem() {
        if (isEntryValid()) {
            val selectedOwnerPosition: Int = ownerSpinner.selectedItemPosition
            if (selectedOwnerPosition != AdapterView.INVALID_POSITION) {
                val selectedOwnerName: String = ownerSpinner.adapter.getItem(selectedOwnerPosition).toString()
                lifecycleScope.launch(Dispatchers.Main) {
                    val ownerId = viewModel.getOwnerIdByName(selectedOwnerName)
                    viewModel.addNewItem(
                        binding.itemName.text.toString(),
                        binding.itemPrice.text.toString(),
                        binding.itemCount.text.toString(),
                        binding.itemCategory.text.toString(),
                        Date(),
                        binding.itemColor.text.toString(),
                        binding.itemLocation.text.toString(),
                        binding.itemPurchaseLocation.text.toString(),
                        ownerId.toLong()
                    )
                    showToast("Item added successfully")
                    val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
                    findNavController().navigate(action)
                }
            } else {
                showToast("Please select an owner")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun bind(item: Item) {
        val price = "%.2f".format(item.itemPrice)
        binding.apply {
            itemName.setText(item.itemName, TextView.BufferType.SPANNABLE)
            itemPrice.setText(price, TextView.BufferType.SPANNABLE)
            itemCount.setText(item.quantityInStock.toString(), TextView.BufferType.SPANNABLE)
            itemCategory.setText(item.category, TextView.BufferType.SPANNABLE)
            itemColor.setText(item.color, TextView.BufferType.SPANNABLE)
            itemLocation.setText(item.location, TextView.BufferType.SPANNABLE)
            itemPurchaseLocation.setText(item.purchaseLocation, TextView.BufferType.SPANNABLE)
            saveAction.setOnClickListener { updateItem() }
        }
    }

    private fun updateItem() {
        if (isEntryValid()) {
            val selectedOwnerPosition: Int = ownerSpinner.selectedItemPosition
            if (selectedOwnerPosition != AdapterView.INVALID_POSITION) {
                val selectedOwnerName: String = ownerSpinner.adapter.getItem(selectedOwnerPosition).toString()
                lifecycleScope.launch(Dispatchers.Main) {
                    val ownerId = viewModel.getOwnerIdByName(selectedOwnerName)

                    try {
                        val price = NumberFormat.getInstance().parse(binding.itemPrice.text.toString())?.toDouble()
                        val count = NumberFormat.getInstance().parse(binding.itemCount.text.toString())?.toInt()

                        if (price != null && count != null) {
                            viewModel.updateItem(
                                this@AddItemFragment.navigationArgs.itemId,
                                binding.itemName.text.toString(),
                                price.toString(),
                                count.toString(),
                                binding.itemCategory.text.toString(),
                                Date(),
                                binding.itemColor.text.toString(),
                                binding.itemLocation.text.toString(),
                                binding.itemPurchaseLocation.text.toString(),
                                ownerId.toLong()
                            )

                            showToast("Item updated successfully")
                            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
                            findNavController().navigate(action)
                        } else {
                            showToast("Invalid price or count format")
                        }
                    } catch (e: ParseException) {
                        showToast("Invalid price or count format")
                    }
                }
            } else {
                showToast("Please select an owner")
            }
        }
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.itemId
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                item = selectedItem
                bind(item)
            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewItem()
            }
        }

        ownerSpinner = view.findViewById(R.id.owner_spinner)
        val owners: LiveData<List<Owner>> = viewModel.allOwners
        owners.observe(viewLifecycleOwner) { ownerList ->
            if (ownerList.isNotEmpty()) {
                val ownerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    ownerList.map { "${it.firstName} ${it.lastName}" }
                )
                ownerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                ownerSpinner.adapter = ownerAdapter
            } else {
                showToast("No owners available")
                val action = AddItemFragmentDirections.actionAddItemFragmentToAddOwnerFragment(title = "Add Owner")
                findNavController().navigate(action)
            }
        }
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
