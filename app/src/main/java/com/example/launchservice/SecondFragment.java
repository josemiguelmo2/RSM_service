package com.example.launchservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.launchservice.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    private ReadingSensor mBoundService;
    private boolean mIsBound;
    Intent message;
    private ServiceConnection mConnection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBoundService= ((ReadingSensor.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBoundService = null;
        }

    };

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState

    ) {

        doBindService();
        getActivity().startService(message);
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();



    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().stopService(message);
                mBoundService.sensorRunning=false;
                doUnBindService();
                Log.i("Second Fragment:","Stopping the service");
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }


    void doUnBindService(){
        if(mIsBound){
            getActivity().unbindService(mConnection);
            mIsBound=false;
        }
    }
    void doBindService(){
        message= new Intent(getActivity(),ReadingSensor.class);
        getActivity().bindService(message,mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}