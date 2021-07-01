package com.maheshwari.stores.grocerystore.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maheshwari.stores.grocerystore.R;
import com.maheshwari.stores.grocerystore.adapter.OfferAdapter;
import com.maheshwari.stores.grocerystore.helper.OfferImagesClass;
import com.maheshwari.stores.grocerystore.model.Offer;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OffrersFragment extends Fragment {

    OfferImagesClass offerImagesClass;
    private List<Offer> offerList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OfferAdapter mAdapter;

    public OffrersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer, container, false);
        recyclerView = view.findViewById(R.id.offer_rv);
        offerImagesClass = new OfferImagesClass();
        mAdapter = new OfferAdapter(offerImagesClass.getOfferList(), getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Offers");
    }
}
