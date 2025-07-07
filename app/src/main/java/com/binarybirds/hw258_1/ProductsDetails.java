package com.binarybirds.hw258_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.flexbox.FlexboxLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class ProductsDetails extends AppCompatActivity {

    RecyclerView reviewsRecyclerView;
    ArrayList<HashMap<String, String>> arrayList;
    FlexboxLayout tagsContainer;
    Button orderNow;
    TextView shippingInformationTextView, warrantyInformationTextView, availabilityTextView, skuTextView, minimumOrder, returnPolicy, dimensions, widthTextView, heightTextView, depthTextView, metaCreatedDate, metaUpdatedDate, metaBarcode;
    ImageView metaQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_details);

        HashMap<String, String> product = (HashMap<String, String>) getIntent().getSerializableExtra("product");

        ImageSlider imageSlider = findViewById(R.id.image_slider);
        TextView productTitle = findViewById(R.id.productTitle);
        TextView productDescription = findViewById(R.id.productDescription);
        TextView productOfferPrice = findViewById(R.id.productPrice);
        TextView productMainPrice = findViewById(R.id.productDiscount);
        TextView productRating = findViewById(R.id.productRating);
        TextView productStock = findViewById(R.id.productStock);
        TextView productBrand = findViewById(R.id.productBrand);
        TextView productCategory = findViewById(R.id.productCategory);
        TextView weight = findViewById(R.id.weight);
        //TextView tagsTextView = findViewById(R.id.productTags);
        tagsContainer = findViewById(R.id.tagsContainer);

        minimumOrder = findViewById(R.id.minimumOrder);
        returnPolicy = findViewById(R.id.returnPolicy);
        metaCreatedDate = findViewById(R.id.metaCreatedDate);
        metaUpdatedDate = findViewById(R.id.metaUpdatedDate);
        metaBarcode = findViewById(R.id.barCode);
        metaQrCode = findViewById(R.id.qrCode);

        shippingInformationTextView = findViewById(R.id.shippingInformation);
        warrantyInformationTextView = findViewById(R.id.warrantyInformation);
        availabilityTextView = findViewById(R.id.availabilityStatus);
        skuTextView = findViewById(R.id.sku);
        dimensions = findViewById(R.id.dimensions);
        widthTextView = findViewById(R.id.width);
        heightTextView = findViewById(R.id.height);
        depthTextView = findViewById(R.id.depth);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        ConstraintLayout mainContainer = findViewById(R.id.mainContainer);
        orderNow = findViewById(R.id.orderNow);

        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);
        arrayList = new ArrayList<>();
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter();
        reviewsRecyclerView.setAdapter(reviewsAdapter);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if (product != null) {
            try {
                String title = product.get("title");
                String description = product.get("description");
                String price = product.get("price");
                String discount = product.get("discountPercentage");
                String rating = product.get("rating");
                String stock = product.get("stock");
                String brand = product.get("brand");
                String category = product.get("category");
                String[] images = product.get("images").split(",");

                String sku = product.get("sku");
                String shippingInformation = product.get("shippingInformation");
                String warrantyInformation = product.get("warrantyInformation");
                String availabilityStatus = product.get("availabilityStatus");
                String minimumOrderQuantity = product.get("minimumOrderQuantity");
                String returnPolicyValue = product.get("returnPolicy");


                //=========================== Dimensions =============================
                double width = Double.parseDouble(product.get("width"));
                double height = Double.parseDouble(product.get("height"));
                double depth = Double.parseDouble(product.get("depth"));

                //=========================== meta =============================

                String createdAt = product.get("createdAt");
                String updatedAt = product.get("updatedAt");
                String barcode = product.get("barcode");
                String qrCode = product.get("qrCode");


                productTitle.setText(title);
                productDescription.setText(description);

                double mainPriceDouble = Double.parseDouble(price);
                double disDouble = Double.parseDouble(discount);
                String disPercentage = String.format("%.2f", disDouble);
                String discountedPrice = String.format("%.2f", mainPriceDouble - (mainPriceDouble * disDouble / 100));
                productOfferPrice.setText("Offer Price :  $" + discountedPrice + " (After " + disPercentage + "% Off)");
                productMainPrice.setText("Price :  $" + price);
                productMainPrice.setPaintFlags(productMainPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

                productRating.setText("Rating :  " + rating + " / 5");
                productStock.setText("Stock :  " + stock);
                productBrand.setText("Brand : " + brand);
                productCategory.setText("Category :  " + category);
                weight.setText("Weight :  " + product.get("weight"));

                if (width != 0 && height != 0 && depth != 0) {

                    widthTextView.setText("Width: " + width);
                    heightTextView.setText("Height: " + height);
                    depthTextView.setText("Depth: " + depth);

                } else {
                    dimensions.setVisibility(View.GONE);
                }

                shippingInformationTextView.setText("Shipping Information: " + shippingInformation);
                warrantyInformationTextView.setText("Warranty Information: " + warrantyInformation);
                returnPolicy.setText("Return Policy: " + returnPolicyValue);

                metaCreatedDate.setText("Created At: " + formatDate(createdAt));
                metaUpdatedDate.setText("Updated At: " + formatDate(updatedAt));
                metaBarcode.setText(barcode);

                Picasso.get().load(qrCode).placeholder(R.drawable.shaon).into(metaQrCode);

                skuTextView.setText("SKU: " + sku);
                minimumOrder.setText("Minimum Order Quantity: " + minimumOrderQuantity);
                availabilityTextView.setText(availabilityStatus);

                if (availabilityStatus != null && availabilityStatus.contains("In Stock")) {
                    availabilityTextView.setBackgroundResource(R.drawable.bg_available);
                    availabilityTextView.setTextColor(getResources().getColor(android.R.color.white));
                } else {
                    availabilityTextView.setBackgroundResource(R.drawable.bg_unavailable);
                    availabilityTextView.setTextColor(getResources().getColor(android.R.color.white));
                }


                /*
                if (availabilityStatus != null && availabilityStatus.toLowerCase().contains("in stock")) {
                    availabilityTextView.setBackgroundResource(R.drawable.bg_available);
                    availabilityTextView.setTextColor(getResources().getColor(android.R.color.white));
                } else {
                    availabilityTextView.setBackgroundResource(R.drawable.bg_unavailable);
                    availabilityTextView.setTextColor(getResources().getColor(android.R.color.white));
                }

                 */


                // Image Slider
                List<SlideModel> slideModels = new ArrayList<>();
                for (String imageUrl : images) {
                    slideModels.add(new SlideModel(imageUrl.trim(), ScaleTypes.FIT));
                }
                imageSlider.setImageList(slideModels);
                imageSlider.setSlideAnimation(AnimationTypes.TOSS);
                imageSlider.startSliding(2000);
                imageSlider.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void doubleClick(int i) {
                    }

                    @Override
                    public void onItemSelected(int position) {
                        Toast.makeText(ProductsDetails.this, "Clicked image " + (position + 1), Toast.LENGTH_SHORT).show();
                    }
                });

                /*

                // Tags
                String tagsJson = product.get("tags");
                if (tagsJson != null && !tagsJson.isEmpty()) {
                    List<String> tagList = new ArrayList<>();
                    if (tagsJson.trim().startsWith("[")) {
                        JSONArray tagsArray = new JSONArray(tagsJson);
                        for (int i = 0; i < tagsArray.length(); i++) {
                            tagList.add(tagsArray.getString(i));
                        }
                    } else {
                        for (String tag : tagsJson.split(",")) {
                            tagList.add(tag.trim());
                        }
                    }
                    tagsTextView.setText("Tags: " + TextUtils.join(", ", tagList));
                } else {
                    tagsTextView.setText("Tags: None");
                }

                 */

                tagsContainer.removeAllViews(); // Clear existing if reused

                String tagsJson = product.get("tags");
                if (tagsJson != null && !tagsJson.isEmpty()) {
                    List<String> tagList = new ArrayList<>();
                    if (tagsJson.trim().startsWith("[")) {
                        JSONArray tagsArray = new JSONArray(tagsJson);
                        for (int i = 0; i < tagsArray.length(); i++) {
                            tagList.add(tagsArray.getString(i));
                        }
                    } else {
                        for (String tag : tagsJson.split(",")) {
                            tagList.add(tag.trim());
                        }
                    }

                    for (String tag : tagList) {
                        TextView tagView = new TextView(this);
                        tagView.setText(tag);
                        tagView.setTextColor(getResources().getColor(R.color.secondary_text));
                        //tagView.setTextColor(getResources().getColor(android.R.color.black));
                        tagView.setBackgroundResource(R.drawable.tag_background);
                        tagView.setPadding(24, 12, 24, 12);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10, 10, 10, 10);
                        tagView.setLayoutParams(params);

                        tagsContainer.addView(tagView);
                    }
                }

                orderNow.setOnClickListener(v -> {

                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout.removeAllViews();

                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.add_to_cart_product, null);
                    linearLayout.addView(view);

                    orderNow.setEnabled(false);

                    ImageView qtyMinus = view.findViewById(R.id.qtyMinus);
                    ImageView qtyPlus = view.findViewById(R.id.qtyPlus);
                    ImageView productImage = view.findViewById(R.id.productImage);
                    Button confirmOrder = view.findViewById(R.id.confirmOrder);
                    TextView totalPrice = view.findViewById(R.id.totalPrice);
                    TextView totalDiscount = view.findViewById(R.id.totalDiscount);
                    TextView qty = view.findViewById(R.id.qty);

                    int minQty = Integer.parseInt(minimumOrderQuantity);
                    int maxQty = Integer.parseInt(stock);
                    final int[] currentQty = {minQty};

                    qty.setText(String.valueOf(currentQty[0]));

                    Picasso.get().load(images[0]).placeholder(R.drawable.shaon).into(productImage);

                    // Calculate prices initially
                    updatePrice(currentQty[0], price, discount, totalPrice, totalDiscount);

                    // Disable buttons if needed
                    qtyMinus.setEnabled(currentQty[0] > minQty);
                    qtyPlus.setEnabled(currentQty[0] < maxQty);

                    qtyMinus.setOnClickListener(view1 -> {
                        if (currentQty[0] > minQty) {
                            currentQty[0]--;
                            qty.setText(String.valueOf(currentQty[0]));
                            updatePrice(currentQty[0], price, discount, totalPrice, totalDiscount);
                        }

                        qtyMinus.setEnabled(currentQty[0] > minQty);
                        qtyPlus.setEnabled(currentQty[0] < maxQty);
                    });

                    qtyPlus.setOnClickListener(view12 -> {
                        if (currentQty[0] < maxQty) {
                            currentQty[0]++;
                            qty.setText(String.valueOf(currentQty[0]));
                            updatePrice(currentQty[0], price, discount, totalPrice, totalDiscount);
                        }

                        qtyMinus.setEnabled(currentQty[0] > minQty);
                        qtyPlus.setEnabled(currentQty[0] < maxQty);
                    });


                    confirmOrder.setOnClickListener(view13 -> {
                        linearLayout.removeAllViews();
                        orderNow.setEnabled(true);

                        // Get values
                        int finalQty = currentQty[0];
                        double mainPrice = Double.parseDouble(price);
                        double discountPercent = Double.parseDouble(discount);
                        double discountedUnitPrice = mainPrice - (mainPrice * discountPercent / 100);
                        double totalPriceAfterDiscount = finalQty * discountedUnitPrice;
                        double totalDiscountAmount = finalQty * (mainPrice - discountedUnitPrice);

                        // Build message
                        String message = "Order Summary:\n" +
                                "Product: " + title + "\n" +
                                "Quantity: " + finalQty + "\n" +
                                "Total Price (after " + discount + "% discount): $" + String.format("%.2f", totalPriceAfterDiscount) + "\n\n" +
                                "You saved: $" + String.format("%.2f", totalDiscountAmount) + "\n\n" +
                                "Thank you for your purchase!\nMd ShAoN Ali";

                        // Share intent (SMS, Email, WhatsApp, etc.)
                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.setType("text/plain");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, message);

                        // Optional: preset email subject if sent via email
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Order Confirmation");

                        // Start chooser
                        startActivity(Intent.createChooser(sendIntent, "Share order via"));

                        Toast.makeText(ProductsDetails.this, "Order Confirmed", Toast.LENGTH_SHORT).show();
                    });



                });


                // Reviews
                String reviewsJson = product.get("reviews");
                if (reviewsJson != null && !reviewsJson.isEmpty()) {
                    JSONArray reviewsArray = new JSONArray(reviewsJson);
                    for (int i = 0; i < reviewsArray.length(); i++) {
                        JSONObject reviewObject = reviewsArray.getJSONObject(i);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("reviewerName", reviewObject.optString("reviewerName"));
                        hashMap.put("reviewerEmail", reviewObject.optString("reviewerEmail"));
                        hashMap.put("comment", reviewObject.optString("comment"));
                        hashMap.put("rating", reviewObject.optString("rating"));
                        hashMap.put("date", reviewObject.optString("date"));
                        arrayList.add(hashMap);
                    }
                    reviewsAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                Log.e("ProductsDetails", "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String formatDate(String isoDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Make sure it's UTC if needed
            Date date = inputFormat.parse(isoDate);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
            outputFormat.setTimeZone(TimeZone.getDefault()); // Local timezone
            return outputFormat.format(date);
        } catch (Exception e) {
            return isoDate; // Fallback to raw if parsing fails
        }
    }

    private void updatePrice(int quantity, String priceStr, String discountStr, TextView totalPriceView, TextView totalDiscountView) {
        try {
            double mainPrice = Double.parseDouble(priceStr);
            double discountPercent = Double.parseDouble(discountStr);

            double discountedUnitPrice = mainPrice - (mainPrice * discountPercent / 100);
            double totalPrice = quantity * discountedUnitPrice;
            double totalDiscount = quantity * (mainPrice - discountedUnitPrice);

            totalPriceView.setText("Total Price: $" + String.format("%.2f", totalPrice));
            totalDiscountView.setText("Total Discount: $" + String.format("%.2f", totalDiscount));




        } catch (Exception e) {
            Log.e("updatePrice", "Error parsing price or discount: " + e.getMessage());
        }
    }

    private class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.UserReview> {

        @NonNull
        @Override
        public UserReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customers_reviews, parent, false);
            return new UserReview(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserReview holder, int position) {
            HashMap<String, String> hashMap = arrayList.get(position);

            String reviewerName = hashMap.get("reviewerName");
            String reviewerEmail = hashMap.get("reviewerEmail");
            String comment = hashMap.get("comment");
            String rating = hashMap.get("rating");
            String rawDate = hashMap.get("date");
            String formattedDate = formatDate(rawDate);

            holder.tvReviewerName.setText("Name: " + reviewerName);
            holder.tvReviewerEmail.setText("Email: " + reviewerEmail);
            holder.tvComment.setText(comment);
            holder.tvRating.setText("Rating: " + rating + " /5");
            holder.tvDate.setText("Date: " + formattedDate);


        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        class UserReview extends RecyclerView.ViewHolder {
            TextView tvReviewerName, tvReviewerEmail, tvComment, tvRating, tvDate;

            public UserReview(@NonNull View itemView) {
                super(itemView);
                tvReviewerName = itemView.findViewById(R.id.reviewerName);
                tvReviewerEmail = itemView.findViewById(R.id.reviewerEmail);
                tvComment = itemView.findViewById(R.id.comment);
                tvRating = itemView.findViewById(R.id.rating);
                tvDate = itemView.findViewById(R.id.date);
            }
        }
    }


}
