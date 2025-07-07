package com.binarybirds.hw258_1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
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

    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    GridView gridView;
    SearchView searchView;
    ImageView cartView;
    SwipeRefreshLayout swipeRefreshLayout;
    LottieAnimationView animationView, noInternetAnimation;
    ConstraintLayout checkInternetConnection, contentContainer;

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
        checkInternetConnection = findViewById(R.id.checkInternetConnection);
        contentContainer = findViewById(R.id.contentContainer);
        noInternetAnimation = findViewById(R.id.noInternetAnimation);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        animationView.setVisibility(View.VISIBLE);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            vibrate();
            arrayList.clear();
            parseData();
            animateGridView();
            swipeRefreshLayout.setRefreshing(false);
        });

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

        NoInternet();
        parseData();

        cartView.setOnClickListener(v -> cartView());
    }

    public void parseData() {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, PRODUCT_URL, null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("products");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put("id", jsonObject.optString("id"));
                    hashMap.put("title", jsonObject.optString("title"));
                    hashMap.put("description", jsonObject.optString("description"));
                    hashMap.put("category", jsonObject.optString("category"));
                    hashMap.put("price", String.valueOf(jsonObject.optDouble("price")));
                    hashMap.put("discountPercentage", String.valueOf(jsonObject.optDouble("discountPercentage")));
                    hashMap.put("rating", String.valueOf(jsonObject.optDouble("rating")));
                    hashMap.put("stock", String.valueOf(jsonObject.optInt("stock")));
                    hashMap.put("brand", jsonObject.optString("brand", "N/A"));
                    hashMap.put("sku", jsonObject.optString("sku", "N/A"));
                    hashMap.put("shippingInformation", jsonObject.optString("shippingInformation", "N/A"));
                    hashMap.put("warrantyInformation", jsonObject.optString("warrantyInformation", "N/A"));
                    hashMap.put("availabilityStatus", jsonObject.optString("availabilityStatus", "N/A"));
                    hashMap.put("minimumOrderQuantity", jsonObject.optString("minimumOrderQuantity", "N/A"));
                    hashMap.put("returnPolicy", jsonObject.optString("returnPolicy", "N/A"));


                    JSONObject dimensions = jsonObject.optJSONObject("dimensions");
                    if (dimensions != null) {
                        hashMap.put("depth", dimensions.optString("depth"));
                        hashMap.put("width", dimensions.optString("width"));
                        hashMap.put("height", dimensions.optString("height"));
                    }

                    JSONObject meta = jsonObject.optJSONObject("meta");
                    if (meta != null) {
                        hashMap.put("createdAt", meta.optString("createdAt"));
                        hashMap.put("updatedAt", meta.optString("updatedAt"));
                        hashMap.put("barcode", meta.optString("barcode"));
                        hashMap.put("qrCode", meta.optString("qrCode"));
                    }


                    hashMap.put("weight", String.valueOf(jsonObject.optDouble("weight")));
                    hashMap.put("thumbnail", jsonObject.optString("thumbnail", ""));

                    JSONArray tagsArray = jsonObject.optJSONArray("tags");
                    hashMap.put("tags", tagsArray != null ? tagsArray.toString() : "");

                    JSONArray imagesArray = jsonObject.optJSONArray("images");
                    StringBuilder images = new StringBuilder();
                    if (imagesArray != null) {
                        for (int j = 0; j < imagesArray.length(); j++) {
                            images.append(imagesArray.optString(j));
                            if (j != imagesArray.length() - 1) images.append(", ");
                        }
                    }
                    hashMap.put("images", images.toString());

                    JSONArray reviewsArray = jsonObject.optJSONArray("reviews");
                    hashMap.put("reviews", reviewsArray != null ? reviewsArray.toString() : "");

                    arrayList.add(hashMap);
                }

                gridView.setAdapter(new MyAdapter(arrayList));
                animationView.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
                animationView.setVisibility(View.GONE);
            }
        }, error -> {
            if (NoInternet()) {
                Toast.makeText(MainActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            }
            animationView.setVisibility(View.GONE);
        });

        Volley.newRequestQueue(this).add(objectRequest);
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
    }

    private void animateGridView() {
        Animation bounce = new ScaleAnimation(1f, 1.05f, 1f, 1.05f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        bounce.setDuration(300);
        bounce.setRepeatCount(1);
        bounce.setRepeatMode(Animation.REVERSE);
        gridView.startAnimation(bounce);
    }

    public void cartView() {
        startActivity(new Intent(getApplicationContext(), CartView.class));
    }

    public boolean NoInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean noInternet = networkInfo == null || !networkInfo.isConnected();
        checkInternetConnection.setVisibility(noInternet ? View.VISIBLE : View.GONE);
        contentContainer.setVisibility(noInternet ? View.GONE : View.VISIBLE);
        return noInternet;
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
            return filteredList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

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

            Picasso.get().load(hashMap.get("thumbnail")).placeholder(R.drawable.shaon).into(itemLayoutImage);
            itemName.setText(hashMap.get("title"));
            category1.setText(hashMap.get("category"));
            idNo.setText("ID No: " + hashMap.get("id"));

            double originalPrice = Double.parseDouble(hashMap.get("price"));
            double discountPercent = Double.parseDouble(hashMap.get("discountPercentage"));
            double discountedPrice = originalPrice - (originalPrice * discountPercent / 100);

            priceAfterDis.setText("$" + String.format("%.2f", discountedPrice));
            discountAmount.setText("$" + hashMap.get("price") + " (-" + hashMap.get("discountPercentage") + "%)");
            discountAmount.setPaintFlags(discountAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            myView.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), ProductsDetails.class);
                intent.putExtra("product", filteredList.get(i));
                startActivity(intent);
            });

            return myView;
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
    }
}
