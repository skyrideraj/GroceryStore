package com.maheshwari.stores.grocerystore.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.maheshwari.stores.grocerystore.R;
import com.maheshwari.stores.grocerystore.adapter.SearchAdapter;
import com.maheshwari.stores.grocerystore.api.clients.RestClient;
import com.maheshwari.stores.grocerystore.fragment.CategoryFragment;
import com.maheshwari.stores.grocerystore.fragment.HomeFragment;
import com.maheshwari.stores.grocerystore.fragment.MyOrderFragment;
import com.maheshwari.stores.grocerystore.fragment.NewProductFragment;
import com.maheshwari.stores.grocerystore.fragment.OffrersFragment;
import com.maheshwari.stores.grocerystore.fragment.PopularProductFragment;
import com.maheshwari.stores.grocerystore.fragment.ProfileFragment;
import com.maheshwari.stores.grocerystore.helper.Converter;
import com.maheshwari.stores.grocerystore.model.Product;
import com.maheshwari.stores.grocerystore.model.ProductResult;
import com.maheshwari.stores.grocerystore.model.SearchProduct;
import com.maheshwari.stores.grocerystore.model.User;
import com.maheshwari.stores.grocerystore.util.CustomToast;
import com.maheshwari.stores.grocerystore.util.localstorage.LocalStorage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    User user;
    List<Product> productList = new ArrayList<>();
    SearchAdapter mAdapter;
    private RecyclerView recyclerView;

    @SuppressLint("ResourceAsColor")
    static void centerToolbarTitle(@NonNull final Toolbar toolbar) {
        final CharSequence title = toolbar.getTitle();
        final ArrayList<View> outViews = new ArrayList<>(1);
        toolbar.findViewsWithText(outViews, title, View.FIND_VIEWS_WITH_TEXT);
        if (!outViews.isEmpty()) {
            final TextView titleView = (TextView) outViews.get(0);
            titleView.setGravity(Gravity.CENTER);
            titleView.setTextColor(Color.parseColor("#FAD23C"));
            final Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) titleView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            toolbar.requestLayout();
            //also you can use titleView for changing font: titleView.setTypeface(Typeface);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            setTitle(R.string.app_name);
            getSupportFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }



    /*public void toggleCommunicationGroup(View button) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        MenuItem group = navigationView.getMenu().findItem(R.id.nav_communication_group);
        boolean isVisible = group.isVisible();
        group.setVisible(!isVisible);
        Button toggleButton = (Button)findViewById(R.id.main_toggle_button);
        if (isVisible) {
            toggleButton.setText("Enable communication group");
        } else {
            toggleButton.setText("Disable communication group");
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.cart_action);
        menuItem.setIcon(Converter.convertLayoutToImage(MainActivity.this, cartCount(), R.drawable.ic_shopping_basket));
        final MenuItem searchItem = menu.findItem(R.id.action_search);


        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        searchView.setQueryHint("Search products..");
        EditText searchBox = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchBox.setTextColor(Color.BLACK);
        searchBox.setHintTextColor(Color.GRAY);
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        ImageView searchClose = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.ic_close_black_24dp);

        if (searchView != null) {
            final SearchView finalSearchView = searchView;
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (query.length() < 3) {
                        recyclerView.setVisibility(View.GONE);
                        productList = new ArrayList<>();
                    } else {
                        getSearchProduct(query);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    // UserFeedback.show( "SearchOnQueryTextChanged: " + s);


                    return false;
                }
            });
        }




        return true;
    }

    private void getSearchProduct(String query) {
        Call<ProductResult> call = RestClient.getRestService(getApplicationContext()).searchProduct(new SearchProduct(query, user.getToken()));
        call.enqueue(new Callback<ProductResult>() {
            @Override
            public void onResponse(Call<ProductResult> call, Response<ProductResult> response) {
                Log.d("Response :=>", response.body() + "");
                if (response != null) {

                    ProductResult productResult = response.body();
                    if (productResult.getCode() == 200) {

                        productList = productResult.getProductList();
                        setUpRecyclerView();

                    }

                }


            }

            @Override
            public void onFailure(Call<ProductResult> call, Throwable t) {
                Log.d("Error", t.getMessage());


            }
        });

    }

    private void setUpRecyclerView() {
        if (productList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }

        mAdapter = new SearchAdapter(productList, MainActivity.this, recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart_action:
                Intent cartIntent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(cartIntent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        centerToolbarTitle(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.search_recycler_view);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        localStorage = new LocalStorage(getApplicationContext());
        String userString = localStorage.getUserLogin();
        Gson gson = new Gson();
        userString = localStorage.getUserLogin();
        user = gson.fromJson(userString, User.class);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);

        TextView nav_user = hView.findViewById(R.id.nav_header_name);
        LinearLayout nav_footer = findViewById(R.id.footer_text);
        if (user != null) {
            nav_user.setText(user.getFname());
        }
        nav_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localStorage.logoutUser();
                startActivity(new Intent(getApplicationContext(), LoginRegisterActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                // Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_LONG).show();
            }
        });


        displaySelectedScreen(R.id.nav_home);

        //Handle deep links for product share
        if(getIntent().hasExtra("deeplink")){
            String deepLinkParamString = getIntent().getStringExtra("deeplink");
            try {
                String decodedString = URLDecoder.decode(deepLinkParamString,"UTF-8");
                Uri deepLinkUri = Uri.parse(decodedString);
                Intent intent = new Intent(MainActivity.this, ProductViewActivity.class);
                intent.putExtra("id", deepLinkUri.getQueryParameter("_id"));
                intent.putExtra("title", deepLinkUri.getQueryParameter("_title"));
                intent.putExtra("image", deepLinkUri.getQueryParameter("_image"));
                intent.putExtra("price", deepLinkUri.getQueryParameter("_price"));
                intent.putExtra("currency", deepLinkUri.getQueryParameter("_currency"));
                intent.putExtra("attribute", deepLinkUri.getQueryParameter("_attribute"));
                intent.putExtra("discount", deepLinkUri.getQueryParameter("_discount"));
                intent.putExtra("description", deepLinkUri.getQueryParameter("_description"));

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    private void displaySelectedScreen(int itemId) {
        int flaghome = 0;
        //creating fragment object
        Fragment fragment = null;
        String fragmentTag = "home";

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_home:
                flaghome=1;
                fragmentTag = "home";
                fragment = new HomeFragment();
                break;
            case R.id.nav_profile:
                fragmentTag = "profile";
                fragment = new ProfileFragment();
                break;
            case R.id.nav_category:
                fragmentTag = "category";
                fragment = new CategoryFragment();
                break;
            case R.id.nav_popular_products:
                fragmentTag = "popular_products";
                fragment = new PopularProductFragment();
                break;
            case R.id.nav_new_product:
                fragmentTag = "new_products";
                fragment = new NewProductFragment();
                break;

            case R.id.nav_offers:
                fragmentTag = "offers";
                fragment = new OffrersFragment();
                break;

            case R.id.nav_my_order:
                fragmentTag = "order";
                fragment = new MyOrderFragment();
                break;
            case R.id.nav_my_cart:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;


        }

        //replacing the fragment
        if (fragment != null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left,R.anim.slide_from_right, R.anim.slide_to_left);
            if(manager.findFragmentByTag(fragmentTag)==null){
                ft.add(R.id.content_frame, fragment, fragmentTag);
                if(flaghome==0)
                    ft.addToBackStack("fragback");

            }
            else{
                //ft.show(manager.findFragmentByTag(fragmentTag));
                ft.replace(R.id.content_frame,fragment, fragmentTag);
            }
            ft.commit();

        Log.i(TAG, fragmentTag+" Fragment added!");

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

    @Override
    public void onAddProduct() {
        super.onAddProduct();
        invalidateOptionsMenu();

    }

    @Override
    public void onRemoveProduct() {
        super.onRemoveProduct();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Log.i(TAG, "MainActivity new intent called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }
}
