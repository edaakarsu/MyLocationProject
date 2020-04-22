package com.example.mylocationproject;

import android.content.DialogInterface;
import android.os.Bundle;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class Dialog extends AppCompatDialogFragment {

//    Location l = new Location();

   public  Dialog newInstance(String str) {

        Dialog fragment = new Dialog();

            Bundle bundle = new Bundle();
            bundle.putString("coordinates", str);// set msg here
            fragment.setArguments(bundle);
         return fragment;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        String coordinates = getArguments().getString("coordinates");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if ( coordinates=="warning") {
              warningDialog(builder);
            } else {
                informationDialog(builder,coordinates);
            }
        return builder.create();
    }


    public void informationDialog(AlertDialog.Builder builder, String coordinates) {

        builder.setTitle("Information").setMessage(coordinates).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }


    public void warningDialog(AlertDialog.Builder builder) {

        builder.setTitle("Warning").setMessage("Please get back to original position" ).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                l.closeDialogs();
            }
        });
    }

//    boolean showing;
//    public boolean isShowing() {
//        return showing;
//    }
//
//    public void setShowing(boolean showing) {
//       this.showing = showing;
//    }
}
