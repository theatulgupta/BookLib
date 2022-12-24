package com.agkminds.booklib.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.agkminds.booklib.R
import com.agkminds.booklib.adapter.DashboardRecyclerAdapter
import com.agkminds.booklib.databinding.FragmentDashboardBinding
import com.agkminds.booklib.model.Book
import com.agkminds.booklib.util.ConnectionManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val bookInfoList = arrayListOf<Book>()
    private val ratingComparator = Comparator<Book> { book1, book2 ->
        if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
            book1.bookName.compareTo(book2.bookName, true)
        } else {
            book1.bookRating.compareTo(book2.bookRating, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Initializing the binding of Dashboard Fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

//        This code will set the Sort Icon in the appBar
        setHasOptionsMenu(true)

//        Using Volley-Library to fetch JSON objects from API
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"
        if (ConnectionManager().isNetworkAvailable(activity as Context)) {

//        Progress bar displaying
            binding.progressBar.visibility = View.VISIBLE

            val jsonObjectRequest =
                @SuppressLint("NotifyDataSetChanged")
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        binding.progressBar.visibility =
                            View.GONE // This will hide the progress bar when the content is loaded.
                        val success = it.getBoolean("success")
                        if (success) {
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJsonObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("image"),
                                )
                                bookInfoList.add(bookObject) // Adding Parsed data to bookInfoList

                                // Setting adapter for the Recycler View
                                binding.recyclerView.adapter =
                                    DashboardRecyclerAdapter(activity as Context, bookInfoList)

                                // Specifying the LayoutManager of the Recycler View
                                binding.recyclerView.layoutManager = LinearLayoutManager(activity)
                                binding.recyclerView.adapter?.notifyDataSetChanged()

                                // This code adds a divider line between items of Recycler View
//                                binding.recyclerView.addItemDecoration(DividerItemDecoration(binding.recyclerView.context,
//                                    LinearLayoutManager.VERTICAL))
                            }
                        } else {
                            Toast.makeText(activity as Context,
                                "Some Error Occurred",
                                Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(activity as Context,
                            "Some unexpected error occurred!!!",
                            Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(activity as Context,
                        "Volley error occurred!!",
                        Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "4fb0bb6a0f10ec"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest) // Adding the JSON object request
        } else {
            //                Internet is not available
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("No Internet Connectivity") // Setting the Title for the Dialog Box
            dialog.setMessage("Please check your internet connection and try again") // Setting message for the Dialog Box
            dialog.setPositiveButton("Open Settings") { _, _ ->
//                This will open the Wireless Settings on the Phone
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity) // This will exit the application and finish all the activities
            }
            dialog.setCancelable(false)
            dialog.create()
            dialog.show()
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.action_sort) {
            Collections.sort(bookInfoList, ratingComparator)
            bookInfoList.reverse()
        }

        binding.recyclerView.adapter?.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

}