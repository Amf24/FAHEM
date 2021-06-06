//package com.example.gradproject.IgnoredClasses;
//
//import android.app.Activity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.example.gradproject.R;
//import com.example.gradproject.data.Tutor;
//
//import java.util.List;
//
//public class TutorsList extends ArrayAdapter<Tutor>
//{
//    private Activity context;
//    private List<Tutor> tutorsList;
//
//    public TutorsList(Activity context, List<Tutor> tutorsList)
//    {
//        super(context, R.layout.tutor_list_card);
//        this.context = context;
//        this.tutorsList = tutorsList;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
//    {
//        LayoutInflater inflater = context.getLayoutInflater();
//
//        View listViewItem = inflater.inflate(R.layout.tutor_list_card, parent, true);
//
//        TextView tutorName = listViewItem.findViewById(R.id.textView);
//        TextView tutorRating = listViewItem.findViewById(R.id.textView3);
//
//        ImageView tutorProfilePic = listViewItem.findViewById(R.id.tutor_profile_pic);
//
//        Tutor tutor = tutorsList.get(position);
//        tutorName.setText(tutor.getFullName());
//        tutorRating.setText("Rating: *****");
//
//        return listViewItem;
//
////        View listViewItem = convertView;
////
////        if(listViewItem == null)
////        {
////            listViewItem = LayoutInflater.from(getContext()).inflate(R.layout.tutor_list_card, parent,false);
////        }
////
////        Tutor tutor = getItem(position);
////
////        TextView tutorName = listViewItem.findViewById(R.id.textView);
////        TextView tutorRating = listViewItem.findViewById(R.id.textView3);
////
////        tutorName.setText(tutor.getFullName());
////        tutorRating.setText("Rating: *****");
////
////        return listViewItem;
//
//    }
//}
