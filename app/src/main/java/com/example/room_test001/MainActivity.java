package com.example.room_test001;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.room_test001.database.DataBase;
import com.example.room_test001.database.DataUao;
import com.example.room_test001.database.MyData;

//Stetho工具，需要另外加入implementation in gradle.
import com.facebook.stetho.Stetho;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class MainActivity extends AppCompatActivity {

    MyAdapter myAdapter;
    MyData nowSelectedData;
    RecyclerView recyclerView;
    EditText editName;
    EditText editAge;
    EditText editHobby;
    EditText editEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);    //Facebook開發的調適工具Stetho，設置資料庫監視。

        myAdapter = new MyAdapter(this);


        Button btCreate = findViewById(R.id.create_text);
        Button btClear = findViewById(R.id.clear_text);
        Button btModify = findViewById(R.id.modify_text);
        Button btRefresh = findViewById(R.id.refrash_text);
        editName = findViewById(R.id.editname);
        editAge = findViewById(R.id.editage);
        editHobby = findViewById(R.id.edithobby);
        editEmail = findViewById(R.id.editemail);
        recyclerView = findViewById(R.id.recyclerviewinfo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//設置分隔線

        //建立被觀察者
        Observable observable = Observable.create((ObservableOnSubscribe<String>) e -> {   //ObservableEmitter為信號發送器，此發送String型態資料，也可發送別的
            //向觀察者(Observer)發送信號
            e.onNext("This");
            e.onNext("is");
            e.onNext("a");
            e.onComplete();
            e.onNext("Observable");//已onComplete，不會發送此行信號
        });
        //建立觀察者
        Observer<String> observer = new Observer<String>() {    //傳入String型態資料
            @Override
            public void onSubscribe(Disposable d) { //Disposable可用以解除訂閱(d.dispose())、或查詢是否解除訂閱(d.isDisposed())

            }

            @Override
            public void onNext(String value) {  //改為String型態
                Log.d("observer","onNext:"+value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d("observer","onComplete!");
            }
        };
        //產生訂閱(subscribe)以結合，Observable->Observer
        observable.subscribe(observer);

        //先檢查順序Adapter、有沒有放錯，有沒有拿到。

        setRecyclerFunction(recyclerView);//設置RecyclerView左滑刪除
        //設定更改資料的事件
        btModify.setOnClickListener((view) ->{
            new Thread(() -> {
                if (nowSelectedData ==null) return;
                String name = editName.getText().toString();
                String Age = editAge.getText().toString();
                String Hobby = editHobby.getText().toString();
                String Email = editEmail.getText().toString();
                MyData data = new MyData(nowSelectedData.getId(),name,Age,Hobby,Email);
                DataBase.getInstance(this).getDataUao().updateData(data);
                runOnUiThread(()->{
                    editName.setText("");
                    editAge.setText("");
                    editEmail.setText("");
                    editHobby.setText("");
                    nowSelectedData = null;
                    myAdapter.refreshView();

                    Toast.makeText(this,"已更新資訊!",Toast.LENGTH_LONG).show();
                });
            }).start();
        });

        //清空資料事件按鈕
        btClear.setOnClickListener((view -> {
            editName.setText("");
            editAge.setText("");
            editHobby.setText("");
            editEmail.setText("");
            nowSelectedData = null;
        }));

        //========================================//
        /**新增資料**/
        //問題:無法更新RecyclerView **解決**
        btCreate.setOnClickListener((view -> {
            new Thread(() -> {
               String name = editName.getText().toString();
               String Age = editAge.getText().toString();
               String Hobby = editHobby.getText().toString();
               String Email = editEmail.getText().toString();
               if (name.length() == 0) return;
               MyData data = new MyData(name,Age,Hobby,Email);
               DataBase.getInstance(this).getDataUao().insertData(data);
               refrashed();
               runOnUiThread(()->{
                   myAdapter.refreshView();
                   editName.setText("");
                   editAge.setText("");
                   editHobby.setText("");
                   editEmail.setText("");
               });
//
//                myAdapter.refreshView();
            }).start();
        }));
//**
// "刷新資料表"的按鈕事件。
// 因為在剛開啟時沒有顯示資料表所存在的資料，若要顯示資料表必須先"新增資料"進去。
// 所以我新增一個refresh按鈕，在一開始時先抓到資料表並顯示在recyclerview。
// /
        btRefresh.setOnClickListener(View -> {
            new Thread(()->{
                DataBase.getInstance(this).getDataUao().displayAll();
                refrashed();
                runOnUiThread(()->{
                    myAdapter.refreshView();
                });
            }).start();
        });
    }
    //初始化RecyclerView的表格
    public void refrashed(){
//        new Thread(()->{          //這邊已經在一個Thread中了，因此若在主程式中再宣告進另一個Thread中會有問題。
            List<MyData> data = DataBase.getInstance(this).getDataUao().displayAll();

            runOnUiThread(()->{
                recyclerView.setAdapter(myAdapter);

                myAdapter.setData(data);
                myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(MyData myData) {
                    }
                });
            //**
            // 取得在RecyclerView被選取的資料表欄位並顯示在上方的欄位中。
            // /
                myAdapter.setOnItemClickListener((myData) -> {
                    nowSelectedData = myData;
                    editName.setText(myData.getName());
                    editAge.setText(myData.getAge());
                    editHobby.setText(myData.getHobby());
                    editEmail.setText(myData.getEmail());
                });
            });
//        });
    }
    /**設置RecyclerView的左滑刪除行為*/
    public void setRecyclerFunction(RecyclerView recyclerView){
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {//設置RecyclerView手勢功能
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                //**
                // Returns the Adapter position of the item represented by this ViewHolder with respect to the RecyclerView's RecyclerView.Adapter.
                //
                // If the RecyclerView.Adapter that bound this RecyclerView.ViewHolder is inside another adapter (e.g. ConcatAdapter),
                // this position might be different and will include the offsets caused by other adapters in the ConcatAdapter.
                //
                // /
                int position = viewHolder.getAdapterPosition(); //可能以後會被淘汰??

                switch (direction){
                    case ItemTouchHelper.LEFT:
                    case ItemTouchHelper.RIGHT:
                        myAdapter.deleteData(position);
                        break;
                }
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }

}