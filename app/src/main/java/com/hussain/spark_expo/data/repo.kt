package com.hussain.spark_expo.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.hussain.spark_expo.model.CategoryModel
import com.hussain.spark_expo.model.ProductModel
import com.hussain.spark_expo.model.UserModel
import com.hussain.spark_expo.utils.Constants
import kotlinx.coroutines.tasks.await

class repo {

    private val db = FirebaseFirestore.getInstance()
    private val constants = Constants()
    private val _isUnique = MutableLiveData<Boolean>()
    val isUnique: LiveData<Boolean> get() = _isUnique


    private val adminCollection = constants.USER_COLLECTION
    private val categoryCollection = db.collection("category")
    private val productCollection = db.collection("Products")
    private val productRef = FirebaseFirestore.getInstance().collection("Products")

    private var productListner: ListenerRegistration? = null



    fun getProducts (): LiveData<List<ProductModel>> {
        val productLiveData = MutableLiveData<List<ProductModel>>()

        productListner = db.collection("Products")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    productLiveData.value = emptyList()
                    return@addSnapshotListener
                }
                val product = snapshots?.documents?.mapNotNull { document ->
                    document.toObject(ProductModel::class.java)
                }
                productLiveData.value = product ?: emptyList()
            }
        return productLiveData
    }










    // ✅ User Login
    fun loginUser(email: String, password: String): MutableLiveData<UserModel?> {
        val loginResult = MutableLiveData<UserModel?>()
        db.collection(adminCollection)
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                loginResult.value = if (!documents.isEmpty) {
                    documents.documents[0].toObject(UserModel::class.java)
                } else null
            }
            .addOnFailureListener { loginResult.value = null }

        return loginResult
    }

    // ✅ Get Categories (Suspended)
    suspend fun getCategories(): List<CategoryModel> {
        return try {
            val snapshot = categoryCollection.get().await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(CategoryModel::class.java)?.apply {
                    id = document.id  // Assign Firestore document ID
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ✅ Add Category
    suspend fun addCategory(category: CategoryModel): Boolean {
        return try {
            val docRef = categoryCollection.add(category).await()
            category.id = docRef.id
            true
        } catch (e: Exception) {
            false
        }
    }

    // ✅ Update Category
    suspend fun updateCategory(category: CategoryModel): Boolean {
        return try {
            categoryCollection.document(category.id).set(category).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // ✅ Delete Category
    suspend fun deleteCategory(categoryId: String): Boolean {
        return try {
            categoryCollection.document(categoryId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun checkUniqueItemCodeAndName(itemCode: String, name: String): Boolean {
        return try {
            val codeCheck = productCollection
                .whereEqualTo("itemCode", itemCode)
                .whereEqualTo("name", name)
                   .get()
                .await()

            codeCheck.isEmpty // Returns true if unique (not found), false if exists
        } catch (e: Exception) {
            false // If an error occurs, assume item is not unique (handle errors safely)
        }
    }

    // ✅ Add Product (Suspended)
    suspend fun addProduct(product: ProductModel): Result<Boolean> {
        return try {
            val productDocRef = productRef.document() // Generate a new document reference
            product.id = productDocRef.id // Assign the generated ID to the product
            productDocRef.set(product).await() // Use set() instead of add() to ensure ID is stored

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}
