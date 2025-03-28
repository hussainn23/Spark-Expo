package com.hussain.spark_expo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.util.copy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hussain.spark_expo.R
import com.hussain.spark_expo.ViewModel.ProductViewModel
import com.hussain.spark_expo.adapter.CtgAdapter
import com.hussain.spark_expo.databinding.FragmentCategoriesBinding
import com.hussain.spark_expo.model.CategoryModel
import com.hussain.spark_expo.utils.Utils

class CategoriesFragment : Fragment() {

    private val viewModel: ProductViewModel by viewModels()
    private lateinit var utils: Utils
    private lateinit var adapter: CtgAdapter
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        utils = Utils(requireContext())

        adapter = CtgAdapter(
            emptyList(),
            onEdit = { showAddEditDialog(it) },
            onDelete = { deleteCategory(it) }
        )

        binding.recCtg.layoutManager = LinearLayoutManager(requireContext())
        binding.recCtg.adapter = adapter

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.updateList(categories)
        }
        // Start animation before loading categories
        utils.startLoadingAnimation()

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.updateList(categories)

            // Stop animation once data is loaded
            utils.endLoadingAnimation()
        }

        binding.fabAdd.setOnClickListener { showAddEditDialog(null) }

        viewModel.loadCategories()
    }

    private fun showAddEditDialog(category: CategoryModel?) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_link, null)

        val titleTextView = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val editText = dialogView.findViewById<EditText>(R.id.addCategory)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAdd)

        if (category == null) {
            titleTextView.text = "Add Category"
            btnAdd.text = "Add"
        } else {
            titleTextView.text = "Edit Category"
            btnAdd.text = "Update"
            editText.setText(category.name)
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        btnAdd.setOnClickListener {
            val newName = editText.text.toString().trim()
            if (newName.isNotEmpty()) {
                utils.startLoadingAnimation()

                if (category == null) {
                    // Add new category
                    viewModel.addCategory(CategoryModel(name = newName)) { success ->
                        Toast.makeText(
                            requireContext(),
                            if (success) "Category Added" else "Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        utils.endLoadingAnimation()
                    }
                } else {
                    // Update existing category
                    val updatedCategory = category.copy(name = newName)
                    viewModel.updateCategory(updatedCategory) { success ->
                        Toast.makeText(
                            requireContext(),
                            if (success) "Category Updated" else "Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        utils.endLoadingAnimation()
                    }
                }
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun deleteCategory(category: CategoryModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete this category?")
            .setPositiveButton("Delete") { _, _ ->
                utils.startLoadingAnimation()
                viewModel.deleteCategory(category.id) { success ->
                    Toast.makeText(
                        requireContext(),
                        if (success) "Category Deleted" else "Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    utils.endLoadingAnimation()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
