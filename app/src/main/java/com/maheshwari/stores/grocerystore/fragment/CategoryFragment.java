package com.maheshwari.stores.grocerystore.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.maheshwari.stores.grocerystore.R;
import com.maheshwari.stores.grocerystore.adapter.CategoryAdapter;
import com.maheshwari.stores.grocerystore.api.clients.RestClient;
import com.maheshwari.stores.grocerystore.helper.OfferImagesClass;
import com.maheshwari.stores.grocerystore.model.Category;
import com.maheshwari.stores.grocerystore.model.CategoryResult;
import com.maheshwari.stores.grocerystore.model.Token;
import com.maheshwari.stores.grocerystore.model.User;
import com.maheshwari.stores.grocerystore.util.CustomToast;
import com.maheshwari.stores.grocerystore.util.localstorage.LocalStorage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {
    OfferImagesClass offerImagesClass;
    View progress;
    LocalStorage localStorage;
    Gson gson = new Gson();
    User user;
    Token token;
    private List<Category> categoryList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CategoryAdapter mAdapter;

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = view.findViewById(R.id.category_rv);
        progress = view.findViewById(R.id.progress_bar);

        localStorage = new LocalStorage(getContext());
        user = gson.fromJson(localStorage.getUserLogin(), User.class);
        token = new Token(user.getToken());

        getCategoryData();


        return view;
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
                Toast.makeText(getActivity(),"No internet connection available! Try again later.",Toast.LENGTH_LONG).show();
                hideProgressDialog();
            }
        });

    }

    private void setupCategoryRecycleView() {
        mAdapter = new CategoryAdapter(categoryList, getContext(), "Category");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void hideProgressDialog() {
        progress.setVisibility(View.GONE);
    }

    private void showProgressDialog() {
        progress.setVisibility(View.VISIBLE);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Category");
    }

}
