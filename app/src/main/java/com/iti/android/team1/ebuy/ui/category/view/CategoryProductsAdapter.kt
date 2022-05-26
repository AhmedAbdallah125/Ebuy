package com.iti.android.team1.ebuy.ui.category.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iti.android.team1.ebuy.R
import com.iti.android.team1.ebuy.databinding.ProductLayoutBinding
import com.iti.android.team1.ebuy.model.pojo.Product

class CategoryProductsAdapter(var products: List<Product>) :
    RecyclerView.Adapter<CategoryProductsAdapter.Holder>() {

    lateinit var context:Context

    class Holder(binding: ProductLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        var productImageView = binding.image
        var tvTitle = binding.txtProductName
        var tvPrice = binding.txtProductPrice

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context=parent.context
        val binding: ProductLayoutBinding =
            ProductLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.tvTitle.text = products[position].productName
        holder.tvPrice.text =
            products[position].productVariants?.get(0)?.productVariantPrice.toString()

        Glide.with(context).load(products[position].productImage?.imageURL).into(holder.productImageView)

    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun setList(products: List<Product>) {
        this.products = products
        notifyDataSetChanged()
    }


}