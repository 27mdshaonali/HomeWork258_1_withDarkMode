package com.binarybirds.hw258_1;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
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

        //============== Get the passed product data ==============//
        HashMap<String, String> product = (HashMap<String, String>) getIntent().getSerializableExtra("product");

        //============== Initialize Views ==============//
        ImageSlider imageSlider = findViewById(R.id.image_slider);


        TextView productTitle = findViewById(R.id.productTitle);
        TextView productDescription = findViewById(R.id.productDescription);
        TextView productOfferPrice = findViewById(R.id.productPrice);
        TextView productMainPrice = findViewById(R.id.productDiscount);
        TextView productRating = findViewById(R.id.productRating);
        TextView productStock = findViewById(R.id.productStock);
        TextView productBrand = findViewById(R.id.productBrand);
        TextView productCategory = findViewById(R.id.productCategory);

        Button addToCart = findViewById(R.id.addToCart);
        Button viewCart = findViewById(R.id.viewCart);
        CardView mainCard = findViewById(R.id.mainCard);

        ConstraintLayout mainContainer = findViewById(R.id.mainContainer);

        //============== Set the product data to the views ==============//
        if (product != null) {
            try {
                String title = product.get("title"); //==============Title====================
                String description = product.get("description"); //==============Description====================
                String price = product.get("price"); //==============Price====================
                String discount = product.get("discountPercentage"); //==============Discount in Percentage====================
                String rating = product.get("rating"); //==============Rating====================
                String stock = product.get("stock"); //==============Stock====================
                String brand = product.get("brand"); //==============Brand====================
                String category = product.get("category"); //==============Category====================
                String[] images = product.get("images").split(","); //==============Images====================

                productTitle.setText(title); //==============Set Title====================
                productDescription.setText(description); //==============Set Description====================

                double mainPriceDouble = Double.parseDouble(price);
                double disDouble = Double.parseDouble(discount);
                String disPercentage = String.format("%.2f", disDouble);
                String discountedPriceDouble = String.format("%.2f", mainPriceDouble - (mainPriceDouble * disDouble / 100));
                productOfferPrice.setText("Offer Price :  $" + discountedPriceDouble + " (After " + disPercentage + "% Off)"); //==============Set Discounted Price====================
                productMainPrice.setText("Price :  $" + price); //==============Set Original Price====================
                productMainPrice.setPaintFlags(productMainPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG); //==============Strike Through Price====================

                productRating.setText("Rating :  " + rating + " / 5"); //==============Set Rating====================

                int stockInt = Integer.parseInt(stock);
                productStock.setText("Stock :  " + stockInt); //==============Set Stock====================

                productBrand.setText("Brand : " + brand); //==============Set Brand====================
                productCategory.setText("Category :  " + category); //==============Set Category====================


                //============== IMAGE SLIDER SETUP ==============//
                List<SlideModel> slideModels = new ArrayList<>();

                if (images.length > 0) {
                    for (String imageUrl : images) {
                        String trimmedUrl = imageUrl.trim();
                        slideModels.add(new SlideModel(trimmedUrl, ScaleTypes.FIT));
                    }

                    imageSlider.setImageList(slideModels);
                    imageSlider.setSlideAnimation(AnimationTypes.TOSS);
                    imageSlider.startSliding(2000);

                    imageSlider.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void doubleClick(int i) {
                            //Toast.makeText(ProductsDetails.this, "Item double Clicked " + (i + 1), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onItemSelected(int position) {
                            Toast.makeText(ProductsDetails.this, "Clicked image " + (position + 1), Toast.LENGTH_SHORT).show();
                        }
                    });


                }

                //============== End of Image Slider Setup ==============//

            } catch (Exception e) {
                Log.e("ProductsDetails", "Error setting product data: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "Error loading product details", Toast.LENGTH_SHORT).show();
            }
        }

        //============== Button Click Listeners ==============//
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove any existing cart view first
                View existingView = mainContainer.findViewById(R.id.mainCard);
                if (existingView != null) {
                    mainContainer.removeView(existingView);
                }

                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View myView = layoutInflater.inflate(R.layout.add_to_cart_product, null);
                myView.setId(R.id.mainCard); // Set an ID so we can find it later

                RoundedImageView productImage = myView.findViewById(R.id.productImage);
                TextView qty = myView.findViewById(R.id.qty);
                TextView qtyMinus = myView.findViewById(R.id.qtyMinus);
                TextView qtyPlus = myView.findViewById(R.id.qtyPlus);
                Button btnSubmit = myView.findViewById(R.id.btnSubmit);

                productImage.setImageResource(R.drawable.shaon);
                qty.setText("1");
                btnSubmit.setText("Submit");

                qtyMinus.setOnClickListener(view -> {
                    int currentQty = Integer.parseInt(qty.getText().toString());
                    if (currentQty > 1) {
                        currentQty--;
                        qty.setText(String.valueOf(currentQty));
                    }
                });

                qtyPlus.setOnClickListener(view -> {
                    int currentQty = Integer.parseInt(qty.getText().toString());
                    currentQty++;
                    qty.setText(String.valueOf(currentQty));
                });

                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                mainContainer.addView(myView, params);
            }
        });


        viewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProductsDetails.this, "Opening Cart", Toast.LENGTH_SHORT).show();
            }
        });
        //============== End of Button Click Listeners ==============//

    }

}
