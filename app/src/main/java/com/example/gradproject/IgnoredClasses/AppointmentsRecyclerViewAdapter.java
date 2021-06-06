//package com.example.gradproject.IgnoredClasses;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.gradproject.AppointmentDetailsActivity;
//import com.example.gradproject.R;
//
//import java.util.List;
//
//public class AppointmentsRecyclerViewAdapter extends RecyclerView.Adapter<AppointmentsRecyclerViewAdapter.MyViewHolder>
//{
//    List<String> data1;
//    List<String> data2;
//    Context context;
//    int img;
//
//    public AppointmentsRecyclerViewAdapter(Context ct, List<String> s1, List<String> s2, int image1)  //  public AppointmentsRecyclerViewAdapter(Context ct, String s1[], String s2[],int image1)
//    {
//        context = ct;
//        data1 = s1;
//        data2 = s2;
//        this.img = image1;
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
//    {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View view = inflater.inflate(R.layout.appointment_card, parent, false);
//
//        return new MyViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position)
//    {
//
//        holder.tv1.setText(data1.get(position));
//        holder.tv2.setText(data2.get(position));
//        holder.imgv.setImageResource(img);
//
//        holder.rowLayout.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent = new Intent(context, AppointmentDetailsActivity.class);
//                context.startActivity(intent);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return data1.size();
//    }
//
//
//    public class MyViewHolder extends RecyclerView.ViewHolder
//    {
//
//        TextView tv1, tv2;
//        ImageView imgv;
//        ConstraintLayout rowLayout;
//
//        public MyViewHolder(@NonNull View itemView)
//        {
//            super(itemView);
//            tv1 = itemView.findViewById(R.id.appointment_text);
//            tv2 = itemView.findViewById(R.id.appointment_details_text);
//            imgv = itemView.findViewById(R.id.myImage);
//            rowLayout = itemView.findViewById(R.id.recyclerview_row_appointments);
//
//        }
//    }
//}
