package com.example.weekly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weekly.classes.Item;
import com.example.weekly.classes.ItemAdapter;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.nio.charset.IllegalCharsetNameException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Queue;

public class MainActivity extends AppCompatActivity implements ItemAdapter.OnItemClick {
    ArrayList<Item> itemArrayList = new ArrayList<>();
    RecyclerView rvItems;
    ItemAdapter itemAdapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("demo");

    FloatingActionButton fabAdd, fabFilter;

    Calendar now;
    SimpleDateFormat format;
    TextView tvDates;
    TextView tvTotal, tvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvItems = findViewById(R.id.tvItems);
        tvDates = findViewById(R.id.tvDates);
        rvItems = findViewById(R.id.rvItems);
        tvTotal = findViewById(R.id.tvTotal);
        fabAdd = findViewById(R.id.fabAdd);
        fabFilter = findViewById(R.id.fabFilter);

        now = Calendar.getInstance(Locale.TAIWAN);
        format = new SimpleDateFormat("MM-dd-yyyy");

        setOnClickListener();
        getAllItems(getDayOfTheWeek(0), getDayOfTheWeek(6));
    }

    private void setOnClickListener() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

    }

    private void showFilterDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_filter);
        dialog.show();

        setDialogParams(dialog);

        TextView tvEndDate, tvStartDate;

        tvStartDate = dialog.findViewById(R.id.tvStartDate);
        tvEndDate = dialog.findViewById(R.id.tvEndDate);

        TextInputEditText etFrom = dialog.findViewById(R.id.etFrom);
        etFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Select a date");
                // Building the date picker dialog
                MaterialDatePicker<Long> datePicker = builder.build();
                datePicker.addOnPositiveButtonClickListener(selection -> {
                    Long startDate = datePicker.getSelection();

                    tvStartDate.setText(startDate.toString());

                    etFrom.setText(format.format(startDate));

                });

                // Showing the date picker dialog
                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        TextInputEditText etTo = dialog.findViewById(R.id.etTo);
        etTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Select a date");
                // Building the date picker dialog
                MaterialDatePicker<Long> datePicker = builder.build();
                datePicker.addOnPositiveButtonClickListener(selection -> {
                    Long startDate = datePicker.getSelection();

                    tvEndDate.setText(startDate.toString());

                    etTo.setText(format.format(startDate));

                });

                // Showing the date picker dialog
                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });
        ConstraintLayout layout = dialog.findViewById(R.id.layout);
        Button btnApply = dialog.findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((tvEndDate.getText().toString().isEmpty()) || (tvStartDate.getText().toString().isEmpty())) {
                    Toast.makeText(MainActivity.this, "Select Date Range", Toast.LENGTH_SHORT).show();
                    return;
                }

                getAllItems(tvStartDate.getText().toString(), tvEndDate.getText().toString());
                Toast.makeText(MainActivity.this, "Filters have been applied.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        Button btnShowAll = dialog.findViewById(R.id.btnShowAll);
        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllItems();
                Toast.makeText(MainActivity.this, "All records fetched.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        MaterialButton btnReset = dialog.findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllItems(getDayOfTheWeek(0), getDayOfTheWeek(6));
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Filters have been reset.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllItems() {
        Query query = databaseReference.orderByChild("date");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemArrayList.clear();
                double totalWeek = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item item = dataSnapshot.getValue(Item.class);
                    double _price = Double.parseDouble(item.getPrice()) * 100;
                    totalWeek += _price;

                    itemArrayList.add(item);
                }
                Collections.reverse(itemArrayList);
                itemAdapter = new ItemAdapter(MainActivity.this, itemArrayList, MainActivity.this);
                rvItems.setAdapter(itemAdapter);
                rvItems.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                String _week = String.valueOf(totalWeek / 100);

                String firstDay = itemArrayList.get(0).getDate();
                String lastDay = itemArrayList.get(itemArrayList.size()-1).getDate();


                tvDates.setText(millisecToDate(firstDay)+ " to "+millisecToDate(lastDay));
                tvTotal.setText("Total: " + _week);

                tvItems.setText("Total items: " + itemArrayList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAllItems(String startDay, String endDay) {

        Query query = databaseReference.orderByChild("date").startAt(startDay).endAt(endDay);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemArrayList.clear();
                double totalWeek = 0;
                double totalDay = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item item = dataSnapshot.getValue(Item.class);
                    String today = format.format(now.getTime());
                    double _price = Double.parseDouble(item.getPrice()) * 100;
                    totalWeek += _price;

                    if (today.equalsIgnoreCase(item.getDate())) {
                        totalDay += _price;
                    }
                    itemArrayList.add(item);
                }

                Collections.reverse(itemArrayList);
                itemAdapter = new ItemAdapter(MainActivity.this, itemArrayList, MainActivity.this);
                rvItems.setAdapter(itemAdapter);
                rvItems.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                String _week = String.valueOf(totalWeek / 100);
                String _today = String.valueOf(totalDay / 100);

                tvDates.setText(millisecToDate(startDay) + " to " + millisecToDate(endDay));

                tvTotal.setText("Total: " + _week);
//                tvWeeklyTotal.setText("Today: " + _today + "----Week: " + _week);
                tvItems.setText("Total items: " + itemArrayList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String millisecToDate(String millisec) {
        long mil = Long.parseLong(millisec);
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date date = new Date(mil);

        return dateFormat.format(date).toString();
    }

    private String getDayOfTheWeek(int day) {

        Calendar calendar = Calendar.getInstance(Locale.TAIWAN);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // Set to Saturday
        calendar.add(Calendar.DAY_OF_MONTH, day);


        return String.valueOf(calendar.getTimeInMillis());
    }


    @Override
    public void onItemClick(Item item) {
        showMenu(item);
    }

    private void showMenu(Item item) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_item);
        dialog.show();

        setDialogParams(dialog);

        TextInputEditText etName = dialog.findViewById(R.id.etName);
        TextInputEditText etPrice = dialog.findViewById(R.id.etPrice);

        etName.setText(item.getName().toUpperCase());
        etPrice.setText(item.getPrice());

        MaterialButton btnDelete = dialog.findViewById(R.id.btnConfirm);
        btnDelete.setBackgroundColor(getResources().getColor(R.color.red));
        btnDelete.setText("DELETE");
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(item.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Deleted successfully.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setStrokeColor(getResources().getColorStateList(R.color.white));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    private void addNewItem() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_item);
        dialog.show();

        setDialogParams(dialog);

        TextInputEditText etName = dialog.findViewById(R.id.etName);
        etName.requestFocus();

        TextInputEditText etPrice = dialog.findViewById(R.id.etPrice);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        TextView tvDate = dialog.findViewById(R.id.tvDate);
        Date today = now.getTime();
        tvDate.setText(format.format(today));

        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String price = etPrice.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (price.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter price", Toast.LENGTH_SHORT).show();
                    return;
                }

                String key = databaseReference.push().getKey();

                Item item = new Item(key, name, String.valueOf(today.getTime()), price);

                databaseReference.child(key).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Added Successfully.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Task Failed: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    private void setDialogParams(Dialog dialog) {
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}