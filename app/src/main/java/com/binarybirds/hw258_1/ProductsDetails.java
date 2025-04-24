package com.binarybirds.hw258_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductsDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_details);

        // Get the passed product data
        HashMap<String, String> product = (HashMap<String, String>) getIntent().getSerializableExtra("product");

        // Initialize ImageSlider
        ImageSlider imageSlider = findViewById(R.id.image_slider);

        // Initialize other views
        TextView productTitle = findViewById(R.id.productTitle);
        TextView productDescription = findViewById(R.id.productDescription);
        TextView productPrice = findViewById(R.id.productPrice);
        TextView productDiscount = findViewById(R.id.productDiscount);
        TextView productRating = findViewById(R.id.productRating);
        TextView productStock = findViewById(R.id.productStock);
        TextView productBrand = findViewById(R.id.productBrand);
        TextView productCategory = findViewById(R.id.productCategory);

        Button addToCart = findViewById(R.id.addToCart);
        Button viewCart = findViewById(R.id.viewCart);

        ConstraintLayout mainContainer = findViewById(R.id.mainContainer);

        // Set up image slider
        List<SlideModel> slideModels = new ArrayList<>();

        // Check if images string exists and is not empty
        if (product.containsKey("images") && !product.get("images").isEmpty()) {
            // Split the images string into individual URLs
            String[] images = product.get("images").split(", ");

            // Add each image to the slider
            for (String imageUrl : images) {
                if (!imageUrl.isEmpty()) {
                    slideModels.add(new SlideModel(imageUrl, ScaleTypes.FIT));
//                    imageSlider.setSlideAnimation(AnimationTypes.TOSS);

                }
            }
        }

        // If no images were added, use the thumbnail as fallback
        if (slideModels.isEmpty() && product.containsKey("thumbnail") && !product.get("thumbnail").isEmpty()) {
            slideModels.add(new SlideModel(product.get("thumbnail"), ScaleTypes.FIT));

        }

        // Set the image list to the slider
        if (!slideModels.isEmpty()) {
            imageSlider.setImageList(slideModels, ScaleTypes.FIT);
            imageSlider.setSlideAnimation(AnimationTypes.TOSS);
        }

        // Set product data to TextViews
        if (product.containsKey("title")) {
            productTitle.setText(product.get("title"));
        }

        if (product.containsKey("description")) {
            productDescription.setText(product.get("description"));
        }

        // Calculate and display prices
        if (product.containsKey("price") && product.containsKey("discountPercentage")) {
            try {
                double price = Double.parseDouble(product.get("price"));
                double discount = Double.parseDouble(product.get("discountPercentage"));
                double discountedPrice = price - (price * discount / 100);

                productPrice.setText(String.format("Price: $%.2f (After %.0f%% discount)", discountedPrice, discount));
                productDiscount.setText(String.format("Original Price: $%.2f", price));
                productDiscount.setPaintFlags(productDiscount.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Set other product information
        if (product.containsKey("rating")) {
            productRating.setText(String.format("Rating: %s/5", product.get("rating")));
        }

        if (product.containsKey("stock")) {
            productStock.setText(String.format("Stock: %s", product.get("stock")));
        }

        if (product.containsKey("brand")) {
            productBrand.setText(String.format("Brand: %s", product.get("brand")));
        }

        if (product.containsKey("category")) {
            productCategory.setText(String.format("Category: %s", product.get("category")));
        }

        //============================================== ==========================================//


        addToCart.setOnClickListener(v -> addToCart());

        viewCart.setOnClickListener(v -> cartView());


    }

    public void cartView() {

        Intent intent = new Intent(getApplicationContext(), CartView.class);
        startActivity(intent);

    }

    public void addToCart() {
        // Inflate the layout
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View myView = layoutInflater.inflate(R.layout.add_to_cart_product, null);

        // Create LayoutParams with constraints
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        // Assign a new unique ID to the view (required for constraints)
        myView.setId(View.generateViewId());

        // Set constraints: 10dp above the Add Cart button

        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.topToBottom = R.id.addToCart;
        //params.topToBottom = R.id.addToCart;
        params.bottomMargin = (int) (120 * getResources().getDisplayMetrics().density); // 10dp to px
        //params.bottomMargin = (int) getResources().getDimension(R.dimen.); // use your 10dp dimension

        // Apply the params
        myView.setLayoutParams(params);

        // Add to parent layout
        ConstraintLayout mainContainer = findViewById(R.id.mainContainer);
        mainContainer.addView(myView);

        // Initialize inner views if needed
        TextView qty = myView.findViewById(R.id.qty);
        qty.setText("1");

        // You can set up plus/minus click listeners here too if you want

        RoundedImageView plus = myView.findViewById(R.id.qtyPlus);
        RoundedImageView minus = myView.findViewById(R.id.qtyMinus);
        Button btnSubmit = myView.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(view -> {
            // Remove the view from the parent layout
            mainContainer.removeView(myView);
            Toast.makeText(this, "Product added to cart", Toast.LENGTH_SHORT).show();
        });

        minus.setOnClickListener(v -> {
            int currentQty = Integer.parseInt(qty.getText().toString());
            if (currentQty > 1) {
                qty.setText(String.valueOf(currentQty - 1));
            }
        });


        plus.setOnClickListener(v -> {
            int currentQty = Integer.parseInt(qty.getText().toString());
            qty.setText(String.valueOf(currentQty + 1));
        });

    }


}