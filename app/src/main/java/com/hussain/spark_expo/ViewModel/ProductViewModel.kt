package com.hussain.spark_expo.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.hussain.spark_expo.data.repo
import com.hussain.spark_expo.model.CategoryModel
import com.hussain.spark_expo.model.ProductModel
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private val repository = repo()
    val addProductStatus = MutableLiveData<String>()
    private val _isUnique = MutableLiveData<Boolean>()
    val isUnique: LiveData<Boolean> get() = _isUnique

    private val _addProductResult = MutableLiveData<Result<Boolean>>()
    val addProductResult: LiveData<Result<Boolean>> get() = _addProductResult
    private val _categories = MutableLiveData<List<CategoryModel>>()
    val categories: LiveData<List<CategoryModel>> get() = _categories





    fun getProduct(): LiveData<List<ProductModel>> = repository.getProducts()





    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categoryList = repository.getCategories()
                _categories.postValue(categoryList)
            } catch (e: Exception) {
                _categories.postValue(emptyList())
            }
        }
    }

    fun addCategory(category: CategoryModel, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.addCategory(category)
            if (success) loadCategories()
            callback(success)
        }
    }

    fun updateCategory(category: CategoryModel, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.updateCategory(category)
            if (success) loadCategories()
            callback(success)
        }
    }

    fun deleteCategory(categoryId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.deleteCategory(categoryId)
            if (success) loadCategories()
            callback(success)
        }
    }

    suspend fun validateAndAddProduct(product: ProductModel) {
        if (product.name.isEmpty() || product.categoryName.isEmpty() || product.itemCode.isEmpty() || product.salePrice.toDoubleOrNull() == null || !repository.checkUniqueItemCodeAndName(product.itemCode,product.name)) {
            _addProductResult.postValue(Result.failure(Exception("Invalid product details")))  // ✅ Use _addProductResult
            return
        }

        viewModelScope.launch {
            val result = repository.addProduct(product)
            _addProductResult.postValue(result) // ✅ Use _addProductResult instead of addProductResult
        }
    }


    fun validateItemCodeAndName(itemCode: String, name: String) {
        viewModelScope.launch {
            val result = repository.checkUniqueItemCodeAndName(itemCode, name)
            _isUnique.value = result
        }
    }


}
