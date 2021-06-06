package com.example.gradproject.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gradproject.Fragments.HomeFragmentStudent;
import com.example.gradproject.MainActivity;
import com.example.gradproject.TutorDisplayedProfile;
import com.example.gradproject.R;
import com.example.gradproject.data.Tutor;

import java.util.ArrayList;
import java.util.Collection;

public class TutorsRecyclerViewAdapter extends RecyclerView.Adapter implements Filterable
{

    @NonNull
    Context context;
    public ArrayList<Tutor> OriginalMatchedTutorsObjectsList;
    public ArrayList<Tutor> allMatchedTutorsObjectsList;

    public TutorsRecyclerViewAdapter(Context context)
    {
        this.context = context;
        OriginalMatchedTutorsObjectsList = HomeFragmentStudent.matchedTutorsObjectsList;
        allMatchedTutorsObjectsList = new ArrayList<>(HomeFragmentStudent.matchedTutorsObjectsList);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tutor_card, parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        ((RecyclerViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount()
    {
        return HomeFragmentStudent.matchedTutorsObjectsList.size();
    }

    @Override
    public Filter getFilter()
    {
        return filter;
    }

    Filter filter = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) // this run on background thread.
        {
            ArrayList<Tutor> filteredList = new ArrayList<Tutor>();

            if(constraint.toString().isEmpty())
            {
                filteredList.addAll(allMatchedTutorsObjectsList);
            }else
            {
                for(Tutor tutor: allMatchedTutorsObjectsList)
                {
                    if(tutor.getFullName().toLowerCase().contains(constraint.toString().toLowerCase()))
                    {
                        filteredList.add(tutor);
                    }

                    String numOfStars = String.valueOf(tutor.getGeneralTutorRating()).trim().replace(".0","");
                    if(numOfStars.equalsIgnoreCase(constraint.toString()))
                    {
                        filteredList.add(tutor);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) // run on ui thread.
        {
            OriginalMatchedTutorsObjectsList.clear();
            OriginalMatchedTutorsObjectsList.addAll((Collection<? extends Tutor>) results.values);
            notifyDataSetChanged();
        }
    };

    private class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView tutorName;
        private RatingBar tutorRating;
        private ImageView profilePic;
        private Tutor tutor;


        public RecyclerViewHolder(View itemView)
        {
            super(itemView);

            tutorName = itemView.findViewById(R.id.tutor_name);
            tutorRating = itemView.findViewById(R.id.tutor_rating);
            profilePic = itemView.findViewById(R.id.tutor_profile_pic);
            itemView.setOnClickListener(this);
        }

        public void bindView(final int position)
        {
            //tutorName.setText(OurData.names[position] + "   Size: " + HomeFragmentStudent.matchedTutorsObjectsList.size());
            tutorName.setText(HomeFragmentStudent.matchedTutorsObjectsList.get(position).getFullName());
            //tutorRaing.setText(OurData.ratings[position]);
            tutorRating.setRating(HomeFragmentStudent.matchedTutorsObjectsList.get(position).getGeneralTutorRating()); //tutorRaing.setText(HomeFragmentStudent.matchedTutorsObjectsList.get(position).getPhoneNumber());      // Query query = FirebaseDatabase.getInstance().getReference("TutorsRating").child(tutorId).orderByChild("TotalRating");
            //profilePic.setImageResource(OurData.pics[position]);
            profilePic.setImageResource(R.drawable.ic_profile_black_24dp);
            //HomeFragmentStudent.matchedTutorsObjectsList.get(position).getAccountId();
            tutor = HomeFragmentStudent.matchedTutorsObjectsList.get(position);
        }


        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(context, TutorDisplayedProfile.class);

            intent.putExtra("tutorId",tutor.getAccountId());
            intent.putExtra("tutorName",tutor.getFullName());
            intent.putExtra("tutorDescription",tutor.getTutorDescription());

            context.startActivity(intent);
        }
    }
}
