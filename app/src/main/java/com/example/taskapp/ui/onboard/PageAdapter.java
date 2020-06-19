package com.example.taskapp.ui.onboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.taskapp.R;
import com.example.taskapp.models.BordModel;

public class PageAdapter extends PagerAdapter {

    public interface onStartClickLListener {
        void onStart();
    }

    private onStartClickLListener onStartClickLListener;


    void setOnStartClickLListener(PageAdapter.onStartClickLListener onStartClickLListener) {
        this.onStartClickLListener = onStartClickLListener;
    }

    private BordModel[] bordModels = new BordModel[]{
            new BordModel(R.drawable.image_onboard_1, "Создовай", "создовай собственные задачи")
            , new BordModel(R.drawable.image_onboard_2, "Совершенствуй", "завершай и изменяй свои задачи")
            , new BordModel(R.drawable.image_onboard_3, "Уничтожай", "удаляй заметки. сбрасывай настройки")};

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.page_onboard, container, false);
        onBindPageOnBoard(view, position);
        setOnClickListeners(view);
        container.addView(view);
        return view;
    }


    private void setOnClickListeners(View view) {
        view.findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartClickLListener.onStart();
            }
        });
    }

    private void onBindPageOnBoard(View view, int position) {
        ((TextView) view.findViewById(R.id.tv_title)).setText(bordModels[position].getTitle());
        ((TextView) view.findViewById(R.id.tv_description)).setText(bordModels[position].getDescription());
        ((ImageView) view.findViewById(R.id.imageView)).setImageResource(bordModels[position].getImage());
        if (position < 2) {
            view.findViewById(R.id.btn_start).setVisibility(View.GONE);
            //view.findViewById(R.id.view).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.btn_start).setVisibility(View.VISIBLE);
            //view.findViewById(R.id.view).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
