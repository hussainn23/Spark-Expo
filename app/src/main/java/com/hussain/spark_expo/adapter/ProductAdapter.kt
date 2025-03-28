package com.hussain.spark_expo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.hussain.spark_expo.R
import com.hussain.spark_expo.model.CartModel
import com.hussain.spark_expo.model.ProductModel
import java.util.*

class ProductAdapter(
    private var productList: MutableList<ProductModel>,
    private val onEditClick: (ProductModel) -> Unit, // Callback for edit
    private val onDeleteClick: (ProductModel) -> Unit // Callback for delete
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var filteredList: MutableList<ProductModel> = ArrayList(productList)

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.item_image)
        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemQuantity: TextView = view.findViewById(R.id.item_quantity)
        val itemPrice: TextView = view.findViewById(R.id.item_price)
        val itemCart: TextView = view.findViewById(R.id.addToCart)
        val edit: ImageView = view.findViewById(R.id.edit)
        val delete: ImageView = view.findViewById(R.id.delete) // Add delete icon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_products, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = filteredList[position]
        if(product.quantity.toInt()==0){
            holder.itemCart.visibility=View.INVISIBLE
        }
        holder.itemName.text = product.name
        holder.itemQuantity.text = "Quantity: ${product.quantity}"
        holder.itemPrice.text = "Rs.${product.salePrice}"

        holder.edit.setOnClickListener {
            onEditClick(product)  // Calls function to open the edit dialog
        }

        holder.delete.setOnClickListener {
            onDeleteClick(product) // Calls function to open delete confirmation dialog
        }

        holder.itemCart.setOnClickListener {
            showCartDialog(holder.itemView, product)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            ArrayList(productList)
        } else {
            val resultList = ArrayList<ProductModel>()
            for (product in productList) {
                if (product.name.lowercase(Locale.getDefault())
                        .contains(query.lowercase(Locale.getDefault()))
                ) {
                    resultList.add(product)
                }
            }
            resultList
        }
        notifyDataSetChanged()
    }

    fun updateList(newList: MutableList<ProductModel>) {
        productList = newList
        filteredList = ArrayList(newList)
        notifyDataSetChanged()
    }

    private fun showCartDialog(view: View, product: ProductModel) {
        val dialogView = LayoutInflater.from(view.context).inflate(R.layout.dialog_add_link, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val editText = dialogView.findViewById<TextView>(R.id.addCategory)
        val btnAdd = dialogView.findViewById<TextView>(R.id.btnAdd)

        titleTextView.text = "Add to Cart"
        btnAdd.text = "Add"
        editText.text = ""

        val dialog = MaterialAlertDialogBuilder(view.context)
            .setView(dialogView)
            .create()

        btnAdd.setOnClickListener {
            val newQuantity = editText.text.toString().trim()
            if (newQuantity.isNotEmpty()) {
                addToCart(product, newQuantity.toInt())
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun addToCart(product: ProductModel, quantity: Int) {
        val cartItem = CartModel(
            id = UUID.randomUUID().toString(),
            productId = product.id,
            quantity = quantity.toString()
        )

        FirebaseFirestore.getInstance().collection("Cart")
            .document(cartItem.id)
            .set(cartItem)
    }
}
