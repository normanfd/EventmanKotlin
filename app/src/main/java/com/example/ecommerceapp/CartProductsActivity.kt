package com.example.ecommerceapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_cart_products.*
import java.util.ArrayList

class CartProductsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_products)

        var cartProductsUrl = "http://192.168.42.211/EventmanAPI/fetch_temporary_order.php?email=${Person.email}"
        var cartProductsList = ArrayList<String>()
        var requestQ = Volley.newRequestQueue(this@CartProductsActivity)
        var jsonAR = JsonArrayRequest(Request.Method.GET, cartProductsUrl, null,Response.Listener {
            response ->

            for (joIndex in 0.until(response.length())) { //id, name, price, email, amount
                cartProductsList.add("${response.getJSONObject(joIndex).getInt("id")} \n " +
                        "${response.getJSONObject(joIndex).getString("name")} \n " +
                        "${response.getJSONObject(joIndex).getInt("price")} \n " +
                        "${response.getJSONObject(joIndex).getString("email")} \n " +
                        "${response.getJSONObject(joIndex).getInt("amount")}")
            }

            var cartProductAdapter = ArrayAdapter(this@CartProductsActivity, android.R.layout.simple_list_item_1, cartProductsList)
            cartProductListView.adapter = cartProductAdapter


        }, Response.ErrorListener {
            error ->
        })

        requestQ.add(jsonAR)
    }
}
