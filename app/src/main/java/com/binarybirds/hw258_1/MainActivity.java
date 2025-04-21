package com.binarybirds.hw258_1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static String PRODUCT_URL = "https://dummyjson.com/products";
    //public static String PRODUCT_URL = "https://codecanvas.top/Complex%20JSon/HW258_1.json";

    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();

    GridView gridView;
    SearchView searchView;
    ImageView cartView;
    LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeView();
    }

    public void initializeView() {
        searchView = findViewById(R.id.searchView);
        gridView = findViewById(R.id.gridView);
        cartView = findViewById(R.id.cartView);
        animationView = findViewById(R.id.animationView);

        animationView.setVisibility(View.VISIBLE);

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((MyAdapter) gridView.getAdapter()).getFilter().filter(newText);
                return true;
            }
        });

        parseData();
        cartView.setOnClickListener(v -> cartView());
    }

    public void parseData() {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, PRODUCT_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int total = response.getInt("total");
                    int skip = response.getInt("skip");
                    int limit = response.getInt("limit");

                    //Toast.makeText(MainActivity.this, "Total: " + total + " | Skip: " + skip + " | Limit: " + limit, Toast.LENGTH_SHORT).show();

                    JSONArray jsonArray = response.getJSONArray("products");


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);


                        // Main Product Data
                        String id = jsonObject.optString("id");
                        String title = jsonObject.optString("title");
                        String description = jsonObject.optString("description");
                        String category = jsonObject.optString("category");
                        double price = jsonObject.optDouble("price");
                        double discountPercentage = jsonObject.optDouble("discountPercentage");
                        double rating = jsonObject.optDouble("rating");
                        int stock = jsonObject.optInt("stock");
                        String brand = jsonObject.optString("brand", "N/A");
                        String sku = jsonObject.optString("sku", "N/A");
                        double weight = jsonObject.optDouble("weight");
                        String warrantyInformation = jsonObject.optString("warrantyInformation", "N/A");
                        String shippingInformation = jsonObject.optString("shippingInformation", "N/A");
                        String availabilityStatus = jsonObject.optString("availabilityStatus", "N/A");
                        String returnPolicy = jsonObject.optString("returnPolicy", "N/A");
                        int minimumOrderQuantity = jsonObject.optInt("minimumOrderQuantity");
                        String thumbnail = jsonObject.optString("thumbnail", "");

                        // Dimensions Object
                        JSONObject dimensions = jsonObject.optJSONObject("dimensions");
                        double width = dimensions != null ? dimensions.optDouble("width") : 0;
                        double height = dimensions != null ? dimensions.optDouble("height") : 0;
                        double depth = dimensions != null ? dimensions.optDouble("depth") : 0;

                        // Meta Object
                        JSONObject meta = jsonObject.optJSONObject("meta");
                        String createdAt = meta != null ? meta.optString("createdAt") : "";
                        String updatedAt = meta != null ? meta.optString("updatedAt") : "";
                        String barcode = meta != null ? meta.optString("barcode") : "";
                        String qrCode = meta != null ? meta.optString("qrCode") : "";

                        // Tags Array
                        JSONArray tagsArray = jsonObject.optJSONArray("tags");
                        StringBuilder tags = new StringBuilder();
                        if (tagsArray != null) {
                            for (int j = 0; j < tagsArray.length(); j++) {
                                tags.append(tagsArray.optString(j));
                                if (j != tagsArray.length() - 1) tags.append(", ");
                            }
                        }

                        // Images Array
                        JSONArray imagesArray = jsonObject.optJSONArray("images");
                        StringBuilder images = new StringBuilder();
                        if (imagesArray != null) {
                            for (int j = 0; j < imagesArray.length(); j++) {
                                images.append(imagesArray.optString(j));
                                if (j != imagesArray.length() - 1) images.append(", ");
                            }
                        }

                        // Reviews Array
                        JSONArray reviewsArray = jsonObject.optJSONArray("reviews");
                        StringBuilder reviews = new StringBuilder();
                        if (reviewsArray != null) {
                            for (int j = 0; j < reviewsArray.length(); j++) {
                                JSONObject review = reviewsArray.optJSONObject(j);
                                if (review != null) {
                                    reviews.append("[").append(review.optString("reviewerName", "Anonymous")).append(" | ").append("Rating: ").append(review.optInt("rating")).append(" | ").append(review.optString("comment", "No comment")).append("]\n");
                                }
                            }
                        }

                        // Add to HashMap
                        HashMap<String, String> hashMap = new HashMap<>();

                        hashMap.put("total", String.valueOf(total));
                        hashMap.put("skip", String.valueOf(skip));
                        hashMap.put("limit", String.valueOf(limit));


                        hashMap.put("id", id);
                        hashMap.put("title", title);
                        hashMap.put("description", description);
                        hashMap.put("category", category);
                        hashMap.put("price", String.valueOf(price));
                        hashMap.put("discountPercentage", String.valueOf(discountPercentage));
                        hashMap.put("rating", String.valueOf(rating));
                        hashMap.put("stock", String.valueOf(stock));
                        hashMap.put("brand", brand);
                        hashMap.put("sku", sku);
                        hashMap.put("weight", String.valueOf(weight));
                        hashMap.put("warrantyInformation", warrantyInformation);
                        hashMap.put("shippingInformation", shippingInformation);
                        hashMap.put("availabilityStatus", availabilityStatus);
                        hashMap.put("returnPolicy", returnPolicy);
                        hashMap.put("minimumOrderQuantity", String.valueOf(minimumOrderQuantity));
                        hashMap.put("thumbnail", thumbnail);

                        // Dimensions
                        hashMap.put("width", String.valueOf(width));
                        hashMap.put("height", String.valueOf(height));
                        hashMap.put("depth", String.valueOf(depth));

                        // Meta
                        hashMap.put("createdAt", createdAt);
                        hashMap.put("updatedAt", updatedAt);
                        hashMap.put("barcode", barcode);
                        hashMap.put("qrCode", qrCode);

                        // Arrays
                        hashMap.put("tags", tags.toString());
                        hashMap.put("images", images.toString());
                        hashMap.put("reviews", reviews.toString());

                        // Add to list
                        arrayList.add(hashMap);
                    }

                    // Set adapter
                    MyAdapter myAdapter = new MyAdapter(arrayList);
                    gridView.setAdapter(myAdapter);

                    animationView.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Products Loaded!", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Parsing Error!", Toast.LENGTH_SHORT).show();
                    animationView.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
                animationView.setVisibility(View.GONE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);
    }

    public void cartView() {

        Intent intent = new Intent(getApplicationContext(), CartView.class);
        startActivity(intent);

    }

    public class MyAdapter extends BaseAdapter implements Filterable {
        private final ArrayList<HashMap<String, String>> originalList;
        private final ArrayList<HashMap<String, String>> filteredList;

        public MyAdapter(ArrayList<HashMap<String, String>> arrayList) {
            this.originalList = new ArrayList<>(arrayList);
            this.filteredList = new ArrayList<>(arrayList);
        }

        @Override
        public int getCount() {
            return filteredList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    ArrayList<HashMap<String, String>> filteredResults = new ArrayList<>();

                    if (constraint == null || constraint.length() == 0) {
                        filteredResults.addAll(originalList);
                    } else {
                        String filterPattern = constraint.toString().toLowerCase().trim();

                        for (HashMap<String, String> item : originalList) {
                            if (item.get("title").toLowerCase().contains(filterPattern)) {
                                filteredResults.add(item);
                            }
                        }
                    }

                    results.values = filteredResults;
                    results.count = filteredResults.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredList.clear();
                    filteredList.addAll((ArrayList<HashMap<String, String>>) results.values);
                    notifyDataSetChanged();
                }
            };
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("ViewHolder") View myView = layoutInflater.inflate(R.layout.product_cardview, viewGroup, false);

            RoundedImageView itemLayoutImage = myView.findViewById(R.id.itemLayoutImage);
            TextView itemName = myView.findViewById(R.id.itemName);
            TextView priceAfterDis = myView.findViewById(R.id.priceAfterDis);
            TextView discountAmount = myView.findViewById(R.id.discountAmount);
            TextView category1 = myView.findViewById(R.id.category);
            TextView idNo = myView.findViewById(R.id.idNo);

            HashMap<String, String> hashMap = filteredList.get(i);
            String total = hashMap.get("total");
            String skip = hashMap.get("skip");
            String limit = hashMap.get("limit");

            String thumbnail = hashMap.get("thumbnail");
            String id = hashMap.get("id");
            String title = hashMap.get("title");
            String category = hashMap.get("category");
            String price = hashMap.get("price");
            String discountPercentage = hashMap.get("discountPercentage");

            // Load image
            Picasso.get().load(thumbnail).placeholder(R.drawable.shaon).into(itemLayoutImage);

            // Set text values
            idNo.setText("ID No: " + id);
            itemName.setText(title);
            category1.setText(category);

            // Calculate discounted price
            double originalPrice = Double.parseDouble(price);
            double discountPercent = Double.parseDouble(discountPercentage);
            double discountedPrice = originalPrice - (originalPrice * discountPercent / 100);

            // Show discounted price
            priceAfterDis.setText("$" + String.format("%.2f", discountedPrice));

            // Show original price with strikethrough
            discountAmount.setText("%" + price);
            discountAmount.setPaintFlags(discountAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            Toast.makeText(getApplicationContext(), "Total: " + total, Toast.LENGTH_SHORT).show();


            // Set click listener for the item

            myView.setOnClickListener(v -> {
                HashMap<String, String> selectedProduct = filteredList.get(i);
                Intent intent = new Intent(getApplicationContext(), ProductsDetails.class);
                intent.putExtra("product", selectedProduct);
                startActivity(intent);

                Toast.makeText(getApplicationContext(), "Clicked on: " + selectedProduct.get("title"), Toast.LENGTH_SHORT).show();

            });


            return myView;
        }
    }
}