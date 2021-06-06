package com.example.gradproject.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gradproject.R;
import com.example.gradproject.RecyclerView.TutorsRecyclerViewAdapter;
import com.example.gradproject.data.Course;
import com.example.gradproject.data.Student;
import com.example.gradproject.data.Tutor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragmentStudent extends Fragment {

    DatabaseReference tutorsDatabase;
    RecyclerView TutorRecyclerView;
    ProgressBar loadingTutorList;
    TextView addCoursesNote;

    public static ArrayList<String> matchedTutorsIdList;
    public static ArrayList<Tutor> matchedTutorsObjectsList;
    FirebaseAuth mAuth;

    public static TutorsRecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_home_student, container, false);

        tutorsDatabase = FirebaseDatabase.getInstance().getReference("Accounts/Tutors");
        tutorsDatabase.keepSynced(true);

        matchedTutorsIdList = new ArrayList<>();
        matchedTutorsObjectsList = new ArrayList<>();

        TutorRecyclerView = rootView.findViewById(R.id.tutor_recycler_view);
        loadingTutorList = rootView.findViewById(R.id.loading_tutor_list);
        addCoursesNote = rootView.findViewById(R.id.please_add_courses);

        loadingTutorList.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Collections.sort(matchedTutorsObjectsList);

                loadingTutorList.setVisibility(View.GONE);
                adapter = new TutorsRecyclerViewAdapter(getContext());
                TutorRecyclerView.setAdapter(adapter);
                TutorRecyclerView.setHasFixedSize(true);
                TutorRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                if (matchedTutorsObjectsList.isEmpty())
                    addCoursesNote.setVisibility(View.VISIBLE);
                else
                    addCoursesNote.setVisibility(View.GONE);
            }
        }, 3000);

        mAuth = FirebaseAuth.getInstance();
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        tutorsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Tutor tutor = snapshot.getValue(Tutor.class);
                        Query tutorCourses = getTutorCourses(tutor.getAccountId());
                        matchTutorWithStudent(tutor, tutorCourses, tutor.getAccountId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //Toast.makeText(getContext(), "size: " + matchedTutorsObjectsList.size(), Toast.LENGTH_LONG).show();

        //Here you can have a list of all tutors ids that match.

//        FirebaseRecyclerAdapter<Tutor, TutorViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tutor, TutorViewHolder>(Tutor.class, R.layout.tutor_card, TutorViewHolder.class, tutorsDatabase)
//        {
//            @Override
//            protected void populateViewHolder(TutorViewHolder tutorViewHolder, final Tutor tutor, int i)
//            {
//                    tutorViewHolder.setName(tutor.getFullName());
//                    tutorViewHolder.setRating(tutor.getPhoneNumber());
//                    tutorViewHolder.setImage(getContext(),"");
//
//                tutorViewHolder.rowLayout.setOnClickListener(new View.OnClickListener()
//                {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        //Toast.makeText(getContext(), "This is " + tutor.getAccountId(), Toast.LENGTH_LONG).show();
//                        if(!matchedTutorsIdList.isEmpty())
//                        {
//                            //textView.setText(Arrays.toString(matchedTutorsIdList.toArray()));
//                            //Toast.makeText(getContext(), "The List is  NOT empty " + matchedTutorsIdList.isEmpty() + "\nThe First User in the list is " + matchedTutorsIdList.get(0), Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(getContext(), TutorDisplayedProfile.class);
//                            startActivity(intent);
//                        }else
//                        {
//                            Toast.makeText(getContext(), "The List is empty", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//            }
//        };
//        TutorRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

//    public static class TutorViewHolder extends RecyclerView.ViewHolder
//    {
//        View mView;
//        ConstraintLayout rowLayout;
//
//        public TutorViewHolder(View itemView)
//        {
//            super(itemView);
//            mView = itemView;
//            rowLayout = itemView.findViewById(R.id.recyclerview_row_tutor);
//        }
//
//        public void setName(String name)
//        {
//            TextView nameTv = mView.findViewById(R.id.tutor_name);
//            nameTv.setText(name);
//        }
//
//        public void setRating(String rating)
//        {
//            TextView ratingTv = mView.findViewById(R.id.tutor_rating);
//            ratingTv.setText(rating);
//        }
//
//        public void setImage(Context ct, String image)
//        {
//            ImageView imageView = mView.findViewById(R.id.tutor_profile_pic);
//            //Picasso.with(ctx).load(image).into(image);
//            imageView.setImageResource(R.drawable.ic_profile_black_24dp);
//        }
//    }


    private void matchTutorWithStudent(final Tutor tutor, final Query tutorCourses, final String tutorId) {
        //This method will populate a list that contain all tutors that match in "matchedTutorsIdList".

        Query currentStudentCourses = FirebaseDatabase.getInstance().getReference("AssignedCourses").child(mAuth.getCurrentUser().getUid()); //What if the user did not add his courses?

        currentStudentCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                matchedTutorsIdList.clear();
                matchedTutorsObjectsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Course scourse = snapshot.getValue(Course.class);
                    tutorCourses.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                Course tcourse = snapshot.getValue(Course.class);
                                if (scourse.getCourseId().equals(tcourse.getCourseId()))
                                {
                                    boolean found = false;
                                    for (String id : matchedTutorsIdList) {
                                        if (id.equals(tutorId))
                                            found = true;
                                    }

                                    if (!found) {
                                        matchedTutorsIdList.add(tutorId);
                                        matchedTutorsObjectsList.add(tutor);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //Toast.makeText(getContext(), "Inside match " + matchedTutorsObjectsList.size(), Toast.LENGTH_LONG).show();
    }

    private Query getTutorCourses(String tutorId)
    {
        Query query = FirebaseDatabase.getInstance().getReference("AssignedCourses").child(tutorId); //What if the user did not add his courses?
        return query;
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
//    {
//        inflater.inflate(R.menu.search_for_tutor_menu, menu);
//        MenuItem item = menu.findItem(R.id.tutor_search);
//        SearchView searchView = (SearchView) item.getActionView();
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
//        super.onCreateOptionsMenu(menu, inflater);
//    }
}
