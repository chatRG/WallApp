package com.wallapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.wallapp.R;

public class OpenLicFragment extends Fragment {

    WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_license, container, false);
        mWebView = (WebView) view.findViewById(R.id.webview);
        mWebView.loadUrl("file:///android_asset/open_licenses.html");
        return view;
    }

    @Override
    public void onDestroy() {
        ((Toolbar) getActivity().findViewById(R.id.toolbar))
                .setTitle(R.string.settings);
        super.onDestroy();
    }
}
