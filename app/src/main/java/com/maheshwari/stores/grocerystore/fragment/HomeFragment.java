package com.maheshwari.stores.grocerystore.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.maheshwari.stores.grocerystore.R;
import com.maheshwari.stores.grocerystore.activity.MainActivity;
import com.maheshwari.stores.grocerystore.adapter.CategoryAdapter;
import com.maheshwari.stores.grocerystore.adapter.HomeSliderAdapter;
import com.maheshwari.stores.grocerystore.adapter.NewProductAdapter;
import com.maheshwari.stores.grocerystore.adapter.PopularProductAdapter;
import com.maheshwari.stores.grocerystore.api.clients.RestClient;
import com.maheshwari.stores.grocerystore.helper.OfferImagesClass;
import com.maheshwari.stores.grocerystore.model.Category;
import com.maheshwari.stores.grocerystore.model.CategoryResult;
import com.maheshwari.stores.grocerystore.model.Offer;
import com.maheshwari.stores.grocerystore.model.Product;
import com.maheshwari.stores.grocerystore.model.ProductResult;
import com.maheshwari.stores.grocerystore.model.Token;
import com.maheshwari.stores.grocerystore.model.User;
import com.maheshwari.stores.grocerystore.util.Utils;
import com.maheshwari.stores.grocerystore.util.localstorage.LocalStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    Timer timer;
    int page_position = 0;
    OfferImagesClass offerImagesClass;
    View progress;
    LocalStorage localStorage;
    Gson gson = new Gson();
    User user;
    Token token;
    private int dotscount;
    ImageView home_offer_iv1,home_offer_iv2;
    private ImageView[] dots;
    private List<Category> categoryList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private List<Product> popularProductList = new ArrayList<>();
    private RecyclerView recyclerView, nRecyclerView, pRecyclerView;
    private CategoryAdapter mAdapter;
    private NewProductAdapter nAdapter;
    private PopularProductAdapter pAdapter;
    private Integer[] images = {R.drawable.maheshwari_inside_1, R.drawable.maheshwari_inside_2, R.drawable.maheshwari_inside_3, R.drawable.maheshwari_collage_all, R.drawable.maheshwari_map};

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.category_rv);
        pRecyclerView = view.findViewById(R.id.popular_product_rv);
        nRecyclerView = view.findViewById(R.id.new_product_rv);
        progress = view.findViewById(R.id.progress_bar);
        home_offer_iv1 = view.findViewById(R.id.home_offer_iv1);
        home_offer_iv2 = view.findViewById(R.id.home_offer_iv2);

        localStorage = new LocalStorage(getContext());
        user = gson.fromJson(localStorage.getUserLogin(), User.class);
        token = new Token(user.getToken());
        if(Utils.isNetworkAvailable(getActivity())){
            getCategoryData();
            getNewProduct();
            getPopularProduct();
        }
        else{
            Toast.makeText(getActivity(),"No internet connection available! Try again later.",Toast.LENGTH_LONG).show();
        }

        List<Offer> offerList = new OfferImagesClass().getOfferList();
        Picasso.get().load(offerList.get(3).getImage()).error(R.drawable.iv_thumbnail).into(home_offer_iv1);
        Picasso.get().load(offerList.get(1).getImage()).error(R.drawable.iv_thumbnail).into(home_offer_iv2);


        timer = new Timer();
        viewPager = view.findViewById(R.id.viewPager);

        sliderDotspanel = view.findViewById(R.id.SliderDots);

        HomeSliderAdapter viewPagerAdapter = new HomeSliderAdapter(getContext(), images);

        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        scheduleSlider();

        return view;
    }

    private void getPopularProduct() {
        showProgressDialog();
        Call<ProductResult> call = RestClient.getRestService(getContext()).popularProducts(token);
        call.enqueue(new Callback<ProductResult>() {
            @Override
            public void onResponse(Call<ProductResult> call, Response<ProductResult> response) {
                Log.d("Response :=>", response.body() + "");
                if (response != null) {

                    ProductResult productResult = response.body();
                    if (productResult.getCode() == 200) {

                        popularProductList = productResult.getProductList();
                        setupPopularProductRecycleView();

                    }

                }

                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<ProductResult> call, Throwable t) {

            }
        });
    }

    private void setupPopularProductRecycleView() {

        pAdapter = new PopularProductAdapter(popularProductList, getContext(), "Home");
        RecyclerView.LayoutManager pLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        pRecyclerView.setLayoutManager(pLayoutManager);
        pRecyclerView.setItemAnimator(new DefaultItemAnimator());
        pRecyclerView.setAdapter(pAdapter);

    }

    private void getNewProduct() {
        showProgressDialog();
        Call<ProductResult> call = RestClient.getRestService(getContext()).newProducts(token);
        call.enqueue(new Callback<ProductResult>() {
            @Override
            public void onResponse(Call<ProductResult> call, Response<ProductResult> response) {
                Log.d("Response :=>", response.body() + "");
                if (response != null) {

                    ProductResult productResult = response.body();
                    if (productResult.getCode() == 200) {

                        productList = productResult.getProductList();
                        setupProductRecycleView();

                    }

                }

                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<ProductResult> call, Throwable t) {
                Log.d("Error", t.getMessage());
                hideProgressDialog();

            }
        });
    }

    private void setupProductRecycleView() {
        nAdapter = new NewProductAdapter(productList, getContext(), "Home");
        RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        nRecyclerView.setLayoutManager(nLayoutManager);
        nRecyclerView.setItemAnimator(new DefaultItemAnimator());
        nRecyclerView.setAdapter(nAdapter);

    }

    private void getCategoryData() {

        showProgressDialog();

        Call<CategoryResult> call = RestClient.getRestService(getContext()).allCategory(token);
        call.enqueue(new Callback<CategoryResult>() {
            @Override
            public void onResponse(Call<CategoryResult> call, Response<CategoryResult> response) {
                Log.d("Response :=>", response.body() + "");
                if (response != null) {

                    CategoryResult categoryResult = response.body();
                    if (categoryResult.getCode() == 200) {

                        categoryList = categoryResult.getCategoryList();
                        setupCategoryRecycleView();

                    }

                }

                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<CategoryResult> call, Throwable t) {
                Log.d("Error==>", t.getMessage());
            }
        });

    }

    private void setupCategoryRecycleView() {
        mAdapter = new CategoryAdapter(categoryList, getContext(), "Home");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


    }


    public void scheduleSlider() {

        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (page_position == dotscount) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }
                viewPager.setCurrentItem(page_position, true);
            }
        };

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 2000, 4000);
    }

    @Override
    public void onStop() {
        timer.cancel();
        super.onStop();
    }

    @Override
    public void onPause() {
        timer.cancel();
        super.onPause();
    }

    public void onLetsClicked(View view) {
        startActivity(new Intent(getContext(), MainActivity.class));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        Log.i(HomeFragment.class.getSimpleName(),"Home Fragment created!");
        getActivity().setTitle(R.string.app_name);
    }

    private void hideProgressDialog() {
        progress.setVisibility(View.GONE);
    }

    private void showProgressDialog() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mAdapter!=null)
            mAdapter.notifyDataSetChanged();
        if(nAdapter!=null)
            nAdapter.notifyDataSetChanged();
        if(pAdapter!=null)
            pAdapter.notifyDataSetChanged();
    }
}
